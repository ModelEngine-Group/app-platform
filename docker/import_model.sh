#!/bin/bash
set -eux

export WORKSPACE=$(cd "$(dirname "$(readlink -f "$0")")" && pwd)
source ${WORKSPACE}/.env

export OS_TYPE=$(uname -s)
if [[ "${OS_TYPE}" == "Darwin" ]]; then
  export SED="sed -i '.bak' "
else
  export SED="sed -i"
fi
cp ${WORKSPACE}/sql/init/data/tr_init_models.sql.example ${WORKSPACE}/sql/init/data/tr_init_models.sql
${SED} "s#MODEL_NAME#${MODEL_NAME}#g" ${WORKSPACE}/sql/init/data/tr_init_models.sql
${SED} "s#BASE_URL#${BASE_URL}#g" ${WORKSPACE}/sql/init/data/tr_init_models.sql
${SED} "s#APIKEY#${APIKEY}#g" ${WORKSPACE}/sql/init/data/tr_init_models.sql

if [[ "${OS_TYPE}" == "Darwin" ]]; then
  rm -f docker/sql/init/data/tr_init_models.sql\'.bak\'
fi