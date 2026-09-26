#!/usr/bin/env bash
set -euo pipefail

script_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
project_root="$(cd -- "${script_root}/.." && pwd)"
source "${script_root}/lib/utils.sh"

state_file="${script_root}/.demo-temp-dir"
temp_dir=""
if [[ -f "$state_file" ]]; then
  IFS= read -r temp_dir < "$state_file"
  if [[ "$temp_dir" != /*/lauth-api-demo.* || "$temp_dir" == *$'\n'* ]]; then
    fail "Invalid demo temporary directory in $state_file"
  fi
fi

export LAUTH_REGISTRY_HOST=localhost
export LAUTH_KEYS_DIR="${temp_dir:-/tmp/lauth-api-demo-missing}/secrets"
export LAUTH_DATA_DIR="${temp_dir:-/tmp/lauth-api-demo-missing}/data"

compose=(docker compose -p lilamaris-lauth-api-demo -f "${script_root}/docker-compose.yml")
run "shutdown and cleanup" "${compose[@]}" down --volumes --remove-orphans

if [[ -n "$temp_dir" ]]; then
  run "remove temporary data" rm -rf -- "$temp_dir"
  rm -f -- "$state_file"
fi
