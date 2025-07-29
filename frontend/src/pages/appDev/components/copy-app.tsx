/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useImperativeHandle } from 'react';
import { useHistory } from 'react-router-dom';
import { Modal, Form, Input } from 'antd';
import { useTranslation } from 'react-i18next';
import { createAipp } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { convertImgPath } from '@/common/util';
import { TENANT_ID } from '@/pages/chatPreview/components/send-editor/common/config';
import UploadImg from '@/components/file-upload';

/**
 * 复制应用
 *
 * @param copyRef 当前组件的ref.
 * @return {JSX.Element}
 * @constructor
 */

const CopyApp = ({ copyRef }) => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const [open, setOpen] = useState(false);
  const [filePath, setFilePath] = useState('');
  const [loading, setLoading] = useState(false);
  const [appInfo, setAppInfo] = useState({});
  const navigate = useHistory().push;

  useImperativeHandle(copyRef, () => {
    return {
      openModal: (appInfo) => dataInit(appInfo),
    };
  });
  
  // 初始化数据
  const dataInit = (appInfo) => {
    setAppInfo(appInfo);
    form.setFieldsValue({
      name: appInfo.name,
      icon: appInfo.attributes?.icon,
    });
    if (appInfo.attributes?.icon) {
      setFilePath(appInfo.attributes.icon);
    }
    setOpen(true);
  }
  // 复制应用
  const handleCopyApp = async () => {
    try {
      setLoading(true);
      const { id, attributes, type, appCategory, appBuiltType } = appInfo;
      const formParams = await form.validateFields();
      const copyParam = {
        name: formParams.name,
        description: attributes.description,
        icon: '',
        app_type: attributes.app_type,
        app_built_type: appBuiltType,
        type,
        id,
        app_category: appCategory
      };
      const res = await createAipp(TENANT_ID, id, copyParam);
      if (res.code === 0) {
        const { data } = res;
        setOpen(false);
        Message({ type: 'success', content: t('operationSucceeded') });
        navigate(`/app-develop/${TENANT_ID}/app-detail/${data.id}/${data.aippId}`);
      } else {
        Message({ type: 'error', content: res.msg || t('copyAppFailed') });
      }
    } finally {
      setLoading(false);
    }
  };

  return <>
    <Modal
      title={t('copyApp')}
      width='526px'
      open={open}
      centered
      onOk={handleCopyApp}
      onCancel={() => setOpen(false)}
      okButtonProps={{ loading }}
      okText={t('ok')}
      cancelText={t('cancel')}
      forceRender
      maskClosable={false}
      className='upload-app'
    >
      <div>
        <Form form={form} layout='vertical' autoComplete='off' className='edit-form-content'>
          <Form.Item label={t('icon')} name='icon'>
            <div className='avatar'>
              <UploadImg 
                appId={appInfo.id}
                icon={appInfo.attributes?.icon}
                uploadSuccess={(path: string) => setFilePath(path)}
              />
            </div>
          </Form.Item>
          <Form.Item
              label={t('name')}
              name='name'
              rules={[
              { required: true, message: t('plsEnter') },
              {
                  type: 'string',
                  max: 64,
                  message: `${t('characterLength')}：1 - 64`,
              },
              ]}
          >
            <Input />
          </Form.Item>
        </Form> 
      </div>
    </Modal>
  </>
};

export default CopyApp;
