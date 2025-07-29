/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Table, Collapse, Tooltip } from 'antd';
import type { TableProps } from 'antd';
import Pagination from '@/components/pagination';
import { useTranslation } from 'react-i18next';

interface DataType {
  key: string;
  nodeName?: string;
  input?: string;
  score?: string;
}

interface ResultProps {
  resultIndex?: any;
  resultSize?: any;
  resultTotal?: number;
  resultData?: any;
}

const Result = ({ resultIndex, resultSize, resultTotal, resultData }: ResultProps) => {
  const { t } = useTranslation();
  const [pageIndex, setPageIndex] = useState(1); // 分页
  const [pageSize, setPageSize] = useState(10); // 分页数
  // 处理input数据
  const func = (value: any) => {
    let newArr = Object.entries(JSON.parse(value)).map((item) => {
      let newStr = item.map((e: any) => {
        if (typeof e === 'object') {
          return JSON.stringify(e);
        } else {
          return e;
        }
      });
      return newStr.join(': ');
    });
    return newArr;
  };

  const columns: TableProps<DataType>['columns'] = [
    {
      key: 'nodeName',
      title: t('name'),
      dataIndex: 'nodeName',
      width: 150,
      ellipsis: true,
    },
    {
      key: 'score',
      title: t('scores'),
      width: 100,
      dataIndex: 'score',
    },
    {
      key: 'input',
      title: t('evaluationData'),
      dataIndex: 'input',
      ellipsis: {
        showTitle: false,
      },
      width: 750,
      render: (value) => {
        return (
          <div>
            {func(value).map((item: any, index: number) => {
              return (
                <Tooltip key={index} placement='topLeft' title={item}>
                  <div key={index} className='text-ellipsis'>
                    {item}
                  </div>
                </Tooltip>
              );
            })}
          </div>
        );
      },
    },
  ];

  const paginationChange = (curPage: number, curPageSize: number) => {
    setPageIndex(curPage);
    setPageSize(curPageSize);
  };
  
  useEffect(() => {
    resultIndex(pageIndex), resultSize(pageSize);
  }, [pageIndex, pageSize]);
  return (
    <div style={{ width: '100%' }}>
      <Collapse bordered={false}>
        {resultData?.map((item: any, index: number) => {
          const idx = (pageIndex - 1) * pageSize + index + 1;
          const time = item.evalCaseEntity.latency / 1000;
          return (
            <Collapse.Panel header={`${t('useCase')} ${idx}`} key={item.evalCaseEntity.id}>
              <Table
                key={item.evalRecordEntities.id}
                columns={columns}
                dataSource={item.evalRecordEntities}
                pagination={false}
              />
            </Collapse.Panel>
          );
        })}
      </Collapse>
      <Pagination
        total={resultTotal}
        current={pageIndex}
        onChange={paginationChange}
        pageSize={pageSize}
      />
    </div>
  );
};

export default Result;
