/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Tooltip } from 'antd';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import ComponentFactory from './configUi/components/component-factory';
import './index.scoped.scss';
import './configUi/index.scoped.scss';
import { setConfigData } from '@/store/appConfig/config';
import { getConfigValue } from '@/shared/utils/common';
import { createGraphOperator } from '@fit-elsa/elsa-react';

const ConfigForm = (props) => {
  const {
    mashupClick,
    configData,
    handleConfigDataChange,
    inspirationChange,
    showElsa,
    graph,
    showConfig,
    onChangeShowConfig,
  } = props;
  const { t } = useTranslation();

  const [configStructure, setConfigStructure] = useState([]);
  const [getCategory, setGetCategory] = useState('');
  const graphOperator = useRef(null);
  const dispatch = useAppDispatch();
  const appConfig = useAppSelector((state) => state.appConfigStore.inputConfigData);
  const appInfo = useAppSelector((state) => state.appStore.appInfo);

  const handlePropDataChange = () => {
    const inputUpdate = Object.values(appConfig);
    handleConfigDataChange({ input: inputUpdate, graph: graphOperator.current?.getGraph() });
  }

  const eventConfigs = {
    enterWorkflow: {
      click: mashupClick,
    },
    inspiration: {
      change: inspirationChange,
    },
  }

  useEffect(() => {
    if (!configData) return;
    const configStructure = configData;
    const appCategory = appInfo.appCategory;
    setConfigStructure(configStructure);
    setGetCategory(appCategory);
    const data = getConfigValue(configStructure);
    dispatch(setConfigData(data));
  }, [configData]);

  useEffect(() => {
    if (!graph) return;
    graphOperator.current = createGraphOperator(JSON.stringify(graph));
  }, [graph]);

  return (
    <>
      <div className={['config-form-wrap', showConfig ? null : 'hidden'].join(' ')}>
        <div className={['config-form', showElsa ? 'config-form-elsa' : null].join(' ')}>
          <div className='config-wrap'>
            <ComponentFactory
              updateData={handlePropDataChange}
              configStructure={configStructure}
              graphOperator={graphOperator.current}
              eventConfigs={eventConfigs}
              categoryType={getCategory}
            ></ComponentFactory>
          </div>
        </div>
      </div>
      <div className="splitter-bar">
        <Tooltip placement='rightTop' title={t(showConfig ? 'collapseConfig' : 'expandConfig')}>
          <div
            className={['splitter-collapse', showConfig ? null : 'collapsed' ].join(' ')}
            onClick={onChangeShowConfig}
          ></div>
        </Tooltip>
      </div>
    </>
  );
};

export default ConfigForm;
