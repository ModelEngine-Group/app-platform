/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setConfigItem } from '@/store/appConfig/config';
import { setHistorySwitch } from '@/store/common/common';
import MultiConversationContent from './mutiConversation';
import { Collapse, Switch, Form } from 'antd';
import CloseImg from '@/assets/images/close_arrow.png';
import OpenImg from '@/assets/images/open_arrow.png';
const { Panel } = Collapse;

const MultiConversationContainer = (props) => {
  const { graphOperator, config, updateData, readOnly } = props;
  const [memoryValues, setMemoryValues] = useState(null);
  const [memorySwitch, setMemorySwitch] = useState(false);
  const haveSetMemory = useRef(false);
  const dispatch = useAppDispatch();
  const appConfig = useAppSelector((state) => state.appConfigStore.inputConfigData);
  const historySwitch = useAppSelector((state) => state.commonStore.historySwitch);
  const useMemory = useAppSelector((state) => state.commonStore.useMemory);
  const [form] = Form.useForm();
  
  // 更新Memory
  const updateMemory = (value) => {
    if (config.from === 'graph') {
      graphOperator.update(config.defaultValue, value);
    } else {
      dispatch(setConfigItem({ key: config.name, value }));
    }
    updateData();
  };

  // 更新是否展示多轮对话开关
  const historySwitchChange = (checked, event) => {
    event.stopPropagation();
    setMemorySwitch(checked);
    updateMemory({ memorySwitch: checked });
  };

  // 更新多轮对话type
  const onTypeChange = (e) => {
    updateMemory({ type: e});
  };

  // 更新多轮对话value
  const onValueChange = (newValue) => {
    updateMemory({ value: newValue });
  };

  useEffect(() => {
    if(!memoryValues) {
      return;
    }
    setMemorySwitch(memoryValues.memorySwitch);
    dispatch(setHistorySwitch(memoryValues.memorySwitch));
    form.setFieldsValue(memoryValues);
    haveSetMemory.current = true;
  }, [memoryValues]);

  useEffect(() => {
    if (useMemory !== memoryValues?.memorySwitch && haveSetMemory.current) {
      updateMemory({ memorySwitch: useMemory });
    }
  }, [useMemory]);

  useEffect(() => {
    if (!config.from) {
      return;
    }
    haveSetMemory.current = false;
    if (config.from === 'graph') {
      setMemoryValues(graphOperator.getConfig(config.defaultValue));
    } else {
      setMemoryValues(config.defaultValue);
    }
  }, [config, appConfig]);

  return <>
    <Collapse
      bordered={false}
      expandIcon={({ isActive }) => isActive ? <img src={CloseImg} alt="" /> : <img src={OpenImg} alt="" />}
    >
      <Panel header={<div className='panel-label'>
          <span>{config.description}</span>
          <Switch
            onChange={(checked, event) => historySwitchChange(checked, event)}
            checked={memorySwitch}
            disabled={readOnly}
          />
        </div>} forceRender key='memory' className="site-collapse-custom-panel">
        {
          memoryValues?.type &&
          <Form form={form} layout='vertical' disabled={readOnly}>
            <MultiConversationContent
              disabled={!historySwitch}
              onTypeChange={onTypeChange}
              onValueChange={onValueChange}
            />
          </Form>
        }
      </Panel>
    </Collapse>
  </>
};

export default MultiConversationContainer;
