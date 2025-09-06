/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import i18n from "@/locale/i18n";
import { Message } from '@/shared/utils/message';

// 添加工具
export const handleClickAddBasicNode = (item, e) => {
  if (!validate(item.type)) return;
  e.clientX += 100;
  window.agent.createNode(item.type, e, item);
}
// 拖动工具添加
export const handleDragBasicNode = (item, e) => {
  e.dataTransfer.setData('itemTab', 'basic');
  e.dataTransfer.setData('itemType', item.type);
  e.dataTransfer.setData('itemMetaData', JSON.stringify(item));
}
// 添加插件
export const handleClickAddToolNode = (type, e, metaData) => {
  if (!validate(type)) return;
  e.clientX += 100;
  window.agent.createNode(type, e, metaData);
}
// 拖动插件添加
export const handleDragToolNode = (item, e) => {
  e.dataTransfer.setData('itemTab', 'tool');
  e.dataTransfer.setData('itemType', item.type || 'toolInvokeNodeState');
  e.dataTransfer.setData('itemMetaData', JSON.stringify(item));
}

export const validate = (type, length = 1) => {
  let list = window.agent.graph.activePage.shapes || [];
  list = list.filter(item => !item.isTypeof('jadeEvent'));
  if (list.length >= 100) {
    Message({ type: 'warning', content: i18n.t('addPluginWarning') });
    return false
  }
  let shapesData = window.agent.serialize().pages[0].shapes || [];
  let componentList = shapesData.filter(item => item.type !== 'jadeEvent').map(item => item.type);
  let obj:any = {};
  componentList.forEach(item => {
    if (!obj[item]) {
      obj[item] = 1;
    } else {
      obj[item] += 1;
    }
  });
  if (obj[type] + length > 20) {
    Message({ type: 'warning', content: i18n.t('addNodeWarning') });
    return false
  }
  return true
}