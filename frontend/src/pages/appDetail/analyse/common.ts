/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import i18n from '../../../locale/i18n';

export const timeOption = [
  { value: '0', label: i18n.t('today') },
  { value: '1', label: i18n.t('yesterday') },
  { value: '2', label: i18n.t('last7Days') },
  { value: '3', label: i18n.t('last30Days') },
  { value: '4', label: i18n.t('thisWeek') },
  { value: '5', label: i18n.t('lastWeek') },
  { value: '6', label: i18n.t('thisMonth') },
  { value: '7', label: i18n.t('lastMonth') },
];

export const top5UserOption = {
  grid: {
    left: 50,
    right: 20,
    bottom: 20,
    top: 40
  },
  xAxis: {
    type: 'category',
    axisTick: {
      show: false, // 是否显示坐标轴刻度
    },
    data: [],
  },
  yAxis: {
    type: 'value',
    axisLabel: {
      show: true,
      interval: 'auto',
    },
  },
  series: [
    {
      data: [],
      type: 'bar',
      barWidth: '20',
    },
  ],
};

export const top5DepartmentOption = {
  xAxis: {
    type: 'category',
    axisTick: {
      show: false, // 是否显示坐标轴刻度
    },
    data: [],
  },
  yAxis: {
    type: 'value',
    axisLabel: {
      show: true,
      interval: 'auto',
      formatter: '{value} %',
    },
  },
  series: [
    {
      data: [],
      type: 'bar',
      barWidth: '25%',
    },
  ],
};

export const speedOption = {
  color: ['#3388ff', '#2eb6e6', '#6e6eff'],
  tooltip: {
    trigger: 'item',
  },
  legend: {
    orient: 'vertical',
    top: 80,
    left: 450,
    selectedMode: false,
    formatter(params) {
      return params;
    },
  },
  series: [
    {
      type: 'pie',
      radius: ['80%', '94%'],
      avoidLabelOverlap: true,
      itemStyle: {
        borderRadius: 2,
        borderColor: '#fff',
        borderWidth: 2,
      },
      label: {
        show: false,
        position: 'center',
      },
      labelLine: {
        show: false,
      },
      data: [],
    },
  ],
};

export const tradeOption = {
  grid: {
    left: 50,
    right: 50,
    bottom: 20,
    top: 40
  },
  tooltip: {
    trigger: 'item'
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: [],
  },
  yAxis: {
    type: 'value',
  },
  series: [
    {
      data: [],
      type: 'line',
      smooth: true,
    },
  ],
};
