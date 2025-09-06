/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { useMemo } from 'react';
import { useLocation } from 'react-router-dom';

interface AnyObject {
  [key: string]: any;
}

/**
 * 获取当前页面的查询参数
 */
function useSearchParams(): AnyObject {
  const { search } = useLocation();
  return useMemo(() => {
    const urlSearchParams = new URLSearchParams(search);
    const params = {};
    urlSearchParams.forEach((value, key) => {
      params[key] = value;
    });
    return params;
  }, [search]);
}

export default useSearchParams;
