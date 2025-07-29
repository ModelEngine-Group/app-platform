/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect } from 'react';
import { Input, Button, Dropdown, Tabs, Space } from 'antd';
import { DownOutlined } from '@ant-design/icons';
import { getPluginTool, getPluginWaterFlow } from '../../shared/http/plugin';
import { Icons } from '../../components/icons';
import UploadToolDrawer from './upload/uploadTool';
import { useAppSelector } from '../../store/hook';
import { useTranslation } from 'react-i18next';
import i18n from '@/locale/i18n';
import WorkflowCard from '../../components/plugin-card/workFlowCard';
import CreateWorkfowDrawer from './upload/createWorkflow';
import Pagination from '../../components/pagination/index';
import PluginCard from '../../components/plugin-card';
import '../../index.scss';
import './style.scoped.scss';

enum tabItemE {
  TOOL = 'FIT',
  TOOLFLOW = 'waterFlow',
}


const tabItems = [
  {
    key: tabItemE.TOOL,
    label: i18n.t('tool'),
  },
  {
    key: tabItemE.TOOLFLOW,
    label: i18n.t('workflow'),
  },
];

const appItems = [
  {
    key: 'draft',
    label: i18n.t('draft'),
  },
  {
    key: 'published',
    label: i18n.t('active'),
  },
  {
    key: 'avaliable',
    label: i18n.t('alreadyShelves'),
  },
];

const MyPlugins = () => {
  const { t } = useTranslation();
  const [total, setTotal] = useState(0);
  const [name, setName] = useState<string>(undefined);
  const [pluginData, setPluginData] = useState([]);
  const [openUploadDrawer, setOpenUploadDrawer] = useState(0); // 数字，非布尔值
  const [openCreateDrawer, setOpenCreateDrawer] = useState(0); // 数字，非布尔值
  const [currentTab, setCurrentTab] = useState(tabItemE.TOOL);
  const [pagination, setPagination] = useState({ pageNum: 1, pageSize: 8 });
  const [refreshSignal, setRefreshSignal] = useState(0);
  const [activeKey, setActiveKey] = useState('');
  const tenantId = useAppSelector((state) => state.appStore.tenantId);

  const onTabChange = (activeKey) => {
    setCurrentTab(activeKey);
    setPagination({ pageNum: 1, pageSize: 8 });
    setRefreshSignal(new Date().valueOf());
  };

  const getPluginList = () => {
    const { pageNum, pageSize } = pagination;
    if (currentTab === tabItemE.TOOL) {
      getPluginTool(tenantId, { pageNum, pageSize, name }).then(({ data }) => {
        setTotal(data?.total);
        setPluginData(data?.toolData);
      });
    }
    if (currentTab === tabItemE.TOOLFLOW) {
      getPluginWaterFlow(tenantId, {
        offset: (pageNum - 1) * pageSize,
        limit: pageSize,
        type: currentTab,
        name,
      }).then(({ data }) => {
        setTotal(data?.range?.total);
        setPluginData(data?.results);
      });
    }
  };

  const selectPage = (curPage: number, curPageSize: number) => {
    setPagination({ pageNum: curPage, pageSize: curPageSize });
    setRefreshSignal(new Date().valueOf());
  };

  const filterByName = (e) => {
    if (e?.target?.value) {
      setName(e.target.value);
      setRefreshSignal(e.timeStamp);
    }
  };
  const refreshPluginList = () => {
    const { pageSize } = pagination;
    setPagination({ pageNum: 1, pageSize });
    setName('');
    getPluginList();
  }
  useEffect(() => {
    getPluginList();
  }, [refreshSignal]);

  useEffect(() => {
    let showDrawer = sessionStorage.getItem('pluginType');
    if (showDrawer) {
      sessionStorage.removeItem('pluginType');
      showDrawer === 'plugin' ? setOpenUploadDrawer(1) : '';
    }
  }, [])
  return (
    <div className='aui-block myplugin'>
      <Tabs defaultActiveKey={activeKey} items={tabItems} onChange={onTabChange} />
      <div className='top-operate'>
        <div className='button-display'>
          <Button
            type='primary'
            iconPosition='end'
            onClick={(e) => {
              if (currentTab === tabItemE.TOOL) {
                setOpenUploadDrawer(e.timeStamp);
              }
              if (currentTab === tabItemE.TOOLFLOW) {
                setOpenCreateDrawer(e.timeStamp)
              }
            }}
          >
            {t('create')}
          </Button>
          <Input
            disabled
            showCount
            maxLength={20}
            placeholder={t('search')}
            onPressEnter={(e) => filterByName(e)}
            prefix={<Icons.search color='rgb(230, 230, 230)' />}
            defaultValue={name}
          />
        </div>
        <div>
          <div hidden>
            <Dropdown menu={{ items: appItems }} trigger={['click']}>
              <Space className='app-select'>
                {t('allApplications')}
                <DownOutlined />
              </Space>
            </Dropdown>
          </div>
        </div>
      </div>
      <div className='plugin-cards'>
        {pluginData.map((card: any) =>
          currentTab === tabItemE.TOOL ? (
            <PluginCard key={card.uniqueName} pluginData={card} />
          ) : (
              <WorkflowCard key={card.uniqueName} pluginData={card} />
            )
        )}
      </div>
      <div style={{ paddingTop: 16 }}>
        <Pagination
          total={total}
          current={pagination?.pageNum}
          onChange={selectPage}
          pageSize={pagination?.pageSize}
          pageSizeOptions={[8, 16, 32, 60]}
        />
      </div>
      <UploadToolDrawer openSignal={openUploadDrawer} refreshPluginList={refreshPluginList} />
      <CreateWorkfowDrawer openSignal={openCreateDrawer} />
    </div>
  );
};

export default MyPlugins;
