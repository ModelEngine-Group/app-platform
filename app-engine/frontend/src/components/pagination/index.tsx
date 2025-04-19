/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Pagination } from 'antd';
import { useTranslation } from "react-i18next";
import { getCookie } from '@/shared/utils/common';

interface paginationProps {
  // 当前页
  current?: number;

  // 总条数
  total?: 0;

  // 默认页
  defaultCurrent?: number;

  // 默认分页
  defaultPageSize?: number;

  // 每页条数
  pageSize?: number;

  // 分页设置
  pageSizeOptions?: number[] | string[];

  // 分页是否显示
  showSizeChanger?: boolean;

  // 快速跳转
  showQuickJumper?: boolean;

  // 禁用分页
  disabled?: boolean;

  // 只有一页是否隐藏分页
  hideOnSinglePage?: boolean;

  // 页码或pagesize改变的回调
  onChange?: (page: number, pageSize: number) => any;

  // 展示total
  showTotalFunc?: boolean;

  size?: string

}

// 默认参数
const defaultConfig: paginationProps = {
  current: 1,
  pageSizeOptions: [10, 20, 50, 100],
  pageSize: 10,
  showSizeChanger: true,
  showQuickJumper: true,
  hideOnSinglePage: false,
  showTotalFunc: true,
  total: 0,
  size: 'small'
}

const App: React.FC = (props: paginationProps = defaultConfig) => {
  const config = {...defaultConfig, ...props };
  const cLocale = getCookie('locale').toLocaleLowerCase();
  const { t } = useTranslation();
  return (
    <>
      {
        (props.total > 0) && <div className='page-component'>
          { config.showTotalFunc && ( cLocale !== 'en-us' ? 
            <span>{t('total')} {props.total} {t('piece')}</span> : 
            <span>{t('total')}：{props.total} </span>)
          }
          <Pagination {...config} />
        </div>
      }
    </>
  )
};

export default App;
