/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Breadcrumb, Descriptions } from 'antd';
import { getEvaluateReport, getEvaluateTaskResult } from '@/shared/http/appEvaluate';
import DescriptionTable from './descriptionTable';
import OverviewCharts from './overviewCharts';
import EvaluateCharts from './evaluateCharts';
import OverviewResult from './overviewResult';
import { useTranslation } from 'react-i18next';
import LeftArrowImg from '@/assets/images/ai/left-arrow.png';
import './index.scss';

const viewReport = () => {
  const { t } = useTranslation();
  const [detailData, setDetailData] = useState([]);
  const [pageIndex, setPageIndex] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [resultTotal, setResultTotal] = useState(0);
  const [resultData, setResultData] = useState([]);
  const viewInfo = JSON.parse(sessionStorage.getItem('evaluateView') as any);
  const detailInfo = JSON.parse(sessionStorage.getItem('evaluateDetails') as any);

  const previousClick = () => {
    window.history.back();
    sessionStorage.removeItem('evaluateView');
    sessionStorage.removeItem('evaluateDetails');
  };

  const detailsInfo = async () => {
    const res: any = await getEvaluateReport({ instanceId: viewInfo.instanceId });
    setDetailData(res?.data.items);
  };

  const resultInfo = async () => {
    const params = {
      instanceId: viewInfo.instanceId,
      pageIndex: pageIndex,
      pageSize: pageSize,
    };
    const res: any = await getEvaluateTaskResult(params);
    setResultTotal(res.data.total || 0);
    setResultData(res.data.items || []);
  };

  // 获取当前时间
  const getNowFormatDate = () => {
    let date = new Date();
    let obj: any = {
      year: date.getFullYear(),
      month: date.getMonth() + 1,
      strDate: date.getDate(),
      hour: date.getHours(),
      minute: date.getMinutes(),
      second: date.getSeconds(),
    };
    Object.keys(obj).forEach((key) => {
      if (obj[key] < 10) {
        return (obj[key] = `0${obj[key]}`);
      }
    });
    return `${obj.year}-${obj.month}-${obj.strDate} ${obj.hour}:${obj.minute}:${obj.second}`;
  };

  useEffect(() => {
    detailsInfo();
  }, []);

  useEffect(() => {
    resultInfo();
  }, [pageIndex, pageSize]);
  return (
    <div className='view-report'>
      <div className='mb-4'>
        <Breadcrumb>
          <Breadcrumb.Item>{t('appDevelopment')}</Breadcrumb.Item>
          <Breadcrumb.Item>{t('appDetail')}</Breadcrumb.Item>
          <Breadcrumb.Item>{t('viewReport')}</Breadcrumb.Item>
        </Breadcrumb>
      </div>
      <div className='view-report-flex-box mb-8'>
        <img src={LeftArrowImg} onClick={previousClick} className='w-16' />
        <div>{t('viewReport')}</div>
      </div>
      <div className='view-report-flex'>
        <div className='mb-52 fs-40 col-234EB5'>
          {detailInfo.name}
          {t('descriptionTip1')}
        </div>
        <div className='mb-72 fs-16'>
          {t('generationTime')}
          {getNowFormatDate()}
        </div>
      </div>
      <div className='view-report-flex'>
        <DescriptionTable descripInfo={detailData} viewInfo={viewInfo} detailInfo={detailInfo} />
        <div className='bg-white w-70 p-content display-box'>
          <div className='w-60'>
            <Descriptions title={t('evaluateOverview')}>
              <Descriptions.Item>
                <OverviewCharts passCnt={viewInfo.passCount || 0} totalCnt={resultTotal || 0} />
              </Descriptions.Item>
            </Descriptions>
          </div>
          <div className='w-40 view-report-flex-box'>{viewInfo.description}</div>
        </div>
        <div className='bg-white w-70 p-content'>
          <Descriptions title={t('evaluateCharts')}>
            <Descriptions.Item>
              <EvaluateCharts echartInfo={detailData} />
            </Descriptions.Item>
          </Descriptions>
        </div>
        <div className='bg-white w-70 p-footer'>
          <Descriptions title={t('evaluateResult')}>
            <Descriptions.Item>
              <OverviewResult
                resultIndex={setPageIndex}
                resultSize={setPageSize}
                resultTotal={resultTotal}
                resultData={resultData}
              />
            </Descriptions.Item>
          </Descriptions>
        </div>
      </div>
    </div>
  );
};

export default viewReport;
