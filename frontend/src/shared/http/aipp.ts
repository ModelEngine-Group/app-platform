/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { del, get, post, put } from './http';
import serviceConfig from './httpConfig';
const { AIPP_URL, PLUGIN_URL, APP_URL, LOGIN_URL } = serviceConfig;
const sso_url = '/v1/user/sso_login_info';
const sso_url_spa = '/framework/v1/sessions/current';

// 获取当前用户信息
export const getCurUser = () => {
  return new Promise((resolve, reject) => {
    get(`${PLUGIN_URL}` + sso_url).then(
      (res) => {
        resolve(res);
      },
      (error) => {
        reject(error);
      }
    );
  });
};
// 获取oms当前用户信息
export const getOmsCurUser = () => {
  return new Promise((resolve, reject) => {
    get(`${LOGIN_URL}` + sso_url_spa).then(
      (res) => {
        resolve(res);
      },
      (error) => {
        reject(error);
      }
    );
  });
};

// 获取当前用户角色
export function getUserRole() {
  return get(`${LOGIN_URL}/framework/v1/sessions/current`);
}
// 查询应用列表
export function getAippList(tenant_id, params, limit, offset, name) {
  let url = `${AIPP_URL}/${tenant_id}/app?offset=${offset}&limit=${limit}`;
  if (name) {
    url += `&name=${name}`;
  }
  return get(url, params);
}
// 创建应用
export function createAipp(tenantId, appId, params) {
  return post(`${AIPP_URL}/${tenantId}/app/${appId}`, params);
}
// 创建智能体
export function createAgent(tenantId, params) {
  return post(`${AIPP_URL}/${tenantId}/agent`, params);
}
// 获取应用详情
export function getAppInfo(tenantId, appId) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}`);
}
// 获取已发布应用的app_id
export function getPublishAppId(tenantId, appId) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/latest_published`);
}
// 获取公共访问应用的详情
export function getPreviewAppInfo(uid) {
  return get(`${AIPP_URL}/chat/${uid}`);
}
// 点击去编排
export function getAppInfoByVersion(tenantId, appId) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/latest_orchestration`);
}
// 更新应用全部详情
export function updateAppInfo(tenantId, appId, params) {
  return put(`${AIPP_URL}/${tenantId}/app/${appId}`, params);
}
// 更新表单详情
export function updateFormInfo(tenantId, appId, params) {
  return put(`${AIPP_URL}/${tenantId}/app/${appId}/config`, params);
}
// 更新工作流详情
export function updateFlowInfo(tenantId, appId, params) {
  return put(`${AIPP_URL}/${tenantId}/app/${appId}/graph`, params);
}
// 调试应用
export function aippDebug(tenantId, appId, params, chatType = '') {
  let url = `${AIPP_URL}/${tenantId}/app/${appId}/debug`;
  if (chatType !== 'inactive') {
    url = `${AIPP_URL}/${tenantId}/app/${appId}/latest_published `;
    return get(url);
  } else {
    return post(url, params);
  }
}

// 发布应用
export function appPublish(tenantId, appId, params) {
  return post(`${AIPP_URL}/${tenantId}/app/${appId}/publish`, params);
}
// 轮训接口
export function reGetInstance(tenantId, appId, instanceId, version) {
  return get(`${AIPP_URL}/${tenantId}/aipp/${appId}/instances/${instanceId}?version=${version}`);
}
// 获取历史对话
export function getRecentInstances(tenantId, appId, type) {
  return get(`${AIPP_URL}/${tenantId}/log/app/${appId}/recent?type=${type}`);
}
// 获取多应用历史对话
export function getAppRecentlog(tenantId, appId, type) {
  return get(`${AIPP_URL}/${tenantId}/log/app/${appId}/chat/recent?type=${type}`);
}
// 获取会话历史对话
export function getChatRecentLog(tenantId, chatId, appId) {
  return get(`${AIPP_URL}/${tenantId}/log/app/${appId}/chat/${chatId}`);
}
// 删除历史对话记录
export function clearInstance(tenantId, appId, type) {
  return del(`${AIPP_URL}/${tenantId}/log/app/${appId}?type=${type}`);
}
// 用户自勾选删除历史记录
export function clearChat(appId, list) {
  return del(`${AIPP_URL}/${appId}/log/logs`, list);
}
// 终止当前对话
export function stopInstance(tenantId, instanceId, params) {
  if (params.logId) {
    return put(`${AIPP_URL}/${tenantId}/instances/${instanceId}/terminate/log/${params.logId}`, params);
  }
  return put(`${AIPP_URL}/${tenantId}/instances/${instanceId}/terminate`, params);
}
// 获取灵感大全部门数据
export function queryDepartMent(tenantId, appId, debug = false) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/prompt?isDebug=${debug}`);
}
// 添加灵感大全部门数据
export function addInspiration(tenantId, appId, id, data) {
  return post(`${AIPP_URL}/${tenantId}/app/${appId}/prompt/${id}`, data);
}
// 编辑灵感大全部门数据
export function editInspiration(tenantId, appId, categoryId, id, data) {
  return put(`${AIPP_URL}/${tenantId}/app/${appId}/prompt/${categoryId}/inspiration/${id}`, data);
}
// 删除灵感大全部门数据
export function deleteInspiration(tenantId, appId, categoryId, id) {
  return del(`${AIPP_URL}/${tenantId}/app/${appId}/prompt/${categoryId}/inspiration/${id}`);
}
// 获取灵感大全列表数据
export function queryInspiration(tenantId, appId, categoryId, debug = false) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/prompt/${categoryId}?isDebug=${debug}`);
}
// 获取灵感大全下拉数据
export function queryInspirationSelect(tenantId, fitableid, params) {
  return post(`${AIPP_URL}/${tenantId}/genericables/fitables/${fitableid}`, params);
}
// 获取灵感大全下拉数据
export function getLatestChatId(tenantId, params) {
  return post(`${AIPP_URL}/${tenantId}/chat/chat_info`, params);
}
// 文件上传
export function uploadChatFile(tenantId, appId = '', data, headers) {
  return post(`${AIPP_URL}/${tenantId}/file?aipp_id=${appId}`, data, { ...headers, 'Content-Type': 'multipart/form-data' });

}
// 多文件上传
export function uploadMultipleFile(tenantId, appId, data) {
  return post(`${AIPP_URL}/${tenantId}/files?app_id=${appId}`, data, { 'Content-Type': 'multipart/form-data' });
}

// 文件上传
export function uploadImage(tenantId, data, headers) {
  return post(`${AIPP_URL}/${tenantId}/file`, data, { ...headers, 'Content-Type': 'multipart/form-data' });
}
// 调试轮询
export function reTestInstance(tenantId, aippId, instanceId, version) {
  return get(
    `${AIPP_URL}/${tenantId}/aipp/${aippId}/instances/${instanceId}/runtime?version=${version}`
  );
}
// 获取版本历史记录
export function getVersion(tenantId, appId, type, offset, limit) {
  return get(
    `${AIPP_URL}/${tenantId}/app/${appId}/recentPublished?offset=${offset}&limit=${limit}${type ? `&type=${type}` : ''}`
  );
}
// 获取插件接口
export function getToolList(params) {
  return get(`${AIPP_URL}/store/plugins/search`, params);
}

// AI生成提示词
export function generatePrompt(params) {
  return post(`${AIPP_URL}/model/prompt`, params);
}
// 应用导入
export function importApp(tenantId, data) {
  return post(`${AIPP_URL}/${tenantId}/app/import`, data, { 'Content-Type': 'multipart/form-data' });
}
// 应用导出
export function exportApp(tenantId, appId) {
  return get(`${AIPP_URL}/${tenantId}/app/export/${appId}`);
}
// 获取应用配置校验清单
export function getCheckList(tenantId, params) {
  return post(`${AIPP_URL}/${tenantId}/app/available-check`, params);
}
// 退出登录
export function userLogOut() {
  return post(`${LOGIN_URL}/framework/v1/sessions/clear?isTimeout=false`, {});
}
// 获取应用分类
export function getAppCategories (tenantId) {
  return get(`${AIPP_URL}/${tenantId}/app/type`)
}
// 根据模板创建应用
export function templateCreateAipp(tenantId, params) {
  return post(`${AIPP_URL}/${tenantId}/template/create`, params);
}
// 获取api文档
export function getApiDocument() {
  return get(`${AIPP_URL}/document`);
}
// 恢复应用到某个发布版本
export function resetApp(tenantId, appId, params, headers) {
  return post(`${AIPP_URL}/${tenantId}/app/${appId}/recover`, params, headers);
}
