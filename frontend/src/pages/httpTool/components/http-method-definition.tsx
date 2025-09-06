/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useContext } from 'react';
import { Button } from 'antd';
import HttpInput from './http-input';
import HttpOutPut from './http-output';
import { HttpContext } from '../config/context';
import {
  tabeleDataProcess,
  validateTableData,
  getDefaultExpandedRowKeys,
  convertArrayToObject,
  checkNamesNotEmpty,
  setHttpMap,
  convertHttpMap,
} from '../utils';
import { deepClone } from '../../chatPreview/utils/chat-process';
import { Message } from '@/shared/utils/message';
import { useTranslation } from 'react-i18next';

const MethodDefinition = (props: any) => {
  const { setStepCurrent } = props;
  const { t } = useTranslation();
  const { httpInfo, setHttpInfo } = useContext(HttpContext);
  const [inputTableData, setInputTableData] = useState([]);
  const [outputTableData, setOutputTableData] = useState<any>([]);
  const [inputExpandedRowKeys, setInputExpandedRowKeys] = useState([]);
  const [outputExpandedRowKeys, setOutputExpandedRowKeys] = useState([]);

  // 输入参数拼接
  const inputParam = (type: string) => {
    const result = validateTableData(inputTableData);
    setInputTableData(deepClone(result));
    const hasError = checkNamesNotEmpty(result);
    if (hasError && type === 'next') {
      Message({ type: 'warning', content: t('cannotBeEmpty') });
      return;
    }
    let data = convertArrayToObject(inputTableData);
    let requestData = convertHttpMap(deepClone(inputTableData), true);
    httpInfo.schema.parameters.type = 'object';
    delete data.required;
    httpInfo.schema.parameters.required = Object.keys(data);
    httpInfo.schema.parameters.properties = data;
    httpInfo.schema.order = Object.keys(data);
    httpInfo.runnables.HTTP.mappings = requestData;
  };
  // 输出参数拼接
  const outputParam = (type: string) => {
    const result = validateTableData(outputTableData);
    setOutputTableData(deepClone(result));
    const hasError = checkNamesNotEmpty(result);
    if (hasError && type === 'next') {
      Message({ type: 'warning', content: t('cannotBeEmpty') });
      return;
    }
    let data = convertArrayToObject(outputTableData, 'output');
    httpInfo.schema.return.properties = data;
    httpInfo.schema.return.type = outputTableData[0].type;
  };
  // 下一步
  const confirm = (type: string) => {
    inputParam(type);
    outputParam(type);
    setHttpInfo(httpInfo);
    setStepCurrent(type === 'next' ? 2 : 0);
  };

  useEffect(() => {
    if (httpInfo.schema.name) {
      const { schema, runnables } = httpInfo;
      let httpMap = setHttpMap(runnables.HTTP.mappings);
      let inputInfo = tabeleDataProcess(schema.parameters, httpMap);
      let outputInfo = tabeleDataProcess(schema.return, httpMap);
      if (inputInfo && inputInfo.length) {
        let expandList = getDefaultExpandedRowKeys(inputInfo);
        setInputExpandedRowKeys(expandList);
      }
      setInputTableData(inputInfo);
      if (outputInfo && outputInfo.length) {
        let expandList = getDefaultExpandedRowKeys(outputInfo);
        setOutputExpandedRowKeys(expandList);
      }
      setOutputTableData(outputInfo);
    }
  }, [httpInfo]);

  return (
    <>
      <div className='http-tool-input'>
        <div className='http-input-content'>
          <div className='http-tool-title'>{t('input')}</div>
          <HttpInput
            inputTableData={inputTableData}
            setInputTableData={setInputTableData}
            expandedRowKeys={inputExpandedRowKeys}
            setExpandedRowKeys={setInputExpandedRowKeys}
          />
          <div className='http-tool-title'>{t('output')}</div>
          <HttpOutPut
            outputTableData={outputTableData}
            setOutputTableData={setOutputTableData}
            expandedRowKeys={outputExpandedRowKeys}
            setExpandedRowKeys={setOutputExpandedRowKeys}
          />
        </div>
        <div className='http-tool-footer'>
          <Button onClick={() => confirm('prev')}>{t('previousStep')}</Button>
          <Button type='primary' onClick={() => confirm('next')}>
            {t('nextStep')}
          </Button>
        </div>
      </div>
    </>
  );
};
export default MethodDefinition;
