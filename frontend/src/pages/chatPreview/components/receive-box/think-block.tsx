/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, memo } from 'react';
import { UpOutlined } from '@ant-design/icons';
import ThinkBtn from './think-btn';
import './styles/think-block.scss';

const ThinkBlock = memo(({ content = '', thinkTime = '' }) => {
  let thinkEndIdx = content.indexOf('</think>');
  const thinkFinished = thinkEndIdx > -1;
  const [collapse, setcollapse] = useState(false);
  const thinkElRef = useRef<any>(null);

  const toggleFold = () => {
    if (!thinkFinished) {
      return;
    }
    if (!collapse) {
      thinkElRef.current.style.height = 0;
    } else {
      thinkElRef.current.style.height = 'auto'
    }
    setcollapse(!collapse);
  };

  return (
    <>
      <div className='think-info-btn' onClick={toggleFold}>
        <ThinkBtn finished={thinkFinished} time={thinkTime} />
        {thinkFinished && <UpOutlined rotate={collapse ? 180 : 0} />}
      </div>
      <div
        className={[
          'think-info-html',
          thinkFinished ? 'think-info-html-finished' : '',
          collapse ? 'think-info-html-collapse' : '',
        ].join(' ')}
        ref={thinkElRef}
        dangerouslySetInnerHTML={{ __html: content }}
      ></div>
    </>
  );
});

export default ThinkBlock;
