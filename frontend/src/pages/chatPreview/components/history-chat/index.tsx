/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useRef, useState } from 'react';
import { Drawer, Dropdown, Tooltip, Modal, Spin, Empty } from 'antd';
import type { MenuProps } from 'antd';
import { v4 as uuidv4 } from 'uuid';
import { EllipsisOutlined, ClearOutlined, CloseOutlined } from '@ant-design/icons';
import Pagination from '@/components/pagination/index';
import { clearChatHistory, getChatList, queryFeedback } from '@/shared/http/chat';
import {
  clearGuestModeChatHistory,
  getGuestModeChatList,
  guestModeQueryFeedback,
} from '@/shared/http/guest';
import { getChatRecentLog } from '@/shared/http/aipp';
import { formatLocalDate } from '@/common/dataUtil';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { isChatRunning } from '@/shared/utils/chat';
import { setChatList, setChatRunning, setChatId, setOpenStar } from '@/store/chatStore/chatStore';
import { updateChatId } from '@/shared/utils/common';
import { historyChatProcess } from '../../utils/chat-process';
import { useTranslation } from 'react-i18next';
import * as dayjs from 'dayjs';
import { Message } from '@/shared/utils/message';
import './style.scoped.scss';

/**
 * 应用聊天历史记录列表
 *
 * @return {JSX.Element}
 * @param openHistorySignal 打开抽屉
 * @constructor
 */
interface HistoryChatProps {
  openHistorySignal: number;
  setListCurrentList: any;
}

const HistoryChatDrawer: React.FC<HistoryChatProps> = ({
  openHistorySignal,
  setListCurrentList,
}) => {
  const { t } = useTranslation();
  const currentChat = useRef<any>(null);
  const dispatch = useAppDispatch();
  const appId = useAppSelector((state) => state.appStore.appId);
  const aippId = useAppSelector((state) => state.appStore.aippId);
  const appVersion = useAppSelector((state) => state.appStore.appVersion);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const chatId = useAppSelector((state) => state.chatCommonStore.chatId);
  const openStar = useAppSelector((state) => state.chatCommonStore.openStar);
  const inspirationOpen = useAppSelector((state) => state.chatCommonStore.inspirationOpen);
  const isGuest = useAppSelector((state) => state.appStore.isGuest);
  const [open, setOpen] = useState(false);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);
  const [isClearOpen, setClearOpen] = useState(false);

  const refreshList = async () => {
    try {
      setLoading(true);
      const requestBody = {
        aipp_id: aippId,
        aipp_version: appVersion,
        offset: (page - 1) * 20,
        limit: 20,
        app_state: 'active',
      };
      const chatRes: any = isGuest
        ? await getGuestModeChatList(tenantId, requestBody)
        : await getChatList(tenantId, requestBody);
      setTotal(chatRes?.data?.range?.total || 0);
      let timeTag = [false, false, false];
      chatRes?.data?.results.forEach((item, idx) => {
        const uTime = dayjs(new Date(item?.update_time_timestamp));
        const cTime = dayjs(new Date(item?.current_time_timestamp));
        if (!timeTag[0] && uTime.isSame(cTime, 'day')) {
          timeTag[0] = true;
          item.categoryTag = 'today';
        } else if (!timeTag[1] && uTime.isSame(cTime, 'month') && !uTime.isSame(cTime, 'day')) {
          timeTag[1] = true;
          item.categoryTag = 'thisMonth';
        } else if (!timeTag[2] && uTime.isBefore(cTime, 'month')) {
          timeTag[2] = true;
          item.categoryTag = 'older';
        }
      });
      setData(chatRes?.data?.results || []);
    } finally {
      setLoading(false);
    }
  };
  // 删除单个对话
  const deleteAllChat = async () => {
    let params = {
      chat_id: currentChat.current?.chat_id,
    };
    if (isChatRunning() && chatId === currentChat.current?.chat_id) {
      Message({ type: 'warning', content: t('tryLater') });
      return;
    }
    setLoading(true);
    try {
      isGuest
        ? await clearGuestModeChatHistory(tenantId, appId, params)
        : await clearChatHistory(tenantId, appId, params);
      let storageParams: any = {
        deleteAppId: appId,
        deleteChatId: currentChat?.current?.chat_id,
        type: 'deleteChat',
      };
      if (chatId === currentChat?.current?.chat_id) {
        dispatch(setChatId(null));
        dispatch(setChatList([]));
        storageParams.refreshChat = true;
      }
      localStorage.setItem('storageMessage', JSON.stringify(storageParams));
      if (data.length === 1 && page > 1) {
        setPage(page - 1);
      } else {
        refreshList();
      }
    } catch {
      setLoading(false);
    }
  };
  const items: MenuProps['items'] = [
    {
      key: '1',
      label: <div onClick={deleteAllChat}>{t('delete')}</div>,
    },
  ];

  // 继续聊天
  const continueChat = async (chat_id, dimensionId = '') => {
    if (isChatRunning()) {
      Message({ type: 'warning', content: t('tryLater') });
      return;
    }
    dispatch(setChatRunning(false));
    dispatch(setChatList([]));
    setLoading(true);
    try {
      const chatListRes = await getChatRecentLog(tenantId, chat_id, appId);
      let chatItem = historyChatProcess(chatListRes);
      let chatArr = await Promise.all(
        chatItem.map(async (item) => {
          if (item.type === 'receive' && item?.instanceId) {
            const res = isGuest
              ? await guestModeQueryFeedback(item.instanceId)
              : await queryFeedback(item.instanceId);
            item.feedbackStatus = res?.usrFeedback ?? -1;
          }
          return item;
        })
      );
      setListCurrentList(chatArr);
      await dispatch(setChatList(chatArr));
      dispatch(setChatId(chat_id));
      setOpen(false);
      updateChatId(chat_id, appId);
    } finally {
      setLoading(false);
    }
  };
  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    if (page !== curPage) {
      setPage(curPage);
    }
  };
  const onClearList = async () => {
    if (isChatRunning()) {
      Message({ type: 'warning', content: t('tryLater') });
      return;
    }
    setClearOpen(false);
    let storageParams = {
      deleteAppId: appId,
      refreshChat: true,
      key: uuidv4(),
      type: 'deleteChat',
    };
    setLoading(true);
    try {
      isGuest
        ? await clearGuestModeChatHistory(tenantId, appId)
        : await clearChatHistory(tenantId, appId);
      refreshList();
      dispatch(setChatList([]));
      dispatch(setChatId(null));
      localStorage.setItem('storageMessage', JSON.stringify(storageParams));
      updateChatId(null, appId);
      setOpen(false);
    } catch {
      setLoading(false);
    }
  };
  const removeTagContent = (content: string) => {
    if (!content) return '';
    return content.replace(/^[\s\S]*?<\/think>/s, '');
  };

  useEffect(() => {
    if (openHistorySignal > 0 && !open) {
      setOpen(true);
      dispatch(setOpenStar(false));
      refreshList();
    }
  }, [openHistorySignal]);

  useEffect(() => {
    if (openStar === true) {
      setOpen(false);
    }
  }, [openStar]);
  useEffect(() => {
    if (inspirationOpen && open) {
      setOpen(false);
    }
  }, [inspirationOpen]);
  useEffect(() => {
    open && refreshList();
  }, [page]);
  return (
    <Drawer
      destroyOnClose
      mask={false}
      title={
        <div className='history-title'>
          <div className='history-title-left'>
            <span>{t('historyChat')}</span>
            <div className='history-clear-btn' onClick={() => setClearOpen(true)}>
              <ClearOutlined style={{ fontSize: 14, marginLeft: 8 }} />
              <span className='history-clear-btn-text'>{t('clear')}</span>
            </div>
          </div>
          <CloseOutlined style={{ fontSize: 20 }} onClick={() => setOpen(false)} />
        </div>
      }
      onClose={() => setOpen(false)}
      open={open}
      closeIcon={false}
      width='460px'
      bodyStyle={{ padding: 0 }}
    >
      <Spin spinning={loading}>
        {data.length > 0 && (
          <div className='history-wrapper'>
            {data?.map((item: any) => (
              <>
                {item.categoryTag && (
                  <div className='history-category' key={item.categoryTag}>
                    {t(item.categoryTag)}
                  </div>
                )}
                <div
                  className='history-item'
                  key={item?.chat_id}
                  onClick={() => {
                    currentChat.current = item;
                  }}
                >
                  <div className='history-item-content'>
                    <div className='history-item-header'>
                      <Tooltip
                        placement='top'
                        title={<span style={{ color: '#4d4d4d' }}>{item?.chat_name}</span>}
                        color='#ffffff'
                      >
                        <div className='history-item-title'>{item?.chat_name}</div>
                      </Tooltip>
                      <span
                        className='history-item-btn'
                        onClick={() => {
                          continueChat(item?.chat_id, item.attributes.dimension_id);
                        }}
                      >
                        {t('continueChat')}
                      </span>
                    </div>
                    <div className='history-item-desc'>{removeTagContent(item?.recent_info)}</div>
                  </div>
                  <div className='history-item-footer'>
                    <span>{formatLocalDate(item?.update_time_timestamp)}</span>
                    <Dropdown menu={{ items }} trigger={['click']}>
                      <EllipsisOutlined className='history-item-footer-more' />
                    </Dropdown>
                  </div>
                </div>
              </>
            ))}
          </div>
        )}
        {data.length === 0 && (
          <div className='history-wrapper flex-box'>
            <Empty description={t('noData')} />
          </div>
        )}
      </Spin>
      <div className='history-page'>
        <Pagination
          total={total}
          current={page}
          pageSize={20}
          onChange={paginationChange}
          showTotal={false}
          showLessItems
          showSizeChanger={false}
        />
      </div>
      <Modal
        title={t('alert')}
        open={isClearOpen}
        onOk={onClearList}
        onCancel={() => setClearOpen(false)}
      >
        <p>{t('clearHistoryChatContent')}</p>
      </Modal>
    </Drawer>
  );
};

export default HistoryChatDrawer;
