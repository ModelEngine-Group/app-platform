/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect, useImperativeHandle } from 'react';
import { Modal, Upload } from 'antd';
import type { UploadProps } from 'antd';
import { useTranslation } from 'react-i18next';
import { Message } from '@/shared/utils/message';
import { fileValidate } from '@/shared/utils/common';
import { bytesToSize } from '@/common/util';
import { importApp } from '@/shared/http/aipp';
import uploadAppImg from '@/assets/images/upload_icon.svg';
import complateImg from '@/assets/images/ai/complate.png';
import deleteImg from '@/assets/images/ai/delete.png';

/**
 * 导入应用组件
 *
 * @param uploadRef 当前组件的ref.
 * @param tenantId 租户id.
 * @param addAippCallBack 新增应用的回调方法.
 * @return {JSX.Element}
 * @constructor
 */

const UploadApp = ({ uploadRef, tenantId, addAippCallBack }) => {
  const { t } = useTranslation();
  const [importOpen, setImportOpen] = useState(false);
  const [fileList, setFileList] = useState([]);
  const [imporLoading, setImporLoading] = useState(false);

  useImperativeHandle(uploadRef, () => {
    return {
      openUpload: () => setImportOpen(true),
    };
  });

  // 上传应用
  const handleImportApp = async () => {
    const importFile = fileList[0];
    if (!importFile) {
      Message({ type: 'warning', content: t('fileNotUploaded') });
      return;
    }
    setImporLoading(true);
    const formData = new FormData();
    formData.append('file', importFile);
    const result = await importApp(tenantId, formData);
    try {
      if (result?.code === 0 && result?.data) {
        addAippCallBack(result.data.id, result.data.aippId, result.data.appCategory);
        setFileList([]);
      }
    } finally {
      setImporLoading(false);
    }
  };

  const customRequest = async (val) => {
    setFileList([val.file]);
    val.onSuccess();
  };

  const beforeUpload = (file) => {
    if (fileList.length) {
      Message({ type: 'warning', content: t('onlyOneApplicationTip') });
      return false;
    }
    if (!fileValidate(file, ['json'], 3)) {
      return false;
    }
    return true;
  };

  const uploadProps: UploadProps = {
    name: 'file',
    customRequest,
    beforeUpload,
    showUploadList: false,
  };

  useEffect(() => {
    if (!importOpen) {
      setFileList([]);
      setImporLoading(false);
    }
  }, [importOpen])

  return <>
    <Modal
      title={t('importApplication')}
      width='526px'
      open={importOpen}
      centered
      onOk={handleImportApp}
      onCancel={() => setImportOpen(false)}
      okButtonProps={{ loading: imporLoading }}
      okText={t('create')}
      cancelText={t('cancel')}
      maskClosable={false}
      className='upload-app'
    >
      <Upload.Dragger {...uploadProps} accept='.json'>
        <div className='import-upload'>
          <img src={uploadAppImg} />
          <div className='upload-word'>{t('fileUploadContent1')}</div>
          <div className='tips'>{t('uploadAppTip')}</div>
        </div>
      </Upload.Dragger>
      <div className='file-upload-list'>
        {fileList?.map((item) => (
          <div className='file-item' key={item.uid}>
            <div className='file-item-left'>
              <span className='file-name'>{item.name}</span>
              <span>({bytesToSize(item.size)})</span>
            </div>
            <div className='file-item-right'>
              <img src={complateImg} />
              <img src={deleteImg} onClick={() => setFileList([])} />
            </div>
          </div>
        ))}
      </div>
    </Modal>
  </>
};

export default UploadApp;
