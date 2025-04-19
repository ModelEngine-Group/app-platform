/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useRef, useState } from 'react';
import { Button, DatePicker, Drawer, Table } from 'antd';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { CloseOutlined } from '@ant-design/icons';
import { feedbackType } from './model';
import { AppIcons } from '@/components/icons/app';
import TableTextSearch from '@/components/table-text-search';
import { exportFeedBackData, getFeedBackData } from '@/shared/http/apps';
import Pagination from '@/components/pagination';
import './style.scoped.scss';

const feedbackIcon = {
  '-1': <AppIcons.UnFeedbackIcon style={{ verticalAlign: 'text-bottom' }} />,
  '0': <AppIcons.LikeIcon style={{ verticalAlign: 'text-bottom' }} />,
  '1': <AppIcons.DisLikeIcon style={{ verticalAlign: 'text-bottom' }} />,
};

const FeedBack = () => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [data, setData] = useState<any[]>([]);
  const [searchParams, setSearchParams] = useState({});
  const { appId } = useParams();
  const currentRow = useRef(null);
  const refreshData = () => {};
  useEffect(() => {
    refreshData();
  }, [searchParams]);

  // 搜索数据
  const buildSearchParms = (filters: any, sorter: any) => {
    const filterData: any = {};
    Object.keys(filters).forEach(item => {
      filterData[item] = filters[item]?.[0] ?? ''
    });
    const sorterData: any = {};
    if (sorter.columnKey === 'createTime') {
      sorterData['isSortByCreateTime'] = true;
      sorterData['isSortByResponseTime'] = false;
    } else {
      sorterData['isSortByCreateTime'] = false;
      sorterData['isSortByResponseTime'] = true;
    }
    sorterData['orderDirection'] = sorter.order === 'ascend' ? 'ASC' : sorter.order === 'descend' ? 'DESC' : ''
    return { ...filterData, ...sorterData };
  }
  const handleChange: void = (pagination, filters, sorter) => {
    const search = buildSearchParms(filters, sorter)
    if (searchParams?.startTime) {
      setSearchParams({ ...search, startTime: searchParams?.startTime || null, endTime: searchParams?.endTime || null });
    } else {
      setSearchParams({ ...search });
    }
    setPage(1);
  };
  const basicInfoCols = [
    {
      key: 'createTime',
      label: t('createdAt'),
    },
    {
      key: 'responseTime',
      label: t('responseSpeed'),
    },
    {
      key: 'createUser',
      label: t('user'),
    },
    {
      key: 'userFeedback',
      label: t('userFeedback'),
      render: (value) => (
        <div>
          <span>{feedbackType[value]}</span> {feedbackIcon[value]}
        </div>
      ),
    },
  ];
  const columns = [
    {
      title: t('userQuestion'),
      dataIndex: 'question',
      key: 'question',
      width: 300,
      ellipsis: true,
      ...TableTextSearch('question'),
    },
    {
      title: t('applicationAnswer'),
      dataIndex: 'answer',
      key: 'answer',
      width: 300,
      ellipsis: true,
      ...TableTextSearch('answer'),
    },
    {
      title: t('createdAt'),
      dataIndex: 'createTime',
      key: 'createTime',
      width: 200,
      sorter: (a: any, b: any) => Date.parse(a.createTime.replace(/-/g, "/")) - Date.parse(b.createTime.replace(/-/g, "/")),
    },
    {
      title: t('responseSpeed'),
      dataIndex: 'responseTime',
      key: 'responseTime',
      sorter: (a: any, b: any) => a.responseTime - b.responseTime,
      render: (value) => <>
        {value}ms
      </>
    },
    {
      title: t('userFeedback'),
      dataIndex: 'userFeedback',
      key: 'userFeedback',
      render: (value, record) => (
        <div>
          {feedbackIcon[value]} <span>{feedbackType[value]}</span>
        </div>
      ),
    },
    {
      title: t('operate'),
      dataIndex: 'operate',
      key: 'operate',
      render: (value, record) => (
        <a
          onClick={() => {
            currentRow.current = record;
            setOpen(true);
          }}
        >
          {t('details')}
        </a>
      ),
    },
  ];

  // 总条数
  const [total, setTotal] = useState(0);

  // 分页
  const [page, setPage] = useState(1);

  // 分页数
  const [pageSize, setPageSize] = useState(10);

  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    if (page !== curPage) {
      setPage(curPage);
    }
    if (pageSize != curPageSize) {
      setPageSize(curPageSize);
    }
  }

  useEffect(() => {
    getFeedBack()
  }, [page, pageSize, searchParams])

  // 查询反馈数据
  const getFeedBack = async () => {
    try {
      const res = await getFeedBackData({
        pageIndex: page,
        pageSize: pageSize,
        appId: appId,
        ...searchParams
      });
      setTotal(res?.total || 0);

      const resdata: any[] = (res?.data || []).map((item: any) => ({
        ...item,
        id: item.id,
        question: item.question,
        answer: item.answer,
        responseTime: item.responseTime,
        createTime: item.createTime,
        createUser: item.createUser,
        userFeedback: item.userFeedback
      }));
      setData([...resdata]);
    } catch (error) {

    }
  }

  // 导出数据
  const exportData = async () => {
    try {
      await exportFeedBackData({
        pageIndex: page,
        pageSize: pageSize,
        appId: appId,
        ...searchParams
      });

    } catch (error) {

    }
  }

  return (
    <div className='appengine-feedback'>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 14 }}>
        <DatePicker.RangePicker
          showTime
          onChange={(_date, dateString) => {
            setSearchParams({ ...searchParams, startTime: dateString[0] || null, endTime: dateString[1] || null })
            setPage(1);
          }}
        />
        <Button type='primary' onClick={exportData}>{t('export')}</Button>
      </div>
      <Table
        dataSource={data}
        columns={columns}
        onChange={handleChange}
        virtual
        scroll={{ y: 'calc(100vh - 320px)' }}
        pagination={false}
      />
      <Pagination total={total} current={page} onChange={paginationChange} pageSize={pageSize} />
      <Drawer
        title={t('feedbackDetails')}
        placement='right'
        size='large'
        closeIcon={false}
        onClose={false}
        open={open}
        extra={
          <CloseOutlined
            onClick={() => {
              setOpen(false);
            }}
          />
        }
        footer={
          <Button
            style={{ float: 'right', width: 90 }}
            onClick={() => {
              setOpen(false);
            }}
          >
            {t('close')}
          </Button>
        }
      >
        <div className='drawer-title'>{t('basicInformation')}</div>
        <div
          style={{
            display: 'flex',
            flexWrap: 'wrap',
            whiteSpace: 'normal',
            wordBreak: 'break-all',
            marginTop: '10px',
          }}
        >
          {basicInfoCols.map((item) => (
            <div style={{ width: 'calc((100%) / 3)', marginBottom: 10 }}>
              <div style={{ color: '#4d4d4d', fontSize: 12 }}>{item.label}</div>
              <div style={{ marginTop: '5px', fontSize: '14px' }}>
                {item.render?.(currentRow.current?.[item.key]) || currentRow.current?.[item.key]}
              </div>
            </div>
          ))}
        </div>
        <div className='drawer-title'>{t('answerDetails')}</div>
        <div className='drawer-sub-title'>{t('userQuestion')}</div>
        <div className='question-card '>{currentRow.current?.question}</div>
        <div className='drawer-sub-title'>{t('userAnswer')}</div>
        <div className='answer-card'>
          {currentRow.current?.answer}
        </div>
        <div className='drawer-title'>{t('userFeedback')}</div>
        <div className='question-card '>{currentRow.current?.userFeedbackText}</div>
      </Drawer>
    </div>
  );
};

export default FeedBack;
