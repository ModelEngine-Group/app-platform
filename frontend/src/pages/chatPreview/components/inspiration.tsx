/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useRef, useState, useContext, useImperativeHandle } from 'react';
import { Input, Button, Popover, Tree, Empty, Dropdown, Modal } from 'antd';
import { useParams } from 'react-router-dom';
import { useLocation } from 'react-router';
import {
  SwapOutlined,
  SearchOutlined,
  PlusCircleOutlined
} from '@ant-design/icons';
import { ArrowDownIcon } from "@/assets/icon";
import { Icons } from '@/components/icons';
import { AippContext } from '@/pages/aippIndex/context';
import {
  getDepth,
  delNodeChild,
  filterArr,
  arrayToTree,
  getDeepNode,
  findMyCategoryId,
  findExpandId
} from '../utils/inspiration-utils';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { Message } from '@/shared/utils/message';
import { queryDepartMent, queryInspiration, deleteInspiration } from '@/shared/http/aipp';
import { TENANT_ID } from '../../chatPreview/components/send-editor/common/config';
import { isChatRunning } from '@/shared/utils/chat';
import { useTranslation } from 'react-i18next';
import Add from './inspiration/add-inspiration';
import '../styles/inspiration.scss';

/**
 * 应用聊天右侧灵感大全组件
 *
 * @return {JSX.Element}
 * @param inspirationClick 灵感大全item点击回调
 * @param setEditorSelect 灵感大全提示词存在变量时设置下拉
 * @param setEditorHtml 灵感大全点击设置html
 * @constructor
 */
const Inspiration = (props) => {
  const { t } = useTranslation();
  const { inspirationClick, setEditorSelect, setEditorHtml, reload } = props;
  const { messageChecked, reloadInspiration } = useContext(AippContext);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const appId = useAppSelector((state) => state.appStore.appId);
  const [showDrop, setShowDrop] = useState(false);
  const [open, setOpen] = useState(false);
  const [promptTypeList, setPromptTypeList] = useState([]);
  const [dropList, setDropList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [popoverOpen, setPopoverOpen] = useState(false);
  const [allPromptData, setAllPromptData] = useState([]);
  const [prompData, setPrompData] = useState([]);
  const [currentNodeId, setCurrentNodeId] = useState('');
  const [currentPromptType, setCurrentPromptType] = useState('root');
  const [currentPromptName, setCurrentPromptName] = useState('');
  const [searchValue, setSearchValue] = useState('');
  const [searchPromptValue, setSearchPromptValue] = useState('');
  const [showMorePromptTypes, setShowMorePromptTypes] = useState(false);
  const { tenantId, aippId } = useParams();
  const storageId = aippId ? aippId : appId;
  const dispatch = useAppDispatch();
  const location = useLocation();
  const treeNormalData = useRef();
  const deleteItem = useRef<any>();
  const tenantIdVal = useRef('');
  const typeRefresh = useRef(false);
  const appIdVal = useRef('');
  const myCategoryID = useRef('');
  const inspirationRef = useRef<any>(null);
  const treeChildData = useRef([]);
  const detailPage = location.pathname.indexOf('app-detail') !== -1;
  let regex = /{{(.*?)}}/g;


  useEffect(() => {
    appId && initInspiration();
  }, [appId, reloadInspiration]);

  // 初始化灵感大全
  const initInspiration = () => {
    if (appId) {
      tenantIdVal.current = tenantId;
      appIdVal.current = appId;
      setCurrentNodeId('');
      getList();
    }
  }
  // 刷新灵感大全
  const refreshInspiration = () => {
    typeRefresh.current = false;
    getList();
  }
  // 获取灵感大全列表
  async function getList() {
    const res:any = await queryDepartMent(TENANT_ID, appIdVal.current, detailPage);
    if (res.code === 0 && res.data?.length) {
      inspirationProcess(res.data[0]);
    }
  }
  // 灵感大全数据处理
  function inspirationProcess(data) {
    let childNodes = data.children;
    treeNormalData.current = JSON.parse(JSON.stringify(childNodes));
    if (childNodes.length) {
      let h = getDepth(childNodes);
      if (h === 1) {
        let arr = [{ title: t('all'), id: 'root' }];
        let arr1 = arr.concat(childNodes);
        arr1.push({ title: t('others'), id: 'others' });
        setPromptTypeList(arr1);
        setCurrentPromptType('root');
        getPromptList('root');
        setShowDrop(false);
        myCategoryID.current = findMyCategoryId(childNodes);
      } else {
        setShowDrop(true);
        multiInspirationProcess(childNodes);
      }
    } else {
      setPromptTypeList([]);
      setShowDrop(false);
      getPromptList('root');
    }
  }
  // 灵感大全多层处理逻辑
  const multiInspirationProcess = (childNodes) => {
    let id = findMyCategoryId(childNodes);
    myCategoryID.current = id;
    let arr = JSON.parse(JSON.stringify(childNodes));
    let list = delNodeChild(arr);
    list.forEach((item) => {
      delete item.childrenEmpty;
    });
    let setArr = filterArr(list);
    setArr.forEach((item) => delete item.children);
    setArr = setArr.filter((item) => item.childrenEmpty === undefined);
    setArr = arrayToTree(setArr);
    setArr.push({
      children: [],
      title: t('others'),
      id: 'others',
      parent: 'root:others',
    });
    setDropList(setArr);
    setDefaultSelect(setArr);
  }
  // 设置默认选中
  const setDefaultSelect = async (setArr) => {
    let obj;
    obj = getDeepNode(setArr, (node) => {
      return !node.children.length;
    });
    let parentId = obj.parent.split(':')[1];
    nodeClick(obj.id, obj.title, parentId);
  };
  // 根据节点获取灵感大全数据
  async function getPromptList(nodeId) {
    const res:any = await queryInspiration(TENANT_ID, appIdVal.current, nodeId, detailPage);
    if (res.code === 0 && res.data) {
      let categories = res.data.categories || [];
      setAllPromptData(res.data.inspirations);
      search(searchValue, res.data.inspirations);
    } else {
      setAllPromptData([]);
      search(searchValue, []);
    }
  }
  function onSearch(value) {
    setSearchValue(value.trim());
    search(value, allPromptData);
  }
  // 灵感过滤
  function search(e, allPromptData) {
    let arr = allPromptData;
    if (e.trim().length) {
      arr = allPromptData.filter((item) => item.name.indexOf(e) !== -1);
    }
    setPrompData(arr);
  }
  // 灵感大全分类点击
  function radioClick(item) {
    if (item.title === t('mine')) {
      myCategoryID.current = item.id;
    }
    setCurrentPromptType(item.id);
    getPromptList(item.id);
  }
  // 灵感大全点击
  function handleClickPrompt(item) {
    if (messageChecked) {
      Message({ type: 'warning', content: t('selectedWarning') });
      return;
    }
    if (isChatRunning()) {
      Message({ type: 'warning', content: t('tryLater') });
      return;
    }
    let result = [];
    let match;
    while ((match = regex.exec(item.prompt))) {
      result.push(match[1]);
    }
    if (item.name && item.auto) {
      if (result.length) {
        setEditorSelect(result, item, item.auto);
        return
      }
      inspirationClick(item?.prompt);
      return;
    }
    if (result.length) {
      setEditorSelect(result, item);
    } else {
      setEditorHtml(item.prompt || '');
    }
  }
  // 隐藏下拉框
  function hide() {
    setPopoverOpen(false);
  }
  // 分类点击回调
  async function nodeClick(id, name, parentId) {
    setCurrentPromptName(name);
    deepGetChild(treeNormalData.current, id);
    let arr = [{ title: t('all'), id: parentId }];
    let typeList = treeChildData.current.length
      ? arr.concat(treeChildData.current)
      : [];
    setCurrentPromptType(parentId);
    setCurrentNodeId(id);
    setPromptTypeList(parentId === 'others' ? [] : typeList);
    getPromptList(parentId);
  }
  // 递归获取点击节点
  function deepGetChild(list, id) {
    list.forEach((item) => {
      if (item.id === id) {
        treeChildData.current = item.children;
      } else if (item.children?.length) {
        deepGetChild(item.children, id);
      }
    });
  }
  // 搜索
  const searchChange = (e) => {
    onSearch(e.target.value);
  }
  function handleOpenChange(newOpen) {
    let hasRunning = chatList.filter(item => item.status === 'RUNNING')[0];
    if (hasRunning) {
      Message({ type: 'warning', content: t('tryLater') })
      return;
    }
    setPopoverOpen(newOpen);
  }
  // 点击灵感大全分类的回调
  const handlePromptTypeClick = (item) => {
    radioClick(item);
    showMorePromptTypes && setShowMorePromptTypes(false);
  }
  // 更多分类搜索框修改的回调
  const handleSearchPromptType = (e) => {
    setSearchPromptValue(e.target.value.trim());
  }
  // 点击展开更多灵感大全分类图标回调
  const handleClickMore = () => {
    setShowMorePromptTypes(!showMorePromptTypes);
    setSearchPromptValue('');
  }

  // 添加灵感大全
  const addInspiration = () => {
    inspirationRef.current?.initAdd({ str: '' }, 'add');
  }
  // 编辑灵感大全
  const clickItem = (info: any, item) => {
    if (info.key === 'edit') {
      inspirationRef.current?.initAdd(item, 'edit');
    } else {
      deleteItem.current = item;
      setOpen(true);
    }
  };
  // 删除
  const deleteApp = async () => {
    let { id, category } = deleteItem.current;
    let parentId = '';
    let categoryArr = category.split(':');
    parentId = categoryArr[categoryArr.length - 1] || '';
    const res:any = await deleteInspiration(tenantId, appId, parentId, id);
    if (res.code === 0) {
      setOpen(false);
      Message({ type: 'success', content: t('deleteSuccess') });
      refreshInspiration();
    }
  }
  // 编辑和删除按钮显示
  const showOperationBtn = (item) => {
    let { category } = item;
    if (detailPage || !myCategoryID.current) {
      return false
    } else if (category && category.indexOf(myCategoryID.current) !== -1) {
      return true;
    }
    return false;
  }
  // 点击选择
  const nodeCallBack = (id, name, parentId) => {
    typeRefresh.current = false;
    nodeClick(id, name, parentId)
  }
  useImperativeHandle(reload, () => {
    return {
      'initInspiration': refreshInspiration,
    }
  });
  useEffect(() => {
    return () => {
      typeRefresh.current = false;
    }
  }, [])
  return (
    <>
      {
        <div className='inspiration-conyainer'>
          <div className='right-content'>
            <div className={showDrop ? 'has-drop title' : 'title'}>
              <span className='title-icon'>
                <span className='inspiration-text'>
                  <span style={{ paddingRight: '12px' }}>{t('creativeInspiration')}</span>
                  {!detailPage && <PlusCircleOutlined onClick={addInspiration} />}
                </span>
              </span>
              {showDrop && (
                <Popover
                  content={
                    <DropMenu
                      treeList={dropList}
                      hide={hide}
                      nodeId={currentNodeId}
                      nodeClick={nodeCallBack} />
                  }
                  open={popoverOpen}
                  onOpenChange={handleOpenChange}
                  overlayClassName='inspiration-popover'
                  trigger='click'
                  placement='bottomRight'
                >
                  <Button size='small' style={{ display: 'flex', alignItems: 'center' }} icon={<SwapOutlined />} >
                    <span className='btn-text' title={currentPromptName}>{currentPromptName}</span>
                  </Button>
                </Popover>
              )}
            </div>
            <div className='prompt-search'>
              <Input
                prefix={<SearchOutlined />}
                allowClear
                maxLength={200}
                onChange={searchChange}
                placeholder={t('search')}
              />
            </div>
            <div className='prompt-container'>
              <div className='prompt-type'>
                {promptTypeList.slice(0, 4).map((item, index) => {
                  return (
                    <span
                      key={index}
                      title={item.title}
                      className={
                        currentPromptType === item.id
                          ? 'prompt-type-active prompt-type-item'
                          : 'prompt-type-item'
                      }
                      onClick={() => handlePromptTypeClick(item)}
                    >
                      <span className='text'> {item.title}</span>
                      <span className='line'></span>
                    </span>
                  );
                })}
                <span className='line'></span>
                <div
                  className="prompt-type-more-container"
                  onClick={(e) => e.stopPropagation()}
                >
                  { promptTypeList.length > 4 &&
                    <ArrowDownIcon
                      onClick={handleClickMore}
                      className={showMorePromptTypes ? 'prompt-type-more-svg' : ''}
                    />
                  }
                  { showMorePromptTypes && <div className="prompt-type-more">
                    <Input
                      className="prompt-type-more-search"
                      prefix={<SearchOutlined />}
                      allowClear
                      onChange={handleSearchPromptType}
                      placeholder={t('search')}
                      maxLength={200}
                    />
                    <div className="prompt-type-more-select-container">
                      {promptTypeList.slice(4)
                        .filter(item => item.title.indexOf(searchPromptValue) !== -1)
                        .map((item, index) => {
                        return (
                          <div
                            className={ currentPromptType === item.id ?
                              "prompt-type-more-select active" : "prompt-type-more-select" }
                            key={item.id}
                            title={item.title}
                            onClick={() => handlePromptTypeClick(item)}
                          >
                            {item.title}
                        </div>
                        )
                      })
                    }
                    </div>
                  </div> }
                  </div>
              </div>
              {prompData && prompData.length ? (
                <div className='prompt-list'>
                  {prompData.map((cItem, cIndex) => {
                    return (
                      <div
                        key={cIndex}
                        className='prompt-item'
                        onClick={() => handleClickPrompt(cItem)}
                      >
                        <div className='title'> {cItem.name}</div>
                        <div className='content text-mul-ellipsis' title={cItem.description}>
                          {cItem.description}
                        </div>
                        { showOperationBtn(cItem) && <div className='prompt-operator'>
                          <Dropdown
                            menu={{
                              items: [
                                {
                                  key: 'edit',
                                  label: <div>{t('edit')}</div>,
                                },
                                {
                                  key: 'delete',
                                  label: <div>{t('delete')}</div>,
                                },
                              ],
                              onClick: (info) => {
                                clickItem(info, cItem);
                                info.domEvent.stopPropagation();
                              },
                            }}
                            placement='bottomLeft'
                            trigger={['click']}
                          >
                            <div
                              style={{ cursor: 'pointer' }}
                              onClick={(e) => {
                                e.stopPropagation();
                              }}
                            >
                              <Icons.more width={20} />
                            </div>
                          </Dropdown>
                        </div>}

                      </div>
                    );
                  })}
                </div>
              ) : (
                  <div className='prompt-empty'>
                    <Empty description={t('noData')} />
                  </div>
                )}
            </div>
          </div>
          {!detailPage && <Add addRef={inspirationRef} refreshData={refreshInspiration} />}
          {/* 删除弹窗 */}
          <Modal
            title={t('Hints')}
            width='380px'
            open={open}
            centered
            onOk={() => deleteApp()}
            onCancel={() => setOpen(false)}
            okText={t('ok')}
            cancelText={t('cancel')}
          >
            <p>{t('deleteTip')}</p>
          </Modal>
        </div>
      }
    </>
  );
};

const DropMenu = (props) => {
  const { treeList, nodeClick, hide, nodeId } = props;
  const [expandKey, setExpandKey] = useState([]);
  function onSelect(k, v) {
    if (v.node.children.length) {
      return;
    }
    let currentId = v.node.id;
    let nodeName = v.node.title;
    let parentId = v.node.parent.split(':')[1];
    nodeId !== currentId && nodeClick(currentId, nodeName, parentId);
    hide();
  }
  const onExpand = (expandedKeys) => {
    setExpandKey(expandedKeys);
  };
  useEffect(() => {
    let parentId = findExpandId(treeList, nodeId);
    setExpandKey([parentId]);
  }, [])
  
  return (
    <>
      {
        <div className='drop-tree-menu'>
          <Tree
            expandedKeys={expandKey}
            treeData={treeList}
            selectedKeys={[nodeId]}
            onExpand={onExpand}
            onSelect={onSelect}
            showLine
            fieldNames={{ title: 'title', key: 'id', children: 'children' }}
          />
        </div>
      }
    </>
  );
};

export default Inspiration;
