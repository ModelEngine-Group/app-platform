/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect } from 'react';
import { Spin, Modal, Button } from 'antd';
import { useTranslation } from 'react-i18next';
import { NewFeatIcon, RocketIcon, FixIcon } from '@/assets/icon';
import { getAnnouncement } from '@/shared/http/apps';
import './infomodal.module.scss';

const InfoModal = () => {
  const { t } = useTranslation();
  const [showModal, setShowModal] = useState(false);
  const [spinning, setSpining] = useState(true);
  const [lastAnnounceTime, setLastAnnounceTime] = useState(localStorage.getItem('lastAnnounceTime') || '2024-06-19 00:00:00');
  const [infoList, setInfoList] = useState([]);

  // 将后端数据转换为可展示的数组
  const handleData = (data) => {
    const res = [];
    const mapper = {
      '新功能': {
        id: 1,
        icon: <NewFeatIcon />,
      },
      '优化': {
        id: 2,
        icon: <RocketIcon />,
      },
      '修复': {
        id: 3,
        icon: <FixIcon />,
      },
    };
    for (const key of Object.keys(data)) {
      const item = {
        icon: mapper[key].icon,
        title: key,
        id: mapper[key].id,
        desc: <div>{
          data[key].map((a, idx) => <p key={idx}>{`${idx + 1}. ${a.content}`}</p>)
        }</div>,
      }
      res.push(item);
    }
    res.sort((a, b) => a.id - b.id);
    setInfoList(res);
  }
  useEffect(() => {
    const fetchData = async () => {
      const res = await getAnnouncement();
      if (!res.announcementsByType) return
      setLastAnnounceTime(res.latestCreateTime);
      handleData(res.announcementsByType);
      const timeNow = Date.parse(res.latestCreateTime);
      const timeOld = Date.parse(lastAnnounceTime || res.latestCreateTime);
      if (timeNow > timeOld) {
        setShowModal(true);
        localStorage.setItem('lastAnnounceTime', res.latestCreateTime);
      }
      setSpining(false);
    }
    fetchData();
  }, []);

  const footer = () => <div className='center' style={{ width: '100%' }}>
    <Button onClick={() => setShowModal(false)}>
      {t('gotIt')}
    </Button>
  </div>

  return (
    <Modal
      title={t('updateLog')}
      open={showModal}
      footer={spinning ? null : footer}
      keyboard
      wrapClassName='modal-bg'
      onCancel={() => setShowModal(false)}
    >
      <div>{spinning ? '' : lastAnnounceTime.split(' ')[0]}</div>
      <hr className='updateDivLine' />
      <div className={spinning ? 'contentSpin' : 'updateContent'}>
        {
          spinning ? <Spin spinning={spinning} /> :
            infoList.map(item => <div className='updateItem' key={item.title}>
              <div className='itemTitle'>{item.icon}{item.title}</div>
              <div className='desc'>
                {item.desc}
              </div>
            </div>)
        }
      </div>
    </Modal>
  )
}

export default InfoModal;