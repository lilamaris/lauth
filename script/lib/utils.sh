#!/usr/bin/env bash

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

fail() {
  log_error "$1"
  exit 1
}

run() {
  log_command "Running:" "$@"
  "$@"
}
