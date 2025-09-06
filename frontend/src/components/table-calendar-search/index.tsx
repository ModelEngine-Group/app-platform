/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { DatePicker, TableColumnType, Button, Space } from 'antd';
import { CalendarOutlined } from '@ant-design/icons';
import type { GetProps } from 'antd';
import { useTranslation } from 'react-i18next';

type RangePickerProps = GetProps<typeof DatePicker.RangePicker>;

const { RangePicker } = DatePicker;

/*
* @params:
* searchKeyName：筛选字段名key
* async: 是否异步表格；若为true，则不触发onfilter方法（表格数据项筛选）,由调用方在onChange中处理筛选逻辑。
*/
const TableCalendarSearch = (searchKeyName: string, async: boolean = true): TableColumnType<any> => {
  const { t } = useTranslation();
  return ({
    filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, close }) => (
      <div style={{ padding: 8, width: 360 }} onKeyDown={(e) => e.stopPropagation()}>
        <Space direction='vertical' size={12}>
          <RangePicker
            showTime={{ format: 'HH:mm:ss' }}
            format='YYYY-MM-DD HH:mm:ss'
            onChange={(_, dateString) => {
              setSelectedKeys(dateString);
            }}
          />
        </Space>
        <Space style={{ float: 'right', marginBottom: 8 }}>
          <Button
            type='default'
            size='small'
            style={{ minWidth: 60, height: 24 }}
            onClick={() => {
              close();
            }}
          >
            {t('close')}
          </Button>
          <Button
            type='primary'
            onClick={() => confirm()}
            size='small'
            style={{ minWidth: 60, height: 24, backgroundColor: '#2673e5' }}
          >
            {t('ok')}
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered: boolean) => (
      <CalendarOutlined style={{ color: filtered ? '#1677ff' : undefined }} />
    ),
    onFilter: (value: any, record) => {
      if (!async) {
        const startDate = value[0];
        const endDate = value[1];
        return record[searchKeyName];
      }
      return record;
    }
  });

};

export default TableCalendarSearch;
