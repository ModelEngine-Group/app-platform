/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Space, Button, DatePicker } from 'antd';
import type { TableColumnType } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { TableIcons } from '../icons/table';
import { TFunction } from 'react-i18next';

// 自定义搜索面板
const getColumnTimePickerProps = (dataIndex: string, onChange?: any, t?: TFunction): TableColumnType<string> => ({
  filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters, close }) => (
    <div style={{ padding: 8, display: 'flex', flexDirection: 'column' }} onKeyDown={(e) => e.stopPropagation()}>
      <DatePicker.RangePicker
        placeholder={[t('plsChoose'), t('plsChoose')]}
        value={selectedKeys as any}
        format='YYYY-MM-DD'
        onChange={(date, dateString) => {
          setSelectedKeys((date ? date : []) as any)
        }}
      />
      <Space>
        <Button
          type='primary'
          onClick={() => {
            onChange(selectedKeys as string[], confirm, dataIndex);
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
    <>
      <div style={{ color: filtered ? '#1677ff' : undefined, marginTop: 4 }}>
        <TableIcons.date />
      </div>
    </>
  ),
});

export {
  getColumnTimePickerProps,
}
