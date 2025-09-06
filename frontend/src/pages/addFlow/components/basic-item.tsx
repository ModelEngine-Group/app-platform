/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import {
  StartIcon,
  DataRetrievalIcon,
  EndIcon,
  ManualCheckIcon,
  LlmIcon,
  IfIcon,
  FitIcon,
  CodeIcon,
  ClassificationIcon,
  QueryOptimizationIcon,
  KnowledgeRetrievalIcon,
  TextExtractionIcon,
  HttpIcon,
  VariableAggregation,
  TextToImageIcon,
  FileExtractionIcon,
  LoopIcon,
  PairingIcon,
} from '@/assets/icon';
import { handleClickAddBasicNode, handleDragBasicNode } from '../utils'

/**
 * 应用编排左侧基础节点列表
 *
 * @return {JSX.Element}
 * @param dragData 左侧拖拽菜单列表
 * @constructor
 */
const BasicItems = (props: any) => {
  const { dragData } = props;

  // 根据类型设置图标
  const getIconByType = (type: string | number) => {
    return {
      'startNodeStart': <StartIcon />,
      'retrievalNodeState': <DataRetrievalIcon />,
      'llmNodeState': <LlmIcon />,
      'endNodeEnd': <EndIcon />,
      'intelligentFormNodeState': <ManualCheckIcon />,
      'fitInvokeNodeState': <FitIcon />,
      'conditionNodeCondition': <IfIcon />,
      'toolInvokeNodeState': <FitIcon />,
      'codeNodeState': <CodeIcon />,
      'queryOptimizationNodeState': <QueryOptimizationIcon />,
      'knowledgeRetrievalNodeState': <KnowledgeRetrievalIcon />,
      'textExtractionNodeState': <TextExtractionIcon />,
      'questionClassificationNodeCondition': <ClassificationIcon />,
      'httpNodeState': <HttpIcon />,
      'variableAggregationNodeState': <VariableAggregation />,
      'textToImageNodeState': <TextToImageIcon />,
      'fileExtractionNodeState': <FileExtractionIcon />,
      'noteNode': <ClassificationIcon />,
      'loopNodeState': <LoopIcon />,
      'manualCheckNodeState': <ManualCheckIcon />,
      'parallelNodeState': <PairingIcon />
    }[type];
  }
  return <>
    <div className='basic-drag-list'>
      {dragData.map((item, index) => {
        return (
          <div
            className='drag-item'
            onDragStart={(e) => handleDragBasicNode(item, e)}
            draggable={true}
            key={index}
          >
            <div className='drag-item-title'>
              <div>
                {getIconByType(item.type)}
                <span className='content-node-name'>{item.name}</span>
              </div>
              <span className='drag-item-icon' onClick={(event) => handleClickAddBasicNode(item, event)}>
              </span>
            </div>
          </div>
        )
      })
      }
    </div>
  </>
};


export default BasicItems;
