#!/bin/bash

directory="../icon"

# 创建目录（若不存在）
mkdir -p "$directory"

# 清空目录内容（仅在目录存在时执行）
if [ -d "$directory" ]; then
    find "$directory" -mindepth 1 -delete
fi

# 查找并复制所有 .png 文件
icon_list=$(find ../app-builder/builtin/app-template -name "*.png")

echo "${icon_list}"
for icon_file in ${icon_list}
do
  cp "$icon_file" "$directory"
done

exit 0