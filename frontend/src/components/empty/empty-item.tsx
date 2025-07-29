/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Icons } from '../icons';
import i18n from '../../locale/i18n';

/**
 * 暂无数据提示插件
 *
 * @param text 名称. 默认显示暂无数据
 * @param IconNode 暂无数据图标.
 * @return {JSX.Element}
 * @constructor
 */
const EmptyItem = ({ text = i18n.t('noData'), iconType = 'normal' }) => {
  return (
    <>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        { iconType === 'normal' ? <Icons.emptyIcon />  : <Icons.emptyUrlIcon /> }
        <div style={{ margin: '12px 0', color: 'rgb(128, 128, 128)' }}>{text}</div>
      </div>
    </>
  );
}

export default EmptyItem;
