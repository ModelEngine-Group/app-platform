/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useContext, useEffect, useState } from 'react';
import { Checkbox } from 'antd';
import { useLocation } from 'react-router-dom';
import { ChatContext } from '@/pages/aippIndex/context';
import { useAppSelector } from '@/store/hook';
import { convertImgPath } from '@/common/util';
import { scrollBottom } from '../../utils/chat-process';
import MessageDetail from './message-detail';
import SendBtn from '../send-box/send-btn';
import RemoteForm from './render';
import knowledgeBase from '@/assets/images/knowledge/knowledge-base.png';
import '../../styles/receive-box.scss';

/**
 * 接受聊天消息组件
 *
 * @return {JSX.Element}
 * @param formConfig 表单配置.
 * @constructor
 */
const ReceiveBox = (props) => {
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const { checkCallBack, showCheck } = useContext(ChatContext);
  const {
    content,
    pictureList,
    thinkTime,
    recieveType,
    formConfig,
    loading,
    checked,
    markdownSyntax,
    chartConfig,
    logId,
    instanceId,
    finished,
    status,
    feedbackStatus,
    appName,
    appIcon,
    filters,
    path,
    isAt,
    reference,
    msgType,
  } = props.chatItem;
  const [showIcon, setShowIcon] = useState(true);
  const location = useLocation();
  useEffect(() => {
    const { pathname } = location;
    if (pathname.includes('/chatShare/')) {
      setShowIcon(false);
    }
  }, [location]);
  useEffect(() => {
    if (chatRunning) {
      scrollBottom();
    } else if (finished) {
      setTimeout(() => {
        scrollBottom();
      }, 300);
    }
  }, [props.chatItem]);
  function onChange(e) {
    props.chatItem.checked = e.target.checked;
    checkCallBack();
  }

  // 设置显示类型
  function setReceiveDom(type) {
    if (type === 'form') {
      return <RemoteForm uniqueId={logId} path={path} formConfig={formConfig} />
    }
    return <MessageDetail
      content={content}
      pictureList={pictureList}
      thinkTime={thinkTime}
      markdownSyntax={markdownSyntax}
      finished={finished}
      chartConfig={chartConfig}
      filters={filters}
      status={status}
      instanceId={instanceId}
      feedbackStatus={feedbackStatus}
      refreshFeedbackStatus={props.refreshFeedbackStatus}
      reference={reference}
      msgType={msgType}
    />
  }

  return <>{(
    <div className='receive-box' data-logid={logId}>
      {showCheck && <Checkbox className='check-box' checked={checked} onChange={onChange}></Checkbox>}
      <div className='user-image'>
        {isAt ? <Img iconPath={appIcon} /> : <Img iconPath={appInfo.attributes?.icon} />}
        {isAt ? <span>{appName}</span> : <span>{appInfo.name}</span>}
      </div>
      <span className={recieveType !== 'form' ? 'receive-info-inner' : 'receive-info-inner receive-info-remote'}>
        {loading ? <Loading /> : setReceiveDom(recieveType)}
        {showIcon && <SendBtn content={content} sendType={recieveType} isRecieve={true} />}
      </span>
    </div>
  )}</>
}
// 接收消息loading
const Loading = () => {
  return (
    <>
      <div className='receive-loading'>
        <div className='bounce1'></div>
        <div className='bounce2'></div>
        <div className='bounce3'></div>
      </div>
    </>
  )
}
const Img = (props) => {
  const { iconPath } = props;
  const [imgPath, setImgPath] = useState('');
  useEffect(() => {
    if (iconPath) {
      convertImgPath(iconPath).then(res => {
        setImgPath(res);
      });
    }
  }, [iconPath]);
  return <>{(
    <span>
      {imgPath ? <img src={imgPath} /> : <img src={knowledgeBase} />}
    </span>
  )}</>
}


export default ReceiveBox;
