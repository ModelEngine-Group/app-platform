/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect } from 'react';
import { Input, Spin } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';
import { Icons } from '@/components/icons';
import { queryAppsApi } from '@/shared/http/apps';
import AppCard from '@/components/appCard';
import { debounce, getCookie, setSpaClassName } from '@/shared/utils/common';
import { useHistory } from 'react-router-dom';
import { deleteAppApi } from '@/shared/http/appDev';
import Pagination from '@/components/pagination';
import Empty from '@/components/empty/empty-item';
import { TENANT_ID } from '../chatPreview/components/send-editor/common/config';
import { useTranslation } from 'react-i18next';
import './index.scoped.scss';

const Apps: React.FC = () => {
  const tenantId = TENANT_ID;
  const { t } = useTranslation();
  const navigate = useHistory().push;
  const [appData, setAppData] = useState<any[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(8);
  const [search, setSearch] = useState('');
  const [listLoading, setListLoading] = useState(false);

  async function queryApps() {
    const params = {
      pageNum: page,
      pageSize,
      includeTags: 'APP',
      name: search
    };
    setListLoading(true);
    try {
      const res: any = await queryAppsApi(tenantId, params);
      if (res.code === 0) {
        const { data, total } = res;
        setAppData([...data]);
        setTotal(total);
      }
    } finally {
      setListLoading(false);
    }
  }
  const paginationChange = (curPage: number, curPageSize: number) => {
    if (page !== curPage) {
      setPage(curPage);
    }
    if (pageSize != curPageSize) {
      setPageSize(curPageSize);
    }
  }

  // 搜索
  function onSearchValueChange(newSearchVal: string) {
    if (newSearchVal !== search) {
      setPage(1);
      setSearch(newSearchVal);
    }
  }
  const handleSearch = debounce(onSearchValueChange, 500);

  // 点击卡片
  function clickCard(item: any, e: any) {
    let id = item.runnables?.APP?.appId;
    let aippId = item.runnables?.APP?.aippId;
    if (aippId) {
      navigate(`/app/${tenantId}/chat/${id}/${aippId}`);
    } else {
      navigate(`/app/${tenantId}/chat/${id}`);
    }
  }

  // 点击更多操作选项
  function clickMore(type: string, appId: string) {
    if (type === 'delete') {
      deleteApp(appId);
    }
  }
  // 删除
  async function deleteApp(appId: string) {
    const res: any = await deleteAppApi(tenantId, appId);
    if (res.code === 0) {
      if (appData.length === 1) {
        setPage(1);
        return;
      }
      queryApps();
    }
  }

  // 联机帮助
  const onlineHelp = () => {
    window.open(`${window.parent.location.origin}/help${getCookie('locale').toLocaleLowerCase() === 'en-us' ? '/en' : '/zh'}/application_market.html`, '_blank');
  }

  useEffect(() => {
    queryApps();
  }, [page, pageSize, search]);
  return (
    <div className={setSpaClassName('apps_root')}>
      <div className='apps_header'>
        <div className='apps_title'>{t('applicationMarket')}</div>
        { process.env.PACKAGE_MODE === 'spa' && <QuestionCircleOutlined onClick={onlineHelp} />}
      </div>
      <div className='apps_main_market'>
        <div className='operatorArea'>
          <Input
            className='apps-search-input'
            showCount
            maxLength={64}
            placeholder={t('search')}
            prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
            onChange={(e) => handleSearch(e.target.value)}
          />
        </div>
        <Spin spinning={listLoading}>
          {appData.length > 0 ?
            <div className='card_list'>
              {appData.map((item: any) => (
                <div
                  className='card_box'
                  key={item.uniqueName}
                  onClick={(e) => clickCard(item, e)}
                >
                  <AppCard cardInfo={item} clickMore={clickMore} showOptions={false} />
                </div>
              ))}
            </div> :
            <div className='empty-box'>
              <Empty />
            </div>
          }
        </Spin>
        <div className='page_box'>
          <Pagination
            current={page}
            onChange={paginationChange}
            pageSizeOptions={[8, 16, 32, 60]}
            total={total}
            pageSize={pageSize}
          />
        </div>
      </div>
    </div>
  );
};
export default Apps;
