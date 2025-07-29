/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useImperativeHandle } from 'react';
import { Drawer } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';

/**
 * 使用帮助
 *
 * @param helpRef 组件引用
 * @return {JSX.Element}
 * @constructor
 */
const UseHelp = ({ helpRef }) => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  useImperativeHandle(helpRef, () => {
    return { openHelp: () => setOpen(true) };
  });

  const content = [
    {
      title: t('implementationTutorialTitle'),
      content: t('implementationTutorialContent')
    },
    {
      title: t('useTutorialTitle'),
      content: t('implementationTutorialContent1')
    },
    {
      title: t('packTutorialTitle'),
      content: t('implementationTutorialContent2')
    },
    {
      title: t('publishTutorialTitle'),
      content: t('implementationTutorialContent3')
    },
  ]

  return (
    <Drawer
      title={t('help')}
      className='intelligent-form'
      maskClosable={false}
      closable={false}
      open={open}
      width={570}
      extra={
        <CloseOutlined
          onClick={() => {
            setOpen(false);
          }}
        />
      }
    >
      {
        content.map((item, index) =>
          <div key={index} className='help-item'>
            <div className='help-title'>{item.title}</div>
            <div className='help-content'>{item.content}</div>
          </div>
        )
      }
    </Drawer>
  )
}
export default UseHelp;