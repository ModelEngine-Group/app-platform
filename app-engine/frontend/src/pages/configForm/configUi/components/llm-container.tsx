/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useRef, useState } from 'react';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setConfigItem } from '@/store/appConfig/config';
import LLM from './llm';
import { Collapse, Form } from 'antd';
import { pick } from 'lodash';
import CloseImg from '@/assets/images/close_arrow.png';
import OpenImg from '@/assets/images/open_arrow.png';
const { Panel } = Collapse;

const LLMContainer = (props) => {
  const { graphOperator, config, updateData, validateList, readOnly } = props;
  const [validateItem, setValidateItem] = useState({});
  const dispatch = useAppDispatch();
  const appConfig = useAppSelector((state) => state.appConfigStore.inputConfigData);
  const [form] = Form.useForm();
  const llmRef = useRef();

  // 更新model
  const updateModel = (value) => {
    form.validateFields().then(() => {
      if (config.from === 'graph') {
        graphOperator.update(config.defaultValue, value);
      } else {
        dispatch(setConfigItem({ key: config.name, value }));
      }
      updateData();
    }).catch((errorInfo) => { })
  };

  useEffect(() => {
    if (!config.from) {
      return;
    }
    const modelData = (config.from === 'graph' ? graphOperator.getConfig(config.defaultValue) : config.defaultValue);
    form.setFieldsValue(pick(modelData, ['model', 'temperature', 'systemPrompt']));
    llmRef.current.setPromptValue(modelData.systemPrompt);
  }, [config, appConfig]);

  useEffect(() => {
    const validateData = validateList.find(item => item.type === 'llmNodeState' && item.configName === 'accessInfo');
    if (validateData) {
      setValidateItem(validateData);
      form.validateFields();
    }
  }, [validateList]);

  return <>
    <Collapse
      bordered={false}
      expandIcon={({ isActive }) => isActive ? <img src={CloseImg} alt="" /> : <img src={OpenImg} alt="" />}
      defaultActiveKey={['model']}
    >
      <Panel header={config.description} forceRender key='model' className="site-collapse-custom-panel">
        <Form form={form} layout='vertical' disabled={readOnly}>
          <LLM llmRef={llmRef} form={form} validateItem={validateItem} updateData={updateModel} readOnly={readOnly} />
        </Form>
      </Panel>
    </Collapse>
  </>
};

export default LLMContainer;
