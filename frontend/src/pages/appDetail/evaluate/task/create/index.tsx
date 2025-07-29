/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Modal, Form, Input } from 'antd';
import { getEvaluateId } from '../../../../../shared/http/appEvaluate';
import { useHistory, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const CreateModal = (props: any) => {
  const { t } = useTranslation();
  const { isShow, setIsShow, detailInfo } = props;
  const [form] = Form.useForm();
  const { tenantId, appId } = useParams();
  const navigate = useHistory().push;

  const onOK = async () => {
    const validate = await form.validateFields();
    const res: any = await getEvaluateId(tenantId, appId, {
      name: validate.username,
      description: validate.description,
      icon: detailInfo?.attributes?.icon,
      greeting: detailInfo?.attributes?.greeting,
      app_type: detailInfo?.attributes?.app_type,
      type: 'evaluate',
      app_category: detailInfo?.appCategory,
      app_built_type: detailInfo?.appBuiltType,
    });
    if (res.code === 0) {
      navigate({
        pathname: `/app-develop/${tenantId}/add-flow/${res?.data.id}`,
        search: `?type=evaluate&appId=${appId}`,
      });
    }
  };

  const onCancel = () => {
    setIsShow(false);
    form.resetFields();
  };
  return (
    <>
      <Modal
        title={t('createEvaluate')}
        onOk={onOK}
        open={isShow}
        onCancel={onCancel}
        className='create-evaluation'
      >
        <Form form={form}>
          <Form.Item
            label={t('name')}
            name='username'
            rules={[{ required: true }]}
            style={{ marginBottom: '16px' }}
          >
            <Input allowClear placeholder={t('plsEnterName')} />
          </Form.Item>
          <Form.Item label={t('describe')} name='description' rules={[{ required: true }]}>
            <Input.TextArea
              rows={4}
              allowClear
              placeholder={t('plsEnterEvaluateDescription')}
              maxLength={512}
              showCount
            />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default CreateModal;
