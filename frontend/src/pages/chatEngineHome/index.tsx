/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect } from 'react';
import CommonChat from '../chatPreview/chatComminPage';
import InfoModal from './components/InfoModal';
import { getAppInfo } from '@/shared/http/aipp';
import { useAppDispatch } from '@/store/hook';
import { setHistorySwitch, setIsDebug } from '@/store/common/common';
import { setAppId, setAippId, setAppInfo } from '@/store/appInfo/appInfo';
import { HOME_APP_ID, TENANT_ID } from '../chatPreview/components/send-editor/common/config';
import { findConfigValue } from '@/shared/utils/common';
import './index.scss'

const ChatRunning = () => {
  const dispatch = useAppDispatch();
  

  const getAppDetails= async ()=>{
    const res = await getAppInfo(TENANT_ID, HOME_APP_ID);
    if (res.code === 0) {
      const appInfo = res.data;
      appInfo.notShowHistory = true;
      dispatch(setAppInfo(appInfo));
      dispatch(setAippId(appInfo.aipp_id));
      dispatch(setAppId(appInfo.id));
      const memoryItem = findConfigValue(appInfo, 'memory');
      dispatch(setHistorySwitch(memoryItem?.type !==  'NotUseMemory'));
    }
  }

  useEffect(()=>{
    getAppDetails();
    dispatch(setIsDebug(false));
    return () => {
      dispatch(setAppInfo({}));
      dispatch(setAippId(''));
      dispatch(setAppId(''));
    }
  },[]);
  return (
    <div className='chat-engine-container'>
      <CommonChat />
      <InfoModal />
    </div>
);
  }


export default ChatRunning;
