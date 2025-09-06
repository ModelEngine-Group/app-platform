/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect } from 'react';
import { Upload, Space, Typography } from 'antd';
import type { UploadProps } from 'antd';
import JSZip from 'jszip';
import { useTranslation } from 'react-i18next';
import { downloadForm } from '@/shared/http/form';
import { Message } from '@/shared/utils/message';
import { fileValidate, getCookie } from '@/shared/utils/common';
import { bytesToSize } from '@/common/util';
import { TENANT_ID } from '../../chatPreview/components/send-editor/common/config';
import UploadAppImg from '@/assets/images/upload_icon.svg';
import complateImg from '@/assets/images/ai/complate.png';
import deleteImg from '@/assets/images/ai/delete.png';

/**
 * 上传表单zip包组件
 *
 * @param drawerOpen 初始化上传.
 * @param previewCallBack 图片预览回调方法.
 * @param file 当前文件对象.
 * @param previewCallBack 图片预览回调方法.
 * @param fileUploadCallBack 图片上传回调方法.
 * @return {JSX.Element}
 * @constructor
 */
const UploadForm = ({ drawerOpen, file, previewCallBack, fileUploadCallBack }) => {
  const { t } = useTranslation();
  const [fileList, setFileList] = useState([]);
  const cLocale = getCookie('locale').toLocaleLowerCase();
  const customRequest = async (val) => {
    const zip = new JSZip();
    zip.loadAsync(val?.file).then((zip) => {
      const requiredFiles = [
        `config.json`,
        `build/index.js`,
        `build/index.html`
      ];
      const requiredImgFiles = [
        `form.png`,
        `form.jpg`,
        `form.jpeg`,
      ];
      const missingFiles = requiredFiles.filter((file) => !zip.file(file));
      const missingImgFiles = requiredImgFiles.filter((file) => zip.file(file));
      if (missingFiles.length > 0) {
        Message({ type: 'warning', content: `${t('uploadFormList')}: ${missingFiles.join(', ')}` });
      } else if (missingImgFiles.length === 0) {
        Message({ type: 'warning', content: t('uploadFormTip') });
      } else if (validateFileSize(missingImgFiles, zip)) {
        Message({ type: 'warning', content: t('uploadFormImg') });
      } else if (validateAllSize(zip)) {
        Message({ type: 'warning', content: t('uploadFormTotal') });
      } else {
        setFileList([val.file]);
        fileUploadCallBack(val.file);
        val.onSuccess();
        setPreviewImg(missingImgFiles, zip);
      }
    }).catch((err) => {
      console.error(err);
      Message({ type: 'error', content: t('fileParseError') });
    });
  };

  // 设置预览图
  const setPreviewImg = (imgFiles, zip) => {
    try {
      zip.file(imgFiles[0]).async('blob').then(blob => {
        const imageUrl = URL.createObjectURL(blob);
        previewCallBack(imageUrl);
      });
    } catch (err) {
      console.warn(err)
    }
  }
  // 验证图片大小
  const validateFileSize = (imgFiles, zip) => {
    let arr = [];
    try {
      arr = imgFiles.filter(item => {
        return (zip.file(item)._data.uncompressedSize / 1024) > 1024;
      })
    } catch (err) {
      console.warn(err);
      return true;
    }
    return arr.length > 0;
  }
  // 总文件大小
  const validateAllSize = (zip) => {
    let totalSize = 0;
    zip.forEach(function (relativePath, zipEntry) {
      if (!zipEntry.dir) {
        totalSize += zipEntry._data.uncompressedSize;
      }
    });
    return (totalSize / 1024) > (1024 * 5);
  }
  // 删除文件列表
  const deleteFileList = () => {
    setFileList([]);
    fileUploadCallBack(null);
    previewCallBack('');
  }
  
  const beforeUpload = (file) => {
    const regex = /^[a-zA-Z0-9\u4e00-\u9fa5]+([_-][a-zA-Z0-9\u4e00-\u9fa5]+)*$/;
    let { name } = file;
    if (!regex.test(name.split('.')[0])) {
      Message({ type: 'warning', content: t('formUploadTips') })
      return false;
    }
    if (!fileValidate(file, ['zip'], 5)) {
      return false;
    }
    return true;
  };

  // 下载模版表单
  const downloadTemplate = async (event) => {
    event.stopPropagation();
    let res:any = await downloadForm(TENANT_ID);
    const blob = new Blob([res], {type: 'application/octet-stream'});
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = 'template.zip';
    link.style.display = 'none';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const uploadProps: UploadProps = {
    name: 'file',
    customRequest,
    beforeUpload,
    showUploadList: false,
  };
  useEffect(() => {
    if (drawerOpen) {
      if (file) {
        setFileList([file])
      } else {
        setFileList([])
      }
    }
  }, [drawerOpen]);
  return <>
    <Upload.Dragger {...uploadProps} accept='.zip'>
      <div className='import-upload'>
        <img src={UploadAppImg} alt="" />
        <div className='upload-word'>{t('fileUploadContent1')}</div>
        <div className='tips'>
          <Space>
            <div className='form-upload-tips'>{t('uploadFormContent')}</div>
            <div className={cLocale !== 'en-us' ? 'form-upload-link' : 'form-upload-link-en'}>
              <Typography.Link onClick={downloadTemplate}>
                {t('downloadTemplateForm')}
              </Typography.Link>
            </div>
          </Space>
        </div>
      </div>
    </Upload.Dragger>
    <div className='file-upload-list'>
      {fileList?.map((item) => (
        <div className='file-item' key={item.size}>
          <div className='file-item-left'>
            <span className='file-name'>{item.name}</span>
            <span>({bytesToSize(item.size)})</span>
          </div>
          <div className='file-item-right'>
            <img src={complateImg} />
            <img src={deleteImg} onClick={deleteFileList} />
          </div>
        </div>
      ))}
    </div>
  </>
};

export default UploadForm;
