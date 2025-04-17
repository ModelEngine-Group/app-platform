/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import {Tabs} from 'antd';
import { setSpaClassName } from '@/shared/utils/common';
import GoBack from '@/components/go-back/GoBack';
import AppOverview from './overview';
import AppAnalyse from './analyse';
import FeedBack from './feedback';
import { useTranslation } from 'react-i18next';
import './style.scoped.scss';

const AppDetail: React.FC = () => {
  const [tabsKey, setTabsKey] = useState('1');
  const onChange = (key: string) => {
    sessionStorage.setItem('defaultKey', key);
    setTabsKey(key);
  };
  const { t } = useTranslation();
  const items = [
    {
      key: '1',
      label: t('overview'),
      children: <AppOverview />,
    },
    {
      key: '2',
      label: t('analyse'),
      children: <AppAnalyse />,
    },
    {
      key: '3',
      label: t('feedback'),
      children: <FeedBack />,
    },
  ];
  useEffect(() => {
    setTabsKey(sessionStorage.getItem('defaultKey') as string || '1');
  }, [tabsKey]);

  return (
    <div className={setSpaClassName('app-fullpage')}>
      <div className='aui-header-1'>
        <div className='aui-title-1'>
          <GoBack path={'/app-develop'} title={t('appDetail')} />
        </div>
      </div>
      <div className='aui-block appengine-detail' style={{ paddingTop: 0 }}>
        <Tabs type='card' activeKey={tabsKey} items={items} onChange={onChange} />
        <div />
      </div>
    </div>
  );
};

export default AppDetail;
