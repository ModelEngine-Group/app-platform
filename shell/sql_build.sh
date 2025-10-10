#!/bin/bash

directory="../sql"  # 替换为实际目录路径

# 创建目录（若不存在）
mkdir -p "$directory"

# 清空目录内容（仅在目录存在时执行）
if [ -d "$directory" ]; then
    find "$directory" -mindepth 1 -delete
fi

mkdir -p "$directory/schema"
mkdir -p "$directory/data"

# app plugin相关sql语句
app_plugin_schema_sql_list=$(find ../app-builder/plugins/aipp-plugin/src/main/resources/sql/schema -name "*.sql")
echo "${app_plugin_schema_sql_list}"
for i in ${app_plugin_schema_sql_list}
do
  cp "$i" "$directory/schema"
done

app_plugin_data_sql_list=$(find ../app-builder/plugins/aipp-plugin/src/main/resources/sql/data -name "*.sql")
echo "${app_plugin_data_sql_list}"
for i in ${app_plugin_data_sql_list}
do
  cp "$i" "$directory/data"
done

aipp_parallel_tool_data_sql_list=$(find ../app-builder/plugins/aipp-parallel-tool/src/main/resources/sql/data -name "*.sql")
echo "${aipp_parallel_tool_data_sql_list}"
for i in ${aipp_parallel_tool_data_sql_list}
do
  cp "$i" "$directory/data"
done

# store相关sql语句
store_schema_sql_list=$(find ../carver/plugins/tool-repository-postgresql/src/main/resources/sql/schema -name "*.sql")
echo "${store_schema_sql_list}"
for i in ${store_schema_sql_list}
do
  cp "$i" "$directory/schema"
done

store_schema_sql_list_task=$(find ../store/plugins/store-repository-postgresql/src/main/resources/sql/schema -name "*.sql")
echo "${store_schema_sql_list_task}"
for i in ${store_schema_sql_list_task}
do
  cp "$i" "$directory/schema"
done

store_data_sql_list_task=$(find ../store/plugins/store-repository-postgresql/src/main/resources/sql/data -name "*.sql")
echo "${store_data_sql_list_task}"
for i in ${store_data_sql_list_task}
do
  cp "$i" "$directory/data"
done

# app-engine-announcement 相关sql 脚本
app_announcement_schema_sql_list=$(find ../app-builder/plugins/app-announcement/src/main/resources/sql/schema -name "*.sql")
echo "${app_announcement_schema_sql_list}"
for i in ${app_announcement_schema_sql_list}
do
  cp "$i" "$directory/schema"
done

# app-engine-metrics 相关sql 脚本
app_metrics_schema_sql_list=$(find ../app-builder/plugins/app-metrics/src/main/resources/sql/schema -name "*.sql")
echo "${app_metrics_schema_sql_list}"
for i in ${app_metrics_schema_sql_list}
do
  cp "$i" "$directory/schema"
done

app_base_schema_sql_list=$(find ../app-builder/plugins/app-base/src/main/resources/sql/schema -name "*.sql")
echo "${app_base_schema_sql_list}"
for i in ${app_base_schema_sql_list}
do
  cp "$i" "$directory/schema"
done

# app-eval 相关 sql 脚本
eval_dataset_schema_sql_list=$(find ../app-builder/plugins/eval-dataset/src/main/resources/sql/schema -name "*.sql")
echo "${eval_dataset_schema_sql_list}"
for i in ${eval_dataset_schema_sql_list}
do
  cp "$i" "$directory/schema"
done

eval_task_schema_sql_list=$(find ../app-builder/plugins/eval-task/src/main/resources/sql/schema -name "*.sql")
echo "${eval_task_schema_sql_list}"
for i in ${eval_task_schema_sql_list}
do
  cp "$i" "$directory/schema"
done

app_worker_schema_sql_list=$(find ../app-builder/plugins/simple-uid-generator/src/main/resources/sql/schema -name "*.sql")
echo "${app_worker_schema_sql_list}"
for i in ${app_worker_schema_sql_list}
do
  cp "$i" "$directory/schema"
done

# 联网搜索相关 sql 脚本
cp ../app-builder/plugins/fit_py_internet_search/schema/search-online-plugin.sql "$directory/data"

# 自定义模型相关 sql 脚本
app_model_center_schema_sql_list=$(find ../app-builder/plugins/aipp-custom-model-center/src/main/resources/sql/schema -name "*.sql")
echo "${app_model_center_schema_sql_list}"
for i in ${app_model_center_schema_sql_list}
do
  cp "$i" "$directory/schema"
done

app_model_center_data_sql_list=$(find ../app-builder/plugins/aipp-custom-model-center/src/main/resources/sql/data -name "*.sql")
echo "${app_model_center_data_sql_list}"
for i in ${app_model_center_data_sql_list}
do
  cp "$i" "$directory/data"
done

# 自定义知识库相关 sql 脚本
app_knowledge_schema_sql_list=$(find ../app-builder/plugins/knowledge-manager/src/main/resources/sql/schema -name "*.sql")
echo "${app_knowledge_schema_sql_list}"
for i in ${app_knowledge_schema_sql_list}
do
  cp "$i" "$directory/schema"
done

app_knowledge_data_sql_list=$(find ../app-builder/plugins/knowledge-manager/src/main/resources/sql/data -name "*.sql")
echo "${app_knowledge_data_sql_list}"
for i in ${app_knowledge_data_sql_list}
do
  cp "$i" "$directory/data"
done

# wenjie 相关 sql 脚本
app_wenjie_data_sql_list=$(find ../app-builder/plugins/plugins-show-case-parent/aito-data/src/main/resources/sql/data -name "*.sql")
echo "${app_wenjie_data_sql_list}"
for i in ${app_wenjie_data_sql_list}
do
  cp "$i" "$directory/data"
done

# app-template 相关 sql 脚本
app_template_data_sql_list=$(find "../app-builder/builtin/app-template" -name '*.sql')
echo "${app_template_data_sql_list}"
for sql_file in ${app_template_data_sql_list}
do
  cp "$sql_file" "$directory/data"
done

exit 0