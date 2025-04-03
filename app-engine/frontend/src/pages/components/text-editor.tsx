/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { forwardRef, useImperativeHandle, useEffect } from 'react';
import { uploadImage } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { useTranslation } from 'react-i18next';
import { useAppSelector } from '@/store/hook';
import { getCookie, fileValidate } from '@/shared/utils/common';
import serviceConfig from '@/shared/http/httpConfig';
import tinymce from 'tinymce';
import 'tinymce/models/dom/index.js';
import 'tinymce/icons/default/index.js';
import 'tinymce/plugins/image/index.js';
import 'tinymce/plugins/table/index.js';
import 'tinymce/plugins/lists/index.js';
import 'tinymce/plugins/wordcount/index.js';
import 'tinymce/skins/ui/oxide/skin.min.css';
import 'tinymce/themes/silver/theme.min.js';
const { AIPP_URL } = serviceConfig;

/**
 * 发布应用富文本编辑器组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const TextEditor = forwardRef((props, ref) => {
  const { t } = useTranslation();
  const cLocale = getCookie('locale');
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  // 对外暴露方法
  useImperativeHandle(ref, () => {
    return {
      handleChange: () => {
        let chartCount = tinymce.activeEditor?.plugins.wordcount.body.getCharacterCount();
        let editorBody = tinymce.activeEditor?.getBody();
        let imgNodes = tinymce.activeEditor?.dom.select('img', editorBody);
        let imgCount = imgNodes.length;
        if (imgCount > 10) {
          Message({ type: 'warning', content: t('max_editor_img')})
          return false;
        }
        if (chartCount > 5000) {
          Message({ type: 'warning', content: t('max_editor_count')})
          return false;
        }
        let content = tinymce.activeEditor?.getContent();
        return content
      }
    }
  });
  // 上传图片
  async function pictureUpload(file) {
    if (!fileValidate(file, ['jpg', 'png', 'jpeg', 'gif'], 5)) {
      return false;
    }
    const headers = {
      'attachment-filename': encodeURI(file.name || ''),
    };
    try {
      const formData = new FormData();
      formData.append('file', file);
      const res: any = await uploadImage(tenantId, formData, headers);
      if (res.code === 0) {
        return {
          filePath: res.data.file_path,
          fileName: res.data.file_name
        };
      }
    } catch (err) {
      Message({ type: 'error', content: err.message || t('uploadImageFail') });
    }
  }

  // 基于环境调整tinymce初始化文件路径
  const adjustInitPathByEnv = url => {
    if (process.env.NODE_ENV === 'production' && process.env.PACKAGE_NODE === 'spa') {
        return `/apps/appengine/${url}`;
    }
    return url;
  }

  useEffect(() => {
    tinymce.init({
      selector: '#publish-editor',
      plugins: 'lists image table wordcount',
      language: 'zh_CN',
      language_url: cLocale === 'en-us' ? '': `${adjustInitPathByEnv('/src/assets/tinymce/lang/zh-CN.js')}`,
      skin_url: `${adjustInitPathByEnv('/src/assets/tinymce/skins/ui/oxide')}`,
      content_css: `${adjustInitPathByEnv('/src/assets/tinymce/skins/content/default/content.css')}`,
      height: 260,
      menubar: false,
      statusbar: false,
      font_size_formats: '12px 14px 16px 18px 24px 36px 48px 56px 72px',
      line_height_formats: '0.5 1.0 1.5 2.0 2.5 3.0 3.5 4.0',
      style_formats: [
        {title: 'h1', selector: 'h1', classes: 'text-editer-center'},
        {title: 'h2', selector: 'h2', classes: 'text-editer-center'},
        {title: 'h3', selector: 'h3', classes: 'text-editer-center'},
        {title: 'h4', selector: 'h4', classes: 'text-editer-centerr'},
        {title: 'h5', selector: 'h5', classes: 'text-editer-center'},
        {title: 'image', selector: 'img', classes: 'text-editer-center'},
        {title: 'p', selector: 'p', classes: 'text-editer-center'},
        {title: 'span', selector: 'span', classes: 'text-editer-center'},
      ],
      toolbar: `formatselect fontsizeselect | forecolor backcolor bold italic underline strikethrough link | 
      alignleft aligncenter alignright | 
      bullist numlist | table image wordcount`,
      images_upload_handler: (blobInfo, progress) => {
        return new Promise(async (resolve, reject) => {
          const { filePath, fileName } = await pictureUpload(blobInfo.blob());
          if (filePath) {
            const url = `${AIPP_URL}/${tenantId}/file?filePath=${filePath}&fileName=${fileName}`;
            resolve(url);
          } else {
            resolve('');
          }
        });
      }
    });
    return () => {
      tinymce.remove();
    }
  }, []);
  return <>{(
    <div style={{ height: '280px' }}>
      <textarea id='publish-editor'></textarea>
    </div>
  )}</>
});

export default TextEditor;
