#!/usr/bin/env bash
set -euo pipefail

source "$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)/lib/utils.sh"

usage() {
  cat >&2 <<EOF
Usage:
  ${0##*/} [options] <build target>...

Options:
  -t, --tag <tag>                       Docker image tag                            (optional, default: git commit short hash)
  -n, --namespace <namespace>           Docker image namespace                      (optional, default: lauth)
  --dry-run                             Evaluates a command without any changes     (optional, default: false)

Examples:
  ${0##*/} --tag local identity-service/launcher
  ${0##*/} --dry-run identity-service/launcher

Targets are project-relative module paths (for example identity-service/launcher).
Build JARs are detected from each module's build/libs directory, excluding plain JARs.
Image platform is detected automatically from the host CPU (linux/amd64 or linux/arm64).
Images use the localhost host prefix and are loaded into the local Docker engine.
After building identity-service/launcher, starts the local experience stack with Docker Compose.
Requires Java 21 for Gradle, Docker with buildx, and a Linux Docker engine.
EOF
}

project_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_root"

tag=""
namespace="lauth"
dry_run=false
targets=()

while (( $# > 0 )); do
  case "$1" in
    -t|--tag|-n|--namespace)
      require_option_value "$1" "${2:-}"
      case "$1" in
        -t|--tag) tag="$2" ;;
        -n|--namespace) namespace="$2" ;;
      esac
      shift 2
      ;;
    --dry-run) dry_run=true; shift ;;
    -h|--help) usage; exit 0 ;;
    --) shift; targets+=("$@"); break ;;
    -*) fail "Unknown option: $1" ;;
    *) targets+=("$1"); shift ;;
  esac
done

(( ${#targets[@]} > 0 )) || { usage; fail "At least one build target is required."; }

host_os="$(uname -s)"
host_arch="$(uname -m)"
case "$host_os" in
  Linux|Darwin) ;;
  *) fail "Unsupported host OS: $host_os. Use Linux, macOS, or WSL2 Bash." ;;
esac
case "$host_arch" in
  x86_64|amd64) platform=linux/amd64 ;;
  aarch64|arm64) platform=linux/arm64 ;;
  *) fail "Unsupported host CPU: $host_arch. Supported CPUs: amd64 and arm64." ;;
esac
log_info "Host: $host_os/$host_arch; image platform: $platform"

if [[ -z "$tag" ]]; then
  require_cmd git
  tag="$(git rev-parse --short HEAD)" || fail "Cannot determine image tag; specify --tag."
fi
[[ "$tag" =~ ^[a-zA-Z0-9_][a-zA-Z0-9_.-]{0,127}$ ]] || fail "Invalid image tag: $tag"
[[ "$namespace" =~ ^[a-z0-9]+([._-][a-z0-9]+)*(/[a-z0-9]+([._-][a-z0-9]+)*)*$ ]] || fail "Invalid image namespace: $namespace"

[[ -f gradlew ]] || fail "Gradle wrapper not found."
# Validate every target before starting any build.
for target in "${targets[@]}"; do
  [[ "$target" =~ ^[a-zA-Z0-9_-]+(/[a-zA-Z0-9_-]+)*$ ]] || fail "Invalid module path: $target"
  [[ -f "$target/build.gradle.kts" || -f "$target/build.gradle" ]] || fail "Gradle module not found: $target"
  [[ -f "$target/Dockerfile" ]] || fail "Dockerfile not found: $target/Dockerfile"
done

[[ " ${targets[*]} " == *" identity-service/launcher "* ]] || fail "The experience stack requires identity-service/launcher as a build target."

if [[ "$dry_run" == false ]]; then
  if [[ -n "${JAVA_HOME:-}" ]]; then
    [[ -x "$JAVA_HOME/bin/java" ]] || fail "JAVA_HOME does not contain an executable bin/java."
  else
    require_cmd java
  fi
  require_cmd docker
  docker buildx version >/dev/null 2>&1 || fail "Docker buildx is unavailable."
  docker_os="$(docker info --format '{{.OSType}}')" || fail "Cannot connect to the Docker engine."
  [[ "$docker_os" == linux ]] || fail "A Linux Docker engine is required (found: $docker_os)."
  docker buildx inspect --bootstrap >/dev/null || fail "The selected buildx builder is unavailable."
else
  log_warn "Dry run: commands only; Java, Docker, and build artifacts are not checked."
fi

gradle_tasks=()
for target in "${targets[@]}"; do
  gradle_tasks+=(":${target//\//:}:build")
done

gradle_command=(bash ./gradlew "${gradle_tasks[@]}")
if [[ "$dry_run" == true ]]; then
  log_command "Planned:" "${gradle_command[@]}"
else
  run "${gradle_command[@]}"
fi

for target in "${targets[@]}"; do
  if [[ "$dry_run" == true ]]; then
    artifact="build/libs/<detected-boot-jar>.jar"
    log_info "Will select exactly one non-plain JAR from $target/build/libs after Gradle build."
  else
    jars=()
    for candidate in "$target"/build/libs/*.jar; do
      [[ -f "$candidate" && "$candidate" != *-plain.jar ]] || continue
      jars+=("$candidate")
    done
    (( ${#jars[@]} == 1 )) || fail "Expected exactly one non-plain JAR in $target/build/libs; found ${#jars[@]}."
    artifact="${jars[0]#"$target/"}"
  fi
  if [[ "$dry_run" == false ]]; then
    [[ -s "$target/$artifact" ]] || fail "Build JAR is missing or empty: $target/$artifact"
  fi

  # Include the module path to avoid collisions between launcher modules.
  image="localhost/$namespace/${target//\//-}:$tag"
  build_command=(docker buildx build \
    --platform "$platform" \
    --load \
    --file "$target/Dockerfile" \
    --build-arg "JAR_FILE=$artifact" \
    --tag "$image" \
    "$target")
  if [[ "$dry_run" == true ]]; then
    log_command "Planned:" "${build_command[@]}"
    log_info "Planned image: $image"
  else
    run "${build_command[@]}"
    log_info "Image build completed: $image"
  fi
done

if [[ "$dry_run" == true ]]; then
  log_command "Planned:" docker compose \
    -p lauth-dry-run \
    -f script/docker-compose.yaml \
    up --remove-orphans
  exit 0
fi

temp_dir="$(mktemp -d "${TMPDIR:-/tmp}/lauth-start.XXXXXXXX")"
compose_project="lauth-$(printf '%s' "${temp_dir##*.}" | tr '[:upper:]' '[:lower:]')"
compose_files=(-f script/docker-compose.yaml)
compose_started=false

cleanup() {
  local exit_code=$?
  trap - EXIT INT TERM

  if [[ "$compose_started" == false ]] || docker compose -p "$compose_project" "${compose_files[@]}" down --volumes --remove-orphans; then
    rm -rf -- "$temp_dir"
  else
    log_error "Compose shutdown failed; keeping temporary files at $temp_dir"
    exit_code=1
  fi

  exit "$exit_code"
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

mkdir -p "$temp_dir/data" "$temp_dir/secrets"
cp script/data/adjectives script/data/nouns "$temp_dir/data/"
bash script/key-gen.sh --kid local "$temp_dir/secrets"

export LAUTH_IMAGE_NAMESPACE="$namespace"
export LAUTH_IMAGE_TAG="$tag"
export LAUTH_KEYS_DIR="$temp_dir/secrets"
export LAUTH_DATA_DIR="$temp_dir/data"
export LAUTH_HASHER_KEY="${LAUTH_HASHER_KEY:-$(openssl rand -hex 32)}"

log_info "Starting the experience stack. Press Ctrl+C to stop and clean up."
compose_started=true
run docker compose -p "$compose_project" "${compose_files[@]}" up --remove-orphans
