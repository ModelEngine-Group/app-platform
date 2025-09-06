/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useImperativeHandle, useRef } from 'react';
import { Input, Modal, Tooltip, Button, Empty } from 'antd';
import { UndoOutlined, PlusOutlined } from '@ant-design/icons';
import { listData } from './common/mock';
import { setSpaClassName } from '@/shared/utils/common';
import { useTranslation } from 'react-i18next';
import './styles/flow-modal.scss';

const { Search } = Input;
const FlowModal = (props) => {
  const { t } = useTranslation();
  const { modalRef } = props;
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [searchList, setSearchList] = useState([]);
  const [selectedNodes, setSelectedNodes] = useState([]);
  const listRef = useRef<any>(null);
  useEffect(() => {
    setSearchList(listData);
  }, [props])

  const showModal = () => {
    setIsModalOpen(true);
  };
  const handleOk = () => {
    setIsModalOpen(false);
  };
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  // 搜索
  const onSearch = (value) => {
    let list = listData.filter(item => item.name.indexOf(value) !== -1);
    setSearchList(list)
  }
  // 工作流点击
  useEffect(() => {
    listRef.current = selectedNodes;
  }, [selectedNodes])
  function changeSelect(node) {
    if (listRef.current.includes(node.name)) {
      let list = listRef.current.filter(item => item !== node.name);
      setSelectedNodes(list);
    } else {
      setSelectedNodes([...listRef.current, node.name])
    };
  }
  // 刷新
  const refreshList = () => { }

  // 新建工作流
  const addFlow = () => { }

  return <>{(
    <Modal
      title={t('toolFlowConfiguration')}
      width='880px'
      maskClosable={false}
      centered
      open={isModalOpen}
      onOk={handleOk}
      onCancel={handleCancel}>
      <div className='search-box'>
        <Search
          placeholder={t('plsEnter')}
          allowClear={false}
          onSearch={onSearch}
          onChange={e => { onSearch(e.target.value) }} />
        <Button type='primary' shape='circle' size='small' icon={<UndoOutlined />} onClick={refreshList} className='add-btn' />
        <Button type='primary' size='small' icon={<PlusOutlined />} onClick={addFlow} className='add-btn'>
          <span className='add-text'>{t('greenfield')}</span>
        </Button>
      </div>
      <div className={setSpaClassName('search-list')}>
        {
          searchList.length ? searchList.map((item, index) => {
            return (
              <div
                className={['node', selectedNodes.includes(item.name) ? 'selected-node' : null].join(' ')}
                key={index}
                onClick={() => changeSelect(item)}
              >
                <p className='node-name'>{item.name}</p>
                <Tooltip title={item.description} destroyTooltipOnHide={true}>
                  <p className='node-description'>{item.description}</p>
                </Tooltip>
              </div>
            )
          }) : <Empty description={t('noData')} />
        }
      </div>
    </Modal>
  )}</>
};

export default FlowModal;
