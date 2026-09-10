#!/usr/bin/env bash
set -euo pipefail

RED=$'\033[0;31m'
GREEN=$'\033[0;32m'
YELLOW=$'\033[0;33m'
NC=$'\033[0m'

log_info() {
  printf '%s[INFO]%s %s\n' "$GREEN" "$NC" "$1"
}

log_error() {
  printf '%s[ERROR]%s %s\n' "$RED" "$NC" "$1"
}

log_warn() {
  printf '%s[WARN]%s %s\n' "$YELLOW" "$NC" "$1"
}

log_command() {
  local message="$1"
  shift

  printf '%s[INFO]%s %s' "$GREEN" "$NC" "$message"
  printf ' %q' "$@"
  printf '\n'
}

usage() {
  cat >&2 <<EOF
Usage:
  ${0##*/} [options] <output-dir>

Options:
  --kid <kid name>        KeyID name              (optional, default: key-(%Y%m%d-%H%M%S) format)
  --prvk-name <name>      Private key name        (optional, default: private)
  --pubk-name <name>      Public key name         (optional, default: public)
  --rsa                   Generate RSA key        (optional, default gen type)
  --ed25519               Generate ED25519 key    (optional)

Examples:
  ${0##./} \\
    --prvk-name my-private \\
    --pubk-name my-public \\
    /run/lauth/secrets
EOF
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    log_error "Missing required command: $1"
    exit 1
  }
}

require_option_value() {
  local option="$1"
  local value="${2:-}"

  [[ -n "$value" ]] || {
    log_error "Option requires a value: ${option}"
    usage
    exit 2
  }
}

require_cmd openssl

kid="key-$(date +%Y%m%d-%H%M%S)"
alg_flag="rsa"
prvk_name="private"
pubk_name="public"
output_dir=""

while (( $# > 0 )); do
  case "$1" in
    --kid)
      require_option_value "$1" "${2:-}"
      kid="$2"
      shift 2
      ;;
    --rsa)
      alg_flag="rsa"
      shift 1
      ;;
    --ed25519)
      alg_flag="ed25519"
      shift 1
      ;;
    --prvk-name)
      require_option_value "$1" "${2:-}"
      prvk_name="$2"
      shift 2
      ;;
    --pubk-name)
      require_option_value "$1" "${2:-}"
      pubk_name="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    --)
      shift
      require_option_value "<output-dir>" "${1:-}"
      output_dir="$1"
      shift
      break
      ;;
    -*)
      log_error "Unknown option: $1"
      usage
      exit 2
      ;;
    *)
      if [[ -n "$output_dir" ]]; then
        log_error "Unexpected argument: $1"
        usage
        exit 2
      fi

      output_dir="${1%%/}"
      shift
      ;;
  esac
done

[[ -n "$output_dir" ]] || {
  log_error "Missing required argument: <output-dir>"
  usage
  exit 2
}

[[ -d "$output_dir" ]] || {
  log_error "Output dir is not a directory: ${output_dir}"
  exit 1
}

key_set_dir="${output_dir}/${kid}"

[[ ! -e "$key_set_dir" ]] || {
  log_error "Key set path already exists: ${key_set_dir}"
  exit 1
}

mkdir -p "${key_set_dir}" || {
  log_error "Failed to create key set dir: ${key_set_dir}"
  exit 1
}

prvk_path="${key_set_dir}/${prvk_name}.pem"
pubk_path="${key_set_dir}/${pubk_name}.pem"

genpkey_cmd=(
  openssl genpkey
  -out "${prvk_path}"
)

alg=""

[[ "$alg_flag" == "rsa" ]] && {
  alg="RS256"
  genpkey_cmd+=(-algorithm RSA -pkeyopt rsa_keygen_bits:2048)
}

[[ "$alg_flag" == "ed25519" ]] && {
  alg="Ed25519"
  genpkey_cmd+=(-algorithm ED25519)
}

log_command "Executing ${genpkey_cmd[@]}"

"${genpkey_cmd[@]}"

openssl pkey \
  -pubout \
  -in "${prvk_path}" \
  -out "${pubk_path}"

cat > "${key_set_dir}/metadata.env" <<EOF
KID=${kid}
ALG=${alg}
CREATED_AT=$(date -Iseconds)
EOF

log_info "Generated key set:"
printf "  Key ID: %s\n" "$kid"
printf "  Public key path: %s\n" "$pubk_path"
printf "  Private key path: %s\n" "$prvk_path"
