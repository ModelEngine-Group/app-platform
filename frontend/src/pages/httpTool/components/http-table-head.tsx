/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';

const HttpTableTitle = (props) => {
  const { title, required } = props;
  return (
    <>
      {
        <div className='http-table-head'>
          <span>{title}</span>
          {required && <span className='required'>*</span>}
        </div>
      }
    </>
  );
};

export default HttpTableTitle;
