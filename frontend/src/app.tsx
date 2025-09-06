/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect } from 'react';
import { HashRouter, BrowserRouter } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import AppLayout from '@/components/layout/index';
import zhCN from 'antd/lib/locale/zh_CN';
import enUS from 'antd/lib/locale/en_US';
import './locale/i18n';
import 'antd/dist/antd.less';
import '@/styles/index.scss';
import '@/styles/common.scss';
import '@/styles/workSpace.scss';
import '@/styles/antStyle.scss';
import './index.scss';
import { getCookie, setCookie } from './shared/utils/common';

let userName = localStorage.getItem('__account_name__') || '';
localStorage.getItem('currentUser') || localStorage.setItem('currentUser', userName);
localStorage.getItem('appChatMap') || localStorage.setItem('appChatMap', JSON.stringify({}));
localStorage.getItem('showFlowChangeWarning') || localStorage.setItem('showFlowChangeWarning', 'true');

const locale = getCookie('language').toLocaleLowerCase();
if (!locale) {
  setCookie('locale', 'zh-cn');
} else {
  if (locale === 'zh_cn') {
    setCookie('locale', 'zh-cn');
  }
  if (locale === 'en_us') {
    setCookie('locale', 'en-us');
  }
}

let isHashRouter = true;
let basename = '/'
if (process.env.PACKAGE_MODE === 'spa') {
  isHashRouter = false;
  if (process.env.NODE_ENV === 'production') {
    basename = '/appengine'
  }
  import(`./styles/appengine-bg-spa.scss`);
} else {
  import(`./styles/appengine-bg.scss`);
}
const RouterComponent = isHashRouter ? HashRouter : BrowserRouter
const AppRouter = ({ children }) => (
  <RouterComponent basename={basename}>
    {children}
  </RouterComponent>
);
export default function App() {
  return (
    <ConfigProvider locale={getCookie('locale').toLocaleLowerCase() === 'en-us' ? enUS : zhCN} autoInsertSpace={true}>
      <AppRouter>
        <AppLayout />
      </AppRouter>
    </ConfigProvider>
  );
}
