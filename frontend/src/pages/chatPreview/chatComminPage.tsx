
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import qs from 'qs';
import React, { useEffect, useMemo, useRef, useState } from "react";
import { useParams, useHistory } from 'react-router-dom';
import useSearchParams from "@/shared/hooks/useSearchParams";
import { AippContext } from '../aippIndex/context';
import ChatPreview from './index';
import { useAppDispatch, useAppSelector } from "@/store/hook";
import { TENANT_ID } from "@/pages/chatPreview/components/send-editor/common/config";
import { findConfigValue } from "@/shared/utils/common";
import { setAppInfo } from "@/store/appInfo/appInfo";

// 公共参数，公共聊天界面
const CommonChat = (props: any) => {
  const { contextProvider, previewBack, showElsa, pluginName = 'default' } = props;
  const iframeRef = useRef<HTMLIFrameElement | null>(null);
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const isDebug = useAppSelector((state) => state.commonStore.isDebug);
  const pluginList = useAppSelector((state) => state.chatCommonStore.pluginList);

  const history = useHistory();
  const dispatch = useAppDispatch();

  const { uid } = useParams();
  const isPreview = useMemo(() => !!uid, [uid]);

  const searchParams = useSearchParams();
  const [plugin, setPlugin] = useState();

  useEffect(() => {
    const found = pluginList.find((item: any) => item.name === pluginName);
    setPlugin(found);
  }, [pluginList, pluginName]);

  const iframeUrl = useMemo(() => {
    let url = plugin?.url;
    const hasSearch = url?.includes('?');
    const search = qs.stringify(searchParams);
    if (search) {
      url += hasSearch ? `&${search}` : `?${search}`
    }
    return url;
  }, [plugin, isPreview, searchParams]);

  const handleReady = () => {
    sendMessageToIframe();
  };

  const handleBack = () => {
    if (isPreview) {
      history.goBack();
    } else {
      history.push({
        pathname: '/app'
      });
    }
  };

  const handleNavigate = (data: any) => {
    const search = new URLSearchParams(history.location.search);
    Object.entries(data.params).forEach(([key, value]) => {
      if (value === null) {
        search.delete(key);
      } else {
        search.set(key, String(value));
      }
    });

    history.push({
      search: search.toString()
    });
  };

  useEffect(() => {
    const handler = (e: { data: string }) => {
      const data = e.data;
      if (data.type === 'ready') {
        handleReady();
      } else if (data.type === 'back') {
        handleBack();
      } else if (data.type === 'navigate') {
        handleNavigate(data);
      }
    };
    window.addEventListener('message', handler);
    return () => window.removeEventListener('message', handler);
  });

  // 给iframe的对话界面传递参数
  const sendMessageToIframe = () => {
    let params = {
      tenantId: TENANT_ID,
      appId: appInfo.id,
      useMemory: findConfigValue(appInfo, 'memory').memorySwitch,
      isDebug
    }
    iframeRef.current?.contentWindow.postMessage({...params}, '*');
  }

  useEffect(() => {
    return () => {
      dispatch(setAppInfo({}));
    };
  }, []);

  return (
    (plugin && !showElsa)
      ? <iframe
        ref={iframeRef}
        src={iframeUrl}
        allowFullScreen
        allow="clipboard-read; clipboard-write"
        style={ {border: 'none', height: '100%'} }
        width="100%"
        onLoad={sendMessageToIframe}
      />
      : <AippContext.Provider value={{ ...contextProvider }}>
        <ChatPreview previewBack={previewBack} />
      </AippContext.Provider>
  )
};

export default CommonChat;
