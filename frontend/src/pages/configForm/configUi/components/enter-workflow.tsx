/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';

const EnterWorkflow = (props) => {
  const { config, eventConfigs } = props;
  const mashupClick = eventConfigs?.enterWorkflow?.click;
  return <div className='enter-workflow' onClick={mashupClick}>{config.description}</div>;
};

export default EnterWorkflow;