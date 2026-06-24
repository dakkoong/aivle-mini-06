#!/bin/bash
set -e

if [ -f /opt/aivle-mini-06/backend/app.pid ]; then
  PID=$(cat /opt/aivle-mini-06/backend/app.pid)
  if ps -p "$PID" >/dev/null 2>&1; then
    kill "$PID"
    sleep 5
  fi
  rm -f /opt/aivle-mini-06/backend/app.pid
fi

pkill -f "bookapp-0.0.1-SNAPSHOT.jar" || true
