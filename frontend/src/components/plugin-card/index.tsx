/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { Tag, message, Modal, Drawer, Dropdown } from 'antd';
import { EllipsisOutlined, StarOutlined, UserOutlined } from '@ant-design/icons';
import { useHistory } from 'react-router';
import { IconMap, PluginCardTypeE, PluginStatusTypeE, PluginCnType } from '@/pages/plugin/helper';
import { deletePluginAPI } from '@/shared/http/plugin';
import { setSpaClassName } from '@/shared/utils/common';
import Detail from '@/pages/plugin/detail/detail';
import { useTranslation } from 'react-i18next';
import knowledgeImg from '@/assets/images/knowledge/plugin.png';
import userImg from '@/assets/images/ai/user.jpg';
import './style.scss';

/**
 * 插件卡片组件
 *
 * @return {JSX.Element}
 * @param cardInfo  插件详情数据
 * @param cardType  卡片类型
 * @param getPluginList  获取插件列表
 * @param pluginId  插件ID
 * @param pluginRoot  是否为父插件
 * @param readOnly  是否只读
 * @constructor
 */
const PluginCard = ({ pluginData, cardType, getPluginList, pluginId, pluginRoot, readOnly }: any) => {
  const { t } = useTranslation();
  const [isShow, setIsShow] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useHistory().push;

  const items = [
    {
      label: t('delete'),
      key: '1',
      onClick: () => setDeleteOpen(true)
    },
  ]
  // 插件点击详情
  const pluginCardClick = () => {
    pluginRoot ? navigate(`/plugin/detail/${pluginId}`) : setIsShow(true);
  };
  
  const confirm = () => {
    setLoading(true);
    deletePluginAPI(pluginId).then((res) => {
      setLoading(false);
      if (res.code === 0) {
        getPluginList();
        setDeleteOpen(false);
        message.success(t('deleteSuccess'));
      }
    }).catch(() => {
      setLoading(false);
    });
  }

  const setPluginName = () => {
    let name = '';
    name = pluginRoot ? pluginData.pluginName : pluginData?.name;
    return name;
  }
  
  return (
    <>
      <div className={setSpaClassName('page-plugin-card')} onClick={pluginCardClick}>
        <div className='plugin-card-header'>
          <img src={knowledgeImg} />
          <div className='header-content'>
            <div className='header-name'>
              <div className='text' title={setPluginName()}>
                {setPluginName()}
              </div>
            </div>
            <div className='plugin-card-user'>
              <img width="18" height="18" src={userImg} alt="" />
              <span className='creator'>{pluginData.creator}</span>
              {pluginData?.tags?.map((tag: string, index: number) => {
                 if (tag.trim().length > 0) {
                  return <Tag style={{ margin: 0 }} key={index}>{tag}</Tag>
                 }
              })}
            </div>
          </div>
        </div>
        <div className='card-content'>
          {pluginRoot ? pluginData.extension.description : pluginData?.description}
        </div>
        {/* 卡片底部 */}
        <div className='card-footer' style={{ position: 'relative' }}>
          <div hidden>
            <div className='card-footer-content'>
              <span hidden={cardType === PluginCardTypeE.MARKET}>
                <Tag className='footer-type'>Tag 1</Tag>
              </span>
              <span>
                <UserOutlined style={{ marginRight: 8 }} />
                {pluginData?.downloadCount}
              </span>
              <span>
                <StarOutlined style={{ marginRight: 8 }} />
                {pluginData?.likeCount}
              </span>
            </div>
          </div>
          <div hidden={cardType !== PluginCardTypeE.MARKET}>
            <div className='card-footer-right'>
              {IconMap[pluginData?.source?.toUpperCase()]?.icon}
              <span>
                {IconMap[pluginData?.source?.toUpperCase()]?.name}
              </span>
            </div>
          </div>
          { !readOnly && !(!pluginRoot || pluginData?.isBuiltin) && 
            <div className='footer-icon' onClick={(e) => {e.stopPropagation()}}>
              <Dropdown menu={{ items }} trigger={['click']}>
                <EllipsisOutlined className='footer-more' />
              </Dropdown>
            </div> 
          }
        </div>
        {/* 卡片状态 */}
        { (pluginData.deployStatus && !pluginData.isBuiltin) && 
        <span className={['plugin-tag', PluginStatusTypeE[pluginData.deployStatus]].join(' ')}>
          {PluginCnType[pluginData.deployStatus]}
        </span>}
      </div>
       <Drawer
          width={800}
          open={isShow}
          onClose={() => setIsShow(false)}
        >
        <Detail pluginData={pluginData} />
      </Drawer>
      <Modal
        title={t('deletePlugin')}
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
          <span>{t('deletePluginTips')}</span>
        </div>
      </Modal>
    </>
  );
}
export default PluginCard;
