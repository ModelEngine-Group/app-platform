/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect } from 'react';
import { Input, Form, InputNumber, Switch } from 'antd';
import { useTranslation } from 'react-i18next';
const QUESTION_NAME = 'Question';

/**
 * 调试表单渲染
 *
 * @return {JSX.Element}
 * @param type 表单类型.
 * @param name 表单key.
 * @param label 表单名称.
 * @param isRequired 是否必填.
 * @param form 表单引用.
 * @constructor
 */
const RenderFormItem = (props) => {
  const { t } = useTranslation();
  const { type, name, label, isRequired, form } = props;

  useEffect(() => {
    formInit();
  }, []);

  useEffect(() => {
    formInit();
  }, [type])

  // 初始化数据
  const formInit = () => {
    if (type === 'Boolean') {
      form.setFieldValue(name, false);
      return;
    }
    form.setFieldValue(name, null);
  }

  const customLabel = (
    <span className='debug-form-label'>
      <span className='item-name'>{label}</span>
      <span className='item-type'>{type}</span>
    </span>
  );
    
  const validateNumber = (value, isInteger) => {
    if (value === undefined || value === null || value === '') {
      return Promise.resolve();
    }
    if (isNaN(value)) {
      return Promise.reject(new Error(t('plsEnterValidNumber')));
    }
    if (isInteger && (value < -999999999 || value > 999999999)) {
      return Promise.reject(new Error(t('integerValidateTip')));
    }
    if (!isInteger && (value < -999999999.99 || value > 999999999.99)) {
      return Promise.reject(new Error(t('numberValidateTip')));
    }
    return Promise.resolve();
  };

  const handleNumberItemBlur = (value, isInteger) => {
    if (isNaN(value)) {
      form.setFieldValue(name, null);
      form.validateFields([name]);
    } else if (value === '') {
      form.setFieldValue(name, null);
    } else {
      let inputNumber = isInteger ? value : Number(value).toFixed(2);
      form.setFieldValue(name, Number(inputNumber));
    }
  }

  const handleStringItemBlur = (value) => {
    if (value !== '') {
      return;
    }
    form.setFieldValue(name, null);
  }

  return <>
    {type === 'String' &&
      <Form.Item
        name={name}
        label={customLabel}
        rules={[
          { required: isRequired !== false, message: t('plsEnterString') },
        ]}
        className='debug-form-item'
      >
        <Input.TextArea
          placeholder={`${t('plsEnter')}`}
          showCount
          rows={3}
          onBlur={(e) => handleStringItemBlur(e.target.value)}
        />
      </Form.Item>
    }
    {type === 'Integer' &&
      <Form.Item
        name={name}
        label={customLabel}
        initialValue={null}
        rules={[
          { required: isRequired !== false, message: t('plsEnterInt') },
          { validator: (_, value) => validateNumber(value, true) }
        ]}
        className='debug-form-item'
      >
        <InputNumber
          step={1}
          maxLength={11}
          style={{ width: '100%' }}
          placeholder={`${t('plsEnter')}${name}`}
          onBlur={(e) => handleNumberItemBlur(e.target.value, true)}
          parser={(value) => value.replace(/[^\d-]/g, '')} // 仅允许数字和负号
          formatter={(value) => {
            if (value === '0') {
              return value;
            }
            return `${Math.floor(value) || ''}`;
          }
          } // 强制显示整数
        />
      </Form.Item>
    }
    {type === 'Number' &&
      <Form.Item
        name={name}
        label={customLabel}
        initialValue={null}
        rules={[
          { required: isRequired !== false, message: t('plsEnterNumber') },
          { validator: (_, value) => validateNumber(value, false) }
        ]}
        className='debug-form-item'
      >
        <InputNumber
          maxLength={14}
          step={0.01}
          precision={2}
          style={{ width: '100%' }}
          placeholder={`${t('plsEnter')}${name}`}
          onBlur={(e) => handleNumberItemBlur(e.target.value, false)}
        />
      </Form.Item>
    }
    {type === 'Boolean' &&
      <Form.Item
        name={name}
        label={customLabel}
        rules={[
          { required: isRequired !== false, message: t('plsChoose') },
        ]}
        className='debug-form-item'
      >
        <Switch />
      </Form.Item>
    }
  </>
}


export default RenderFormItem;
