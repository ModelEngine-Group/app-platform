#!/bin/bash
set -eu

export WORKSPACE=$(cd "$(dirname "$(readlink -f "$0")")" && pwd)
PLUGINS_DIR="${WORKSPACE}/../build/plugins"
SHARED_DIR="${WORKSPACE}/../build/shared"

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
if [ ! -d "$PLUGINS_DIR" ] || [ -z "$(ls -A "$PLUGINS_DIR" 2>/dev/null)" ]; then
    echo "Error: plugins directory is empty or does not exist: $PLUGINS_DIR"
    exit 1
fi

if [ ! -d "$SHARED_DIR" ] || [ -z "$(ls -A "$SHARED_DIR" 2>/dev/null)" ]; then
    echo "Error: shared directory is empty or does not exist: $SHARED_DIR"
    exit 1
fi

echo "=== Stopping app-builder service ==="
docker-compose stop app-builder

echo "=== Creating development version image ==="
# Use stable version as base
docker run -d --name app-builder-tmp --entrypoint sleep ${REPO}/app-builder:${BASE_VERSION} infinity

# Copy files
echo "Copying plugins..."
docker cp "$PLUGINS_DIR"/. app-builder-tmp:/opt/fit-framework/plugins/

echo "Copying shared libraries..."
docker cp "$SHARED_DIR"/. app-builder-tmp:/opt/fit-framework/shared/

# Commit as development version
echo "Committing development version image: ${DEV_VERSION}"
docker commit --change='ENTRYPOINT ["/opt/fit-framework/bin/start.sh"]' app-builder-tmp ${REPO}/app-builder:${DEV_VERSION}

# Create development tag (for docker-compose convenience)
docker tag ${REPO}/app-builder:${DEV_VERSION} ${REPO}/app-builder:dev-latest

echo "=== Cleaning up temporary container ==="
docker stop app-builder-tmp
docker rm app-builder-tmp

echo "=== Restarting services ==="
docker-compose -f docker-compose.dev.yml -p app-platform up -d app-builder

echo "=== Waiting for services to be ready ==="
# Use gtimeout on macOS or implement timeout logic ourselves
MAX_WAIT=800
WAITED=0
while [ $WAITED -lt $MAX_WAIT ]; do
    if docker-compose -f docker-compose.dev.yml -p app-platform ps app-builder | grep -q "healthy"; then
        echo "Services are ready!"
        break
    fi
    sleep 5
    WAITED=$((WAITED + 5))
    echo -n "."
done

if [ $WAITED -ge $MAX_WAIT ]; then
    echo "Warning: Service startup timeout, but continuing execution..."
fi

echo ""
echo "=== Completed! ==="
echo "Development version deployed: ${DEV_VERSION}"
echo "Current tag in use: dev-latest"
echo "Service URL: http://localhost:8001"
echo ""
echo "=== Version Management Commands ==="
echo "View all versions: docker images ${REPO}/app-builder"
