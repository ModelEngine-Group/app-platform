/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Form, Select, Slider } from 'antd';
import { useTranslation } from 'react-i18next';

const MutiConversation = (props) => {
  const { t } = useTranslation();
  const { onTypeChange, onValueChange, disabled } = props;

  const options = [{ value: 'ByConversationTurn', label: t('byConversationTurn') }]
  const defaultRecalls = {
    1: '1', [3]: t('default'), 20: '20'
  };
  return <>
    <div>
      <Form.Item
        label={t('pleaseSelectAMemoryMode')}
        name='type'
        rules={[{ required: true, message: t('memoryModeCannotBeEmpty') }]}
        validateTrigger='onBlur'
      >
        <Select options={options} onChange={onTypeChange}></Select>
      </Form.Item>
      <Form.Item
        label={t('pleaseSelectADialogueRound')}
        name='value'
        rules={[{ required: true, message: t('conversationTurnCannotBeEmpty') }]}
        validateTrigger='onBlur'
      >
        <Slider style={{ width: '95%', marginTop: 0 }} // 设置固定宽度
          min={1}
          max={20}
          disabled={disabled}
          defaultValue={3}
          marks={defaultRecalls}
          step={1} // 设置步长为1
          onChange={(e) => onValueChange(e.toString())}
        />
      </Form.Item>
    </div >
  </>
};

export default MutiConversation;