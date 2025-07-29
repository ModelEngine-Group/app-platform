/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { CloseOutlined, ToTopOutlined } from '@ant-design/icons';
import { Button, Drawer, Form, Input, Upload } from 'antd';
import TextArea from 'antd/es/input/TextArea';
import { useHistory } from 'react-router';
import { useAppSelector } from '@/store/hook';
import serviceConfig from '@/shared/http/httpConfig';
import { createAipp, uploadImage } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { fileValidate, formEnv } from '@/shared/utils/common';
import { useTranslation } from 'react-i18next';
import { convertImgPath } from '@/common/util';
import knowledgeImg from '@/assets/images/knowledge/knowledge-base.png';
import '../style.scoped.scss';

const CreateWorkfowDrawer = (props) => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const { AIPP_URL } = serviceConfig;
  const [form] = Form.useForm();
  const [filePath, setFilePath] = useState(undefined);
  const [fileName, setFileName] = useState(undefined);
  const [imgPath, setImgPath] = useState('');
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const navigate = useHistory().push;

  // 上传图片
  async function pictureUpload(file) {
    const headers = {
      'attachment-filename': encodeURI(file.name || ''),
    };
    try {
      const formData = new FormData();
      formData.append('file', file);
      const res = await uploadImage(tenantId, formData, headers);
      if (res.code === 0) {
        setFileName(res.data.file_name);
        setFilePath(res.data.file_path);
        let path = `${AIPP_URL}/${tenantId}/file?filePath=${res.data.file_path}&fileName=${res.data.file_name}`;
        convertImgPath(path).then(res => {
          setImgPath(res);
        });
      }
    } catch (err) {
      Message({ type: 'error', content: err.message || t('uploadImageFail') });
    }
  }

  const beforeUpload = (file) => false;
  const onChange = ({ file }) => {
    let validateResult = fileValidate(file);
    validateResult && pictureUpload(file);
  };
  useEffect(() => {
    if (props.openSignal > 0) {
      form.resetFields();
      setOpen(true);
    }
  }, [props?.openSignal]);

  return (
    <Drawer
      title={t('createWorkflow')}
      placement='right'
      closeIcon={false}
      onClose={() => setOpen(false)}
      width={520}
      open={open}
      maskClosable={false}
      extra={
        <CloseOutlined
          onClick={() => {
            setOpen(false);
          }}
        />
      }
      footer={
        <div className='drawer-footer'>
          <Button
            style={{ width: 90 }}
            onClick={() => {
              setOpen(false);
            }}
          >
            {t('cancel')}
          </Button>
          <Button
            style={{ width: 90, backgroundColor: '#2673e5', color: '#ffffff' }}
            onClick={async () => {
              await form.validateFields();
              const icon = filePath && `${formEnv() ? '/appbuilder' : '/api/jober'}/v1/api/${tenantId}/file?filePath=${filePath}&fileName=${fileName}`
              const res = await createAipp(tenantId, 'df87073b9bc85a48a9b01eccc9afccc3', { type: 'waterFlow', name: form.getFieldValue('name'), icon: icon, description: form.getFieldValue('description'), app_built_type: 'workflow', app_category: 'workflow' });
              const aippId = res.data.id;
              sessionStorage.setItem('add-type', 'plugin');
              navigate(`/app-develop/${tenantId}/add-flow/${aippId}?type=workFlow`);
              setOpen(false);
            }}
          >
            {t('ok')}
          </Button>
        </div>
      }
    >
      <div>
        <Form form={form} layout='vertical' autoComplete='off' className='edit-form-content'>
          <Form.Item label={t('icon')} name='icon'>
            <div className='avatar'>
              {imgPath ? (
                <img
                  className='img-send-item'
                  src={imgPath}
                />
              ) : (
                  <img src={knowledgeImg} />
                )}
              <Upload
                beforeUpload={beforeUpload}
                onChange={onChange}
                showUploadList={false}
                accept='.jpg,.png,.gif,.jpeg'
              >
                <Button icon={<ToTopOutlined />}>{t('uploadManually')}</Button>
              </Upload>
            </div>
          </Form.Item>
          <Form.Item
            label={t('name')}
            name='name'
            rules={[
              { required: true, message: t('plsEnterName') },
              {
                type: 'string',
                max: 64,
                message: t('enterNameRule'),
              },
            ]}
          >
            <Input showCount maxLength={64} />
          </Form.Item>
          <Form.Item label={t('describe')} name='description'>
            <TextArea rows={3} showCount maxLength={300} />
          </Form.Item>
        </Form>
      </div>
    </Drawer>
  );
};

export default CreateWorkfowDrawer;
