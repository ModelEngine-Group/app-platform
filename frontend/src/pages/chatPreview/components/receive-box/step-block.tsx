/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, memo } from 'react';
import { useTranslation } from 'react-i18next';
import StepImg from '@/assets/images/ai/step.svg';
import arrowImg from '@/assets/images/ai/down.png';
import './styles/step-block.scss';

const StepBlock = memo((props: any) => {
  const { t } = useTranslation();
  const { content, finished } = props;
  const stepElRef = useRef<any>(null);
  const [collapse, setcollapse] = useState(false);

  const toggleFold = () => {
    if (!finished) {
      return;
    }
    if (!collapse) {
      stepElRef.current.style.height = 0;
    } else {
      stepElRef.current.style.height = 'auto'
    }
    setcollapse(!collapse);
  }
  return (
    <div className='appengine-step-tool'>
      <div 
        className={[
          'appengine-step-process',
          collapse ? 'step--info-html-collapse' : '',
        ].join(' ')}
        onClick={toggleFold}
      >
        <img src={StepImg} alt='' />
        <span className='step-text'>{t('thinkProcess')}</span>
        { finished && <img src={arrowImg} className='step-arrow' /> }
      </div>
      <div 
        ref={stepElRef}
        className='appengine-step-content' 
        dangerouslySetInnerHTML={{ __html: content }}>
      </div>
    </div>
  );
});

export default StepBlock;
