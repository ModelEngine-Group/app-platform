/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { del, get, post, patch } from './http';
import serviceConfig from './httpConfig';
const { APP_URL } = serviceConfig;
import { getCookie } from '@/shared/utils/common';

// 获取应用市场列表
export function queryAppsApi(tenantId, params) {
  return get(`${APP_URL}/store/apps/search`, params);
}
export function getEvalTaskList(requestBody) {
  return post(`${APP_URL}/evalTask/list`, requestBody);
}

export function copyEvalTask(id, author) {
  return post(`${APP_URL}/evalTask/copy?id=${id}&author=${author}`, {});
}

// 应用测评=查看报告-根据报告ID查看调用轨迹
export function getEvalReportTrace(id) {
  return get(`${APP_URL}/evalTask/report?reportId=${id}`, {});
}

// 应用测评=查看报告-根据任务ID查看报告详情
export function getEvalTaskReport(id) {
  return get(`${APP_URL}/evalTask/reportSummary?evalTaskId=${id}`, {});
}

// 获取评估测试集
export function getEvalDataList(params) {
  return post(`${APP_URL}/evalDataset/list`, params);
}

// 创建测试集
export function createEvalData(data) {
  return post(`${APP_URL}/evalDataset`, data);
}

// 上传测试集文件
export function uploadTestSetFile(data) {
  const url = `${APP_URL}/evalDataset/upload`;
  return post(url, data);
}

// 查询单个数据集列表
export function getDataSetListById(params) {
  const url = `${APP_URL}/evalDataset`;
  return get(url, params);
}

/**
 * @description 修改数据集基本信息
 * @param {Data} data - 修改数据集基本信息.
 * @property {string} datasetName - 数据集名称.
 * @property {string} description - 数据集描述.
 * */
export function modifyDataSetBaseInfo(data) {
  const url = `${APP_URL}/evalDataset`;
  return patch(url, data);
}

/**
 * @typedef {Object} DataSet
 * @property {string} id - 评估数据集id.
 * @property {string} output - .
 * @property {string} input - .
 * */

/**
 * @description 修改评估数据集
 * @param {DataSet} dataSet - 修改数据集基本信息.
 * @property {string} id - 数据集id.
 * @property {string} output - 输入.
 * @property {string} input - 输出.
 * */
export function modifyDataSetListData(dataSet) {
  const url = `${APP_URL}/evalDataset/evalData`;
  return patch(url, dataSet);
}

/**
 * @description 创建评估数据集
 * @property {string} datasetId - 数据集id.
 * @property {string} output - 输入.
 * @property {string} input - 输出.
 * */
export function createDataSetListData(dataSet) {
  const url = `${APP_URL}/evalDataset/evalData`;
  return post(url, dataSet);
}

/**
 * @description 删除单条数据
 * @property {string} id - 数据集id.
 * */
export function deleteDataSetListData(id) {
  const url = `${APP_URL}/evalDataset/evalData/${id}`;
  return del(url);
}

/**
 * @description 删除测试集
 * @property {string} id - 数据集id.
 * */
export function deleteDataSetData(id) {
  const url = `${APP_URL}/evalDataset/${id}`;
  return del(url);
}

// 查询评估算法列表
export function getAlgorithmsList() {
  const url = `${APP_URL}/evalTask/evalAlgorithmList`;
  return get(url);
}

// 创建评估任务
export function createAssessmentTasks(data) {
  const url = `${APP_URL}/evalTask`;
  return post(url, data);
}

// 下载模板链接
export const downTemplateUrl = `${APP_URL}/eval_dataset_template.xlsx`;

// 查询分析数据 appId ,  timeType
export function getAnalysisData(data) {
  const url = `${APP_URL}/metrics/analysis`;
  return get(url, data);
}

// 查询反馈数据 isSortByCreateTime  isSortByResponseTime  answer  question pageIndex createTime appId pageSize orderDirection startTime createUser endTime
export function getFeedBackData(data) {
  const url = `${APP_URL}/metrics/feedback`;
  return post(url, data);
}

// 导出数据
export function exportFeedBackData(data) {
  const url = `${APP_URL}/metrics/export`;
  let xhr = new XMLHttpRequest();
  xhr.open('POST', url, true);
  xhr.setRequestHeader('Content-Type', 'application/json');
  xhr.setRequestHeader('X-Auth-Token', getCookie('__Host-X-Auth-Token'));
  xhr.setRequestHeader('X-Csrf-Token', getCookie('__Host-X-Csrf-Token'));
  xhr.responseType = 'blob';
  xhr.onload = function () {
    if (xhr.status === 200) {
      let contentDisposition = xhr.getResponseHeader('Content-Disposition');
      let match = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      let filename = match[1].replace(/['"]/g, '');
      filename = decodeURI(filename.split('UTF-8')[1]);
      let blob = new Blob([xhr.response], { type: 'application/octet-stream' });
      let url = URL.createObjectURL(blob);
      let a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
    }
  };
  xhr.send(JSON.stringify(data));
}

// 获取公告
export function getAnnouncement()  {
  return get(`${APP_URL}/announcement`);
}
