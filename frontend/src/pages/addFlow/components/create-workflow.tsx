/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Button, Form, Input, Modal, Upload } from 'antd';
import TextArea from 'antd/es/input/TextArea';
import { ToTopOutlined } from '@ant-design/icons';
import { useHistory } from 'react-router';
import { createAipp, uploadImage } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { useAppSelector } from '@/store/hook';
import serviceConfig from '@/shared/http/httpConfig';
import { fileValidate, formEnv } from '@/shared/utils/common';
import { convertImgPath } from '@/common/util';
import { useTranslation } from 'react-i18next';
import knowledgeImg from '@/assets/images/knowledge/knowledge-base.png';

const CreateWorkflow = (props) => {
  const { t } = useTranslation();
  const [openWorkFlow, setOpenWorkFlow] = useState(false);
  const { AIPP_URL } = serviceConfig;
  const [form] = Form.useForm();
  const [filePath, setFilePath] = useState('');
  const [fileName, setFileName] = useState('');
  const [imgPath, setImgPath] = useState('');
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const appId = useAppSelector((state) => state.appStore.appId);
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
    if (!validateResult) {
      setFilePath('');
    }
    validateResult && pictureUpload(file);
  };
  useEffect(() => {
    if (props.createWorkflowSignal > 0) {
      form.resetFields();
      setOpenWorkFlow(true);
    }
  }, [props?.createWorkflowSignal]);
  return (
    <Modal
      title={t('createWorkflow')}
      centered
      open={openWorkFlow}
      onCancel={() => {
        setOpenWorkFlow(false);
      }}
      onOk={async () => {
        await form.validateFields();
        const icon = filePath && `${formEnv() ? '/appbuilder' : '/api/jober'}/v1/api/${tenantId}/file?filePath=${filePath}&fileName=${fileName}`;
        const res = await createAipp(tenantId, 'df87073b9bc85a48a9b01eccc9afccc3', {
          type: 'waterFlow',
          name: form.getFieldValue('name'),
          icon,
          description: form.getFieldValue('description'),
        });
        const aippId = res.data.id;
        navigate(`/app-develop/${tenantId}/app-detail/add-flow/${aippId}`);
        sessionStorage.setItem('appId', appId);
        setOpenWorkFlow(false);
      }}
    >
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
        <Form.Item label={t('description')} name='description' style={{ marginBottom: '30px' }}>
          <TextArea rows={3} showCount maxLength={300} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateWorkflow;
