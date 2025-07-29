/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Descriptions, Divider } from 'antd';
import { useTranslation } from 'react-i18next';
interface DescriptionProps {
  descripInfo: any;
  viewInfo: any;
  detailInfo: any;
}

const DescriptionTable = ({ descripInfo, viewInfo, detailInfo }: DescriptionProps) => {
  const { t } = useTranslation();
  const dataLen = descripInfo.length > 3;
  // 判断保留小数一位
  const toFixed = (num: any) => {
    if (num.toString().split('.')[1]) {
      return num.toFixed(1);
    } else {
      return num;
    }
  };
  return (
    <div className={`bg-white w-70 p-header ${dataLen ? '' : 'display-box'}`}>
      <div className={dataLen ? '' : 'w-60'}>
        <Descriptions layout='vertical' colon={false} column={dataLen ? 4 : 3}>
          <Descriptions.Item label={t('applicationName')}>
            <div>{detailInfo.name}</div>
          </Descriptions.Item>
          <Descriptions.Item label={t('applicationVersion')}>
            <div>{detailInfo.version}</div>
          </Descriptions.Item>
          <Descriptions.Item label={t('evaluateCreateAt')}>
            <div>{viewInfo.instanceCreatedAt}</div>
          </Descriptions.Item>
          <Descriptions.Item label={t('evaluateFinishAt')}>
            <div>{viewInfo.instanceFinishedAt}</div>
          </Descriptions.Item>
          <Descriptions.Item label={t('evaluator')}>
            <div>{viewInfo.instanceCreatedBy}</div>
          </Descriptions.Item>
        </Descriptions>
      </div>
      <Divider style={{ height: 'auto' }} type={dataLen ? 'horizontal' : 'vertical'} />
      <div className={`display-box ${dataLen ? '' : 'w-40'}`}>
        {descripInfo?.map((item: any) => {
          let score = item.averageScore;
          return (
            <div key={item.id} className='view-report-flex'>
              <div>
                <span className={`col-bule ${dataLen ? 'fs-57' : 'fs-75'}`}>{toFixed(score)}</span>
                {t('score')}
              </div>
              <div>{item.nodeName}</div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default DescriptionTable;
