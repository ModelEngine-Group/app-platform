/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Slider } from 'antd';
import { useTranslation } from 'react-i18next';

/**
 * 参数搜索配置的滑动输入条
 * @param data 传入滑动输入条的初始数据
 * @param step 滑动输入条中的步距
 * @param onChangeFnc 滑动输入条数据变化时的回调
 * @param scale 传入滑动输入条刻度的默认值
 */
interface SliderProps {
  data?: any;
  step?: number;
  onChangeFnc?: any;
  scale?: number;
}

const SliderFilter = ({ data, step, onChangeFnc, scale }: SliderProps) => {
  const { t } = useTranslation();
  // 刻度标记变化回调
  const getMarks = (item: any) => {
    return {
      [item.minimum]: item.minimum,
      [item.defaultValue]: t('default'),
      [item.maximum]: item.maximum,
    };
  };
  // slider触发onChange回调
  const onchange = (value: number) => {
    onChangeFnc(value);
  };
  return (
    <>
      <Slider
        min={data.minimum}
        max={data.maximum}
        marks={getMarks(data)}
        step={step}
        defaultValue={scale}
        onChange={onchange}
      />
    </>
  );
};

export default SliderFilter;
