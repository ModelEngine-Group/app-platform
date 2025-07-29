/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import { Input, Modal, Select, Button, Empty, Spin, Tabs, Divider } from 'antd';
import { Icons } from '@/components/icons';
import Pagination from '@/components/pagination';
import { useHistory } from 'react-router-dom';
import { handleClickAddToolNode } from '../utils';
import ToolTable from './tool-table';
import AddWaterFlow from './add-waterflow-drawer';
import { Message } from '@/shared/utils/message';
import { debounce } from '@/shared/utils/common';
import CreateWorkflow from './create-workflow';
import { getPlugins, getPluginTools, getPluginListByCategory } from '@/shared/http/plugin';
import { useAppSelector } from '@/store/hook';
import { PluginTypeE } from './model';
import { useTranslation } from 'react-i18next';
import { deepClone } from '../../chatPreview/utils/chat-process';
import {
  minePluginCategories,
  generalPluginCategories,
  chatbotPluginCategories,
} from '../../plugin/helper';
import Mine from '@/assets/images/pluginModal/mine.png';
import All from '@/assets/images/pluginModal/all.png';
import Tool from '@/assets/images/pluginModal/tool.png';
import Http from '@/assets/images/pluginModal/http.png';
import HuggingFace from '@/assets/images/pluginModal/hugging-face.png';
import LangChain from '@/assets/images/pluginModal/lang-chain.png';
import LlamaIndex from '@/assets/images/pluginModal/llama-index.png';
import Chatbot from '@/assets/images/pluginModal/chatbot.png';
import Agent from '@/assets/images/pluginModal/agent.png';
import Workflow from '@/assets/images/pluginModal/workflow.png';
import '../styles/tool-modal.scss';

const { Option } = Select;

/**
 * 插件通用弹窗
 * @param showModal 弹窗显示隐藏状态
 * @param setShowModal 弹窗显示隐藏方法
 * @param checkData elsa选中的数据
 * @param confirmCallBack 添加工作流回调
 * @param type 插件工具弹窗状态
 * @param modalType 工作流不同类型弹窗状态
 * @param toolsConfirm 工具确定
 */
const ToolDrawer = (props) => {
  const { t } = useTranslation();
  const { showModal, setShowModal, checkData, confirmCallBack, type, modalType, toolsConfirm } = props;
  const [activeKey, setActiveKey] = useState('');
  const [loading, setLoading] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [pluginData, setPluginData] = useState([]);
  const currentUser = localStorage.getItem('currentUser') || '';
  const [open, setOpen] = useState(false);
  const checkedList = useRef([]);
  const [createWorkflowSignal, setCreateWorkflowSignal] = useState(false);
  const searchName = useRef<any>(undefined);
  const listType = useRef('owner');
  const navigate = useHistory().push;
  const [selectedSourceTab, setSelectedSourceTab] = useState(minePluginCategories?.[0]?.key);
  const [pluginCategory, setPluginCategory] = useState('mine');

  useEffect(() => {
    if (selectedSourceTab === 'WATERFLOW') {
      getWaterFlowList();
      return;
    }
    if(chatbotPluginCategories.includes(selectedSourceTab)){
      getChatbotInfo(selectedSourceTab);
      return
    }
    showModal && getPluginList();
  }, [props.showModal, pageNum, pageSize, activeKey, selectedSourceTab]);
  useEffect(() => {
    type === 'addSkill' && (checkedList.current = JSON.parse(JSON.stringify(checkData)));
  }, [props.checkData]);
  const btnItems = [{ key: 'tool', label: t('plugin') }];
  const handleChange = (value: string) => {
    setPageNum(1);
    listType.current = value;
    getPluginList();
  };

  // 获取插件列表
  const getPluginList = async () => {
    let extrasParams = '';
    let params: any = {
      name: searchName?.current,
      pageNum: pageNum,
      pageSize,
      isDeployed: true,
    };
    if (selectedSourceTab === 'APP') {
      params.excludeTags = selectedSourceTab;
      extrasParams = 'excludeTags=BASIC';
    } else {
      params.includeTags = selectedSourceTab;
    }
    setLoading(true);
    getPlugins(params, extrasParams).then(({ data, total }) => {
        setTotal(total);
        setPluginData(data || []);
        setDefaultCheck(data);
        setLoading(false);
      })
      .catch(() => {
        setLoading(false);
      });
  };
  // 获取工作流列表
  const getWaterFlowList = () => {
    let params: any = {
      pageNum,
      pageSize,
      includeTags: selectedSourceTab,
      isBuiltin: true,
      name: searchName.current,
      creator: currentUser,
    };
    getPluginTools(params)
      .then((res: any) => {
        const { data } = res;
        const list = data || [];
        list.forEach((item: any) => {
          item.mapType = 'waterFlow';
          item.toolCount = 0;
        });
        setTotal(res.total || 0);
        setPluginData(list);
      })
      .catch(() => {
        setTotal(0);
        setPluginData([]);
      });
  };
  // 分页
  const selectPage = (curPage: number, curPageSize: number) => {
    setPageNum(curPage);
    setPageSize(curPageSize);
  };
  // 名称搜索
  const filterByName = (value: string) => {
    searchName.current = value.trim();
    setPageNum(1);
    getPluginList();
  };
  const handleSearch = debounce(filterByName, 1000);
  // 添加插件
  const toolAdd = () => {
    checkedList.current.forEach((item, index) => {
      const type = item.type || 'toolInvokeNodeState';
      handleClickAddToolNode(type, { clientX: 400 + 10 * index, clientY: 300 + 10 * index }, item);
    });
    Message({ type: 'success', content: t('operationSucceeded') });
  };

  // 添加工作流
  const workflowAdd = () => {
    const workFlowList: any = [];
    const fitList: any = [];
    checkedList.current.forEach((item) => {
      if (item.tags.includes('WATERFLOW')) {
        workFlowList.push({ ...item, type: 'waterflow' });
      } else {
        fitList.push({ ...item, type: 'tool' });
      }
    });
    const workFlowId = workFlowList.map((item) => item.uniqueName);
    const fitId = fitList.map((item) => item.uniqueName);
    if (type === 'addSkill') {
      confirmCallBack(workFlowList, fitList);
    } else {
      confirmCallBack(workFlowId, fitId);
    }
  };

  // 设置默认选中
  const setDefaultCheck = (data) => {
    let nameList = checkedList.current.map((item) => item.uniqueName);
    data.forEach((item) => {
      item.checked = nameList.includes(item.uniqueName);
    });
  };

  // 创建跳转到插件
  const onClickCreate = () => {
    sessionStorage.setItem('pluginType', 'plugin');
    navigate(`/plugin`);
  };

  // 获取智能体数据
  const getChatbotInfo = async (item: any) => {
    try {
      const param = {
        pageNum,
        pageSize,
        type: item,
      };
      const res: any = await getPluginListByCategory(param);
      if (res.code === 0) {
        setTotal(res.total);
        setPluginData(res.data || []);
      }
    } catch (error) {}
  };

  // 点击我的
  const onClickMine = ()=>{
    setPluginCategory('mine');
    setSelectedSourceTab(minePluginCategories[0].key);
    getPluginList();
  }

  // 点击切换左侧菜单
  const onClickPluginSource = async (item: any) => {
    setPluginCategory(item.label);
    if (generalPluginCategories.includes(item.key)) {
      setSelectedSourceTab(item.key);
    } else {
      getChatbotInfo(item.key);
    }
  };

  // 根据工具市场名字获取对应图标
  const getIconByName = (imgName: string) => {
    switch (imgName) {
      case 'APP':
        return All;
      case 'TOOL':
        return Tool;
      case 'HTTP':
        return Http;
      case 'HUGGINGFACE':
        return HuggingFace;
      case 'LLAMAINDEX':
        return LlamaIndex;
      case 'LANGCHAIN':
        return LangChain;
      case 'CHATBOT':
        return Chatbot;
      case 'AGENT':
        return Agent;
      case 'WORKFLOW':
        return Workflow;
      default:
        break;
    }
  };

  // 关闭弹窗
  const closeModal = () => {
    setShowModal(false);
    searchName.current = '';
    setPluginCategory('mine');
    setSelectedSourceTab(minePluginCategories[0].key);
  };

  return (
    <>
      <Modal
        title={t('morePlugins')}
        open={showModal}
        onCancel={closeModal}
        destroyOnClose={true}
        width='1100px'
        footer={<div className='drawer-footer'></div>}
        className='tool-modal'
      >
        <div className='plugin-box'>
          <div className='left-menu'>
            <Input
              maxLength={200}
              placeholder={t('plsEnter')}
              onChange={(e) => handleSearch(e.target.value)}
              prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
              className='search-input'
            />
            <Button type='primary' className='search-button' onClick={onClickCreate}>
              {t('create')}
            </Button>
            <div
              className={`mine-icon ${pluginCategory === 'mine' ? 'menu-tags' : ''}`}
              onClick={onClickMine}
            >
              <img src={Mine} />
              <span className='icon-size'>{t('mine')}</span>
            </div>
            <Divider />
            <div>
              <div className='tool-market'>{t('toolMarket')}</div>
              {(minePluginCategories as any).map((item: any) => {
                return (
                  <div
                    key={item.label}
                    onClick={() => onClickPluginSource(item)}
                    className={`tool-market-tabs ${pluginCategory === item.label ? 'menu-tags' : ''}`}
                  >
                    <img src={getIconByName(item.key)} />
                    <span className='tool-icon'>{item.label}</span>
                  </div>
                );
              })}
            </div>
          </div>
          <div className='right-table'>
            <div>
              {pluginCategory === 'mine' && (
                <Tabs
                  items={minePluginCategories}
                  activeKey={selectedSourceTab}
                  onChange={(key: string) => setSelectedSourceTab(key)}
                  style={{ width: '100%', textAlign: 'center' }}
                  centered={true}
                />
              )}
              <Spin spinning={loading}>
                {pluginData?.length > 0 && (
                  <div className='mashup-add-inner'>
                    {pluginData.map((card: any) => {
                      return (
                        <div
                          className='mashup-add-item'
                          style={{ width: '100%' }}
                          key={card.pluginId || card.uniqueName}
                        >
                          <ToolTable
                            pluginData={card}
                            toolsConfirm={toolsConfirm}
                            type={type}
                            toolAdd={toolAdd}
                            workflowAdd={workflowAdd}
                            checkedList={checkedList}
                            modalType={modalType}
                            setShowModal={setShowModal}
                            checkData={checkData}
                            searchName={searchName}
                          />
                        </div>
                      );
                    })}
                  </div>
                )}
                {!pluginData.length && (
                  <div className='tool-empty'>
                    <Empty description={t('noData')} />
                  </div>
                )}
              </Spin>
            </div>
            <div style={{ paddingTop: 16 }}>
              <Pagination
                total={total}
                current={pageNum}
                onChange={selectPage}
                pageSize={pageSize}
              />
            </div>
          </div>
        </div>
        <CreateWorkflow createWorkflowSignal={createWorkflowSignal} />
      </Modal>
      <AddWaterFlow open={open} setOpen={setOpen} />
    </>
  );
};

export default ToolDrawer;
