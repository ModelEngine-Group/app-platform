/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Select } from 'antd';
import ReactEcharts from 'echarts-for-react';

interface IPorps {
  echartInfo?: any;
}

const Evaluate = ({ echartInfo }: IPorps) => {
  const [xAxisNewData, setXAxisNewData] = useState([]);
  const [optionLable, setOptionLable] = useState([]);
  const [optionValue, setOptionValue] = useState(0);
  const res = echartInfo[optionValue]?.histogram;
  const colors = ['#5470C6', '#EE6666'];

  // x轴百分比
  const xAxisFnc = () => {
    let newArr: any = [];
    const xAxisData = Array.from({ length: 10 }).map((_, index) => `${(index + 1) * 10}%`);
    xAxisData.forEach((val, index) => {
      let obj = {};
      obj.key = index;
      obj.value = val;
      obj.textStyle = {
        padding: [0, 0, 0, 0],
      };
      newArr.push(obj);
    });
    setXAxisNewData(newArr);
  };

  // 柱状图、曲线
  const echartFnc = () => {
    let selectOptions: any = [];
    echartInfo.forEach((ite: any, index: number) => {
      let optionObj = {};
      optionObj.label = ite.nodeName;
      optionObj.value = index;
      selectOptions.push(optionObj);
    });
    setOptionLable(selectOptions);
  };
  
  // 下拉
  const handleOnchange = (value: any) => {
    setOptionValue(value);
  };

  useEffect(() => {
    xAxisFnc();
  }, []);

  useEffect(() => {
    echartFnc();
  }, [echartInfo]);

  const getOptions = () => {
    return {
      color: colors,
      tooltip: {
        trigger: 'axis',
        formatter: function (params: any) {
          return `${params[0].value}`;
        },
      },
      xAxis: [
        {
          type: 'category',
          data: xAxisNewData,
        },
      ],
      yAxis: [
        {
          show: true,
        },
      ],
      series: [
        {
          type: 'bar',
          data: res,
          barWidth: 18,
        },
        {
          type: 'line',
          showSymbol: false,
          data: res,
          smooth: true,
        },
      ],
    };
  };
  return (
    <>
      <div style={{ width: '95%' }}>
        <div>
          <Select
            style={{ width: '200px' }}
            defaultValue={0}
            options={optionLable}
            onChange={handleOnchange}
          />
        </div>
        <div style={{ width: '95%' }}>
          <ReactEcharts option={getOptions()} style={{ width: '95%' }} />
        </div>
      </div>
    </>
  );
};

export default Evaluate;
