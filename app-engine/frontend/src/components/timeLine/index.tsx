/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useContext, useEffect, useRef, useState } from 'react';
import { Button, Drawer, Tag, Timeline } from 'antd';
import { useParams } from 'react-router-dom';
import { CloseOutlined } from '@ant-design/icons';
import { Message } from '@/shared/utils/message';
import { getAppInfo, getVersion, resetApp } from '@/shared/http/aipp';
import { useTranslation } from 'react-i18next';
import { useAppDispatch } from '@/store/hook';
import { setIsReadOnly } from '@/store/common/common';
import { RenderContext } from '@/pages/aippIndex/context';

const PAGE_SIZE = 10;

const TimeLineFc = (props) => {
  const { open, setOpen, type = '', updateAippCallBack } = props;
  const [timeList, setTimeList] = useState([]);
  const { tenantId, appId } = useParams();
  const { t } = useTranslation();
  const [selectedAppId, setSelectedAppId] = useState(appId);
  const dispatch = useAppDispatch();
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const scrollRef = useRef();
  const hasMoreRef = useRef<any>(true);
  const currentAppInfo = useRef<any>(null);
  const { renderRef, elsaReadOnlyRef } = useContext(RenderContext);

  const fetchData = async (currentPage: number) => {
    if (!open || loading || !hasMoreRef.current) return;
    setLoading(true);
    try {
      const res = await getVersion(tenantId, appId, type, PAGE_SIZE * (currentPage - 1), PAGE_SIZE);
      if (res.code === 0) {
        const newItems = res.data.results || [];
        setTimeList((prev) => {
          const newIds = new Set(prev.map((item) => item.id));
          const filteredNewItems = newItems.filter((item) => !newIds.has(item.id));
          return [...prev, ...filteredNewItems];
        });
        if (newItems.length < PAGE_SIZE) {
          hasMoreRef.current = false;
        } else {
          setPage(currentPage + 1);
        }
      }
    } finally {
      setLoading(false);
    }
  };

  const getCurrentApp = async (tenantId: string, appId: string) => {
    const res: any = await getAppInfo(tenantId, appId);
    if (res.code === 0) {
      currentAppInfo.current = res.data;
    }
  };

  useEffect(() => {
    if (open) {
      dispatch(setIsReadOnly(true));
      setTimeList([]);
      setPage(1);
      hasMoreRef.current = true;
      window.agent?.readOnly();

      Promise.all([
        getCurrentApp(tenantId, appId),  // 刷新当前应用数据
        fetchData(1)      // 加载历史版本
      ]).catch(console.error);
    }
  }, [open]);

  useEffect(() => {
    const handleScroll = () => {
      const container = scrollRef.current;
      if (!container) return;
      const { scrollTop, scrollHeight, clientHeight } = container;
      if (scrollTop + clientHeight >= scrollHeight - 50) {
        fetchData(page);
      }
    };

    const container = scrollRef.current;
    container?.addEventListener('scroll', handleScroll);
    return () => container?.removeEventListener('scroll', handleScroll);
  }, [page, open, hasMoreRef.current]);


  const descProcess = (str) => (!str || str === 'null' ? '' : str);

  const handleRecover = () => {
    if (appId !== selectedAppId) {
      resetApp(tenantId, appId, selectedAppId, {
        'Content-Type': 'application/json'
      }).then(res => {
        if (res.code === 0) {
          Message({ type: 'success', content: t('resetSucceed') });
          currentAppInfo.current = res.data;
          handleClose();
        }
      });
    }
  };

  const handleItemClick = (timeItem) => {
    setSelectedAppId(timeItem.id);
    updateAippCallBack(
      {
        flowGraph: timeItem.flowGraph,
        configFormProperties: timeItem.configFormProperties
      }
    );
   renderRef.current = false;
   elsaReadOnlyRef.current = true;
  };

  const handleClose = () => {
    dispatch(setIsReadOnly(false));
    setSelectedAppId(appId);
    setTimeList([]);
    setPage(1);
    hasMoreRef.current = true;
    updateAippCallBack(currentAppInfo.current);
    renderRef.current = false;
    elsaReadOnlyRef.current = false;
    setOpen(false);
  }

  useEffect(() => {
    return () => {
      // 组件卸载时自动重置
      dispatch(setIsReadOnly(false));
    };
  }, [dispatch]);

  return (
    <Drawer
      title={t('publishHistory')}
      placement='right'
      width='420px'
      closeIcon={false}
      onClose={handleClose}
      open={open}
      mask={false}
      footer={
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', padding: '16px 0' }}>
          <Button onClick={handleClose}>{t('exit')}</Button>
          <Button type="primary" onClick={handleRecover} disabled={appId === selectedAppId}>{t('recover')}</Button>
        </div>
      }
      extra={<CloseOutlined onClick={handleClose} />}
    >
      <div ref={scrollRef} style={{ maxHeight: '750px', overflowY: 'auto', paddingRight: '8px' }}>
        {timeList.length > 0 ? (
          <Timeline>
            <Timeline.Item
              color={appId === selectedAppId ? 'blue' : '#000000'}
              key={appId}
            >
              <div
                className="time-line-inner"
                style={{
                  color: appId === selectedAppId ? '#1677ff' : 'rgb(77, 77, 77)',
                  backgroundColor: appId === selectedAppId ? '#e6f7ff' : 'transparent',
                  borderRadius: '4px',
                  padding: '8px',
                  cursor: 'pointer',
                }}
                onClick={() => handleItemClick(currentAppInfo.current)}
              >
                <div style={{ fontWeight: '700' }}>{t('currentDraft')}</div>
              </div>
            </Timeline.Item>
            {timeList.map((timeItem, index) => {
              const isSelected = timeItem.id === selectedAppId;
              const isLatest = index === 0;
              return (
                <Timeline.Item
                  color={isSelected ? '#2673e5' : 'rgb(77, 77, 77)'}
                  key={timeItem.id}
                >
                  <div
                    className="time-line-inner"
                    style={{
                      color: isSelected ? '#2673e5' : 'rgb(77, 77, 77)',
                      backgroundColor: isSelected ? '#e6f7ff' : 'transparent',
                      borderRadius: '4px',
                      padding: '8px',
                      cursor: 'pointer',
                    }}
                    onClick={() => handleItemClick(timeItem)}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', fontWeight: 700, gap: '12px' }}>
                      <span>{timeItem.version}</span>
                      {isLatest &&
                        <Tag
                          style={{
                            borderColor: '#2673e5',
                            color: '#2673e5',
                            backgroundColor: 'transparent',
                            fontWeight: 'normal',
                            padding: '0 8px',
                            height: '22px',
                          }}
                        >
                          {t('latest')}
                        </Tag>
                      }
                    </div>
                    <div style={{ margin: '8px 0' }}>{descProcess(timeItem.publishedDescription)}</div>
                    <div>{timeItem.updateBy}</div>
                    <div>{timeItem.updateAt}</div>
                  </div>
                </Timeline.Item>
              );
            })}
            {loading && <div style={{ textAlign: 'center', padding: '12px' }}>{t('loading')}...</div>}
            {!hasMoreRef.current && <div style={{ textAlign: 'center', color: '#888' }}>{t('noMore')}</div>}
          </Timeline>
        ) : null}
      </div>
    </Drawer>
  );
};

export default TimeLineFc;
