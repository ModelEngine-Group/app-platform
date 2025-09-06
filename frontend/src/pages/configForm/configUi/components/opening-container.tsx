/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, useEffect } from 'react';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setConfigItem } from '@/store/appConfig/config';
import { useTranslation } from 'react-i18next';
import { Collapse, Input, Form } from 'antd';
import CloseImg from '@/assets/images/close_arrow.png';
import OpenImg from '@/assets/images/open_arrow.png';
const { Panel } = Collapse;
const { TextArea } = Input;

const OpeningContainer = (props) => {
  const { t } = useTranslation();
  const { graphOperator, config, updateData, readOnly } = props;
  const isOpengingChange = useRef(false);
  const [activePanelKey, setActivePanelKey] = useState(['']);
  const dispatch = useAppDispatch();
  const appConfig = useAppSelector((state) => state.appConfigStore.inputConfigData);
  const [form] = Form.useForm();

  // 更新opening
  const updateOpening = (value) => {
    if (config.from === 'graph') {
      graphOperator.update(config.defaultValue, value);
    } else {
      dispatch(setConfigItem({ key: config.name, value }));
    }
    updateData();
  };

  // 更新opening
  const handleChangeOpening = () => {
    isOpengingChange.current = true;
  };

  // 失去焦点更新
  const handleOpeningBlur = (e) => {
    if (isOpengingChange.current) {
      updateOpening(e.target.value);
    }
  };
  useEffect(() => {
    if (!config.from) {
      return;
    }
    const opening = (config.from === 'graph' ? graphOperator.getConfig(config.defaultValue) : config.defaultValue);
    form.setFieldsValue({ opening });
  }, [config, appConfig]);
  
  return <>
    <Collapse
      bordered={false}
      expandIcon={({ isActive }) => isActive ? <img src={CloseImg} alt="" /> : <img src={OpenImg} alt="" />}
      activeKey={activePanelKey}
      onChange={(keys) => setActivePanelKey(keys)}
    >
      <Panel header={config.description} forceRender key='opening' className="site-collapse-custom-panel">
        <Form form={form} layout='vertical'>
          <Form.Item
            label={t('openingRemarks')}
            name='opening'
          >
            <TextArea rows={3} showCount maxLength={300} onChange={handleChangeOpening} onBlur={handleOpeningBlur} disabled={readOnly} />
          </Form.Item>
        </Form>
      </Panel>
    </Collapse>
  </>
};

export default OpeningContainer;
