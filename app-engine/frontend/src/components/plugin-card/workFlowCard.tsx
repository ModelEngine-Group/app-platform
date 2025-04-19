/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect } from 'react';
import { Dropdown, MenuProps, Tag, message } from 'antd';
import { EllipsisOutlined, StarOutlined, UserOutlined } from '@ant-design/icons';
import { useHistory } from 'react-router';
import { useAppSelector } from '@/store/hook';
import { setSpaClassName } from '@/shared/utils/common';
import { getAppInfoByVersion } from '@/shared/http/aipp';
import { deleteAppApi } from '@/shared/http/appDev';
import { useTranslation } from 'react-i18next';
import { convertImgPath } from '@/common/util';
import knowledgeImg from '@/assets/images/knowledge/knowledge-base.png';
import userImg from '@/assets/images/ai/user.jpg';
import './style.scss';

const WorkflowCard = ({ pluginData, type, getWaterFlowList }: any) => {
  const { t } = useTranslation();
  const [imgPath, setImgPath] = useState('');
  const navigate = useHistory().push;
  const tenantId = useAppSelector((state) => state.appStore.tenantId);

  // 删除工具流
  const delWaterflow = async () => {
    const res: any = await deleteAppApi(tenantId, pluginData?.id);
    if(res.code === 0){
      getWaterFlowList();
      message.success(t('deleteSuccess'));
    }
  };

  const operatItems: MenuProps['items'] = [
    {
      label: <div onClick={DropdownItemClick}>{t('arrange')}</div>,
      key: 'choreography',
    },
    {
      label: <div onClick={delWaterflow}>{t('delete')}</div>,
      key: 'delete',
    },
  ];
  async function DropdownItemClick() {
    let id = pluginData?.id;
    if (pluginData?.state === 'active') {
      const res = await getAppInfoByVersion(tenantId, id);
      id = res?.data?.id;
    }
    navigate({
      pathname: `/app-develop/${tenantId}/add-flow/${id}`,
      search: '?type=workFlow',
    });
  }

  const setClassName = () => {
    if (type === 'plugin') {
      return setSpaClassName('page-plugin-card');
    }
    return setSpaClassName('plugin-card');
  }

  // 获取图片
  const getImgPath = async (info) => {
    const res:any = await convertImgPath(info.icon);
    setImgPath(res);
  }

  useEffect(() => {
    pluginData.attributes.icon && getImgPath(pluginData.attributes);
    return () => {
      setImgPath('');
    }
  }, [pluginData.attributes]);

  return (
    <div className={setClassName()}
      onClick={async () => {
        let id = pluginData?.id;
        if (pluginData?.state === 'active') {
          const res = await getAppInfoByVersion(tenantId, id);
          id = res?.data?.id;
        }
        navigate({
          pathname: `/app-develop/${tenantId}/add-flow/${id}`,
          search: '?type=workFlow',
        });
      }}
    >
      <div className='plugin-card-header'>
        {imgPath ? <img src={imgPath} alt='' /> : <img src={knowledgeImg} alt='' />}
        <div className='plugin-header-item'>
          <div className='plugin-title'>
            <div className='plugin-head'>
              <span className='text' title={pluginData?.name}>{pluginData?.name}</span>
              <Tag className='version'>V{pluginData?.version}</Tag>
            </div>
          </div>
          <div className='plugin-card-user'>
            <img width="18" height="18" src={userImg} alt="" />
            <span style={{ marginRight: 8 }}>{pluginData?.createBy}</span>
            {pluginData?.tags?.map((tag: string, index: number) => {
              if (tag.trim().length > 0) {
                return <Tag style={{ margin: 0 }} key={index}>{tag}</Tag>
              }
            })}
          </div>
        </div>
      </div>
      <div className='card-content'>
        {pluginData?.attributes?.description}
      </div>
      {/* 卡片底部 */}
      <div className='card-footer'>
        <div>
          <div className='card-footer-content'>
            <span>
              {
                (pluginData?.attributes?.latest_version || pluginData.state === 'active') ?
                  <Tag color='processing' className='footer-type'>{t('active')}</Tag> :
                  <Tag className='footer-type'>{t('draft')}</Tag>
              }
            </span>
            <span hidden>
              <UserOutlined style={{ marginRight: 8 }} />
              {pluginData?.downloadCount}
            </span>
            <span hidden>
              <StarOutlined style={{ marginRight: 8 }} />
              {pluginData?.likeCount}
            </span>
          </div>
        </div>
        <div onClick={(e) => { e.stopPropagation(); }}>
          <Dropdown menu={{ items: operatItems }} trigger={['click']}>
            <EllipsisOutlined className='footer-more' />
          </Dropdown>
        </div>
      </div>
    </div >
  )
}

export default WorkflowCard;
