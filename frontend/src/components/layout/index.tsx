/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect, Suspense } from 'react';
import type { MenuProps } from 'antd';
import { Layout, Menu } from 'antd';
import { MenuFoldOutlined } from '@ant-design/icons';
import {
  Route,
  useHistory,
  useLocation,
  Redirect,
  Switch
} from 'react-router-dom';
import {
  routeList,
  flattenRoute,
  getRouteByKey,
  getMenus,
} from '../../router/route';
import { Provider } from 'react-redux';
import { Icons, KnowledgeIcons } from '../icons/index';
import store from '@/store/store';
import { setSpaClassName } from '@/shared/utils/common';
import { getUser, getOmsUser, getRole, getChatPluginList } from '../../pages/helper';
import './style.scoped.scss';

const { Content, Sider } = Layout;
type MenuItem = Required<MenuProps>['items'][number];
const items: MenuItem[] = getMenus(routeList);
const flattenRouteList = flattenRoute(routeList);

/**
 * 页面整体布局组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const AppLayout: React.FC = () => {
  const [showMenu, setShowMenu] = useState(false);
  const [defaultActive, setDefaultActive] = useState<string[]>([])
  const navigate = useHistory().push;
  const location = useLocation();
  
  /**
   * @description 从后往前遍历路由 父子路由，子路由前缀需要是父路由
   * @pathname 当前路径
   * */
  const getCurrentRoute = (pathname: string) => {
    // 拆开路由
    const pathGroup = pathname.split('/').filter(item => item !== '');
    if (pathGroup?.length) {
      let len = pathGroup?.length - 1;
      while (len >= 0) {
        const key = '/' + pathGroup.slice(0, len + 1).join('/');
        let route = getRouteByKey(flattenRouteList, key);
        if (route && !route?.hidden) {
          setDefaultActive([key]);
          break;
        }
        len--;
      }
    } else {
      setDefaultActive(['/app-develop']);
    }
  }
  const menuClick = (e: any) => {
    navigate(e.key);
  };

  const colorBgContainer = '#F0F2F4';
  const setClassName = () => {
    if (location.pathname.includes('home')) {
      return `${setSpaClassName('home-chat')} layout-container`
    } else if (location.pathname.includes('app')) {
      return `${setSpaClassName('home-app')} layout-container`
    }
    return 'layout-container'
  }
  const layoutValidate = () => {
    if (process.env.NODE_ENV !== 'development' && process.env.PACKAGE_MODE !== 'common') {
      return false
    }
    if (location.pathname.includes('/chat/') && !location.pathname.includes('/app/')){
      return false;
    }
    return true;
  }
  const isSpaMode = () => {
    return  (process.env.NODE_ENV !== 'development' && process.env.PACKAGE_MODE !== 'common')
  }
  useEffect(() => {
    const { pathname, search } = location;
    const route = getRouteByKey(flattenRouteList, pathname);
    if (pathname.includes('/app-detail/')) {
      setShowMenu(false);
    } else if (!route?.hidden || pathname.includes('/http')) {
      setShowMenu(true);
    } else {
      setShowMenu(false);
    }
    getCurrentRoute(pathname);
    parent?.window?.navigatePath?.('appengine', pathname + search);
  }, [location]);

  useEffect(() => {
    if (process.env.PACKAGE_MODE === 'common') {
    // TODO: 待后端接口归一后调用 getUser()
    } else if (!location.pathname.includes('/chat/')){
      getOmsUser();
      getRole();
    }
    getChatPluginList();
  }, [])
  return (
    <Layout>
      { layoutValidate() && (
        <>
          <Sider
            collapsible
            collapsed={false}
            onCollapse={() => setShowMenu(false)}
            trigger={null}
            width={showMenu ? 220 : 0}
            className='layout-sider'
          >
            <div className='layout-sider-header'>
              <div className='layout-sider-content'>
                <Icons.logo />
                <span className='layout-sider-title'>ModelEngine</span>
              </div>
              <MenuFoldOutlined
                style={{ color: '#6d6e72' }}
                onClick={() => setShowMenu(false)}
              />
            </div>
            <Menu
              className='menu'
              theme='dark'
              selectedKeys={defaultActive}
              mode='inline'
              items={items}
              onClick={menuClick}
            />
          </Sider>
          <div className='layout-sider-folder'>
            <KnowledgeIcons.menuFolder onClick={() => setShowMenu(true)} />
          </div>
        </>
      )
    }
      <Layout className={setClassName()}>
        <Provider store={store}>
          <Content style={{ padding: (layoutValidate() || isSpaMode()) ? '0 16px' : '0', background: colorBgContainer }}>
            <Suspense fallback={<div style={{display:"none"}}>loading</div>}>
              <Switch>
                {flattenRouteList.map((route) => (
                  <Route
                    exact
                    path={route.key}
                    key={route.key}
                    component={route.component}
                  />
                ))}
                <Route exact path='/' key='/' >
                  <Redirect to='/app-develop' />
                </Route>
              </Switch>
            </Suspense>
          </Content>
        </Provider>
      </Layout>
    </Layout>
  );
};

export default AppLayout;
