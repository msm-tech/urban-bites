#!/usr/bin/env bash
# run-dev.sh - Starts backend and frontend (Linux/macOS)
# Run from repo root: ./run-dev.sh

# go to repo root (script's directory)
cd "$(dirname "$0")" || exit 1

# Start backend in background
echo "Starting backend..."
( cd backend && ./mvnw spring-boot:run ) &
BACKEND_PID=$!

echo "Backend started with PID $BACKEND_PID"

# Wait up to 180 seconds for backend to respond on http://localhost:8080/api/health
MAX_WAIT=180
ELAPSED=0
SLEEP_INTERVAL=3
HEALTH_URL="http://localhost:8080/api/health"

echo "Waiting for backend to be available at $HEALTH_URL ..."
while [ $ELAPSED -lt $MAX_WAIT ]; do
  # Try to fetch health URL with a short timeout
  if curl --silent --max-time 5 "$HEALTH_URL" > /dev/null 2>&1; then
    echo "Backend is up after ${ELAPSED}s"
    break
  fi
  sleep $SLEEP_INTERVAL
  ELAPSED=$((ELAPSED + SLEEP_INTERVAL))
done

if [ $ELAPSED -ge $MAX_WAIT ]; then
  echo "WARNING: Backend did not respond after ${MAX_WAIT}s. Starting frontend anyway."
fi

# Start frontend in foreground (so logs are visible)
echo "Starting frontend..."
cd frontend || exit 1
npm install
npm start

# When frontend exits, kill backend
echo "Frontend stopped, stopping backend (PID $BACKEND_PID)"
kill $BACKEND_PID || true
