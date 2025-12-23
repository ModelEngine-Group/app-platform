/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useCallback, useEffect, useRef, useState, lazy, Suspense } from 'react';
import { Spin } from 'antd';
import { useParams } from 'react-router-dom';
import AddFlow from '../addFlow';
import ConfigForm from '../configForm';
const CommonChat = lazy(() => import('../chatPreview/chatComminPage'));
import ChoreographyHead from '../components/header';
import { getAppInfo, updateFormInfo } from '@/shared/http/aipp';
import { debounce, getCurrentTime, getUiD, setSpaClassName, getAppConfig } from '@/shared/utils/common';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setInspirationOpen } from '@/store/chatStore/chatStore';
import { setAippId, setAppId, setAppInfo, setChoseNodeId, setValidateInfo } from '@/store/appInfo/appInfo';
import { setIsDebug } from '@/store/common/common';
import { setTestStatus } from '@/store/flowTest/flowTest';
import { RenderContext } from '@/pages/aippIndex/context';

/**
 * 应用配置页面首页
 *
 * @return {JSX.Element}
 * @constructor
 */
const AippIndex = () => {
  const { appId, tenantId, aippId } = useParams();
  const [showElsa, setShowElsa] = useState(false);
  const [showConfig, setShowConfig] = useState(true);
  const [spinning, setSpinning] = useState(false);
  const [saveTime, setSaveTime] = useState('');
  const [reloadInspiration, setReloadInspiration] = useState('');
  const [workFlow, setWorkFlow] = useState('');
  const [showChat, setShowChat] = useState(false);
  const [messageChecked, setMessageCheck] = useState(false);
  const [showFlowChangeWarning, setShowFlowChangeWarning] = useState(false);
  const aippRef = useRef<any>(null);
  const inspirationRefresh = useRef<any>(false);
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const pluginList = useAppSelector((state) => state.chatCommonStore.pluginList);
  const addFlowRef = useRef<any>(null);
  const renderRef = useRef(false);
  const elsaReadOnlyRef = useRef(false);

  const [pluginName, setPluginName] = useState('default');
  const [plugin, setPlugin] = useState();


  const elsaChange = () => {
    setShowElsa(!showElsa);
    showElsa && getAippDetails(true);
  }

  const handleChangeShowConfig = () => {
    setShowConfig(!showConfig);
  };

  useEffect(() => {
    const found = pluginList.find((item: any) => item.name === pluginName);
    setPlugin(found);
  }, [pluginList, pluginName]);

  useEffect(() => {
    if (plugin) {
      setShowConfig(false);
    }
  }, [plugin]);

  useEffect(() => {
    dispatch(setAppInfo({}));
    dispatch(setAppId(appId));
    aippId && dispatch(setAippId(aippId));
    dispatch(setInspirationOpen(false));
    // TODO: 待后端接口归一后调用 getUser()
    getAippDetails();
    // TODO: 后续归一插件和应用创建工具流入口的时候需注意type
    if (window.location.href.indexOf('type=workFlow') !== -1) {
      setShowElsa(true);
      setWorkFlow('workFlow');
    };
    return () => {
      dispatch(setChoseNodeId(''));
      dispatch(setValidateInfo([]));
      dispatch(setAppInfo({}));
      dispatch(setAppId(''));
      dispatch(setAippId(''));
    };
  }, []);

  useEffect(() => {
    dispatch(setIsDebug(appInfo.state !== 'active'));
  }, [appInfo.state])

  // 获取aipp详情
  const getAippDetails = async (update = false) => {
    !update && setSpinning(true);
    try {
      const res:any = await getAppInfo(tenantId, appId);
      if (res.code === 0) {
        res.data.hideHistory = true;
        aippRef.current = res.data;
        dispatch(setAppInfo(res.data));
        RefreshChatStyle(res.data);
      }
    } finally {
      setSpinning(false);
    }
  }

  // 基于appInfo更新对话界面
  const RefreshChatStyle = (appInfo) => {
    const appChatStyle = getAppConfig(appInfo) ? getAppConfig(appInfo).appChatStyle : null;
    setPluginName(appChatStyle || 'default');
  };


  // 修改aipp更新回调
  const updateAippCallBack = (partialData) => {
    if (partialData) {
      aippRef.current = {
        ...aippRef.current,
        ...partialData
      };
      dispatch(setAppInfo(aippRef.current));
    }
  }

  // 遍历 configFormProperties[0].children 的所有子孙节点，按 name 建立映射
  const buildNameToNodeMap = (nodes: any[]): Record<string, any> => {
    const map: Record<string, any> = {};

    const traverse = (node: any) => {
      if (node.name) {
        map[node.name] = node;
      }
      if (Array.isArray(node.children)) {
        node.children.forEach(traverse);
      }
    }
    nodes.forEach(traverse);
    return map;
  }

  // 保存配置
  const saveConfig = (data) => {
    updateFormInfo(tenantId, appId, {config: aippRef.current.config, ...data}).then((res) => {
      if (res.code === 0) {
        dispatch(setTestStatus(null));
        setSaveTime(getCurrentTime());

        aippRef.current.flowGraph.appearance = JSON.parse(data.graph);
        if (aippRef.current?.configFormProperties?.[0]?.children && Array.isArray(data.input)) {
          const nodeMap = buildNameToNodeMap(aippRef.current.configFormProperties[0].children);

          for (const item of data.input) {
            const targetNode = nodeMap[item.name];
            if (targetNode) {
              // 赋值：用 item.defaultValue 作为当前值（根据你给的数据结构）
              targetNode.defaultValue = item.defaultValue;
            }
          }
        }
        aippRef.current = JSON.parse(JSON.stringify(aippRef.current));
        dispatch(setAppInfo(aippRef.current));
        RefreshChatStyle(aippRef.current);
        if (inspirationRefresh.current) {
          inspirationRefresh.current = false;
          let key = getUiD();
          setReloadInspiration(key);
        }
      }
    })
  }
  // 灵感大全更新后自动刷新
  const inspirationChange = () => {
    inspirationRefresh.current = true;
  }
  // 编辑工具流设置右侧聊天展开
  const changeChat = () => {
    setShowChat(!showChat)
  }
  // 实时自动保存
  const handleSearch = useCallback(debounce((data) => saveConfig(data), 1000), []);
  const handleConfigDataChange = (data) => {
    handleSearch(data);
  };
  // 打开调试抽屉方法
  const openDebug = () => {
    if (!showElsa) {
      setShowElsa(true);
    }
    addFlowRef.current?.handleDebugClick();
  }
  const contextProvider = {
    messageChecked,
    setMessageCheck,
    showElsa,
    updateAippCallBack,
    reloadInspiration,
    tenantId,
  };

  return (
    <>
      <Spin spinning={spinning}>
        <div
          className={`container ${showElsa ? 'layout-elsa-content' : ''} ${showChat ? 'layout-show-preview' : ''}`}
        >
          <RenderContext.Provider value={{renderRef: renderRef, elsaReadOnlyRef: elsaReadOnlyRef}}>
            {!workFlow ? (
              <ChoreographyHead
                appInfo={appInfo}
                showElsa={showElsa}
                saveTime={saveTime}
                updateAippCallBack={updateAippCallBack}
                mashupClick={elsaChange}
                openDebug={openDebug}
                addFlowRef={addFlowRef}
              />
            ) : null}
            <div className={setSpaClassName('layout-content')}>
              {showElsa ? (
                <AddFlow
                  type='edit'
                  addFlowRef={addFlowRef}
                  appInfo={appInfo}
                  setSpinning={setSpinning}
                  saveTime={saveTime}
                  setSaveTime={setSaveTime}
                  showFlowChangeWarning={showFlowChangeWarning}
                  setShowFlowChangeWarning={setShowFlowChangeWarning}
                  updateAippCallBack={updateAippCallBack}
                />
              ) : (
                <ConfigForm
                  mashupClick={elsaChange}
                  configData={appInfo.configFormProperties || []}
                  graph={appInfo.flowGraph?.appearance}
                  handleConfigDataChange={handleConfigDataChange}
                  inspirationChange={inspirationChange}
                  showElsa={showElsa}
                  showConfig={showConfig}
                  onChangeShowConfig={handleChangeShowConfig}
                />
              )}
              <Suspense fallback={<div style={{display:"none"}}>loading</div>}>
                <CommonChat
                  showElsa={showElsa}
                  contextProvider={contextProvider}
                  previewBack={changeChat}
                  pluginName={pluginName}
                />
              </Suspense>
            </div>
          </RenderContext.Provider>
        </div>
      </Spin>
    </>
  );
};

export default AippIndex;
