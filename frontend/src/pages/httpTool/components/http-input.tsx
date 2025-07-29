/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect } from 'react';
import { Select, Input, Switch, Button } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { v4 as uuidv4 } from 'uuid';
import { initParams, paramsTypeOption, paramsArrayOption } from '../config';
import { filterTable, updateTable, findNodeAndAddChild, getDefaultExpandedRowKeys } from '../utils';
import { deepClone } from '../../chatPreview/utils/chat-process';
import HttpTable from './http-table';
import HttpTableTitle from './http-table-head';
import { setInputData } from '@/store/toolHttp/toolHttp';
import { useAppDispatch } from '@/store/hook';
import { useTranslation } from 'react-i18next';
import ExpandImg from '@/assets/images/ai/expand.png';
import DeleteRowImg from '@/assets/images/ai/delete-row.png';

const HttpInput = (props: any) => {
  const { t } = useTranslation();
  const { inputTableData, setInputTableData, expandedRowKeys, setExpandedRowKeys } = props;
  const dispatch = useAppDispatch();
  const addType = ['object', 'array<object>'];
  const defaultType = ['string', 'boolean', 'integer'];
  const normalType = ['array<string>', 'array<object>', 'array<integer>', 'array<boolean>'];
  const switchType = ['string', 'boolean', 'integer', 'object', 'array'];

  const tableColumn = [
    {
      title: () => <HttpTableTitle title={t('paramName')} required />,
      dataIndex: 'name',
      key: 'name',
      render: (name: string, record) => (
        <div className={setErrorClass('name', record)}>
          <Input
            onChange={(e) => valueChange(e, 'name', record)}
            disabled={record.disabled}
            defaultValue={name}
            placeholder={t('plsEnter')}
            maxLength={64}
          />
        </div>
      ),
    },
    {
      title: () => <HttpTableTitle title={t('paramDescription')} required />,
      dataIndex: 'description',
      key: 'description',
      render: (description: string, record) => (
        <div className={setErrorClass('description', record)}>
          <Input
            onChange={(e) => valueChange(e, 'description', record)}
            disabled={record.type === 'array'}
            defaultValue={description}
            value={record.type === 'array' ? '' : description}
            placeholder={record.type === 'array' ? '' : t('plsEnter')}
            maxLength={64}
          />
        </div>
      ),
    },
    {
      title: <HttpTableTitle title={t('paramType')} required />,
      dataIndex: 'type',
      key: 'type',
      width: '160px',
      render: (type: string, record) => (
        <Select
          onChange={(e) => valueChange(e, 'type', record, 'select')}
          defaultValue={type || paramsTypeOption[0].value}
          options={setTypeOption(type)}
          style={{ width: '100%' }}
        />
      ),
    },
    {
      title: t('defaultValue'),
      dataIndex: 'defaultValue',
      key: 'defaultValue',
      width: '180px',
      render: (defaultValue: string, record) => (
        <div>
          {setDefaultShow(record) && (
            <Input
              onChange={(e) => valueChange(e, 'defaultValue', record)}
              defaultValue={defaultValue}
              placeholder={t('plsEnter')}
              maxLength={64}
            />
          )}
        </div>
      ),
    },
    {
      title: t('required'),
      dataIndex: 'required',
      key: 'required',
      width: '90px',
      render: (required: boolean, record) =>
        switchType.includes(record.type) &&
        record.deep === 1 && (
          <Switch
            onChange={(e) => valueChange(e, 'required', record, 'switch')}
            checked={required}
          />
        ),
    },
    {
      title: t('operate'),
      dataIndex: 'operation',
      key: 'operation',
      width: '120px',
      render: (text, record) => (
        <div className='http-tool-operation'>
          {addType.includes(record.type) && (
            <img onClick={() => addClick(record)} src={ExpandImg} alt='' />
          )}
          {!normalType.includes(record.type) && (
            <img
              onClick={() => deleteClick(record)}
              src={DeleteRowImg}
              alt=''
            />
          )}
        </div>
      ),
    },
  ];
  // 设置参数类型下拉
  const setTypeOption = (type) => {
    let arrayType = paramsArrayOption.filter((item) => item.value === type)[0];
    if (arrayType) {
      return paramsArrayOption;
    }
    return paramsTypeOption;
  };
  // 设置默认值显示
  const setDefaultShow = (record) => {
    if (record.deep && record.deep > 1) {
      return false;
    }
    if (defaultType.includes(record.type)) {
      return true;
    }
    return false;
  };
  // 校验设置类名
  const setErrorClass = (key, record) => {
    if (record.error && record.error.includes(key)) {
      return 'tool-input-error';
    }
    return 'tool-input';
  };
  // 数据变化
  const valueChange = (e, key, record, type = 'input') => {
    let value = '';
    if (type === 'input') {
      value = e.target.value;
    } else {
      value = e;
    }
    updateTable(inputTableData, record.rowKey, key, value);
    setInputTableData(deepClone(inputTableData));
    getExpandRow(inputTableData);
  };
  // 删除
  const deleteClick = (record) => {
    filterTable(inputTableData, record.rowKey);
    setInputTableData(deepClone(inputTableData));
  };
  // 添加
  const addClick = (record) => {
    initParams.rowKey = uuidv4();
    initParams.reference = `${record.reference}.${record.name}`;
    initParams.deep = record.deep + 1;
    findNodeAndAddChild(inputTableData, record.rowKey, initParams);
    setInputTableData(deepClone(inputTableData));
  };
  // 添加表格数据
  const addRows = () => {
    initParams.rowKey = uuidv4();
    initParams.deep = 1;
    const list = [...inputTableData, initParams];
    setInputTableData(deepClone(list));
  };
  // 数据更新后获取展开数据
  const getExpandRow = (data) => {
    let expandList = getDefaultExpandedRowKeys(data);
    setExpandedRowKeys(expandList);
  };

  useEffect(() => {
    dispatch(setInputData(inputTableData));
  }, [inputTableData]);
  return (
    <>
      <div className='http-tool-input'>
        <div>
          <HttpTable
            columns={tableColumn}
            data={inputTableData}
            expandedRowKeys={expandedRowKeys}
            setExpandedRowKeys={setExpandedRowKeys}
          />
          <div style={{ marginTop: '12px' }}>
            <Button icon={<PlusOutlined />} onClick={addRows}>
              {t('additions')}
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};

export default HttpInput;
