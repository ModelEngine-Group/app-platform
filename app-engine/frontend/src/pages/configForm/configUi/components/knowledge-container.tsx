/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setConfigItem } from '@/store/appConfig/config';
import ConnectKnowledge from '@/pages/addFlow/components/connect-knowledge';
import Knowledge from './knowledge';
import { Collapse } from 'antd';
import { Message } from '@/shared/utils/message';
import CloseImg from '@/assets/images/close_arrow.png';
import OpenImg from '@/assets/images/open_arrow.png';
import AddImg from '@/assets/images/add_btn.svg';
import SettingImg from '@/assets/svg/icon-search-args-config.svg';
const { Panel } = Collapse;

const KnowledgeContainer = (props) => {
  const { t } = useTranslation();
  const { graphOperator, config, updateData, validateList } = props;
  const [knowledge, setKnowledge] = useState([]);
  const [groupConfig, setGroupConfig] = useState({});
  const [groupId, setGroupId] = useState('default');
  const [activePanelKey, setActivePanelKey] = useState(['']);
  const knowledgeRef: any = useRef(null);
  const curKnowledge = useRef(null);
  const curGroupValue = useRef({});
  const connectKnowledgeRef = useRef<any>(null);
  const dispatch = useAppDispatch();
  const appConfig = useAppSelector((state) => state.appConfigStore.inputConfigData);

  // 更新knowledge
  const updateKnowledge = (value) => {
    value.forEach(item => delete item.notExist);
    if (config.from === 'graph') {
      graphOperator.update(config.defaultValue, value);
    } else {
      dispatch(setConfigItem({ key: config.name, value }));
    }
    updateData();
  };

  // 新增知识库
  const addKnowledgeBase = (event) => {
    event.stopPropagation();
    knowledgeRef.current.addKnowledge();
    setActivePanelKey(['knowledge']);
  };

  // 获取知识库ID
  const getKnowledgeId = (config) => {
    const groupConfig = [config[0], 'option'];
    const groupValue = graphOperator.getConfig(groupConfig);
    if (groupValue) {
      const { groupId } = groupValue;
      groupId && setGroupId(groupId);
      setGroupConfig(groupConfig);
      curGroupValue.current = groupValue;
    }
  }

  // 设置知识库
  const knowledgeModalOpen = (e) => {
    e.stopPropagation();
    connectKnowledgeRef.current.openModal();
  }

  // 更新groupId
  const updateGroupId = (val) => {
    setGroupId(val);
    if (curGroupValue.current.groupId !== val) {
      curGroupValue.current.groupId = val;
      graphOperator.update(groupConfig, curGroupValue.current);
      updateData();
    }
  }

  // 更新每一条是否存在
  const updateExistStatus = () => {
    if (!validateList?.length) {
      return;
    }
    curKnowledge.current = curKnowledge.current.map(item => {
      return {
        ...item,
        notExist: !!validateList.find(it => it.configName === 'knowledgeRepos' && it.id == item.id),
      }
    });
    setKnowledge(curKnowledge.current);
  };

  useEffect(() => {
    if (knowledge?.length) {
      setActivePanelKey(['knowledge']);
    }
  }, [knowledge]);

  useEffect(() => {
    if (!config.from) {
      return;
    }
    if (config.from === 'graph') {
      try {
        curKnowledge.current = graphOperator.getConfig(config.defaultValue);
        getKnowledgeId(config.defaultValue);
      } catch {
        Message({ type: 'warning', content: t('dataError') });
      }
    } else {
      curKnowledge.current = config.defaultValue;
    }
    setKnowledge(curKnowledge.current);
    updateExistStatus();
  }, [config, appConfig]);

  useEffect(() => {
    updateExistStatus();
  }, [validateList]);
  
  return <>
    <Collapse
      bordered={false}
      expandIcon={({ isActive }) => isActive ? <img src={CloseImg} alt="" /> : <img src={OpenImg} alt="" />}
      activeKey={activePanelKey}
      onChange={(keys) => setActivePanelKey(keys)}
    >
      <Panel header={<div className='panel-label'>
        <div className='panel-label-config'>
          <span>{config.description}</span>
          <img src={SettingImg} onClick={(e) => knowledgeModalOpen(e)} />
        </div>
        <img src={AddImg} style={{ width: 16, height: 16 }} alt="" onClick={addKnowledgeBase} />
      </div>} forceRender key='knowledge' className="site-collapse-custom-panel">
        <Knowledge 
          knowledgeRef={knowledgeRef} 
          knowledge={knowledge}
          groupId={groupId}
          updateData={updateKnowledge} 
        />
      </Panel>
    </Collapse>
    {/* 知识库 */}
    <ConnectKnowledge
      modelRef={connectKnowledgeRef}
      groupId={groupId}
      updateGroupId={updateGroupId}
    />
  </>
};

export default KnowledgeContainer;