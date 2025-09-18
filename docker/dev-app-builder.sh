#!/bin/bash
set -eu

export WORKSPACE=$(cd "$(dirname "$(readlink -f "$0")")" && pwd)
PLUGINS_DIR="${WORKSPACE}/../build/plugins"
SHARED_DIR="${WORKSPACE}/../build/shared"

cd ${WORKSPACE}
source .env

# 临时启动容器
docker run -d --name app-builder-tmp --entrypoint sleep modelengine/app-builder:$VERSION infinity

# 拷贝本地编译产物到容器
if [ -d "$PLUGINS_DIR" ] && [ -n "$(ls -A "$PLUGINS_DIR")" ]; then
    ls | grep -E '\.jar$' | grep -v -E '^(fit|fel)' | xargs rm -f
    docker cp "$PLUGINS_DIR"/. app-builder-tmp:/opt/fit-framework/plugins/
else
    echo "Error: plugins directory is empty or doesn't exist, skipped copy."
fi

if [ -d "$SHARED_DIR" ] && [ -n "$(ls -A "$SHARED_DIR")" ]; then
    ls | grep -E '\.jar$' | grep -v '^(opentelemetry)' | xargs rm -f
    docker cp "$SHARED_DIR"/. app-builder-tmp:/opt/fit-framework/shared/
else
    echo "Error: shared directory is empty or doesn't exist, skipped copy."
fi

# 提交镜像
docker commit --change='ENTRYPOINT ["/opt/fit-framework/bin/start.sh"]' app-builder-tmp modelengine/app-builder:$VERSION

docker stop app-builder-tmp
docker rm app-builder-tmp

# 重启服务
docker-compose down
echo "Service stopped."

docker-compose -p app-platform up -d
echo "Service restarted."

docker stop db-initializer
docker rm db-initializer
echo "=== Finished ==="