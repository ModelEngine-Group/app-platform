/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Modal, Button } from 'antd';
import { CopyUrlIcon } from '@/assets/icon';
import { useTranslation } from "react-i18next";
import { toClipboard } from '@/shared/utils/common';
import IframeImg from '@/assets/images/ai/iframe.png';
import './iframe-modal.scss';

/**
 * 第三方嵌入app弹窗
 *
 * @param deleteOpen 抽屉是否打开.
 * @param url 应用访问链接.
 * @param setDeleteOpen 抽屉是否打开的方法.
 * @return {JSX.Element}
 * @constructor
 */
const Index = ({ deleteOpen, setDeleteOpen, url }) => {
  const { t } = useTranslation();
   // 复制
   const copyClick = (url) => {
    const str = `<iframe 
      src=${url}
      style-"width: 100%; height: 100%; min-height: 700px" 
      frameborder="g" 
      allow="microphone"> 
    </iframe>` 
    toClipboard(str);
  }
  return (
    <Modal
      title={t('iframeTip')}
      open={deleteOpen}
      centered
      width='900px'
      okText={t('ok')}
      onCancel={() => setDeleteOpen(false)}
      footer={
        <div>
          <Button onClick={() => setDeleteOpen(false)}>{t('close')}</Button>
        </div>}
    >
      <div className='appengine-iframe-box'>
        <div><img src={IframeImg} /></div>
        <div className='iframe-code'>
          <div className='iframe-title'>
            <span>{t('iframeTitle')}</span>
            <span>
            <CopyUrlIcon onClick={() => copyClick(url)} style={{ cursor: 'pointer' }} />
            </span>
          </div>
          <div className='code'>
            { `<iframe 
                  src=${url}
                  style="width: 100%; height: 100%; min-height: 700px; border: none;"
                > 
                </iframe>` 
            }
          </div>
        </div>
      </div>
    </Modal>
  );
};

export default Index;
