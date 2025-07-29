/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, useEffect } from 'react';
import { Collapse, Switch } from 'antd';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setConfigItem } from '@/store/appConfig/config';
import Recommend from './recommend';
import CloseImg from '@/assets/images/close_arrow.png';
import OpenImg from '@/assets/images/open_arrow.png';
import AddImg from '@/assets/images/add_btn.svg';
import LineImg from '@/assets/images/line.svg';
const { Panel } = Collapse;

const RecommendContainer = (props) => {
  const { graphOperator, config, updateData, readOnly } = props;
  const recommendRef: any = useRef(null);
  const [activePanelKey, setActivePanelKey] = useState(['']);
  const [recommendValues, setRecommendValues] = useState({});
  const [showRecommend, setShowRecommend] = useState(false);
  const [recommendNum, setRecommendNum] = useState(0);
  const dispatch = useAppDispatch();
  const appConfig = useAppSelector((state) => state.appConfigStore.inputConfigData);
  const updateRecommend = (value) => {
    if (config.from === 'graph') {
      graphOperator.update(config.defaultValue, value);
    } else {
      dispatch(setConfigItem({ key: config.name, value }));
    }
    updateData();
  };

  // 新增猜你想问
  const addRecommend = (event) => {
    event.stopPropagation();
    if (recommendNum < 3 && showRecommend) {
      recommendRef.current.addRecommend();
      setActivePanelKey(['recommend']);
    }
  };

  // 更新猜你想问个数
  const updateRecommendNum = (count) => {
    setRecommendNum(count);
  };

  // 更新猜你想问开关
  const showRecommendChange = (checked, event) => {
    event.stopPropagation();
    setShowRecommend(checked);
    updateRecommend({ ...recommendValues, showRecommend: checked });
  };

  useEffect(() => {
    setShowRecommend(recommendValues?.showRecommend);
    if (recommendValues?.list?.length) {
      setActivePanelKey(['recommend']);
    }
  }, [recommendValues]);

  useEffect(() => {
    if (!config.from) {
      return;
    }
    if (config.from === 'graph') {
      setRecommendValues(graphOperator.getConfig(config.defaultValue));
    } else {
      setRecommendValues(config.defaultValue);
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
          <img src={AddImg} style={{ width: 16, height: 16 }} alt="" onClick={addRecommend} className={[recommendNum < 3 && showRecommend ? '' : 'not-allowed', readOnly ? 'version-preview' : ''].join(' ')} />
          <img src={LineImg} alt="" />
          <Switch onChange={(checked, event) => showRecommendChange(checked, event)} checked={showRecommend} disabled={readOnly} />
        </div>
      </div>} forceRender key='recommend' className="site-collapse-custom-panel">
        <Recommend recommendRef={recommendRef} updateData={updateRecommend} updateRecommendNum={updateRecommendNum} recommendValues={recommendValues} readOnly={readOnly}/>
      </Panel>
    </Collapse>
  </>
};

export default RecommendContainer;
