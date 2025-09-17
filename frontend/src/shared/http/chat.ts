/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { del, get, post, patch } from './http';
import serviceConfig from './httpConfig';
const { PLUGIN_URL } = serviceConfig;

// 点赞点灭
export function feedbacksRq(params) {
  const url = `${PLUGIN_URL}/aipp/user/feedback`;
  return post(url, params);
}

// 查询应用对话列表
export function getChatList(tenantId, params) {
  const url = `${PLUGIN_URL}/v1/api/${tenantId}/chat/chat_list`;
  return post(url, params);
}

// 删除某次对话
export function deleteChat(tenantId, chatId) {
  const url = `${PLUGIN_URL}/v1/api/${tenantId}/chat?chat_id=${chatId}`;
  return del(url);
}

//清空APP的历史对话
export function clearChatHistory(tenantId, appId, params = {}) {
  let url = `${PLUGIN_URL}/v1/api/${tenantId}/chat?app_id=${appId}`;
  return del(url, params);
}
// 猜你想问
export function getRecommends(params) {
  const url = `${PLUGIN_URL}/v1/api/recommend`;
  return post(url, params);
}

// 获取点赞点灭详情
export function queryFeedback(id) {
  const url = `${PLUGIN_URL}/aipp/user/feedback/${id}`;
  return get(url);
}

// 更新点赞点灭
export function updateFeedback(id, data) {
  const url = `${PLUGIN_URL}/aipp/user/feedback/${id}`;
  return patch(url, data);
}

// 取消点赞点灭
export function deleteFeedback(id) {
  const url = `${PLUGIN_URL}/aipp/user/feedback/${id}`;
  return del(url);
}

export function queryPluginList() {
  const isProd = process.env.NODE_ENV === 'production';
  const isSpa = process.env.PACKAGE_MODE === 'spa';
  const prefix = isProd && isSpa ? '/apps/appengine' : '';
  return fetch(`${prefix}/plugins/manifest.json`)
    .then(resp => resp.json())
    .catch(() => []);
}
