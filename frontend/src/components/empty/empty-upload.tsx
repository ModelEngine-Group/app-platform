/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Icons } from '../icons';
import i18n from '../../locale/i18n';

const EmptyUpload = ({ text = i18n.t('noUploadTips') }) => {
  return (
    <>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Icons.emptyIcon />
        <div style={{ margin: '12px 0' }}>{text}</div>
      </div>
    </>
  );
};

export default EmptyUpload;
