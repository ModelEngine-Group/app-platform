/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect, useRef } from 'react';
import { Spin } from 'antd';
import { Message } from '@/shared/utils/message';
import { InspirationIcon, RebotIcon } from '@/assets/icon';
import { getRecommends } from '@/shared/http/chat';
import { getGuestModeRecommends } from '@/shared/http/guest';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setInspirationOpen } from '@/store/chatStore/chatStore';
import { useTranslation } from 'react-i18next';
import { findConfigValue } from '@/shared/utils/common';
import { isChatRunning } from '@/shared/utils/chat';

/**
 * 应用聊天猜你想问组件
 *
 * @return {JSX.Element}
 * @param onSend 点击猜你想问发送聊天消息
 * @param resetEditorHeight 猜你想问高度动态适配聊天列表高度
 * @constructor
 */
const Recommends = (props) => {
  const { t } = useTranslation();
  const { onSend, resetEditorHeight } = props;
  const [recommendList, setRecommendList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showRecommend, setShowRecommend] = useState(false);
  const [showInspiration, setShowInspiration] = useState(false);
  const curRecommendList = useRef([]);
  const refreshRef = useRef<any>(true);
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const inspirationOpen = useAppSelector((state) => state.chatCommonStore.inspirationOpen);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const isGuest = useAppSelector((state) => state.appStore.isGuest);

  // 猜你想问
  const recommendClick = (item) => {
    if (isChatRunning()) {
      Message({ type: 'warning', content: t('tryLater') });
      return;
    }
    onSend(item);
  }
  // 换一批
  const refreshClick = () => {
    if (loading) return;
    if (isChatRunning()) {
      Message({ type: 'warning', content: t('tryLater') });
      return;
    }
    if (chatList && chatList.length) {
      getRecommendList();
    }
  }
  // 获取推荐列表
  async function getRecommendList() {
    let chatLength = chatList.length;
    let question = chatList[chatLength - 2]?.content;
    let answer = chatList[chatLength - 1]?.content;
    let params = {
      question,
      answer
    }
    recommendList.length > 0 && setLoading(true);
    try {
      const res:any = isGuest ? await getGuestModeRecommends(params) : await getRecommends(params);
      if (res.code === 0 && res.data.length > 0) {
        let list = shuffle(res.data);
        curRecommendList.current = list;
        setRecommendList(list);
      } else if (res.data.length === 0 && recommendList.length > 0) {
        let list = shuffle(recommendList);
        curRecommendList.current = list;
        setRecommendList(list);
      }
    } finally {
      setLoading(false);
    }
  }
  // 数组打乱排序
  const shuffle = (arr) => {
    return arr.sort(() => Math.random() - 0.5);
  }
  // 打开收起灵感大全
  const iconClick = () => {
    dispatch(setInspirationOpen(!inspirationOpen));
  }

  useEffect(() => {
    if (!Object.keys(appInfo).length) return;
    const recommendItem = findConfigValue(appInfo, 'recommend');
    const inspirationItem = findConfigValue(appInfo, 'inspiration');
    setShowRecommend(recommendItem?.showRecommend || false);
    setShowInspiration(inspirationItem?.showInspiration || false);
    setRecommendList(recommendItem?.list || []);
  }, [appInfo]);

  useEffect(() => {
    if (chatRunning) {
      refreshRef.current = true;
    }
  }, [chatRunning]);

  // 实时刷新推荐列表
  useEffect(() => {
    if (chatList?.length > 0 && showRecommend && refreshRef.current) {
      let chatItem = chatList[chatList.length - 1];
      if (chatItem && chatItem.finished && !chatItem.messageType) {
        getRecommendList();
        refreshRef.current = false;
      }
    }
  }, [chatList]);

  useEffect(() => {
    return () => {
      curRecommendList.current = [];
      setRecommendList([]);
    }
  }, []);

  useEffect(() => {
    resetEditorHeight(recommendList);
  }, [recommendList]);

  return <>
    {
      (recommendList?.length > 0 && showRecommend) && (
        <div className='recommends-top'>
          <span className='title'>{t('guessAsk')}</span>
          {
            chatList?.length > 0 && (<div>
              <RebotIcon onClick={refreshClick} />
              <span className='refresh' onClick={refreshClick}>{t('changeBatch')}</span>
            </div>)
          }
        </div>
      )
    }
    <div className='recommends-list'>
      <Spin spinning={loading}>
        <div className='list-left'>
          {
            showRecommend && (recommendList?.map((item, index) => {
              return (
                <div
                  className='recommends-item'
                  onClick={() => recommendClick(item)}
                  key={index}
                >
                  {item}
                </div>
              )
            }))
          }
        </div>
      </Spin>
      {
        showInspiration && (<div className='list-right'
          onClick={iconClick}
        >
          <InspirationIcon className={inspirationOpen ? 'active' : ''} />
        </div>)
      }
    </div>
  </>
}

export default Recommends;
