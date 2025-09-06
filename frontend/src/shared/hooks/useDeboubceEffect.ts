/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import { useCallback, useEffect } from 'react';

/**
 *
 * @param effect debounce回调函数
 * @param deps effect触发的依赖
 * @param delay debounce时间间隔，默认300ms
 */
const useDebouncedEffect = (effect, deps, delay = 300): void => {
  const callback = useCallback(effect, deps);

  useEffect(() => {
    const handler = setTimeout(() => {
      callback();
    }, delay);
    return (): void => {
      clearTimeout(handler);
    };
  }, [callback, delay]);
};

export default useDebouncedEffect;
