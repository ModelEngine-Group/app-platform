/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { UploadFile } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import Upload from 'antd/es/upload/Upload';
import { useTranslation } from 'react-i18next';
import './style.scoped.scss';

const LiveUpload: React.FC = ({ customRequest }: any) => {
  const { t } = useTranslation();
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const customRequestInner = async ({ file }: any) => {
    await customRequest(file);
    setFileList([file]);
  }
  return (
    <Upload
      style={{ width: '100%' }}
      customRequest={customRequestInner}
      fileList={fileList}
      multiple={false}
      maxCount={1}
      disabled={fileList.length ? true : false}
    >
      <div className='live-upload-trigger-container' style={{ backgroundColor: fileList.length ? '#ccc' : '' }}>
        <span style={{
          display: 'block',
          height: '40px',
          paddingLeft: '8px',
          color: 'grey',
          lineHeight: '40px'
        }}>
          {t('pickFile')}
        </span>
        <span style={{
          position: 'absolute',
          top: 0,
          right: 0,
          padding: '7px 8px'
        }}><UploadOutlined /></span>
      </div>
    </Upload>
  )
};

export default LiveUpload;
