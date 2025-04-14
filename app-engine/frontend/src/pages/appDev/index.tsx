/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect, useRef } from 'react';
import { Input, Dropdown, Modal, Spin, Tabs } from 'antd';
import { QuestionCircleOutlined, DownOutlined } from '@ant-design/icons';
import { useHistory } from 'react-router-dom';
import { Icons } from '@/components/icons';
import { exportApp } from '@/shared/http/aipp';
import { deleteAppApi, queryAppDevApi } from '@/shared/http/appDev';
import { debounce, setSpaClassName, queryAppCategories, getCookie } from '@/shared/utils/common';
import { Message } from '@/shared/utils/message';
import { TENANT_ID } from '../chatPreview/components/send-editor/common/config';
import { tabItems, items } from './common';
import { useTranslation } from 'react-i18next';
import { useAppSelector } from '@/store/hook';
import { cloneDeep } from 'lodash';
import AppCard from '@/components/appCard';
import EditModal from '../components/edit-modal';
import Pagination from '@/components/pagination';
import UploadApp from './components/upload-app';
import TemplateList from '@/pages/components/template-list';
import WarningIcon from '@/assets/images/warning_icon.svg';
import CreateImg from '@/assets/images/ai/create.png';
import TemplateImg from '@/assets/images/ai/create2.png';
import ImportImg from '@/assets/images/ai/import.png';
import './index.scoped.scss';

/**
 * 应用开发页面
 *
 * @return {JSX.Element}
 * @constructor
 */
const AppDev: React.FC = () => {
  const { t } = useTranslation();
  const tenantId = TENANT_ID;
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [listLoading, setListLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(11);
  const [search, setSearch] = useState('');
  const [appData, setAppData] = useState([]);
  const [tabs, setTabs] = useState([]);
  const [typeKey, setTypeKey] = useState('all');
  const [categoryKey, setCategoryKey] = useState('all');
  const [statusKey, setStatusKey] = useState(items[0].key);
  const [statusLabel, setStatusLabel] = useState(items[0].label);
  const uploadRef = useRef<any>();
  const currentApp = useRef<any>({});
  const tempalteListRef = useRef<any>(null);
  const readOnly = useAppSelector((state) => state.chatCommonStore.readOnly);
  const navigate = useHistory().push;

  // 应用导入点击回调
  function handleCreateClick() {
    uploadRef.current.openUpload();
  };

  // 查询应用列表
  async function queryApps() {
    const params = {
      offset: (page - 1) * pageSize,
      limit: pageSize,
      name: search || '',
      app_type: typeKey !== 'all' ? typeKey : null,
      state: statusKey !== 'all' ? statusKey : null,
      app_category: categoryKey !== 'all' ? categoryKey : null
    };
    setListLoading(true);
    try {
      const res: any = await queryAppDevApi(tenantId, params);
      if (res && res.code === 0) {
        const { results, range } = res.data;
        const arr = results.map((v: any) => {
          const { description, icon } = v.attributes;
          return {
            ...v,
            description,
            icon
          };
        });
        setAppData(arr);
        setTotal(range.total);
      }
    } finally {
      setListLoading(false);
    }
  }

  // tab点击回调
  function tabChange(key: string) {
    setPage(1);
    setCategoryKey(key);
  };

  // 点击类目tab
  const handleTypeChange = (id: String) => {
    setPage(1);
    setTypeKey(id);
  }

  // 分页change回调方法
  const paginationChange = (curPage: number, curPageSize: number) => {
    if (page !== curPage) {
      setPage(curPage);
    }
    if (pageSize !== curPageSize) {
      setPageSize(curPageSize);
    }
  };

  // 创建应用弹窗
  let modalRef = useRef<any>();
  const [modalInfo, setModalInfo] = useState({});
  const create = () => {
    setModalInfo(() => {
      modalRef.current.showModal();
      return {
        name: '',
        attributes: {
          description: '',
          icon: '',
          app_type: typeKey !== 'all' ? typeKey : tabs?.[1]?.key,
        },
      };
    });
  };

  // 应用添加成功回调
  function addAippCallBack(appId: string, aippId: string, appCategory?: string) {
    if (appCategory && appCategory === 'workflow') {
      navigate({
        pathname: `/app-develop/${tenantId}/app-detail/${appId}/${aippId}`,
        search: '?type=chatWorkflow',
      });
      return;
    }
    if (aippId) {
      navigate(`/app-develop/${tenantId}/app-detail/${appId}/${aippId}`);
      return;
    }
    navigate(`/app-develop/${tenantId}/app-detail/${appId}`);
  };

  // 应用名称搜索
  const handleSearch = debounce(onSearchValueChange, 500);
  function onSearchValueChange(newSearchVal: string) {
    if (newSearchVal !== search) {
      setPage(1);
      setSearch(newSearchVal);
    }
  };

  // 点击卡片
  function clickCard(item: any, e: any) {
    navigate(`/app-develop/${tenantId}/appDetail/${item.id}`);
    sessionStorage.setItem('defaultKey', '1');
  };

  // 点击更多操作选项
  function clickMore(type: string, appInfo: string) {
    currentApp.current = appInfo;
    if (type === 'delete') {
      setOpen(true);
    } else if (type === 'export') {
      handleExportApp(cloneDeep(appInfo));
    }
  };

  // 应用导出
  const handleExportApp = async (curInfo) => {
    const res = await exportApp(tenantId, curInfo.id);
    const blob = new Blob([JSON.stringify(res)], { type: 'application/json' })
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${curInfo.name}应用.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  };

  // 删除应用
  async function deleteApp() {
    const storage = {
      appId: currentApp.current.id,
      type: 'deleteApp'
    }
    setLoading(true);
    try {
      const res: any = await deleteAppApi(tenantId, currentApp.current.id);
      if (res.code === 0) {
        queryApps();
        setOpen(false);
        Message({ type: 'success', content: t('deleteAppSuccess') });
        localStorage.setItem('storageMessage', JSON.stringify(storage));
        if (appData.length === 1) {
          setPage(1);
          return;
        }
      } else {
        Message({ type: 'error', content: res.msg || t('deleteFail') });
      }
    } finally {
      setLoading(false);
    }
  };

  // 应用状态查询点击回调
  const clickItem = (e) => {
    const { key } = e;
    const label = items.find(status => status.key === key)?.label;
    setPage(1);
    setStatusLabel(label);
    setStatusKey(key);
  };

  // 获取应用列表
  useEffect(() => {
    queryApps();
  }, [page, pageSize, search, categoryKey, typeKey, statusKey]);

  // 删除弹窗title组件
  const DeleteTitle = () => {
    return <div className='delete-title'>
      <img src={WarningIcon}></img>
      <span>{t('deleteAppModalTitle')}</span>
    </div>
  };

  // 联机帮助
  const onlineHelp = () => {
    window.open(`${window.parent.location.origin}/help${getCookie('locale').toLocaleLowerCase() === 'en-us' ? '/en' : '/zh'}/application_development.html`, '_blank');
  }

  useEffect(() => {
    const fetchTab = async () => {
      const newTab = await queryAppCategories(tenantId, false);
      setTabs(newTab);
    };
    fetchTab();
  }, []);

  return (
    <div className={setSpaClassName('apps_root')}>
      <div className='apps_header'>
        <div className='apps_title'>{t('appDevelopment')}</div>
        { process.env.PACKAGE_MODE === 'spa' && <QuestionCircleOutlined onClick={onlineHelp} />}
      </div>
      <div className='apps_main'>
        <div className='apps-haeader-content'>
          <div className='tabs'>
            {tabItems.map((item) => {
              return (
                <span
                  className={categoryKey === item.key ? 'tab-active app-card-tab' : 'app-card-tab'}
                  key={item.key}
                  onClick={() => tabChange(item.key)}>
                  {item.label}
                </span>
              )
            })}
          </div>
          <div className='operatorArea'>
            <Dropdown menu={{ items, activeKey: statusKey, onClick: (e) => clickItem(e) }}>
              <div className='status-dropdown'>
                <span>{statusLabel}</span>
                <DownOutlined />
              </div>
            </Dropdown>
            <Input
              maxLength={64}
              placeholder={t('search')}
              style={{ width: '368px', height: '32px', marginLeft: '16px' }}
              prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
              onChange={(e) => handleSearch(e.target.value)}
            />
          </div>
        </div>
        <Tabs
          items={tabs}
          activeKey={typeKey}
          onChange={(key: string) => handleTypeChange(key)}
          style={{ width: '100%', textAlign: 'center' }}
          centered={true}
        />
        {/* 应用卡片列表 */}
        <Spin spinning={listLoading}>
          <div className='card_list'>
            { !readOnly &&  
              <div className='card_box card_box_add'>
                <div className='add_title'>{t('create')}</div>
                <div><img src={CreateImg} /><span onClick={() => create()}>{t('ceateBlankApplication')}</span></div>
                <div><img src={TemplateImg} /><span onClick={() => tempalteListRef.current.open()}>{t('templateCreate')}</span></div>
                <div className='split-line'></div>
                <div><img src={ImportImg} /><span onClick={handleCreateClick}>{t('importApplication')}</span></div>
              </div>
            }
            {
              appData.map((item: any) => (
                <div className='card_box' key={item.id} onClick={(e) => clickCard(item, e)}>
                  <AppCard cardInfo={item} clickMore={clickMore} readOnly={readOnly} showOptions />
                </div>
              ))
            }
          </div>
        </Spin>
        <div className='page_box'>
          <Pagination
            current={page}
            onChange={paginationChange}
            pageSizeOptions={[11, 23, 35, 47]}
            total={total}
            pageSize={pageSize}
          />
        </div>
      </div>
      {/*创建弹窗*/}
      <EditModal
        type='add'
        modalRef={modalRef}
        appInfo={modalInfo}
        addAippCallBack={addAippCallBack}
      />
      {/* 删除弹窗 */}
      <Modal
        title={<DeleteTitle />}
        width='380px'
        open={open}
        centered
        onOk={() => deleteApp()}
        onCancel={() => setOpen(false)}
        okButtonProps={{ loading }}
        okText={t('ok')}
        cancelText={t('cancel')}
        destroyOnClose
      >
        <p>{t('deleteAppModalAlert')}</p>
      </Modal>
      {/* 应用导入 */}
      <UploadApp
        uploadRef={uploadRef}
        tenantId={tenantId}
        addAippCallBack={addAippCallBack}
      ></UploadApp>
      <TemplateList tempalteRef={tempalteListRef} tabs={tabs}/>
    </div>
  );
};
export default AppDev;
