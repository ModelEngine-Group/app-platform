/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useImperativeHandle } from 'react';
import {
  PlusOutlined,
  QuestionCircleOutlined
} from '@ant-design/icons';
import {
  Button,
  Form,
  Input,
  Drawer,
  Pagination,
  Popover,
  Select,
  Switch,
  Table,
  TreeSelect
} from 'antd';
import InspirationList from './inspiration-list'
import TreeComponent from '../tree';
import { getFitables } from '@/shared/http/appBuilder';
import { sourceTypes } from '../../common/common';
import { uuid } from '@/common/util';
import { useTranslation } from 'react-i18next';
import { Message } from '@/shared/utils/message';
import '../styles/inspiration.scoped.scss';
import '../styles/inspiration.scss';

const Inspiration = (props) => {
  const { t } = useTranslation();
  const { updateData, inspirationRef, readOnly } = props;
  const [inspirationValues, setInspirationValues] = useState(null);
  const [treeData, setTreeData] = useState(null);
  const [cacheTreeData, setCacheTreeData] = useState(null);
  const [selectTreeData, setSelectTreeData] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [showCateModal, setShowCateModal] = useState(false);
  const [promptVar, setPromptVar] = useState([]);
  const [cachePromptVar, setCachePromptVar] = useState(null);
  const [promptVarData, setPromptVarData] = useState([]);
  const [autoValue, setAutoValue] = useState([]);
  const [nodeList, setNodeList] = useState(null);
  const [fitables, setFitables] = useState(null);
  const [category, setCategory] = useState(null);
  const [id, setId] = useState('');
  const [disabled, setDisabled] = useState(false);
  const [type, setType] = useState('');
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(5);
  const [modalForm] = Form.useForm();
  const { TextArea } = Input;
  let regex = /{{(.*?)}}/g;

  useImperativeHandle(inspirationRef, () => {
    return { onAddClick };
  });

  const formItemLayout = {
    labelCol: { span: 3 },
    wrapperCol: { span: 24 },
  };

  const columns = [
    {
      title: t('variable'),
      dataIndex: 'var',
      key: 'var',
      render: (varName, record) => (
        <div style={{ maxWidth: '240px' }}>{varName}</div>
      )
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
            onChange={(sourceType) => handleTableChange(sourceType, record, 'sourceType')} />
        </>
      )
    },
    {
      title: t('sourceInfo'),
      dataIndex: 'sourceInfo',
      key: 'sourceInfo',
      render: (sourceInfo, record) => (
        <>
          {
            record.sourceType === 'fitable' ?
              <Select options={fitables}
                      onFocus={handleGetFitable}
                      fieldNames={{
                        label: 'name',
                        value: 'fitableId'
                      }}
                      defaultValue={sourceInfo}
                      onChange={(sourceInfo) => handleTableChange(sourceInfo, record, 'sourceInfo')} /> :
              <Input
                defaultValue={sourceInfo}
                onBlur={(e) => handleTableChange(e.target.value, record, 'sourceInfo')}
                maxLength={2000}
                placeholder={t('promptVarPlaceHolder')}
              />
          }
        </>
      )
    },
    {
      title: t('operate'),
      key: 'action',
      render: (text, record) => {
        return (
          <span className='text-link' onClick={(event) => handleDeleteClick(record, event)}>{t('delete')}</span>
        );
      },
    }
  ];


  const clickInspiration = (value) => {
    if (!inspirationValues?.showInspiration) {
      return;
    }
    setType('edit');
    setShowModal(true);
    setId(value.id);
    setCategory(value.category);
    setAutoValue(value.auto);
    modalForm.setFieldsValue(value);
    setPromptVarData(value.promptVarData);
    const newPromptVar = value.promptVarData ? value.promptVarData.map(item => {
      return item.var;
    }) : [];
    setCachePromptVar(null);
    setPromptVar(newPromptVar);
  }

  const onAddClick = () => {
    modalForm.setFieldsValue({
      category: null,
      auto: false,
      name: '',
      description: '',
      prompt: '',
      promptVarData: [],
      promptTemplate: ''
    });
    setPromptVar([]);
    setShowModal(true);
    setAutoValue(false);
    setId(null);
    setCategory(null);
  }

  const onPromptChange = (event) => {
    let result = new Set();
    let match;
    while (match = regex.exec(event.target.value)) {
      result.add(match[1]);
    }
    setCachePromptVar(promptVar);
    setPromptVar([...result]);
  }
  const onChange = (value) => {
    modalForm.setFieldValue('auto', value);
    setAutoValue(value);
  }

  const handleTableChange = (checked, record, key) => {
    const newData = promptVarData.map(item => {
      if (item.var === record.var) {
        if (key === 'sourceType') {
          return {
            ...item,
            [key]: checked,
            sourceInfo: null
          }
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
  }

  const handleChangeCategory = (value) => {
    modalForm.setFieldValue('category', value);
    setCategory(value);
  }

  const handleDeleteClick = (record, event) => {
    const data = promptVarData.filter(item => item.var !== record.var);
    setPromptVarData(data);
  }

  const handleModalOK = () => {
    modalForm
      .validateFields()
      .then((values) => {
        let newvalues = [];
        let newInspirationValues:any = {}
        if (inspirationValues) {
          newvalues = id ? inspirationValues?.inspirations?.map(item => {
            if (item.id === id) {
              return { ...values, id };
            }
            return item;
          }) : [...inspirationValues?.inspirations, { ...values, id: uuid() }];
          newInspirationValues = { ...inspirationValues, inspirations: newvalues };
        } else {
          newvalues =  [item]
          newInspirationValues = { inspirations: newvalues };
        }
        updateData(newInspirationValues, 'inspiration');
        setInspirationValues(newInspirationValues);
        setType('');
        setShowModal(false);
      })
      .catch((errorInfo) => { });
  }

  const handleModalCancel = () => {
    setShowModal(false);
    setType('');
  }

  /**
   * 检验是否有不合理类目
   */
  const validateCate = () => {
    if (disabled) {
      Message({ type: 'warning', content: t('invalidCategory') });
      return true;
    }
    return false;
  }
  /**
   * 点击树形类目弹框确认按钮的回调
   */
  const handleCateModalOK = () => {
    if (validateCate()) return;
    setTreeData(cacheTreeData);
    setShowCateModal(false);
    const newInspirationValues = {
      ...inspirationValues, category: [
        {
          title: 'root',
          id: 'root',
          children: cacheTreeData
        }]
    };
    updateData(newInspirationValues);
    setInspirationValues(newInspirationValues);
  }
  /**
   * 点击树形类目弹框取消按钮的回调
   */
  const handleCateModalCancel = () => {
    setShowCateModal(false);
  }

  const openCategoryModal = () => {
    setShowCateModal(true);
  }

  const handleGetFitable = () => {
    getFitables().then(res => {
      if (res.code === 0) {
        setFitables(res.data);
      }
    })
  }

  useEffect(() => {
    if (!cachePromptVar && id) return;
    if (!promptVar.length && !id) {
      setPromptVarData([]);
      return;
    }
    const newVar = promptVar.filter(item => !cachePromptVar.includes(item));
    const saveVar = promptVar.filter(item => cachePromptVar.includes(item));
    const data = newVar.map((item) => {
      return {
        key: uuid(),
        var: item,
        varType: t('selectionBox'),
        sourceType: 'input',
        sourceInfo: '',
        multiple: false
      }
    });
    setPromptVarData([...promptVarData.filter(item => saveVar.includes(item.var)), ...data]);
  }, [promptVar]);

  useEffect(() => {
    modalForm.setFieldValue('promptVarData', promptVarData);
  }, [promptVarData]);

  useEffect(() => {
    if (!props.inspirationValues || (JSON.stringify(props.inspirationValues)) === '{}') return;
    setInspirationValues(props.inspirationValues);
    if(props.inspirationValues) {
      let treeNode = props.inspirationValues.category[0].children ? props.inspirationValues.category[0].children : []
      setTreeData(treeNode);
      setCacheTreeData(treeNode);
    }
  }, [props.inspirationValues]);

  const updateTreeData = (value) => {
    setCacheTreeData(value);
  }

  useEffect(() => {
    if (!inspirationValues) return;
    const data = inspirationValues.inspirations?.map(item => item.category?.split(':')[1]);
    setNodeList(data);
  }, [inspirationValues]);

  useEffect(() => {
    if (!treeData) return;
    setSelectTreeData(disableNodes(treeData));
  }, [treeData])

  const handleDeleteIns = (id) => {
    if (!inspirationValues?.showInspiration) {
      return;
    }
    const newvalues = inspirationValues.inspirations.filter(item => item.id !== id);
    const newInspirationValues = { ...inspirationValues, inspirations: newvalues };
    updateData(newInspirationValues);
    setInspirationValues(newInspirationValues);
  }

  const disableNodes = (nodes) => {
    return nodes.map(node => {
      const children = node.children ? disableNodes(node.children) : [];
      const isLeafNode = children.length === 0;
      return {
        ...node,
        disabled: !isLeafNode,
        children: children
      };
    });
  }

  // 处理分页器页数修改
  const handleCurrentPageChange = (page, pageSize) => {
    setCurrent(page);
  }

  // 获取分页的提示词变量数据
  const fetchData = () => {
    return promptVarData.slice((current - 1) * pageSize, current * pageSize);
  }

  return (
    <>
      <div className='control-container'>
        <div className='control'>
          <div className='control-header '>
            <div className='control-title'>
              <span>{t('inspirationFunctionDescription')}</span>
            </div>
          </div>
          <InspirationList inspirationValues={inspirationValues} clickInspiration={clickInspiration} handleDeleteIns={handleDeleteIns} readOnly={readOnly}></InspirationList>
        </div>
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
            </Button>
          ]}
          width='1000px'>
          <div className='inspiration-wrap'>
            <Form
              form={modalForm}
              autoComplete="off"
              layout='vertical'
              {...formItemLayout}
            >
              <Form.Item
                name='name'
                label={t('name')}
                rules={[
                  {
                    required: true,
                    message: t('plsEnterName'),
                  },
                  {
                    whitespace: true,
                    message: t('whitespacePrompt'),
                  }
                ]}
                style={{ marginBottom: '8px' }}
              >
                <Input placeholder={t('plsEnter')}
                       maxLength={20}
                       showCount />
              </Form.Item>
              <Form.Item
                name='description'
                label={t('description')}
                rules={[
                  {
                    required: true,
                    message: t('descriptionMessage'),
                  },
                  {
                    whitespace: true,
                    message: t('whitespacePrompt'),
                  }
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
                    message: t('promptMessage'),
                  },
                  {
                    whitespace: true,
                    message: t('whitespacePrompt'),
                  }
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
                <div className="prompt-table">
                  <Table
                    columns={columns}
                    dataSource={fetchData()}
                    pagination={false}
                  />
                </div>
                <div className="prompt-pagination">
                  <Pagination
                    size="small"
                    pageSize={pageSize}
                    current={current}
                    total={promptVarData.length}
                    onChange={handleCurrentPageChange}
                  />
                </div>
              </Form.Item>
              <Form.Item
                name='category'
                label={t('classify')}
              >
                <div className='flex-center'>
                  <TreeSelect
                    treeData={selectTreeData}
                    treeDefaultExpandAll
                    fieldNames={{
                      label: 'title',
                      value: 'parent'
                    }}
                    value={category}
                    onSelect={handleChangeCategory}
                    popupClassName='select-class'
                    treeLine={true}
                  />
                  <PlusOutlined 
                    className='plus-icon' 
                    style={{ flex: 1, fontSize: '16px', color: '#2673e5' }} 
                    onClick={openCategoryModal} />
                </div>
              </Form.Item>
              <Form.Item
                name='auto'
              >
                <div style={{ display: 'flex', alignItems: 'center', marginTop: '16px' }}>
                <span style={{ marginRight: '8px' }}>{t('automatic')}</span>
                    <Popover content={
                      <div style={{ maxWidth: '400px' }}>
                        {t('automaticDesc')}<br />
                        {t('automaticDesc2')}。
                      </div>}
                    >
                      <QuestionCircleOutlined style={{ marginRight: '8px' }}/>
                    </Popover>
                  <Switch checked={autoValue} onChange={onChange} />
                </div>
              </Form.Item>
            </Form>
          </div>
        </Drawer>
        <Drawer
          title={t('categoryConfiguration')}
          destroyOnClose
          open={showCateModal}
          maskClosable={false}
          onClose={handleCateModalCancel}
          width='560px'
          footer={[
            <Button key='back' onClick={handleCateModalCancel}>
              {t('cancel')}
            </Button>,
            <Button key='submit' type='primary' onClick={handleCateModalOK}>
              {t('ok')}
            </Button>
          ]}>
          <TreeComponent 
            tree={treeData}
            nodeList={nodeList}
            updateTreeData={updateTreeData}
            setDisabled={setDisabled}
            category={category}
          />
        </Drawer>
      </div>
    </>
  )
};


export default Inspiration;
