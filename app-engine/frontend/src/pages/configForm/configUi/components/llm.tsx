/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef, useImperativeHandle } from 'react';
import { Form, Select, InputNumber, Input } from 'antd';
import PromptWord from './prompt-word';
import PromptTemplate from './prompt-template';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setValidateInfo } from '@/store/appInfo/appInfo';
import { getModels } from '@/shared/http/appBuilder';
import { useTranslation } from 'react-i18next';
import GenerateIcon from '@/assets/images/ai/generate_icon.png';
import FullScreenIcon from '@/assets/images/ai/full_screen_icon.png';

const LLM = (props) => {
  const { t } = useTranslation();
  const { updateData, llmRef, form, validateItem, readOnly } = props;
  const [showControl, setShowControl] = useState(true);
  const [models, setModels] = useState([]);
  const [promptValue, setPromptValue] = useState('');
  const [currentModelInfo, setCurrentModelInfo] = useState({});
  const isPromptChange = useRef(false);
  const isTemperatureChange = useRef(false);
  const promptWordRef = useRef();
  const promptTemplateRef = useRef();
  const dispatch = useAppDispatch();
  const appValidateInfo = useAppSelector((state) => state.appStore.validateInfo);
  const { TextArea } = Input;

  useImperativeHandle(llmRef, () => {
    return { setPromptValue };
  });

  const updatePromptValue = (val, updateToInput = true) => {
    setPromptValue(val);
    // 如果是从抽屉打开的弹框，弹框确认应用提示词后只更新抽屉中的值，不更新提示词输入框的值
    if (updateToInput) {
      if (val !== form.getFieldValue('systemPrompt')) {
        isPromptChange.current = true;
        form.setFieldValue('systemPrompt', val);
        updatePrompt(val);
      }
    }
  };

  const handleGetModels = (open) => {
    if (!open) return;
    getModels().then((res) => {
      const models = res.models.map((model) => {
        return {
          ...model,
          id: model.serviceName + '***' + model.tag
        }
      })
      setModels(models);
    })
  }

  // 温度输入框formatter
  const formatter = (newValue) => {
    if (!newValue) {
      return '';
    }
    const value = parseFloat(newValue);
    if (value === 0.0) {
      return 0;
    }
    if (value >= 1) {
      return 1;
    }
    return value;
  }

  // 温度输入框失焦回调
  const handleTemperBlur = (e) => {
    if (e.target.value === '') {
      return;
    }
    let originValue = parseFloat(e.target.value);
    if (isNaN(originValue)) {
      originValue = 0;
    }
    let changeValue;
    if (originValue <= 0.0) {
      changeValue = 0;
    } else if (originValue >= 1.0) {
      changeValue = 1;
    } else {
      // 保留一位小数
      changeValue = Math.round(originValue * 10) / 10;
    }
    if (isTemperatureChange.current) {
      setTimeout(() => updateData({ temperature: changeValue }), 100);
    }
  };

  // 更新保存提示词
  const updatePrompt = (val) => {
    if (isPromptChange.current) {
      updateData({ systemPrompt: val });
    }
  };

  // 校验模型是否存在
  const checkExist = (rules, value) => {
    if (!models.find(item => item.id === value)) {
      return Promise.reject(new Error(`${t('LLM')}${value}${t('modelNotExistTip')}`));
    }
    return Promise.resolve();
  };

  const handleUpdateModel = (models, value) => {
    if (appValidateInfo.length) {
      form.validateFields(['model']).then(() => {
        dispatch(setValidateInfo(appValidateInfo.filter(item => (item.configCheckId !== validateItem.configCheckId) && item.configName !== 'accessInfo')));
      })
    }
    const updateModelInfo = models.find(item => item.id === value);
    updateData({ model: value, accessInfo: { serviceName: updateModelInfo.serviceName, tag: updateModelInfo.tag } });
  };

  const openGeneratePrompt = (isFormDrawer = false) => {
    setCurrentModelInfo({
      templateType: 'system',
    });
    promptWordRef.current.openPromptModal(isFormDrawer);
  };

  const openPromptDrawer = () => {
    setPromptValue(form.getFieldValue('systemPrompt'));
    promptTemplateRef.current.openPromptDrawer()
  };

  useEffect(() => {
    handleGetModels(true);
  }, [])

  return (
    <>
      <div className='control-container llm-container'>
        <div className='control'>
          <div style={{ display: showControl ? 'block' : 'none' }}>
            <div style={{ display: 'flex' }}>
              <Form.Item
                style={{ flex: 1, marginRight: 10, maxWidth: 200 }}
                name='model'
                label={t('LLM')}
                rules={[{ required: true },
                { validator: checkExist }
                ]}
              >
                <Select
                  className={'full-border'}
                  placeholder={t('selectLlm')}
                  options={models}
                  onDropdownVisibleChange={(open) => handleGetModels(open)}
                  onChange={(value) => handleUpdateModel(models, value)}
                  fieldNames={{
                    label: 'serviceName',
                    value: 'id'
                  }}
                >
                </Select>
              </Form.Item>
              <Form.Item
                style={{ flex: 1 }}
                name='temperature'
                label={t('temperature')}
                rules={[{ required: true }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  max={1}
                  controls={true}
                  keyboard={true}
                  precision={1}
                  onBlur={handleTemperBlur}
                  onChange={() => isTemperatureChange.current = true}
                  step={0.1}
                  type="number"
                  formatter={formatter}
                />
              </Form.Item>
            </div>
            <div style={{ position: 'relative' }}>
              <Form.Item name='systemPrompt' label={t('promptName')} style={{ marginBottom: 0 }}>
                <TextArea
                  placeholder={t('promptHolder')}
                  rows={16}
                  showCount
                  maxLength={2000}
                  onChange={() => isPromptChange.current = true}
                  onBlur={(e) => updatePrompt(e.target.value)}
                />
              </Form.Item>
              <div className='generate-btn'>
                <img src={GenerateIcon} alt="" style={{ marginRight: 5 }} onClick={() => openGeneratePrompt()} className={readOnly ? 'version-preview' : ''} />
                <img src={FullScreenIcon} alt="" onClick={openPromptDrawer} />
              </div>
            </div>
          </div>
        </div>
        <PromptWord
          promptWordRef={promptWordRef}
          updatePromptValue={updatePromptValue}
          currentModelInfo={currentModelInfo}
        ></PromptWord>
        <PromptTemplate
          promptTemplateRef={promptTemplateRef}
          promptValue={promptValue}
          openGeneratePrompt={openGeneratePrompt}
          updatePromptValue={updatePromptValue}
          readOnly={readOnly}
        ></PromptTemplate>
      </div>
    </>
  )
};
export default LLM;
