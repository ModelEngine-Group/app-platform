/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect, createContext, useContext } from 'react';
import type { MenuProps } from 'antd';
import { Layout, Menu, Avatar, Dropdown } from 'antd';
import { UserOutlined } from '@ant-design/icons';
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
import { KnowledgeIcons } from '../icons/index';
import AidoIcon from '../../assets/images/aido-icon.png';
import CollapseIcon from '../../assets/svg/collapse-icon.svg';
import knowledgeBase from '@/assets/images/knowledge/knowledge-base.png';
import { store } from '@/store';
import { convertImgPath } from '@/common/util';
import { setSpaClassName, updateChatId } from '@/shared/utils/common';
import { getUser, getOmsUser, getRole, getChatPluginList } from '../../pages/helper';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setChatId, setChatList, setChatRunning, setAtChatId } from '@/store/chatStore/chatStore';
import { setAtAppInfo, setAtAppId } from '@/store/appInfo/appInfo';
import { useTranslation } from 'react-i18next';
import { useGuestName } from '@/shared/hooks/useGuestName';
import { handleLogout } from '@/components/userAuthButton';
import { getUsername } from '@/shared/http/aipp';
import HistorySidebar from './history-sidebar';
import ChatHistorySidebar from './chat-history-sidebar';
import './style.scoped.scss';
import './user-dropdown.scss';

const { Content, Sider } = Layout;
type MenuItem = Required<MenuProps>['items'][number];

// 根据当前路由过滤菜单项
const getFilteredMenus = (routeList: any[], currentPath: string): any[] => {
  const filteredMenus = routeList.map((route) => {
    // 判断是否是应用聊天页面：/app/{tenantId}/chat/{appId}
    const isAppChatPage = currentPath.includes('/app/') && currentPath.includes('/chat/');
    // 判断是否是独立聊天页面：/chat/*（不含 /app/）
    const isChatPage = currentPath.includes('/chat/') && !currentPath.includes('/app/');
    
    // 聊天相关页面下不显示"探索"、"工作台"、"工具"
    if (isAppChatPage || isChatPage) {
      if (route.key === '/app' || route.key === '/app-develop' || route.key.startsWith('/tools')) {
        return null;
      }
    }

    // 处理子菜单
    if (route.children && route.children.length) {
      const filteredChildren = getFilteredMenus(route.children, currentPath);
      return {
        ...route,
        children: filteredChildren.length > 0 ? filteredChildren : undefined,
      };
    }

    return route;
  }).filter((item): item is any => item !== null); // 过滤掉 null 值并确保类型安全

  return filteredMenus.filter((item) => !item?.hidden);
};

const items: MenuItem[] = getMenus(routeList);
const flattenRouteList = flattenRoute(routeList);

// 创建Context用于传递setListCurrentList函数
const HistoryContext = createContext<{
  setListCurrentList: (list: any) => void;
}>({
  setListCurrentList: () => {}
});

// 历史记录状态管理组件
const HistoryProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [listCurrentList, setListCurrentList] = useState<any[]>([]);

  return (
    <HistoryContext.Provider value={{ setListCurrentList }}>
      {children}
    </HistoryContext.Provider>
  );
};

// 使用Context的HistorySidebar组件
const HistorySidebarWithContext: React.FC = () => {
  const { setListCurrentList } = useContext(HistoryContext);
  const location = useLocation();

  // 在 /chat/* 路由下使用独立的对话历史组件
  if (location.pathname.includes('/chat/') && !location.pathname.includes('/app/')) {
    return <ChatHistorySidebar setListCurrentList={setListCurrentList} />;
  }

  // 其他路由使用原有的历史记录组件
  return <HistorySidebar setListCurrentList={setListCurrentList} />;
};

/**
 * 页面整体布局组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const AppLayout: React.FC = () => {
  const [showMenu, setShowMenu] = useState(false);
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [defaultActive, setDefaultActive] = useState<string[]>([])
  const [loading, setLoading] = useState(false);
  const [apiUsername, setApiUsername] = useState<string>(() => {
    // 初始化时从localStorage加载缓存的用户名
    return localStorage.getItem('apiUsername') || '';
  });
  const [appIconPath, setAppIconPath] = useState('');
  const navigate = useHistory().push;
  const location = useLocation();
  const dispatch = useAppDispatch();
  const appId = useAppSelector((state) => state.appStore.appId);
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const isGuest = useAppSelector((state) => state.appStore.isGuest);
  const { t } = useTranslation();
  const guestName = useGuestName();
  const currentUser = localStorage.getItem('currentUser') || '';

  // 根据当前路由获取过滤后的菜单项
  const filteredItems = getFilteredMenus(routeList, location.pathname);

  // 获取用户名信息
  const fetchUsername = async () => {
    // 如果已经有缓存的用户名，直接使用
    if (apiUsername) {
      return;
    }

    try {
      const response: any = await getUsername();
      if (response && response.username) {
        setApiUsername(response.username);
        // 缓存到localStorage
        localStorage.setItem('apiUsername', response.username);
      }
    } catch (error) {
      console.error('获取用户名失败:', error);
      // 失败时使用localStorage中的缓存或默认值
      const cachedUsername = localStorage.getItem('apiUsername');
      if (cachedUsername) {
        setApiUsername(cachedUsername);
      }
    }
  };

  // 退出登录处理 - 直接复用现有逻辑

  // 用户信息下拉菜单项
  const userMenuItems = [
    {
      key: 'github',
      label: (
        <div className="user-menu-item">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="16"
            height="16"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            aria-hidden="true"
            className="user-menu-icon"
          >
            <title>GitHub</title>
            <path d="M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22"></path>
          </svg>
          <span className="user-menu-text">开源地址</span>
        </div>
      ),
    },
    {
      key: 'logout',
      label: (
        <div className="user-menu-item">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="16"
            height="16"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            aria-hidden="true"
            className="user-menu-icon"
          >
            <title>Logout</title>
            <path d="M14 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h9"></path>
            <path d="M16 17l5-5-5-5"></path>
            <path d="M21 12H9"></path>
          </svg>
          <span className="user-menu-text">{t('logout')}</span>
        </div>
      ),
    },
  ];

  /**
   * @description 从后往前遍历路由 父子路由，子路由前缀需要是父路由
   * @pathname 当前路径
   * */
  const getCurrentRoute = (pathname: string) => {
    // 如果是新对话页面，不设置选中状态
    if (pathname === '/home') {
      setDefaultActive([]);
      return;
    }

    // 特殊处理：如果是工具相关的页面（包括工具流详情页），选中工具菜单
    if (pathname.startsWith('/tools/')) {
      setDefaultActive(['/tools/:tab?']);
      return;
    }

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
      setDefaultActive([]);
    }
  }
  const menuClick = (e: any) => {
    // 如果点击工具菜单，导航到默认的ALL标签页
    if (e.key.startsWith('/tools')) {
      setDefaultActive([e.key]);
      navigate('/tools/all');
      return;
    }
    
    // 如果是新对话菜单项，需要清空聊天状态和选中状态
    if (e.key === '/home') {
      // 判断是否是应用聊天页面：/app/{tenantId}/chat/{appId}
      const isAppChatPage = location.pathname.includes('/app/') && location.pathname.includes('/chat/');
      // 判断是否是独立聊天页面：/chat/*（不含 /app/）
      const isChatPage = location.pathname.includes('/chat/') && !location.pathname.includes('/app/');
      
      if (isAppChatPage || isChatPage) {
        // 在聊天相关页面下创建新对话，不跳转
        console.log('在聊天页面创建新对话，保持在当前路由');

        // 只清空聊天相关的状态，不清空应用信息
        dispatch(setChatRunning(false));
        dispatch(setChatId(null));
        dispatch(setChatList([]));
        dispatch(setAtChatId(null));

        // 清空本地存储的聊天ID
        if (appId) {
          updateChatId(null, appId);
        }

        // 设置存储参数用于页面刷新
        const storageParams = {
          deleteAppId: null,
          refreshChat: true,
          resetInput: true,
          resetButtons: true,
          key: Date.now().toString(),
          type: 'deleteChat'
        };
        localStorage.setItem('storageMessage', JSON.stringify(storageParams));

        // 触发自定义事件来通知页面重置状态
        const resetEvent = new CustomEvent('resetChatState');
        window.dispatchEvent(resetEvent);

        // 清空菜单选中状态
        setDefaultActive([]);

        // 不跳转路由，保持在当前页面
        return;
      } else {
        // 在其他路由下（如 /home、/app 市场页），清空所有状态并跳转到 /home
        dispatch(setChatRunning(false));
        dispatch(setChatId(null));
        dispatch(setChatList([]));
        dispatch(setAtAppInfo(null));
        dispatch(setAtChatId(null));
        dispatch(setAtAppId(null));

        // 清空本地存储的聊天ID
        if (appId) {
          updateChatId(null, appId);
        }

        // 设置存储参数用于页面刷新
        const storageParams = {
          deleteAppId: null,
          refreshChat: true,
          resetInput: true,
          resetButtons: true,
          key: Date.now().toString(),
          type: 'deleteChat'
        };
        localStorage.setItem('storageMessage', JSON.stringify(storageParams));

        // 触发自定义事件来通知页面重置状态
        console.log('触发新对话重置事件');
        const resetEvent = new CustomEvent('resetChatState');
        window.dispatchEvent(resetEvent);

        // 清空菜单选中状态
        setDefaultActive([]);

        // 跳转到 /home
        navigate(e.key);
      }
    } else {
      // 其他菜单项保持正常的选中状态
      setDefaultActive([e.key]);
      navigate(e.key);
    }
  };

  const isWelcomePage = () => location.pathname.startsWith('/welcome');
  const colorBgContainer = isWelcomePage() ? '#0b0b0f' : '#ffffff';
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
    // 支持 /chat/* 路由显示边栏，但排除 /app/*/chat/* 路由
    if (location.pathname.includes('/chat/') && !location.pathname.includes('/app/')){
      return true; // 修改为 true，支持 /chat/* 路由显示边栏
    }
    // 去除工作流编排相关页面的左侧边栏显示
    if (location.pathname.includes('/add-flow/') ||
        location.pathname.includes('/flow-detail/') ||
        location.pathname.includes('/app-detail/')) {
      return false;
    }
    return true;
  }

  // 判断是否显示历史记录侧边栏
  const shouldShowHistorySidebar = () => {
    return location.pathname.includes('/chat/') ||
           location.pathname.includes('/home');
  }

  // 判断是否显示用户信息栏
  const shouldShowUserInfo = () => {
    return location.pathname.includes('/chat/') ||
           location.pathname.includes('/home') ||
           location.pathname.includes('/app') ||
           location.pathname.includes('/app-develop') ||
           location.pathname.includes('/tools');
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
    (parent?.window as any)?.navigatePath?.('appengine', pathname + search);
  }, [location]);

  useEffect(() => {
    if (process.env.PACKAGE_MODE === 'common') {
    // TODO: 待后端接口归一后调用 getUser()
    } else if (!location.pathname.includes('/chat/')){
      getOmsUser();
      getRole();
    }
    getChatPluginList();

    // 获取用户名信息（欢迎页面不需要）
    if (!location.pathname.includes('/welcome')) {
      fetchUsername();
    }
  }, [])

  // 获取应用图标
  useEffect(() => {
    if (appInfo?.attributes?.icon) {
      convertImgPath(appInfo.attributes.icon, isGuest).then((res: any) => {
        setAppIconPath(res || '');
      });
    } else {
      setAppIconPath('');
    }
  }, [appInfo?.attributes?.icon, isGuest]);

  // 当显示用户信息时，确保获取用户名
  useEffect(() => {
    if (shouldShowUserInfo() && !apiUsername) {
      fetchUsername();
    }
  }, [location.pathname, shouldShowUserInfo, apiUsername])
  return (
    <HistoryProvider>
      <Layout>
        { layoutValidate() && !isWelcomePage() && (
          <>
            <Sider
              collapsible
              collapsed={isCollapsed}
              onCollapse={() => setShowMenu(false)}
              trigger={null}
              width={showMenu ? (isCollapsed ? 60 : 280) : 0}
              className={`layout-sider ${isCollapsed ? 'collapsed' : ''}`}
            >
              <div className='layout-sider-header'>
                <div className='layout-sider-content'>
                  {location.pathname.includes('/app/') && location.pathname.includes('/chat/') && appInfo?.name ? (
                    // 应用聊天页面
                    isCollapsed ? (
                      // 折叠时只显示应用图标
                      <img
                        style={{width: '44px', height: '44px', objectFit: 'contain', cursor: 'pointer', borderRadius: '4px'}}
                        src={appIconPath || knowledgeBase}
                        alt="App Icon"
                        className='project-icon collapsed'
                        onClick={() => setIsCollapsed(false)}
                      />
                    ) : (
                      // 展开时显示应用图标 + 名称
                      <div className='app-name-header' style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '8px',
                        overflow: 'hidden',
                        cursor: 'default'
                      }}>
                        <img 
                          src={appIconPath || knowledgeBase} 
                          alt="App Icon"
                          style={{
                            width: '32px',
                            height: '32px',
                            
                            objectFit: 'contain',
                            borderRadius: '4px',
                            flexShrink: 0
                          }}
                        />
                        <span style={{
                          fontSize: '16px',
                          fontWeight: 500,
                          color: '#000',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap'
                        }}>
                          {appInfo.name}
                        </span>
                      </div>
                    )
                  ) : (
                    // 其他页面显示 AidoIcon
                    <img
                      style={{width: '44px', height: '44px', objectFit: 'contain', cursor: 'pointer'}}
                      src={AidoIcon}
                      alt="Aido Icon"
                      className={`project-icon ${isCollapsed ? 'collapsed' : ''}`}
                      onClick={() => {
                        if (isCollapsed) {
                          setIsCollapsed(false);
                        }
                      }}
                    />
                  )}
                </div>
                <img
                  src={CollapseIcon}
                  alt="Collapse"
                  className={`collapse-icon ${isCollapsed ? 'collapsed' : ''}`}
                  style={{ width: '24px', height: '24px' }}
                  onClick={() => {
                    if (isCollapsed) {
                      setIsCollapsed(false);
                    } else {
                      setIsCollapsed(true);
                    }
                  }}
                />
              </div>
              <div className='layout-sider-content-wrapper'>
                <Menu
                  className={`menu ${isCollapsed ? 'collapsed' : ''}`}
                  theme='light'
                  selectedKeys={defaultActive}
                  mode='inline'
                  items={filteredItems}
                  onClick={menuClick}
                />
                {shouldShowHistorySidebar() && (
                  <div className='layout-sider-history'>
                    {!isCollapsed && (
                      <HistorySidebarWithContext />
                    )}
                  </div>
                )}
              </div>
              {shouldShowUserInfo() && (
                <div className='layout-sider-user'>
                  <Dropdown
                    menu={{
                      items: userMenuItems,
                      onClick: ({ key }) => {
                        if (key === 'logout') {
                          handleLogout(setLoading);
                        } else if (key === 'github') {
                          window.open('https://github.com/ModelEngine-Group/app-platform', '_blank');
                        }
                      }
                    }}
                    placement="topRight"
                    trigger={['click']}
                    overlayClassName="user-info-dropdown"
                  >
                    <div className='layout-sider-user-info'>
                      <Avatar size={28} icon={<UserOutlined />} />
                       {!isCollapsed && (
                         <span className='layout-sider-user-name'>
                           {isGuest ? guestName : (apiUsername || currentUser || 'Unknown User')}
                         </span>
                       )}
                    </div>
                  </Dropdown>
                </div>
              )}
            </Sider>
            <div className='layout-sider-folder'>
              <KnowledgeIcons.menuFolder onClick={() => {
                if (isCollapsed) {
                  setIsCollapsed(false);
                } else {
                  setShowMenu(true);
                }
              }} />
            </div>
          </>
        )
      }
        <Layout className={setClassName()}>
          <Provider store={store}>
            <Content style={{ padding: isWelcomePage() ? '0' : ((layoutValidate() || isSpaMode()) ? '0 16px' : '0'), background: colorBgContainer }}>
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
                  <Redirect to='/welcome' />
                </Route>
              </Switch>
            </Content>
          </Provider>
        </Layout>
      </Layout>
    </HistoryProvider>
  );
};

export { HistoryContext };
export default AppLayout;
