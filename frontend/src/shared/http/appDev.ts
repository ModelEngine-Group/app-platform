/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import {del, get, patch} from "./http";
import serviceConfig from './httpConfig';
const collectUrl = '/api/jober/aipp';

const { AIPP_URL, COLLECT_URL = collectUrl } = serviceConfig;

// 获取应用开发列表
export function queryAppDevApi(tenantId,params) {
  return get(`${AIPP_URL}/${tenantId}/app`, params);
}

// 获取应用模板列表
export function getTemplateList(tenantId, params) {
  return get(`${AIPP_URL}/${tenantId}/template`, params);
}

// 删除应用
export function deleteAppApi(tenantId,appId) {
  return del(`${AIPP_URL}/${tenantId}/app/${appId}`);
}


/**
 * @description 更新收藏
 * @param {any} defaultApp - appid. 取消收藏
 * @property {string} userName - 用户名.
 * 
 * */ 
export function updateCollectionApp(userName, defaultApp) {
  const url = `${COLLECT_URL}/usr/info`
  return patch(url, {
    userName,
    defaultApp,
  });
}


/**
 * @description 查询用户收藏的应用详情
 * @property {string} id - 用户id.
 * */ 
export function getUserCollection(id) {
  const url = `${COLLECT_URL}/usr/collection/app/${id}`
  return get(url);
}

/**
 * @description 取消收藏
 * @property {string} usrInfo - 用户id.
 * @property {string} aippId - 用户appId.
 * */ 
export function cancelUserCollection(data) {
  data.appId = data.aippId;
  const url = `${COLLECT_URL}/usr/collection`;
  return del(url, data);
}