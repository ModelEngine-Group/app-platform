/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import type { TableColumnType } from 'antd';
import { Button, Input, Space } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import './index.scoped.scss';

/*
* @params:
* searchKeyName：筛选字段名key
* async: 是否异步表格；若为true，则不触发onfilter方法（表格数据项筛选）,由调用方在onChange中处理筛选逻辑。
*/
const TableTextSearch = (searchKeyName: string, async: boolean = true): TableColumnType<any> => {
  const { t } = useTranslation();
  return ({
    filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, close }) => (
      <div className='table-header-search' onKeyDown={(e) => e.stopPropagation()}>
        <Input
          className='search-input'
          value={selectedKeys[0]}
          onChange={(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])}
          onPressEnter={() => confirm()}
        />
        <Space className='search-btn'>
          <Button
            className='search-btn-item'
            type='default'
            size='small'
            onClick={() => {
              close();
            }}
          >
            {t('close')}
          </Button>
          <Button
            className='search-btn-item'
            type='primary'
            onClick={() => confirm()}
            size='small'
          >
            {t('ok')}
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered: boolean) => (
      <SearchOutlined style={{ color: filtered ? '#1677ff' : undefined }} />
    ),
    onFilter: (value, record) => {
      if (!async) {
        return record[searchKeyName]
          .toString()
          .toLowerCase()
          .includes((value as string).toLowerCase());
      }
      return record;
    }

  });

};

export default TableTextSearch;
