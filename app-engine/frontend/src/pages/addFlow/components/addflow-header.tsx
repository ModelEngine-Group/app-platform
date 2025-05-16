/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { Button } from 'antd';
import { LeftArrowIcon, UploadIcon } from '@/assets/icon';
import { updateAppInfo } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { convertImgPath } from '@/common/util';
import { FlowContext } from '@/pages/aippIndex/context';
import EditTitleModal from '@/pages/components/edit-title-modal';
import PublishModal from '@/pages/components/publish-modal';
import TestModal from '@/pages/components/test-modal';
import TestStatus from '@/pages/components/test-status';
import TimeLineDrawer from '@/components/timeLine';
import { useTranslation } from 'react-i18next';
import { setTestStatus, setTestTime } from "@/store/flowTest/flowTest";
import { useAppSelector, useAppDispatch } from "@/store/hook";
import knowledgeImg from '@/assets/images/knowledge/knowledge-base.png';
import editImg from '@/assets/images/ai/edit.png';
import complateImg from '@/assets/images/ai/complate.png';
import publishImg from '@/assets/images/ai/publish.png';
import timeImg from '@/assets/images/ai/time.png';

/**
 * 工具流编排头部信息展示组件
 *
 * @return {JSX.Element}
 * @param handleDebugClick 点击调试回调
 * @param saveTime 自动保存时间
 * @constructor
 */
const AddHeader = (props) => {
  const dispatch = useAppDispatch();
  const { t } = useTranslation();
  const { handleDebugClick, workFlow, types, saveTime, updateAippCallBack } = props;
  const { appInfo, setFlowInfo } = useContext(FlowContext);
  const [open, setOpen] = useState(false);
  const [imgPath, setImgPath] = useState('');
  const { tenantId, appId } = useParams();
  let editRef: any = useRef(null);
  let modalRef: any = useRef(null);
  let testRef: any = useRef(null);
  const testStatus = useAppSelector((state) => state.flowTestStore.testStatus);

  // 发布工具流
  const handleUploadFlow = () => {
    if (testStatus !== 'Finished') {
      testRef.current.showModal();
      return;
    }
    modalRef.current.showModal();
  }
  // 编辑工具流
  const handleEditClick = () => {
    editRef.current.showModal();
  }
  // 返回上一页
  const handleBackClick = () => {
    if (testStatus) {
      dispatch(setTestStatus(null));
      dispatch(setTestTime(0));
    }
    window.history.back();
  }
  // 保存回调
  function onFlowNameChange(params) {
    appInfo.name = params.name;
    appInfo.attributes.description = params.description;
    updateAppWorkFlow('waterFlow');
  }
  // 创建更新应用
  async function updateAppWorkFlow(optionType = '') {
    const res:any = await updateAppInfo(tenantId, appId, appInfo);
    if (res.code === 0) {
      Message({ type: 'success', content: t('editSuccess') });
      optionType && editRef.current.handleCancel();
      setFlowInfo(JSON.parse(JSON.stringify(appInfo)));
    } else {
      optionType && editRef.current.handleLoading();
    }
  }
  const versionDetail = () => {
    setOpen(true);
  }
  useEffect(() => {
    if (appInfo.attributes?.icon) {
      convertImgPath(appInfo.attributes.icon).then(res => {
        setImgPath(res);
      });
    }
  }, [appInfo]);
  return <>{(
    <div>
      <div className='app-header'>
        <div className='logo'>
          { <LeftArrowIcon className='back-icon' onClick={handleBackClick} /> }
          {imgPath ? <img src={imgPath} /> : <img src={knowledgeImg} />}
          <span className='header-text' title={appInfo?.name}>{appInfo?.name}</span>
          <img className='edit-icon' src={editImg} onClick={handleEditClick} />
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
          <TestStatus />
        </div>
        <div className='header-grid'>
          {
            (appInfo.attributes?.latest_version || appInfo.state === 'active') &&
            <span className='history' onClick={versionDetail}>
              <img src={timeImg} />
            </span>
          }
          <Button
            className='header-btn test-btn'
            onClick={handleDebugClick}
            disabled={testStatus === 'Running'}
          >
            {t('debug')}
          </Button>
          <Button
            type='primary'
            className='header-btn'
            onClick={handleUploadFlow}
            disabled={testStatus === 'Running'}
          >
            <UploadIcon />{t('publish')}
          </Button>
        </div>
      </div>
      {/* 工具流发布弹窗 */}
      <PublishModal
        modalRef={modalRef}
        appInfo={appInfo}
        publishType={types}
      />
      {/* 工具流发布未调试提示弹窗 */}
      <TestModal
        testRef={testRef}
        handleDebugClick={handleDebugClick}
      />
      {/* 工具流修改基础信息弹窗 */}
      <EditTitleModal
        modalRef={editRef}
        onFlowNameChange={onFlowNameChange}
        appInfo={appInfo}
      />
      {/* 工具流发布历史信息弹窗 */}
      <TimeLineDrawer
        open={open}
        setOpen={setOpen}
        updateAippCallBack ={updateAippCallBack }
        workflow={workFlow} type='waterflow'
      />
    </div>
  )}</>
};


export default AddHeader;
