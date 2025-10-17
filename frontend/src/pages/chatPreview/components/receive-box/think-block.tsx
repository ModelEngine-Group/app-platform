/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, memo, useEffect } from 'react';
import { UpOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import { Message } from '@/shared/utils/message';
import { isChatRunning } from '@/shared/utils/chat';
import ThinkBtn from './think-btn';
import './styles/think-block.scss';

interface ThinkBlockProps {
  content: string;
  thinkTime: any;
}

const ThinkBlock = memo<ThinkBlockProps>(({ content = '', thinkTime = '' }) => {
  const { t } = useTranslation();
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

  // 处理引用标签的点击和悬停事件
  useEffect(() => {
    const container = thinkElRef.current;
    if (!container) return;

    const handleReferenceClick = (event: Event) => {
      const target = event.target as HTMLElement;
      if (target.classList.contains('reference-circle') || target.classList.contains('think-reference')) {
        event.preventDefault();
        event.stopPropagation();

        const url = target.getAttribute('data-ref-url');

        if (isChatRunning()) {
          Message({ type: 'warning', content: t('tryLater') });
          return;
        }

        if (url && /^https?:\/\//.test(url)) {
          window.open(url, '_blank');
        }
      }
    };

    const handleReferenceMouseEnter = (event: Event) => {
      const target = event.target as HTMLElement;
      if (target.classList.contains('reference-circle') || target.classList.contains('think-reference')) {
        // 清除之前可能存在的 tooltip
        const existingTooltips = document.querySelectorAll('[data-tooltip-id="think-ref-tooltip"]');
        existingTooltips.forEach(t => t.remove());

        const title = target.getAttribute('data-ref-title') || '未知来源';
        const summary = target.getAttribute('data-ref-summary') || '无摘要';

        const tooltip = document.createElement('div');
        tooltip.className = 'reference-hover-tooltip';
        tooltip.setAttribute('data-tooltip-id', 'think-ref-tooltip');
        tooltip.innerHTML = `
          <div class="reference-hover-title">${title}</div>
          <div class="reference-hover-summary">${summary}</div>
        `;

        // 计算 tooltip 位置
        const rect = target.getBoundingClientRect();
        tooltip.style.left = `${rect.left}px`;
        tooltip.style.top = `${rect.bottom + 8}px`;

        document.body.appendChild(tooltip);

        // 移除 tooltip
        const removeTooltip = () => {
          const existingTooltip = document.querySelector('[data-tooltip-id="think-ref-tooltip"]');
          if (existingTooltip) {
            existingTooltip.remove();
          }
          target.removeEventListener('mouseleave', removeTooltip);
        };

        target.addEventListener('mouseleave', removeTooltip);
      }
    };

    container.addEventListener('click', handleReferenceClick);
    container.addEventListener('mouseenter', handleReferenceMouseEnter, true);

    return () => {
      container.removeEventListener('click', handleReferenceClick);
      container.removeEventListener('mouseenter', handleReferenceMouseEnter, true);
      // 清理可能残留的 tooltip
      const tooltips = document.querySelectorAll('[data-tooltip-id="think-ref-tooltip"]');
      tooltips.forEach(t => t.remove());
    };
  }, [content, t]);

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
