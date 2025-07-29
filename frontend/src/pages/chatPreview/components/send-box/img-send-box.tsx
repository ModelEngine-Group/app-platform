
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { httpUrlMap } from '@/shared/http/httpConfig';
import { convertImgPath } from '@/common/util';
import fileImg from '@/assets/images/ai/file2.png';

const { AIPP_URL } = httpUrlMap[process.env.NODE_ENV];
const ImgSendBox = (props) => {
  const { content, sendType } = props;
  const { tenantId } = useParams();
  const [imgPath, setImgPath] = useState('');
  let { file_name, file_path } = JSON.parse(content);
  useEffect(() => {
    if (sendType === 'image') {
      let iconPath = `${AIPP_URL}/${tenantId}/file?filePath=${file_path}&fileName=${file_name}`;
      convertImgPath(iconPath).then(res => {
        setImgPath(res);
      });
    }
  }, [sendType]);
  function setFileDom(type) {
    switch (type) {
      case 'image':
        return <img className='img-send-item' src={imgPath} />
        break;
      case 'audio':
        return <audio src={`${AIPP_URL}/${tenantId}/file?filePath=${file_path}&fileName=${file_name}`} controls></audio>
        break;
      case 'video':
        return <video src={`${AIPP_URL}/${tenantId}/file?filePath=${file_path}&fileName=${file_name}`} controls></video>
        break;
      default:
        return (<div className='file-div-item'>
          <img className='file-item' src={fileImg} />
          <span className='file-text' title={file_name}>{file_name}</span>
        </div>)
    }
  }
  return <>{(
    <div className='img-send-box'>
      { setFileDom(sendType)}
    </div>
  )}</>
}

export default ImgSendBox;
