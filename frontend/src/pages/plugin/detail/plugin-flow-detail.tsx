/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { getPluginFlowDetail } from '@/shared/http/plugin';
import Detail from './detail';

const FlowDetail = () => {
  const [data, setData] = useState(null);
  const { pluginId } = useParams();
  const refreshDetail = async () => {
    const res = await getPluginFlowDetail(pluginId);
    setData(res?.data);
  }
  useEffect(() => {
    if (pluginId) {
      refreshDetail();
    }
  }, [pluginId])
  return <>
    <Detail pluginData={data} />
  </>
};


export default FlowDetail;
