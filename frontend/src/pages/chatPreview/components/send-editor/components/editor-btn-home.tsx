/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import { Button, Modal, Switch, Tooltip } from 'antd';
import { Message } from '@/shared/utils/message';
import { CloseOutlined } from '@ant-design/icons';
import { AtIcon, HistoryIcon, NotificationIcon } from '@/assets/icon';
import { clearInstance } from '@/shared/http/aipp';
import { clearGuestModeInstance } from '@/shared/http/guest';
import ReferencingApp from './referencing-app';
import UploadFile from './upload-file';
import StarApps from '../../star-apps';
import ConversationConfiguration from './conversation-configuration';
import HistoryChatDrawer from '../../history-chat';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import {
  setAtChatId,
  setChatId,
  setChatList,
  setChatRunning,
  setOpenStar,
} from '@/store/chatStore/chatStore';
import { setAtAppInfo, setAtAppId } from '@/store/appInfo/appInfo';
import { getAppInfo } from '@/shared/http/aipp';
import { setUseMemory } from '@/store/common/common';
import { setSpaClassName, updateChatId, findConfigValue } from '@/shared/utils/common';
import { isChatRunning } from '@/shared/utils/chat';
import { convertImgPath } from '@/common/util';
import { HOME_APP_ID } from '../common/config';
import { useTranslation } from 'react-i18next';
import knowledgeBase from '@/assets/images/knowledge/knowledge-base.png';

/**
 * 应用聊天输入框上方操作按钮组件
 *
 * @return {JSX.Element}
 * @param fileCallBack 更新文件列表的方法
 * @param editorRef 输入框编辑器引用
 * @param setEditorShow 显示消息多选框
 * @param setListCurrentList 设置当前会话列表list
 * @param showMask 未登录显示遮罩层，无法操作
 * @param fileList 多模态文件列表
 * @constructor
 */
const EditorBtnHome = (props) => {
  const { t } = useTranslation();
  const { fileCallBack, editorRef, setEditorShow, setListCurrentList, showMask, fileList, display } = props;
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const appId = useAppSelector((state) => state.appStore.appId);
  const aippId = useAppSelector((state) => state.appStore.aippId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const chatId = useAppSelector((state) => state.chatCommonStore.chatId);
  const atAppId = useAppSelector((state) => state.appStore.atAppId);
  const atAppInfo = useAppSelector((state) => state.appStore.atAppInfo);
  const useMemory = useAppSelector((state) => state.commonStore.useMemory);
  const isDebug = useAppSelector((state) => state.commonStore.isDebug);
  const isGuest = useAppSelector((state) => state.appStore.isGuest);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [showAt, setShowAt] = useState(false);
  const [showNotice, setShowNotice] = useState(false);
  const [notice, setNotice] = useState('');
  const [appName, setAppName] = useState('');
  const [appIcon, setAppIcon] = useState('');
  const [openHistorySignal, setOpenHistorySignal] = useState(0);
  const [searchKey, setSearchKey] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [multiFileConfig, setMultiFileConfig] = useState<any>({});
  const deleteId = useRef<any>([]);
  const detailPage = location.href.indexOf('app-detail') !== -1;
  const storageId = detailPage ? aippId : appId;
  const isGuestRef = useRef(isGuest);
  isGuestRef.current = isGuest;

  useEffect(() => {
    document.body.addEventListener('click', () => {
      setShowAt(false);
    });
    getImgPath(appInfo.attributes);
    setAppName(appInfo.name || t('app'));
    setMultiFileConfig(findConfigValue(appInfo, 'multimodal') || {});
  }, [appInfo]);

  useEffect(() => {
    if (atAppInfo) {
      getImgPath(atAppInfo.attributes);
      setAppName(atAppInfo.name);
    } else {
      getImgPath(appInfo.attributes);
      setAppName(appInfo.name || t('app'));
    }
  }, [atAppInfo]);

  // 获取图片
  const getImgPath = async (cardInfo) => {
    if (cardInfo && cardInfo.icon) {
      const res: any = await convertImgPath(cardInfo.icon);
      setAppIcon(res);
    }
  };

  // 监听storage事件
  const storageEvent = (event) => {
    if (event.key === 'storageMessage') {
      message(event.newValue);
    }
  };

  // 检测是否输入@
  useEffect(() => {
    const handleInputAt = () => {
      if (isGuestRef.current) {
        return;
      }
      const value = editorRef.current.innerText;
      if (value.startsWith('@')) {
        const contentAfterAt = value.slice(1);
        setSearchKey(contentAfterAt ? contentAfterAt : '');
        setShowAt(true);
      } else {
        setShowAt(false);
      }
    };
    editorRef.current.addEventListener('input', handleInputAt);
    window.addEventListener('storage', storageEvent, false);
    return () => {
      if (editorRef.current) {
        editorRef.current.removeEventListener('input', handleInputAt);
      }
      window.removeEventListener('storage', storageEvent, false);
      deleteId.current = [];
    };
  }, [isGuest]);

  // 多标签处理
  const message = (newValue) => {
    try {
      const data = JSON.parse(newValue);
      if (data.type === 'deleteApp') {
        const { appId } = data;
        if (appId) {
          deleteId.current.push(appId);
        }
      }
      if (data.type === 'deleteChat') {
        chatDeleteMessage(data);
      }
    } catch {
      throw new Error('Invalid data');
    }
  };
  // 多标签删除会话ID问题
  const chatDeleteMessage = (data) => {
    let { deleteChatId, refreshChat, deleteAppId } = data;
    if (appId === deleteAppId && (deleteChatId === chatId || refreshChat)) {
      dispatch(setChatId(null));
      updateChatId(null, storageId);
    }
  };
  // 清空历史记录
  const handleOk = async () => {
    if (isChatRunning()) {
      return;
    }
    if (!chatList.length) {
      setIsModalOpen(false);
      return;
    }
    const type = appInfo.state === 'active' ? 'normal' : 'preview';
    const res: any = isGuest
      ? await clearGuestModeInstance(tenantId, appId, type)
      : await clearInstance(tenantId, appId, type);
    if (res.code === 0) {
      dispatch(setChatList([]));
    }
    setIsModalOpen(false);
  };
  // @ 应用点击
  const atClick = (e) => {
    e.stopPropagation();
    if (isChatRunning()) {
      return;
    }
    setShowAt(!showAt);
  };
  // 取消@应用功能
  const cancelAt = () => {
    setAppName(appInfo.name);
    dispatch(setAtAppId(null));
    dispatch(setAtAppInfo(null));
    dispatch(setAtChatId(null));
  };
  // @应用点击回调
  const atItemClick = async (item) => {
    const appId = item.runnables.APP.appId;
    const appInfoRes: any = await getAppInfo(tenantId, appId);
    if (appInfoRes.code === 0) {
      dispatch(setAtAppInfo(appInfoRes.data));
    }
    dispatch(setAtAppId(appId));
    setAppName(item.name);
    setShowAt(false);
    dispatch(setOpenStar(false));
    if (appId !== atAppId) {
      dispatch(setAtChatId(null));
    }
  };
  // 更多应用
  const showMoreClick = () => {
    if (isChatRunning()) {
      return;
    }
    setShowAt(false);
    dispatch(setOpenStar(true));
  };
  // 多模态上传文件
  const uploadClick = () => {
    if (isChatRunning()) {
      return;
    }
    if (fileList.length > multiFileConfig.maxUploadFilesNum - 1) {
      return Message({
        type: 'error',
        content: `${t('clickUploadCountTip')}${multiFileConfig.maxUploadFilesNum}${t('num')}`,
      });
    }
    setModalOpen(true);
  };
  //是否使用多轮对话
  const onMultiConverChange = (checked) => {
    dispatch(setUseMemory(checked));
  };
  //点击“新聊天”按钮回调
  const onClickNewChat = async () => {
    if (isChatRunning()) {
      return;
    }
    dispatch(setChatRunning(false));
    updateChatId(null, storageId);
    dispatch(setChatId(null));
    dispatch(setChatList([]));
    dispatch(setAtAppInfo(null));
    dispatch(setAtChatId(null));
    dispatch(setAtAppId(null));
  };
  // 点击历史对话图标回调
  const historyChatClick = (e) => {
    if (isChatRunning()) {
      return;
    }
    setOpenHistorySignal(e.timeStamp);
  };
  // 点击更多应用按钮回调
  const onClickShowMore = () => {
    return;
  };
  // 公告
  const announcementsClick = () => {
    let { publishedUpdateLog } = appInfo.attributes;
    if (publishedUpdateLog && publishedUpdateLog.length) {
      setNotice(publishedUpdateLog);
    } else {
      setNotice(t('noAnnouncement'));
    }
    setShowNotice(true);
  };
  return (
    <div
      className={`${setSpaClassName('btn-inner')} ${fileList.length === 0 ? 'btn-radius' : ''} ${showMask ? 'btn-inner-disabled' : ''}`}
    >
      <div className='inner-left'>
        <div className='inner-item'>
          {appIcon ? <img src={appIcon} alt='' /> : <img src={knowledgeBase} alt='' />}
          <div
            className={['switch-app', atAppId ? 'switch-active' : null].join(' ')}
            onClick={onClickShowMore}
          >
            {atAppId && <span style={{ marginLeft: '6px' }}>{t('chatWith')}</span>}
            <span className='item-name' title={appName}>
              {appName}
            </span>
            {atAppId && <span style={{ marginLeft: '6px' }}>{t('chat')}</span>}
          </div>
          {multiFileConfig.useMultimodal && (
            <span className='item-upload' onClick={uploadClick}></span>
          )}
          <ConversationConfiguration
            appInfo={appInfo}
            display={display}
            updateUserContext={props.updateUserContext}
            chatRunning={chatRunning}
            isChatRunning={isChatRunning}
          />
          {!atAppId && appId === HOME_APP_ID && <AtIcon onClick={atClick} />}
        </div>
      </div>
      <div className='inner-right'>
        {atAppId ? (
          <div className='inner-item'>
            <CloseOutlined className='item-close' onClick={cancelAt} />
          </div>
        ) : (
          <div className='inner-item'>
            <NotificationIcon onClick={announcementsClick} />
            {!isDebug && <HistoryIcon onClick={historyChatClick} />}
            {
              <div className='multi-conversation-title'>
                <span>{t('multiTurnConversation')}</span>
                <Switch
                  className='multi-conversation-switch'
                  disabled={showMask}
                  size='small'
                  checked={useMemory}
                  onChange={onMultiConverChange}
                />
              </div>
            }
            {!showMask && (
              <Tooltip
                title={<span style={{ color: '#4d4d4d' }}>{t('newChat')}</span>}
                color='#ffffff'
              >
                <span className='item-clear' onClick={onClickNewChat}></span>
              </Tooltip>
            )}
          </div>
        )}
      </div>
      {showAt && (
        <ReferencingApp
          atItemClick={atItemClick}
          atClick={showMoreClick}
          searchKey={searchKey}
          setSearchKey={setSearchKey}
        />
      )}
      {/* 清空历史记录 */}
      <Modal
        title={t('clearCurrentChat')}
        open={isModalOpen}
        onOk={handleOk}
        onCancel={() => setIsModalOpen(false)}
        centered
      >
        <span>{t('clearCurrentChatContent')}</span>
      </Modal>
      {/* 公告 */}
      <Modal
        title={t('updateLog')}
        width={800}
        open={showNotice}
        onCancel={() => setShowNotice(false)}
        className='modal-magic-bg'
        footer={null}
      >
        <div style={{ maxHeight: '400px', overflow: 'auto', padding: '0 12px' }}>
          <div dangerouslySetInnerHTML={{ __html: notice }}></div>
        </div>
        <div style={{ display: 'flex', justifyContent: 'center' }}>
          <Button onClick={() => setShowNotice(false)}>{t('gotIt')}</Button>
        </div>
      </Modal>
      <StarApps handleAt={atItemClick} />
      <Modal
        title={t('uploadFile')}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        footer={null}
        width={608}
        centered
      >
        <UploadFile
          maxCount={multiFileConfig.maxUploadFilesNum}
          fileList={fileList}
          updateFileList={(fileList) => fileCallBack(fileList, multiFileConfig.autoChatOnUpload)}
          updateModal={setModalOpen}
        />
      </Modal>
      {!isDebug && (
        <HistoryChatDrawer
          openHistorySignal={openHistorySignal}
          setListCurrentList={setListCurrentList}
        />
      )}
    </div>
  );
};

export default EditorBtnHome;
