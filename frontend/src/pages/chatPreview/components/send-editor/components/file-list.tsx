/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { bytesToSize } from '@/common/util';
import { useTranslation } from 'react-i18next';
import { Image } from 'antd';
import { useAppSelector } from '@/store/hook';
import serviceConfig from '@/shared/http/httpConfig';
import { FileType, PictureFileType } from '../common/config';
import '../styles/file-list.scss';
import docxIcon from '@/assets/images/ai/docx.svg';
import htmlIcon from '@/assets/images/ai/html.svg';
import markdownIcon from '@/assets/images/ai/markdown.svg';
import mp3Icon from '@/assets/images/ai/mp3.svg';
import mp4Icon from '@/assets/images/ai/mp4.svg';
import pdfIcon from '@/assets/images/ai/pdf.svg';
import txtIcon from '@/assets/images/ai/txt.svg';
import excelIcon from '@/assets/images/ai/excel.svg';
import csvIcon from '@/assets/images/ai/csv.svg';
import deleteFileIcon from '@/assets/images/ai/delete_file.svg';
import previewIcon from '@/assets/images/ai/preview_icon.svg';
const pictureTypes = Object.values(PictureFileType);
const { AIPP_URL } = serviceConfig;
/**
 * 对话框上传多文件展示列表组件
 *
 * @param isChatUpload 是否是在对话输入框中上传.
 * @param isDebug 是否是调试页面.
 * @param fileList 多文件列表.
 * @param updateFileList 更新文件列表的方法.
 * @return {JSX.Element}
 * @constructor
 */
const FileList = (props) => {
  const { isChatUpload = true, isDebug = false, fileList, updateFileList } = props;
  const { t } = useTranslation();
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const [showOperateIndex, setShowOperateIndex] = useState(-1);

  // 获取文件图标
  const getFileIcon = (type, url, name) => {
    if (pictureTypes.includes(type.toLocaleLowerCase())) {
      return <Image src={`${AIPP_URL}/${tenantId}/file?filePath=${url}&fileName=${name}`} height={'100%'} width={'100%'} preview={{ mask: getImgMask(), maskClassName: 'img-mask' }} />
    }
    return <img src={getOtherFileIcon(type.toLocaleLowerCase())} alt="" />
  };

  const getImgMask = () => {
    return <img src={previewIcon} alt="" />
  };

  // 获取名字显示（名字可能有省略号，但是类型需要展示出来）
  const getFileName = (name) => {
    const fileArr = name.split('.');
    const suffix = fileArr.pop();
    const fileName = fileArr.join('.');
    return <div className='file-name-container'>
      <div className='file-name' title={name}>{fileName}</div>
      <div>.{suffix}</div>
    </div>
  };

  // 获取除图片外的文件图标
  const getOtherFileIcon = (type) => {
    switch (type) {
      case FileType.DOCX:
        return docxIcon;
      case FileType.XLSX:
        return excelIcon;
      case FileType.HTM:
      case FileType.HTML:
        return htmlIcon;
      case FileType.MD:
        return markdownIcon;
      case FileType.PDF:
        return pdfIcon;
      case FileType.TXT:
        return txtIcon;
      case FileType.CSV:
        return csvIcon;
      default:
        break;
    }
  };

  // hover显示操作按钮
  const handleHoverItem = (index, operate) => {
    if (operate === 'enter') {
      setShowOperateIndex(index);
    } else {
      setShowOperateIndex(-1);
    }
  };

  // 删除文件
  const deleteFile = (index) => {
    setShowOperateIndex(-1);
    updateFileList(fileList.filter((item, idx) => idx !== index));
  };

  const getDetail = (file) => {
    switch (file.uploadStatus) {
      case 'success':
        return bytesToSize(file.size);
      case 'uploading':
        return t('uploading');
      case 'failed':
        return file.failedReason || t('uploadFailed');
      default:
        break;
    }
  };

  return <div className='chat-file-list' style={{ flexDirection: isDebug ? 'column' : 'row' }}>
    {
      fileList.map((file, index) =>
        <div className={`file-container ${isChatUpload ? 'editable' : ''} ${file.uploadStatus === 'failed' ? 'upload-failed' : ''} ${isDebug ? 'debug-item' : ''}`}
          onMouseEnter={() => handleHoverItem(index, 'enter')}
          onMouseLeave={() => handleHoverItem(index, 'leave')}
          key={index}
        >
          <div className='file-icon'>{getFileIcon(file.file_type, file.file_url, file.file_name)}</div>
          <div className='file-content'>
            {getFileName(file.file_name)}
            {isChatUpload && <div className='file-detail'>{getDetail(file)}</div>}
          </div>
          {
            isChatUpload && showOperateIndex === index && <img className='delete-file' src={deleteFileIcon} alt="" onClick={() => deleteFile(index)} />
          }
        </div>
      )
    }
  </div>
};

export default FileList;
