/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect } from 'react';
import { Tabs, Input, Tag, Button, Spin, Dropdown } from 'antd';
import Pagination from '@/components/pagination/index';
import PluginCard from '@/components/plugin-card';
import WorkflowCard from '@/components/plugin-card/workFlowCard';
import { getPlugins, getPluginWaterFlow } from '@/shared/http/plugin';
import { Icons } from '@/components/icons';
import { PluginCardTypeE, sourceTabs } from './helper';
import { debounce } from '@/shared/utils/common';
import { Message } from '@/shared/utils/message';
import UploadToolDrawer from './upload/uploadTool';
import EmptyItem from '@/components/empty/empty-item';
import { useTranslation } from 'react-i18next';
import { useAppSelector } from '@/store/hook';
import { useHistory } from 'react-router-dom';
import CreateWorkfowDrawer from './upload/createWorkflow';
import type { MenuProps } from 'antd';
import './styles/market.scss';

const MarketItems = ({ reload, readOnly }) => {
  const { t } = useTranslation();
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(8);
  const [name, setName] = useState('');
  const [selectedSource, setSelectedSource] = useState(sourceTabs?.[0]?.key);
  const [pluginData, setPluginData] = useState([]);
  const [isOpenPlugin, setIsOpenPlugin] = useState(0);
  const [loading, setLoading] = useState(false);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const isAutoOpen = useAppSelector((state) => state.commonStore.isAutoOpen);
  const history = useHistory().push;
  const [openCreateDrawer, setOpenCreateDrawer] = useState(0);
  const currentUser = localStorage.getItem('currentUser') || '';

  useEffect(() => {
    if (selectedSource === 'WATERFLOW') {
      getWaterFlowList();
      return
    }
    getPluginList();
  }, [selectedSource, name, pageNum, pageSize, reload]);

  // 获取插件列表
  const getPluginList = () => {
    let regex = /&/g;
    let result = name.match(regex);
    if (result && result.length) {
      setPluginData([]);
      setTotal(0);
      return;
    }
    let params: any = {
      name,
      pageNum: pageNum,
      pageSize
    }
    if (selectedSource === 'APP') {
      params.excludeTags = selectedSource;
    } else if (selectedSource === 'MINE') {
      params.creator = currentUser;
      params.isBuiltin = true;
    } else {
      params.includeTags = selectedSource;
    }
    setLoading(true);
    getPlugins(params).then(({ data, total }) => {
      setTotal(total);
      setPluginData(data || []);
      setLoading(false);
    }).catch(() => {
      setLoading(false);
    });
  };
  // 获取工具列表
  const getWaterFlowList = () => {
    getPluginWaterFlow(tenantId, {
      offset: (pageNum - 1) * pageSize,
      limit: pageSize,
      type: 'waterFlow',
      name,
    }).then(({ data }) => {
      const { results, range } = data;
      const list = results || [];
      list.forEach(item => {
        item.mapType = 'waterFlow';
      })
      setTotal(range.total || 0);
      setPluginData(list);
    }).catch(() => {
      setTotal(0);
      setPluginData([]);
    });
  }
  // 分页
  const selectPage = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
    if (pageSize !== curPageSize) {
      setPageSize(curPageSize);
    }
  };
  // 名称搜索
  const filterByName = (value: string) => {
    if (value !== name) {
      setPageNum(1);
      setName(value);
    }
  };
  const handleSearch = debounce(filterByName, 1000);
  const uploadAdd = (e) => {
    if (total >= 3000) {
      Message({ type: 'warning', content: t('uploadOptions') });
      return;
    }
    setIsOpenPlugin(e.timeStamp);
  }
  const workFlow = (e) => {
    setOpenCreateDrawer(e.timeStamp);
  };
  // 下拉
  const items: MenuProps['items'] = [
    {
      key: '1',
      label: <div onClick={(e) => uploadAdd(e)}>{t('customPlugin')}</div>,
    },
    {
      key: '2',
      label: <div onClick={() => history({ pathname: '/http' })}>{t('httpPlugin')}</div>,
    },
    {
      key: '3',
      label: <div onClick={(e) => workFlow(e)}>{t('workflow')}</div>,
    },
  ];

  useEffect(() => {
    if (isAutoOpen) {
      uploadAdd({ timeStamp: Date.now() });
    }
  }, [isAutoOpen]);

  // tabs切换回调
  const tabsOnChange = (key:string) =>{
    setSelectedSource(key);
    setPageNum(1);
  }
  
  return (
    <div className='aui-block market-block'>
      <div className='market-search'>
        <Input
          showCount
          maxLength={20}
          placeholder={t('search')}
          className='market-input'
          onChange={(e) => handleSearch(e.target.value)}
          prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
          defaultValue={name}
        />
        { !readOnly &&  <Dropdown menu={{ items }}>
          <Button type='primary' className='market-button'>
            {t('upload')}
          </Button>
        </Dropdown> }
        <UploadToolDrawer openSignal={isOpenPlugin} refreshPluginList={getPluginList} />
      </div>
      <Tabs
        items={sourceTabs}
        activeKey={selectedSource}
        onChange={(key: string) => tabsOnChange(key)}
        style={{ width: '100%', textAlign: 'center' }}
        centered={true}
      />
      <Spin spinning={loading}>
        {pluginData && pluginData.length > 0 ?
          <>
            <div className='market-card' >
              {pluginData.map((card: any) => (
                card.mapType === 'waterFlow' ?
                  <WorkflowCard key={card.uniqueName} pluginData={card} type='plugin' getWaterFlowList={getWaterFlowList} /> :
                  <PluginCard
                    key={card.pluginId}
                    getPluginList={getPluginList}
                    pluginData={card}
                    cardType={PluginCardTypeE.MARKET}
                    pluginId={card.pluginId}
                    readOnly={readOnly}
                  />
              ))}
            </div>
          </>
          :
          <div className='market-empty'>
            <EmptyItem />
          </div>
        }
        <div className='market-page'>
          <Pagination
            total={total}
            current={pageNum}
            onChange={selectPage}
            pageSizeOptions={[8, 16, 32, 60]}
            pageSize={pageSize} />
        </div>
      </Spin>
      <CreateWorkfowDrawer openSignal={openCreateDrawer} />
    </div>
  );
};
export default MarketItems;
