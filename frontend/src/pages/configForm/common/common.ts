/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import i18n from "@/locale/i18n";

const sourceTypes = [
  {
    value: 'input',
    label: i18n.t('selectionCustom'),
  },
];
const multiModal = [
  {
    value: 'file',
    label: i18n.t('fileLabel'),
  },
  {
    value: 'image',
    label: i18n.t('picture'),
  },
  {
    value: 'radio',
    label: i18n.t('audio'),
  },
  {
    value: 'video',
    label: i18n.t('video'),
  },
];
const pluginItems = [
  { key: 'FIT', label: i18n.t('tool') },
  { key: 'WATERFLOW', label: i18n.t('workflow') }
];
export { multiModal, sourceTypes, pluginItems };
