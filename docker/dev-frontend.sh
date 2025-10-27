#!/bin/bash
set -eu

export WORKSPACE=$(cd "$(dirname "$(readlink -f "$0")")" && pwd)
BUILD_DIR="${WORKSPACE}/../frontend/build/"

cd ${WORKSPACE}
source .env

# Generate development version tag
BASE_VERSION=${VERSION}
TIMESTAMP=$(date +%Y%m%d-%H%M%S)
GIT_COMMIT=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
DEV_VERSION="${BASE_VERSION}-dev-${TIMESTAMP}-${GIT_COMMIT}"

echo "=== Version Information ==="
echo "Base Version: ${BASE_VERSION}"
echo "Development Version: ${DEV_VERSION}"
echo "Git Commit: ${GIT_COMMIT}"

# Check local build artifacts
if [ ! -d "$BUILD_DIR" ] || [ -z "$(ls -A "$BUILD_DIR" 2>/dev/null)" ]; then
    echo "Error: plugins directory is empty or does not exist: $BUILD_DIR"
    exit 1
fi

echo "=== Stopping web service ==="
docker-compose stop web

echo "=== Creating development version image ==="
# Use stable version as base
docker run -d --name web-tmp --entrypoint sleep ${REPO}/jade-web:${BASE_VERSION} infinity

# Copy files
echo "Copying frontend files..."
docker exec  web-tmp rm -rf //usr//share//nginx//html
docker exec web-tmp mkdir -p //usr//share//nginx//html
docker cp "$BUILD_DIR"/. web-tmp:/usr/share/nginx/html/

# Commit as development version
echo "Committing development version image: ${DEV_VERSION}"
docker commit --change='ENTRYPOINT ["/init.sh"]' web-tmp ${REPO}/jade-web:${DEV_VERSION}

# Create development tag (for docker-compose convenience)
docker tag ${REPO}/jade-web:${DEV_VERSION} ${REPO}/jade-web:dev-latest

echo "=== Cleaning up temporary container ==="
docker stop web-tmp
docker rm web-tmp

echo "=== Restarting services ==="
docker-compose -f docker-compose.dev.yml -p app-platform up -d web

echo ""
echo "=== Completed! ==="
echo "Development version deployed: ${DEV_VERSION}"
echo "Current tag in use: dev-latest"
echo "Service URL: http://localhost:8001"
echo ""
echo "=== Version Management Commands ==="
echo "View all versions: docker images ${REPO}/jade-web"
