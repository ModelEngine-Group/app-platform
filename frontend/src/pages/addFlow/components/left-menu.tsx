/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import BasicItems from './basic-item';
import ToolItems from './tool-item';
import EvaluationItems from './evaluation-item';
import ArrowImg from '@/assets/images/ai/arrow.png';
import { useTranslation } from 'react-i18next';

/**
 * 应用编排左侧菜单组件
 *
 * @return {JSX.Element}
 * @param dragData 左侧拖拽菜单列表
 * @param menuClick 菜单点击回调
 * @constructor
 */
const LeftMenu = (props) => {
  const { t } = useTranslation();
  const { dragData, menuClick, evaluateData, type } = props;
  const [activeKey, setActiveKey] = useState('basic');
  const tab = [
    { name: t('basic'), key: 'basic' },
    type === 'evaluate' && { name: t('evaluation'), key: 'evaluation'},
    { name: t('plugin'), key: 'plugin' }
  ]
  const handleClick = (key) => {
    setActiveKey(key);
  }
  return <>{(
    <div className='content-left '>
      <div className='tool-modal-tab'>
        {tab.map(item => {
          return (
            <span className={activeKey === item.key ? 'active' : null}
              key={item.key}
              onClick={() => handleClick(item.key)}
            >
              <span className='text'>{item.name}</span>
              {item && <span className='line'></span>}
            </span>
          )
        })
        }
        <div className='arrow-icon' onClick={menuClick}>
          <img src={ArrowImg} />
        </div>
      </div>
      {
        activeKey === 'basic' ?
          <BasicItems dragData={dragData.basic || []} /> :
          activeKey === 'evaluation' ? 
          <EvaluationItems dragData={evaluateData} /> :
          <ToolItems />
      }
    </div>
  )}</>
};


export default LeftMenu;
