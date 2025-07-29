/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

/*************************************************此处为人工表单示例***************************************************/
/*********************************************data为表单的初始化入参数据***********************************************/
/*******************************************terminateClick为调用终止对话接口的回调方法**********************************/
/**************************************resumingClick为调用继续对话接口的回调方法***************************************/
/***************************************restartClick为调用重新对话接口的回调方法**************************************/
import {Button, Input, Radio, Select, Switch, Checkbox} from 'antd';
import React, {useContext, useEffect, useState} from 'react';
import {DataContext} from "../context";
import '../styles/form.scss';

const SmartForm = ({onSubmit, onCancel}) => {

  const buildFormSchema = (parameters, data) => {
    const map = {
      Input: 'input',
      Radio: 'radio',
      Switch: 'switch',
      Select: 'select',
      CheckBox: 'checkBox',
      Label: 'label',
    };
    return parameters.map((param) => {
      // renderType 与本 SmartForm 中需要的 type 映射
      let fieldType = map[param.renderType] || map['Input'];

      // 若 data 中存在 name + '-options'，则作为可选项
      const dynamicKey = `${param.name}-options`;
      const dynamicOptions = data[dynamicKey] || [];

      return {
        name: param.name,
        label: param.displayName,
        type: fieldType,
        options: dynamicOptions,
      };
    });
  };

  const {data} = useContext(DataContext);

  const [formData, setFormData] = useState({});
  const [buttonsDisabled, setButtonsDisabled] = useState(false);


  // // 初始化表单数据
  useEffect(() => {
    if (!data) return;
    data.schema?.parameters?.forEach(item => {
      const dynamicKey = `${item.name}-options`;
      const dynamicOptions = data.data[dynamicKey] || [];
      if (['Select', 'Radio'].includes(item.renderType) && !data.data[item.name]) {
        data.data[item.name] = dynamicOptions[0] || '';
      }
    });
    setFormData(data.data);
  }, [data]);

  if (!data) return (<div></div>);

  const formSchema = buildFormSchema(data.schema.parameters, formData);

  const handleChange = (name, value) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const isImageUrl = (url) => {
    const hasImageExtension = ['.jpg', '.jpeg', '.png', '.gif', '.webp'].some(ext =>
      url.toLowerCase().includes(ext)
    );

    const hasDimensions = /$\d+×\d+$/.test(url);

    const hasImageServicePattern = /(fw\d+|thumbnail|webp)/i.test(url);

    return hasImageExtension || hasDimensions || hasImageServicePattern;
  };

  const useCorrectRenderComponent = (key, value) => {
    if (typeof value !== 'string') {
      return <span>{String(value)}</span>;
    }

    const isUrl = value.startsWith('http://') || value.startsWith('https://');

    const isImage = isUrl && isImageUrl(value);

    if (isImage) {
      return (
        <div style={{ margin: '10px 0'}}>
          <img
            src={value}
            alt={key}
            style={{
              maxWidth: '100%',
              maxHeight: '600px',
              border: '1px solid #eee',
              borderRadius: '4px'
            }}
            onError={(e) => {
              (e.target as HTMLImageElement).style.display = 'none';
            }}
          />
        </div>
      );
    }

    return (
      <span style={{
        wordBreak: 'break-word',
        whiteSpace: 'pre-wrap'
      }}>
      {value}
    </span>
    );
  };

  const renderField = (field) => {
    switch (field.type) {
      case 'input':
        return (
          <Input
            style={{width: '100%', maxWidth: '900px'}}
            value={formData[field.name] || ''}
            onChange={(e) => handleChange(field.name, e.target.value)}
          />
        );
      case 'radio': {
        const options = field.options || [];
        return (
          <Radio.Group
            style={{display: 'flex', flexDirection: 'column'}}
            value={formData[field.name] || ''}
            onChange={(e) => handleChange(field.name, e.target.value)}
          >
            {options.map((opt) => (
              <Radio
                key={opt}
                value={opt}
                style={{margin: '6px 0'}}
              >
                {useCorrectRenderComponent(opt, opt)}
              </Radio>
            ))}
          </Radio.Group>
        );
      }
      case 'select': {
        const options = field.options || [];
        return (
          <Select
            style={{width: '100%', maxWidth: '900px'}}
            value={formData[field.name] || ''}
            onChange={(val) => handleChange(field.name, val)}
          >
            {options.map((opt) => (
              <Select.Option key={opt} value={opt}>
                {useCorrectRenderComponent(opt, opt)}
              </Select.Option>
            ))}
          </Select>
        );
      }
      case 'switch':
        return (
          <Switch
            checked={formData[field.name] || false}
            onChange={(checked) => handleChange(field.name, checked)}
          />
        );
      case 'checkBox':
        const options = field.options || [];
        return (
          <Checkbox.Group
            style={{display: 'flex', flexDirection: 'column'}}
            value={formData[field.name] || []}
            onChange={(checkedValues) => handleChange(field.name, checkedValues)}
          >
            {options.map((opt) => (
              <Checkbox
                key={opt}
                value={opt}
                style={{margin: '6px 0'}}
              >
                {useCorrectRenderComponent(opt, opt)}
              </Checkbox>
            ))}
          </Checkbox.Group>
        );
      case 'label':
        return (useCorrectRenderComponent(field.name, formData[field.name])) as React.ReactNode;  // 明确指定返回类型
      default:
        return null;
    }
  };

  const submitForm = () => {
    setButtonsDisabled(true);
    // 在提交时过滤掉所有以 '-options' 结尾的 key
    const dataToSubmit = Object.keys(formData).reduce((acc, key) => {
      if (!key.endsWith('-options')) {
        acc[key] = formData[key];
      }
      return acc;
    }, {});

    onSubmit(dataToSubmit);
  };
  const cancelForm = () => {
    setButtonsDisabled(true);
    onCancel();
  };
  return (
    <div style={{padding: '0 24px'}}>
      {formSchema.map((field) => (
        <div key={field.name} className='filed-node'>
          <div className='filed-node-label'>{field.label}</div>
          {renderField(field)}
        </div>
      ))}
      <div className='form-btn'>
        <Button type="primary" style={{marginRight: '16px'}} disabled={buttonsDisabled} onClick={submitForm}>
          提交
        </Button>
        <Button disabled={buttonsDisabled} onClick={cancelForm}>
          取消
        </Button>
      </div>
    </div>
  );
};

export default SmartForm;
