/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { LeftArrowIcon } from '@/assets/icon';
import { useParams } from 'react-router-dom';
import { getAppInfo } from '@/shared/http/aipp';
import { JadeFlow } from '@fit-elsa/agent-flow';
import { configMap } from '../addFlow/config';
import i18n from '../../locale/i18n';
import './index.scss';

const FlowDetail = () => {
  const { appId, tenantId } = useParams();
  const [appInfo, setAppInfo] = useState(false);
  const { CONFIGS } = configMap[process.env.NODE_ENV];

  useEffect(() => {
    getAippDetails();
    return () => {
      window.agent = null;
    }
  }, [])
  // 获取aipp详情
  async function getAippDetails() {
    const res = await getAppInfo(tenantId, appId);
    if (res.code === 0) {
      setAppInfo(res.data);
      setElsaData(res.data.flowGraph?.appearance);
    }
  }
  // 编辑工作流
  function setElsaData(editData) {
    const stageDom = document.getElementById('stageDetail');
    let data = JSON.parse(JSON.stringify(editData));
    let configIndex = CONFIGS.findIndex(item => item.node === 'llmNodeState');
    CONFIGS[configIndex].params.tenantId = tenantId;
    CONFIGS[configIndex].params.appId = appId;
    JadeFlow.edit({
      div: stageDom,
      tenant: tenantId,
      flowConfigData: data,
      configs: CONFIGS,
      i18n,
      importStatements: [],
      flowType: appInfo.type
    }).then(agent => {
      window.agent ? null : window.agent = agent;
    })
  }
  function handleBackClick() {
    window.history.back();
  }
  return <>{(
    <div className='graph-detail'>
      <div className='header'>
        <div className='header-left'>
          <LeftArrowIcon className='icon-back' onClick={handleBackClick} />
          <span className='header-text' title={appInfo?.name}>{appInfo.name}</span>
        </div>
        <div className='header-right'>
          <span className='header-text'>{i18n.t('creator')}：{appInfo.createBy}</span>
          <span className='header-text'>{i18n.t('releaseTime')}：{appInfo.updateAt}</span>
        </div>
      </div>
      <div id='stageDetail'></div>
    </div>
  )}</>
};


export default FlowDetail;
