#!/bin/bash
set -eux
export WORKSPACE=$(cd "$(dirname "$(readlink -f "$0")")" && pwd)

echo "=== Deploying... ==="
cd ${WORKSPACE}
mkdir -p app-platform-tmp/app-builder
mkdir -p app-platform-tmp/fit-runtime
mkdir -p app-platform-tmp/jade-db
mkdir -p app-platform-tmp/log
echo "Starting service..."
docker-compose -p app-platform up -d
echo "Service started"
docker stop db-initializer
docker stop sql-initializer
docker rm db-initializer
docker rm sql-initializer
echo "=== Finished ==="
