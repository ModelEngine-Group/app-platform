/*************************************************请勿修改或删除该文件**************************************************/
import React, { useState, useEffect } from 'react';
import { getQueryParams } from './utils/index';
import { DataContext } from './context';
import Form from './components/form';

export default function App() {
  const [receiveData, setReceiveData] = useState<any>({});
  const uniqueId = getQueryParams(window.location.href);

  // 处理消息
  const handleMessage = (event: any) => {
    if (window.self !== top) {
      setReceiveData(event.data);
    }
  };

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
    window.addEventListener('message', handleMessage);
    window.parent.postMessage({ type: 'app-engine-form-ready', uniqueId }, '*');

    const ro = new ResizeObserver(entries => {
      entries.forEach(entry => {
        const height = entry.contentRect.height;
        window.parent.postMessage({  type: 'app-engine-form-resize', height, uniqueId }, "*");
      });
    });
    ro.observe(document.querySelector('#custom-smart-form'));
    return () => {
      window.removeEventListener('message', handleMessage);

      ro.unobserve(document.querySelector('#custom-smart-form'));
      ro.disconnect();
    };
  }, []);

  return (
    <div className='layout' id="custom-smart-form">
      <DataContext.Provider value={{ ...receiveData, terminateClick, resumingClick, restartClick}}>
        <Form />
      </DataContext.Provider>
    </div>
  )
};