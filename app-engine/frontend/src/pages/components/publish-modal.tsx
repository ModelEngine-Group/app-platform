/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useImperativeHandle, useRef } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import { Input, Modal, Button, Form } from 'antd';
import { Message } from '@/shared/utils/message';
import { appPublish, updateFlowInfo, getVersion } from '@/shared/http/aipp';
import { versionStringCompare } from '@/shared/utils/common';
import { useAppDispatch } from '@/store/hook';
import { setChatId, setChatList } from '@/store/chatStore/chatStore';
import { setTestStatus } from "@/store/flowTest/flowTest";
import { useTranslation } from 'react-i18next';
import { createEvaluate } from '../../shared/http/appEvaluate';
import TextEditor from './text-editor';
import infoImg from '@/assets/images/ai/info.png';
import './styles/publish-modal.scss';

/**
 * 发布应用弹窗组件
 *
 * @return {JSX.Element}
 * @param modalRef  组件引用
 * @param appInfo  应用详情
 * @param publishType  发布应用类型
 * @constructor
 */
const { TextArea } = Input;
const PublishModal = (props) => {
  const { t } = useTranslation();
  const dispatch = useAppDispatch();
  const { modalRef, appInfo, publishType } = props;
  const { appId, tenantId } = useParams();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const editorRef = useRef<any>();
  const navigate = useHistory().push;
  
  const showModal = () => {
    form.setFieldsValue({
      name: appInfo.name,
      description: appInfo.publishedDescription,
      version: appInfo.version,
      app_type: publishType !== 'app' ? 'waterflow' : appInfo.attributes?.app_type
    });
    setIsModalOpen(true);
    getVersion(tenantId, appId, null, 0, 1).then(res => {
      if (res.code === 0) {
        form.setFieldsValue({
          description: res.data[0]?.publishedDescription || ''
        })
      }
    });
  };
  // 发布点击
  function publishClick() {
    appInfo.publishUrl = `/aipp/${tenantId}/chat/${appId}`;
    if (publishType === 'app') {
      publishApp();
    } else if (publishType === 'waterFlow') {
      updateAppRunningFlow();
    } else {
      createEvalute();
    }
  }
  // 发布应用
  async function publishApp() {
    const formParams = await form.validateFields();
    if (appInfo.attributes.latest_version === appInfo.version) {
      Message({ type: 'warning', content: `${t('currentVersion')}${appInfo.version} ${t('cannotBeEarlier')}` });
      return
    }
    if (versionStringCompare(formParams.version || appInfo.version, appInfo.version) === -1) {
      Message({ type: 'warning', content: `${t('currentVersion')}${appInfo.version} ${t('cannotBeEarlier')}` });
      return
    }
    setLoading(true);
    try {
      let textEditor = editorRef.current.handleChange();
      if (textEditor === false) {return};
      let params = JSON.parse(JSON.stringify(appInfo));
      if (window.agent) {
        let graphChangeData = window.agent.serialize();
        params.flowGraph.appearance = graphChangeData;
      }
      params.version = formParams.version || appInfo.version;
      params.attributes.app_type = appInfo.appType;
      params.publishedDescription = formParams.description;
      params.publishedUpdateLog = textEditor;
      params.type = 'app';
      const res:any = await appPublish(tenantId, appId, params);
      if (res.code === 0) {
        Message({ type: 'success', content: t('successReleased') });
        setIsModalOpen(false);
        dispatch(setTestStatus(null));
        dispatch(setChatId(null));
        dispatch(setChatList([]));
        publishType === 'evaluate' ? window.history.back() : navigate(`/app`);
      }
    } finally {
      setLoading(false);
    }
  }
  // 发布工具流
  async function publishWaterFlow() {
    const formParams = await form.validateFields();
    if (versionStringCompare(formParams.version, appInfo.version) === -1) {
      Message({ type: 'warning', content: `${t('currentVersion')}${appInfo.version} ${t('cannotBeEarlier')}` });
      setLoading(false);
      return
    }
    let textEditor = editorRef.current.handleChange();
    if (textEditor === false) {return};
    appInfo.version = formParams.version;
    appInfo.publishedDescription = formParams.description;
    appInfo.publishedUpdateLog = editorRef.current.handleChange();
    try {
      const res = await appPublish(tenantId, appId, appInfo);
      if (res.code === 0) {
        Message({ type: 'success', content: t('successReleased2') });
        sessionStorage.setItem('uniqueName', res.data.tool_unique_name);
        const appEngineId = sessionStorage.getItem('appId');
        if (appEngineId) {
          appEngineId && navigate(`/app-develop/${tenantId}/app-detail/${appEngineId}`);
        } else {
          sessionStorage.setItem('pluginType', 'workflow');
          if (window.location.href.indexOf('type=chatWorkflow') !== -1) {
            navigate(`/app-develop`);
          } else {
            navigate(`/plugin`);
          }
        }
      }
    } finally {
      setLoading(false)
    }
  }
  // 编辑更新应用
  async function updateAppRunningFlow() {
    setLoading(true);
    let params = appInfo.flowGraph;
    const res = await updateFlowInfo(tenantId, appId, params);
    if (res.code === 0) {
      publishWaterFlow();
    } else {
      setLoading(false)
    }
  }
  // 创建评估记录
  const createEvalute = async () => {
    let params = {
      name: appInfo.name,
      description: appInfo.attributes?.description,
      status: 'PUBLISHED',
      appId: window.location.href.split('&')[1].split('=')[1],
      workflowId: appInfo.id,
    };
    const res = await createEvaluate(params);
    if (res.code === 0) {
      publishApp();
    }
  };

  const handleCancel = () => {
    setIsModalOpen(false);
  };

  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  return (
    <>
      {
        <Modal
          title={
            publishType === 'app'
              ? t('releaseApplication')
              : publishType === 'evaluate'
                ? t('releaseEvaluation')
                : t('releaseToolFlow')
          }
          width={800}
          maskClosable={false}
          destroyOnClose
          centered
          open={isModalOpen}
          onOk={publishClick}
          onCancel={handleCancel}
          footer={[
            <Button key='back' onClick={handleCancel}>
              {t('cancel')}
            </Button>,
            <Button key='submit' type='primary' loading={loading} onClick={publishClick}>
              {t('ok')}
            </Button>,
          ]}
        >
          {publishType === 'evaluate' ? (
            <div>{t('releaseTip3')}</div>
          ) : (
            <div>
              <div className='search-list'>
                {appInfo.attributes?.latest_version || appInfo.state === 'active' ? (
                  <div className='publish-tag'>
                    <img src={infoImg} />
                    <span>{t('releaseTip')}</span>
                  </div>
                ) : (
                  <div
                    className='publish-tag'
                    style={{ display: publishType === 'app' ? 'block' : 'none' }}
                  >
                    <img src={infoImg} />
                    <span>{t('releaseTip2')}</span>
                  </div>
                )}
                <Form
                  form={form}
                  layout='vertical'
                  autoComplete='off'
                  className='edit-form-content'
                >
                  <Form.Item
                    label={t('versionName')}
                    name='version'
                    rules={[
                      { required: true, message: t('plsEnter') },
                      { pattern: /^([0-9]+)\.([0-9]+)\.([0-9]+)$/, message: t('versionTip') },
                    ]}
                  >
                    <Input showCount maxLength={8} />
                  </Form.Item>
                  <Form.Item
                    label={t('description')}
                    name='description'
                  >
                    <TextArea rows={4} placeholder={t('plsEnter')} showCount maxLength={300} />
                  </Form.Item>
                  {
                    <Form.Item
                      label={t('updateLog')}
                      name='updateLog'
                    >
                      <TextEditor ref={editorRef} />
                    </Form.Item>
                  }
                </Form>
              </div>
            </div>
          )}
        </Modal>
      }
    </>
  );
};

export default PublishModal;
