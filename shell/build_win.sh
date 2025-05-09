#!/bin/bash

./sql_build.sh
./sql_exec.sh "$@"

current_dir=$(pwd)
root_dir=$(echo $current_dir | cut -c 1-2)
mkdir -p "${root_dir}"/var/share/smart_form

cp -r ../examples/app-demo/normal-form/* "${root_dir}"/var/share/smart_form/
cp -r ../examples/smart-form/* "${root_dir}"/var/share/smart_form/

exit 0