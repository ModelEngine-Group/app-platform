/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useContext } from 'react';
import { useLocation } from 'react-router-dom';
import { Checkbox } from 'antd';
import { ChatUserIcon } from '@/assets/icon';
import { ChatContext } from '@/pages/aippIndex/context';
import SendBtn from './send-btn';
import FileList from '../send-editor/components/file-list';
import '../../styles/send-box.scss';

const SendBox = (props) => {
  const { content, checked, logId, sendType, shareUser, fileList } = props.chatItem;
  const { checkCallBack, showCheck } = useContext(ChatContext);
  const [showIcon, setShowIcon] = useState(true);
  const currentUser = localStorage.getItem('currentUser') || '';
  const location = useLocation();

  // 选中回调
  function onChange(e) {
    props.chatItem.checked = e.target.checked;
    checkCallBack();
  }
  useEffect(() => {
    const { pathname } = location;
    if (pathname.includes('/chatShare/')) {
      setShowIcon(false);
    }
  }, [location]);
  
  return <>{(
    <div className='send-box' data-logid={logId}>
      {showCheck && <Checkbox className='check-box' checked={checked} onChange={onChange}></Checkbox>}
      <div className='user-image'>
        <ChatUserIcon />
        <span>{shareUser || currentUser}</span>
      </div>
      {
        fileList?.length > 0 && <span className='send-info'>
          <div className='send-info-inner file-send'>
            <FileList fileList={fileList} isChatUpload={false}></FileList>
          </div>
        </span>
      }
      {
        content && <div className='send-info'>
          <span className='send-info-inner'>
            {sendType === 'text' && (<div>{content}</div>)}
            {showIcon && <SendBtn content={content} sendType={sendType} />}
          </span>
        </div>
      }
    </div>
  )}</>
};

export default SendBox;
