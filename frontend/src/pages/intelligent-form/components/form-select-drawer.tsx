/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import { Drawer, Image, Input, Button } from 'antd';
import { Icons } from '@/components/icons';
import { useTranslation } from 'react-i18next';
import { getFormList } from '@/shared/http/form';
import { debounce, formEnv } from '@/shared/utils/common';
import FormDrawer from './form-drawer';
import Empty from '@/components/empty/empty-item';
import Pagination from '../../../components/pagination/index';
import { TENANT_ID } from '../../chatPreview/components/send-editor/common/config';
import '../styles/form-select.scss';

/**
 * 编排页面添加表单组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const FormSelectDrawer = ({ showDrawer, setShowDrawer, formConfirm }) => {
  const { t } = useTranslation();
  const [currentForm, setCurrentForm] = useState({});
  const [isAddOperate, setIsAddOperate] = useState(true);
  const [pageNum, setPageNum] = useState(1);
  const [total, setTotal] = useState(0);
  const [pageSize, setPageSize] = useState(8);
  const [name, setName] = useState('');
  const [tableLoading, setTableLoading] = useState(false);
  const [tableData, setTableData] = useState([]);
  const drawerRef = useRef<any>();

  // 名称搜索
  const filterByName = (value: string) => {
    if (value !== name) {
      setPageNum(1);
      setName(value);
    }
  };
  const handleSearch = debounce(filterByName, 1000);
  // 创建表单
  const createForm = () => {
    setCurrentForm({ name: '', describe: '', formComponentPackage: '', componentPreview: '' });
    setIsAddOperate(true);
    drawerRef.current.openOperateDrawer();
  };
  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
    if (pageSize != curPageSize) {
      setPageSize(curPageSize);
    }
  };
  // 获取表单列表
  const getTableList = async () => {
    const params = { pageNum, pageSize, name };
    setTableLoading(true);
    try {
      const res:any = await getFormList(TENANT_ID, params);
      if (res.code === 0) {
        let list = res.data.results || [];
        list.forEach(item => {
          let imgUrl = item.appearance.imgUrl;
          item.description = item.appearance?.description || '';
          item.appearance.imgUrl = `${origin}/${formEnv() ? 'appbuilder' : 'api/jober'}/static/${imgUrl}`;
        })
        setTableData(list);
        setTotal(res.data.range?.total || 0);
      }
    } finally {
      setTableLoading(false);
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
  const itemClick = (item) => {
    formConfirm(item);
  }
  useEffect(() => {
    getTableList();
  }, [pageNum, pageSize, name]);
  return <>
    <Drawer
      title={t('chooseForm')}
      className='intelligent-form'
      maskClosable={false}
      open={showDrawer}
      onClose={() => {setShowDrawer(false)}}
      width={720}
      footer={
        <div className='drawer-footer'>
          <Button
            style={{ width: 90 }}
            onClick={() => {
              setShowDrawer(false);
            }}
          >
            {t('cancel')}
          </Button>
        </div>
      }
    >
      <div className='form-select-content'>
        <div id='form-select-search'>
          <Input
            showCount
            maxLength={20}
            placeholder='Search'
            className='form-select-input'
            onChange={(e) => handleSearch(e.target.value)}
            prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
            defaultValue={name}
          />
          <Button className='form-select-button' onClick={(e) => createForm(e)}>
            {t('create')}
          </Button>
        </div>
        {  tableData.length > 0 ? <div className='form-select-table'>
          {
            tableData.map((item, index) => {
              return (
                <div key={index} className='select-item' onClick={() => itemClick(item)}>
                  <div className='form-view' onClick={(e) => e.stopPropagation()}>
                    <Image
                      width={210}
                      height={100}
                      src={item.appearance.imgUrl}
                      style={{ objectFit: 'contain' }}
                    />
                  </div>
                  <div className='form-name' title={item.name}>{item.name}</div>
                  <div className='form-desc' title={item.appearance.description || ''}>{item.appearance.description || ''}</div>
                </div>
              )
            })
          }
        </div> : <div className='empty-form-box'><Empty /></div>}
        <div>
          <Pagination
            total={total}
            current={pageNum}
            onChange={paginationChange}
            pageSizeOptions={[8, 16, 32, 60]}
            showQuickJumper
            pageSize={pageSize}
          />
        </div>
        <div className='form-select-btn'>

        </div>
      </div>
    </Drawer>
    <FormDrawer 
      drawerRef={drawerRef} 
      formData={currentForm} 
      isAddOperate={isAddOperate}
      refresh={refreshTable}
    />
  </>;
};

export default FormSelectDrawer;
