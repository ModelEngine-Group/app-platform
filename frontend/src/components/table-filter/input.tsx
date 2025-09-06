/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Space, Button, Input } from 'antd';
import type { TableColumnType } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { TFunction } from 'react-i18next';

// 自定义搜索面板
const getColumnSearchProps = (dataIndex: string, onChange?: any, t?: TFunction): TableColumnType<string> => ({
  filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters, close }) => (
    <div style={{ padding: 8 }} onKeyDown={(e) => e.stopPropagation()}>
      <Input
        placeholder={t('plsEnter')}
        value={selectedKeys[0]}
        onChange={(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])}
        onPressEnter={() => {
          onChange(selectedKeys as string[], confirm, dataIndex)
        }}
        style={{ marginBottom: 8, display: 'block' }}
      />
      <Space>
        <Button
          type='primary'
          onClick={() => {
            onChange(selectedKeys as string[], confirm, dataIndex)
          }}
          icon={<SearchOutlined />}
          size='small'
          style={{ width: 90 }}
        >
          {t('search')}
        </Button>
        <Button
          onClick={
            () => {
              clearFilters && clearFilters();
              onChange([], confirm, dataIndex);
            }
          }
          size='small'
          style={{ width: 90 }}
        >
          {t('reset')}
        </Button>
      </Space>
    </div>
  ),
  filterIcon: (filtered: boolean) => (
    <SearchOutlined style={{ color: filtered ? '#1677ff' : undefined }} />
  ),
});

export {
  getColumnSearchProps,
}
