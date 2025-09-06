/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

/*************************************************请勿修改或删除该文件**************************************************/
import React, { useState, useEffect } from 'react';
import { getQueryParams } from './utils/index';
import { DataContext } from './context';
import SmartForm from "./components/form";

export default function App() {

  const [receiveData, setReceiveData] = useState<any>({});
  const uniqueId = getQueryParams(window.location.href);

  // 终止会话
  const terminateClick = (params: any) => {
    window.parent.postMessage({ type: 'app-engine-form-terminate',...params, uniqueId }, receiveData.origin);
  }

  // 继续会话
  const resumingClick = (params: any) => {
    window.parent.postMessage({ type: 'app-engine-form-resuming', ...params, uniqueId }, receiveData.origin);
  }

  // 重新生成
  const restartClick = (params: any) => {
    window.parent.postMessage({ type: 'app-engine-form-restart', ...params, uniqueId }, receiveData.origin);
  }

  useEffect(() => {
    window.addEventListener('message', function(event) {
      if (window.self !== top && event.data) {
        setReceiveData(event.data);
      }
    });
    const ro = new ResizeObserver(entries => {
      entries.forEach(entry => {
        const height = entry.contentRect.height;
        window.parent.postMessage({  type: 'app-engine-form-resize', height, uniqueId }, "*");
      });
    });
    ro.observe(document.querySelector('#custom-smart-form'));
    return () => {
      ro.unobserve(document.querySelector('#custom-smart-form'));
      ro.disconnect();
    };
  }, []);

  return (
    <div className='layout' id="custom-smart-form">
      <DataContext.Provider value={{ ...receiveData, terminateClick, resumingClick, restartClick}}>
        <SmartForm
          onSubmit={data => {
            resumingClick({"params" : data});
          }}
          onCancel={terminateClick}
        />
      </DataContext.Provider>
    </div>
  )
};
