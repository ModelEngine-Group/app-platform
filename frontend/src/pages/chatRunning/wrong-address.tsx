/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import './wrong-address.scss';
import { useTranslation } from 'react-i18next';

/**
 * 公共访问界面地址错误组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const WrongAddress = ({}) => {
  const { t } = useTranslation();
  return (
    <div className='no-access-container'>
      <div className='no-access-content'>
        <div className='no-access-icon'>
          <svg
            viewBox='0 0 24 24'
            width='64'
            height='64'
            stroke='currentColor'
            strokeWidth='2'
            fill='none'
          >
            <circle cx='12' cy='12' r='10'></circle>
            <line x1='4.93' y1='4.93' x2='19.07' y2='19.07'></line>
          </svg>
        </div>
        <h2 className='no-access-title'>{t('wrongAddr')}</h2>
        <p className='no-access-message'>{t('wrongAddrMsg')}</p>
      </div>
    </div>
  );
};

export default WrongAddress;
