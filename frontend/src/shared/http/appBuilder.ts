/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { get } from "./http";
import serviceConfig from './httpConfig';
const { TOOL_URL, AIPP_URL } = serviceConfig;

// 获取模型列表接口
const getModels = () => {
  return new Promise((resolve, reject) => {
    get(`${AIPP_URL}/fetch/model-list?type=chat_completions`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}

// 获取工具 工具流列表
const getTools = (params) => {
  let url = `${TOOL_URL}/store/platform/jade/categories/TOOL?pageNum=${params.pageNum}&pageSize=${params.pageSize}`;
  if (params.includeTags) {
    url += `&includeTags=${params.includeTags}`;
  }
  if (params.excludeTags) {
    url += `&excludeTags=${params.excludeTags}`;
  }
  return new Promise((resolve, reject) => {
    get(url).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}

// 获取个人插件列表
const getPersonPluginData = (tenant_Id, params) => {
  return new Promise((resolve, reject) => {
    get(`${AIPP_URL}/${tenant_Id}/store/plugins`, params).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}

const getWaterFlows = (params) => {
  let url = `${AIPP_URL}/${params.tenantId}/store/waterflow?pageNum=${params.pageNum}&pageSize=${params.pageSize}`;
  return new Promise((resolve, reject) => {
    get(url).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}

// 获取灵感大全fitable列表
const getFitables = () => {
  return new Promise((resolve, reject) => {
    get(`${TOOL_URL}/jober/v1/api/public/genericables/d01041a73e00ac46bedde08d02c6818e`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
};

// 获取流程配置面板配置
const getAddFlowConfig = (tenant_Id, params) => {
  return new Promise((resolve, reject) => {
    get(`${AIPP_URL}/${tenant_Id}/store/nodes`, params).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}
// 获取应用评估配置面板
const getEvaluateConfig = (tenant_Id, params)=>{
  return new Promise((resolve, reject) => {
    get(`${AIPP_URL}/${tenant_Id}/store/nodes/list`, params).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
};
// 获取hugging-face列表
const getHuggingFaceList = (tenant_Id, params) => {
  return new Promise((resolve, reject) => {
    get(`${AIPP_URL}/${tenant_Id}/store/models`, params).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}

export {
  getModels,
  getTools,
  getFitables,
  getAddFlowConfig,
  getWaterFlows,
  getHuggingFaceList,
  getPersonPluginData,
  getEvaluateConfig,
};
