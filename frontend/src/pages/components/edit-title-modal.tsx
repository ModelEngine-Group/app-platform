/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useImperativeHandle, useContext } from 'react';
import { Input, Modal, Form, Button } from 'antd';
import { FlowContext } from '../aippIndex/context';
import { useTranslation } from 'react-i18next';
import './styles/edit-modal.scss';

const EditTitleModal = (props) => {
  const { t } = useTranslation();
  const { modalRef, onFlowNameChange } = props;
  const [form] = Form.useForm();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const { appInfo } = useContext(FlowContext);
  const showModal = () => {
    setIsModalOpen(true);
  };
  useEffect(() => {
    form.setFieldsValue({
      name: appInfo.name,
      description: appInfo.attributes?.description,
      icon: appInfo.attributes?.icon
    })
  }, [isModalOpen])
  const handleOk = async () => {
    const formParams = await form.validateFields();
    setLoading(true);
    onFlowNameChange(formParams);
  };
  const handleCancel = () => {
    setIsModalOpen(false);
    setLoading(false);
  };
  const handleLoading = () => {
    setLoading(false);
  }
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal,
      'handleCancel': handleCancel,
      'handleLoading': handleLoading
    }
  })
  return <>
    {(
      <Modal
        title={t('modifyingBasicInfo')}
        width='600px'
        maskClosable={false}
        open={isModalOpen}
        onOk={handleOk}
        onCancel={handleCancel}
        footer={[
          <Button key='back' onClick={handleCancel}>
            {t('cancel')}
          </Button>,
          <Button key='submit' type='primary' loading={loading} onClick={handleOk}>
            {t('ok')}
          </Button>
        ]}>
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
              rules={[{ required: true, message: t('plsEnter') }, {
                type: 'string',
                max: 64,
                message: `${t('characterLength')}：1 - 64`
              }]}
            >
              <Input maxLength={64} showCount />
            </Form.Item>
            <Form.Item
              label={t('description')}
              name='description'
            >
              <Input.TextArea rows={4} maxLength={300} showCount />
            </Form.Item>
          </Form>
        </div>
      </Modal>
    )}</>
};

export default EditTitleModal;
