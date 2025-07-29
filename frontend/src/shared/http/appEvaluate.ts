/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { del, get, post, put } from './http';
import serviceConfig from './httpConfig';
const { PLUGIN_URL} = serviceConfig;

// 测试集列表
export function getEvalDataList(params) {
  return get(`${PLUGIN_URL}/eval/dataset`, params);
}
// 创建测试集
export function createEvalData(params) {
  return post(`${PLUGIN_URL}/eval/dataset`, params);
}
// 上传文件
export function uploadTestSetFiles(data) {
  return post(`${PLUGIN_URL}/eval/file`, data, { 'Content-Type': 'application/octet-stream' });
}
// 删除测试集
export function deleteDataSetData(params) {
  let resParams = params.map((item) => {
    return `datasetIds=${item}`;
  });
  return del(`${PLUGIN_URL}/eval/dataset?${resParams.join('&')}`);
}
// 测试集详情
export function getEvalDataListDetail(params) {
  return get(`${PLUGIN_URL}/eval/dataset/${params}`);
}
// 编辑测试集
export function editEvalData(params) {
  return put(`${PLUGIN_URL}/eval/dataset`, params);
}
// 查询测试集数据
export function getEvalDataEvaluate(params) {
  return get(`${PLUGIN_URL}/eval/data`, params);
}
// 删除评估数据
export function deleteEvalData(params) {
  let resParams = params.map((item) => {
    return `dataIds=${item}`;
  });
  return del(`${PLUGIN_URL}/eval/data?${resParams.join('&')}`);
}
// 添加评估数据
export function addEvalData(params) {
  return post(`${PLUGIN_URL}/eval/data`, params);
}

// 创建评估任务
export function createEvaluate(params) {
  return post(`${PLUGIN_URL}/eval/task`, params);
}
// 查询评估任务列表
export function getEvaluateList(params) {
  return get(`${PLUGIN_URL}/eval/task`, params);
}
// 获取创建返回id
export function getEvaluateId(tenant_id, app_id, params) {
  return post(`${PLUGIN_URL}/v1/api/${tenant_id}/app/${app_id}`, params);
}
// 创建评估任务实例
export function createEvaluateInstance(params) {
  return post(`${PLUGIN_URL}/eval/task/instance`, params);
}
// 查询评估任务实例
export function getEvaluateInstance(params) {
  return get(`${PLUGIN_URL}/eval/task/instance`, params);
}
// 查询评估报告
export function getEvaluateReport(params) {
  return get(`${PLUGIN_URL}/eval/task/report`, params);
}
// 删除评估数据
export function deleteEvalTaxk(params) {
  let resParams = params.map((item) => {
    return `taskIds=${item}`;
  });
  return del(`${PLUGIN_URL}/eval/task?${resParams.join('&')}`);
}
// 查询评估任务用例结果
export function getEvaluateTaskResult(params) {
  return get(`${PLUGIN_URL}/eval/task/case`, params);
}
