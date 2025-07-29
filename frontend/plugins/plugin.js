/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

const fs = require('fs');

function ensure(fileName) {
  if (!fs.existsSync(fileName)) {
    fs.writeFileSync(fileName, '[]', { encoding: 'utf8' });
  }
}

function read(fileName) {
  return fs.readFileSync(fileName, 'utf8');
}

function write(fileName, content, appendContent = '') {
  if (!appendContent) return;
  try {
    const array = JSON.parse(content || '[]');
    const appendItem = JSON.parse(appendContent);

    // 不存在时新增，存在时更新
    const found = array.find(item => item.name === appendItem.name);
    if (!found) {
      array.push(appendItem)
    } else {
      Object.assign(found, appendItem);
    }

    fs.writeFileSync(fileName, JSON.stringify(array, null, 2), { encoding: 'utf8' });
  } catch (error) {
    console.error(error);
  }
}

function main() {
  const args = process.argv.slice(2);
  const content = args[0] || '';
  const fileName = args[1] || './manifest.json';

  ensure(fileName);
  write(fileName, read(fileName), content);
}

main();