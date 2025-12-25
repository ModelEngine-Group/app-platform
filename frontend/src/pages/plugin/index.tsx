/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { Drawer, Button } from 'antd';
import { setSpaClassName, getCookie } from '@/shared/utils/common';
import { QuestionCircleOutlined, CloseOutlined } from '@ant-design/icons';
import { useAppSelector } from '@/store/hook';
import { useTranslation } from 'react-i18next';
import MarketItems from './market';
import DeployMent from './deployment';

/**
 * 应用插件页面
 *
 * @return {JSX.Element}
 * @constructor
 */
const Plugin = () => {
  const { t } = useTranslation();
  const readOnly = useAppSelector((state) => state.chatCommonStore.readOnly);
  const [open, setOpen] = useState(false);
  const [reload, setReload] = useState(false);
  const onClose = () => {
    setOpen(false);
  };
  const confirm = () => {
    setOpen(false);
    setReload(!reload);
  }
  // 部署时获取列表
  const setDeployOpen = () => {
    setOpen(true);
    setReload(!reload);
  }
  return (
    <div className={setSpaClassName('app-fullpage')} style={{ display: 'flex', flexDirection: 'column' }}>
      <div className='aui-header-1 '>
        <div className='aui-title-1'>
          {t('pluginManagement')}
        </div>
        { !readOnly && <Button type='primary' onClick={setDeployOpen}>{t('deploying')}</Button> }
      </div>
      <div>
        <MarketItems reload={reload} readOnly={readOnly}/>
      </div>
      <Drawer
        title={t('pluginDeploying')}
        width={900}
        onClose={onClose}
        closeIcon={false}
        open={open}
        destroyOnClose
        extra={
          <CloseOutlined onClick={() => setOpen(false)} />
        }
        footer={null}
      >
        <DeployMent cancle={() => setOpen(false)} confirm={confirm} />
      </Drawer>
    </div>
  )
}
export default Plugin;
