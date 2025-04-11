/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef, useCallback } from 'react';
import { Spin } from 'antd';
import { useParams } from 'react-router-dom';
import AddFlow from '../addFlow';
import ConfigForm from '../configForm';
import CommonChat from '../chatPreview/chatComminPage';
import ChoreographyHead from '../components/header';
import { getAppInfo } from '@/shared/http/aipp';
import { updateFormInfo } from '@/shared/http/aipp';
import { debounce, getUiD, getCurrentTime, setSpaClassName } from '@/shared/utils/common';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setInspirationOpen } from '@/store/chatStore/chatStore';
import { setAppId, setAippId, setAppInfo, setChoseNodeId, setValidateInfo  } from '@/store/appInfo/appInfo';
import { setIsDebug } from "@/store/common/common";
import { getUser } from '../helper';
import { setTestStatus } from "@/store/flowTest/flowTest";

/**
 * 应用配置页面首页
 *
 * @return {JSX.Element}
 * @constructor
 */
const AippIndex = () => {
  const { appId, tenantId, aippId } = useParams();
  const [showElsa, setShowElsa] = useState(false);
  const [spinning, setSpinning] = useState(false);
  const [saveTime, setSaveTime] = useState('');
  const [reloadInspiration, setReloadInspiration] = useState('');
  const [showChat, setShowChat] = useState(false);
  const [messageChecked, setMessageCheck] = useState(false);
  const [showFlowChangeWarning, setShowFlowChangeWarning] = useState(false);
  const aippRef = useRef<any>(null);
  const inspirationRefresh = useRef<any>(false);
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const addFlowRef = useRef<any>(null);

  const elsaChange = () => {
    setShowElsa(!showElsa);
    showElsa && getAippDetails(true);
  }

  useEffect(() => {
    dispatch(setAppInfo({}));
    dispatch(setAppId(appId));
    aippId && dispatch(setAippId(aippId));
    dispatch(setInspirationOpen(false));
    // TODO: 待后端接口归一后调用 getUser()
    getAippDetails();
    if (window.location.href.indexOf('type=chatWorkflow') !== -1) {
      setShowElsa(true);
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
      }
    } finally {
      setSpinning(false);
    }
  }
  // 修改aipp更新回调
  const updateAippCallBack = (data) => {
    if (data) {
      aippRef.current = data;
      dispatch(setAppInfo(aippRef.current));
    }
  }
  // 保存配置
  const saveConfig = (data) => {
    updateFormInfo(tenantId, appId, {config: aippRef.current.config, ...data}).then((res) => {
      if (res.code === 0) {
        dispatch(setTestStatus(null));
        setSaveTime(getCurrentTime());
        getAippDetails(true);
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
          className={`container ${showElsa ? 'layout-elsa-content' : ''} ${showChat ? 'layout-show-preview' : ''}`}>
          <ChoreographyHead
            appInfo={appInfo}
            showElsa={showElsa}
            saveTime={saveTime}
            updateAippCallBack={updateAippCallBack}
            mashupClick={elsaChange}
            openDebug={openDebug}
            addFlowRef={addFlowRef}
          />
          <div className={setSpaClassName('layout-content')}>
            {showElsa ?
              (
                <AddFlow
                  type='edit'
                  addFlowRef={addFlowRef}
                  appInfo={appInfo}
                  setSpinning={setSpinning}
                  saveTime={saveTime}
                  setSaveTime={setSaveTime}
                  showFlowChangeWarning={showFlowChangeWarning}
                  setShowFlowChangeWarning={setShowFlowChangeWarning}
                />
              ) :
              (
                <ConfigForm
                  mashupClick={elsaChange}
                  configData={appInfo.configFormProperties || []}
                  graph={appInfo.flowGraph?.appearance}
                  handleConfigDataChange={handleConfigDataChange}
                  inspirationChange={inspirationChange}
                  showElsa={showElsa}
                />
              )}
            <CommonChat
              contextProvider={contextProvider}
              previewBack={changeChat}
            />
          </div>
        </div>
      </Spin>
    </>
  );
};

export default AippIndex;
