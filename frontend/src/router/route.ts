/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import type { MenuProps } from 'antd';
import { ReactElement, lazy } from 'react';
import { Icons } from '../components/icons/index';
const IntelligentForm = lazy(()=> import('../pages/intelligent-form'));
const Plugin = lazy(()=> import('../pages/plugin'));
const ChatHome = lazy(()=> import('../pages/chatEngineHome/index'));
const ChatRunning = lazy(()=> import('../pages/chatRunning/index'));
const AppDetail = lazy(()=> import('../pages/appDetail'));
const AippIndex = lazy(() => import('../pages/aippIndex'));
const AddFlow = lazy(()=> import('../pages/addFlow'));
const FlowDetail = lazy(()=> import('../pages/detailFlow'));
const Apps = lazy(()=> import('../pages/apps'));
const AppDev = lazy(()=> import('../pages/appDev/index'));
const PlugeDetail = lazy(()=> import('../pages/plugin/detail/plugin-list'));
const PlugeFlowDetail = lazy(()=> import('../pages/plugin/detail/plugin-flow-detail'));
const ViewReport = lazy(()=> import('../pages/appDetail/evaluate/task/viewReport'));
const HttpTool = lazy(()=> import('../pages/httpTool'));

import i18n from '../locale/i18n';

export type MenuItem = Required<MenuProps>['items'][number] & {
  component?: (() => ReactElement) | React.FC<any>;
  children?: MenuItem[] | null;
  label: string;
  key: string;
  hidden?: boolean;
  title?: string;
};

export const routeList: MenuItem[] = [
  {
    key: '/chat/:uid',
    icon: Icons.app({}),
    label: i18n.t('applicationMarket'),
    component: ChatRunning,
    hidden: true,
  },
  {
    key: '/app',
    icon: Icons.app({}),
    label: i18n.t('applicationMarket'),
    component: Apps,
    children: [
      {
        key: '/app/:tenantId/chat/:appId/:aippId?',
        icon: Icons.app({}),
        label: '',
        component: ChatRunning,
        hidden: true,
      },
    ],
  },
  {
    key: '/app-develop',
    icon: Icons.app({}),
    label: i18n.t('appDevelopment'),
    component: AppDev,
    children: [
      {
        key: '/app-develop/:tenantId/app-detail/:appId/:aippId?',
        icon: Icons.app({}),
        label: i18n.t('arrange'),
        component: AippIndex,
        hidden: true,
      },
      {
        key: '/app-develop/:tenantId/add-flow/:appId',
        icon: Icons.app({}),
        label: i18n.t('addWorkflow'),
        component: AippIndex,
        hidden: true,
      },
      {
        key: '/app-develop/:tenantId/flow-detail/:appId',
        icon: Icons.app({}),
        label: i18n.t('workflow'),
        component: FlowDetail,
        hidden: true,
      },
      {
        key: '/app-develop/:tenantId/appDetail/:appId',
        icon: Icons.app({}),
        label: '',
        component: AppDetail,
        hidden: true,
        children: [
          {
            key: '/app-develop/:tenantId/appDetail/:appId/task/viewReport',
            icon: Icons.app({}),
            label: '',
            component: ViewReport,
            hidden:true
          },
        ],
      },
    ],
  },
  {
    key: '/intelligent-form',
    icon: Icons.app({}),
    label: i18n.t('intelligentForm'),
    component: IntelligentForm,
    children: [],
  },
  {
    key: '/plugin',
    icon: Icons.app({}),
    label: i18n.t('plugin'),
    component: Plugin,
    children: [
      {
        key: '/plugin/detail/:pluginId',
        icon: Icons.app({}),
        label: i18n.t('pluginDetails'),
        component: PlugeDetail,
        hidden: true,
      },
      {
        key: '/plugin/detail-flow/:pluginId',
        icon: Icons.app({}),
        label: i18n.t('pluginDetails2'),
        component: PlugeFlowDetail,
        hidden: true,
      },
      {
        key: '/http',
        icon: Icons.app({}),
        label: i18n.t('thirdPartyTool'),
        component: HttpTool,
        hidden: true,
      }
    ],
  }
];

// 生成菜单
export const getMenus = (routeList: MenuItem[]): MenuItem[] => {
  const menus: MenuItem[] = routeList.map((route) => {
    let children: MenuItem[] = [];
    if (route.children && route.children.length) {
      children = getMenus(route.children);
    }
    if (children.length) {
      return {
        ...route,
        children,
      };
    } else {
      return {
        ...route,
        children: null,
      };
    }
  });
  return menus.filter((item) => !item?.hidden);
};

// 将路由展平
export const flattenRoute = (routeList: MenuItem[]): MenuItem[] => {
  let flattenRouteList: MenuItem[] = [];
  const rootLayer = routeList.map((item) => {
    if (item?.children && item.children.length) {
      flattenRouteList = [...flattenRouteList, ...flattenRoute(item.children)];
    }
    return item;
  });
  return [...rootLayer, ...flattenRouteList];
};

// 根据key值返回路由, 传入展平的数组
export const getRouteByKey = (routeList: MenuItem[], key: string): MenuItem | null => {
  return routeList.find((item) => item.key === key) || null;
};
