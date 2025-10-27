/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import PropTypes from 'prop-types';
import {Form} from 'antd';
import {useEffect} from 'react';
import {useFormContext} from '@/components/DefaultRoot.jsx';
import {DATA_TYPES, RENDER_TYPE} from '@/common/Consts.js';
import {JadeStopPropagationSelect} from '@/components/common/JadeStopPropagationSelect.jsx';
import {useTranslation} from 'react-i18next';

FormItemFieldType.propTypes = {
  itemId: PropTypes.string.isRequired, // 确保 itemId 是一个必需的字符串
  propValue: PropTypes.string.isRequired, // 确保 propValue 是一个必需的字符串
  disableModifiable: PropTypes.bool.isRequired, // 确保 disableModifiable 是一个必需的bool值
  onChange: PropTypes.func.isRequired, // 确保 onChange 是一个必需的函数
};

/**
 * 表单字段类型。
 *
 * @param itemId 表单字段类型所属Item的唯一标识。
 * @param propValue 类型的初始值。
 * @param disableModifiable 该字段是否禁止修改。
 * @param onChange 值被修改时调用的函数。
 * @param renderType 表单字段类型所属Item对应的渲染类型。
 * @returns {JSX.Element} 入参类型的Dom。
 */
export default function FormItemFieldType({itemId, propValue, disableModifiable, onChange, renderType}) {
  const { t } = useTranslation();
  const form = useFormContext();

  useEffect(() => {
    form.setFieldsValue({ [`type-${itemId}`]: propValue });
  }, [propValue, form]);

  const handleSelectClick = (event) => {
    event.stopPropagation(); // 阻止事件冒泡
  };

  // 根据 不同组件 渲染不同的可选类型
  const renderOptions = () => {
    switch (renderType) {
      case RENDER_TYPE.INPUT:
      case RENDER_TYPE.RADIO:
      case RENDER_TYPE.SELECT:
      case RENDER_TYPE.CHECK_BOX:
      case RENDER_TYPE.LABEL:
        return [
          {value: DATA_TYPES.STRING, label: DATA_TYPES.STRING},
          {value: DATA_TYPES.INTEGER, label: DATA_TYPES.INTEGER},
          {value: DATA_TYPES.NUMBER, label: DATA_TYPES.NUMBER},
        ];
      case RENDER_TYPE.SWITCH:
        return [
          {value: DATA_TYPES.BOOLEAN, label: DATA_TYPES.BOOLEAN},
        ];
      default:
        return [];
    }
  };

  return (<Form.Item
    className="jade-form-item"
    label={t('formItemType')}
    name={`type-${itemId}`}
    rules={[{required: true, message: t('formItemFieldTypeCannotBeEmpty')}]}
    initialValue={propValue}
  >
    <JadeStopPropagationSelect
      className={`jade-select intelligent-form-right-select`}
      value={propValue}
      disabled={disableModifiable}
      style={{width: "100%"}}
      onClick={handleSelectClick} // 点击下拉框时阻止事件冒泡
      onChange={(value) => {
        onChange(itemId, [{key: 'type', value: value}]); // 当选择框的值发生变化时调用父组件传递的回调函数
        document.activeElement.blur();// 在选择后取消焦点
      }}
      options={renderOptions()}
    />
  </Form.Item>);
}