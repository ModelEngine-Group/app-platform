/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef, useImperativeHandle } from 'react';
import { Tooltip } from 'antd';
import { useParams, useHistory } from 'react-router-dom'
import { getAppInfo } from '@/shared/http/aipp';
import { ConfigFlowIcon } from '@/assets/icon';
import { Message } from '@/shared/utils/message';
import { FlowContext } from '../aippIndex/context';
import { setTestTime, setTestStatus } from "@/store/flowTest/flowTest";
import { useAppDispatch, useAppSelector } from "@/store/hook";
import LeftMenu from './components/left-menu';
import Stage from './components/elsa-stage';
import FlowHeader from './components/addflow-header';
import FlowTest from './components/flow-test';
import { useTranslation } from 'react-i18next';
import './styles/index.scss';

/**
 * 应用编排页面
 *
 * @return {JSX.Element}
 * @param type 类型（区分应用还是工具流的编排）
 * @param appInfo 应用详情
 * @param addFlowRef 给父组件的引用
 * @param showFlowChangeWarning elsa数据变化后全局提示
 * @param setShowFlowChangeWarning 设置是否展示elsa变化后全局提示弹窗
 * @param saveTime 自动保存时间
 * @param setSaveTime 设置自动保存时间
 * @constructor
 */
const AddFlow = (props) => {
  const { t } = useTranslation();
  const dispatch = useAppDispatch();
  const {
    type,
    appInfo,
    addFlowRef,
    showFlowChangeWarning,
    setShowFlowChangeWarning,
    saveTime,
    setSaveTime,
    updateAippCallBack,
  } = props;
  const [dragData, setDragData] = useState([]);
  const [evaluateData, setEvaluateData] = useState([]);
  const [flowInfo, setFlowInfo] = useState({});
  const [showMenu, setShowMenu] = useState(false);
  const [debugTypes, setDebugTypes] = useState([]);
  const [showDebug, setShowDebug] = useState(false);
  const [showToolFlowChangeWarning, setShowToolFlowChangeWarning] = useState(false);
  const [workFlow,setWorkFlow] = useState('');
  const { tenantId, appId } = useParams();
  const [evaluateType, setEvaluateType] = useState('waterFlow');
  const readOnly = useAppSelector((state) => state.chatCommonStore.readOnly);
  const preview = useAppSelector((state) => state.commonStore.isReadOnly);
  const appRef = useRef<any>(null);
  const flowIdRef = useRef<any>(null);
  const elsaRunningCtl = useRef<any>(null);
  const flowContext = {
    type,
    appInfo: type ? appInfo : flowInfo,
    setFlowInfo
  };
  const navigate = useHistory().push;

  // 初始化编排数据
  useEffect(() => {
    !type && initElsa();
  }, [type]);

  // 只读模式无法访问编排页面
  useEffect(() => {
    if (readOnly) {
      navigate(`/app-develop`);
    }
  }, [readOnly]);

  // 新建工作流时获取详情
  async function initElsa() {
    if (window.location.href.indexOf('type=evaluate') !== -1) {
      setEvaluateType('evaluate');
    }
    const res:any = await getAppInfo(tenantId, appId);
    if (res.code === 0) {
      setFlowInfo(res.data);
    }
  }

  // 设置编排类型
  useEffect(()=>{
    if (window.location.href.indexOf('type=workFlow') !== -1) {
      setWorkFlow('workFlow');
    }
  },[]);

  // 页面初始化重置状态
  useEffect(() => {
    if (!type) return;
    dispatch(setTestTime(null));
    dispatch(setTestStatus(null));
    elsaRunningCtl.current?.reset();
  }, [props.type]);

  // 显示隐藏左侧菜单
  function menuClick() {
    setShowMenu(!showMenu)
  }

  // 测试
  const handleDebugClick = () => {
    window.agent.validate().then(() => {
      appRef.current.elsaChange();
      setDebugTypes(window.agent.getFlowRunInputMetaData());
      setShowDebug(true);
    }).catch(err => {
      if (typeof (err) === 'string') {
        Message({ type: 'warning', content: err});
      } else if (Array.isArray(err)) {
        err.forEach((item) => {
          item.errorFields.forEach((errorField) => {
            errorField.errors.forEach((error) => {
              Message({ type: 'warning', content: error || '编排节点必填校验未通过！'});
            });
          });
        });
      };
    });
  };

  // 给父组件的测试回调
  useImperativeHandle(addFlowRef, () => {
    return {
      'handleDebugClick': handleDebugClick,
    }
  });

  return <>{(
    <div className='add-flow-container'>
      <FlowContext.Provider value={flowContext}>
        {/* 工具流header */}
        {(!type || workFlow) &&
          <FlowHeader
            debugTypes={debugTypes}
            handleDebugClick={handleDebugClick}
            showDebug={showDebug}
            saveTime={saveTime}
            setShowDebug={setShowDebug}
            workFlow={workFlow}
            types={evaluateType}
            updateAippCallBack={updateAippCallBack}
          />}
        {/* 调试抽屉弹窗 */}
        <FlowTest
          setShowDebug={setShowDebug}
          showDebug={showDebug}
          debugTypes={debugTypes}
          appRef={appRef}
          elsaRunningCtl={elsaRunningCtl}
          setShowFlowChangeWarning={type ? setShowFlowChangeWarning : setShowToolFlowChangeWarning}
        />
        {/* 左侧菜单 */}
        <div className={['content', !type ? 'content-add' : null].join(' ')}>
          {
            !preview && (
              showMenu ? (
                <LeftMenu
                  menuClick={menuClick}
                  dragData={dragData}
                  setDragData={setDragData}
                  evaluateData={evaluateData}
                  setEvaluateData={setEvaluateData}
                  type={evaluateType}
                />
              ) : (
                  <Tooltip placement='rightTop' title={t('expandArrange')}>
                    <div className='menu-icon' onClick={menuClick}>
                      <ConfigFlowIcon />
                    </div>
                  </Tooltip>
              )
            )
          }
          {/* elsa编排组件 */}
          <Stage
            setDragData={setDragData}
            setEvaluateData={setEvaluateData}
            appRef={appRef}
            setSaveTime={setSaveTime}
            flowIdRef={flowIdRef}
            elsaRunningCtl={elsaRunningCtl}
            showFlowChangeWarning={type ? showFlowChangeWarning : showToolFlowChangeWarning}
            setShowFlowChangeWarning={type ? setShowFlowChangeWarning : setShowToolFlowChangeWarning}
            types={evaluateType}
          />
        </div>
      </FlowContext.Provider>
    </div>
  )}</>
};
export default AddFlow;
