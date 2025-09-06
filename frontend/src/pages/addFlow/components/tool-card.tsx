/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState }from 'react';
import { Drawer, Tag } from 'antd';
import { EyeOutlined } from '@ant-design/icons';
import { useHistory } from 'react-router';
import { setSpaClassName } from '@/shared/utils/common';
import { useAppSelector } from '@/store/hook';
import { getAppInfoByVersion } from '@/shared/http/aipp';
import { useTranslation } from 'react-i18next';
import Detail from '../../plugin/detail/detail';
import knowledgeImg from '@/assets/images/knowledge/knowledge-base.png';
import aiImg from '@/assets/images/ai/2.png';
import workflowImg from '@/assets/images/ai/workflow.png';
import applicationImg from '@/assets/images/ai/application.png';
import userImg from '@/assets/images/ai/user.jpg';
import '../styles/tool-card.scss';

const ToolCard = ({ pluginData, tenantId }: any) => {
  const { t } = useTranslation();
  const [isShow, setIsShow] = useState(false);
  const navigate = useHistory().push;
  const appId = useAppSelector((state) => state.appStore.appId);

  // 类型处理
  const detailClick = async () => {
    if (pluginData.tags.includes('WATERFLOW')) {
      const res = await getAppInfoByVersion(tenantId, pluginData?.runnables?.APP?.appId);
      if (res.data.id) {
        sessionStorage.setItem('appId', appId);
        navigate(`/app-develop/${tenantId}/app-detail/add-flow/${res?.data?.id}`);
      }
    } else {
      setIsShow(true);
    }
  }

  return (
    <div className={setSpaClassName('plugin-card')}>
      <div className='plugin-card-header'>
        {
          pluginData.tags.includes('HUGGINGFACE') ?
            <img src={aiImg} /> :
            <img src={knowledgeImg} />
        }

        <div>
          <div style={{ display: 'flex' }}>
            <div className='tool-name'>
              <span className='text'>{pluginData.name}</span>
              {pluginData.tags.includes('WATERFLOW') || pluginData.tags.includes('HUGGINGFACE') ?
                <img src={workflowImg} alt='' /> :
                <img src={applicationImg} alt='' />}
            </div>
          </div>
          <div className='plugin-card-user'>
            <img width="18" height="18" src={userImg} alt="" />
            <span style={{ marginRight: 8 }}>{pluginData.creator}</span>
            {pluginData.tags.map((tag: string, index: number) =>{
              if (tag.trim().length > 0) {
                return <Tag style={{ margin: 0 }} key={index}>{tag}</Tag>
               }
            })}
          </div>
        </div>
      </div>
      <div className='card-content'>
        {pluginData.description === 'null' ? t('noDescription') : pluginData.description}
      </div>
      {/* 卡片底部 */}
      <div className='card-footer'></div>
      <div className='card-detail' onClick={detailClick}>
        {<EyeOutlined style={{ cursor: 'pointer', fontSize: '14px', color: '#4D4D4D', marginRight: '8px' }} />}
      </div>
      <Drawer
        width={800}
        open={isShow}
        onClose={() => setIsShow(false)}
      >
        <Detail pluginData={pluginData} />
      </Drawer>
    </div >
  )
}

export default ToolCard;
