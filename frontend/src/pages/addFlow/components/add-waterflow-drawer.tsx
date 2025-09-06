/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Drawer, Form, Input, Button } from 'antd';
import { useParams, useHistory } from 'react-router-dom';
import { createAipp } from '@/shared/http/aipp';
import { CloseOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';

/**
 * 创建工具流左侧抽屉组件
 *
 * @return {JSX.Element}
 * @param open 抽屉打开关闭属性
 * @param setOpen 设置抽屉是否打开
 * @constructor
 */
const AddWaterFlow = (props) => {
  const { t } = useTranslation();
  const { open, setOpen } = props;
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const { tenantId, appId } = useParams();
  const navigate = useHistory().push;

  useEffect(() => {
    form.setFieldsValue({
      name: '',
      description: ''
    })
  }, [open]);
  const confrimClick = async () => {
    setLoading(true);
    try {
      const formParams = await form.validateFields();
      const res = await createAipp(tenantId, 'df87073b9bc85a48a9b01eccc9afccc3', { type: 'waterFlow', name: formParams.name });
      if (res.code === 0) {
        const aippId = res.data.id;
        navigate(`/app-develop/${tenantId}/app-detail/add-flow/${aippId}`);
      }
    } finally {
      setLoading(false);
    }
  }
  return <>
    <Drawer
      title={t('createWorkflow')}
      placement='right'
      width='420px'
      closeIcon={false}
      onClose={() => setOpen(false)}
      open={open}
      footer={[
        <Button key='back' onClick={() => setOpen(false)}>
          {t('cancel')}
        </Button>,
        <Button key='submit' type='primary' loading={loading} onClick={confrimClick}>
          {t('ok')}
        </Button>
      ]}
      extra={
        <CloseOutlined onClick={() => setOpen(false)} />
      }>
      <div style={{ marginBottom: '30px' }}>
        <Form
          form={form}
          layout='vertical'
          autoComplete='off'
          className='edit-form-content'
        >
          <Form.Item
            label={t('name')}
            name='name'
            rules={[{ required: true, message: t('plsEnterName') }, {
              type: 'string',
              max: 64,
              message: t('enterNameRule')
            }]}
          >
            <Input maxLength={64} showCount />
          </Form.Item>
          <Form.Item
            label={t('description')}
            name='description'
          >
            <Input.TextArea rows={3} showCount maxLength={300} />
          </Form.Item>
        </Form>
      </div>
    </Drawer>
  </>
};


export default AddWaterFlow;
