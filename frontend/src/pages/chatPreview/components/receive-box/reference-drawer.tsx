/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Drawer } from 'antd';
import { useTranslation } from 'react-i18next';
import 'highlight.js/styles/monokai-sublime.min.css';
import './styles/message-detail.scss';

/**
 * 溯源抽屉弹窗
 *
 * @isOpen 显示隐藏
 * @setIsOpen 显示隐藏回调
 * @reference 溯源数据
 * @referenceStr 溯源拼接后数据
 * @referenceIndex 溯源对应的索引
 */
const MessageRefence = (props: any) => {
  const { t } = useTranslation();
  const { isOpen, setIsOpen, reference, referenceStr, referenceIndex } = props;
  const [text, setText] = useState([]);

  useEffect(() => {
    if (isOpen && referenceStr) {
      let obj: any = {};
      let arr = [];
      let referenceList = reference[referenceIndex] || [];
      Object.keys(referenceList).forEach((item)=>{
        obj[item] = referenceList[item].text;
      })
      arr = referenceStr.split('_').map((item: any) => {
        return obj[item];
      });
      setText(arr);
    }
  }, [isOpen]);

  // 关闭抽屉回调
  const onClose = () => {
    setIsOpen(false);
    document.querySelectorAll('.source-word').forEach((item) => {
      item.classList.remove('source-word-click');
    });
  };

  return (
    <Drawer destroyOnClose title={t('source')} width={800} open={isOpen} onClose={onClose}>
      {text.map((item, index) => {
        return (
          <div key={index} className='source-drawer'>
            <span>{item}</span>
          </div>
        );
      })}
    </Drawer>
  );
};

export default MessageRefence;
