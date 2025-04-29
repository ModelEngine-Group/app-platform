/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useCallback, useState, useRef, useImperativeHandle, useContext } from 'react';
import { Button, Alert, Spin } from 'antd';
import { useParams } from 'react-router-dom';
import { JadeFlow } from '@fit-elsa/elsa-react';
import { validate } from '../utils';
import AddKnowledge from '../../configForm/configUi/components/add-knowledge';
import HuggingFaceModal from './hugging-face-modal';
import FormSelectDrawer from '../../intelligent-form/components/form-select-drawer';
import ToolModal from './tool-modal';
import PromptWord from '../../configForm/configUi/components/prompt-word';
import PromptTemplate from '../../configForm/configUi/components/prompt-template';
import ConnectKnowledge from './connect-knowledge';
import { debounce, getCurrentTime } from '@/shared/utils/common';
import { updateFlowInfo } from '@/shared/http/aipp';
import { getAddFlowConfig, getEvaluateConfig } from '@/shared/http/appBuilder';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setAppInfo, setValidateInfo } from '@/store/appInfo/appInfo';
import { setTestStatus, setTestTime } from '@/store/flowTest/flowTest';
import { FlowContext } from '../../aippIndex/context';
import CreateTestSet from '../../appDetail/evaluate/testSet/createTestset/createTestSet';
import AddSearch from '../../configForm/configUi/components/add-search';
import { configMap } from '../config';
import { useTranslation } from 'react-i18next';
import i18n from '../../../locale/i18n';
import { cloneDeep } from 'lodash';

/**
 * elsa编排组件
 *
 * @return {JSX.Element}
 * @param appRef 编排组件引用
 * @param setDragData 设置左侧菜单基础节点
 * @param elsaRunningCtl elsa实例引用
 * @param showFlowChangeWarning elsa数据变化后全局提示
 * @param setShowFlowChangeWarning 设置是否展示elsa变化后全局提示弹窗
 * @param setSaveTime 设置自动保存时间方法
 * @constructor
 */
const Stage = (props) => {
  const { t } = useTranslation();
  const { 
    appRef, 
    setDragData, 
    showFlowChangeWarning, 
    setShowFlowChangeWarning, 
    elsaRunningCtl, 
    types, 
    setEvaluateData, 
    setSaveTime 
  } = props;
  const [showModal, setShowModal] = useState(false);
  const [showTools, setShowTools] = useState(false);
  const [showDrawer, setShowDrawer] = useState(false);
  const [spinning, setSpinning] = useState(false);
  const [isDrawper, setIsDrawper] = useState(false);
  const [taskName, setTaskName] = useState('');
  const [selectModal, setSelectModal] = useState('');
  const [modalTypes, setModalTypes] = useState('llmTool');
  const [skillList, setSkillList] = useState([]);
  const [promptValue, setPromptValue] = useState('');
  const [currentModelInfo, setCurrentModelInfo] = useState({});
  const [groupId, setGroupId] = useState("");
  const { CONFIGS } = configMap[process.env.NODE_ENV];
  const { type, appInfo, setFlowInfo } = useContext(FlowContext);
  const testStatus = useAppSelector((state) => state.flowTestStore.testStatus);
  const appValidateInfo = useAppSelector((state) => state.appStore.validateInfo);
  const choseNodeId = useAppSelector((state) => state.appStore.choseNodeId);
  const { tenantId, appId } = useParams();
  const testStatusRef = useRef<any>();
  const modelCallback = useRef<any>();
  const knowledgeCallback = useRef<any>();
  const pluginCallback = useRef<any>();
  const formCallback = useRef<any>();
  const currentApp = useRef<any>();
  const render = useRef<any>(false);
  const currentChange = useRef<any>(false);
  const modalRef = useRef<any>();
  const openModalRef = useRef<any>();
  const searchCallback = useRef<any>();
  const promptWordRef = useRef<any>();
  const promptTemplateRef = useRef<any>();
  const promptEvent = useRef<any>({});
  const connectKnowledgeRef = useRef<any>();
  const connectKnowledgeEvent = useRef<any>();
  const dispatch = useAppDispatch();
  useEffect(() => {
    if (appInfo.name && !render.current) {
      render.current = true;
      currentApp.current = JSON.parse(JSON.stringify(appInfo));
      window.agent = null;
      setElsaData();
    }
  }, [appInfo]);
  useEffect(() => {
    return () => {
      render.current = false;
      window.agent = null;
      dispatch(setTestStatus(null));
    }
  }, [])
  function getQueryString(name) {
    let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    let r = window.location.href.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
  }
  const realAppId = getQueryString('appId');
  // 编辑工作流
  function setElsaData() {
    let graphData = appInfo.flowGraph?.appearance || {};
    const stageDom = document.getElementById('stage');
    let data = JSON.parse(JSON.stringify(graphData));
    let configIndex = CONFIGS.findIndex(item => item.node === 'llmNodeState');
    CONFIGS[configIndex].params.tenantId = tenantId;
    CONFIGS[configIndex].params.appId = appId;
    setSpinning && setSpinning(true);
    const flow = types === 'evaluate'
      ? JadeFlow.evaluate(stageDom, tenantId, realAppId, data, false, CONFIGS, i18n)
      : JadeFlow.edit({
        div: stageDom,
        tenant: tenantId,
        appId: realAppId,
        flowConfigData: data,
        configs: CONFIGS,
        i18n,
        importStatements: [],
        flowType: appInfo.type === 'waterFlow' ? 'workflow' : appInfo.type
      });
    flow.then((agent) => {
      setSpinning && setSpinning(false);
      window.agent ? null : window.agent = agent;
      agent.onChange((dirtyAction) => {
        if (dirtyAction.action === 'jade_node_config_change') {
          handleFlowConfigChange();
        }
        currentChange.current = true;
        handleChange(CONFIGS[configIndex].params.appId);
      });
      // huggingface模态框
      agent.onModelSelect(({ taskName, selectedModel, onSelect }) => {
        setSelectModal(selectedModel);
        setTaskName(taskName.trim());
        modelCallback.current = onSelect;
        setShowModal(true);
      });
      // 知识库模态框
      agent.onKnowledgeBaseSelect((args) => {
        let { selectedKnowledgeBases, onSelect, groupId } = args;
        setGroupId(groupId);
        knowledgeCallback.current = onSelect;
        modalRef.current.showModal(selectedKnowledgeBases, groupId);
      });
      // 插件模态框
      agent.onPluginSelect((args) => {
        let { selectedPluginUniqueNames, onSelect } = args;
        setSkillList(selectedPluginUniqueNames);
        pluginCallback.current = onSelect;
        setShowTools(true);
      });
      // 评估测试集模态框
      agent.onCreateButtonClick(({ }) => {
        setIsDrawper(true);
      });
      // 参数搜索配置模态框
      agent.onKnowledgeSearchArgsSelect(({ callback, options, groupId }) => {
        setGroupId(groupId);
        searchCallback.current = callback;
        openModalRef.current.showOpenModal(options,groupId);
      });
      // 插件模态框
      agent.onImportButtonClick(({ onSelect }) => {
        pluginCallback.current = onSelect;
        setShowTools(true);
        setModalTypes('pluginButtonTool');
      });
      // 大模型技能模态框
      agent.onToolSelect(({ onSelect }) => {
        pluginCallback.current = onSelect;
        setShowTools(true);
        setModalTypes('llmTool');
      });
      // AI生成提示词弹框
      agent.listen('GENERATE_AI_PROMPT', (event) => {
        promptEvent.current = event;
        setCurrentModelInfo({
          templateType: event.type || 'user',
        });
        openGeneratePrompt();
      });
      // 自定义表单选择
      agent.onFormSelect(({ shapeId, selectedForm, onSelect }) => {
        setShowDrawer(true);
        formCallback.current = onSelect;
      });
      // 可用性检查高亮
      agent.validate(appValidateInfo, (val) => {
        dispatch(setValidateInfo(cloneDeep(val)));
      });
      if (choseNodeId) {
        agent.scrollToShape(choseNodeId);
      }
      // 连接知识库
      agent.listen('SELECT_KNOWLEDGE_BASE_GROUP', (event) => {
        connectKnowledgeEvent.current = event;
        connectKnowledgeRef.current.openModal();
        event.onSelect(groupId);
        setGroupId(event.selectedGroupId);
      });
      // 循环节点
      agent.onLoopSelect(({ onSelect }) => {
        pluginCallback.current = onSelect;
        setShowTools(true);
        setModalTypes('loop');
      });
    }).catch(() => {
      setSpinning && setSpinning(false);
    });
    getAddFlowConfig(tenantId, { pageNum: 1, pageSize: 20, tag: 'Builtin', version: '' }).then(res => {
      if (res.code === 0) {
        setDragData(res.data);
      }
    });
    getEvaluateConfig(tenantId, { pageNum: 1, pageSize: 20, tag: 'Builtin', version: '', type: 'evaluation' }).then(res => {
      if (res.code === 0) {
        setEvaluateData(res.data);
      }
    });
  }

  useEffect(() => {
    testStatusRef.current = testStatus;
  }, [testStatus])

  // 修改流程配置回调
  const handleFlowConfigChange = () => {
    if (testStatusRef.current) {
      localStorage.getItem('showFlowChangeWarning') !== 'false' && setShowFlowChangeWarning(true);
      elsaRunningCtl.current?.reset();
      dispatch(setTestStatus(null));
      dispatch(setTestTime(0));
    }
  }
  // hugging-face模型选中
  const onModelSelectCallBack = (model) => {
    modelCallback.current(model);
  }
  // 知识库选中
  const handleKnowledgeChange = (value) => {
    knowledgeCallback.current(value);
  }
  // 搜索参数配置选中
  const handleSearchChange = (value) => {
    searchCallback.current(value);
  };
  // 插件工具流选中
  const toolsConfirm = (item) => {
    let obj = {};
    let uniqueName = '';
    let loopObj = {};
    item.forEach((e) => {
      obj = e.schema;
      uniqueName = e.uniqueName;
      loopObj = e;
    });
    if (modalTypes === 'loop') {
      pluginCallback.current(loopObj);
    } else if (modalTypes === 'llmTool') {
      pluginCallback.current(uniqueName);
    } else {
      pluginCallback.current(obj);
    }
  }
  // 自定义表单选中
  const formConfirm = (item) => {
    formCallback.current(item);
    setShowDrawer(false);
  }
  // 数据实时保存
  const handleChange = useCallback(debounce((id) => elsaChange(id), 2000), []);
  function elsaChange(id: any) {
    let graphChangeData = window.agent.serialize();
    currentApp.current.flowGraph.appearance = graphChangeData;
    updateAppRunningFlow(id);
  }
  // 编辑更新应用
  async function updateAppRunningFlow(id = undefined) {
    currentChange.current = false;
    if (id && id !== appId) return;
    const res: any = await updateFlowInfo(tenantId, id ? id : appId, currentApp.current.flowGraph);
    if (res.code === 0) {
      if (!type) {
        setFlowInfo(currentApp.current);
      }
      setSaveTime(getCurrentTime());
    }
  }
  // 拖拽完成回调
  function handleDragEnter(e) {
    const nodeTab = e.dataTransfer.getData('itemTab');
    let nodeType = e.dataTransfer.getData('itemType');
    let nodeMetaData = JSON.parse(e.dataTransfer.getData('itemMetaData'));
    if (!validate(nodeType)) return;
    switch (nodeTab) {
      case 'basic':
        window.agent.createNode(nodeType, e, nodeMetaData);
        break;
      case 'tool':
        window.agent.createNode(nodeType, e, nodeMetaData);
        break;
      default:
        break;
    }
  }

  // 点击“不再提示”按钮回调
  const handleClickNoMoreTips = () => {
    localStorage.setItem('showFlowChangeWarning', 'false');
    setShowFlowChangeWarning(false);
  }

  // 点击关闭流程配置修改后告警回调
  const handleCloseFlowChangeWarningAlert = () => {
    setShowFlowChangeWarning(false);
  }
  // 给父组件的保存
  useImperativeHandle(appRef, () => {
    return {
      'elsaChange': elsaChange,
    }
  })

  // 打开生成提示词弹框
  const openGeneratePrompt = (isFormDrawer = false) => {
    promptWordRef.current.openPromptModal(isFormDrawer);
  };

  // 更新提示词
  const updatePromptValue = (val, updateToInput = true) => {
    setPromptValue(val);
    if (updateToInput) {
      promptEvent.current.applyPrompt(val);
    }
  };

  const handleUpdateChose = (e) => {
    if (e.detail.choseItem && agent) {
      agent.scrollToShape(e.detail.choseItem.nodeId);
    }
  };

  useEffect(() => {
    if (appValidateInfo.length && window.agent) {
      window.agent.validate(appValidateInfo, (val) => {
        dispatch(setValidateInfo(cloneDeep(val)));
      });
    }
  }, [appValidateInfo]);

  useEffect(() => {
    window.addEventListener("updateChoseNode", handleUpdateChose);
    return () => {
      window.removeEventListener("updateChoseNode", handleUpdateChose);
    }
  }, []);


  // 更新groupId
  const updateGroupId = (val) => {
    connectKnowledgeEvent.current.onSelect(val);
    setGroupId(val);
  };
  return <>
    <div
      className='content-right'
      onDragOver={(e) => e.preventDefault()}
      onDrop={handleDragEnter}>
      <Spin spinning={spinning}>
        <div className='elsa-canvas' id='stage'></div>
      </Spin>
    </div>
    {/* huggingFace选择弹窗 */}
    <HuggingFaceModal
      showModal={showModal}
      setShowModal={setShowModal}
      onModelSelectCallBack={onModelSelectCallBack}
      taskName={taskName}
      selectModal={selectModal}
    />
    {/* 添加知识库弹窗 */}
    <AddKnowledge
      modalRef={modalRef}
      tenantId={tenantId}
      groupId={groupId}
      handleDataChange={handleKnowledgeChange}
    />
    {/* 添加工具弹窗 */}
    <ToolModal
      showModal={showTools}
      setShowModal={setShowTools}
      toolsConfirm={toolsConfirm}
      checkData={skillList}
      modalType={modalTypes}
      type='addSkill'
    />
    {/* 创建测试集 */}
    <CreateTestSet
      visible={isDrawper}
      createCallback={() => setIsDrawper(false)}
      type='create'
      realAppId={realAppId}
    />
    {/* 表单选择 */}
    <FormSelectDrawer
      setShowDrawer={setShowDrawer}
      formConfirm={formConfirm}
      showDrawer={showDrawer}
    />
    {/* 参数搜索设置模态框 */}
    <AddSearch
      openModalRef={openModalRef}
      groupId={groupId}
      handleDataChange={handleSearchChange}
    />
    {/* 全局提示弹窗 */}
    {showFlowChangeWarning && <Alert
      className='flow-change-warning-content'
      message=''
      description={t('flowChangeWarningContent')}
      type='info'
      onClose={handleCloseFlowChangeWarningAlert}
      action={
        <Button size='small' type='link' onClick={handleClickNoMoreTips}>
          {t('noMoreTips')}
        </Button>
      }
      closable
    />}
    {/* 提示词 */}
    <PromptWord
      promptWordRef={promptWordRef}
      updatePromptValue={updatePromptValue}
      currentModelInfo={currentModelInfo}
    ></PromptWord>
    {/* 提示词 */}
    <PromptTemplate
      promptTemplateRef={promptTemplateRef}
      promptValue={promptValue}
      openGeneratePrompt={openGeneratePrompt}
      updatePromptValue={updatePromptValue}
    ></PromptTemplate>
    {/* 知识库 */}
    <ConnectKnowledge
      modelRef={connectKnowledgeRef}
      groupId={groupId}
      updateGroupId={updateGroupId}
    ></ConnectKnowledge>
  </>
};

export default Stage;
