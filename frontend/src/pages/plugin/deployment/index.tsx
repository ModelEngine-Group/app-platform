/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import { Modal, Button } from 'antd';
import DeployTable from './deploy-table';
import { getDeployTool, setDeployTool } from '@/shared/http/plugin';
import { Message } from '@/shared/utils/message';
import { useTranslation } from 'react-i18next';
import infoUploadImg from '@/assets/images/ai/info-upload.png';
import '../styles/deployment.scss';

const DeployMent = ({ cancle, confirm }) => {
  const { t } = useTranslation();
  const [disabled, setDisabled] = useState(true);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [deployedNum, setDeployedNum] = useState(0);
  const [pluginNum, setPluginNum] = useState(0);
  const pluginRef = useRef(null);
  // 获取所有部署中的插件
  const getData = async () => {
    const res = await getDeployTool('deploying');
    if (res.code === 0 && res.total === 0) {
      setDisabled(false)
    }
  }
  const confirmSunmit = () => {
    const list = pluginRef.current?.getCheckedList();
    const deployedList = pluginRef.current?.getDeployedList();
    let uninstallNum = deployedList.length;
    const deployedIdList = deployedList.map(item => item.pluginId);
    list.forEach(item => {
      if (deployedIdList.includes(item.pluginId)) {
        uninstallNum -= 1;
      }
    });
    setPluginNum(list.length);
    setDeployedNum(uninstallNum);
    setLoading(false);
    setOpen(true);
  }
  // 确定部署
  const handleOk = async () => {
    const list = pluginRef.current?.getCheckedList();
    let idList = list.map(item => item.pluginId);
    try {
      setLoading(true);
      const res = await setDeployTool({ pluginIds: idList });
      if (res.code === 0) {
        Message({ type: 'success', content: t('operationSucceeded') });
        setOpen(false);
        confirm && confirm();
      }
    } finally {
      setLoading(false);
    }
  };
  const handleCancel = () => {
    if (loading) return;
    setOpen(false);
  }
  useEffect(() => {
    getData();
  }, []);
  return <>
    <div className='engine-deployment'>
      <div className='upload-info-head'>
        <img src={infoUploadImg} />
        <span>{t('pluginTips2')}</span>
      </div>
      <DeployTable pluginRef={pluginRef} />
      <div className='deploy-info-btn'>
        <Button onClick={() => cancle()}>{t('cancel')}</Button>
        <Button type='primary' onClick={confirmSunmit} disabled={disabled}>{t('ok')}</Button>
      </div>
    </div>
    <Modal
      open={open}
      title={t('confirmDeployment')}
      centered
      onCancel={handleCancel}
      footer={[
        <Button onClick={handleCancel}>
          {t('cancel')}
        </Button>,
        <Button type='primary' loading={loading} onClick={handleOk}>
          {t('ok')}
        </Button>
      ]}
    >
      <p>{t('deployTip')} <b>{pluginNum}</b> {t('num')}{t('plugin')}</p>
      {deployedNum > 0 && <p>{t('deployCanceled')} <b>{deployedNum}</b> {t('num')}{t('deployCanceledTips')}</p>}
    </Modal>
  </>
};


export default DeployMent;
