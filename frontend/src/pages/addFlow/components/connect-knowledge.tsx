/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useImperativeHandle, useEffect } from 'react';
import { Modal, Typography } from 'antd';
import { useTranslation } from 'react-i18next';
import { useHistory } from 'react-router-dom';
import { useAppDispatch } from '@/store/hook';
import { setIsAutoOpen } from '@/store/common/common';
import { getConnectKnowledgeList, getKnowledgeConfigId } from '@/shared/http/knowledge';
import BookIcon from '@/assets/images/ai/connect-knowledge.png';
import NotConnectIcon from '@/assets/images/ai/not-connect.png';
import ConnectedIcon from '@/assets/images/ai/connected.svg';
import '../styles/connect-knowledge.scss'

/**
 * 连接知识库弹框
 *
 * @param modelRef 当前组件ref.
 * @param groupId 父组件组件groupId.
 * @param updateKnowledgeOption 更新父组件groupId的方法.
 * @return {JSX.Element}
 * @constructor
 */

const ConnectKnowledge = ({ modelRef, groupId, updateKnowledgeOption }) => {
  const { t } = useTranslation();
  const dispatch = useAppDispatch();
  const navigate = useHistory().push;
  const [open, setOpen] = useState(false);
  const [connectList, setConnectList] = useState([]);
  const [chosenId, setChosenId] = useState(groupId);
  const [knowledgeConfigId, setKnowledgeConfigId] = useState('');

  useImperativeHandle(modelRef, () => {
    return { openModal: () => setOpen(true) };
  });

  // 确定
  const confirm = () => {
    setOpen(false);
    updateKnowledgeOption(chosenId, knowledgeConfigId);
  };

  // 获取知识库列表
  const getConnectList = async () => {
    try {
      const res = await getConnectKnowledgeList();
      if (res.code === 0 && res.data) {
        setConnectList(res.data);
      }
    } catch (error) { }
  };

  // 跳转到上传插件页面
  const goPluginPage = () => {
    dispatch(setIsAutoOpen(true));
    navigate(`/plugin`);
  };

  // 选择知识库集回调
  const onClickKnowledgeSet = async (groupId: String) => {
    setChosenId(groupId);
    const res = await getKnowledgeConfigId(groupId);
    if (res.code === 0) {
      setKnowledgeConfigId(res.data);
    }
  }

  useEffect(() => {
    if (open) {
      getConnectList();
      setChosenId(groupId);
    }
  }, [open]);

  useEffect(() => {
    dispatch(setIsAutoOpen(false));
  }, []);

  return <>
    <Modal
      title={t('connect') + t('knowledgeBase')}
      open={open}
      width={550}
      className='connect-knowledge'
      onOk={confirm}
      onCancel={() => setOpen(false)}
    >
      <div className='connect-tip'>
        <div>{t('connectTip')}</div>
        <Typography.Link onClick={goPluginPage}>{t('toConnect')}</Typography.Link>
      </div>
      <div className='connect-list'>
        {
          connectList.map(item =>
            <div className={`knowledge-item ${chosenId === item.groupId ? 'chose' : ''}`}
                 key={item.groupId}
                 onClick={() => onClickKnowledgeSet(item.groupId)}
            >
              <div className='knowledge-title'>
                <img src={BookIcon} alt="" style={{ marginRight: 8 }} />
                {item.name}
              </div>
              <div className='knowledge-desc' title={item.description}>{item.description}</div>
              <div className='connect-circle'>
                <img src={chosenId === item.groupId ? ConnectedIcon : NotConnectIcon} alt="" />
              </div>
            </div>
          )
        }
      </div>
    </Modal>
  </>
};

export default ConnectKnowledge;
