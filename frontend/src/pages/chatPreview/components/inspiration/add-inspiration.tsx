/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef, useImperativeHandle } from 'react';
import { Form, Input, Drawer, Switch, Table, Popover, Select, Button } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';
import { TENANT_ID } from '@/pages/chatPreview/components/send-editor/common/config';
import { uuid } from '@/common/util';
import { useTranslation } from 'react-i18next';
import { addInspiration, editInspiration } from '@/shared/http/aipp';
import { guestModeAddInspiration, guestModeEditInspiration } from '@/shared/http/guest';
import { sourceTypes } from '@/pages/configForm/common/common';
import { Message } from '@/shared/utils/message';
import { useAppSelector } from '@/store/hook';

const AddIns = (props) => {
  const { t } = useTranslation();
  const { addRef, refreshData } = props;
  const appId = useAppSelector((state) => state.appStore.appId);
  const isGuest = useAppSelector((state) => state.appStore.isGuest);
  const [showModal, setShowModal] = useState(false);
  const [cachePromptVar, setCachePromptVar] = useState(null);
  const [promptVar, setPromptVar] = useState([]);
  const [promptVarData, setPromptVarData] = useState([]);
  const [type, setType] = useState('');
  const editId = useRef('');
  const category = useRef('');
  const [modalForm] = Form.useForm();
  const { TextArea } = Input;
  let regex = /{{(.*?)}}/g;
  const formItemLayout = {
    labelCol: { span: 3 },
    wrapperCol: { span: 24 },
  };
  const columns = [
    {
      title: t('variable'),
      dataIndex: 'var',
      key: 'var',
    },
    {
      title: t('varType'),
      dataIndex: 'varType',
      key: 'varType',
    },
    {
      title: t('sourceType'),
      dataIndex: 'sourceType',
      key: 'sourceType',
      render: (sourceType, record) => (
        <>
          <Select
            options={sourceTypes}
            defaultValue={sourceType}
            onChange={(sourceType) => handleTableChange(sourceType, record, 'sourceType')}
          />
        </>
      ),
    },
    {
      title: t('sourceInfo'),
      dataIndex: 'sourceInfo',
      key: 'sourceInfo',
      render: (sourceInfo, record) => (
        <>
          {
            <Input
              defaultValue={sourceInfo}
              onBlur={(e) => handleTableChange(e.target.value, record, 'sourceInfo')}
            />
          }
        </>
      ),
    },
    {
      title: t('operate'),
      key: 'action',
      render: (text, record) => {
        return (
          <span className='text-link' onClick={(event) => handleDeleteClick(record, event)}>
            {t('delete')}
          </span>
        );
      },
    },
  ];
  const handleModalOK = () => {
    modalForm
      .validateFields()
      .then((params) => {
        if (type === 'add') {
          addCallback(params);
        } else {
          editCallback(params);
        }
      })
      .catch((errorInfo) => {});
  };

  // 添加回调
  const addCallback = async (params) => {
    params.id = uuid();
    let parentId = 'root';
    const res: any = isGuest
      ? await guestModeAddInspiration(TENANT_ID, appId, parentId, params)
      : await addInspiration(TENANT_ID, appId, parentId, params);
    if (res.code === 0) {
      setShowModal(false);
      Message({ type: 'success', content: t('addedSuccessfully') });
      refreshData();
    }
  };

  // 编辑回调
  const editCallback = async (params) => {
    params.id = editId.current;
    let parentId = '';
    let categoryArr = category.current.split(':');
    parentId = categoryArr[categoryArr.length - 1] || '';
    const res: any = isGuest
      ? await guestModeEditInspiration(TENANT_ID, appId, parentId, editId.current, params)
      : await editInspiration(TENANT_ID, appId, parentId, editId.current, params);
    if (res.code === 0) {
      setShowModal(false);
      Message({ type: 'success', content: t('operationSucceeded') });
      refreshData();
    }
  };

  const handleModalCancel = () => {
    setShowModal(false);
  };

  const onPromptChange = (event) => {
    let result = new Set();
    let match;
    while ((match = regex.exec(event.target.value))) {
      result.add(match[1]);
    }
    setCachePromptVar(promptVar);
    setPromptVar([...result]);
  };

  const handleTableChange = (checked, record, key) => {
    const newData = promptVarData.map((item) => {
      if (item.var === record.var) {
        if (key === 'sourceType') {
          return {
            ...item,
            [key]: checked,
            sourceInfo: null,
          };
        } else {
          return {
            ...item,
            [key]: checked,
          };
        }
      }
      return item;
    });
    setPromptVarData(newData);
  };

  const handleDeleteClick = (record, event) => {
    const data = promptVarData.filter((item) => item.var !== record.var);
    setPromptVarData(data);
  };

  const initAdd = (data, type) => {
    setType(type);
    setShowModal(true);
    if (type === 'add') {
      modalForm.setFieldValue('prompt', data.str);
      modalForm.setFieldValue('name', '');
      modalForm.setFieldValue('description', '');
      onPromptChange({ target: { value: data.str } });
    } else {
      modalForm.setFieldValue('prompt', data.prompt);
      modalForm.setFieldValue('name', data.name);
      modalForm.setFieldValue('description', data.description);
      editId.current = data.id;
      category.current = data.category;
      if (data.promptVarData.length > 0) {
        let list = data.promptVarData.map((item) => item.var);
        modalForm.setFieldValue('promptVarData', data.promptVarData);
        setPromptVarData(data.promptVarData);
        setCachePromptVar(list);
        setPromptVar(list);
      }
    }
  };
  useImperativeHandle(addRef, () => {
    return {
      initAdd: initAdd,
    };
  });

  useEffect(() => {
    const newVar = promptVar.filter((item) => !cachePromptVar.includes(item));
    const saveVar = promptVar.filter((item) => cachePromptVar.includes(item));
    const data = newVar.map((item) => {
      return {
        key: uuid(),
        var: item,
        varType: t('selectionBox'),
        sourceType: 'input',
        sourceInfo: '',
        multiple: false,
      };
    });
    setPromptVarData([...promptVarData.filter((item) => saveVar.includes(item.var)), ...data]);
  }, [promptVar]);
  useEffect(() => {
    modalForm.setFieldValue('promptVarData', promptVarData);
  }, [promptVarData]);
  return (
    <>
      {
        <Drawer
          title={type !== 'edit' ? t('createInspiration') : t('editInspiration')}
          open={showModal}
          maskClosable={false}
          onClose={handleModalCancel}
          footer={[
            <Button key='back' onClick={handleModalCancel}>
              {t('cancel')}
            </Button>,
            <Button key='submit' type='primary' onClick={handleModalOK}>
              {t('ok')}
            </Button>,
          ]}
          width='1000px'
        >
          <div className='inspiration-wrap'>
            <Form form={modalForm} {...formItemLayout} layout='vertical'>
              <Form.Item
                name='name'
                label={t('name')}
                rules={[
                  {
                    required: true,
                  },
                  {
                    whitespace: true,
                    message: t('whitespacePrompt'),
                  },
                ]}
                style={{ marginBottom: '8px' }}
              >
                <Input placeholder={t('plsEnter')} maxLength={20} showCount />
              </Form.Item>
              <Form.Item
                name='description'
                label={t('description')}
                rules={[
                  {
                    required: true,
                  },
                  {
                    whitespace: true,
                    message: t('whitespacePrompt'),
                  },
                ]}
                style={{ marginBottom: '8px' }}
              >
                <TextArea maxLength={300} showCount placeholder={t('plsEnter')} rows={3} />
              </Form.Item>
              <Form.Item
                name='prompt'
                label={t('promptName')}
                rules={[
                  {
                    required: true,
                  },
                  {
                    whitespace: true,
                    message: t('whitespacePrompt'),
                  },
                ]}
                style={{ marginBottom: '8px' }}
              >
                <TextArea
                  placeholder={t('promptTextarea')}
                  rows={6}
                  maxLength={3000}
                  showCount
                  onBlur={onPromptChange}
                />
              </Form.Item>
              <Form.Item
                name='promptVarData'
                label={t('promptVar')}
                style={{ display: promptVar.length ? 'block' : 'none', marginTop: '10px' }}
              >
                <Table columns={columns} dataSource={promptVarData} />
              </Form.Item>
              <Form.Item
                name='auto'
                label={
                  <div>
                    <span>{t('automatic')}</span>
                    <Popover
                      content={
                        <div>
                          {t('automaticDesc')}
                          <br />
                          {t('automaticDesc2')}{' '}
                        </div>
                      }
                    >
                      <QuestionCircleOutlined style={{ marginRight: '8px' }} />
                    </Popover>
                  </div>
                }
                checked={modalForm.getFieldValue('auto')}
              >
                <Switch />
              </Form.Item>
            </Form>
          </div>
        </Drawer>
      }
    </>
  );
};

export default AddIns;
