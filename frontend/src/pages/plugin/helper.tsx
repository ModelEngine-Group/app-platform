/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { PluginIcons } from '../../components/icons/plugin';
import i18n from '@/locale/i18n';

export const IconMap = {
  HUGGINGFACE: {
    icon: <PluginIcons.HuggingFaceIcon />,
    name: 'HuggingFace',
  },
  BUILTIN: {
    icon: <PluginIcons.SytemIcon />,
    name: 'Builtin',
  },
  LANGCHAIN: {
    icon: <PluginIcons.LangchainIcon />,
    name: 'Langchain',
  },
  LLAMAINDEX: {
    icon: <PluginIcons.LiamaindexIcon />,
    name: 'Llamaindex',
  },
  FAVOURITE: {
    icon: null,
    name: i18n.t('myFavorites'),
  },
};

export const sourceTabs = [
  { key: 'APP', label: i18n.t('all') },
  { key: 'HTTP', label: 'Http' },
  { key: 'WATERFLOW', label: i18n.t('workflow') },
  { key: 'MINE', label: i18n.t('mine') },
];

export const paramsColumns = [
  {
    title: i18n.t('paramName'),
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: i18n.t('paramType'),
    dataIndex: 'type',
    key: 'type',
  },
  {
    title: i18n.t('paramDescription'),
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
  },
];

export const minePluginCategories = [
  { key: 'APP', label: i18n.t('all') },
  { key: 'TOOL', label: i18n.t('tool') },
  { key: 'HTTP', label: 'Http' },
  { key: 'WORKFLOW', label: i18n.t('workFlow') },
];

export const generalPluginCategories = ['APP', 'TOOL', 'HTTP'];

export const chatbotPluginCategories = ['CHATBOT', 'AGENT', 'WORKFLOW'];

export const outputColumns = [
  {
    title: i18n.t('paramType'),
    dataIndex: 'type',
    key: 'type',
  },
  {
    title: i18n.t('paramDescription'),
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
  },
];

export enum PluginCardTypeE {
  MARKET = 'market',
  MY = 'my',
}

export enum PluginStatusTypeE {
  DEPLOYED = 'deployed',
  DEPLOYING = 'deploying',
  UNDEPLOYED = 'undeployed',
  DEPLOYMENT_FAILED = 'deployment_failed',
  RELEASED = 'published',
}
export enum PluginCnType {
  DEPLOYED = i18n.t('deployed'),
  DEPLOYING = i18n.t('deployment'),
  UNDEPLOYED = i18n.t('notDeployed'),
  DEPLOYMENT_FAILED = i18n.t('deploymentFailed'),
  RELEASED = i18n.t('active'),
}
