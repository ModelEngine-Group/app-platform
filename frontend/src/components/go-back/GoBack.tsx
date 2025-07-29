/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { useHistory } from 'react-router-dom';
import { Icons } from '../icons';

const GoBack: React.FC<{ path, title }> = ({ path, title }) => {
  const navigate = useHistory().push;
  return (
    <div
      onClick={(): void => {
        navigate(path);
      }}
      style={{ display: 'flex' }}
    >
      <div style={{ cursor: 'pointer' }}><Icons.Left /></div>
      <span style={{ marginLeft: 8, marginTop: -3 }}>{title}</span>
    </div>
  )
};

export default GoBack;
