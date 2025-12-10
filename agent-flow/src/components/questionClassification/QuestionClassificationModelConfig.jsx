/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import PropTypes from 'prop-types';
import React from 'react';
import {Collapse} from 'antd';
import {Model} from '@/components/llm/Model.jsx';
import {useShapeContext} from '@/components/DefaultRoot.jsx';
import {useTranslation} from 'react-i18next';
import {JadeCollapse} from '@/components/common/JadeCollapse.jsx';

const {Panel} = Collapse;

/**
 * 问题分类模型配置组件 - 只包含模型配置，不包含提示词模板
 *
 * @param disabled 是否禁用
 * @param modelOptions 模板选项
 * @param temperature 温度对象
 * @param serviceName 模型对象
 * @param tag 模型对象标签
 * @return {JSX.Element} 模型配置组件
 * @private
 */
const _QuestionClassificationModelConfig = ({
                                              disabled,
                                              modelOptions,
                                              temperature,
                                              serviceName,
                                              tag,
                                            }) => {
  const shape = useShapeContext();
  const shapeId = shape.id;
  const {t} = useTranslation();

  return (<>
    <JadeCollapse defaultActiveKey={[`questionClassificationModelPanel${shapeId}`]}>
      {<Panel
        key={`questionClassificationModelPanel${shapeId}`}
        header={<div className='panel-header'>
          <span className='jade-panel-header-font'>{t('llm')}</span>
        </div>}
        className='jade-panel'
      >
        <div className={'jade-custom-panel-content'}>
          <Model
            disabled={disabled} shapeId={shapeId} modelOptions={modelOptions} temperature={temperature}
            serviceName={serviceName} tag={tag}/>
        </div>
      </Panel>}
    </JadeCollapse>
  </>);
};

_QuestionClassificationModelConfig.propTypes = {
  disabled: PropTypes.bool,
  modelOptions: PropTypes.array.isRequired,
  temperature: PropTypes.object.isRequired,
  serviceName: PropTypes.object.isRequired,
  tag: PropTypes.object.isRequired,
};

const areEqual = (prevProps, nextProps) => {
  return prevProps.disabled === nextProps.disabled &&
    prevProps.modelOptions === nextProps.modelOptions &&
    prevProps.temperature === nextProps.temperature &&
    prevProps.serviceName === nextProps.serviceName &&
    prevProps.tag === nextProps.tag;
};

export const QuestionClassificationModelConfig = React.memo(_QuestionClassificationModelConfig, areEqual);
