/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useImperativeHandle, useEffect, useRef } from 'react';
import { Modal, Input, Button, Spin } from 'antd';
import { generatePrompt } from '@/shared/http/aipp';
import { useTranslation } from 'react-i18next';
import { Message } from '@/shared/utils/message';
import EmptyPromptIcon from '@/assets/images/ai/empty_prompt.png';
import GenerateBtnIcon from '@/assets/images/ai/generate_btn_icon.png';
import LoadingIcon from '@/assets/images/ai/loading_icon.png';
import '../styles/prompt-word.scss';

/**
 * AI生成提示词的弹框组件
 *
 * @param promptWordRef 当前组件ref.
 * @param updatePromptValue 更新父组件提示词.
 * @param currentModelInfo 模型的信息（模型、温度）.
 * @return {JSX.Element}
 * @constructor
 */

const PromptWord = ({ promptWordRef, updatePromptValue, currentModelInfo }) => {
  const generateStatusMap = {
    NOT_GENEATED: 'notGenerated',
    GENEATING: 'generating',
    GENEATED: 'generated',
  };
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [command, setCommand] = useState('');
  const [prompt, setPrompt] = useState('');
  const [generateStatus, setGenerateStatus] = useState(generateStatusMap.NOT_GENEATED);
  const isDrawerOpen = useRef(false);
  const isGenerating = useRef(false);

  useImperativeHandle(promptWordRef, () => {
    return {
      openPromptModal: (isFormDrawer) => {
        isDrawerOpen.current = isFormDrawer;
        setOpen(true);
      }
    };
  });

  // 生成提示词
  const handleGenerate = async () => {
    setGenerateStatus(generateStatusMap.GENEATING);
    isGenerating.current = true;
    setPrompt('');
    try {
      const res = await generatePrompt(
        {
          input: command,
          templateType: currentModelInfo.templateType,
        }
      );
      if (res?.code === 0 && res?.data) {
        if (isGenerating.current) {
          setPrompt(res.data.substring(0, 5000));
          setGenerateStatus(generateStatusMap.GENEATED);
        }
      } else {
        setGenerateStatus(generateStatusMap.NOT_GENEATED);
      }
    } catch (err) {
      setGenerateStatus(generateStatusMap.NOT_GENEATED);
    } finally {
      isGenerating.current = false;
    }
  };

  // 输入指令
  const commandChange = (e) => {
    setCommand(e.target.value);
  };

  // 提示词输入
  const promptChange = (e) => {
    setPrompt(e.target.value);
  };

  // 确认应用提示词
  const confirm = () => {
    if (generateStatus !== generateStatusMap.GENEATED && !prompt) {
      Message({ type: 'warning', content: t('confirmUseTip') });
      return;
    }
    updatePromptValue(prompt, !isDrawerOpen.current);
    setOpen(false);
  };

  // 提示词显示内容
  const getGenerateContent = () => {
    switch (generateStatus) {
      case generateStatusMap.GENEATING:
        return <Spin indicator={<img src={LoadingIcon} alt="" />} tip={t('intelligentGeneration')}></Spin>
      case generateStatusMap.GENEATED:
        return <Input.TextArea
          value={prompt}
          maxLength={5000}
          showCount
          style={{ height: '100%', width: '100%' }}
          onChange={promptChange}
        />
      default:
        return <div className='empty-prompt'>
          <img src={EmptyPromptIcon} alt="" />
          <div className='empty-tip'>{t('pleaseEnterTheCommandOnTheLeft')}</div>
        </div>
    }
  };

  // 初始弹框内容
  useEffect(() => {
    setCommand('');
    setGenerateStatus(generateStatusMap.NOT_GENEATED);
    setPrompt('');
    isGenerating.current = false;
  }, [open]);

  return <>
    <Modal title="Basic Modal"
      open={open}
      onCancel={() => setOpen(false)}
      maskClosable={false} width={988}
      footer={null}
      wrapClassName="prompt-word"
    >
      <div className='prompt-content'>
        <div className='content-part'>
          <div className='generate-title ai-generate'>{'AI' + t('generate')}</div>
          <div className='enter-command'>{t('enterCommand')}</div>
          <Input.TextArea
            value={command}
            placeholder={`${t('plsEnter')}`}
            className='command-text'
            maxLength={500}
            showCount
            onChange={commandChange}
          />
          <div className='prompt-footer'>
            <Button
              disabled={generateStatus === generateStatusMap.GENEATING || !command}
              className='generate-btn'
              onClick={handleGenerate}
            >
              <img src={GenerateBtnIcon} alt="" style={{ marginRight: 5 }} />
              <span>{t('generate')}</span>
            </Button>
          </div>
        </div>
        <div className='content-part'>
          <div className='generate-title'>{t('generate') + t('promptName')}</div>
          <div className='prompt-area'>{getGenerateContent()}</div>
          <div className='prompt-footer'>
            <Button style={{ width: 90 }} onClick={() => setOpen(false)}>{t('cancel')}</Button>
            <Button type='primary' style={{ width: 90, marginLeft: 16 }} onClick={confirm}>{t('confirmUse')}</Button>
          </div>
        </div>
      </div>
    </Modal>
  </>
};

export default PromptWord;
