/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useContext } from 'react';
import { Form, Input, Button } from 'antd';
import { useTranslation } from 'react-i18next';
import { HttpContext } from '../config/context';
import ImgUpload from '@/components/img-upload';

const HttpForm = (props: any) => {
  const { setStepCurrent } = props;
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const { httpInfo, setHttpInfo } = useContext(HttpContext);
  // 点击下一步
  const confirm = async () => {
    const formParams = await form.validateFields();
    httpInfo.schema.name = formParams.name;
    httpInfo.schema.description = formParams.description;
    setHttpInfo(httpInfo);
    setStepCurrent(1);
  };
  useEffect(() => {
    if (httpInfo.schema.name) {
      form.setFieldValue('name', httpInfo.schema.name);
      form.setFieldValue('description', httpInfo.schema.description);
    }
  }, [httpInfo]);
  return (
    <>
      <div className='http-tool-input'>
        <div className='http-form-content'>
          <Form form={form} layout='vertical' autoComplete='off' className='edit-form-content'>
            <Form.Item label={t('iconHttp')} name='icon'>
              <ImgUpload />
            </Form.Item>
            <Form.Item
              label={t('name')}
              name='name'
              rules={[
                { required: true, message: t('plsEnterName') },
                {
                  type: 'string',
                  max: 256,
                  message: t('enterNameRule'),
                },
              ]}
              style={{ margin: '15px 0' }}
            >
              <Input showCount maxLength={256} />
            </Form.Item>
            <Form.Item
              label={t('describe')}
              name='description'
              rules={[
                { required: true, message: t('describeMessage') },
                {
                  type: 'string',
                  max: 100,
                  message: t('describeMessageLength'),
                },
              ]}
              style={{ margin: '15px 0' }}
            >
              <Input.TextArea rows={3} showCount maxLength={100} />
            </Form.Item>
          </Form>
        </div>
        <div className='http-tool-footer'>
          <Button type='primary' onClick={confirm}>
            {t('nextStep')}
          </Button>
        </div>
      </div>
    </>
  );
};

export default HttpForm;
