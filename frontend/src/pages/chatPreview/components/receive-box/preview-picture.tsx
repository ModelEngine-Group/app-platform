/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import PictureList from './picture-list';
import downloadIcon from '@/assets/images/ai/download_preview.svg';
import closeIcon from '@/assets/images/ai/close_preview.svg';
import './styles/preview-picture.scss';

/**
 * 文生图预览页面组件
 *
 * @param pictureList 图片列表.
 * @param curPicturePath 预览主图的路径.
 * @param closePreview 关闭预览的方法.
 * @return {JSX.Element}
 * @constructor
 */
const PreviewPicture = ({ pictureList, curPicturePath, closePreview }) => {
  const { t } = useTranslation();
  const [curPreviewPic, setCurPreviewPic] = useState('');

  useEffect(() => {
    setCurPreviewPic(curPicturePath);
  }, [curPicturePath]);

  const download = () => {
    const downloadLink = document.createElement('a');
    downloadLink.href = curPreviewPic;
    downloadLink.target = '_self'; // 确保图片在当前页面下载
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
  };

  return <div className='preview-picture'>
    <PictureList isPreview pictureList={pictureList} curPicturePath={curPreviewPic} updatePreviewPath={setCurPreviewPic}/>
    <div className='preview-header'>
      <div className='download-preview' onClick={download}>
        <img src={downloadIcon} alt="" style={{ marginRight: 4 }} />
        <span>{t('download')}</span>
      </div>
      <img src={closeIcon} onClick={closePreview} />
    </div>
  </div>
};

export default PreviewPicture;