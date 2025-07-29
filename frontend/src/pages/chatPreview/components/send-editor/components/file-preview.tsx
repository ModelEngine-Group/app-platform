/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { ClearFileIcon } from '@/assets/icon';
import UploadFile from './upload-file';
import { useTranslation } from 'react-i18next';
import '../styles/file-preview.scss';

const LinkFile = ({ openUploadRef }) => {
  const { t } = useTranslation();
  const [showPreview, setShowPreview] = useState(false);
  const [file, setFile] = useState({ data: null, type: null });
  // 取消文件
  const cancleFile = () => {
    setShowPreview(false);
  }
  return (
    <>
      {/* 预览文件内容 */}
      {showPreview &&
        <div className='file-preview'>
          <div className='preview-inner'>
            <div>{t('fileContent')}</div>
            <span className='delete-icon'>
              <ClearFileIcon onClick={() => cancleFile()} />
            </span>
          </div>
        </div>
      }
      {/* 上传文件弹窗 */}
      <UploadFile
        openUploadRef={openUploadRef}
        fileSend={(data, type) => { setFile({ data, type }) }}
      />
    </>
  )
};


export default LinkFile;
