/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import { useEffect, useRef } from 'react';

type callbackFn = () => any;

function useSetInterval(callback: callbackFn, deps: any[] = [], delay = 30000): void {
  if (!(callback instanceof Function)) {
    throw new Error('callback 参数必须是函数！');
  }
  if (!(delay === null || typeof delay === 'number')) {
    throw new Error('delay 必须是 null 或者数字！');
  }
  const savedCallback = useRef<callbackFn>(() => null);

  useEffect((): void => {
    savedCallback.current = callback;
  }, [callback]);

  useEffect((): any => {
    if (delay === null || delay === undefined) {
      return null;
    }
    const timer = setInterval(() => {
      if (typeof savedCallback.current === 'function') {
        (savedCallback.current as callbackFn)();
      }
    }, delay);
    return (): void => {
      clearInterval(timer);
    };
  }, [delay, ...deps]);
}

export default useSetInterval;
