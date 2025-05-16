/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useContext, useRef } from 'react';
import { useLocation } from 'react-router';
import { useParams } from 'react-router-dom';
import { Spin } from 'antd';
import { LeftArrowIcon } from '@/assets/icon';
import { Message } from '@/shared/utils/message';
import {
  isJsonString,
  updateChatId,
  isInputEmpty,
  findConfigValue,
  getConfiguration,
  setSpaClassName
} from '@/shared/utils/common';
import { isChatRunning } from '@/shared/utils/chat';
import { initChat } from './common/config';
import { AippContext } from '../aippIndex/context';
import { stopInstance, getChatRecentLog, clearChat} from '@/shared/http/aipp';
import { sseChat, saveContent } from '@/shared/http/sse';
import {
  historyChatProcess,
  inspirationProcess,
  messageProcess,
  messageProcessNormal,
  sendProcess,
  deepClone,
  scrollBottom
} from './utils/chat-process';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setUseMemory } from '@/store/common/common';
import {
  setAtChatId,
  setChatId,
  setChatList,
  setChatRunning,
  setFormReceived,
  setReference,
  setReferenceList,
} from '@/store/chatStore/chatStore';
import { v4 as uuidv4 } from 'uuid';
import { storage } from '@/shared/storage';
import { EventSourceParserStream } from '@/shared/eventsource-parser/stream';
import { setAppId, setAippId, setAppInfo } from '@/store/appInfo/appInfo';
import { useTranslation } from 'react-i18next';
import { pick, cloneDeep } from 'lodash';
import ChatMessage from './components/chat-message';
import SendEditor from './components/send-editor/send-editor';
import CheckGroup from './components/check-group';
import Inspiration from './components/inspiration';
import PreviewPicture from './components/receive-box/preview-picture';
import './styles/chat-preview.scss';

/**
 * 应用聊天对话页面
 *
 * @return {JSX.Element}
 * @param previewBack  应用市场点击返回回调方法
 * @constructor
 */

const ChatPreview = (props) => {
  const { t } = useTranslation();
  const { previewBack } = props;
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const chatId = useAppSelector((state) => state.chatCommonStore.chatId);
  const appId = useAppSelector((state) => state.appStore.appId);
  const aippId = useAppSelector((state) => state.appStore.aippId);
  const inspirationOpen = useAppSelector((state) => state.chatCommonStore.inspirationOpen);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const referenceList = useAppSelector((state) => state.chatCommonStore.referenceList);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const atChatId = useAppSelector((state) => state.chatCommonStore.atChatId);
  const atAppId = useAppSelector((state) => state.appStore.atAppId);
  const atAppInfo = useAppSelector((state) => state.appStore.atAppInfo);
  const formReceived = useAppSelector((state) => state.chatCommonStore.formReceived);
  const showMulti = useAppSelector((state) => state.commonStore.historySwitch);
  const useMemory = useAppSelector((state) => state.commonStore.useMemory);
  const isDebug = useAppSelector((state) => state.commonStore.isDebug);
  const { showElsa } = useContext(AippContext);
  const [checkedList, setCheckedList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showStop, setShowStop] = useState(false);
  const [stopLoading, setStopLoading] = useState(false);
  const [groupType, setGroupType] = useState('share');
  const [showCheck, setShowCheck] = useState(false);
  const [showInspiration, setShowInspiration] = useState(false);
  const [userContext, setUserContext] = useState({});
  const [chatFileList, setChatFileList] = useState([]);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewProps, setPreviewProps] = useState({ pictureList: [], curPicturePath: '' });
  const [configAppInfo, setConfigAppInfo] = useState({});
  const location = useLocation();
  let editorRef = useRef<any>(null);
  let runningInstanceId = useRef<any>('');
  let timeProcess = useRef<any>();
  let currentInfo = useRef<any>();
  let feedRef = useRef<any>();
  let testRef = useRef<any>(false);
  let historyRender = useRef<any>(false);
  let chatRender = useRef<any>(false);
  const listRef = useRef<any>([]);
  const inspirationRef = useRef<any>(null);
  const isAutoSend = useRef<boolean>(false);
  const detailPage = location.pathname.indexOf('app-detail') !== -1;
  const storageId = detailPage ? aippId : appId;
  const chatStatus = ['ARCHIVED', 'ERROR', 'TERMINATED'];
  const messageType = ['MSG', 'ERROR', 'META_MSG'];
  const readOnly = useAppSelector((state) => state.commonStore.isReadOnly);

  useEffect(() => {
    currentInfo.current = appInfo;
    window.addEventListener("previewPicture", handlePreview);
    return () => {
      closeConnected();
      dispatch(setAppInfo({}));
      dispatch(setAppId(null));
      dispatch(setChatId(undefined));
      dispatch(setAippId(''));
      dispatch(setChatList([]));
      dispatch(setReference(false));
      dispatch(setReferenceList({}));
      window.removeEventListener("previewPicture", handlePreview);
    };
  }, []);
  useEffect(() => {
    const inspirationItem = findConfigValue(appInfo, 'inspiration');
    setShowInspiration(inspirationItem?.showInspiration || false);
  }, [appInfo]);
  useEffect(() => {
    testRef.current = formReceived;
  }, [formReceived]);

  // 灵感大全设置下拉列表
  function setEditorSelect(data, prompItem, auto = false) {
    let { prompt, promptArr } = inspirationProcess(tenantId, data, prompItem, appInfo);
    if (auto) {
      let promtpStr = prompt.replace(/(<([^>]+)>)/ig, '')
        .replace(/&quot;/g, '"')
        .replace(/&apos;/g, "'")
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>');
      onSend(promtpStr);
      return
    }
    editorRef.current?.setFilterHtml(prompt, promptArr, true);
  }
  function setEditorHtml(content) {
    editorRef.current?.setFilterHtml(content, [], false);
  }
  // 获取历史会话
  async function initChatHistory(chatId) {
    listRef.current = [];
    setLoading(true);
    try {
      const res:any = await getChatRecentLog(tenantId, chatId, appId);
      if (res.data && res.data.length) {
        let chatArr = historyChatProcess(res);
        listRef.current = deepClone(chatArr);
        await dispatch(setChatList(chatArr));
        feedRef.current.initFeedbackStatus('all');
        scrollToBottom();
      }
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    if (appInfo.id) {
      dispatch(setChatRunning(false));
      dispatch(setChatList([]));
      if (appInfo.name && !appInfo.notShowHistory) {
        historyInit();
      }
    }
  }, [appInfo.id]);
  // 获取历史记录处理
  const historyInit = () => {
    historyRender.current = true;
    let chatId = storage.getChatId(storageId);
    dispatch(setChatId(chatId));
    chatId && initChatHistory(chatId);
  }
  useEffect(() => {
    if (showMulti) {
      dispatch(setUseMemory(true));
    } else {
      dispatch(setUseMemory(false));
    }
  }, [showMulti]);
  // 发送消息
  const onSend = (value) => {
    if (!checkMutipleInput() || !validateSend() || !checkFileSuccess()) {
      return;
    }
    if (isChatRunning()) {
      Message({ type: 'warning', content: t('tryLater') });
      return
    }
    // 文件列表不从参数传过来是因为灵感大全发送消息也需要判断文件
    const sentItem = sendProcess(chatRunning, value, chatFileList);
    if (sentItem) {
      let arr = [...chatList, sentItem];
      listRef.current = arr;
      sendMessageRequest(value, chatFileList);
    }
  };

  // 更新上传文件
  const handleUpdateFileList = (fileList, isAuto) => {
    setChatFileList(fileList);
    isAutoSend.current = isAuto;
    if (isAutoSend.current) {
      if (fileList.find(item => item.uploadStatus === 'failed')) {
        Message({ type: 'warning', content: t('uploadFailedTip') });
      } else if (fileList.every(item => item.uploadStatus === 'success')) {
        // 发送消息
        const sentItem = sendProcess(chatRunning, '', fileList);
        if (sentItem) {
          let arr = [...chatList, sentItem];
          listRef.current = arr;
          sendMessageRequest('', fileList, true);
        }
        editorRef.current?.clearFileList();
      }
    }
  };

  // 校验文件是否都上传成功
  const checkFileSuccess = () => {
    if (chatFileList.length) {
      if (chatFileList.find(item => item.uploadStatus === 'failed')) {
        Message({ type: 'warning', content: t('uploadFailedTip') });
        return false;
      } else if (chatFileList.find(item => item.uploadStatus !== 'success')) {
        Message({ type: 'warning', content: t('waitFileSuccessTip') });
        return false;
      }
    }
    return true;
  };

  // 发送消息前验证
  const validateSend = () => {
    let hasRunning = chatList.filter(item => item.status === 'RUNNING')[0];
    if (hasRunning) {
      Message({ type: 'warning', content: t('tryLater') })
      return false;
    }
    return true;
  }
  // 发送消息
  const sendMessageRequest = async (value, fileList) => {
    const reciveInitObj = deepClone(initChat);
    if (atAppInfo) {
      reciveInitObj.appName = atAppInfo.name;
      reciveInitObj.appIcon = atAppInfo.attributes?.icon;
      reciveInitObj.isAt = true;
    }
    let arr = [...listRef.current, reciveInitObj];
    listRef.current = deepClone(arr);
    dispatch(setChatList(deepClone(arr)));
    dispatch(setChatRunning(true));
    chatRender.current = true;
    chatMissionStart(value, fileList);
    scrollBottom();
  };
  // 启动任务
  const chatMissionStart = async (value, fileList) => {
    let chatParams: any = {
      'app_id': appId,
      'question': value,
      'context': {
        'use_memory': useMemory,
        'user_context': cloneDeep(userContext)
      }
    };
    if (chatId) {
      chatParams['chat_id'] = chatId;
    }
    if (atAppId) {
      chatParams.context.at_chat_id = atChatId;
      chatParams.context.at_app_id = atAppInfo.id;
    }
    if (fileList.length) {
      chatParams.context.user_context['$[FileDescription]$'] = fileList.map(item => {
        return pick(item, ['file_name', 'file_url', 'file_type']);
      });
    }
    queryInstance(chatParams);
  };

  // 校验多输入是否必填
  const checkMutipleInput = () => {
    const configurationList = getConfiguration(configAppInfo);
    const requiredList = [];
    configurationList.forEach(item => {
      if (item.isRequired && item.type !== 'Boolean' && isInputEmpty(userContext?.[item.name])) {
        requiredList.push(item.displayName);
      }
    })
    if (requiredList.length) {
      const requiredStr = requiredList.map(item => item + t('requiredValidate')).join('');
      Message({
        type: 'error', content: <div className='required-tip'>
          {requiredStr + t('pleaseFillUserInput')}
        </div>
      });
      const mutipleInputRequied = new CustomEvent('mutipleInputRequied', {
        detail: {
          openInput: true
        }
      });
      window.dispatchEvent(mutipleInputRequied);
      return false;
    }
    return true;
  };

  // 开始对话
  const queryInstance = async (params, type = undefined, instanceId = '') => {
    dispatch(setReferenceList({}));
    dispatch(setReference(false));
    runningInstanceId.current = null;
    let response: any;
    if (type === 'clar') {
      response = await saveContent(tenantId, instanceId, params, isDebug);
    } else {
      response = await sseChat(tenantId, params, isDebug, isAutoSend.current);
    }
    if (response.status !== 200) {
      listRef.current[listRef.current.length - 2].logId = `${uuidv4()}-empty`;
      listRef.current[listRef.current.length - 1].logId = `${uuidv4()}-empty`;
      chatRender.current && onStop(response.msg || response.suppressed || t('conversationFailed'));
      return;
    };
    chatStreaming(response);
  }
  // sse流式输出
  const chatStreaming = async (response) => {
    timeProcess.current && clearTimeout(timeProcess.current);
    timeProcess.current = setTimeout(() => {
      chatRender.current = false;
      chatRunning && onStop(t('sseFailed'));
    }, 300000);
    const reader = response?.body?.pipeThrough(new TextDecoderStream()).pipeThrough(new EventSourceParserStream()).getReader();
    while (chatRender.current) {
      const sseResData = await reader?.read();
      const { done, value } = sseResData;
      clearTimeout(timeProcess.current);
      timeProcess.current = null;
      if (!done) {
        try {
          let msgStr = value.data;
          const receiveData = JSON.parse(msgStr);
          if (receiveData.code) {
            onStop(value.msg || t('conversationFailed'));
          } else {
            sseReceiveProcess(receiveData);
          }
        } catch (e) {
          break;
        }
      } else {
        timeProcess.current && clearTimeout(timeProcess.current);
        clearInterval(chatRender.current);
        if (chatFileList.length) {
          setChatFileList([]);
        };
        break;
      };
    };
  }
  // sse接收消息回调
  const sseReceiveProcess = (messageData) => {
    timeProcess.current && clearTimeout(timeProcess.current);
    try {
      // 初始化设置instanceId
      if (messageData.status === 'READY') {
        runningInstanceId.current = messageData.instance_id;
        setShowStop(true);
        return;
      }
      // 智能表单
      if (messageData.formAppearance?.length) {
        let obj = messageProcess(runningInstanceId.current, messageData, atAppInfo);
        chatForm(obj);
        saveLocalChatId(messageData);
        return;
      }
      // 过程日志打印
      if (messageData.extensions && messageData.extensions.enableStageDesc) {
        let receiveItem:any = {
          recieveType: 'msg',
          content: '',
          step: true
        };
        let msg = messageData.extensions.stageDesc || '';
        receiveItem.content = msg;
        receiveItem.logId = messageData.log_id;
        chatStrInit(msg, receiveItem, messageData.status, messageData.log_id, { isStepLog: true });
        return
      }
      // 普通日志
      messageData.answer?.forEach((log) => {
        if (log.type === 'FORM') {
          let obj = messageProcess(runningInstanceId.current, { ...log.content, log_id: messageData.log_id, status: messageData.status }, atAppInfo);
          chatForm(obj);
          saveLocalChatId(messageData);
        }
        if (log.type === 'QUESTION' || log.type === 'QUESTION_WITH_FILE' || log.type === 'FILE') {
          listRef.current[listRef.current.length - 2]['logId'] = Number(messageData.log_id);
          dispatch(setChatList(deepClone(listRef.current)));
        }
        if (log.type === 'KNOWLEDGE') {
          let knowledgeReference = typeof(log.content) === 'string' ? JSON.parse(log.content) : log.content;
          dispatch(setReference(true));
          dispatch(setReferenceList({ ...referenceList, ...knowledgeReference }));
        }
        if (messageType.includes(log.type)) {
          let { msg, recieveChatItem } = messageProcessNormal(log, atAppInfo, messageData);
          if (log.msgId) {
            chatSplicing(log, msg, recieveChatItem, messageData.status);
          } else {
            chatStrInit(msg, recieveChatItem, messageData.status, messageData.log_id, messageData.extensions);
          }
        }
      });
      if (chatStatus.includes(messageData.status)) {
        saveLocalChatId(messageData);
        dispatch(setChatRunning(false));
        setShowStop(false);
        dispatch(setReferenceList({}));
      }
    } catch (err) {
      console.error(err);
      onStop(t('dataParseError'));
    }
  };
  // sse回调保存chatId
  const saveLocalChatId = (data) => {
    let { chat_id, at_chat_id } = data;
    if (chat_id) {
      updateChatId(chat_id, storageId);
      dispatch(setChatId(chat_id));
    }
    if (at_chat_id) {
      dispatch(setAtChatId(at_chat_id));
    }
  };
  // 聊天表单
  const chatForm = (chatObj) => {
    let idx = listRef.current.length - 1;
    let lastItem = listRef.current[listRef.current.length - 1];
    if (!lastItem.loading && !lastItem.step) {
      listRef.current[idx].finished = true;
      idx = listRef.current.length;
    }
    listRef.current.splice(idx, 1, chatObj);
    dispatch(setChatList(deepClone(listRef.current)));
    dispatch(setChatRunning(false));
    setShowStop(false);
    scrollToBottom();
  };
  // 流式输出
  function chatStrInit(msg, initObj, status, logId, extensions:any = {}) {
    let idx = 0;
    if (isJsonString(msg)) {
      let msgObj = JSON.parse(msg);
      if (msgObj.chartData && msgObj.chartType) {
        initObj.chartConfig = msgObj;
      }
    }
    if (msg === '<think>') {
      initObj.thinkStartTime = Date.now();
    }
    initObj.loading = false;
    idx = listRef.current.length - 1;
    if (status === 'ARCHIVED') {
      initObj.finished = true;
      if (extensions.isEnableLog && !listRef.current[idx].loading) {
        idx = listRef.current.length;
      } else {
        if (!extensions.isEnableLog && !listRef.current[idx].step) {
          initObj.content ? null :  initObj.content = listRef.current[idx].content;
        }
      }
    }
    if (extensions.isStepLog) {
      if (!listRef.current[idx].step && !listRef.current[idx].loading) {
        idx = listRef.current.length;
      }
    }
    if (testRef.current) {
      initObj.messageType = 'form';
      listRef.current.push(initObj);
      dispatch(setFormReceived(false));
    } else {
      const { thinkTime } = listRef.current[listRef.current.length - 1];
      if (thinkTime) {
        initObj.thinkTime = thinkTime;
      }
      const receiveItem = multiModelProcess(initObj);
      listRef.current.splice(idx, 1, deepClone(receiveItem));
    }
    listRef.current[listRef.current.length - 1]['logId'] = logId ? Number(logId) : '';
    dispatch(setChatList(deepClone(listRef.current)));
  }
  // 流式输出拼接
  function chatSplicing(log, msg, initObj, status) {
    let msgId = log.msgId;
    let currentChatItem = listRef.current.filter((item) => item.msgId === msgId)[0];
    if (currentChatItem) {
      let index = listRef.current.findIndex((item) => item.msgId === log.msgId);
      let item = listRef.current[index];
      let str = '';
      let { content } = currentChatItem;
      str = content + msg;
      item.content = str;
      if (status === 'ARCHIVED') {
        item.finished = true;
      }
      if (msg === '</think>' && item.thinkStartTime) {
        item.thinkTime = Date.now() - item.thinkStartTime;
      }
      dispatch(setChatList(deepClone(listRef.current)));
    } else {
      chatStrInit(msg, initObj, status, '');
    }
  }
  // 多模型回答时消息处理
  const multiModelProcess = (initObj) => {
    let latestMsgId = initObj.msgId;
    let latestContent = initObj.content;
    let { msgId, content } = listRef.current[listRef.current.length - 1];
    if (latestMsgId) {
      if (msgId && msgId !== latestMsgId) {
        initObj.content = `${content}<br/>${latestContent}`
      }
    }
    return initObj;
  }
  // 显示问答组
  function setEditorShow(val, type = 'share') {
    if (isChatRunning()) {
      Message({ type: 'warning', content: t('tryLater') });
      return
    }
    !val && setCheckedList([]);
    setShowCheck(val);
    val && setGroupType(type);
    feedRef.current?.setCheckStatus();
  }
  // 终止对话成功回调
  function onStop(str) {
    let item = listRef.current[listRef.current.length - 1];
    item.content = str;
    item.recieveType = undefined;
    item.loading = false;
    item.messageType = 'form';
    item.status = 'TERMINATED';
    chatRender.current = false;
    dispatch(setChatList(deepClone(listRef.current)));
    dispatch(setChatRunning(false));
    setShowStop(false);
    dispatch(setReference(false));
    dispatch(setReferenceList({}));
  }
  // 终止进行中的对话
  async function chatRunningStop(params) {
    let terminateParams: any = {};
    params.content ? terminateParams.content = params.content : terminateParams.content = t('conversationTerminated');
    params.instanceId ? runningInstanceId.current = params.instanceId : '';
    if (params.logId) {
      terminateParams.logId = params.logId;
    }
    if (!runningInstanceId.current) {
      onStop(params.content);
      return;
    };
    setStopLoading(true);
    try {
      const res:any = await stopInstance(tenantId, runningInstanceId.current, terminateParams);
      if (res.code === 0) {
        onStop(res.data || terminateParams.content);
        Message({ type: 'success', content: t('conversationTerminated') });
      } else {
        Message({ type: 'error', content: t('terminateFailed') });
      }
    } finally {
      setStopLoading(false);
    }
  }
  // 表单重新对话
  function questionClarConfirm(params, instanceId) {
    queryInstance(params, 'clar', instanceId);
  }
  function conditionConfirm(response, logId = undefined) {
    const reciveInitObj = deepClone(initChat);
    if (logId) {
      let currentChatItem = listRef.current.filter((item) => item.logId === logId)[0];
      if (currentChatItem) {
        let index = listRef.current.findIndex((item) => item.logId === logId);
        listRef.current[index].status = 'ARCHIVED';
      }
    }
    listRef.current.push(reciveInitObj);
    dispatch(setChatList(deepClone(listRef.current)));
    dispatch(setChatRunning(true));
    chatRender.current = true;
    scrollToBottom();
    setShowStop(false);
    chatStreaming(response);
  }
  // 用户自勾选删除对话回调
  const deleteChat = async (list) => {
    const res:any = await clearChat(appId, list);
    if (res.code === 0) {
      Message({ type: 'success', content: t('deleteSuccess') });
      listRef.current = listRef.current.filter(item => !list.includes(Number(item.logId)))
      dispatch(setChatList(deepClone(listRef.current)));
      setEditorShow(false);
    }
  }
  // 继续会话回填chatlist
  const setListCurrentList = (list) => {
    listRef.current = deepClone(list);
  }
  // 关闭链接
  const closeConnected = () => {
    chatRender.current = false;
    dispatch(setChatRunning(false));
    setShowStop(false);
  }
  // 刷新灵感大全
  const refreshInspiration = () => {
    inspirationRef.current.initInspiration();
  }

  const handlePreview = (e) => {
    if (e.detail) {
      setPreviewProps(e.detail);
      setPreviewVisible(true);
    }
  };

  const scrollToBottom = () => {
    setTimeout(() => {
      scrollBottom();
    }, 300);
  }

  useEffect(() => {
    setConfigAppInfo(atAppInfo || appInfo || {});
  }, [atAppInfo, appInfo]);

  useEffect(() => {
    if (!chatRunning) {
      setChatFileList([]);
      isAutoSend.current = false;
    }
  }, [chatRunning]);

  return (
    <div
      className={`
        chat-preview 
        ${showElsa ? 'chat-preview-elsa' : ''} 
        ${detailPage ? 'chat-preview-inner' : ''} 
        ${(showElsa && inspirationOpen) ? 'chat-preview-mr' : ''}`}
    >
      <Spin spinning={loading}>
        <span className='icon-back' onClick={previewBack}>
          {showElsa && <LeftArrowIcon />}
        </span>
        <div className={`${setSpaClassName('chat-inner')} ${!detailPage ? setSpaClassName('chat-page-inner') : ''}`}>
          <div className={`chat-inner-left ${inspirationOpen && showInspiration ? 'chat-left-close' : 'no-border'}`}>
            <ChatMessage
              feedRef={feedRef}
              chatRunningStop={chatRunningStop}
              setCheckedList={setCheckedList}
              setEditorShow={setEditorShow}
              conditionConfirm={conditionConfirm}
              chatStreaming={chatStreaming}
              questionClarConfirm={questionClarConfirm}
              refreshInspiration={refreshInspiration}
              showCheck={showCheck} />
            <CheckGroup
              type={groupType}
              display={showCheck}
              setEditorShow={setEditorShow}
              checkedChat={checkedList}
              deleteChat={deleteChat} />
            <SendEditor
              display={!showCheck && !readOnly}
              onSend={onSend}
              onStop={chatRunningStop}
              filterRef={editorRef}
              showStop={showStop}
              stopLoading={stopLoading}
              setEditorShow={setEditorShow}
              inspirationOpen={inspirationOpen}
              setListCurrentList={setListCurrentList}
              checkMutipleInput={checkMutipleInput}
              updateUserContext={(val) => setUserContext(val)}
              setChatFileList={handleUpdateFileList}
              checkFileSuccess={checkFileSuccess}
            />
            {previewVisible && <PreviewPicture {...previewProps} closePreview={() => setPreviewVisible(false)} />}
          </div>
          {showInspiration && <div className={`chat-inner-right ${inspirationOpen && !readOnly ? 'chat-right-close' : ''}`}>
            {appInfo.id &&
              <Inspiration
                reload={inspirationRef}
                inspirationClick={onSend}
                setEditorSelect={setEditorSelect}
                setEditorHtml={setEditorHtml} />
            }
          </div>}
        </div>
      </Spin>
    </div>
  );
};

export default ChatPreview;
