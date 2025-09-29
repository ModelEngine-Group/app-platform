/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import {
  Modal,
  Form,
  Input,
  Select,
  InputNumber,
  Checkbox,
  Button,
  Row,
  Col,
  Space,
  Switch,
} from 'antd';
import {
  UnorderedListOutlined,
  FileTextOutlined,
  NumberOutlined,
  CheckSquareOutlined,
  SwitcherOutlined,
  DeleteOutlined,
  PlusOutlined,
} from '@ant-design/icons';
import { v4 as uuidv4 } from 'uuid';
import { useTranslation } from 'react-i18next';

const { Option } = Select;

const InputParamModal = (props) => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const { showModal, setShowModal, onSubmit, mode, modalRef } = props;
  const [fieldType, setFieldType] = useState('input');

  // 字段类型配置
  const fieldTypes = [
    { value: 'input', label: t('inputType'), icon: <FileTextOutlined />, type: 'String' },
    { value: 'number', label: t('numberType'), icon: <NumberOutlined />, type: 'Number' },
    { value: 'dropdown', label: t('dropdownType'), icon: <UnorderedListOutlined />, type: 'String' },
    { value: 'switch', label: t('switchType'), icon: <SwitcherOutlined />, type: 'Boolean' },
    { value: 'multiselect', label: t('multiselectType'), icon: <CheckSquareOutlined />, type: 'Array[String]' },
  ];

  const requiredValue = Form.useWatch('required', form);
  const visibleValue = Form.useWatch('visible', form);
  const maxLength = Form.useWatch(['appearance', 'maxLength'], form);
  const dropdownOptions = Form.useWatch(['appearance', 'options'], form);
  const maxValue = Form.useWatch(['appearance', 'maxValue'], form);
  const minValue = Form.useWatch(['appearance', 'minValue'], form);

  useEffect(() => {
    if (mode === 'edit' && modalRef.current) {
      const defaultValue = modalRef.current.selectedParam;
      form.setFieldsValue({
        fieldType: defaultValue.appearance?.displayType,
        tableName: defaultValue.name,
        displayName: defaultValue.displayName,
        defaultValue: defaultValue.value,
        required: defaultValue.isRequired,
        visible: defaultValue.isVisible,
        appearance: defaultValue.appearance,
      });
      setFieldType(defaultValue.appearance?.displayType || 'input');
    } else if (mode === 'add') {
      // 添加模式：重置表单到初始值
      form.resetFields();
      setFieldType('input');
    }
  }, [showModal, mode]);

  useEffect(() => {
    if (requiredValue && !visibleValue) {
      form.setFieldValue('visible', true);
    }
  }, [requiredValue, form, visibleValue]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      const fieldTypeItem = fieldTypes.find((ft) => ft.value === values.fieldType);
      const type = fieldTypeItem?.type || 'String';
      const finalType = type.includes('Array') ? 'Array' : type;

      const transformed = {
        id: mode === 'add' ? `input_${uuidv4()}` : modalRef.current.selectedParam.id,
        name: values.tableName,
        type: finalType,
        value: getDefaultValueByType(values.defaultValue, finalType),
        displayName: values.displayName,
        isRequired: values.required,
        isVisible: values.visible,
        disableModifiable: false,
        appearance: {
          displayType: fieldTypeItem?.value || 'input',
          ...values.appearance,
        },
      };

      function getDefaultValueByType(value, type) {
        if (value !== undefined && value !== null && value !== '') {
          return value;
        }
        if (type === 'Array') {
          return [];
        } else {
          return '';
        }
      }

      setShowModal(false);
      form.resetFields();
      onSubmit(transformed);
    } catch (error) {
      console.log('验证失败:', error);
    }
  };

  const handleCancel = () => {
    setShowModal(false);
    form.resetFields();
  };

  const handleFieldTypeChange = (value) => {
    setFieldType(value);
    form.setFieldsValue({
      appearance: {}, // 清空所有 appearance 配置
      defaultValue: undefined,
    });
  };

  const getTypeByValue = (value) => {
    const field = fieldTypes.find((item) => item.value === value);
    return field?.type; // 如果找不到会返回 undefined
  };

  // 根据字段类型渲染不同的配置项
  const renderFieldSpecificOptions = () => {
    switch (fieldType) {
      case 'input':
        return (
          <>
            <Form.Item label={t('maxLength')} name={['appearance', 'maxLength']}>
              <InputNumber placeholder={t('plsEnter')} min={1} style={{ width: '100%' }} />
            </Form.Item>

            <Form.Item label={t('defaultValue')} name='defaultValue'>
              <Input placeholder={t('plsEnter')} maxLength={maxLength || undefined} />
            </Form.Item>
          </>
        );

      case 'dropdown':
        return (
          <>
            <Form.List name={['appearance', 'options']} initialValue={['']}>
              {(fields, { add, remove }) => (
                <>
                  {fields.map(({ key, name, ...restField }, index) => (
                    <Form.Item
                      key={key}
                      label={`${t('option')} ${index + 1}`}
                      {...restField}
                      name={name}
                      rules={[
                        { required: true, message: t('plsEnterOption') },
                        ({ getFieldValue }) => ({
                          validator(_, value) {
                            if (!value) return Promise.resolve();
                            const options = getFieldValue(['appearance', 'options']) || [];
                            const duplicates = options.filter(
                              (option, idx) => option === value && idx !== name
                            );
                            if (duplicates.length > 0) {
                              return Promise.reject(new Error(t('optionShouldBeUnique')));
                            }
                            return Promise.resolve();
                          },
                        }),
                      ]}
                    >
                      <Input
                        placeholder={`${t('option')} ${index + 1}`}
                        suffix={
                          <DeleteOutlined
                            onClick={() => {
                              if (fields.length > 1) {
                                const options = form.getFieldValue(['appearance', 'options']);
                                const defaultValue = form.getFieldValue('defaultValue');
                                // 如果删除的是默认值，清空默认值
                                if (defaultValue === options[index]) {
                                  form.setFieldValue('defaultValue', '');
                                }
                                remove(name);
                              }
                            }}
                            style={{
                              cursor: fields.length > 1 ? 'pointer' : 'not-allowed',
                              color: fields.length > 1 ? '#2d2f32' : '#8c8c8c',
                              fontSize: '14px',
                            }}
                          />
                        }
                      />
                    </Form.Item>
                  ))}
                  <Form.Item>
                    <Button
                      type='dashed'
                      onClick={() => add()}
                      icon={<PlusOutlined />}
                      style={{ width: '100%' }}
                    >
                      {t('addOption')}
                    </Button>
                  </Form.Item>
                </>
              )}
            </Form.List>

            <Form.Item label={t('defaultValue')} name='defaultValue'>
              <Select placeholder={t('noDefaultValue')} allowClear={true}>
                {dropdownOptions?.map((option, index) => (
                  <Select.Option key={`${option}-${index}`} value={option}>
                    {option}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </>
        );

      case 'number':
        return (
          <>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label={t('minValue')} name={['appearance', 'minValue']}>
                  <InputNumber style={{ width: '100%' }} placeholder={t('plsEnter')} />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label={t('maxValue')} name={['appearance', 'maxValue']}>
                  <InputNumber style={{ width: '100%' }} placeholder={t('plsEnter')} />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item label={t('defaultValue')} name='defaultValue'>
              <InputNumber
                placeholder={t('plsEnter')}
                min={minValue || undefined}
                max={maxValue || undefined}
              />
            </Form.Item>
          </>
        );

      case 'switch':
        return (
          <>
            <Form.Item label={t('defaultValue')} name='defaultValue'>
              <Switch />
            </Form.Item>
          </>
        );

      case 'multiselect':
        return (
          <>
            <Form.List name={['appearance', 'options']} initialValue={['']}>
              {(fields, { add, remove }) => (
                <>
                  {fields.map(({ key, name, ...restField }, index) => (
                    <Form.Item
                      key={key}
                      label={`${t('option')} ${index + 1}`}
                      {...restField}
                      name={name}
                      rules={[
                        { required: true, message: t('plsEnterOption') },
                        ({ getFieldValue }) => ({
                          validator(_, value) {
                            if (!value) return Promise.resolve();
                            const options = getFieldValue(['appearance', 'options']) || [];
                            const duplicates = options.filter(
                              (option, idx) => option === value && idx !== name
                            );
                            if (duplicates.length > 0) {
                              return Promise.reject(new Error(t('optionShouldBeUnique')));
                            }
                            return Promise.resolve();
                          },
                        }),
                      ]}
                    >
                      <Input
                        placeholder={`${t('option')} ${index + 1}`}
                        suffix={
                          <DeleteOutlined
                            onClick={() => {
                              if (fields.length > 1) {
                                const options = form.getFieldValue(['appearance', 'options']);
                                const defaultValue = form.getFieldValue('defaultValue');
                                // 如果删除的是默认值，清空默认值
                                if (defaultValue === options[index]) {
                                  form.setFieldValue('defaultValue', '');
                                }
                                remove(name);
                              }
                            }}
                            style={{
                              cursor: fields.length > 1 ? 'pointer' : 'not-allowed',
                              color: fields.length > 1 ? '#2d2f32' : '#8c8c8c',
                              fontSize: '14px',
                            }}
                          />
                        }
                      />
                    </Form.Item>
                  ))}
                  <Form.Item>
                    <Button
                      type='dashed'
                      onClick={() => add()}
                      icon={<PlusOutlined />}
                      style={{ width: '100%' }}
                    >
                      {t('addOption')}
                    </Button>
                  </Form.Item>
                </>
              )}
            </Form.List>

            <Form.Item label={t('defaultValue')} name='defaultValue'>
              <Select
                placeholder={t('noDefaultValue')}
                mode='multiple'
                showSearch={false}
                showArrow={true}
                allowClear={true}
                normalize={(value) => {
                  if (
                    value === undefined ||
                    value === null ||
                    value === '' ||
                    !Array.isArray(value)
                  ) {
                    return [];
                  }
                  return value;
                }}
              >
                {dropdownOptions?.map((option, index) => (
                  <Select.Option key={`${option}-${index}`} value={option}>
                    {option}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </>
        );

      default:
        return null;
    }
  };

  return (
    <Modal
      title={mode === 'edit' ? '编辑变量' : '添加变量'}
      open={showModal}
      onCancel={handleCancel}
      width={480}
      footer={[
        <Button key='cancel' onClick={handleCancel}>
          {t('cancel')}
        </Button>,
        <Button key='submit' type='primary' onClick={handleSubmit}>
          {t('save')}
        </Button>,
      ]}
      destroyOnClose
    >
      <Form
        form={form}
        layout='vertical'
        initialValues={{
          fieldType: 'input',
          required: true,
          visible: true,
        }}
      >
        {/* 变量类型 */}
        <Form.Item
          label={t('fieldType')}
          name='fieldType'
          rules={[{ required: true, message: '请选择变量类型' }]}
        >
          <Select
            onChange={handleFieldTypeChange}
            suffixIcon={
              <span style={{ fontSize: 12, color: '#999' }}>{getTypeByValue(fieldType)}</span>
            }
          >
            {fieldTypes.map((type) => (
              <Option key={type.value} value={type.value}>
                <Space>
                  {type.icon}
                  {type.label}
                </Space>
              </Option>
            ))}
          </Select>
        </Form.Item>

        {/* 变量名称 */}
        <Form.Item
          label={t('varName')}
          name='tableName'
          rules={[
            { required: true, message: t('plsEnterVarName') },
            {
              pattern: /^[A-Za-z_][A-Za-z0-9_]*$/,
              message: t('formItemNameRule'),
            },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value) {
                  return Promise.resolve(); // 为空的校验由 required 处理
                }

                const existList = modalRef?.current?.existParam || [];

                const isEditMode = mode === 'edit';
                const currentId = modalRef?.current?.selectedParam?.id;
                const currentName = modalRef?.current?.selectedParam?.name;

                const nameExists = existList.some((param) => {
                  if (isEditMode && param.id === currentId) {
                    return false; // 编辑时跳过自身
                  }
                  return param.name === value;
                });

                if (nameExists) {
                  return Promise.reject(new Error(t('attributeVarNameMustBeUnique')));
                }
                return Promise.resolve();
              },
            }),
          ]}
        >
          <Input placeholder={t('plsEnter')} />
        </Form.Item>

        {/* 显示名称 */}
        <Form.Item
          label={t('displayName')}
          name='displayName'
          rules={[
            { required: true, message: t('paramDisplayNameCannotBeEmpty') },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value) {
                  return Promise.resolve(); // 为空的校验由 required 处理
                }

                const existList = modalRef?.current?.existParam || [];

                const isEditMode = mode === 'edit';
                const currentId = modalRef?.current?.selectedParam?.id;
                const currentName = modalRef?.current?.selectedParam?.displayName;

                const nameExists = existList.some((param) => {
                  if (isEditMode && param.id === currentId) {
                    return false; // 编辑时跳过自身
                  }
                  return param.displayName === value;
                });

                if (nameExists) {
                  return Promise.reject(new Error(t('attributeDisplayNameMustBeUnique')));
                }
                return Promise.resolve();
              },
            }),
          ]}
        >
          <Input placeholder={t('plsEnter')} />
        </Form.Item>

        {/* 字段类型特定配置 */}
        {renderFieldSpecificOptions()}

        {/* 参数展示、必填配置 */}
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item name='required' valuePropName='checked'>
              <Checkbox>{t('requiredOrNot')}</Checkbox>
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name='visible' valuePropName='checked'>
              <Checkbox disabled={requiredValue}>{t('displayInDialogConfiguration')}</Checkbox>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};

export default InputParamModal;
