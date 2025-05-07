#!/bin/bash

directory="./sql"  # 替换为实际目录路径

# 创建目录（若不存在）
mkdir -p "$directory"

# 清空目录内容（仅在目录存在时执行）
if [ -d "$directory" ]; then
    find "$directory" -mindepth 1 -delete
fi

mkdir -p "$directory/schema"
mkdir -p "$directory/data"

# app plugin相关sql语句
app_plugin_schema_sql_list=$(find ../app-builder/jane/plugins/aipp-plugin/src/main/resources/sql/schema -name "*.sql")
echo "${app_plugin_schema_sql_list}"
for i in ${app_plugin_schema_sql_list}
do
  cp "$i" sql/schema
done

app_plugin_data_sql_list=$(find ../app-builder/jane/plugins/aipp-plugin/src/main/resources/sql/data -name "*.sql")
echo "${app_plugin_data_sql_list}"
for i in ${app_plugin_data_sql_list}
do
  cp "$i" sql/data
done

# store相关sql语句
store_schema_sql_list=$(find ../carver/plugins/tool-repository-postgresql/src/main/resources/sql/schema -name "*.sql")
echo "${store_schema_sql_list}"
for i in ${store_schema_sql_list}
do
  cp "$i" sql/schema
done

store_schema_sql_list_task=$(find ../store/plugins/store-repository-postgresql/src/main/resources/sql/schema -name "*.sql")
echo "${store_schema_sql_list_task}"
for i in ${store_schema_sql_list_task}
do
  cp "$i" sql/schema
done

store_data_sql_list_task=$(find ../store/plugins/store-repository-postgresql/src/main/resources/sql/data -name "*.sql")
echo "${store_data_sql_list_task}"
for i in ${store_data_sql_list_task}
do
  cp "$i" sql/data
done

# app-engine-announcement 相关sql 脚本
app_announcement_schema_sql_list=$(find ../app-engine/plugins/app-announcement/src/main/resources/sql/schema -name "*.sql")
echo "${app_announcement_schema_sql_list}"
for i in ${app_announcement_schema_sql_list}
do
  cp "$i" sql/schema
done

# app-engine-metrics 相关sql 脚本
app_metrics_schema_sql_list=$(find ../app-engine/plugins/app-metrics/src/main/resources/sql/schema -name "*.sql")
echo "${app_metrics_schema_sql_list}"
for i in ${app_metrics_schema_sql_list}
do
  cp "$i" sql/schema
done

app_base_schema_sql_list=$(find ../app-engine/plugins/app-base/src/main/resources/sql/schema -name "*.sql")
echo "${app_base_schema_sql_list}"
for i in ${app_base_schema_sql_list}
do
  cp "$i" sql/schema
done

# app-eval 相关 sql 脚本
eval_dataset_schema_sql_list=$(find ../app-eval/plugins/eval-dataset/src/main/resources/sql/schema -name "*.sql")
echo "${eval_dataset_schema_sql_list}"
for i in ${eval_dataset_schema_sql_list}
do
  cp "$i" sql/schema
done

eval_task_schema_sql_list=$(find ../app-eval/plugins/eval-task/src/main/resources/sql/schema -name "*.sql")
echo "${eval_task_schema_sql_list}"
for i in ${eval_task_schema_sql_list}
do
  cp "$i" sql/schema
done

app_worker_schema_sql_list=$(find ../app-eval/plugins/simple-uid-generator/src/main/resources/sql/schema -name "*.sql")
echo "${app_worker_schema_sql_list}"
for i in ${app_worker_schema_sql_list}
do
  cp "$i" sql/schema
done

# 自定义模型相关 sql 脚本
app_model_center_schema_sql_list=$(find ../app-builder/plugins/aipp-custom-model-center/src/main/resources/sql/schema -name "*.sql")
echo "${app_model_center_schema_sql_list}"
for i in ${app_model_center_schema_sql_list}
do
  cp "$i" sql/schema
done

app_model_center_data_sql_list=$(find ../app-builder/plugins/aipp-custom-model-center/src/main/resources/sql/data -name "*.sql")
echo "${app_model_center_data_sql_list}"
for i in ${app_model_center_data_sql_list}
do
  cp "$i" sql/data
done

# 自定义知识库相关 sql 脚本
app_knowledge_schema_sql_list=$(find ../app-knowledge/plugins/knowledge-manager/src/main/resources/sql/schema -name "*.sql")
echo "${app_knowledge_schema_sql_list}"
for i in ${app_knowledge_schema_sql_list}
do
  cp "$i" sql/schema
done

app_knowledge_data_sql_list=$(find ../app-knowledge/plugins/knowledge-manager/src/main/resources/sql/data -name "*.sql")
echo "${app_knowledge_data_sql_list}"
for i in ${app_knowledge_data_sql_list}
do
  cp "$i" sql/data
done

# wenjie 相关 sql 脚本
app_wenjie_data_sql_list=$(find ../app-builder/plugins/plugins-show-case-parent/aito-data/src/main/resources/sql/data -name "*.sql")
echo "${app_wenjie_data_sql_list}"
for i in ${app_wenjie_data_sql_list}
do
  cp "$i" sql/data
done

DB_HOST=$1
DB_PORT=$2
DB_USER=$3
SCHEMA_SQL_DIR=sql/schema
DATA_SQL_DIR=sql/data
DB_NAME=app_builder

export PGPASSWORD=$4
export PGCLIENTENCODING="utf8"

# 检查变量是否设置
if [ -z "$DB_HOST" ]; then
    echo "数据库主机名未配置"
    exit 1
fi
if [ -z "$DB_PORT" ]; then
    echo "数据库端口未配置"
    exit 1
fi
if [ -z "$DB_USER" ]; then
    echo "数据库用户未配置"
    exit 1
fi
if [ -z "$PGPASSWORD" ]; then
    echo "数据库密码未配置"
    exit 1
fi

# 尝试连接数据库
while true
do
    psql -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USER} -c "\dn"
    if [ "$?" -eq 0 ];then
        if [ "$( psql -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USER} -c "SELECT 1 FROM pg_database WHERE datname='${DB_NAME}'" )" = '1' ];then
            echo "Database ${DB_NAME} already exists"
        else
            psql -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USER} -c "CREATE DATABASE ${DB_NAME};"
            echo "Database ${DB_NAME} already created"
        fi
        echo "Connect ${DB_HOST} success and check db ${DB_NAME} is exists"
        break
    fi

    sleep 1
done
echo "----------------"

if [ ! -z "${SCHEMA_SQL_DIR}" ]; then
    files=$(ls ${SCHEMA_SQL_DIR}/*.sql | sort)
    for sql_file in $files;do
        echo "Executing $sql_file..."
        # 执行 schema 相关的 sql 文件
        psql -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USER} -d ${DB_NAME} -f ${sql_file} -v ON_ERROR_STOP=1
        if [ "$?" -ne 0 ]; then
            echo "Error: executing $sql_file failed"
            exit 1
        fi
    done
fi
echo "----------------"

if [ ! -z "${DATA_SQL_DIR}" ]; then
    files=$(ls ${DATA_SQL_DIR}/*.sql | sort)
    for sql_file in $files;do
        echo "Executing $sql_file..."
        # 执行 data 相关的 sql 文件
        psql -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USER} -d ${DB_NAME} -f ${sql_file} -v ON_ERROR_STOP=1
        if [ "$?" -ne 0 ]; then
            echo "Error: executing $sql_file failed"
            exit 1
        fi
    done
fi
echo "----------------"

echo "Completed init DB ${DB_NAME}"
exit 0


