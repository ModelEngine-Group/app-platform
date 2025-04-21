/*************************************************请勿修改或删除该文件**************************************************/
import React, { useState, useEffect } from 'react';
import { inIframe, getQueryParams } from './utils/index';
import { DataContext } from './context';
import Form from './components/form';

export default function App() {
  const [receiveData, setReceiveData] = useState<any>({});
  const uniqueId = getQueryParams(window.location.href);

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
        const height = entry.contentRect.height > 900 ? 900 : entry.contentRect.height;
        window.parent.postMessage({ type: 'app-engine-form-resize', height, uniqueId }, "*");
      });
    });
    ro.observe(document.querySelector('#custom-smart-form'));
    
    return () => {
      if (inIframe()) {
        window.removeEventListener('message', handleMessage);
      }

      ro.unobserve(document.querySelector('#custom-smart-form'));
      ro.disconnect();
    };
  }, []);

  return (
      <div
          className="form-wrap"
          id="custom-smart-form"
      >
          <DataContext.Provider value={{ ...receiveData, terminateClick, resumingClick, restartClick }}>
              <Form />
          </DataContext.Provider>
      </div>
  );
}
