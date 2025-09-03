/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Button, Modal, Spin } from 'antd';
import { useParams, useHistory } from 'react-router-dom';
import { getAppInfo, getPublishAppId, getPreviewAppInfo } from '@/shared/http/aipp';
import {
  setAppId,
  setAppInfo,
  setAippId,
  setAppVersion,
  setIsGuest,
} from '@/store/appInfo/appInfo';
import { setHistorySwitch } from '@/store/common/common';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setIsDebug } from '@/store/common/common';
import { setInspirationOpen } from '@/store/chatStore/chatStore';
import { storage } from '@/shared/storage';
import { useTranslation } from 'react-i18next';
import { findConfigValue, generateUniqueName, getAppConfig } from '@/shared/utils/common';
import {
  getAppGuestIsOpen,
  getGuestModeAppInfo,
  getGuestModePublishAppId,
} from '@/shared/http/guest';
import { TENANT_ID } from '../chatPreview/components/send-editor/common/config';
import CommonChat from '../chatPreview/chatComminPage';
import Login from './login';
import NoAuth from './no-auth';
import './index.scoped..scss';
import { getOmsUser, getRole } from '@/pages/helper';
import WrongAddress from '@/pages/chatRunning/wrong-address';

/**
 * 聊天运行时组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const ChatRunning = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [isPreview, setIsPreview] = useState(false);
  const [login, setLogin] = useState(true);
  const [notice, setNotice] = useState('');
  const [wrongAddress, setWrongAddress] = useState(false);
  const { appId, tenantId, uid } = useParams();
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const loginStatus = useAppSelector((state) => state.chatCommonStore.loginStatus);
  const noAuth = useAppSelector((state) => state.chatCommonStore.noAuth);
  const pluginList = useAppSelector((state) => state.chatCommonStore.pluginList);
  const isGuest = useAppSelector((state) => state.appStore.isGuest);

  // 插件不显示app name，可能遮挡插件内容，仅留返回按钮
  const [pluginName, setPluginName] = useState('default');
  const [plugin, setPlugin] = useState();

  // 获取publishId
  const getPublishId = async () => {
    setLoading(true);
    const res: any = await getPublishAppId(tenantId, appId);
    if (res && res.code === 0) {
      getAippDetails(res.data.app_id);
      dispatch(setAippId(res.data.aipp_id));
      dispatch(setAppVersion(res.data.version));
    } else {
      setLoading(false);
    }
  };
  // 获取公共访问详情
  const getPreviewData = async (isGuest: boolean) => {
    setLoading(true);
    const res: any = isGuest ? await getGuestModeAppInfo(uid) : await getPreviewAppInfo(uid);
    if (res && res.code === 0) {
      res.data.notShowHistory = false;
      dispatch(setAppInfo(res.data));
      dispatch(setAppId(res.data.id));
      dispatch(setIsDebug(false));
      setNotice('');
      announcements(res.data);
      getPreviewVersion(res.data.id, isGuest);
      dispatch(setInspirationOpen(true));
    } else {
      setLoading(false);
    }
  };
  // 预览界面获取aipp_id和aipp_version
  const getPreviewVersion = async (id: string, isGuest: boolean) => {
    try {
      const res: any = isGuest
        ? await getGuestModePublishAppId(TENANT_ID, id)
        : await getPublishAppId(TENANT_ID, id);
      if (res && res.code === 0) {
        dispatch(setAippId(res.data.aipp_id));
        dispatch(setAppVersion(res.data.version));
      }
    } finally {
      setLoading(false);
    }
  };
  // 获取aipp详情
  const getAippDetails = async (appId: string) => {
    try {
      const res: any = await getAppInfo(tenantId || TENANT_ID, appId);
      setLoading(false);
      if (res.code === 0) {
        res.data.notShowHistory = false;
        dispatch(setAppInfo(res.data));
        dispatch(setAppId(res.data.id));
        dispatch(setIsDebug(false));
        setNotice('');
        announcements(res.data);
        dispatch(setInspirationOpen(true));
        const appChatStyle = getAppConfig(res.data) ? getAppConfig(res.data).appChatStyle : null;
        setPluginName(appChatStyle || 'default');
      }
    } finally {
      setLoading(false);
    }
  };

  const getHistorySwitchValue = (data) => {
    if (!data.flowGraph) {
      return false;
    }
    const memoryItem = findConfigValue(data, 'memory');
    return memoryItem?.memorySwitch || false;
  };

  // 公告弹层
  const announcements = (data) => {
    const { id, version, attributes } = data;
    let chatVersionListMap = storage.get('chatVersionMap');
    if (chatVersionListMap) {
      try {
        let versionItem = chatVersionListMap.filter((item) => item.id === id)[0];
        if (!versionItem) {
          chatVersionListMap.push({ id, version });
          setModalContent(data, chatVersionListMap);
        } else if (versionItem && versionItem.version !== version) {
          let index = chatVersionListMap.findIndex((item) => item.id === id);
          chatVersionListMap[index].version = version;
          setModalContent(data, chatVersionListMap);
        }
      } catch {
        setIsModalOpen(false);
      }
    } else {
      setModalContent(data, [{ id, version }]);
    }
  };

  // 保存并显示弹层
  const setModalContent = (data, arr) => {
    let { publishedUpdateLog } = data.attributes;
    if (publishedUpdateLog && publishedUpdateLog.length) {
      setNotice(publishedUpdateLog);
      setIsModalOpen(true);
      storage.set('chatVersionMap', arr);
    }
  };

  // 更新随机用户名
  const refreshUserName = (isGuest) => {
    if (isGuest) {
      !localStorage.getItem('guest-name') &&
        localStorage.setItem('guest-name', generateUniqueName());
    } else {
      localStorage.removeItem('guest-name');
    }
  };

  // 校验公共访问页面地址是否访问合规
  const validatePage = (isGuest) => {
    return location.hash.includes('/guest/chat/') ? isGuest : !isGuest;
  };

  useEffect(() => {
    const found = pluginList.find((item: any) => item.name === pluginName);
    setPlugin(found);
  }, [pluginList, pluginName]);

  // 点击显示弹层
  useEffect(() => {
    if (uid) {
      getAppGuestIsOpen(uid).then((res) => {
        const isGuest = res.data;
        if (!validatePage(isGuest)) {
          setWrongAddress(true);
          return;
        }
        if (!isGuest && process.env.PACKAGE_MODE === 'spa') {
          getOmsUser();
          getRole();
        }
        dispatch(setIsGuest(isGuest));
        refreshUserName(isGuest);
        getPreviewData(isGuest);
      });
      setIsPreview(true);
    } else {
      getPublishId();
      setIsPreview(false);
    }
  }, []);

  useEffect(() => {
    if (appInfo.id) {
      dispatch(setHistorySwitch(getHistorySwitchValue(appInfo)));
    }
  }, [appInfo.id]);

  useEffect(() => {
    if (!loginStatus) {
      setLogin(false);
    }
  }, [loginStatus]);

  // 插件中可能调用history.push，导致无法返回应用市场
  const handleBack = () => {
    if (plugin) {
      history.push({ pathname: '/app' });
    } else {
      history.goBack();
    }
  };

  return (
    <Spin spinning={loading}>
      {wrongAddress ? (
        <WrongAddress />
      ) : noAuth && !isGuest ? (
        <div className='appengine-no-auth'>
          <NoAuth />
        </div>
      ) : (
        <div className={`chat-running-container ${isPreview ? 'chat-running-full' : ''}`}>
          {isPreview ? (
            <Login login={login} />
          ) : (
            <div className='chat-running-chat'>
              <Button
                className='chat-btn-back'
                size='small'
                type='text'
                style={{ margin: '6px 12px' }}
                onClick={handleBack}
              >
                {t('return')}
              </Button>
              {plugin ? null : <span className='running-app-name'>{appInfo.name}</span>}
            </div>
          )}
          <CommonChat pluginName={pluginName} />
          <Modal
            title={t('updateLog')}
            width={800}
            open={isModalOpen}
            onCancel={() => setIsModalOpen(false)}
            className='modal-magic-bg'
            footer={null}
          >
            <div style={{ maxHeight: '400px', overflow: 'auto' }}>
              <div dangerouslySetInnerHTML={{ __html: notice }}></div>
            </div>
            <div style={{ display: 'flex', justifyContent: 'center', padding: '0 12px' }}>
              <Button onClick={() => setIsModalOpen(false)}>{t('gotIt')}</Button>
            </div>
          </Modal>
        </div>
      )}
    </Spin>
  );
};

export default ChatRunning;
