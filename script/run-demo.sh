#!/usr/bin/env bash
set -euo pipefail

# Initial script variables
SCRIPT_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd -- "${SCRIPT_ROOT}/.." && pwd)"
cd -- "$PROJECT_ROOT"

source "${SCRIPT_ROOT}/lib/utils.sh"

TMP_FILE_STATE="${SCRIPT_ROOT}/.state.tmp"
DOCKER_IMAGE_STATE="${SCRIPT_ROOT}/.state.img"

REGISTRY="localhost"
NAMESPACE="lilamaris/lauth"
TAG="local"
PLATFORM="$(get_platform "$(uname -m)")"
GENERATED_OUTPUT=""

usage() {
  printf 'Usage: bash script/run-demo.sh [-o|--generated-output DIRECTORY]\n'
}

while (( $# > 0 )) ; do
  case "$1" in
      -\?|--help|-h)
        usage
        exit 0
        ;;
      -o | --generated-output)
        require_option_value "$1" "${2:-}"
        GENERATED_OUTPUT="$2"
        shift 2
        ;;
      --generated-output=*)
        GENERATED_OUTPUT="${1#*=}"
        require_option_value "--generated-output" "$GENERATED_OUTPUT"
        shift
        ;;
      *)
        usage
        exit 2
        ;;
  esac
done

if [[ -e "$TMP_FILE_STATE" || -e "$DOCKER_IMAGE_STATE" ]]; then
  log_info "Existing demo state found. Stopping the previous demo first."
  bash "${SCRIPT_ROOT}/cleanup-demo.sh"
fi

temp_dir="$(realpath -m -- "$(mktemp -d "${TMPDIR:-/tmp}/lauth-api-demo.XXXXXXXX")")"
GENERATED_OUTPUT="${GENERATED_OUTPUT:-${temp_dir}/generated}"
GENERATED_OUTPUT="$(realpath -m -- "$GENERATED_OUTPUT")"
if [[ "$GENERATED_OUTPUT" == / || "$PROJECT_ROOT" == "$GENERATED_OUTPUT" || "$PROJECT_ROOT" == "$GENERATED_OUTPUT/"* || "$temp_dir" == "$GENERATED_OUTPUT/"* || "${HOME:-/}" == "$GENERATED_OUTPUT" || "${HOME:-/}" == "$GENERATED_OUTPUT/"* ]]; then
  fail "Generated output must be a dedicated directory: $GENERATED_OUTPUT"
fi
if [[ -e "$GENERATED_OUTPUT" || -L "$GENERATED_OUTPUT" ]]; then
  fail "Generated output already exists: $GENERATED_OUTPUT"
fi
printf '%s\n' "$temp_dir" >> "$TMP_FILE_STATE"
printf '%s\n' "$GENERATED_OUTPUT" >> "$TMP_FILE_STATE"

modules=(
  identity-service/launcher
)

log_info "Host platform: $PLATFORM"

# Prepare build & Validate gradle module
[[ -f gradlew ]] || {
  log_error "Gradle wrapper not found."
  exit 1
}

validate_gradle_module "${modules[@]}" || {
  log_error "Failed to validate modules."
  exit 1
}

read -r -a tasks <<< "$(convert_to_gradle_path "${modules[@]}")"

gradle_command=(./gradlew "${tasks[@]}")

# Build gradle module
run "build gradle module" "${gradle_command[@]}"

# Prepare build docker image
run_no_output "check docker daemon" docker info || {
  log_error "docker daemon is unavailable or permission was denied."
  exit 1
}

run_no_output "check docker buildx" docker buildx version || {
  log_error "docker buildx is unavailable"
  exit 1
}

run_no_output "check docker compose" docker compose version || {
  log_error "docker compose plugin is not available."
  exit 1
}

command -v git 1>/dev/null 2>&1 && TAG=$(git rev-parse --short HEAD)
log_info "Image TAG: $TAG"

# Build docker image
for module in "${modules[@]}"; do
  image="${REGISTRY}/${NAMESPACE}/${module}:${TAG}"

  build_command=(
    docker buildx build
    --platform "$PLATFORM"
    --load
    --file "${module}/Dockerfile"
    --build-arg JAR_FILE=build/libs/app.jar
    --tag "$image"
    "$module"
  )

  run "build docker image" "${build_command[@]}"
  printf '%s\n' "$image" >> "$DOCKER_IMAGE_STATE"
done

mkdir -p "$temp_dir/client"
run_no_output "clone client repository" git clone https://github.com/lilamaris/lauth-client.git "$temp_dir/client"
printf '%s\n' "${temp_dir}/client" >> "$TMP_FILE_STATE"

client_tag="$(git -C "$temp_dir/client" rev-parse --short HEAD)"
client_image="${REGISTRY}/${NAMESPACE}/client:${client_tag}"
log_info "Client image TAG: $client_tag"

run "build client docker image" docker buildx build \
  --platform "$PLATFORM" \
  --load \
  --file "$temp_dir/client/docker/Dockerfile" \
  --tag "$client_image" \
  "$temp_dir/client"
printf '%s\n' "$client_image" >> "$DOCKER_IMAGE_STATE"

compose_project="lilamaris-lauth-api-demo"
compose=(docker compose -p "$compose_project" -f "${SCRIPT_ROOT}/docker-compose.yml" )

mkdir -p "$temp_dir/data" "$temp_dir/secrets" "$GENERATED_OUTPUT"
cp script/data/adjectives script/data/nouns "$temp_dir/data/"
printf '%s\n' "${temp_dir}/data" >> "$TMP_FILE_STATE"

bash script/key-gen.sh --kid local "$temp_dir/secrets"
printf '%s\n' "${temp_dir}/secrets" >> "$TMP_FILE_STATE"

export LAUTH_REGISTRY_HOST="$REGISTRY"
export LAUTH_IMAGE_NAMESPACE="$NAMESPACE"
export LAUTH_IMAGE_TAG="$TAG"
export LAUTH_CLIENT_IMAGE_TAG="$client_tag"
export LAUTH_BIND_DIR="$temp_dir"
export LAUTH_GENERATED_OUTPUT="$GENERATED_OUTPUT"
export LAUTH_HASHER_KEY="${LAUTH_HASHER_KEY:-$(openssl rand -hex 32)}"
export LAUTH_TEST_ENABLED=true

log_info "Starting the demo stack. Run bash script/cleanup-demo.sh to stop and clean up."

run "start docker compose" "${compose[@]}" up --remove-orphans --wait
log_info "Demo is now running."
log_warn "Script exit does not remove generated files, containers, or images. Run bash script/cleanup-demo.sh to remove them."
log_info "Created containers:"
docker ps -a --filter "label=com.docker.compose.project=${compose_project}" --format '  {{.Names}}'
log_info "Created images:"
for module in "${modules[@]}"; do
  printf '  %s/%s/%s:%s\n' "$REGISTRY" "$NAMESPACE" "$module" "$TAG"
done
printf '  %s\n' "$client_image"
log_info "Created files and directories:"
while IFS= read -r path; do
  printf '  %s\n' "$path"
done < "$TMP_FILE_STATE"
