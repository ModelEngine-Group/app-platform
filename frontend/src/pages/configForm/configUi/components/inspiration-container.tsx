/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, useEffect } from 'react';
import { Collapse, Switch } from 'antd';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setConfigItem } from '@/store/appConfig/config';
import Inspiration from './inspiration';
import CloseImg from '@/assets/images/close_arrow.png';
import OpenImg from '@/assets/images/open_arrow.png';
import AddImg from '@/assets/images/add_btn.svg';
import LineImg from '@/assets/images/line.svg';
const { Panel } = Collapse;

const InspirationContainer = (props) => {
  const { graphOperator, config, updateData, eventConfigs, readOnly } = props;
  const inspirationChange = eventConfigs?.inspiration?.change;
  const [activePanelKey, setActivePanelKey] = useState(['']);
  const [inspirationValues, setInspiration] = useState(null);
  const [showInspiration, setShowInspiration] = useState(false);
  const inspirationRef: any = useRef(null);
  const dispatch = useAppDispatch();
  const appConfig = useAppSelector((state) => state.appConfigStore.inputConfigData);

  // 更新inspiration
  const updateInspiration = (value) => {
    if (config.from === 'graph') {
      graphOperator.update(config.defaultValue, value);
    } else {
      dispatch(setConfigItem({ key: config.name, value }));
    }
    updateData();
    inspirationChange();
  };

  // 灵感大全开关切换
  const inspirationSwitchChange = (checked, event) => {
    event.stopPropagation();
    setShowInspiration(checked);
    const newInspirationValues = { ...inspirationValues, showInspiration: checked };
    updateInspiration(newInspirationValues);
  };

  // 新增灵感大全
  const addInspiration = (event) => {
    event.stopPropagation();
    if (showInspiration) {
      inspirationRef.current.onAddClick();
      setActivePanelKey(['inspiration']);
    }
  };

  useEffect(() => {
    if (!inspirationValues) {
      return;
    }
    if (inspirationValues?.inspirations?.length) {
      setActivePanelKey(['inspiration']);
    }
    setShowInspiration(inspirationValues.showInspiration);
  }, [inspirationValues]);

  useEffect(() => {
    if (!config.from) {
      return;
    }
    if (config.from === 'graph') {
      setInspiration(graphOperator.getConfig(config.defaultValue));
    } else {
      setInspiration(config.defaultValue);
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
        <div className='panel-add'>
          <img src={AddImg} style={{ width: 16, height: 16 }} alt="" onClick={addInspiration} className={[showInspiration ? '' : 'not-allowed', readOnly ? 'version-preview' : ''].join(' ')} />
          <img src={LineImg} alt="" />
          <Switch onChange={(checked, event) => inspirationSwitchChange(checked, event)} checked={showInspiration} disabled={readOnly}/>
        </div>
      </div>} forceRender key='inspiration' className="site-collapse-custom-panel">
        <Inspiration inspirationRef={inspirationRef} inspirationValues={inspirationValues} updateData={updateInspiration} readOnly={readOnly} />
      </Panel>
    </Collapse>
  </>
};

export default InspirationContainer;
