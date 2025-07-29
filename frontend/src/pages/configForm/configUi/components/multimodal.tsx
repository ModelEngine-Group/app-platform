/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { Form, Select } from 'antd';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import { multiModal } from '../../common/common';

const Multimodal = () => {
  const { t } = useTranslation();
  const [showMultiControl, setShowMultiControl] = useState(true);
  const onArrowClick = (value, func) => {
    func(!value);
  }
  return (
    <>
      <div className='control'>
        <div className='control-header'>
          <div className='control-title'>
            {
              showMultiControl ? <DownOutlined onClick={() => onArrowClick(showMultiControl, setShowMultiControl)} />
                : <UpOutlined onClick={() => onArrowClick(showMultiControl, setShowMultiControl)} />
            }
            <div style={{ marginLeft: '10px' }}>{t('multimodal')}</div>
          </div>
        </div>
        <Form.Item
          name='multimodal'
          label=''
          style={{
            marginTop: '10px',
            display: showMultiControl ? 'block' : 'none',
          }}
        >
          <Select
            mode='multiple'
            allowClear
            placeholder={t('selectMultimodal')}
            defaultValue={['file', 'image', 'radio', 'video']}
            options={multiModal}
          ></Select>
        </Form.Item>
      </div>
    </>
  )
};

export default Multimodal;
