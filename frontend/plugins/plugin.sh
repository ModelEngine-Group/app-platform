#！/bin/bash
#set -x

ENV=$1
MODE=$2
PLUGIN_NAME=$3
CODE_URL=$4
CODE_BRANCH=$5

PLUGIN_URL=""
PLUGIN_ICON=""
PLUGIN_DEV_URL="http://localhost:3099/#/chat?sidebar=0"
if [ "$MODE" = "spa" ]; then
  PLUGIN_PROD_URL="/apps/appengine/plugins/$PLUGIN_NAME/index.html#/chat?sidebar=0"
else
  PLUGIN_PROD_URL="/plugins/$PLUGIN_NAME/index.html#/chat?sidebar=0"
fi
WORKSPACE=packages

cd_workspace() {
  if [ ! -d $WORKSPACE ]; then
    mkdir $WORKSPACE
  fi
  cd ./$WORKSPACE
  pwd
}

# 下载插件代码，或者更新代码
clone_repo() {
#   if [ -d $PLUGIN_NAME ]; then
#     cd $PLUGIN_NAME
#     git pull
#     cd ..
#   else
#     git clone $CODE_URL --branch $CODE_BRANCH $PLUGIN_NAME
#     cd $PLUGIN_NAME
#   fi
  cd $PLUGIN_NAME
}

# 安装插件依赖
install_dep() {
  npm cache clean -f
  npm install --legacy-peer-deps --registry=https://registry.npmmirror.com
}

# 本地运行
run_start() {
  PLUGIN_URL=$PLUGIN_DEV_URL

  if [[ $MODE = "spa" ]]; then
    start cmd /k "npm run start:spa"
  else
    start cmd /k "npm run start"
  fi

  PLUGIN_ICON=""
}

# 生产构建
run_build() {
  PLUGIN_URL=$PLUGIN_PROD_URL

  if [[ $MODE = "spa" ]]; then
    npm run build:single
  else
    npm run build
  fi

  PLUGIN_ICON="/apps/appengine/plugins/$PLUGIN_NAME/icon.jpg"
}

# 安装插件
install_plugin() {
  rm -rf ../../plugins/$PLUGIN_NAME
  cp -r ./dist ../../plugins/$PLUGIN_NAME
}

# 更新插件数据
update_plugin_meta() {
  node ../../plugins/plugin.js "{\"name\":\"$PLUGIN_NAME\",\"icon\":\"$PLUGIN_ICON\",\"url\":\"$PLUGIN_URL\"}" ../../plugins/manifest.json
}

echo "Start download and build plugin pathobot with mode:$MODE"

cd_workspace
clone_repo
install_dep

if [[ $ENV == "prod" ]]; then
  run_build
  install_plugin
else
  run_start
fi

update_plugin_meta