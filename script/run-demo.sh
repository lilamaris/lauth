#!/usr/bin/env bash
set -euo pipefail

# Initial script variables
script_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
project_root="$(cd -- "${script_root}/.." && pwd)"
cd -- "$project_root"

source "${script_root}/lib/utils.sh"

registry_host="localhost"
namespace="lilamaris/lauth"
tag="local"
host_os="$(uname -s)"
host_arch="$(uname -m)"
host_platform="$(get_platform "$host_arch")"

modules=(
  identity-service/launcher
)

log_info "Host OS/Arch: $host_os/$host_arch, Platform: $host_platform"

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

command -v git 1>/dev/null 2>&1 && tag=$(git rev-parse --short HEAD)
log_info "Image tag: $tag"

# Build docker image
for module in "${modules[@]}"; do
  image="${registry_host}/${namespace}/${module}:${tag}"

  build_command=(
    docker buildx build
    --platform "$host_platform"
    --load
    --file "${module}/Dockerfile"
    --build-arg JAR_FILE=build/libs/app.jar
    --tag "$image"
    "$module"
  )

  run "build docker image" "${build_command[@]}"
done

state_file="${script_root}/.demo-temp-dir"
if [[ -e "$state_file" ]]; then
  log_info "Existing demo state found. Stopping the previous demo first."
  bash "${script_root}/cleanup-demo.sh"
fi
temp_dir="$(mktemp -d "${TMPDIR:-/tmp}/lauth-api-demo.XXXXXXXX")"
compose_project="lilamaris-lauth-api-demo"
compose=(docker compose -p "$compose_project" -f "${script_root}/docker-compose.yml" )
printf '%s\n' "$temp_dir" > "$state_file"

mkdir -p "$temp_dir/data" "$temp_dir/secrets"
cp script/data/adjectives script/data/nouns "$temp_dir/data/"
bash script/key-gen.sh --kid local "$temp_dir/secrets"

export LAUTH_REGISTRY_HOST="$registry_host"
export LAUTH_IMAGE_NAMESPACE="$namespace"
export LAUTH_IMAGE_TAG="$tag"
export LAUTH_KEYS_DIR="$temp_dir/secrets"
export LAUTH_DATA_DIR="$temp_dir/data"
export LAUTH_HASHER_KEY="${LAUTH_HASHER_KEY:-$(openssl rand -hex 32)}"

log_info "Starting the demo stack. Run bash script/cleanup-demo.sh to stop and clean up."

run "start docker compose" "${compose[@]}" up --remove-orphans --wait
log_info "Demo is now running."
log_warn "Script exit does not remove generated files or containers. Run bash script/cleanup-demo.sh to remove the temporary files and containers."
log_info "Created containers:"
docker ps -a --filter "label=com.docker.compose.project=${compose_project}" --format '  {{.Names}}'
log_info "Created temporary files:"
printf '  %s\n' "$state_file"
find "$temp_dir" -type f -print | sort | while IFS= read -r file; do
  printf '  %s\n' "$file"
done
