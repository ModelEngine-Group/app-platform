/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Button, Dropdown } from 'antd';
import { userLogOut } from '@/shared/http/aipp';
import { useTranslation } from 'react-i18next';

/**
 * 预览用户信息展示组件
 *
 * @return {JSX.Element}
 * @param login 是否登录
 * @constructor
 */
const Login = ({ login }) => {
  const { t } = useTranslation();
  const currentUser = localStorage.getItem('currentUser') || '';
  const items = [
    {
      key: '1',
      label: (
        <span>{t('logout')}</span>
      ),
    },
  ]
  const loginOut = async () => {
    await userLogOut();
    let url = `${window.location.origin}/SSOSvr/logout`;
    window.location.href = url
  }
  const loginClick = async () => {
    let url = `${window.location.origin}/SSOSvr/login`;
    window.location.href = url
  }
  return(
    <div className='appengine-login'>
      { 
        login ? <Dropdown trigger='click' placement='bottomRight' menu={{ items,  onClick: loginOut }}>
        <span style={{ cursor: 'pointer' }}>{currentUser}</span>
      </Dropdown> : 
        <Button onClick={loginClick}>{t('login')}</Button> 
      }
    </div>
  )
};

export default Login;
