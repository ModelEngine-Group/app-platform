/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useImperativeHandle, useState, useEffect, useRef } from 'react';
import { useHistory } from 'react-router-dom';
import { Drawer, Tabs, Button, Input, Checkbox } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import { getPlugins } from '@/shared/http/plugin';
import { createAipp } from '@/shared/http/aipp';
import { pluginItems } from '../../common/common';
import { Icons } from '@/components/icons';
import ToolCard from '@/pages/addFlow/components/tool-card';
import Pagination from '@/components/pagination/index';
import { useTranslation } from 'react-i18next';
import LoadImg from '@/assets/images/ai/load.png';
import AccountImg from '@/assets/images/ai/account.png';
import '../styles/add-skill.scss';

const AddSkill = (props) => {
  const { t } = useTranslation();
  const { modalRef, tenantId, checkData, confirmCallBack } = props;
  const [open, setOpen] = useState(false);
  const [name, setName] = useState('');
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [pluginCategory, setPluginCategory] = useState(pluginItems[0].key);
  const [pluginData, setPluginData] = useState([]);
  const navigate = useHistory().push;
  const checkedList = useRef([]);

  useEffect(() => {
    open && getPluginList();
    setPluginCategory(pluginItems[0].key);
  }, [open, name, pageNum, pageSize]);

  useEffect(() => {
    checkedList.current = JSON.parse(JSON.stringify(checkData));
  }, [props.checkData])

  const showModal = () => {
    setOpen(true);
  }
  // 确定提交
  const confirm = () => {
    let workFlowList: any = [];
    let fitList: any = [];
    checkedList.current.forEach(item => {
      if (item.tags.includes('WATERFLOW')) {
        workFlowList.push(item);
      } else {
        fitList.push(item);
      }
    })
    let workFlowId = workFlowList.map(item => item.uniqueName);
    let fitId = fitList.map(item => item.uniqueName);
    confirmCallBack(workFlowId, fitId);
    setOpen(false);
  }
  const selectCategory = (category: string) => {
    setPluginCategory(category);
    setPageNum(1);
    setName('');
    getPluginList();
  }
  // 获取插件列表
  const getPluginList = (category = pluginCategory) => {
    getPlugins({ pageNum: pageNum - 1, pageSize, includeTags: 'FIT', name })
      .then(({ data, total }) => {
        setTotal(total);
        setDefaultCheck(data);
        setPluginData(data);
      })
  }
  // 新增工具流
  const handleAddWaterFlow = async () => {
    const timeStr = new Date().getTime().toString();
    const res = await createAipp(tenantId, 'df87073b9bc85a48a9b01eccc9afccc3', { type: 'waterFlow', name: timeStr });
    if (res.code === 0) {
      const aippId = res.data.id;
      navigate(`/app-develop/${tenantId}/app-detail/add-flow/${aippId}`);
    }
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
  // 名称搜索
  const filterByName = (value: string) => {
    if (value !== name) {
      setName(value);
    }
  }
  // 选中
  const onChange = (e, item) => {
    item.checked = e.target.checked;
    if (e.target.checked) {
      checkedList.current.push(item);
    } else {
      checkedList.current = checkedList.current.filter(cItem => cItem.uniqueName !== item.uniqueName);
    }
  }
  // 设置默认选中
  const setDefaultCheck = (data) => {
    let nameList = checkedList.current.map(item => item.uniqueName);
    data.forEach(item => {
      item.checked = nameList.includes(item.uniqueName);
    })
  }
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  return <>
    <Drawer
      title={t('selectPlugin')}
      placement='right'
      width='1230px'
      closeIcon={false}
      onClose={() => setOpen(false)}
      open={open}
      footer={
        <div className='drawer-footer'>
          <Button onClick={() => setOpen(false)}>{t('cancel')}</Button>
          <Button type='primary' onClick={confirm}>{t('ok')}</Button>
        </div>
      }
      extra={
        <CloseOutlined onClick={() => setOpen(false)} />
      }>
      <div className='mashup-add-drawer'>
        <div className='mashup-add-tab'>
          <span className='active'><img src={LoadImg} />{t('market')}</span>
          <span><img src={AccountImg} />{t('mine')}</span>
        </div>
        <div className='mashup-add-tablist'>
          <Tabs
            items={pluginItems}
            activeKey={pluginCategory}
            onChange={(key: string) => selectCategory(key)}
          />
        </div>
        <div className='mashup-add-content'>
          <div className='mashup-add-head'>
            {
              pluginCategory !== 'WATERFLOW' ? (<Input
                placeholder={t('plsEnter')}
                style={{
                  marginBottom: 16,
                  width: '200px',
                  borderRadius: '4px',
                  border: '1px solid rgb(230, 230, 230)',
                }}
                onPressEnter={(e) => filterByName(e.target.value)}
                prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
                defaultValue={name}
              />) : (<Button type='primary' onClick={handleAddWaterFlow} style={{ marginBottom: 16 }}>{t('createWorkflow')}</Button>)
            }
          </div>
          <div className='mashup-add-inner'>
            {pluginData.map((card: any) =>
              <div className='mashup-add-item' key={card.uniqueName}>
                <ToolCard pluginData={card} />
                <span className='check-item'>
                  <Checkbox defaultChecked={card.checked} onChange={(e) => onChange(e, card)}></Checkbox>
                </span>
              </div>
            )}
          </div>
        </div>
        <div style={{ paddingTop: 16 }}>
          {pluginCategory === 'FIT' && <Pagination
            total={total}
            current={pageNum}
            onChange={selectPage}
            pageSize={pageSize}
          />}
        </div>
      </div>
    </Drawer>
  </>
};
export default AddSkill;
