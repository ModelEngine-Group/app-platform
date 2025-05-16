/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect, useRef, useImperativeHandle } from 'react';
import { pick, merge } from 'lodash';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setValidateInfo } from '@/store/appInfo/appInfo';
import { getPluginByUniqueName } from '@/shared/http/plugin';
import AddSkill from '../../../addFlow/components/tool-modal';
import SkillList from './skill-list';

const Skill = (props) => {
  const { pluginData, updateData, toolsRef, validateList, readOnly } = props;
  const [skillList, setSkillList] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const pluginMap = useRef([]);
  const dispatch = useAppDispatch();
  const appValidateInfo = useAppSelector((state) => state.appStore.validateInfo);

  useImperativeHandle(toolsRef, () => {
    return { addPlugin };
  });

  const addPlugin = () => {
    setShowModal(true);
  }
  // 选择数据后回调
  const confirmCallBack = (workFlowList, fitList) => {
    const toolList = fitList.concat(workFlowList);
    setSkillList(toolList);
    const uniqueNameList = toolList.map((item) => item.uniqueName);
    updateData(uniqueNameList, 'tools');
  };
  // 删除
  const deleteItem = (item) => {
    let workFlowList = [];
    let fitList = [];
    pluginMap.current = pluginMap.current.filter(pItem => pItem.uniqueName !== item.uniqueName);
    pluginMap.current.forEach(item => {
      if (item.tags?.includes('WATERFLOW')) {
        workFlowList.push(item);
      } else {
        fitList.push(item);
      }
    });
    setSkillList([...pluginMap.current]);
    confirmCallBack(workFlowList, fitList);
    dispatch(setValidateInfo(appValidateInfo.filter(it => it.uniqueName !== item.uniqueName)));
  }
  // 获取插件列表
  const getPluginList = () => {
    const uniqueNameList = pluginData.map(item => `uniqueNames=${item}`);
    const uniqueNameQuery = uniqueNameList.join('&');
    getPluginByUniqueName(uniqueNameQuery).then(({ data }) => {
      setSkillArr(data || []);
    });
  };
  // 回显设置
  const setSkillArr = (data) => {
    pluginMap.current = [];
    pluginData.forEach(item => {
      const pluginItem = data.find(it => it.uniqueName === item);
      if (pluginItem) {
        const curItem = merge(pick(pluginItem, ['uniqueName', 'pluginId', 'name', 'tags', 'runnables']), {
          type: pluginItem.tags.includes('WATERFLOW') ? 'workflow' : 'tool',
          appId: pluginItem.runnables?.APP?.appId || '',
        })
        pluginMap.current.push(curItem);
      }
    })
    setSkillList([...pluginMap.current]);
  }

  // 更新每一条是否存在
  const updateExistStatus = () => {
    if (!validateList?.length) {
      return;
    }
    pluginMap.current = pluginMap.current.map(item => {
      const checkItem = validateList.find(it => it.configName === 'plugin' && it.uniqueName == item.uniqueName);
      const curItem = checkItem ? checkItem : item;
      return {
        ...curItem,
        name: curItem.name || item.uniqueName,
        notExist: !!checkItem,
      }
    });
    setSkillList(pluginMap.current);
  };

  useEffect(() => {
    if (pluginData && pluginData.length) {
      getPluginList();
    }
  }, [pluginData]);

  useEffect(() => {
    updateExistStatus();
  }, [validateList]);
  return (
    <>
      <div className='control-container'>
        <div className='control'>
          <div className='control-inner inner-list'>
            <SkillList skillList={skillList} deleteItem={deleteItem} readOnly={readOnly}></SkillList>
          </div>
          <AddSkill
            type='addSkill'
            showModal={showModal}
            setShowModal={setShowModal}
            checkData={skillList}
            confirmCallBack={confirmCallBack}
          />
        </div>
      </div>
    </>
  )
};

export default Skill;
