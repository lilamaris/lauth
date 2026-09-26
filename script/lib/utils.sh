#!/usr/bin/env bash

RED=$'\033[0;31m'
GREEN=$'\033[0;32m'
YELLOW=$'\033[0;33m'
NC=$'\033[0m'

## Common logging utility

info_badge() {
  printf '%s[INFO]%s ' "$GREEN" "$NC"
}

error_badge() {
  printf '%s[ERROR]%s ' "$RED" "$NC"
}

warn_badge() {
  printf '%s[WARN]%s ' "$YELLOW" "$NC"
}

log_info() {
  info_badge
  printf '%s\n' "$1"
}

log_error() {
  error_badge
  printf '%s\n' "$1"
}

log_warn() {
  warn_badge
  printf '%s\n' "$1"
}

log_command() {
  local message="$1"
  shift

  printf '%s[INFO]%s %b' "$GREEN" "$NC" "$message"
  printf ' %q' "$@"
  printf '\n'
}

fail() {
  log_error "$1"
  exit 1
}

type_and_reason() {
  printf "[%-8s] - %-25s" "$1" "$2"
}

run_no_output() {
  info_badge
  type_and_reason "RUN" "$1"
  shift
  printf " > "
  printf " %q" "$@"
  printf "\n"

  "$@" >/dev/null 2>&1
}

run() {
  info_badge
  type_and_reason "RUN" "$1"
  shift
  printf " > "
  printf " %q" "$@"
  printf "\n"

  "$@"
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

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    log_error "Missing required command: $1"
    exit 1
  }
}

# Specification work

get_platform() {
  local arch=$(echo "$1" | tr '[:upper:]' '[:lower:]')
  case "$arch" in
    x86_64|amd64|x64) echo "linux/amd64" ;;
    aarch64|arm64) echo "linux/arm64" ;;
    *)
      log_error "Unsupported host arch $1."
      exit 1
      ;;
  esac
}

validate_gradle_module() {
  for target in "$@"; do
    [[ "$target" =~ ^[a-zA-Z0-9_-]+(/[a-zA-Z0-9_-]+)*$ ]] || {
      log_error "Invalid module path: $target"
      return 1
    }
    [[ -f "$target/build.gradle.kts" || -f "$target/build.gradle" ]] || {
      log_error "Gradle module not found: $target"
      return 1
    }
  done
  return 0
}

convert_to_gradle_path() {
  local tasks=()
  for target in "$@"; do
    tasks+=(":${target//\//:}:bootJar")
  done
  echo "${tasks[@]}"
}

get_branch_head_hash() {
  echo $(git rev-parse --short HEAD)
}
