#!/usr/bin/env bash
# Register Debezium outbox connector with Kafka Connect.
# Run after: docker compose up -d
# Connect must be up: http://localhost:8083

set -e
CONNECT_URL="${CONNECT_URL:-http://localhost:8083}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONNECTOR_JSON="${SCRIPT_DIR}/outbox-connector.json"

echo "Waiting for Connect at ${CONNECT_URL}..."
until curl -sf "${CONNECT_URL}/" > /dev/null 2>&1; do
  sleep 2
done
echo "Connect is up."

echo "Registering outbox-connector..."
HTTP=$(curl -s -o /tmp/connector-response -w "%{http_code}" \
  -X POST \
  -H "Content-Type: application/json" \
  -d @"${CONNECTOR_JSON}" \
  "${CONNECT_URL}/connectors")

if [ "$HTTP" = "201" ]; then
  echo "Connector created."
elif [ "$HTTP" = "409" ]; then
  echo "Connector already exists."
else
  echo "Unexpected response: $HTTP"
  cat /tmp/connector-response
  exit 1
fi
