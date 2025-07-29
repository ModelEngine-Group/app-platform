/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import { Input, Empty, Spin, Select } from 'antd';
import { Icons } from '@/components/icons';
import { handleClickAddToolNode, handleDragToolNode } from '../utils';
import ToolModal from './tool-modal';
import { PluginTypeE } from './model';
import { getMyPlugin, getPluginTools } from '@/shared/http/plugin';
import { debounce } from '@/shared/utils/common';
import { useAppSelector } from '@/store/hook';
import { useTranslation } from 'react-i18next';
import pluginImg from '@/assets/images/ai/plugin.png';
import '../styles/tool-item.scss';

const { Option } = Select;

/**
 * 应用编排左侧菜单工具节点列表组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const ToolItem = () => {
  const { t } = useTranslation();
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const [toolKey, setToolKey] = useState('Builtin');
  const [loading, setLoading] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  const [pluginData, setPluginData] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const searchName = useRef<any>(undefined);
  const listType = useRef('market');
  useEffect(() => {
    getPluginList()
  }, [toolKey])

  const handleChange = (value: string) => {
    setPageNum(1);
    listType.current = value;
    getPluginList();
  };

  // 获取插件列表
  const getPluginList = async () => {
    setLoading(true);
    let res:any;
    if (listType.current === PluginTypeE.MARKET) {
      res = await getPluginTools({
        pageNum,
        pageSize: 100,
        isPublished: true,
        name: searchName.current
      }, 'excludeTags=APP&excludeTags=WATERFLOW&excludeTags=BASIC');
    } else {
      res = await getMyPlugin(tenantId, {
        pageNum,
        pageSize: 100,
        isDeployed: true,
        name: searchName.current
      }, 'modal');
    }
    const data = listType.current === PluginTypeE.MARKET ? res?.data : res?.data?.pluginToolData;
    setLoading(false);
    if (toolKey === 'HUGGINGFACE') {
      data.forEach(item => {
        item.type = 'huggingFaceNodeState',
          item.context = {
            default_model: item.defaultModel
          }
      })
    };
    setPluginData(data);
  }

  // 名称搜索
  const filterByName = (value: string) => {
    searchName.current = value.trim();
    setPageNum(1);
    getPluginList();
  }
  const handleSearch = debounce(filterByName, 1000);
  return <>
      <Input
        placeholder={t('plsEnter')}
        maxLength={200}
        onChange={(e) => handleSearch(e.target.value)}
        prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
      />
    <div className='tool-tab'>
      <span className='more' onClick={() => setShowModal(true)}>{t('more')}</span>
    </div>
    <Spin spinning={loading}>
      {
        pluginData.length > 0 && <div className='drag-list'>
          {pluginData.map((item:any, index) => {
            return (
              <div
                className='drag-item'
                onDragStart={(e) => handleDragToolNode(item, e)}
                draggable={true}
                key={index}
              >
                <div className='drag-item-title'>
                  <div>
                    <span className='content-node-name node-tool'>
                      <img src={pluginImg} alt='' />
                      {item.name}
                    </span>
                  </div>
                  <span className='drag-item-icon'
                    onClick={(event) => handleClickAddToolNode(item.type || 'toolInvokeNodeState', event, item)}>
                  </span>
                </div>
              </div>
            )
          })
          }
        </div>
      }
      {pluginData.length === 0 && <div className='tool-empty'><Empty description={t('noData')} /></div>}
    </Spin>
    <ToolModal showModal={showModal} setShowModal={setShowModal} modalType='mashup' />
  </>
};
export default ToolItem;
