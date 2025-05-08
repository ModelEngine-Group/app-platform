/*************************************************请勿修改或删除该文件**************************************************/
import React, { useState, useEffect, useRef } from 'react';
import { inIframe, getQueryParams } from './utils/index';
import { DataContext } from './context';
import Form from './components/form';

export default function App() {
  const [receiveData, setReceiveData] = useState<any>({});
  const uniqueId = getQueryParams(window.location.href);
  const formRef = useRef<HTMLDivElement>(null);

  const handleMessage = (event: any) => {
    setReceiveData(event.data);
  };

  const terminateClick = (params: any) => {
    window.parent.postMessage({ type: 'app-engine-form-terminate', ...params, uniqueId }, receiveData.origin);
  };

  const resumingClick = (params: any) => {
    window.parent.postMessage({ type: 'app-engine-form-resuming', ...params, uniqueId }, receiveData.origin);
  };

  const restartClick = (params: any) => {
    window.parent.postMessage({ type: 'app-engine-form-restart', ...params, uniqueId }, receiveData.origin);
  };

  useEffect(() => {
    if (inIframe()) {
      window.addEventListener('message', handleMessage);
      window.parent.postMessage({
        type: 'app-engine-form-ready',
        uniqueId
      }, '*');
    }

    const ro = new ResizeObserver(entries => {
      entries.forEach(entry => {
        const height = entry.contentRect.height;
        window.parent.postMessage({ type: 'app-engine-form-resize', height, uniqueId }, "*");
      });
    });

    if (formRef.current) {
      ro.observe(formRef.current);
    }
    
    return () => {
      if (inIframe()) {
        window.removeEventListener('message', handleMessage);
      }

      if (formRef.current) {
        ro.unobserve(formRef.current);
      }
      ro.disconnect();
    };
  }, []);

  return (
    <div
      className="form-wrap"
      id="custom-smart-form"
      ref={formRef}
    >
      <DataContext.Provider value={{ ...receiveData, terminateClick, resumingClick, restartClick }}>
        <Form />
      </DataContext.Provider>
    </div>
  );
}
