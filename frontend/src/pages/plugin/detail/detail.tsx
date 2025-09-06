/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Divider, Table, Tag } from 'antd';
import { PluginIcons } from '@/components/icons/plugin';
import { IconMap, outputColumns, paramsColumns } from '../helper';
import { recursion } from '../../helper';
import { useTranslation } from 'react-i18next';
import knowledgeImg from '@/assets/images/knowledge/plugin.png';
import userImg from '@/assets/images/ai/user.jpg';
import '../style.scoped.scss';

/**
 * 插件详情右侧抽屉组件
 *
 * @return {JSX.Element}
 * @param pluginData  插件详情数据
 * @constructor
 */
const PlugeDetail: React.FC = ({ pluginData }) => {
  const { t } = useTranslation();
  const [data, setData] = useState<any>([]);
  const [inputParams, setInputParams] = useState<any>([]);
  const [outputParams, setOutputParams] = useState<any>([]);
  const refreshDetail = async () => {
    setData(pluginData);
    let properties = pluginData?.schema?.parameters?.properties || {};
    let returns = pluginData?.schema?.return;
    const resInput: any = Object.keys(properties).map((key) => ({ ...properties[key], name: key }));
    recursion(resInput);
    setInputParams(resInput);
    if (returns.type === 'object' || returns.type === 'array') {
      let resOutput: any = [returns];
      recursion(resOutput);
      setOutputParams(resOutput);
    } else {
      setOutputParams([pluginData?.schema?.return || {}]);
    }
  };
  useEffect(() => {
    if (pluginData) {
      refreshDetail();
    }
  }, [pluginData]);

  return (
    <div className='engine-plugin-detail'>
      <div className='detail-header'>
        <img src={knowledgeImg} />
        <div>
          <div style={{ display: 'flex', alignItems:'center' }}>
            <div className='detail-header-name'>{data?.name}</div>
            <PluginIcons.ToolIcon />
            <div className='icon-display' style={{ gap: '4px' }}>
              {IconMap[data?.tags?.[0]]?.icon}
              <span style={{ fontSize: 12, fontWeight: 700 }}>
                {IconMap[data?.tags?.[0]]?.name}
              </span>
            </div>
            <div className='header-tag'>
              {data?.tags?.map((tag: string, index: number) => {
                if (tag.trim().length > 0) {
                  return <Tag key={index}>{tag}</Tag>;
                }
              })}
            </div>
          </div>
          <div className='user-info'>
            <img width="18" height="18" src={userImg} alt="" />
            <span className='header-user'>{data?.creator}</span>
            <span className='header-time' hidden>
              {t('createdAt')}
            </span>
            <span className='header-time' hidden>
              {t('quoteNumber')}：
            </span>
          </div>
        </div>
      </div>
      <div style={{ marginTop: 14 }}>{data?.description}</div>
      <Divider />
      <div className='param-title'>{t('inputParam')}</div>
      <Table dataSource={inputParams} columns={paramsColumns} pagination={false} rowKey='key' />
      <div className='param-title' style={{ marginTop: 14 }}>
        {t('outputParam')}
      </div>
      <Table dataSource={outputParams} columns={outputColumns} pagination={false} rowKey='key' />
    </div>
  );
};

export default PlugeDetail;
