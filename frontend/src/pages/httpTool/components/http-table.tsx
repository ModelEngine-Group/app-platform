/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Table } from 'antd';
import { CaretDownOutlined, CaretRightOutlined } from '@ant-design/icons';

const HttpTable = (props) => {
  const { columns, data, expandedRowKeys, setExpandedRowKeys } = props;

  const handleExpand = (expanded, record) => {
    if (expanded) {
      setExpandedRowKeys([...expandedRowKeys, record.rowKey]);
    } else {
      setExpandedRowKeys(expandedRowKeys.filter(key => key !== record.rowKey));
    }
  };

  return <>{(
    <div className='http-input-content'>
      <Table
        columns={columns}
        dataSource={data}
        rowKey={'rowKey'}
        pagination={false}
        scroll={{ y: 650 }}
        expandable={{
          expandedRowKeys: expandedRowKeys,
          onExpand: handleExpand,
          expandIcon: ({ expanded, onExpand, record }) =>{
            if (record.children && record.children.length !== 0) {
              if (expanded) {
                return <CaretDownOutlined  onClick={e => onExpand(record, e)} />
              } else {
                return <CaretRightOutlined onClick={e => onExpand(record, e)} />
              }
            } else {
              return <span className='expand-empty'></span>
            }
          }
        }}
      />
    </div>
  )}</>
};


export default HttpTable;
