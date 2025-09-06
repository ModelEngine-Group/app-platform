/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Tabs } from 'antd';
import TestSet from './testSet';
import EvaluateTask from './task';
import { useTranslation } from 'react-i18next';
 
const AppEvaluate = () => {
  const { t } = useTranslation();
  const items = [
    { label: t('evaluateTasks'), key: '1', children: <EvaluateTask /> },
    { label: t('evaluateTestSet'), key: '2', children: <TestSet /> },
  ];
  return (
    <div className='aui-tab'>
      <Tabs defaultActiveKey='1' items={items} />
    </div>
  );
};

export default AppEvaluate;