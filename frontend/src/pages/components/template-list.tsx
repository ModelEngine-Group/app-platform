/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, useEffect, useImperativeHandle } from 'react';
import { Modal, Input, Spin, Tabs } from 'antd';
import { Icons } from '@/components/icons';
import { debounce } from '@/shared/utils/common';
import { useTranslation } from 'react-i18next';
import Pagination from '@/components/pagination';
import AppCard from '@/components/appCard';
import EditModal from '../../pages/components/edit-modal';
import Empty from '@/components/empty/empty-item';
import { getTemplateList } from '@/shared/http/appDev';
import { useHistory } from 'react-router-dom';
import { TENANT_ID } from '../chatPreview/components/send-editor/common/config';
import { tabItems } from '../appDev/common';
import './styles/template-list.scss'

/**
 * 应用模板列表弹窗组件
 *
 * @param tempalteRef 当前组件ref.
 * @param tabs 应用分类.
 * @return {JSX.Element}
 * @constructor
 */

const TemplateList = ({ tempalteRef, tabs }) => {
  const tenantId = TENANT_ID;
  const navigate = useHistory().push;
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [listLoading, setListLoading] = useState(true);
  const [templatList, setTemplatList] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(8);
  const [search, setSearch] = useState('');
  const [currentHoverId, setCurrentHoverId] = useState('');
  const [activkey, setActiveKey] = useState('all');
  const fetchRef = useRef(0);
  const modalRef = useRef(null);
  const [templateInfo, setTemplateInfo] = useState({});
  const [templateKey, setTemplateKey] = useState(tabs?.[0]?.key || 'all');

  useImperativeHandle(tempalteRef, () => {
    return {
      open: () => openModal(),
    };
  });

  const openModal = () => {
    setTemplateKey(tabs?.[0]?.key);
    setSearch('');
    setPage(1);
    setPageSize(8);
    setOpen(true);
  };

  const addAippCallBack = (appId: string, aippId: string) => {
    if (aippId) {
      navigate(`/app-develop/${tenantId}/app-detail/${appId}/${aippId}`);
      return;
    }
    navigate(`/app-develop/${tenantId}/app-detail/${appId}`);
  };

  // 搜索
  const onSearchValueChange = (newSearchVal: string) => {
    if (newSearchVal !== search) {
      setPage(1);
      setSearch(newSearchVal);
    }
  };

  const handleSearch = debounce(onSearchValueChange, 500);

  const paginationChange = (curPage: number, curPageSize: number) => {
    if (page !== curPage) {
      setPage(curPage);
    }
    if (pageSize !== curPageSize) {
      setPageSize(curPageSize);
    }
  };

  const queryTemplateList = async () => {
    setListLoading(true);
    const params = {
      offset: (page - 1) * pageSize,
      limit: pageSize,
      name: search || undefined,
      categories: activkey !== 'all' ? [activkey].join() : null,
      app_type: templateKey !== 'all' ? templateKey : null
    };
    fetchRef.current += 1;
    const fetchId = fetchRef.current;
    try {
      const res = await getTemplateList(tenantId, params);
      if (fetchId !== fetchRef.current) {
        // for fetch callback order
        return;
      }

      if (res && res.code === 0) {
        const { results, range } = res.data;
        setTemplatList(results);
        setTotal(range.total);
      }
    } finally {
      if (fetchId !== fetchRef.current) {
        // for fetch callback order
        return;
      }
      setListLoading(false);
    }
  }

  // hover显示操作按钮
  const handleHoverItem = (id, operate) => {
    if (operate === 'enter') {
      setCurrentHoverId(id);
    } else {
      setCurrentHoverId('');
    }
  };

  // 打开从模板创建弹框
  const openTemplateModal = (templateInfo) => {
    setTemplateInfo(templateInfo);
    modalRef.current.showModal();
  };

  // tab栏
  const categoryTabChange = (key: string) => {
    setPage(1);
    setActiveKey(key);
  };

  // 点击类目tab
  const typeTabChange = (id: String) => {
    setPage(1);
    setTemplateKey(id);
  }

  useEffect(() => {
    if (open) {
      queryTemplateList();
    }
  }, [page, pageSize, search, open, activkey, templateKey]);

  return (<>
    <Modal
      title={t('createAppUsingTemplate')}
      width='1400px'
      open={open}
      centered
      footer={null}
      onCancel={() => setOpen(false)}
      className='template-list'
    >
      <div className='template-container'>
        <div className='tempate-header'>
          <div>
            {tabItems.map((item) => {
              return (
                <span
                  className={activkey === item.key ? 'tab-active app-card-tab' : 'app-card-tab'}
                  key={item.key}
                  onClick={() => categoryTabChange(item.key)}>
                  {item.label}
                </span>
              )
            })}
          </div>
          <Input
            showCount
            maxLength={64}
            placeholder={t('search')}
            style={{ width: '368px', height: '32px' }}
            prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
            onChange={(e) => handleSearch(e.target.value)}
          />
        </div>
        <Tabs
          items={tabs}
          activeKey={templateKey}
          onChange={(key: string) => typeTabChange(key)}
          style={{ width: '100%', textAlign: 'center' }}
          centered={true}
        />
        {listLoading ? <Spin style={{ width: '100%' }} />
          : templatList.length > 0 ?
            <div className='template-list'>
              {templatList.map((item: any) => (
                <div className='card-box'
                  key={item.id}
                  onMouseEnter={() => handleHoverItem(item.id, 'enter')}
                  onMouseLeave={() => handleHoverItem(item.id, 'leave')}>
                  <AppCard
                    cardInfo={item}
                    showOptions={false}
                    isTemplate
                    isCurrentHover={currentHoverId === item.id}
                    openTemplateModal={openTemplateModal}
                  />
                </div>
              ))}
            </div> :
            <div className='empty-box'>
              <Empty />
            </div>
        }
        <div className='page-conatiner'>
          <Pagination
            current={page}
            onChange={paginationChange}
            pageSizeOptions={[8, 16, 32, 60]}
            total={total}
            pageSize={pageSize}
          />
        </div>
      </div>
      {/* 模板创建弹窗 */}
      <EditModal
        type='template'
        modalRef={modalRef}
        appInfo={templateInfo}
        addAippCallBack={addAippCallBack}
      />
    </Modal>
  </>)
};

export default TemplateList;
