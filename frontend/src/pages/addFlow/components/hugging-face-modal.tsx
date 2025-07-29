/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import { Modal, Input, Button } from 'antd';
import { useParams } from 'react-router-dom';
import { getHuggingFaceList } from '@/shared/http/appBuilder';
import { useTranslation } from 'react-i18next';
import { setSpaClassName } from '@/shared/utils/common';
import EmptyItem from '@/components/empty/empty-item';
import huggingFacImg from '@/assets/images/ai/hugging-face.png';
import downloadImg from '@/assets/images/ai/download.png';
import likeImg from '@/assets/images/ai/like.png';
const { Search } = Input;

const HuggingFaceModal = (props) => {
  const { t } = useTranslation();
  const { showModal, setShowModal, onModelSelectCallBack, taskName, selectModal } = props;
  const [name, setName] = useState('');
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(100);
  const [list, setList] = useState([]);
  const [activeKey, setActiveKey] = useState('');
  const { tenantId } = useParams();
  const listRef = useRef([]);
  useEffect(() => {
    showModal && getPluginList();
  }, [props.showModal, name, pageNum, pageSize]);
  // 获取插件列表
  const getPluginList = () => {
    getHuggingFaceList(tenantId, { pageNum, pageSize, taskName }).then(res => {
      if (res.code === 0) {
        setActiveKey(selectModal);
        listRef.current = JSON.parse(JSON.stringify(res.data.modelDatas));
        setList(listRef.current);
      }
    });
  };
  // 名称搜索
  const filterByName = (value: string) => {
    if (!value.trim().length) {
      setList(listRef.current);
    } else {
      let arr = listRef.current.filter(item => item.name.indexOf(value.trim()) !== -1);
      setList(arr);
    }
  }
  const itemClick = (item) => {
    setActiveKey(item.name);
  }
  const confirm = () => {
    onModelSelectCallBack({ name: activeKey });
    setShowModal(false);
  }
  // 分页
  const selectPage = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
    if (pageSize !== curPageSize) {
      setPageSize(curPageSize);
    }
  }
  return <>
    <Modal
      title={t('plsChooseHFModel')}
      open={showModal}
      onCancel={() => setShowModal(false)}
      width='1100px'
      footer={
        <div className='drawer-footer'>
          <Button onClick={() => setShowModal(false)}>{t('cancel')}</Button>
          <Button type='primary' onClick={confirm}>{t('ok')}</Button>
        </div>
      }
    >
      <div className='tool-modal-search'>
        <Search size='large' onSearch={filterByName} placeholder={t('plsEnter')} allowClear />
      </div>
      <div className={setSpaClassName('tool-modal-content')}>
        <div className='content-left'>
          <div className='left-list'>
            {list.length > 0 && list.map((card: any) =>
              <div className={`left-item ${activeKey === card.name ? 'active' : ''}`}
                key={card.taskName}
                onClick={() => itemClick(card)}>
                <div className='card-detail' onClick={() => window.open(`https://${card.url}`)}>{t('checkMore')}</div>
                <div className='item-top'>
                  <div className='top-left'>
                    <img src={huggingFacImg} alt='' />
                  </div>
                  <div className='top-right'>
                    <div className='item-title' title={card.name}>{card.name} </div>
                    <div className='item-tag'>
                      <span>
                        <img src={downloadImg} alt='' />
                        {card.context.downloads}
                      </span>
                      <span>
                        <img src={likeImg} alt='' />
                        {card.context.likes}
                      </span>
                    </div>
                  </div>
                </div>
                <div className='item-bottom' title={card.context.description}>{card.context.description}</div>
              </div>
            )}
            {list.length === 0 && <div className='tool-empty'><EmptyItem text={t('noData')} /></div>}
          </div>
        </div>
      </div>
    </Modal>
  </>
};

export default HuggingFaceModal;
