#!/bin/bash
set -eu
export WORKSPACE=$(cd "$(dirname "$(readlink -f "$0")")" && pwd)

echo "=== Deploying... ==="
cd ${WORKSPACE}
mkdir -p app-platform-tmp/app-builder
mkdir -p app-platform-tmp/fit-runtime
mkdir -p app-platform-tmp/jade-db
mkdir -p app-platform-tmp/log
read -p "Please input SiliconFlow API key (Official website: https://cloud.siliconflow.cn): " APIKEY
echo "The input API key is: ${APIKEY:0:8}****"
sed -i "s/APIKEY=.*/APIKEY=${APIKEY}/g" .env
echo "Starting service..."
docker-compose -p app-platform up -d
echo "Service started"
docker stop db-initializer
docker stop sql-initializer
docker rm db-initializer
docker rm sql-initializer
echo "=== Finished ==="
echo "Please visit url: http://localhost:8001"