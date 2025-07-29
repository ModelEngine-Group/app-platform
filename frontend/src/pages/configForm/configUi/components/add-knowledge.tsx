/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useImperativeHandle, useState, useRef, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import { Button, Input, Dropdown, Modal, Checkbox, Tooltip } from 'antd';
import { SearchOutlined, DownOutlined } from '@ant-design/icons';
import { setSpaClassName } from '@/shared/utils/common';
import { getKnowledgesCard } from '@/shared/http/knowledge';
import { Message } from '@/shared/utils/message';
import { deepClone } from '@/pages/chatPreview/utils/chat-process';
import { useTranslation } from 'react-i18next';
import Empty from '@/components/empty/empty-item';
import Pagination from '@/components/pagination';
import '../styles/add-knowledge.scss';
const { Search } = Input;

/**
 * 添加知识库模态框
 * @param modalRef 控制模态框显示隐藏
 * @param tenantId 租户ID
 * @param groupId elsa连接知识库的groupId
 * @param handleDataChange 存储数据方法，并传入elsa中
 */

const AddKnowledge = (props) => {
  const { t } = useTranslation();
  const { modalRef, tenantId, groupId, handleDataChange } = props;
  const [open, setOpen] = useState(false);
  const [listPage, setListPage] = useState(1);
  const [pageSize, setPageSize] = useState(12);
  const [total, setTotal] = useState(0);
  const [knowledgeList, setKnowledgeList] = useState([]);
  const [cachedKnowledgeList, setCachedKnowledgeList] = useState<any>([]);
  const searchName = useRef('');
  const checkData = useRef<any>([]);
  const typeMap: any = {
    VECTOR: t('unstructuredData'),
    RDB: t('structuredData'),
    KV: t('structuredData'),
  };
  const btnItems = [{ key: 'knowledge', label: t('knowledgeBases') }];
  const cancel = () => {
    setOpen(false);
  };
  const showModal = (list = [], groupId: String, knowledgeConfigId: String) => {
    setTotal(0);
    checkData.current = deepClone(list);
    setOpen(true);
    setCheck(groupId, knowledgeConfigId);
  };
  // 设置选中列表
  const setCheck = (groupId: String, knowledgeConfigId: String) => {
    setListPage(1);
    setPageSize(12);
    setKnowledgeList(checkData.current);
    handleGetKnowledgeOptions(knowledgeConfigId, groupId);
  };

  // 过滤Id
  const uniqueifyKnowledgeList = (arr: any) => {
    return Array.from(new Map(arr.map((item: { id: any }) => [item.id, item])).values());
  };

  // 获取知识库列表
  const handleGetKnowledgeOptions = async (knowledgeConfigId: String, graphGroupId?:String) => {
    const params = {
      tenantId,
      pageIndex: listPage,
      pageSize: pageSize,
      repoName: encodeURIComponent(searchName.current.trim()),
      groupId: graphGroupId || groupId,
      knowledgeConfigId
    };
    try {
      const res: any = await getKnowledgesCard(params);
      if (res.code === 0) {
        res.data?.items?.forEach((item: any) => {
          checkData.current?.forEach((checkedItem: any) => {
            if (checkedItem.id === item.id) {
              item.checked = true;
            }
          });
        });
        setKnowledgeList(res?.data?.items);
        const cachedListId = uniqueifyKnowledgeList([...cachedKnowledgeList, ...res?.data?.items]);
        setCachedKnowledgeList(cachedListId);
        setTotal(res.data?.total);
      }
    } catch (error) {}
  };
  // 创建知识库
  const createClick = () => {
    window.parent.location.href = `${window.parent.location.origin}/edatamate/model-knowledge/create`;
  };

  // 过滤相同的对象
  const filterSameKnowledges = (paramData: any, elsaData: any) => {
    const map = new Map();
    paramData.forEach((obj: any) => {
      map.set(obj.id, obj);
    });
    const sameInfo = elsaData.filter((item: any) => map.has(item.id));
    return sameInfo;
  };

  // 确定提交
  const confirm = () => {
    if (checkData.current.length === 0) {
      Message({ type: 'warning', content: t('plsSelectKnowledge') });
      return;
    }
    handleDataChange(filterSameKnowledges(cachedKnowledgeList, checkData.current));
    setOpen(false);
  };
  // 搜索
  const onSearch = (value: any) => {
    searchName.current = value.trim();
    setListPage(1);
    handleGetKnowledgeOptions();
  };
  // 分页
  const selectPage = (curPage: number, curPageSize: number) => {
    setListPage(curPage);
    setPageSize(curPageSize);
  };
  // 卡片选中
  const onchangeKnowledge = (val: any, item: any) => {
    let list = deepClone(knowledgeList);
    if (checkData.current.length >= 5 && val.target.checked) {
      Message({ type: 'warning', content: t('selectRepositoryTips') });
      return;
    }
    let cItem = list.filter((pItem: any) => pItem.id === item.id)[0];
    cItem.checked = val.target.checked;
    if (val.target.checked) {
      checkData.current.push(item);
    } else {
      checkData.current = checkData.current.filter((cItem: any) => cItem.id !== item.id);
    }
    setKnowledgeList(list);
  };

  useEffect(() => {
    open && handleGetKnowledgeOptions();
  }, [listPage, pageSize]);

  useImperativeHandle(modalRef, () => {
    return {
      showModal: showModal,
    };
  });

  return (
    <>
      <Modal
        title={t('selectRepository')}
        width='1100px'
        closeIcon={false}
        onCancel={cancel}
        open={open}
        footer={
          <div className='drawer-footer'>
            <Button onClick={cancel}>{t('cancel')}</Button>
            <Button type='primary' onClick={confirm}>
              {t('ok')}
            </Button>
          </div>
        }
      >
        <div className='mashup-add-drawer'>
          <div className='knowledge-search'>
            <Search
              prefix={<SearchOutlined />}
              allowClear
              placeholder={t('search')}
              onSearch={onSearch}
            />
            { process.env.PACKAGE_MODE !== 'common'&& 
              <Dropdown menu={{ items: btnItems, onClick: createClick }} trigger={['click']}>
                <Button type='primary' icon={<DownOutlined />}>
                  {t('create')}
                </Button>
              </Dropdown> 
            }
          </div>
        </div>
        {knowledgeList.length > 0 ? (
          <div className={setSpaClassName('add-knowledge-card')}>
            {knowledgeList.map((item: any) => {
              return (
                <div key={item.id} className='select-card'>
                  <div className='title-box title-box-bottom'>
                    <Tooltip title={item.name} placement='topLeft'>
                      <div className='konwledge-name card-ellipsis'>{item.name}</div>
                    </Tooltip>
                    <Tooltip title={typeMap[item?.type]} placement='topLeft'>
                      <div className='knowledge-size card-data-ellipsis'>{typeMap[item?.type]}</div>
                    </Tooltip>
                  </div>
                  <div className='knowledge-size knowledge-size-bottom'>
                    {t('createdAt')} {item.createdAt}
                  </div>
                  <Tooltip title={item.description} placement='topLeft'>
                    <div className='knowledge-size knowledge-size-bottom card-description'>
                      {item.description}
                    </div>
                  </Tooltip>
                  <div className='flex-end'>
                    <Checkbox checked={item.checked} onChange={(e) => onchangeKnowledge(e, item)} />
                  </div>
                </div>
              );
            })}
          </div>
        ) : (
          <div className='pagination-footer'>
            <Empty />
          </div>
        )}
        <div className='pagination-footer'>
          <Pagination
            total={total}
            current={listPage}
            onChange={selectPage}
            pageSize={pageSize}
            pageSizeOptions={[12, 24, 36, 48]}
          />
        </div>
      </Modal>
    </>
  );
};

export default AddKnowledge;
