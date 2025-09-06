/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { CheckCircleFilled, CloseCircleFilled, LoadingOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import { useAppSelector } from "@/store/hook";
import terminateImg from '@/assets/images/ai/terminate.png';
import './styles/test-status.scss';

/**
 * 应用调试运行状态组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const TestStatus = () => {
  const { t } = useTranslation();
  const testTime = useAppSelector((state) => state.flowTestStore.testTime);
  const testStatus = useAppSelector((state) => state.flowTestStore.testStatus);
  return <>
    { testStatus && <span className={[
      'header-time',
      testStatus === 'Running' ? 'running' : '',
      testStatus === 'Terminate' ? 'terminate' : '',
      testStatus === 'Finished' ? 'finished' : '',
      testStatus === 'Error' ? 'error' : '',
      testStatus === 'Terminate' ? 'terminate' : '',
    ].join(' ').trim()}>
      {testStatus === 'Running' && <div><LoadingOutlined className='test-icon' /><span>{t('running')} {testTime}s</span></div>}
      {testStatus === 'Finished' && <div><CheckCircleFilled className='test-icon' /><span>{t('runSuccessfully')}</span></div>}
      {testStatus === 'Error' && <div><CloseCircleFilled className='test-icon' /><span>{t('runFailed')} {testTime}s</span></div>}
      {testStatus === 'Terminate' && <div><img src={terminateImg} className='test-icon test-img' /><span>{t('runTerminate')}</span></div>}
    </span>}
  </>
};

export default TestStatus;
