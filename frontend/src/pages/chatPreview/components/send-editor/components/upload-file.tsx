/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useRef, useState } from 'react';
import { Upload } from 'antd';
import { uploadMultipleFile } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { useTranslation } from 'react-i18next';
import { useAppSelector } from '@/store/hook';
import { FileType, PictureFileType } from '../common/config';
import uploadImg from '@/assets/images/upload_icon.svg';
const supportFileTypes = Object.values(FileType);
const pictureTypes = Object.values(PictureFileType);

const { Dragger } = Upload;
/**
 * 对话框上传文件弹框组件
 *
 * @param maxCount 最大上传文件数.
 * @param fileList 多文件列表.
 * @param updateFileList 更新文件列表的方法.
 * @param updateModal 修改外层modal的打开状态.
 * @return {JSX.Element}
 * @constructor
 */
const UploadFile = (props) => {
  const { maxCount, fileList, updateFileList, updateModal } = props;
  const { t } = useTranslation();
  const [addFileList, setAddFileList] = useState([]);
  const isWarnLimit = useRef(false);
  const isWarnType = useRef(false);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const appInfo = useAppSelector((state) => state.appStore.appInfo);

  const fileDataProcess = async (curFile, curfileList) => {
    const isLastFile = curFile.name === curfileList[curfileList.length - 1].name;
    if (isWarnLimit.current) {
      if ((curfileList.length + fileList.length <= maxCount) || isLastFile) {
        isWarnLimit.current = false;
      } else {
        return false;
      }
    }
    if (curfileList.length + fileList.length > maxCount && !isWarnLimit.current) {
      isWarnLimit.current = true;
      Message({ type: 'warning', content: `${t('currentAvailableUpload')}${maxCount - fileList.length}${t('num')}${t('file')}` });
      return false;
    }
    curfileList.forEach(file => {
      let suffix = '';
      try {
        const fileArr = file.name.split('.');
        suffix = fileArr.pop();
      } catch {
        suffix = '';
      }
      file.file_type = suffix;
      file.file_name = file.name;
      file.uploadStatus = file.size / (1024 * 15) > 1024 ? 'failed' : 'uploading';
      if (file.uploadStatus === 'failed') {
        file.failedReason = t('uploadExceedLimit');
      }
      if (fileList.find(item => item.file_name === file.name)) {
        Message({ type: 'warning', content: `${t('alreadyUploaded')}${file.name}` });
      }
      if (pictureTypes.includes(file.file_type.toLocaleLowerCase())) {
        file.file_url = URL.createObjectURL(file);
      }
    });
    const illegalTypeList = (curfileList.filter(item => !supportFileTypes.includes(item.file_type.toLocaleLowerCase()))).map(it => it.file_type);
    if (illegalTypeList.length && !isWarnType.current) {
      isWarnType.current = true;
      const illegalTypeStr = illegalTypeList.join('/');
      Message({ type: 'warning', content: `${t('notSupportedUpload')}${illegalTypeStr}${t('fileFormat')}` });
    }
    if (isWarnType.current && isLastFile) {
      isWarnType.current = false;
    }
    curfileList = curfileList.filter(item => {
      return supportFileTypes.includes(item.file_type.toLocaleLowerCase()) && !fileList.find(it => it.file_name === item.name);
    });
    if (curfileList.length) {
      setAddFileList(curfileList);
      updateFileList([...fileList, ...curfileList]);
      if (updateModal) {
        updateModal(false);
      }
    }
    return false;
  };

  const getFileType = () => {
    const typeList = supportFileTypes.map(item => `.${item}`);
    return typeList.join();
  };

  // 批量上传文件
  const uploadFiles = async () => {
    const formData = new FormData();
    addFileList.filter(item => item.uploadStatus !== 'failed').forEach((file => {
      formData.append('file', file);
    }))
    let resultData = [];
    try {
      const result = await uploadMultipleFile(tenantId, appInfo.id, formData);
      if (result.code === 0 && result.data) {
        resultData = result.data;
      }
    } finally {
      updateFileList((preList) => {
        const curList = preList.map(file => {
          const resultItem = resultData.find(item => item.file_name === file.name || item.file_name === file.name.replace(/\s+/g, ''));
          if (resultItem) {
            return Object.assign(file, { file_name: file.name, file_url: resultItem.file_path }, { uploadStatus: 'success' });
          } else {
            const addItem = addFileList.find(item => item.name === file.name);
            if (addItem) {
              return Object.assign(file, { uploadStatus: 'failed' });
            } else {
              return file;
            }
          }
        })
        return curList;
      });
    }
  };

  // 点击上传
  const handleClick = () => {
    if (fileList.length > (maxCount - 1)) {
      Message({ type: 'error', content: `${t('chatFileLimitTip')}${maxCount}${t('num')}${t('file')}` });
    }
  };

  useEffect(() => {
    if (addFileList.length) {
      // 发送请求
      uploadFiles();
    }
  }, [addFileList]);
  return <>{(
    <div className='dragger-modal'>
      <Dragger
        fileList={[]}
        multiple
        maxCount={maxCount}
        accept={getFileType()}
        beforeUpload={fileDataProcess}
        openFileDialogOnClick={fileList.length < maxCount}
      >
        <div className='import-upload' onClick={handleClick}>
          <img src={uploadImg} />
          <div className='upload-word'>{t('fileUploadContent1')}</div>
          <div className='tips'>{`${t('chatSupportedFileTypes')}${supportFileTypes.join('/')}`}</div>
          <div className='tips'>
            <span>{t('chatFileSizeTip')}</span>
            <span>{`${t('chatFileLimitTip')}${maxCount}${t('num')}${t('file')}`}</span>
            <span>{t('currentAvailableUpload')}</span>
            <span className='available-count'>{maxCount - fileList.length}</span>
            <span>{`${t('num')}${t('file')}`}</span>
          </div>
        </div>
      </Dragger>
    </div>
  )}</>
};


export default UploadFile;
