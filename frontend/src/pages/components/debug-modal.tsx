/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setChoseNodeId } from "@/store/appInfo/appInfo";
import { useTranslation } from 'react-i18next';
import { NodeType } from './common/common';
import NodeIcon from '@/components/icons/node-icon';
import closeImg from '@/assets/images/close_btn.svg';
import warningImg from '@/assets/images/debug_warning_icon.svg';
import './styles/debug-modal.scoped.scss';

/**
 * 可用性检查列表弹框组件
 *
 * @param isWorkFlow 当前应用是否为工作流类型.
 * @param showElsa 当前是否在elsa编排页面.
 * @param mashupClick 切换elsa和配置页面的方法.
 * @param closeDebug 关闭弹框的方法.
 * @return {JSX.Element}
 * @constructor
 */

const DebugModal = ({ isWorkFlow, showElsa, mashupClick, closeDebug }) => {
  const { t } = useTranslation();
  const choseNodeId = useAppSelector((state) => state.appStore.choseNodeId);
  const appValidateInfo = useAppSelector((state) => state.appStore.validateInfo);
  const dispatch = useAppDispatch();
  const [checkId, setCheckId] = useState();


  // 选中某一条
  const choseItem = (item) => {
    if (isWorkFlow.current) {
      if (item.nodeId) {
        dispatch(setChoseNodeId(item.nodeId));
      }
      if (!showElsa) {
        mashupClick();
      }
    } else {
      setCheckId(item.configCheckId);
    }
    const updateChoseNode = new CustomEvent('updateChoseNode', {
      detail: {
        choseItem: item
      }
    });
    window.dispatchEvent(updateChoseNode);
  };

  const getPrompt = (item, isWorkFlow) => {
    if (isWorkFlow) {
      switch (item.type) {
        case NodeType.LLM:
          if (item.serviceName) {
            return `${item.serviceName}${t('doesNotExist')}`;
          } else {
            return `${item.name}${t('tool')}${t('doesNotExist')}`;
          }
        case NodeType.KNOWLEDGE_RETRIEVAL:
        case NodeType.RETRIEVAL:
        case NodeType.END:
        case NodeType.MANUAL_CHECK:
          return t('pleaseDeleteAndReconfigure');
        case NodeType.HUGGING_FACE:
        case NodeType.TOOL_INVOKE:
          return t('pluginReconfigurePrompt');
        default:
          break;
      }
    } else {
      switch (item.type) {
        case NodeType.LLM:
          if (item.serviceName) {
            return `${item.serviceName}${t('doesNotExist')}`;
          } else {
            return `${item.name}${t('tool')}${t('doesNotExist')}`;
          }
        case NodeType.KNOWLEDGE_RETRIEVAL:
          return `${item.name}${t('knowledgeBase')}${t('pleaseDeleteAndReconfigure')}`;
        case NodeType.HUGGING_FACE:
        case NodeType.TOOL_INVOKE:
          return `${item.name}${t('tool')}${t('pleaseDeleteAndReconfigure')}`;
        default:
          break;
      }
    }
  };

  return <>
    <div className='debug-container'>
      <div className='debug-title'>{t('configurationErrorList')}</div>
      <div className='tips'>{t('debugTips')}</div>
      <img src={closeImg} alt="" className='close-btn' onClick={closeDebug} />
      <div className='debug-content'>
        {
          appValidateInfo && appValidateInfo.map((item, index) =>
            isWorkFlow.current ? <div className={`debug-item ${choseNodeId === item.nodeId ? 'chose' : ''}`} key={item.nodeId} onClick={() => choseItem(item)}>
              <div className='title'>
                <NodeIcon type={item.type}></NodeIcon>
                <div className='node-name'>{item.name}</div>
              </div>
              {
                item.configChecks && item?.configChecks?.map(it =>
                  <div className='workflow-item' key={it.configCheckId}>
                    <img src={warningImg} alt="" style={{ marginRight: 8 }} />
                    <div>{getPrompt({ ...it, type: item.type }, true)}</div>
                  </div>
                )
              }
            </div> : <div key={item.configCheckId} className={`basic-item ${checkId === item.configCheckId ? 'chose' : ''}`} onClick={() => choseItem(item)}>
              <img src={warningImg} alt="" style={{ marginRight: 8 }} />
              <div>{getPrompt(item, false)}</div>
            </div>
          )
        }
      </div>
    </div>
  </>
};

export default DebugModal;