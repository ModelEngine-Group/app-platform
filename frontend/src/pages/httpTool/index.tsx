/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Steps } from 'antd';
import { stepItems, initHttpData } from './config';
import { useTranslation } from 'react-i18next';
import { HttpContext } from './config/context';
import { deepClone } from '../chatPreview/utils/chat-process';
import HttpForm from './components/http-form';
import MethodDefinition from './components/http-method-definition';
import InformationConfiguration from './components/http-information';
import LeftArrowImg from '@/assets/images/ai/left-arrow.png';
import './styles/index.scss';

const HttpToolIndex = () => {
  const { t } = useTranslation();
  const [stepCurrent, setStepCurrent] = useState(0);
  const [httpInfo, setHttpInfo] = useState(deepClone(initHttpData));
  const httpContext = {
    httpInfo,
    setHttpInfo,
  };
  useEffect(() => {
    return () => {
      setHttpInfo(initHttpData);
    };
  }, []);
  return (
    <>
      <div className='http-tool-container'>
        <div className='http-tool-head'>
          <img src={LeftArrowImg} onClick={() => window.history.back()} />
          <span className='title'>{t('createHttp')}</span>
        </div>
        <div className='http-tool-content'>
          <div className='http-tool-step'>
            <Steps size='small' current={stepCurrent} items={stepItems} />
          </div>
          <div className='http-tool-inner'>
            <HttpContext.Provider value={httpContext}>
              {stepCurrent === 0 && <HttpForm setStepCurrent={setStepCurrent} />}
              {stepCurrent === 1 && <MethodDefinition setStepCurrent={setStepCurrent} />}
              {stepCurrent === 2 && <InformationConfiguration setStepCurrent={setStepCurrent} />}
            </HttpContext.Provider>
          </div>
        </div>
      </div>
    </>
  );
};
export default HttpToolIndex;
