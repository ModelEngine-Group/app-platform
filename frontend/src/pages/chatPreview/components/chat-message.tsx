/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useImperativeHandle, useRef } from 'react';
import SendBox from './send-box/send-box';
import ReceiveBox from './receive-box/receive-box';
import ChatDetail from './chat-details';
import { ChatContext } from '../../aippIndex/context';
import { queryFeedback } from '@/shared/http/chat';
import { guestModeQueryFeedback } from '@/shared/http/guest';
import { deepClone, scrollBottom } from '../utils/chat-process';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setChatList } from '@/store/chatStore/chatStore';
import '../styles/chat-message-style.scss';

const ChatMessage = (props) => {
  const dispatch = useAppDispatch();
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const loginStatus = useAppSelector((state) => state.chatCommonStore.loginStatus);
  const isGuest = useAppSelector((state) => state.appStore.isGuest);
  const [list, setList] = useState([]);
  const [showMask, setShowMask] = useState(false);
  const chatBoxRef = useRef<any>(null);
  const {
    showCheck,
    setCheckedList,
    setEditorShow,
    feedRef,
    chatRunningStop,
    conditionConfirm,
    chatStreaming,
    questionClarConfirm,
    refreshInspiration
  } = props;
  const initFeedbackStatus = async (id) => {
    let arr = JSON.parse(JSON.stringify(chatList));
    for (let i = 0; i < arr?.length; i++) {
      let item = arr[i]
      if (item.type === 'receive' && item?.instanceId && (id === 'all' || item?.instanceId === id)) {
        try {
          const res = isGuest
            ? await guestModeQueryFeedback(item.instanceId)
            : await queryFeedback(item.instanceId);

          item.feedbackStatus = res?.usrFeedback ?? -1;
        } catch (error) {
          item.feedbackStatus = -1;
        }
      }
    }
    dispatch(setChatList(arr));
  }
  useImperativeHandle(feedRef, () => {
    return {
      'initFeedbackStatus': initFeedbackStatus,
      'setCheckStatus': setCheckStatus
    }
  })
  useEffect(() => {
    setList(deepClone(chatList));
  }, [chatList]);

  // 重置选中状态
  const setCheckStatus = () => {
    list.forEach(item => item.checked = false);
  }
  // 删除问答
  function setShareClass(type) {
    setCheckStatus();
    setEditorShow(true, type);
  }
  // 选中回调
  function checkCallBack() {
    let checkList = list?.filter(item => item.checked);
    setCheckedList(checkList);
  }
  // 表单拒绝回调
  async function handleRejectClar(params) {
    chatRunningStop(params);
  }
  // 聊天框添加灵感大全回调
  const addInspirationCb = () => {
    refreshInspiration();
  }

  useEffect(() => {
    if (!loginStatus) {
      setShowMask(true);
    }
  }, [loginStatus]);

  return (
    <div className={['chat-message-container', showCheck ? 'group-active' : null].join(' ')} id='chat-list-dom'>
      { !list?.length && <ChatDetail showMask={showMask} />}
      <ChatContext.Provider
        value={{
          checkCallBack,
          setShareClass,
          showCheck,
          handleRejectClar,
          conditionConfirm,
          chatStreaming,
          questionClarConfirm,
          addInspirationCb,
          tenantId
        }}>
        <div ref={chatBoxRef} className='message-box'>
          {
            list?.map((item, index) => {
              return (
                item.type === 'send' ?
                  <SendBox chatItem={item} key={index} /> :
                  <ReceiveBox
                    chatItem={item}
                    key={index}
                    refreshFeedbackStatus={initFeedbackStatus}
                    mode={item.mode}
                  />
              )
            })
          }
        </div>
      </ChatContext.Provider>
    </div>
  )
};

export default ChatMessage;
