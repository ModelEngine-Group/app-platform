#!/bin/bash
set -eux
export WORKSPACE=$(cd "$(dirname "$(readlink -f "$0")")" && pwd)

bash ${WORKSPACE}/import_model.sh
echo "=== Deploying... ==="
cd ${WORKSPACE}
mkdir -p app-platform-tmp/app-builder
mkdir -p app-platform-tmp/fit-runtime
mkdir -p app-platform-tmp/jade-db
mkdir -p app-platform-tmp/log
echo "Starting service..."
docker-compose up -d
echo "Service started"
echo "=== Finished ==="
