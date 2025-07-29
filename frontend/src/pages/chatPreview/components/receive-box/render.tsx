/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useContext, useEffect, useRef, useState } from 'react';
import { Spin } from 'antd';
import { ChatContext } from '@/pages/aippIndex/context';
import { TENANT_ID } from '../send-editor/common/config';
import { saveContent, saveChart } from "@/shared/http/sse";
import { stopInstance } from '@/shared/http/aipp';
import { formEnv } from '@/shared/utils/common';
import { Message } from "@/shared/utils/message";
import { useTranslation } from 'react-i18next';
import { useAppSelector } from "@/store/hook";
import './styles/render.scss';

/**
 * 自定义表单渲染
 *
 * @return {JSX.Element}
 * @param props 表单配置相关信息.
 * @constructor
 */
const Render = (props: {
  formConfig: {
    formData: any;
    instanceId: any;
    logId: any;
    parentInstanceId: any;
    formAppearance: any;
    startTime: any;
    _internal: any;
    nodeId: any;
    status: any
  };
  funcCallback?: any;
  uniqueId: any;
  path: string;
  formTerminateCallback?: any;
}): JSX.Element => {
  const { t } = useTranslation();
  const { path, uniqueId, funcCallback, formConfig } = props;
  const [loading, setLoading] = useState(false);
  const iframeRef = useRef<any>(null);
  const uniqueIdRef = useRef<any>(null);
  const formConfigRef = useRef<any>({});
  const [iframeHeight, setIframeHeight] = useState(0);
  const isDebug = useAppSelector((state) => state.commonStore.isDebug);
  const { conditionConfirm, handleRejectClar } = useContext(ChatContext);

  // 表单初始化加载
  const handleIframeLoad = () => {
    let params = {
      uniqueId,
      origin: window.location.origin,
      tenantId: TENANT_ID,
      data: formConfigRef.current.formData
    }
    iframeRef.current?.contentWindow.postMessage({ ...params }, '*');
    setLoading(false);
  };
  // 表单通信回调
  const message = async (event: { data: { type?: any; uniqueId?: any; height?: any; }; }) => {
    if (!event.data.type) return
    let iframeKey = event.data?.uniqueId;
    if (String(uniqueIdRef.current) === iframeKey) {
      if (event.data.type === 'app-engine-form-ready') {
        handleIframeLoad();
      } else if (event.data.type === 'app-engine-form-resize') {
        const { height } = event.data;
        height && setIframeHeight(height);
      } else if (event.data.type === 'app-engine-form-terminate') {
        terminateChat(event.data);
      } else if (event.data.type === 'app-engine-form-resuming') {
        resumingChat(event.data);
      } else if (event.data.type === 'app-engine-form-restart') {
        restartChat(event.data)
      }
    }
  }
  // 终止会话
  const terminateChat = async (data) => {
    const { logId, startTime, _internal, nodeId, instanceId, status } = formConfigRef.current;
    if (status === 'ARCHIVED') {
      Message({ type: 'warning', content: t('terminateFormTip') });
      return;
    }
    let params = { ...data, logId, instanceId };
    if (nodeId) {
      params = { ...data, logId, instanceId, startTime, nodeId, _internal }
    }
    if (funcCallback) {
      let res: any = await stopInstance(TENANT_ID, instanceId, params);
      if (res.code === 0) {
        funcCallback();
      } else {
        Message({ type: 'error', content: t('terminateFailed') });
      }
    } else {
      handleRejectClar(params);
    }
  }
  // 重新会话
  const restartChat = async (data: { type?: any; uniqueId?: any; height?: any; params?: any; }) => {
    const { status } = formConfigRef.current;
    if (status === 'RUNNING') {
      Message({ type: 'warning', content: t('restartFormTip') });
      return;
    }
    setLoading(true);
    const params = {
      output: {
        ...data.params
      }
    }
    const res: any = await saveChart(TENANT_ID, formConfigRef.current.instanceId, params);
    if (res.status !== 200) {
      Message({ type: 'warning', content: res.msg || t('savingFailed') });
      return;
    }
    funcCallback ? funcCallback(res) : conditionConfirm(res);
    setLoading(false);
  }
  // 继续会话
  const resumingChat = async (data: {
    type?: any;
    uniqueId?: any;
    height?: any;
    params?: any;
  }) => {
    const {
      logId,
      parentInstanceId,
      formAppearance,
      startTime,
      _internal,
      nodeId,
      instanceId,
      status
    } = formConfigRef.current;
    if (status === 'ARCHIVED') {
      Message({ type: 'warning', content: t('resumingFormTip') });
      return;
    }
    let params:any = {
      formAppearance: JSON.stringify(formAppearance),
      formData: JSON.stringify(data.params),
      businessData: {
        parentInstanceId: parentInstanceId,
        output: {
          ...data.params
        },
      }
    };
    if (nodeId) {
      params.businessData.nodeId = nodeId;
      params.businessData._internal = _internal;
      params.businessData.startTime = startTime;
    }
    setLoading(true);
    const res: any = await saveContent(TENANT_ID, instanceId, params, logId, isDebug);
    if (res.status !== 200) {
      Message({ type: 'warning', content: res.msg || t('conversationFailed') });
      return;
    }
    setLoading(false);
    funcCallback ? funcCallback(res) : conditionConfirm(res, logId);
  };
  useEffect(() => {
    setLoading(true);
    window.addEventListener('message', message);
    return () => {
      window.removeEventListener('message', message)
    };
  }, []);
  useEffect(() => {
    uniqueIdRef.current = uniqueId;
    formConfigRef.current = formConfig;
  }, [uniqueId]);
  return <>{(
    <div>
      <div className='recieve'>
        <Spin spinning={loading}>
        <iframe
            ref={iframeRef}
            onLoad={handleIframeLoad}
            key={props.uniqueId}
            className='appengine-remote-frame'
            sandbox='allow-scripts'
            style={{ width: `100%`, height: `${iframeHeight + 60}px`, border: "none" }}
            src={`${origin}/${formEnv() ? 'appbuilder' : 'api/jober'}/static/${path}?uniqueId=${props.uniqueId}`}>
          </iframe>
        </Spin>
      </div>
    </div>)}
  </>
};


export default Render;
