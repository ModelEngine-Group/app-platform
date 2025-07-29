/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Button, Space, Progress, Table } from 'antd';
import { AppIcons } from '@/components/icons/app';
import {
  getEvaluateList,
  createEvaluateInstance,
  deleteEvalTaxk,
} from '@/shared/http/appEvaluate';
import { TaskStatusE, RunStatus } from './model';
import { useParams } from 'react-router';
import { useHistory } from 'react-router-dom';
import CreateModal from './create';
import { useTranslation } from 'react-i18next';
import i18n from '@/locale/i18n';
import './index.scss';

const taskStatusMap: any = {
  [TaskStatusE.PUBLISHED]: (
    <div>
      <AppIcons.CompleteIcon /> {i18n.t('active')}
    </div>
  ),
  [TaskStatusE.UNPUBLISHED]: (
    <div>
      <AppIcons.AbnormalIcon /> {i18n.t('inactive')}
    </div>
  ),
  [TaskStatusE.DELETED]: (
    <div>
      <AppIcons.PowerOffIcon /> {i18n.t('deleted')}
    </div>
  ),
};

const runstatusMap: any = {
  [RunStatus.RUNNING]: (
    <div>
      <AppIcons.RunningIcon /> {i18n.t('underEvaluation')}
    </div>
  ),
  [RunStatus.PENDING]: (
    <div>
      <AppIcons.PowerOffIcon /> {i18n.t('waitingForEvaluation')}
    </div>
  ),
  [RunStatus.SUCCESS]: (
    <div>
      <AppIcons.CompleteIcon /> {i18n.t('finishEvaluation')}
    </div>
  ),
  [RunStatus.FAILED]: (
    <div>
      <AppIcons.AbnormalIcon /> {i18n.t('failedEvaluation')}
    </div>
  ),
};

const EvaluateTask = () => {
  const { t } = useTranslation();
  const { tenantId, appId } = useParams();
  const [data, setData] = useState<any>([]);
  const [total, setTotal] = useState(0); // 总条数
  const [pageIndex, setPageIndex] = useState(1); // 分页
  const [pageSize, setPageSize] = useState(10); // 分页数
  const [isShow, setIsShow] = useState(false);
  const [searchParams, setSearchParams] = useState({
    pageIndex: pageIndex,
    appId: appId,
    pageSize: pageSize,
  });
  const [selectedList, setSelectedList] = useState<any[]>([]);
  const history = useHistory().push;
  const detailInfo = JSON.parse(sessionStorage.getItem('evaluateDetails') as any)

  const paginationChange = (curPage: number, curPageSize: number) => {
    setPageIndex(curPage);
    setPageSize(curPageSize);
  };

  const refreshData = async () => {
    const res: any = await getEvaluateList(searchParams);
    setTotal(res?.data?.total);
    setData(res?.data?.items);
  };

  useEffect(() => {
    refreshData();
  }, [searchParams]);

  const handleChange = (pagination: any, filters: any, sorter: any) => {
    const paramBody: any = {
      appId: appId,
      pageIndex: pagination.current,
      pageSize: pagination.pageSize,
    };
    setSearchParams(paramBody);
  };

  const showAddFlow = async () => {
    setIsShow(true);
  };
  // 跳转详情
  const toDetails = (e: any) => {
    history({
      pathname: `/app-develop/${tenantId}/app-detail/add-flow/${e.workflowId}`,
      search: `?type=evaluate&appId=${appId}`,
    });
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      render(_: any, record: any) {
        return <a onClick={() => toDetails(record)}>{record?.id}</a>;
      },
    },
    {
      title: t('evaluateName'),
      dataIndex: 'name',
      key: 'name',
      ellipsis: true,
    },
    {
      title: t('evaluateDescription'),
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: t('isPublish'),
      dataIndex: 'status',
      key: 'status',
      ellipsis: true,
      render: (value: string | number) => taskStatusMap[value],
    },
    {
      title: t('instanceStatus'),
      key: 'instanceStatus',
      dataIndex: 'instanceStatus',
      ellipsis: true,
      render: (value: string | number) => runstatusMap[value],
    },
    {
      title: t('passRate'),
      dataIndex: 'passRate',
      key: 'passRate',
      ellipsis: true,
      render: (value: any, record: any) => (
        <Progress percent={Math.floor(value * 100)} size='small' />
      ),
    },
    {
      title: t('createdBy'),
      dataIndex: 'createdBy',
      key: 'createdBy',
      ellipsis: true,
    },
    {
      title: t('createdAt'),
      dataIndex: 'createdAt',
      key: 'createdAt',
      ellipsis: true,
    },
    {
      title: t('operate'),
      dataIndex: 'operate',
      key: 'operate',
      ellipsis: true,
      render(_: any, record: any) {
        return (
          <Space size='middle'>
            <a onClick={() => running(record)}>{t('run')}</a>
            <a
              style={{ pointerEvents: record.instanceStatus !== 'SUCCESS' ? 'none' : 'auto' }}
              onClick={() => viewReport(record)}
            >
              {t('viewReport')}
            </a>
            <a onClick={() => delEvaluate([record.id])}>{t('delete')}</a>
          </Space>
        );
      },
      width: 200,
    },
  ];

  const running = async (e: any) => {
    try {
      const res: any = await createEvaluateInstance({
        taskId: e.id,
        appId: appId,
        initContext: {
          initContext: {
            isDebug: false,
            evalDatasetQuantity: 1000,
          },
        },
        isDebug: false,
        tenantId: tenantId,
        workflowId: e.workflowId,
      });
      if (res.code === 0) {
        refreshData();
      }
    } catch (error) {}
  };

  const viewReport = (record: any) => {
    history({
      pathname: `/app-develop/${tenantId}/appDetail/${appId}/task/viewReport`,
      search: `?instance=${record?.instanceId}`,
    });
    sessionStorage.setItem('evaluateView', JSON.stringify(record));
  };

  const delEvaluate = async (e: any) => {
    const res: any = await deleteEvalTaxk(e);
    if (res.code === 0) {
      refreshData();
    }
  };

  const selectChange = (selectedRowKeys: React.Key[]) => {
    setSelectedList([...selectedRowKeys]);
  };

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button type='primary' style={{ width: '88px', marginRight: '16px' }} onClick={showAddFlow}>
          {t('create')}
        </Button>
        <Button
          onClick={() => delEvaluate(selectedList)}
          disabled={!selectedList.length}
          style={{ width: '88px' }}
        >
          {t('delete')}
        </Button>
      </div>
      <Table
        dataSource={data}
        columns={columns}
        onChange={handleChange}
        rowSelection={{
          type: 'checkbox',
          columnWidth: 60,
          onChange: (k, r, d) => {
            selectChange(k);
          },
        }}
        scroll={{ y: 'calc(100vh - 340px)' }}
        pagination={{
          total,
          position: ['bottomRight'],
          size: 'small',
          showQuickJumper: true,
          defaultCurrent: 1,
          showSizeChanger: true,
          showTotal: () => <div>{t('total')}{total}{t('piece')}</div>,
          onChange: paginationChange,
        }}
      />
      <CreateModal isShow={isShow} setIsShow={setIsShow} detailInfo={detailInfo} />
    </div>
  );
};

export default EvaluateTask;
