/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { get, post, del } from './http';
import serviceConfig from './httpConfig';
const { PLUGIN_URL, AIPP_URL } = serviceConfig;
// 获取插件工具列表，应用于流程编排页面
export function getPluginTools(data: {
  pageNum: number;
  pageSize: number;
  includeTags: string;
  name: string;
}, excludeTags: string = '') {
  const url = `${PLUGIN_URL}/store/plugins/tools/search?${excludeTags}`;
  return get(url, { ...data });
}
// 获取插件列表，应用于插件市场
export function getPlugins(data: {
  pageNum: number;
  pageSize: number;
  includeTags: string;
  name: string;
}, excludeTags: string = '') {
  const url = `${PLUGIN_URL}/store/plugins/search?${excludeTags}`;
  return get(url, { ...data });
}
// 删除插件
export function deletePluginAPI(plugin_id: string) {
  const url = `${PLUGIN_URL}/plugins/delete/${plugin_id}`;
  return del(url);
}
export function getPluginDetail(pluginId) {
  const url = `${PLUGIN_URL}/store/plugins/${pluginId}`;
  return get(url);
}
export function getPluginFlowDetail(pluginId) {
  const url = `${PLUGIN_URL}/store/plugins/tools/${pluginId}`;
  return get(url);
}
// 插件列表
export function getToolsList(params) {
  const url = `${PLUGIN_URL}/tools/search`;
  return get(url, params);
}
// 我的-工具
export function getPluginTool(tenantId, data: { pageNum: number; pageSize: number; }) {
  const url = `${AIPP_URL}/${tenantId}/store/plugins?excludeTags=APP&excludeTags=WATERFLOW`;
  return get(url, data);
}
// 根据uniqueName获取插件
export function getPluginByUniqueName(queryString) {
  const url = `${PLUGIN_URL}/store/plugins/tools?${queryString}`;
  return get(url);
}
// 我的-工具流
export function getPluginWaterFlow(
  tenantId,
  data: { offset: number; limit: number; type: string, name: string }
) {
  const url = `${PLUGIN_URL}/v1/api/${tenantId}/app`;
  return get(url, data);
}
// 我的-已发布（工具+工具流)
export function getMyPlugin(tenantId, data, type = '') {
  let str = type !== 'modal' ? 'excludeTags=APP' : 'excludeTags=APP&excludeTags=WATERFLOW';
  const url = `${AIPP_URL}/${tenantId}/store/plugins?${str}`;
  return get(url, data);
}
// 确认上传插件
export function uploadPlugin(param, toolsName) {
  let toolsNameParam = toolsName.map((item)=>{
    return `toolNames=${item}`
  })
  const url = `${PLUGIN_URL}/plugins/save/plugins?${toolsNameParam.join('&')}`;
  return post(url, param, {
    headers: {
      'Content-Type': 'application/form-data'
    },
  });
}
// 获取部署插件列表
export function getDeployTool(status) {
  const url = `${PLUGIN_URL}/plugins/by-status/${status}`;
  return get(url);
}

// 部署部署插件列表
export function setDeployTool(data) {
  const url = `${PLUGIN_URL}/plugins/deploy`;
  return post(url, data);
}
// 创建Http
export function createHttp(data) {
  const url = `${PLUGIN_URL}/plugins/save/http`;
  return post(url, data);
}

// 重复工具校验
export function existDefs(data) {
  let defGroupNamesParam = data.map((item: any)=>{
    return `defGroupNames=${item}`
  })
  const url = `${PLUGIN_URL}/tools/exist/defs?${defGroupNamesParam.join('&')}`;
  return get(url);
}

// 获取对话助手、智能体、工作流
export function getPluginListByCategory(data) {
  const url = `${PLUGIN_URL}/store/apps/search?pageNum=${data.pageNum}&pageSize=${data.pageSize}&includeTags=APP&&appCategory=${data.type}`;
  return get(url);
}

// 获取对话助手、智能体、工作流详情
export function getChatbotPluginDetail(data) {
  const url = `${PLUGIN_URL}/store/apps/${data}`;
  return get(url);
}