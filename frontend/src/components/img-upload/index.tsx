/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Button, Upload } from 'antd';
import { ToTopOutlined } from '@ant-design/icons';
import { fileValidate } from '@/shared/utils/common';
import serviceConfig from '@/shared/http/httpConfig';
import { uploadImage } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { useAppSelector } from '@/store/hook';
import { convertImgPath } from '@/common/util';
import { useTranslation } from 'react-i18next';
import knowledgeImg from '@/assets/images/knowledge/knowledge-base.png';

const ImgUpload = () => {
  const { t } = useTranslation();
  const [imgPath, setImgPath] = useState('');
  const { AIPP_URL } = serviceConfig;
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  // 上传图片
  async function pictureUpload(file) {
    const headers = {
      'attachment-filename': encodeURI(file.name || ''),
    };
    try {
      const formData = new FormData();
      formData.append('file', file);
      const res = await uploadImage(tenantId, formData, headers);
      if (res.code === 0) {
        let path = `${AIPP_URL}/${tenantId}/file?filePath=${res.data.file_path}&fileName=${res.data.file_name}`;
        convertImgPath(path).then(res => {
          setImgPath(res);
        });
      }
    } catch (err) {
      Message({ type: 'error', content: err.message || t('uploadImageFail') });
    }
  }
  const beforeUpload = (file) => false;
  const onChange = ({ file }) => {
    let validateResult = fileValidate(file);
    if (!validateResult) {
      return
    }
    validateResult && pictureUpload(file);
  };

  return (
    <>
      <div className='avatar'>
        {imgPath ? (
          <img
            className='img-send-item'
            src={imgPath}
          />
        ) : (
          <img src={knowledgeImg} />
        )}
        <Upload
          beforeUpload={beforeUpload}
          onChange={onChange}
          showUploadList={false}
          accept='.jpg,.png,.gif,.jpeg'
        >
          <Button size='small' icon={<ToTopOutlined />}>
            {t('uploadManually')}
          </Button>
        </Upload>
      </div>
    </>
  );
};

export default ImgUpload;
