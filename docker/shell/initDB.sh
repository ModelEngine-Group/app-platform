#!/bin/bash

export PGPASSWORD="$POSTGRES_PASSWORD"

DB_NAME=$1
SQL_DIR=$2
SCHEMA_SQL_DIR="$SQL_DIR/init/schema"
DATA_SQL_DIR="$SQL_DIR/init/data"
UPGRADE_SQL_DIR="$SQL_DIR/upgrade"

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
        echo "Contect ${DB_HOST} success and check db ${DB_NAME} is exists"
        break
    fi

    sleep 1
done
echo "----------------"

if [ "$IS_UPGRADE" = "true" ]; then
    echo "Executing upgrade.sql..."
    # 执行升级脚本
    if [ ! -z "${UPGRADE_SQL_DIR}" ]; then
        files=$(ls ${UPGRADE_SQL_DIR}/*.sql | sort)
        for sql_file in $files;do
            echo "Executing $sql_file..."
            psql -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USER} -d ${DB_NAME} -f ${sql_file} -v ON_ERROR_STOP=1
            if [ "$?" -ne 0 ]; then
                echo "Error: executing $sql_file failed"
                exit 1
            fi
        done
    fi
    echo "Finished executing upgrade.sql "
fi
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