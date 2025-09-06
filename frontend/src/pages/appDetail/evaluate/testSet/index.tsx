/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { Button } from 'antd';
import React, { useState, useEffect } from 'react';
import type { TableProps } from 'antd';
import { Table, Space } from 'antd';
import CreateSet from './createTestset/createTestSet';
import SetDetail from './detail';
import { getEvalDataList, deleteDataSetData } from '@/shared/http/appEvaluate';
import { useParams } from 'react-router-dom';
import Pagination from '@/components/pagination';
import { getAppInfo } from '@/shared/http/aipp';
import { useTranslation } from 'react-i18next';

interface DataType {
  key: string;
  name: string;
  age: number;
  address: string;
  desc: string;
}

type DataIndex = keyof DataType;
type OnChange = NonNullable<TableProps<DataType>['onChange']>;
type Filters = Parameters<OnChange>[1];

const TestSet: React.FC = () => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);

  // 评估弹窗开关
  const [detailInfo, setDetailInfo] = useState({});
  const [filteredInfo, setFilteredInfo] = useState<Filters>({});

  const [data, setData] = useState([]);
  // 总条数
  const [total, setTotal] = useState(0);

  // 分页
  const [page, setPage] = useState(1);

  // 分页数
  const [pageSize, setPageSize] = useState(10);

  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    setPage(curPage);
    setPageSize(curPageSize);
  };

  const [type, setType] = useState('');
  const { tenantId, appId } = useParams();
  const [selectedList, setSelectedList] = useState<any[]>([]);

  // 时间转换
  const formateDate = (str: Date) => {
    if (!str) {
      return '';
    }
    const date = new Date(str);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hour = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${year}-${month.toString().padStart(2)}-${day} ${hour}:${minutes}:${seconds}`;
  };

  // 构建搜索参数
  const buildQuery = () => {
    let query: any = {
      appId,
      pageIndex: page,
      pageSize: pageSize,
    };

    Object.keys(filteredInfo).forEach((key) => {
      const item: any = filteredInfo[key];
      if (key === 'createdAt' || key === 'updatedAt') {
        query[`${key}To`] = formateDate(item[1]);
        query[`${key}From`] = formateDate(item[0]);
      } else {
        query[key] = item[0];
      }
    });
    return query;
  };

  // 获取评估测试集列表
  const refresh = async () => {
    try {
      let res = await getEvalDataList(buildQuery());
      setTotal(res?.data?.total || 0);
      setData(
        (res?.data?.items || []).map((item: any) => ({
          ...item,
          key: item.id,
        }))
      );
    } catch (error) {}
  };

  const openDetails = (val: any) => {
    setDetailInfo(val);
    setDetailOpen(true);
    setType('detail');
  };

  const columns = [
    {
      key: 'id',
      dataIndex: 'id',
      title: 'ID',
      render(_: any, record: any) {
        return <a onClick={() => openDetails(record)}>{record?.id}</a>;
      },
    },
    {
      key: 'name',
      dataIndex: 'name',
      title: t('testSetName'),
    },
    {
      key: 'description',
      dataIndex: 'description',
      title: t('testSetDescription'),
    },
    {
      key: 'createdBy',
      dataIndex: 'createdBy',
      title: t('createdBy'),
    },
    {
      key: 'createdAt',
      dataIndex: 'createdAt',
      title: t('createdAt'),
    },
    {
      key: 'updatedAt',
      dataIndex: 'updatedAt',
      title: t('modificationTime'),
    },
    {
      key: 'action',
      title: t('operate'),
      render(_: any, record: any) {
        const viewDetail = () => {
          setDetailInfo(record);
          setOpen(true);
          setType('edit');
        };
        const deleteData = async () => {
          try {
            await deleteDataSetData([record?.id]);
            refresh();
          } catch (error) {}
        };
        return (
          <Space size='middle'>
            <a onClick={viewDetail}>{t('edit')}</a>
            <a onClick={deleteData}>{t('delete')}</a>
          </Space>
        );
      },
    },
  ];

  const showDrawer = () => {
    setOpen(true);
    setType('create');
  };

  const callback = (type: string, data: any) => {
    setOpen(false);
    refresh();
  };

  const detailCallback = () => {
    setDetailOpen(false);
    refresh();
  };

  const [appInfo, setAppInfo] = useState<any>({});

  // 获取应用信息
  const onGetAppInfo = async () => {
    try {
      const res = await getAppInfo(tenantId, appId);
      setAppInfo(res?.data || {});
    } catch (error) {}
  };

  const selectChange = (selectedRowKeys: React.Key[], selectedRows: DataType[], d) => {
    setSelectedList([...selectedRowKeys]);
  };

  useEffect(() => {
    onGetAppInfo();
  }, []);

  useEffect(() => {
    refresh();
  }, [page, pageSize, filteredInfo]);

  return (
    <div>
      <div className='margin-bottom-standard test'>
        <Button
          className='margin-right-standard'
          type='primary'
          style={{ width: '88px' }}
          onClick={showDrawer}
        >
          {t('create')}
        </Button>
        <Button
          onClick={() => {
            deleteDataSetData(selectedList);
            refresh();
          }}
          disabled={selectedList.length ? false : true}
          style={{ width: '88px' }}
        >
          {t('delete')}
        </Button>
      </div>
      <Table
        dataSource={data}
        columns={columns}
        rowSelection={{
          type: 'checkbox',
          columnWidth: 60,
          onChange: (k, r, d) => {
            selectChange(k, r, d);
          },
        }}
        scroll={{ y: 800 }}
        pagination={false}
      />
      <Pagination total={total} current={page} onChange={paginationChange} pageSize={pageSize} />
      {open && (
        <CreateSet visible={open} createCallback={callback} type={type} detailInfo={detailInfo} />
      )}
      {detailOpen && (
        <SetDetail
          visible={detailOpen}
          params={detailInfo}
          detailCallback={detailCallback}
          type={type}
        />
      )}
    </div>
  );
};

export default TestSet;
