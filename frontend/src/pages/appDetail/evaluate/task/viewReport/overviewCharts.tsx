/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import ReactEcharts from 'echarts-for-react';
import { useTranslation } from 'react-i18next';

interface OverviewProps {
  passCnt?: any;
  totalCnt?: any;
}

const Overview = ({ passCnt, totalCnt }: OverviewProps) => {
  const { t } = useTranslation();
  const getOptions = () => {
    return {
      color: ['rgb(51,136,255)', 'rgb(253,162,0)'],
      tooltip: {
        trigger: 'item',
      },
      title: {
        text: totalCnt,
        subtext: t('totalUseCases'),
        top: 'center',
        left: '24%',
        textAlign: 'middle',
      },
      legend: {
        orient: 'vertical',
        top: 'center',
        right: '20%',
      },
      series: [
        {
          type: 'pie',
          radius: ['60%', '70%'],
          avoidLabelOverlap: false,
          label: {
            show: false,
            position: 'center',
          },
          emphasis: {
            label: {
              show: false,
              fontSize: 40,
              fontWeight: 'bold',
            },
          },
          labelLine: {
            show: false,
          },
          data: [
            { value: passCnt, name: t('successfulUseCases') },
            { value: totalCnt - passCnt, name: t('failedUseCases') },
          ],
          center: ['25%', '50%'],
        },
      ],
    };
  };
  return (
    <>
      <ReactEcharts option={getOptions()} style={{ width: '85%' }} />
    </>
  );
};

export default Overview;
