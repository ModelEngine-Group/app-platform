/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Drawer, Form, Alert, Spin, Tooltip } from 'antd';
import { StartIcon, CloseIcon, RunIcon } from '@/assets/icon';
import { Message } from '@/shared/utils/message';
import { reTestInstance } from '@/shared/http/aipp';
import { messageProcess } from '../../chatPreview/utils/chat-process';
import { workflowDebug, getTestVersion } from '@/shared/http/sse';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setTestStatus, setTestTime } from "@/store/flowTest/flowTest";
import { EventSourceParserStream } from '@/shared/eventsource-parser/stream';
import { findConfigValue } from "@/shared/utils/common";
import RenderFormItem from './render-form-item';
import RemoteForm from '../../chatPreview/components/receive-box/render';
import UploadFile from '../../chatPreview/components/send-editor/components/upload-file';
import FileList from '../../chatPreview/components/send-editor/components/file-list';
import RunImg from '@/assets/images/ai/run.png';
import { setDimension } from '@/store/common/common';
import { useTranslation } from 'react-i18next';
import { pick } from 'lodash';
import '../styles/flow-test.scss';

/**
 *  流程调试抽屉页。
 *
 * @param debugTypes 开始节点入参元数据。
 * @param setShowDebug 设置展示调试窗口的方法。
 * @param showDebug 是否展示调试窗口。
 * @param elsaRunningCtl elsa调试Ctl。
 * @param setShowFlowChangeWarning 设置是否展示修改告警的方法。
 * @returns {JSX.Element}。
 * @constructor
 */
const Index = (props) => {
  const {
    debugTypes,
    setShowDebug,
    showDebug,
    elsaRunningCtl,
    setShowFlowChangeWarning
  } = props;
  const { t } = useTranslation();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [showFormIcon, setShowFormIcon] = useState(false);
  const [chatConfig, setChatConfig] = useState<any>({});
  const [fileList, setFileList] = useState<any>([]);
  const [multiFileConfig, setMultiFileConfig] = useState<any>({});
  const { tenantId, appId } = useParams();
  const [form] = Form.useForm();
  const timerRef = useRef<any>(null);
  const runningInstanceId = useRef('');
  // 是否监听sse流式输出
  const runningParsing = useRef(false);
  const dispatch = useAppDispatch();
  const runningAippId = useRef();
  const runningVersion = useRef();
  const chatIdRef = useRef<string | undefined>();

  // 关闭测试抽屉
  const handleCloseDebug = () => {
    setLoading(false);
    setShowDebug(false);
  }

  const handleStart = async () => {
    try {
      const values = multiFileConfig.autoChatOnUpload ? form.getFieldsValue() : await form.validateFields();
      runningStart(values);
    } catch (error) {
      Message({ type: 'warning', content: t('plsEnterCorrectDebugParams') });
    }
  };

  const handleRunTest = () => {
    if (!checkFileSuccess()) return;
    setShowFlowChangeWarning(false);
    elsaRunningCtl.current?.reset();
    dispatch(setTestStatus(null));
    dispatch(setTestTime(0));
    handleStart();
  }

  // 校验文件是否都上传成功
  const checkFileSuccess = () => {
    if (fileList.length) {
      if (fileList.find(item => item.uploadStatus === 'failed')) {
        Message({ type: 'error', content: t('uploadFailedTip') });
        return false;
      } else if (fileList.find(item => item.uploadStatus !== 'success')) {
        Message({ type: 'warning', content: t('waitFileSuccessTip') });
        return false;
      }
    }
    return true;
  };

  // 请求参数拼接
  const runningStart = (values) => {
    dispatch(setDimension({ id: '', name: values.dimension, value: values.dimension }));
    const obj = { evalDatasetQuantity: 1, isDebug: true };
    if (fileList.length) {
      obj['$[FileDescription]$'] = fileList.map(item => {
        return pick(item, ['file_name', 'file_url', 'file_type']);
      });
    }
    let chatParams: any = {
      'app_id': appId,
      'question': values.Question,
      'context': {
        'user_context': { ...values, ...obj},
        'dimension': values.dimension
      }
    };
    if (chatIdRef.current) {
      chatParams['chat_id'] = chatIdRef.current;
    }
    handleRun(chatParams);
  };

  // 点击运行
  const handleRun = async (params) => {
    setLoading(true);
    const res: any = await workflowDebug(tenantId, params);
    if (res.status !== 200) {
      Message({ type: 'error', content: res.msg || t('startDebugFail') });
      setLoading(false);
      return;
    }
    testStreaming(res);
  }

  // 流式输出sse数据
  const testStreaming = async (res) => {
    let reader = res?.body?.pipeThrough(new TextDecoderStream())
      .pipeThrough(new EventSourceParserStream()).getReader();
    runningParsing.current = true;
    let getReady = false;
    while (runningParsing.current) {
      const sseResData = await reader?.read()
      const { done, value } = sseResData;
      if (!done) {
        try {
          let msgStr = value.data;
          const receiveData = JSON.parse(msgStr);
          if (!getReady && receiveData.status === 'ERROR') {
            setLoading(false);
            Message({ type: 'error', content: t('startDebugFail') });
            chatIdRef.current = receiveData.chat_id;
            break;
          }
          if (receiveData.status === 'READY' && !getReady) {
            getReady = true;
            runningInstanceId.current = receiveData.instance_id;
            const versionRes = await getTestVersion(tenantId, appId);
            const { aipp_id, version } = versionRes.data;
            if (aipp_id && version) {
              runningAippId.current = aipp_id;
              runningVersion.current = version;
              startDebugMission(aipp_id, version, receiveData.instance_id);
            } else {
              setLoading(false);
              elsaRunningCtl.current && elsaRunningCtl.current.reset();
              Message({ type: 'error', content: t('startDebugFail') });
              break;
            }
          } else {
            sseTestProcess(receiveData);
          }
        } catch (e) {
          console.info(e);
        }
      } else {
        break;
      }
    }
  }

  // 接收表单消息
  const sseTestProcess = (messageData) => {
    try {
      // 普通日志
      messageData.answer?.forEach(log => {
        if (log.type === 'FORM') {
          let obj = messageProcess(runningInstanceId.current, {
            ...log.content,
            log_id: messageData.log_id,
            status: messageData.status
          });
          setChatConfig(obj);
          setOpen(true);
        }
        chatIdRef.current = messageData.chat_id;
      });
    } catch (err) {
      console.info(err);
    }
  }

  // 开始调试
  const startDebugMission = (aippId, version, instanceId) => {
    timerRef.current && clearInterval(timerRef.current);
    handleCloseDebug();
    elsaRunningCtl.current = window.agent.run();
    dispatch(setTestStatus('Running'));
    startTestInstance(aippId, version, instanceId);
  }

  // 测试轮询
  const startTestInstance = (aippId, version, instanceId) => {
    timerRef.current = setInterval(async () => {
      const res: any = await reTestInstance(tenantId, aippId, instanceId, version);
      if (res.code !== 0) {
        onStop(res.msg || t('debugFail'));
        clearInterval(timerRef.current);
      }
      if (res.data === null) {
        elsaRunningCtl.current?.stop([]);
        dispatch(setTestStatus('Error'));
        onStop(t('startFlowFailed'));
      }
      const runtimeData = res.data;
      if (runtimeData) {
        elsaRunningCtl.current?.refresh(runtimeData.nodeInfos);
        const time = (runtimeData.executeTime / 1000).toFixed(3);
        dispatch(setTestTime(time));
        if (isError(runtimeData.nodeInfos)) {
          clearInterval(timerRef.current);
          dispatch(setTestStatus('Error'));
          elsaRunningCtl.current?.stop(runtimeData.nodeInfos);
          setShowFormIcon(false);
        } else if (runtimeData.isFinished) {
          clearInterval(timerRef.current);
          dispatch(setTestStatus('Finished'));
          elsaRunningCtl.current?.stop(runtimeData.nodeInfos);
          setShowFormIcon(false);
        }
      }
    }, 3000);
  }

  // 判断是否流程出错
  const isError = (nodeInfos) => {
    return nodeInfos.some((value) => value.status === 'ERROR');
  }

  // 终止轮询
  const onStop = (content) => {
    clearInterval(timerRef.current);
    Message({ type: 'warning', content: content });
    elsaRunningCtl.current?.stop();
    setShowFormIcon(false);
  }

  // 自定义表单调用方法后回调
  const funcCallback = async (res) => {
    setOpen(false);
    if (res) {
      testStreaming(res);
    } else {
      const res: any = await reTestInstance(tenantId, runningAippId.current, runningInstanceId.current, runningVersion.current);
      clearInterval(timerRef.current);
      elsaRunningCtl.current?.stop(res.data.nodeInfos);
      dispatch(setTestStatus('Terminate'));
    }
    setShowFormIcon(false);
  }

  // 自定义表单关闭
  const testDrawerClose = () => {
    setShowFormIcon(true);
    setOpen(false);
  }

  // 再次打开自定义表单
  const testDrawerOpen = () => {
    setShowFormIcon(false);
    setOpen(true);
  }

  useEffect(() => {
    return () => {
      timerRef.current && clearInterval(timerRef.current);
      runningParsing.current = false;
      setShowFormIcon(false);
    }
  }, [])

  useEffect(() => {
    setMultiFileConfig(findConfigValue(appInfo, 'multimodal') || {});
  }, [appInfo]);

  return <>{(
    <div>
      { showFormIcon && 
      <Tooltip placement='rightTop' title={t('intelligentForm')}>
        <div onClick={testDrawerOpen} className='appengine-run-img'>
          <img width={16} height={16} src={RunImg} alt="" />
        </div> 
      </Tooltip>
      }
      <Drawer title={<h5>{t('debugRun')}</h5>} open={showDebug}
        onClose={handleCloseDebug} width={600}
        maskClosable={false}
        footer={
          <Spin spinning={loading}>
            <div style={{ textAlign: 'right' }}>
              <span onClick={handleRunTest} className='run-btn'>
                <RunIcon />{t('run')}
              </span>
            </div>
          </Spin>
        }
        closeIcon={<CloseIcon />}
      >
        <div className='debug'>
          <Alert message={t('debugAlert')} type='info' />
          <div className='debug-header'>
            <StartIcon className='header-icon' />
            <span className='header-title'>{t('startNode')}</span>
          </div>
          <Form
            form={form}
            layout='vertical'
            className='debug-form'
          >
            {debugTypes.map((debugType, index) => {
              return (
                <RenderFormItem
                  form={form}
                  type={debugType.type}
                  name={debugType.name}
                  label={debugType.displayName}
                  key={index}
                  isRequired={debugType.isRequired}
                  appearance={debugType.appearance}
                />
              )
            })}
            {
              multiFileConfig.useMultimodal &&
              <Form.Item name='fileUrls' label={t('uploadFile')}>
                <UploadFile maxCount={multiFileConfig.maxUploadFilesNum} fileList={fileList} updateFileList={setFileList} />
                <FileList isDebug fileList={fileList} updateFileList={setFileList}></FileList>
              </Form.Item>
            }
          </Form>
        </div>
      </Drawer>
      {/* 表单抽屉 */}
      <Drawer
        title={<h5>{t('intelligentForm')}</h5>}
        open={open}
        keyboard={false}
        maskClosable={false}
        onClose={testDrawerClose} 
        width={1000}
      >
        <div style={{ width: '100%' }}>
          <RemoteForm
            uniqueId={chatConfig.logId}
            path={chatConfig.path}
            formConfig={chatConfig.formConfig}
            funcCallback={funcCallback}
          />
        </div>
      </Drawer>
    </div>
  )}</>
};


export default Index;
