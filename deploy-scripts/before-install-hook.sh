#!/bin/bash
set -e

mkdir -p /opt/aivle-mini-06/backend
mkdir -p /usr/share/nginx/html

if command -v systemctl >/dev/null 2>&1; then
  systemctl enable nginx || true
fi
