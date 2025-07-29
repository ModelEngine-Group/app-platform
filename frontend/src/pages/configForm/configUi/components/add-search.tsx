/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useImperativeHandle, useState, useRef } from 'react';
import { Modal, Tabs, Radio, Switch, Tooltip, Spin } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';
import { getSearchParams } from '@/shared/http/knowledge';
import { useTranslation } from 'react-i18next';
import { Message } from '@/shared/utils/message';
import type { RadioChangeEvent } from 'antd';
import Slider from './slider';
import SEMANTICImg from '@/assets/images/knowledge/semantic.png';
import FULLTEXTImg from '@/assets/images/knowledge/fulltext.png';
import HYBRIDImg from '@/assets/images/knowledge/hybrid.png';
import USERImg from '@/assets/images/ai/user.jpg';
import '../styles/add-search.scss';

/**
 * 参数搜索设置模态框
 * @param openModalRef 控制模态框显示隐藏
 * @param groupId elsa连接知识库的groupId
 * @param handleDataChange 存储数据方法，并传入elsa中
 */
const AddSearch = (props: any) => {
  const { t } = useTranslation();
  const { openModalRef, groupId, handleDataChange } = props;
  const [isShow, setIsShow] = useState(false);
  const [checkValue, setCheckValue] = useState(''); // 搜索模式单选按钮
  const [filterValue, setFilterValue] = useState('topK'); // 搜索过滤单选按钮
  const [loading, setLoading] = useState(false);
  const checkSearch = useRef<any>({
    indexType: {},
    similarityThreshold: 0,
    referenceLimit: {
      type: '',
      value: 0,
    },
    rerankParam: {
      enableRerank: false,
    },
  });

  const [searchData, setSearchData] = useState<any>([]);
  const [searchDisableData, setSearchDisableData] = useState<any>([]);
  const [searchFliterData, setSearchFilterData] = useState<any>([]);
  const [rerankData, setRerankData] = useState<any>([]);
  const [switchChecked, setSwitchChecked] = useState(false);
  // 检索设置单选按钮
  const onChangeValue = (e: RadioChangeEvent) => {
    searchData.forEach((item: any) => {
      if (item.name === e.target.value) {
        checkSearch.current.indexType = item;
      }
    });
    setCheckValue(e.target.value);
  };
  // 引用上限单选按钮
  const onChangeFilter = (e: RadioChangeEvent) => {
    setFilterValue(e.target.value);
  };
  // 结果重排开关
  const onChangeSwitch = (checked: boolean) => {
    setSwitchChecked(checked);
    checkSearch.current.rerankParam.enableRerank = checked;
  };
  // topK滑动输入条
  const onChangeTopK = (e: any) => {
    searchFliterData.forEach((item: any) => {
      if (item.type === 'topK') {
        checkSearch.current.referenceLimit.type = item.type;
        checkSearch.current.referenceLimit.value = e;
      }
    });
  };
  // 最低相关度滑动输入条
  const onChangeSimilarityThreshold = (e: any) => {
    searchFliterData.forEach((item: any) => {
      if (item.type === 'similarityThreshold') {
        checkSearch.current.similarityThreshold = e;
      }
    });
  };
  // svg图片
  const imgFnc = (e: any) => {
    if (e.type === 'semantic') {
      return SEMANTICImg;
    } else if (e.type === 'fullText') {
      return FULLTEXTImg;
    } else if (e.type === 'hybrid') {
      return HYBRIDImg;
    } else {
      return USERImg;
    }
  };
  // 搜索模式界面
  const searchHTML = (
    <Spin spinning={loading}>
      <div className='searth-title'>{t('retrieveSetting')}</div>
      {searchData.map((item: any) => {
        return (
          <div
            key={item.name}
            className={`search-set ${checkSearch.current.indexType.name === item.name ? 'search-set-check' : 'search-set-selected'}`}
          >
            <div>
              <div>
                <img src={imgFnc(item)} className='search-set-img' />
                <span className='search-set-size'>{item.name}</span>
              </div>
              <Tooltip title={item.description}>
                <div className='search-set-overflow'>{item.description}</div>
              </Tooltip>
            </div>
            <div>
              <Radio.Group
                defaultValue={checkSearch.current.indexType.name}
                onChange={onChangeValue}
                value={checkValue}
              >
                <Radio value={item.name} />
              </Radio.Group>
            </div>
          </div>
        );
      })}
      {searchDisableData.map((item: any) => {
        return (
          <div
            key={item.name}
            className={`search-set search-disable ${checkValue === item.name ? 'search-set-check' : 'search-set-selected'}`}
          >
            <div>
              <div>
                <img src={imgFnc(item)} className='search-set-img search-set-opacity' />
                <span className='search-set-size'>{item.name}</span>
              </div>
              <Tooltip title={item.description}>
                <div className='search-set-overflow'>{item.description}</div>
              </Tooltip>
            </div>
            <div>
              <Radio.Group
                disabled
                defaultValue={checkValue}
                onChange={onChangeValue}
                value={checkValue}
              >
                <Radio value={item.name} />
              </Radio.Group>
            </div>
          </div>
        );
      })}
    </Spin>
  );
  // 搜索过滤界面
  const searchFilter = (
    <Spin spinning={loading}>
      {searchFliterData.map((item: any, index: number) => (
        <div>
          {item.type === 'topK' && (
            <>
              <div className='searth-title'>{item.name}</div>
              <Radio.Group value={filterValue} onChange={onChangeFilter}>
                <Radio value='topK'>topK</Radio>
              </Radio.Group>
              <Slider
                data={item}
                onChangeFnc={onChangeTopK}
                scale={checkSearch.current.referenceLimit.value}
              />
            </>
          )}
          {item.type === 'similarityThreshold' && (
            <>
              <div>
                <span className='search-icon'>{item.name}</span>
                <Tooltip
                  overlayInnerStyle={{ color: '#000' }}
                  color='#fff'
                  title={t('similarityThresholdTips')}
                >
                  <QuestionCircleOutlined />
                </Tooltip>
              </div>
              <Slider
                data={item}
                step={0.1}
                onChangeFnc={onChangeSimilarityThreshold}
                scale={checkSearch.current.similarityThreshold}
              />
            </>
          )}
        </div>
      ))}
    </Spin>
  );

  const items = [
    {
      label: t('searchMode'),
      key: '1',
      children: searchHTML,
    },
    {
      label: t('searchFilter'),
      key: '2',
      children: searchFilter,
    },
  ];

  const showOpenModal = (list = [], groupId:String) => {
    setIsShow(true);
    checkSearch.current = list;
    setSearchFilterData([checkSearch.current]);
    setCheckValue(checkSearch.current.indexType.name);
    setSwitchChecked(checkSearch.current.rerankParam.enableRerank);
    handleGetKnowledgeOptions(groupId);
  };
  // 搜索参数配置数据请求
  const handleGetKnowledgeOptions = async (graphGroupId:String) => {
    setLoading(true);
    const params = { groupId: graphGroupId || groupId};
    try {
      const res: any = await getSearchParams(params);
      if (res.code === 0) {
        setSearchData(res.data?.enableIndexType);
        setSearchDisableData(res.data?.disableIndexType);
        setSearchFilterData(res.data?.filterConfig);
        setRerankData(res.data?.rerankConfig);
        setLoading(false);
      }
    } catch (error) {
      setLoading(false);
    }
  };

  const handleOk = () => {
    if (Object.keys(checkSearch.current.indexType).length === 0) {
      return Message({ type: 'warning', content: `${t('plsChoose')}${t('retrieveSetting')}` });
    }
    handleDataChange(checkSearch.current);
    setIsShow(false);
  };

  const handleCancel = () => {
    setIsShow(false);
  };

  useImperativeHandle(openModalRef, () => {
    return {
      showOpenModal: showOpenModal,
    };
  });
  return (
    <div>
      <Modal
        open={isShow}
        title={t('serchParamsSetting')}
        onOk={handleOk}
        onCancel={handleCancel}
        maskClosable={false}
        className='search-param'
      >
        <Tabs defaultActiveKey='1' items={items}></Tabs>
      </Modal>
    </div>
  );
};

export default AddSearch;
