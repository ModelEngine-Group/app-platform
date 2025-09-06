/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect, useRef } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import { Button, Dropdown, Badge, Typography } from 'antd';
import { LeftArrowIcon, UploadIcon } from '@/assets/icon';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setTestStatus, setTestTime } from "@/store/flowTest/flowTest";
import { setChatId, setChatList } from '@/store/chatStore/chatStore';
import { APP_TYPE } from '@/pages/components/common/common';
import { getCookie } from '@/shared/utils/common';
import { useTranslation } from 'react-i18next';
import { Message } from "@/shared/utils/message";
import { setAppInfo, setValidateInfo } from '@/store/appInfo/appInfo';
import { getCheckList, exportApp, updateAppInfo, updateFlowInfo } from '@/shared/http/aipp';
import { convertImgPath } from '@/common/util';
import { createGraphOperator } from '@fit-elsa/elsa-react';
import { get, cloneDeep } from 'lodash';
import TimeLineDrawer from '@/components/timeLine';
import PublishModal from './publish-modal';
import EditModal from './edit-modal';
import TestStatus from './test-status';
import TestModal from './test-modal';
import DebugModal from './debug-modal';
import knowledgeImg from '@/assets/images/knowledge/knowledge-base.png';
import complateImg from '@/assets/images/ai/complate.png';
import publishImg from '@/assets/images/ai/publish.png';
import robotImg from '@/assets/images/ai/robot.png';
import moreBtnImg from '@/assets/images/more_btn.svg';
import lineImg from '@/assets/images/line.svg';
import debugBtnImg from '@/assets/images/debug_btn.svg';
import EditImg from '@/assets/images/ai/edit.png';
import './styles/header.scss'

/**
 * 应用配置页面头部组件
 *
 * @return {JSX.Element}
 * @param showElsa 是否为编排页面
 * @param appInfo 应用详情
 * @param updateAippCallBack elsa数据更新后回调
 * @param mashupClick 编排页面返回按钮点击回调
 * @param openDebug 打开调试提示弹窗
 * @param saveTime 自动保存时间
 * @constructor
 */
const ChoreographyHead = (props) => {
  const cLocale = getCookie('locale');
  const { t } = useTranslation();
  const {
    showElsa,
    appInfo,
    updateAippCallBack,
    mashupClick,
    openDebug,
    saveTime
  } = props;
  const { aippId } = useParams();
  const testStatus = useAppSelector((state) => state.flowTestStore.testStatus);
  const appValidateInfo = useAppSelector((state) => state.appStore.validateInfo);
  const readOnly = useAppSelector((state) => state.chatCommonStore.readOnly);
  const preview = useAppSelector((state) => state.commonStore.isReadOnly);
  const [debugVisible, setDebugVisible] = useState(true);
  const [open, setOpen] = useState(false);
  const [isFormPrompt, setIsFormPrompt] = useState(true);
  const [imgPath, setImgPath] = useState('');
  const isChecked = useRef(false);
  const isWorkFlow = useRef(false);
  let modalRef = useRef<any>();
  let editRef = useRef<any>();
  let testRef = useRef<any>();
  const { tenantId, appId } = useParams();
  const navigate = useHistory().push;
  const dispatch = useAppDispatch();

  // 按钮点击回调
  const handleMenuClick = (e) => {
    switch (e.key) {
      case 'history':
        versionDetail();
        break;
      case 'export':
        handleExportApp();
        break;
      default:
        break;
    }
  };

  // 动态设置下拉按钮
  const getMoreItems = () => {
    const items = [
      {
        label: t('export'),
        key: 'export'
      },
    ];
    if (getTalk()) {
      items.unshift({
        label: t('history'),
        key: 'history'
      });
    }
    return items;
  };

  // 校验清单
  const checkValidity = async (graph) => {
    if (!graph) { return};
    const graphOperator = createGraphOperator(JSON.stringify(graph));
    const formValidate = graphOperator.getFormsToValidateInfo();
    const res:any = await getCheckList(tenantId, formValidate);
    if (res?.code === 0 && res?.data) {
      isChecked.current = true;
      let validateList = res.data;
      if (!isWorkFlow.current) {
        validateList = validateList.reduce((acc, cur) => {
          acc = acc.concat(cur.configChecks.map(item => {
            return { ...item, type: cur.type };
          }))
          return acc;
        }, []);
      }
      dispatch(setValidateInfo(validateList));
      if (isWorkFlow.current && isFormPrompt && validateList.find(item => ['knowledgeRetrievalNodeState', 'manualCheckNodeState', 'endNodeEnd'].includes(item.type))) {
        Message({ type: 'warning', content: t('formPrompt') });
        setIsFormPrompt(false);
      }
    }
  };

  // 编辑名称
  const handleEditClick = () => {
    !preview && editRef.current.showModal();
  };

  // 未解决可用性问题点击发布按钮的提示
  const publishTip = () => {
    return <div className='publish-tip'>
      <div className='tip-top'>
        <div className='resolve-tip'>{t('publishCheckTip')}</div>
        <Typography.Link onClick={() => setDebugVisible(true)}>{t('openDebugManifest')}</Typography.Link>
      </div>
      <div className='question-num'>{cLocale === 'en-us' ? t('currentProcess') + appValidateInfo.length : t('currentProcess') + appValidateInfo.length + t('num') + t('question')}</div>
    </div>
  };

  // 点击应用发布按钮
  function handleUploadApp() {
    if (appValidateInfo.length) {
      Message({ type: 'error', content: publishTip() });
      return;
    }
    if (testStatus !== 'Finished' && isWorkFlow.current) {
      testRef.current.showModal();
      return;
    }
    modalRef.current.showModal();
  };

  // 实时保存数据
  const updateGraph = async () => {
    if (preview) {
      return;
    }
    const currentApp = cloneDeep(appInfo);
    currentApp.flowGraph.appearance = window.agent.serialize();
    await updateFlowInfo(tenantId, appId, currentApp.flowGraph);
  };

  // 返回编排页面
  const backClick = async () => {
    if (showElsa && window.agent && !testStatus) {
      await updateGraph();
    }
    if (appInfo.appCategory === 'workflow') {
      return navigate(`/app-develop/${tenantId}/appDetail/${appId}`);
    }
    if (testStatus) {
      dispatch(setTestStatus(null));
      dispatch(setTestTime(0));
    }
    if (showElsa) {
      mashupClick();
    } else {
      navigate(`/app-develop/${tenantId}/appDetail/${appId}`);
    }
  };

  // 打开调试抽屉
  const handleOpenDebug = () => {
    openDebug();
  };

  // 点击去聊天回调
  const chatClick = () => {
    dispatch(setChatId(null));
    dispatch(setChatList([]));
    dispatch(setAppInfo({}));
    navigate(`/app/${tenantId}/chat/${appId}/${aippId}`);
  };
  const versionDetail = () => {
    setOpen(true);
  };

  // 点击去聊天回调
  const getTalk = () => {
    if (appInfo.attributes?.latest_version) {
      return true;
    } else if (appInfo.state === 'active') {
      return true;
    }
  };

  // 应用导出
  const handleExportApp = async () => {
    const res = await exportApp(tenantId, appId);
    const blob = new Blob([JSON.stringify(res)], { type: 'application/json' })
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${appInfo.name}${t('app')}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  };

  // 更新应用状态
  const updateAppState = async () => {
    try {
      const res:any = await updateAppInfo(tenantId, appId, {...appInfo, state: 'inactive'});
      if (res.code === 0 && res.data) {
        updateAippCallBack(res.data);
      }
    } finally {
      isChecked.current = false;
    }
  };

  useEffect(() => {
    isWorkFlow.current = get(appInfo, 'configFormProperties[0].name') === APP_TYPE['WORK_FLOW'].name;
    if (appInfo?.state === 'importing' && !isChecked.current) {
      checkValidity(appInfo?.flowGraph?.appearance);
    }
    if (appInfo.attributes?.icon) {
      convertImgPath(appInfo.attributes.icon).then(res => {
        setImgPath(res);
      });
    }
    return () => {
      setImgPath('');
    }
  }, [appInfo]);

  useEffect(() => {
    setDebugVisible(appValidateInfo?.length > 0);
    if (!appValidateInfo.length && appInfo.state === 'importing' && isChecked.current) {
      updateAppState();
    }
  }, [appValidateInfo]);

  // 只读用户无法访问该页面
  useEffect(() => {
    if (readOnly) {
      navigate(`/app-develop`);
    }
  }, [readOnly]);
  return <>{(
    <div className='app-header'>
      <div className='logo'>
        <LeftArrowIcon className='back-icon' onClick={backClick} />
        {imgPath ?
          <img src={imgPath} onClick={backClick} /> :
          <img src={knowledgeImg} onClick={backClick} />
        }
        <span className='header-text' title={appInfo?.name}>{appInfo?.name}</span>
        <img
          className={['edit-icon', preview ? 'not-allowed' : ''].join(' ')}
          src={EditImg}
          onClick={handleEditClick}
        />
        {
          (appInfo.attributes?.latest_version || appInfo.state === 'active') ?
            (
              <div className='status-tag'>
                <img src={complateImg} />
                <span>{t('active')}</span>
              </div>
            ) :
            (
              <div className='status-tag'>
                <img src={publishImg} />
                <span>{t('inactive')}</span>
              </div>
            )
        }
        {saveTime && <span>{t('autoSave')}：{saveTime}</span>}
        {showElsa && <TestStatus />}
      </div>
      <div className='header-grid'>
        <div className='header-grid-btn'>
          <div className='more'>
            <Dropdown menu={{ items: getMoreItems(), onClick: handleMenuClick }} disabled={preview}>
              <img src={moreBtnImg} alt="" />
            </Dropdown>
            {
              appValidateInfo.length > 0 &&
              <>
                <img src={lineImg} alt="" style={{ margin: '0px 6px' }} />
                <div onClick={() => setDebugVisible(true)}>
                  <Badge size="small" count={appValidateInfo.length}>
                    <img src={debugBtnImg} alt="" style={{ margin: '3px 0px' }} />
                  </Badge>
                </div>
              </>
            }
          </div>
          {
            getTalk() &&
            <span className='history robot' onClick={chatClick}>
              <img src={robotImg} />
              <span>{t('toTalk')}</span>
            </span>
          }
        </div>
        {showElsa && <Button
          className='header-btn test-btn'
          disabled={testStatus === 'Running' || preview}
          onClick={handleOpenDebug}>
          {t('debug')}
        </Button>}
        <Button
          type='primary'
          className='header-btn publish-btn'
          disabled={testStatus === 'Running' || preview}
          onClick={handleUploadApp}>
          <UploadIcon />{t('publish')}
        </Button>
      </div>
      {/* 发布弹窗 */}
      <PublishModal modalRef={modalRef} appInfo={appInfo} publishType='app' />
      {/* 编辑应用基本信息弹窗 */}
      <EditModal modalRef={editRef} appInfo={appInfo} updateAippCallBack={updateAippCallBack} />
      {/* 发布历史记录抽屉 */}
      <TimeLineDrawer
        open={open}
        setOpen={setOpen}
        appInfo={appInfo}
        updateAippCallBack={updateAippCallBack}
      />
      {/* 发布未调试提示弹窗 */}
      <TestModal testRef={testRef} handleDebugClick={openDebug} type='edit' />
      {/* 应用导入错误清单提示抽屉 */}
      {
        debugVisible &&
        <DebugModal
          isWorkFlow={isWorkFlow}
          showElsa={showElsa}
          mashupClick={mashupClick}
          closeDebug={() => setDebugVisible(false)}
        ></DebugModal>
      }
    </div>
  )} </>;
};

export default ChoreographyHead;
