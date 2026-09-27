#!/usr/bin/env bash
set -euo pipefail

script_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
project_root="$(cd -- "${script_root}/.." && pwd)"
source "${script_root}/lib/utils.sh"

state_file="${script_root}/.demo-temp-dir"
image_state_file="${script_root}/.demo-docker-image"
temp_dir=""
images=()
if [[ -f "$state_file" ]]; then
  mapfile -t state < "$state_file"
  temp_dir="${state[0]:-}"
  if [[ "$temp_dir" != /*/lauth-api-demo.* || "$temp_dir" == *$'\n'* ]]; then
    fail "Invalid demo temporary directory in $state_file"
  fi
fi
if [[ -f "$image_state_file" ]]; then
  mapfile -t images < "$image_state_file"
elif [[ -f "$state_file" ]]; then
  # Support state files written before image names had their own file.
  images=("${state[@]:1}")
  if (( ${#images[@]} == 0 )); then
    mapfile -t images < <(docker ps -a \
      --filter label=com.docker.compose.project=lilamaris-lauth-api-demo \
      --filter label=com.docker.compose.service=lauth \
      --format '{{.Image}}')
  fi
fi
for image in "${images[@]}"; do
  [[ "$image" =~ ^localhost/lilamaris/lauth/(identity-service/launcher|client):[A-Za-z0-9_.-]+$ ]] || fail "Invalid demo image in state file: $image"
done

export LAUTH_REGISTRY_HOST=localhost
export LAUTH_KEYS_DIR="${temp_dir:-/tmp/lauth-api-demo-missing}/secrets"
export LAUTH_DATA_DIR="${temp_dir:-/tmp/lauth-api-demo-missing}/data"

compose=(docker compose -p lilamaris-lauth-api-demo -f "${script_root}/docker-compose.yml")
run "shutdown and cleanup" "${compose[@]}" down --volumes --remove-orphans

for image in "${images[@]}"; do
  if docker image inspect "$image" >/dev/null 2>&1; then
    run "remove demo image" docker image rm "$image"
  fi
done

if [[ -n "$temp_dir" ]]; then
  run "remove temporary data" rm -rf -- "$temp_dir"
  rm -f -- "$state_file"
fi
rm -f -- "$image_state_file"
