/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, useEffect } from 'react';
import { Collapse } from 'antd';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setConfigItem } from '@/store/appConfig/config';
import Skill from './skill';
import CloseImg from '@/assets/images/close_arrow.png';
import OpenImg from '@/assets/images/open_arrow.png';
import AddImg from '@/assets/images/add_btn.svg';
const { Panel } = Collapse;

const ToolsContainer = (props) => {
  const { graphOperator, config, updateData, validateList, readOnly } = props;
  const [pluginData, setPluginData] = useState([]);
  const [activePanelKey, setActivePanelKey] = useState(['']);
  const toolsRef = useRef<any>(null);
  const configFromMap = useRef<any>({});
  const dispatch = useAppDispatch();
  const appConfig = useAppSelector((state) => state.appConfigStore.inputConfigData);

  // 更新工具和工作流
  const updateTools = (value, key) => {
    if (config.from === 'graph') {
      graphOperator.update(config.defaultValue[0], value);
    } else {
      dispatch(setConfigItem({ key: config.name, value }));
    }
    updateData();
  };

  // 新增插件
  const addPlugin = (event) => {
    if (readOnly) {
      return;
    }
    event.stopPropagation();
    toolsRef.current.addPlugin();
    setActivePanelKey(['tools']);
  };

  useEffect(() => {
    if (pluginData?.length) {
      setActivePanelKey(['tools']);
    }
  }, [pluginData]);

  useEffect(() => {
    if (!config.from) {
      return;
    }
    if (config.from === 'graph') {
      setPluginData(graphOperator.getConfig(config.defaultValue[0]));
    } else {
      setPluginData(config.defaultValue);
    }
  }, [config, appConfig]);

  return <>
    <Collapse
      bordered={false}
      expandIcon={({ isActive }) => isActive ? <img src={CloseImg} alt="" /> : <img src={OpenImg} alt="" />}
      activeKey={activePanelKey}
      onChange={(keys) => setActivePanelKey(keys)}
    >
      <Panel header={<div className='panel-label'>
        <span>{config.description}</span>
        <img src={AddImg} style={{ width: 16, height: 16 }} alt="" onClick={addPlugin} className={!readOnly ? '' : 'version-preview'} />
      </div>} forceRender key='tools' className="site-collapse-custom-panel">
        <Skill toolsRef={toolsRef} pluginData={pluginData} updateData={updateTools} validateList={validateList} readOnly={readOnly} />
      </Panel>
    </Collapse>
  </>
};

export default ToolsContainer;
