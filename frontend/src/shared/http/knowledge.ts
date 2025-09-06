/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { get } from "./http";
import serviceConfig from './httpConfig';
const { PLUGIN_URL } = serviceConfig;

// 获取知识库卡片列表
const getKnowledgesCard = (params) => {
  return new Promise((resolve, reject) => {
    get(`${PLUGIN_URL}/knowledge-manager/list/repos?groupId=${params.groupId}&knowledgeConfigId=${params.knowledgeConfigId}&repoName=${params.repoName}&pageSize=${params.pageSize}&pageIndex=${params.pageIndex}`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}
// 获取知识库检索参数
const getSearchParams= (params) => {
  return new Promise((resolve, reject) => {
    get(`${PLUGIN_URL}/knowledge-manager/properties?groupId=${params.groupId}`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}

// 获取连接知识库列表
const getConnectKnowledgeList = () => {
  return new Promise((resolve, reject) => {
    get(`${PLUGIN_URL}/knowledge-manager/list/groups`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}

// 获取知识库集的apiKey的唯一Id
const getKnowledgeConfigId = (groupId) => {
  return new Promise((resolve, reject) => {
    get(`${PLUGIN_URL}/knowledge-manager/configId?groupId=${groupId}`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}

export {
  getKnowledgesCard,
  getSearchParams,
  getConnectKnowledgeList,
  getKnowledgeConfigId
};
