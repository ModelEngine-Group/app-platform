/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import EyeImg from '@/assets/images/eye_btn.svg';
import CloseImg from '@/assets/images/close_btn.svg';
import ToolImg from '@/assets/images/ai/tool.png';
import WorkflowImg from '@/assets/images/ai/workflow.png';

const SkillList = (props) => {
  const { t } = useTranslation();
  const { skillList, deleteItem, readOnly } = props;
  const [showOperateIndex, setShowOperateIndex] = useState(-1);
  const { tenantId } = useParams();
  // hover显示操作按钮
  const handleHoverItem = (index, operate) => {
    if (operate === 'enter') {
      setShowOperateIndex(index);
    } else {
      setShowOperateIndex(-1);
    }
  };

  // 工具流详情
  const setLocationUrl = () => {
    if (process.env.PACKAGE_MODE === 'spa') {
      return 'appengine';
    }
    return '#';
  }
  const workflowDetail = (item) => {
    if (item.type === 'workflow') {
      if (item.appId.length) {
        window.open(`${location.origin}/${setLocationUrl()}/app-develop/${tenantId}/app-detail/flow-detail/${item.appId}`);
      }
    } else {
      window.open(`${location.origin}/${setLocationUrl()}/plugin/detail/${item.pluginId}`);
    }
  }

  // 获取详情删除按钮
  const showOperate = (item) => {
    return (<span>
      <img src={EyeImg} alt="" style={{ cursor: 'pointer' }} onClick={() => workflowDetail(item)} />
      <img src={CloseImg} style={{ marginLeft: 16, cursor: 'pointer' }} alt="" onClick={() => handleDelete(item)} />
    </span>);
  };

  const handleDelete = (item) => {
    deleteItem(item);
    setShowOperateIndex(-1);
  };

  return <>
    {
      skillList.length ? skillList.map((item, index) => {
        return (
          <div className='item-container' key={index}>
            <div className='item' onMouseEnter={!readOnly ? () => handleHoverItem(index, 'enter') : undefined} onMouseLeave={!readOnly ? () => handleHoverItem(index, 'leave') : undefined}>
              <span className='item-left'>
                {item.type === 'tool' ?
                  <img src={ToolImg} alt='' /> :
                  <img src={WorkflowImg} alt='' />
                }
                <span className='text'>{item.name || item}</span>
              </span>
              {index === showOperateIndex && showOperate(item)}
            </div>
            {
              item.notExist && <div className='not-exist'>{`${t('tool')}${item.name}${t('selectedValueNotExist')}`}</div>
            }
          </div>
        )
      }) : <div className='no-data'>{t('noData')}</div>
    }
  </>
};

export default SkillList;
