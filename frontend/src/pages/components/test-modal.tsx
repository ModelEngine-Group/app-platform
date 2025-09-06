/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useImperativeHandle } from 'react';
import { Modal, Button } from 'antd';
import { useTranslation } from 'react-i18next';

/**
 * 应用发布未调试提示弹窗
 *
 * @return {JSX.Element}
 * @param testRef 组件引用
 * @param handleDebugClick 点击确定调试回调
 * @param type 调试类型（应用，工具流）
 * @constructor
 */
const TestModal = (props) => {
  const { t } = useTranslation();
  const { testRef, handleDebugClick, type } = props;
  const [isModalOpen, setIsModalOpen] = useState(false);

  const showModal = () => {
    setIsModalOpen(true);
  };
  // 取消
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  // 确定调试
  const handleOK = () => {
    setIsModalOpen(false);
    handleDebugClick();
  }

  useImperativeHandle(testRef, () => {
    return {
      'showModal': showModal
    }
  })
  return <>{(
    <Modal
      title={type ? t('debugTip') : t('debugTip2')}
      width='380px'
      maskClosable={false}
      centered
      open={isModalOpen}
      onOk={handleOK}
      onCancel={handleCancel}
      footer={[
        <Button key='back' onClick={handleCancel}>
          {t('cancel')}
        </Button>,
        <Button key='test' type='primary' onClick={handleOK}>
          {t('ok')}
        </Button>
      ]}>
      { type ? <p>{t('testTip')}</p> : <p>{t('testTip2')}</p>}
    </Modal>
  )}</>
};

export default TestModal;
