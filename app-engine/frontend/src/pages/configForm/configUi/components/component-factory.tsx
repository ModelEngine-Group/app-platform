import React, { useEffect, useState, useRef } from 'react';
import EnterWorkflow from './enter-workflow';
import LLMContainer from './llm-container';
import ToolsContainer from './tools-container';
import KnowledgeContainer from './knowledge-container';
import OpeningContainer from './opening-container';
import MultiConversationContainer from './multiConversation-container';
import RecommendContainer from './recommend-container';
import InspirationContainer from './inspiration-container';
import { useAppSelector } from '@/store/hook';

const ComponentFactory = (props) => {
  const { configStructure, graphOperator, updateData, eventConfigs, categoryType } = props;
  const [validateList, setValidateList] = useState([]);
  const curValidateList = useRef([]);
  const readOnly = useAppSelector((state) => state.commonStore.isReadOnly);

  // 获取各项配置组件
  const createComponent = (config) => {
    const commonProps = {
      graphOperator,
      config,
      updateData,
      key: config.name,
      eventConfigs,
      validateList,
      categoryType,
      readOnly
    };
    switch (config.name) {
      case 'enterWorkflow':
        return <EnterWorkflow {...commonProps}></EnterWorkflow>;
      case 'model':
        return <LLMContainer {...commonProps}></LLMContainer>;
      case 'tools':
        return categoryType === 'chatbot' ? <></> : <ToolsContainer {...commonProps}></ToolsContainer>;
      case 'knowledge':
        return <KnowledgeContainer {...commonProps}></KnowledgeContainer>;
      case 'opening':
        return <OpeningContainer {...commonProps}></OpeningContainer>;
      case 'memory':
        return <MultiConversationContainer {...commonProps}></MultiConversationContainer>;
      case 'recommend':
        return <RecommendContainer {...commonProps}></RecommendContainer>;
      case 'inspiration':
        return <InspirationContainer {...commonProps}></InspirationContainer>;
      case 'ability':
      case 'chat':
        return <ComponentContainer config={config} createComponent={createComponent} />;
      default:
        return '';
    }
  };

  const handleUpdateChose = (e) => {
    if (e.detail.choseItem) {
      setValidateList([...curValidateList.current, e.detail.choseItem]);
    }
  };

  useEffect(() => {
    curValidateList.current = validateList;
  }, [validateList]);

  useEffect(() => {
    window.addEventListener("updateChoseNode", handleUpdateChose);
    return () => {
      window.removeEventListener("updateChoseNode", handleUpdateChose);
    }
  }, []);

  return <>
    {
      configStructure?.[0] && configStructure[0].children.map(config => {
        return <div key={config.name}>{createComponent(config)}</div>
      })
    }
  </>
};

export default ComponentFactory;

export const ComponentContainer = ({ config, createComponent }) => {
  return (<>
    <div className='config-name'>{config.description}</div>
    <div className='config-content'>
      {config.children.map((item, index) => <div key={index}>{createComponent(item)}</div>)}
    </div>
  </>);
};
