/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { memo } from 'react';
import { LoadingOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import BrainImg from '@/assets/images/ai/brain.svg';

const ThinkBtn = memo(({ finished, time }) => {
  const { t } = useTranslation();
  const transTime = (milliseconds: number | '') => {
    if (!milliseconds) {
      return '';
    }
    if (milliseconds < 1000) return `（${t('timeUse')}${(milliseconds / 1000).toFixed(2)}${t('second')}）`;
    let seconds = Math.floor(milliseconds / 1000);
    let minutes = Math.floor(seconds / 60);
    let hours = Math.floor(minutes / 60);

    seconds %= 60;
    minutes %= 60;
    let text = `（${t('timeUse')}`;
    if (hours > 0) {
      text += hours + t('hour');
    }
    if (minutes > 0) {
      text += minutes + t('minute');
    }
    if (seconds > 0) {
      text += seconds + t('second');
    }
    text += '）';
    return text;
  };

  return (
    <>
      <img src={BrainImg} alt='' />
      { finished ? <span>{t('deepThinkFinished')}{transTime(time)}</span> : <span>{t('deepThink')}</span> }
      {!finished && <LoadingOutlined />}
    </>
  );
});

export default ThinkBtn;
