/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useContext, useRef, useState, useEffect } from 'react';
import { PlusCircleOutlined } from '@ant-design/icons';
import { toClipboard } from '@/shared/utils/common';
import { CopyIcon, DeleteIcon } from '@/assets/icon';
import { ChatContext } from '../../../aippIndex/context';
import { useTranslation } from 'react-i18next';
import { useAppSelector } from '@/store/hook';
import { Message } from '@/shared/utils/message';
import { findConfigValue } from '@/shared/utils/common';
import Add from '../inspiration/add-inspiration';
import './styles/send-btn.scss'


const SendBtn = (props) => {
  const { t } = useTranslation();
  const { content, sendType, isRecieve, formConfig = {} } = props;
  const [showInspiration, setShowInspiration] = useState(false);
  const { setShareClass, addInspirationCb } = useContext(ChatContext);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const copyType = ['msg', 'text'];
  const detailPage = location.href.indexOf('app-detail') !== -1;
  const inspirationRef = useRef(null);
  
  // 复制
  function handleCopyQuestion() {
    content && toClipboard(content);
  }
  // 分享删除
  const btnClick = (type) => {
    if (isChatRunning()) { return; }
    setShareClass(type)
  }
  // 添加灵感大全
  const addInspiration = () => {
    if (isChatRunning()) { return; }
    inspirationRef.current?.initAdd({ str: content }, 'add');
  }
  // 添加回调
  const refreshData = () => {
    addInspirationCb();
  }
  // 检验是否正在对话中
  const isChatRunning = () => {
    let hasRunning = chatList.filter(item => item.status === 'RUNNING')[0];
    if (chatRunning || hasRunning) {
      Message({ type: 'warning', content: t('tryLater') });
      return true;
    }
    return false;
  }

  useEffect(() => {
    const inspirationItem = findConfigValue(appInfo, 'inspiration');
    setShowInspiration(inspirationItem?.showInspiration || false);
  }, [appInfo]);

  return <>{(
    <div className='message-tip-box-send'>
      <div className='inner'>
        { copyType.includes(sendType) &&
          <div title={t('copy')} onClick={handleCopyQuestion}>
            <CopyIcon className='hover-blue-icon' />
          </div>
        }
        {formConfig.formName !== 'questionClar' && 
          <div title={t('delete')} onClick={() => btnClick('delete')}>
            <DeleteIcon className='hover-blue-icon' />
          </div>
        }
        {(!isRecieve && !detailPage && showInspiration) &&
          <div title='添加灵感' onClick={addInspiration}>
            <PlusCircleOutlined  className='hover-blue-icon' style={{ fontSize: '18px', color: '#4D4D4D' }} />
          </div>
        }
      </div>
      { !detailPage && <Add addRef={inspirationRef} refreshData={refreshData} /> }
    </div>
  )}</>
};

export default SendBtn;
