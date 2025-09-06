
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import { Button, Modal } from 'antd';
import { useTranslation } from 'react-i18next';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setChatList } from '@/store/chatStore/chatStore';
import { deepClone } from '../utils/chat-process';
import '../styles/check-group.scss';

const CheckGroup = (props) => {
  const { t } = useTranslation();
  const { type, setEditorShow, deleteChat, checkedChat, display } = props;
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [checkedList, setCheckedList] = useState([]);
  const currentLogId = useRef([]);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const dispatch = useAppDispatch();
  const textMap = {
    share: t('share'),
    delete: t('delete'),
    report: '',
  };
  // 全选
  const selectAll = (checkAll: boolean) => {
    const list = deepClone(chatList);
    list.forEach((item) => (item.checked = checkAll));
    dispatch(setChatList(deepClone(list)));
    checkAll ? setCheckedList(list) : setCheckedList([]);
  };
  // 取消
  function cancle() {
    setEditorShow(false);
  }
  // 处理点击
  const handleShare = (e) => {
    const logIdArr = checkedList.map(item => {
      return Number(item.logId);
    });
    currentLogId.current = logIdArr;
    setDeleteOpen(true);
  };
  const confirm = () => {
    setLoading(false);
    deleteChat(currentLogId.current);
    setDeleteOpen(false);
  };
  useEffect(() => {
    setCheckedList(checkedChat);
  }, [checkedChat]);
  return (
    <>
      {
        <div className='message-check-toolbox-wrap' style={{display: display ? 'block' : 'none'}}>
          <div className='message-check-toolbox'>
            <div className='message-check-toolbox-left'>
              <div className='message-check-toolbox__num'>
                {t('isSelected')}：{checkedList.length}{' '}
              </div>
            </div>
            <div className='message-check-toolbox-right'>
              {checkedList.length !== chatList.length && (
                <Button type='primary' onClick={() => selectAll(true)}>
                  {t('selectAll')}
                </Button>
              )}
              {checkedList.length === chatList.length && (
                <Button type='primary' onClick={() => selectAll(false)}>
                  {t('unselectAll')}
                </Button>
              )}
              <Button onClick={cancle}>{t('cancel')}</Button>
              <Button
                type={checkedList.length === 0 ? 'default' : 'primary'}
                disabled={checkedList.length === 0}
                onClick={(e) => handleShare(e)}
              >
                {(t('ok') + textMap[type])}
              </Button>
            </div>
          </div>
          <Modal
            title='提示'
            open={deleteOpen}
            centered
            width='380px'
            okText={t('ok')}
            cancelText={t('cancel')}
            okButtonProps={{ loading }}
            onOk={() => confirm()}
            onCancel={() => setDeleteOpen(false)}
          >
            <div style={{ margin: '8px 0' }}>
              <span>{t('deleteKnowledgeTips')}</span>
            </div>
          </Modal>
        </div>
      }
    </>
  );
};

export default CheckGroup;
