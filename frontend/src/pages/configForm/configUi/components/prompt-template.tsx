/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useImperativeHandle, useEffect } from 'react';
import { Drawer, Button, Form, Input } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import GenerateIcon from '@/assets/images/ai/generate_icon.png';
import '../styles/prompt-template.scss';

/**
 * 用户提示词模版抽屉
 *
 * @param promptTemplateRef 当前组件ref.
 * @param promptValue 父组件传递的提示词.
 * @param openGeneratePrompt 打开生成提示词弹框组件的方法.
 * @param updatePromptValue 更新父组件提示词值的方法.
 * @return {JSX.Element}
 * @constructor
 */

const PromptTemplate = ({ promptTemplateRef, promptValue, openGeneratePrompt, updatePromptValue, readOnly }) => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const [open, setOpen] = useState(false);
  const [currentPrompt, setCurrentPrompt] = useState('');

  useImperativeHandle(promptTemplateRef, () => {
    return { openPromptDrawer: () => setOpen(true) };
  });

  // 回填到输入框
  const confirm = () => {
    updatePromptValue(currentPrompt);
    setOpen(false);
  };

  useEffect(() => {
    setCurrentPrompt(promptValue);
  }, [promptValue, open]);

  return <>
    <Drawer
      title={t('userPromptWordTemplate')}
      className='prompt-template'
      maskClosable={false}
      closable={false}
      open={open}
      width={570}
      extra={
        <CloseOutlined
          onClick={() => {
            setOpen(false);
          }}
        />
      }
      footer={
        <div className='drawer-footer'>
          <Button style={{ width: 90 }} onClick={() => setOpen(false)}>
            {t('cancel')}
          </Button>
          <Button type='primary' style={{ width: 90 }} onClick={confirm}>
            {t('ok')}
          </Button>
        </div>
      }
    >
      <Form
        form={form}
        layout='vertical'
        autoComplete='off'
      >
        <Form.Item
          label={t('promptName')}
          name='promptName'
          rules={[{ required: true }]}
        >
          <div className={['generate-btn', readOnly ? 'version-preview' : ''].join(' ')} onClick={() => openGeneratePrompt(true)}>
            <img src={GenerateIcon} alt="" style={{ marginRight: 5 }} />
            <span>{'AI' + t('generate')}</span>
          </div>
          <Input.TextArea
            value={currentPrompt}
            showCount maxLength={2000}
            style={{ height: '100%' }}
            onChange={(e) => setCurrentPrompt(e.target.value)}
          />
        </Form.Item>
      </Form>
    </Drawer>
  </>
};

export default PromptTemplate;
