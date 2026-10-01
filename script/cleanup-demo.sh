#!/usr/bin/env bash
set -euo pipefail

script_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
project_root="$(cd -- "${script_root}/.." && pwd)"
source "${script_root}/lib/utils.sh"

state_file="${script_root}/.state.tmp"
image_state_file="${script_root}/.state.img"
temp_dir=""
generated_output=""
paths=()
images=()
if [[ -f "$state_file" ]]; then
  mapfile -t paths < "$state_file"
  temp_dir="${paths[0]:-}"
  generated_output="${paths[1]:-}"
  if [[ "$temp_dir" != /*/lauth-api-demo.* || "$temp_dir" == *$'\n'* ]]; then
    fail "Invalid demo temporary directory in $state_file"
  fi
  [[ -n "$generated_output" ]] || fail "Missing generated output in $state_file"
  for path in "${paths[@]}"; do
    if [[ "$path" != /* || "$path" == / || "$path" == *$'\n'* || "$path" != "$(realpath -m -- "$path")" ]]; then
      fail "Invalid cleanup path in $state_file: $path"
    fi
    if [[ "$path" != "$temp_dir" && ( "$temp_dir" == "$path/"* || "$project_root" == "$path" || "$project_root" == "$path/"* || "${HOME:-/}" == "$path" || "${HOME:-/}" == "$path/"* ) ]]; then
      fail "Cleanup path contains another working directory: $path"
    fi
  done
fi
if [[ -f "$image_state_file" ]]; then
  mapfile -t images < "$image_state_file"
fi
for image in "${images[@]}"; do
  [[ "$image" =~ ^localhost/lilamaris/lauth/(identity-service/launcher|client):[A-Za-z0-9_.-]+$ ]] || fail "Invalid demo image in state file: $image"
done

export LAUTH_REGISTRY_HOST=localhost
export LAUTH_BIND_DIR="${temp_dir:-/tmp/lauth-api-demo-missing}"
export LAUTH_GENERATED_OUTPUT="${generated_output:-/tmp/lauth-api-demo-missing/generated}"

compose=(docker compose -p lilamaris-lauth-api-demo -f "${script_root}/docker-compose.yml")
run "shutdown and cleanup" "${compose[@]}" down --volumes --remove-orphans

for image in "${images[@]}"; do
  if docker image inspect "$image" >/dev/null 2>&1; then
    run "remove demo image" docker image rm "$image"
  fi
done

if (( ${#paths[@]} > 0 )); then
  for (( index=${#paths[@]}-1; index>=0; index-- )); do
    run "remove demo path" rm -rf -- "${paths[index]}"
  done
  rm -f -- "$state_file"
fi
rm -f -- "$image_state_file"
