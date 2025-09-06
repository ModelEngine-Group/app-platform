/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { Avatar, Button, Drawer, Input, Dropdown } from 'antd';
import { AnyAction } from 'redux';
import {
  SearchOutlined,
  EllipsisOutlined,
  CloseOutlined,
  UserOutlined
} from '@ant-design/icons';
import { cancelUserCollection, getUserCollection, updateCollectionApp } from '@/shared/http/appDev';
import { setCurAppId } from '@/store/collection/collection';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setAtChatId, setOpenStar } from '@/store/chatStore/chatStore';
import { setAtAppId, setAtAppInfo } from '@/store/appInfo/appInfo';
import { Message } from '@/shared/utils/message';
import { isChatRunning } from '@/shared/utils/chat';
import avatarNormal from '@/assets/images/knowledge/knowledge-base.png';
import { HOME_APP_ID } from '../send-editor/common/config';
import { useTranslation } from 'react-i18next';
import './style.scoped.scss';

/**
 * 应用收藏列表组件
 *
 * @return {JSX.Element}
 * @param handleAt  @ 应用回答方法回调
 * @constructor
 */

const StarApps = ({ handleAt }) => {
  const { t } = useTranslation();
  const dispatch = useAppDispatch();
  const openStar = useAppSelector((state) => state.chatCommonStore.openStar);
  const [apps, setApps] = useState<any[]>([]);
  const clickMap: any = {

    // 设为默认
    2: async (item: AnyAction) => {
      await updateCollectionApp(getLocalUser(), item.appId)
      getUserCollectionList();
    },
    3: async (item: AnyAction) => {
      await updateCollectionApp(getLocalUser(), HOME_APP_ID);
      getUserCollectionList();
    },
    1: async (item: AnyAction) => {
      if (item?.id) {
        await cancelUserCollection({
          usrInfo: getLocalUser(),
          aippId: item.aippId,
        })
      }
      getUserCollectionList();
    }
  }
  const items: any[] = [
    {
      key: '2',
      label: t('setDefaultApp'),
    },
    {
      key: '3',
      label: t('cancelDefaultApp'),
    },
  ];

  const count = useAppSelector((state: any) => state.collectionStore.value);

  // 获取当前登录用户名
  const getLocalUser = () => {
    return localStorage.getItem('currentUserId') ?? '';
  }

  // 数据转换
  const translateData = (remoteData: any): any[] => {

    const defaultData = remoteData?.data?.defaultApp || null;

    // 设置默认应用
    const collectionList: any[] = remoteData?.data?.collectionPoList || [];
    collectionList.unshift(defaultData);
    const data = collectionList.filter(item => item);
    const res = data.map(item => {
      const attr = JSON.parse((item?.attributes ?? JSON.stringify({})))
      return ({
        ...item,
        rawData: item,
        name: item.name,
        author: item.createBy,
        desc: attr.description,
        appAvatar: attr.icon,
        authorAvatar: '',
      })
    })
    return res.filter(item => item);
  }

  // 获取用户收藏列表
  const getUserCollectionList = async () => {
    const res = await getUserCollection(getLocalUser());
    setApps([...translateData(res)]);
  }

  // 点击操作
  const clickOpera = (btn: any, item: any) => {
    clickMap[btn.key](item)
  }

  // 开始聊天
  const startChat = (item: any) => {
    if (isChatRunning()) {
      Message({ type: 'warning', content: t('tryLater') });
      return
    }
    dispatch(setCurAppId(item?.appId))
    dispatch(setOpenStar(false));
    dispatch(setAtAppId(null));
    dispatch(setAtAppInfo(null));
    dispatch(setAtChatId(null));
  }

  // @应用
  const atApp = (item) => {
    let app = item;
    app.id = item.appId;
    handleAt(app);
  }
  return (
    <Drawer
      destroyOnClose
      mask={false}
      title={
        <div className='app-title'>
          <div className='app-title-left'>
            <span>{t('collections')}</span>
          </div>
          <CloseOutlined
            style={{ fontSize: 20 }}
            onClick={() => dispatch(setOpenStar(false))}
          />
        </div>
      }
      closeIcon={false}
      onClose={() => dispatch(setOpenStar(false))}
      open={openStar}
    >
      <Input placeholder={t('search')} prefix={<SearchOutlined />} />
      <div className='app-wrapper'>
        {apps.map((app, index) => (
          <div className='app-item' key={app.id}>
            {index === 0 ? <div className='app-item-default'>{t('defaultApp')}</div> : ''}

            <div className='app-item-content'>
              {app.appAvatar ? <Avatar size={48} src={app.appAvatar} /> : <Avatar size={48} src={avatarNormal} />}
              <div className='app-item-text'>
                <div className='app-item-text-header'>
                  <div className='app-item-title' title={app.name}>{app.name}</div>
                  <div className='app-item-title-actions'>
                    {<span style={{ cursor: 'pointer' }} onClick={() => atApp(app)}>
                      @Ta
                    </span>}
                    <span
                      style={{ color: '#1677ff', cursor: 'pointer' }}
                      onClick={() => startChat(app)}
                    >
                      {t('startChat')}
                    </span>
                  </div>
                </div>
                <div className='app-item-text-desc'>{app.desc}</div>
              </div>
            </div>
            <div className='app-item-footer'>
              <div>
                <UserOutlined />
                <span className='text'>{t('by')}{app.author}{t('create')}</span>
              </div>
              <Dropdown menu={{
                items: [items[index > 0 ? 0 : 1]], onClick: (info) => {
                  clickOpera(info, app)
                }
              }} trigger={['click']} >
                <EllipsisOutlined className='app-item-footer-more' />
              </Dropdown>
            </div>
          </div>
        ))}
      </div>
      <div style={{ float: 'right', marginTop: 12 }}>
        <Button onClick={() => dispatch(setOpenStar(false))} className='close-button'>
          {t('close')}
        </Button>
      </div>
    </Drawer>
  );
};

export default StarApps;
