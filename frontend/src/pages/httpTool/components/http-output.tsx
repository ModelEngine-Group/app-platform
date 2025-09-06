/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Input, Select } from 'antd';
import HttpTable from './http-table';
import HttpTableTitle from './http-table-head';
import { v4 as uuidv4 } from 'uuid';
import { deepClone } from '../../chatPreview/utils/chat-process';
import { useTranslation } from 'react-i18next';
import { updateTable, getDefaultExpandedRowKeys, findNodeAndAddChild, filterTable } from '../utils';
import { initParams, paramsTypeOption, paramsArrayOption } from '../config';
import ExpandImg from '@/assets/images/ai/expand.png';
import DeleteRowImg from '@/assets/images/ai/delete-row.png';

const HttpOutPut = (props) => {
  const { t } = useTranslation();
  const { outputTableData, setOutputTableData, expandedRowKeys, setExpandedRowKeys } = props;
  const addType = ['object', 'array<object>'];

  // 添加
  const addClick = (record) => {
    initParams.rowKey = uuidv4();
    initParams.reference = `${record.reference}.${record.name}`;
    initParams.deep = record.deep + 1;
    findNodeAndAddChild(outputTableData, record.rowKey, initParams);
    setOutputTableData(deepClone(outputTableData));
    getExpandRow(outputTableData);
  };
  // 添加表格数据
  const addRows = () => {
    initParams.rowKey = uuidv4();
    const list = [...outputTableData, initParams];
    setOutputTableData(deepClone(list));
  };
  // 校验设置类名
  const setErrorClass = (key, record) => {
    if (record.error && record.error.includes(key)) {
      return 'tool-input-error';
    }
    return 'tool-input';
  };
  // 数据更新后获取展开数据
  const getExpandRow = (data) => {
    let expandList = getDefaultExpandedRowKeys(data);
    setExpandedRowKeys(expandList);
  };
  // 数据变化
  const valueChange = (e, key, record, type = 'input') => {
    let value = '';
    if (type === 'input') {
      value = e.target.value;
    } else {
      value = e;
    }
    updateTable(outputTableData, record.rowKey, key, value);
    setOutputTableData(deepClone(outputTableData));
    getExpandRow(outputTableData);
  };
  const tableColumn = [
    {
      title: () => <HttpTableTitle title={t('paramName')} required />,
      dataIndex: 'name',
      key: 'name',
      render: (name: string, record) => (
        <div className={setErrorClass('name', record)}>
          <Input
            onChange={(e) => valueChange(e, 'name', record)}
            disabled={record.deep === 1 || record.disabled}
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
      title: t('operate'),
      dataIndex: 'operation',
      key: 'operation',
      width: '120px',
      render: (text, record) => (
        <div className='http-tool-operation'>
          {addType.includes(record.type) && (
            <img onClick={() => addClick(record)} src={ExpandImg} alt='' />
          )}
          {record.deep > 1 && <img onClick={() => deleteClick(record)} src={DeleteRowImg} alt='' />}
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

  // 删除
  const deleteClick = (record) => {
    filterTable(outputTableData, record.rowKey);
    setOutputTableData(deepClone(outputTableData));
  }
  return (
    <>
      <div className='http-tool-input'>
        <div>
          <HttpTable
            columns={tableColumn}
            data={outputTableData}
            expandedRowKeys={expandedRowKeys}
            setExpandedRowKeys={setExpandedRowKeys}
          />
        </div>
      </div>
    </>
  );
};

export default HttpOutPut;
