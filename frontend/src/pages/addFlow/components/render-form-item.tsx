/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect } from 'react';
import { Input, Form, InputNumber, Switch, Select } from 'antd';
import { useTranslation } from 'react-i18next';

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
  const { type, name, label, isRequired, form, appearance } = props;

  const displayType = appearance?.displayType;
  const actualType = displayType || type;

  const getCustomLabel = (typeName) => (
    <span className='debug-form-label'>
      <span className='item-name'>{label}</span>
    </span>
  );

  const validateNumber = (value, isInteger) => {
    if (value === undefined || value === null || value === '') return Promise.resolve();
    if (isNaN(value)) return Promise.reject(new Error(t('plsEnterValidNumber')));
    const min = appearance?.minValue ?? (isInteger ? -999999999 : -999999999.99);
    const max = appearance?.maxValue ?? (isInteger ? 999999999 : 999999999.99);
    if (value < min || value > max) return Promise.reject(new Error(t(isInteger ? 'integerValidateTip' : 'numberValidateTip')));
    return Promise.resolve();
  };

  const handleNumberItemBlur = (value, isInteger) => {
    if (isNaN(value) || value === '') {
      form.setFieldValue(name, null);
    } else {
      form.setFieldValue(name, isInteger ? Number(value) : Number(Number(value).toFixed(2)));
    }
  };

  const handleStringItemBlur = (value) => {
    if (value === '') form.setFieldValue(name, null);
  };

  const isRequiredRule = { required: isRequired !== false, message: t('plsEnter') };

  // 初始化表单值
  useEffect(() => formInit(), []);
  useEffect(() => formInit(), [actualType]);
  const formInit = () => {
    if (actualType === 'Boolean' || actualType === 'switch') {
      form.setFieldValue(name, false);
    }
    if (actualType === 'multiselect') {
      form.setFieldValue(name, []);
    } else {
      form.setFieldValue(name, null);
    }
  };

  // 如果没有 appearance，回退到原来的 type 渲染逻辑
  if (!appearance) {
    switch (type) {
      case 'String':
        return (
          <Form.Item
            name={name}
            label={getCustomLabel(type)}
            rules={[{ required: isRequired !== false, message: t('plsEnterString') }]}
            className='debug-form-item'
          >
            <Input.TextArea placeholder={t('plsEnter')} showCount rows={3} onBlur={(e) => handleStringItemBlur(e.target.value)} />
          </Form.Item>
        );
      case 'Integer':
      case 'Number':
        const isInteger = type === 'Integer';
        return (
          <Form.Item
            name={name}
            label={getCustomLabel(type)}
            rules={[{ required: isRequired !== false, message: t(`plsEnter${type}`) }, { validator: (_, value) => validateNumber(value, isInteger) }]}
            className='debug-form-item'
          >
            <InputNumber
              style={{ width: '100%' }}
              step={isInteger ? 1 : 0.01}
              precision={isInteger ? undefined : 2}
              onBlur={(e) => handleNumberItemBlur(e.target.value, isInteger)}
            />
          </Form.Item>
        );
      case 'Boolean':
        return (
          <Form.Item
            name={name}
            label={getCustomLabel(type)}
            rules={[{ required: isRequired !== false, message: t('plsChoose') }]}
            className='debug-form-item'
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>
        );
      default:
        return null;
    }
  }

  // 有 appearance，走新的 actualType 渲染逻辑
  return (
    <>
      {actualType === 'textInput' && (
        <Form.Item name={name} label={getCustomLabel(actualType)} rules={[isRequiredRule]} className='debug-form-item'>
          <Input placeholder={t('plsEnter')} maxLength={appearance?.maxLength || 500} showCount onBlur={(e) => handleStringItemBlur(e.target.value)} />
        </Form.Item>
      )}
      {actualType === 'numberInput' && (
        <Form.Item name={name} label={getCustomLabel(actualType)} rules={[isRequiredRule, { validator: (_, value) => validateNumber(value, false) }]} className='debug-form-item'>
          <InputNumber
            style={{ width: '100%' }}
            step={0.01}
            precision={2}
            min={appearance?.minValue ?? -999999999.99}
            max={appearance?.maxValue ?? 999999999.99}
            onBlur={(e) => handleNumberItemBlur(e.target.value, false)}
          />
        </Form.Item>
      )}
      {actualType === 'Integer' && (
        <Form.Item name={name} label={getCustomLabel(actualType)} rules={[isRequiredRule, { validator: (_, value) => validateNumber(value, true) }]} className='debug-form-item'>
          <InputNumber
            style={{ width: '100%' }}
            step={1}
            parser={(value) => value.replace(/[^\d-]/g, '')}
            formatter={(value) => (value === '0' ? value : `${Math.floor(value) || ''}`)}
            onBlur={(e) => handleNumberItemBlur(e.target.value, true)}
          />
        </Form.Item>
      )}
      {(actualType === 'Boolean' || actualType === 'switch') && (
        <Form.Item name={name} label={getCustomLabel(actualType)} rules={[isRequiredRule]} className='debug-form-item' valuePropName="checked">
          <Switch />
        </Form.Item>
      )}
      {actualType === 'dropdown' && (
        <Form.Item name={name} label={getCustomLabel(actualType)} rules={[isRequiredRule]} className='debug-form-item'>
          <Select allowClear placeholder={t('plsChoose')}>
            {(appearance?.options || []).map((opt, idx) => (
              <Select.Option key={`${opt}-${idx}`} value={opt}>
                {opt}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
      )}
      {actualType === 'multiselect' && (
        <Form.Item name={name} label={getCustomLabel(actualType)} rules={[isRequiredRule]} className='debug-form-item'>
          <Select placeholder={t('plsChoose')} mode={'multiple'}>
            {(appearance?.options || []).map((opt, idx) => (
              <Select.Option key={`${opt}-${idx}`} value={opt}>
                {opt}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
      )}
    </>
  );
};

export default RenderFormItem;
