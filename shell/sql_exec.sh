#!/bin/bash

DB_HOST=$1
DB_PORT=$2
DB_USER=$3
SCHEMA_SQL_DIR=../sql/schema
DATA_SQL_DIR=../sql/data
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