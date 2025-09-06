
import React from 'react';
import { StartIcon, DataRetrievalIcon, EndIcon, LlmIcon } from '@/assets/icon';
import { handleClickAddBasicNode, handleDragBasicNode } from '../utils';

const BasicItems = (props: any) => {
  const { dragData } = props;

  // 根据类型设置图标
  const getIconByType = (type: string | number) => {
    return {
      'evaluationStartNodeStart': <StartIcon />,
      'evaluationAlgorithmsNodeState': <DataRetrievalIcon />,
      'evaluationTestSetNodeState': <LlmIcon />,
      'evaluationEndNodeEnd': <EndIcon />,
    }[type];
  }
  return (
    <>
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
                <span
                  className='drag-item-icon'
                  onClick={(event) => handleClickAddBasicNode(item, event)}
                >
                </span>
              </div>
            </div>
          );
        })}
      </div>
    </>
  );
};


export default BasicItems;
