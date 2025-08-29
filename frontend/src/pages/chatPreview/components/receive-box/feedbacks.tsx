/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { Modal, Input } from 'antd';
import { LikeIcon, UnlikeIcon, LikeSelectIcon, UnlikeSelectIcon } from '@/assets/icon';
import { feedbacksRq, updateFeedback, deleteFeedback } from '@/shared/http/chat';
import {
  guestModeFeedbacksRq,
  guestModeUpdateFeedback,
  guestModeDeleteFeedback,
} from '@/shared/http/guest';
import { useAppSelector } from '@/store/hook';
import { useTranslation } from 'react-i18next';
import './styles/feedbacks.scss';

const Feedbacks = ({ instanceId, feedbackStatus, refreshFeedbackStatus }) => {
  const { t } = useTranslation();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [textValue, setTextValue] = useState('');
  const isGuest = useAppSelector((state) => state.appStore.isGuest);
  const handleOk = async () => {
    setIsModalOpen(false);
    unLikeClickConfirm();
  };
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  const unLikeClickConfirm = async () => {
    if (feedbackStatus === -1 || feedbackStatus === undefined) {
      let params = { usrFeedback: '1', usrFeedbackText: textValue, instanceId: instanceId };
      isGuest ? await guestModeFeedbacksRq(params) : await feedbacksRq(params);
    } else if (feedbackStatus === 1) {
      isGuest ? await guestModeDeleteFeedback(instanceId) : await deleteFeedback(instanceId);
    } else {
      let data = { usrFeedback: '1', usrFeedbackText: textValue };
      isGuest
        ? await guestModeUpdateFeedback(instanceId, data)
        : await updateFeedback(instanceId, data);
    }
    refreshFeedbackStatus(instanceId);
  };
  // 点赞
  const likeClick = async () => {
    if (feedbackStatus === -1 || feedbackStatus === undefined) {
      let params = { usrFeedback: '0', usrFeedbackText: '', instanceId: instanceId };
      await feedbacksRq(params);
    } else if (feedbackStatus === 0) {
      await deleteFeedback(instanceId);
    } else {
      let data = { usrFeedback: '0', usrFeedbackText: '' };
      await updateFeedback(instanceId, data);
    }
    refreshFeedbackStatus(instanceId);
  };
  // 点踩
  const unLikeClick = () => {
    setTextValue('');
    if (feedbackStatus !== 1) {
      setIsModalOpen(true);
    } else {
      unLikeClickConfirm();
    }
  };
  const onChange = (e) => {
    setTextValue(e.target.value);
  };
  return (
    <>
      {
        <div className='feed-inner'>
          <div className='feed-left'> {t('receiveTips')}</div>
          <div className='feed-right'>
            {feedbackStatus !== 0 && <LikeIcon title={t('like')} onClick={likeClick} />}
            {feedbackStatus === 0 && <LikeSelectIcon title={t('like')} onClick={likeClick} />}
            {feedbackStatus !== 1 && <UnlikeIcon title={t('unLike')} onClick={unLikeClick} />}
            {feedbackStatus === 1 && <UnlikeSelectIcon title={t('unLike')} onClick={unLikeClick} />}
          </div>
          <Modal
            title={t('feedback')}
            open={isModalOpen}
            onOk={handleOk}
            onCancel={handleCancel}
            centered
          >
            <Input.TextArea
              rows={4}
              placeholder={t('plsEnter')}
              value={textValue}
              onChange={onChange}
            />
          </Modal>
        </div>
      }
    </>
  );
};

export default Feedbacks;
