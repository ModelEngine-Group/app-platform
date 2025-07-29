/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef, useImperativeHandle  } from 'react';
import { useParams } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setValidateInfo } from '@/store/appInfo/appInfo';
import AddKnowledge from './add-knowledge';
import { isEmpty } from 'lodash';
import { useTranslation } from 'react-i18next';
import closeImg from '@/assets/images/close_btn.svg';

const Knowledge = (props) => {
  const { t } = useTranslation();
  const { knowledge, groupId, updateData, knowledgeRef, knowledgeConfigId, readOnly } = props;
  const [knows, setKnows] = useState([]);
  const [showOperateIndex, setShowOperateIndex] = useState(-1);
  const { tenantId } = useParams();
  const list = useRef([]);
  const modalRef = useRef();
  const dispatch = useAppDispatch();
  const appValidateInfo = useAppSelector((state) => state.appStore.validateInfo);
  useImperativeHandle(knowledgeRef, () => {
    return { addKnowledge };
  });

  const handleChange = (value) => {
    list.current = value;
    setKnows(value)
    updateData(value);
  };

  // 删除
  const deleteItem = (item) => {
    list.current = list.current.filter(Litem => Litem.id !== item.id);
    setKnows([...list.current]);
    setShowOperateIndex(-1);
    dispatch(setValidateInfo(appValidateInfo.filter(it => it.configName !== 'knowledgeRepos' || (it.configName === 'knowledgeRepos' && it.id != item.id))));
    updateData(list.current);
  };

  const addKnowledge = () => {
    modalRef.current.showModal(knows, groupId, knowledgeConfigId);
  };

  // hover显示操作按钮
  const handleHoverItem = (index, operate) => {
    if (operate === 'enter') {
      setShowOperateIndex(index);
    } else {
      setShowOperateIndex(-1);
    }
  };

  useEffect(() => {
    if (knowledge) {
      setKnows(knowledge.filter(item => !isEmpty(item)));
      list.current = knowledge;
    }
  }, [knowledge]);

  return (
    <>
      <div className='control-container'>
        <div className='control'>
          <div className='control-inner'>
            {
              knows.length ? knows.map((item, index) => {
                return (
                  <div className='item-container' key={index}>
                    <div className='item' onMouseEnter={!readOnly ? () => handleHoverItem(index, 'enter') : undefined} onMouseLeave={!readOnly ? () => handleHoverItem(index, 'leave') : undefined}>
                      <span className='text'>{item.name}</span>
                      {
                        index === showOperateIndex && (<span>
                          <img src={closeImg} style={{ cursor: 'pointer' }} alt="" onClick={() => deleteItem(item)} />
                        </span>)
                      }
                    </div>
                    {
                      item.notExist && <div className='not-exist'>{`${t('knowledgeBase')}${item.name}${t('selectedValueNotExist')}`}</div>
                    }
                  </div>
                )
              }) : <div className='no-data'>{t('noData')}</div>
            }
          </div>
        </div>
      </div>
      <AddKnowledge
        modalRef={modalRef}
        tenantId={tenantId}
        groupId={groupId}
        handleDataChange={handleChange}
      />
    </>
  )
};


export default Knowledge;
