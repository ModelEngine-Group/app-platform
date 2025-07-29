/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Modal, Table, Tag } from 'antd';
import { useTranslation } from 'react-i18next';
import { v4 as uuidv4 } from 'uuid';
import { recursion } from '@/pages/helper';
import PluginWarningImg from '@/assets/images/plugin/plugin-warning.png';
import DownImg from '@/assets/images/ai/down.png';
import KnowledgeBaseImg from '@/assets/images/knowledge/knowledge-base.png';
import i18n from '@/locale/i18n';

/**
 * 重复工具弹窗
 *
 * @isShow 显示隐藏
 * @setIsShow 显示隐藏回调
 * @repeatData 重复工具的数据
 */

const columns = [
  {
    title: i18n.t('paramName'),
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: i18n.t('paramType'),
    dataIndex: 'type',
    key: 'type',
  },
  {
    title: i18n.t('paramDescription'),
    dataIndex: 'description',
    key: 'description',
  },
];

const RepeatTools = (props: any) => {
  const { isShow, setIsShow, repeatData } = props;
  const { t } = useTranslation();
  const [result, setResult] = useState<any>([]);
  const [changeShow, setChangeShow] = useState<any>([]);

  // 关闭弹窗
  const onClose = () => {
    setIsShow(false);
  };

  // 点击获取详情
  const pluginDetailClick = (item: any, lItem: any) => {
    item.definitions.forEach((defItem: any) => {
      if (defItem.key === lItem.key) {
        defItem.open = !defItem.open;
      }
    });
    setResult([item]);
  };

  // 折叠回调
  const pluginItemClick = (item: any) => {
    changeShow.forEach((resultItem: any) => {
      if (resultItem.key === item.key) {
        resultItem.isShow = !resultItem.isShow;
      }
    });
    setResult([...changeShow]);
  };

  useEffect(() => {
    repeatData.forEach((item: any, idx: number) => {
      item.isShow = true;
      item.key = idx;
      item.definitions.forEach((defItem: any, index: number) => {
        defItem.open = false;
        defItem.key = index;
        defItem.id = uuidv4();
      });
    });
    setResult(repeatData);
    setChangeShow(repeatData);
  }, []);

  return (
    <>
      <Modal
        open={isShow}
        title={t('defineGroupDetailsTips')}
        onOk={onClose}
        onCancel={onClose}
        width={800}
        mask={false}
        footer={<></>}
      >
        <div className='upload-repeat-box'>
          <div className='upload-repeat-tips upload-modal-title'>
            <img src={PluginWarningImg} className='upload-img' />
            {t('defineGroupDetailsTitleTips')}
          </div>
          {result.map((item: any, index: number) => {
            return (
              <div
                key={index}
                className={`upload-repeat-content ${item.isShow ? 'upload-repeat-open' : ''}`}
              >
                <div className='upload-repeat-modal-tips' onClick={() => pluginItemClick(item)}>
                  <div className='head-left upload-repeat-modal-tips-title'>
                    <img src={DownImg} className='upload-img' />
                    {item.name}
                  </div>
                  <div className='upload-summary-box'>
                    <span className='upload-repeat-background'>{t('summary')}</span>
                    <span className='upload-repeat-modal-overflow'>{item.summary}</span>
                  </div>
                  <div className='upload-description-box'>
                    <span className='upload-repeat-background'>{t('describe')}</span>
                    <span className='upload-repeat-modal-overflow'>{item.description}</span>{' '}
                  </div>
                </div>
                <div className='upload-plugin-content'>
                  {item.definitions.map((definitionItem: any, index: number) => {
                    let properties = definitionItem?.schema?.parameters?.properties;
                    const repeatRes: any = Object.keys(properties).map((key) => ({
                      ...properties[key],
                      name: key,
                    }));
                    recursion(repeatRes);
                    return (
                      <div key={index} className='param-card'>
                        <div className='card-header-left'>
                          <img src={KnowledgeBaseImg} />
                          <div>
                            <div className='upload-card-name'>{definitionItem.schema?.name}</div>
                            <div className='card-user'>
                              {definitionItem?.tags?.map((tag: string, index: number) => {
                                if (tag.trim().length > 0) {
                                  return (
                                    <Tag className='upload-tags' key={index}>
                                      {tag}
                                    </Tag>
                                  );
                                }
                              })}
                              <span
                                className='card-detail-btn'
                                onClick={() => pluginDetailClick(item, definitionItem)}
                              >
                                {t('viewParam')}
                              </span>
                            </div>
                          </div>
                        </div>
                        <div className='card-des'>{definitionItem?.schema?.description}</div>
                        <div
                          className='card-table'
                          style={{ display: definitionItem.open ? 'block' : 'none' }}
                        >
                          <Table
                            rowKey='name'
                            dataSource={repeatRes}
                            columns={columns}
                            pagination={false}
                          />
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            );
          })}
        </div>
      </Modal>
    </>
  );
};
export default RepeatTools;
