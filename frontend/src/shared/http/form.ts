/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { del, get, post, put, download } from './http';
import serviceConfig from './httpConfig';
const { AIPP_URL } = serviceConfig;

// 获取表单列表
export function getFormList(tenantId: string, data: { pageNum: number; pageSize: number; name?: string }) {
  const url = `${AIPP_URL}/${tenantId}/form/smart_form`;
  return get(url, data);
}
// 创建智能表单
export function createForm(tenantId: string, data: any) {
  const url = `${AIPP_URL}/${tenantId}/form/smart_form`;
  return post(url, data);
}
// 编辑智能表单
export function editForm(tenantId: string, formId:string, data: any) {
  const url = `${AIPP_URL}/${tenantId}/form/smart_form/${formId}`;
  return put(url, data);
}
// 上传表单文件
export function uploadForm(tenantId: string, data: any, headers:any) {
  const url = `${AIPP_URL}/${tenantId}/file/smart_form`;
  return post(url, data, { ...headers, 'Content-Type': 'multipart/form-data' });
}
// 下载模板
export function downloadForm(tenantId: string) {
  const url = `${AIPP_URL}/${tenantId}/file/smart_form/template`;
  return download(url);
}
// 删除智能表单
export function deleteForm(tenantId: string, formId: string) {
  const url = `${AIPP_URL}/${tenantId}/form/smart_form/${formId}`;
  return del(url);
}
