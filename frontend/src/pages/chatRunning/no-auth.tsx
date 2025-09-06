/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Icons } from '@/components/icons';
import { useTranslation } from 'react-i18next';
import { userLogOut } from '@/shared/http/aipp';
import './no-auth.scss';

/**
 * 预览应用没有权限时提示组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const NoAuth = () => {
  const { t } = useTranslation();

  // 点击跳转登录
  const loginClick = async () => {
    await userLogOut();
    let url = `${window.location.origin}/SSOSvr/logout`;
    window.location.href = url
  }
  return (
    <div className="no-page bg">
      <Icons.emptyIcon />
      <div className="page-desc">
        { t('common_noPermission_label') }
      </div>
      <div className="page-sub-desc">
        { t('common_noPermission_desc') }
        <span className="page-sub-link" onClick={loginClick}>
          { t('common_gotoLogin_label')}
        </span>
      </div>
    </div>
  );
};

export default NoAuth;
