/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Drawer, Button, Spin } from 'antd';
import { setSpaClassName, getCookie } from '@/shared/utils/common';
import { CloseOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import { getPluginDetail } from '@/shared/http/plugin';
import { PluginCardTypeE } from '../helper';
import { useAppSelector } from '@/store/hook';
import { useTranslation } from 'react-i18next';
import PluginCard from '@/components/plugin-card';
import EmptyItem from '@/components/empty/empty-item';
import DeployMent from '../deployment';
import userImg from '@/assets/images/ai/user.jpg';
import knowledgeImg from '@/assets/images/knowledge/plugin.png';
import leftArrowImg from '@/assets/images/ai/left-arrow.png';
import '../styles/plugin.scss';

/**
 * 插件列表组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const PliginList = (props) => {
  const { t } = useTranslation();
  const readOnly = useAppSelector((state) => state.chatCommonStore.readOnly);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [pluginData, setPluginData] = useState([]);
  const [data, setData] = useState([]);
  const getPluginList = async () => {
    setLoading(true);
    try {
      const res:any = await getPluginDetail(props.match.params.pluginId);
      if (res.code === 0) {
        if (Array.isArray(res.data.pluginToolDataList)) {
          res.data.pluginToolDataList.forEach(item => item.creator = (res.data.creator || ''));
          setPluginData(res.data.pluginToolDataList);
        }
        setData(res.data);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getPluginList();
  }, []);

  return (
    <Spin spinning={loading}>
      <div className={`${setSpaClassName('app-fullpage')} plugin-detail`}>
        <div className='aui-header-1 '>
          <div className='aui-title-1'>
            {t('pluginManagement')}
          </div>
          { !readOnly && <Button type='primary' onClick={() => setOpen(true)}>{t('deploying')}</Button> }
        </div>
        <div className='plugin-detail-list'>
          <div className='list-head'>
            <div className='list-back-icon flex'>
              <img src={leftArrowImg} onClick={() => window.history.back()} />
            </div>
            <div className='list-detail-img flex'>
              <img src={knowledgeImg} />
            </div>
            <div className='list-detail-desc'>
              <div className='desc-top'>
                <span className='name'>{data?.pluginName}</span>
              </div>
              <div className='desc-middle'>
              <span className='user'>
                  <img width="18" height="18" src={userImg} alt="" />
                  {data?.creator}
                </span>
              </div>
            </div>
          </div>
          {pluginData.length > 0 ? (
            <div className='list-content'>
              {pluginData.map((card: any) => (
                <PluginCard
                  key={card.uniqueName}
                  pluginData={card}
                  cardType={PluginCardTypeE.MARKET}
                />
              ))}
            </div>
          ) : (
              <div className='empty-box'>
                <EmptyItem />
              </div>
            )}
        </div>
        <Drawer
          title={t('deployPlugin')}
          width={1000}
          onClose={() => setOpen(false)}
          closeIcon={false}
          open={open}
          destroyOnClose
          extra={<CloseOutlined onClick={() => setOpen(false)} />}
          footer={null}
        >
          <DeployMent cancle={() => setOpen(false)} />
        </Drawer>
      </div>
    </Spin>
  );
};

export default PliginList;
