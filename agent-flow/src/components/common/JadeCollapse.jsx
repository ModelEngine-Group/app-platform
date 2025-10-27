/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import {Collapse} from 'antd';
import React from 'react';
import {useConfigContext} from '@/components/DefaultRoot.jsx';
import PropTypes from 'prop-types';

/**
 * JadeCollapse组件，该组件会根据Config的值来判断是否需要默认展开。
 *
 * @param defaultActiveKey 默认展开的Collapse名称。
 * @param bordered 是否显示边框。
 * @param className 类名。
 * @param props 参数。
 * @return {JSX.Element} Jade折叠组件。
 * @constructor
 */
export const JadeCollapse = ({ defaultActiveKey = [], bordered = false, className = '', ...props }) => {
  const isConfig  = useConfigContext();
  const activeKey = isConfig ? defaultActiveKey : [];

  return (
    <Collapse
      bordered={bordered}
      className={`jade-custom-collapse ${className}`}
      defaultActiveKey={activeKey}
      {...props}
    />
  );
};

JadeCollapse.propTypes = {
  defaultActiveKey: PropTypes.array,
  bordered: PropTypes.bool,
  className: PropTypes.string,
};