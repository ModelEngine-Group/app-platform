/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Image } from 'antd';
import previewIcon from '@/assets/images/ai/preview_icon.svg';
import downloadIcon from '@/assets/images/ai/download_icon.svg';
import './styles/picture-list.scss';

/**
 * 文生图列表每张图片上hover显示的预览和下载按钮组件
 *
 * @param picture 当前需要预览或者下载的图片信息.
 * @return {JSX.Element}
 * @constructor
 */
const OperateItem = ({ pictureList, picture }) => {

  const handlePreview = () => {
    const previewPicture = new CustomEvent('previewPicture', {
      detail: {
        pictureList,
        curPicturePath: picture.data
      }
    });
    window.dispatchEvent(previewPicture);
  };

  const download = () => {
    const downloadLink = document.createElement('a');
    downloadLink.href = picture.data;
    downloadLink.target = '_self'; // 确保图片在当前页面下载
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
  };

  return <div className='operate-item' onClick={(e) => e.stopPropagation()}>
    <img src={previewIcon} onClick={handlePreview} />
    <img src={downloadIcon} onClick={download} />
  </div>
};


/**
 * 文生图图片列表组件
 *
 * @param pictureList 图片列表.
 * @param isPreview 是否是预览页面.
 * @param curPicturePath 当前选中查看的图片路径.
 * @return {JSX.Element}
 * @constructor
 */
const PictureList = (props) => {
  const { pictureList, isPreview = false, curPicturePath = '', updatePreviewPath } = props;
  const [curPath, setCurPath] = useState('');

  const handleChosePic = (path) => {
    if (isPreview) {
      updatePreviewPath(path);
    }
    setCurPath(path);
  };

  useEffect(() => {
    if (pictureList.length) {
      if (pictureList.find(item => item.data === curPicturePath)) {
        setCurPath(curPicturePath);
      } else {
        setCurPath(pictureList[0].data);
      }
    }
  }, [pictureList, curPicturePath]);

  return <div className='picture-list' style={{ height: isPreview ? 'calc(100% - 50px)' : 'auto' }}>
    <div style={{ height: isPreview ? 'calc(100% - 76px)' : 448, width: isPreview ? 'auto' : 448 }}>
      <Image
        src={curPath}
        width={'100%'}
        height={'100%'}
        style={{ borderRadius: 15, objectFit: isPreview ? 'contain' : 'cover' }}
        preview={false}
      />
    </div>
    {
      pictureList.length > 1 && <div className='content-list'>
        {pictureList.map((item, index) =>
          <div className={`picture-item ${item.data === curPath ? 'cur-picture' : ''}`} key={index}>
            <Image
              src={item.data}
              height={64}
              width={64}
              style={{ borderRadius: 8, objectFit: 'cover', outline: item.data === curPath ? '2px solid #2673e5' : 'none' }}
              onClick={() => handleChosePic(item.data)}
              preview={isPreview ? false : {
                maskClassName: 'preview-mask',
                visible: false,
                mask: <OperateItem picture={item} pictureList={pictureList} />
              }}
            />
          </div>
        )}
      </div>
    }
  </div>
};

export default PictureList;