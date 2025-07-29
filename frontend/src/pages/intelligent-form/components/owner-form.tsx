/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Button, Input, Table, Space, Typography, Modal } from 'antd';
import { getFormList, deleteForm } from '@/shared/http/form';
import { Icons } from '@/components/icons';
import Pagination from '@/components/pagination/index';
import FormDrawer from './form-drawer';
import { Message } from '@/shared/utils/message';
import { debounce, getCookie } from '@/shared/utils/common';
import { TENANT_ID } from '../../chatPreview/components/send-editor/common/config';
import { useAppSelector } from '@/store/hook';

/**
 * 智能表单首页
 *
 * @return {JSX.Element}
 * @constructor
 */
const OwnerForm = () => {
  const { t } = useTranslation();
  const [name, setName] = useState('');
  const [tableLoading, setTableLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [dataSource, setDataSource] = useState([]);
  const [pageSize, setPageSize] = useState(10);
  const [isAddOperate, setIsAddOperate] = useState(true);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [currentForm, setCurrentForm] = useState({});
  const [deleteItem, setDeleteItem] = useState({});
  const readOnly = useAppSelector((state) => state.chatCommonStore.readOnly);
  const drawerRef = useRef<any>();
  const cLocale = getCookie('locale').toLocaleLowerCase();

  // 获取表单列表
  const getTableList = async () => {
    const params = { pageNum, pageSize, name,};
    setTableLoading(true);
    try {
      const res:any = await getFormList(TENANT_ID, params);
      if (res.code === 0) {
        let list = res.data.results || [];
        list.forEach(item => {
          item.description = item.appearance?.description || '';
        })
        setDataSource(list);
        setTotal(res.data.range?.total || 0);
      }
    } finally {
      setTableLoading(false);
    }
  };
  // 创建表单
  const createForm = () => {
    setCurrentForm({ name: '', describe: '', formComponentPackage: '', componentPreview: '' });
    setIsAddOperate(true);
    drawerRef.current.openOperateDrawer();
  };
  // 搜索表单
  const onSearchValueChange = (value) => {
    if (value !== name) {
      setPageNum(1);
      setName(value);
    }
  };
  const handleSearch = debounce(onSearchValueChange, 1000);

  // 编辑表单
  const editForm = (item) => {
    setIsAddOperate(false);
    setCurrentForm({ ...item });
    drawerRef.current.openOperateDrawer();
  };
  // 删除表单
  const deleteOperate = (item) => {
    setDeleteItem(item);
    setDeleteOpen(true);
  }
  const deleteFormConfirm = async () => {
    setDeleteLoading(true);
    try {
      const res:any = await deleteForm(TENANT_ID, deleteItem.id);
      if (res.code === 0) {
        getTableList();
        setDeleteOpen(false);
        Message({ type: 'success', content: t('deleteSuccess') });
      }
    } finally {
      setDeleteLoading(false);
    }
  };

  const columns = [
    {
      title: t('uploadForm'),
      dataIndex: 'name',
      ellipsis: true,
      key: 'name',
    },
    {
      title: t('uploadDesc'),
      dataIndex: 'description',
      ellipsis: true,
      key: 'description',
    },
    {
      title: t('uploadCreateBy'),
      dataIndex: 'createBy',
      key: 'createBy',
    },
    {
      title: t('uploadCreateAt'),
      dataIndex: 'createAt',
      key: 'createAt',
    },
    {
      title: t('uploadUpdateBy'),
      dataIndex: 'updateBy',
      key: 'updateBy',
    },
    {
      title: t('uploadUpdateAt'),
      dataIndex: 'updateAt',
      key: 'updateAt',
    },
    {
      title: t('uploadoperate'),
      dataIndex: 'operate',
      key: 'operate',
      render(_, item) {
        return <Space>
          { !readOnly ? <div style={{ display: 'flex', gap: '8px' }}>
            <Typography.Link onClick={() => editForm(item)}>
              {t('edit')}
            </Typography.Link>
            <a onClick={() => deleteOperate(item)}>{t('delete')}</a>
          </div> : <span>-</span>}
        </Space>
      }
    },
  ];

  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
    if (pageSize != curPageSize) {
      setPageSize(curPageSize);
    }
  };
  // 创建成功后刷新表格
  const refreshTable = () => {
    if (pageNum === 1) {
      getTableList();
    } else {
      setPageNum(1);
    }
  }

  useEffect(() => {
    getTableList();
  }, [pageNum, pageSize, name]);

  return <div style={{ height: '100%' }}>
    {/* 头部 */}
    <div className='operatorArea'>
      { !readOnly && <Button type='primary' onClick={createForm}>{t('create')}</Button> }
      <Input
        className='knowledge-search'
        style={{ width: '200px' }}
        maxLength={20}
        placeholder={t('search')}
        onChange={(e) => handleSearch(e.target.value)}
        prefix={<Icons.search color={'rgb(230, 230, 230)'} />} />
    </div>
    <Table
      dataSource={dataSource}
      columns={columns}
      pagination={false}
      scroll={{ y: 'calc(100vh - 340px)' }}
      style={{ margin: '16px 0px' }}
      rowKey={record => record.id}
      loading={tableLoading}
    />
    <Pagination
      total={total}
      current={pageNum}
      onChange={paginationChange}
      pageSizeOptions={[10, 20, 50, 100]}
      showQuickJumper
      pageSize={pageSize}
    />
    {/* 删除弹框 */}
    <Modal
      title={t('deleteModalTitle')}
      width='380px'
      open={deleteOpen}
      centered
      onOk={deleteFormConfirm}
      onCancel={() => setDeleteOpen(false)}
      okButtonProps={{ loading: deleteLoading }}
      okText={t('ok')}
      cancelText={t('cancel')}
    >
      { cLocale !== 'en-us' ? 
      <div>
        <span>{t('youWillDelete') + deleteItem.name + t('form') + '，' + t('afterDelete')}</span>
        <span style={{ fontWeight: 700 }}>{t('deleteTip')}</span>
      </div> : 
      <div>
        <span>{ t('youWillDelete')}</span>
        <span style={{ fontWeight: 700, margin: '0 8px' }}>{deleteItem.name}</span>
        <span>{t('afterDelete')}</span>
      </div> }
      
    </Modal>
    <FormDrawer 
      drawerRef={drawerRef} 
      formData={currentForm} 
      isAddOperate={isAddOperate} 
      refresh={refreshTable}
    />
  </div>
};

export default OwnerForm;