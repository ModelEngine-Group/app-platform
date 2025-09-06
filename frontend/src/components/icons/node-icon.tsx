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
  TextExtractionIcon
} from '@/assets/icon';

const NodeIcon = ({ type }) => {
  // 根据类型设置图标
  const getIconByType = (type) => {
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
      'manualCheckNodeState': <ManualCheckIcon />,
    }[type];
  }

  return <>{
    getIconByType(type)
  }
  </>

};

export default NodeIcon;