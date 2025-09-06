/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import * as echarts from 'echarts';
import { Card, Select } from 'antd';
import { getAnalysisData } from '@/shared/http/apps';
import { getAppInfo } from '@/shared/http/aipp';
import { useParams } from 'react-router-dom';
import { setSpaClassName } from '@/shared/utils/common';
import { useTranslation } from 'react-i18next';
import { timeOption, top5UserOption, tradeOption, speedOption } from './common';
import knowledgeImg from '@/assets/images/knowledge/knowledge-base.png';
import './style.scoped.scss';

const AnalyseCard = ({ info }) => (
  <Card className='card full-card'>
    <div className='card-title'>{info?.title}</div>
    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
      <div>
        <span className='number'>{info?.num}</span>
        <span className='number-unit'>{info?.unit}</span>
      </div>
      <img className='knowledge' src={knowledgeImg} />
    </div>
  </Card>
);
const ChartCard = ({ info }: any) => (
  <Card className='chart-card full-card' style={{
    width: `${info?.width ?? 'calc(50% - 8px)'}`
  }}>
    <div className='chart-title'>{info.title}</div>
    <div id={info.id} className='chart'/>
  </Card>
);

const AppAnalyse: React.FC = () => {
  const { t } = useTranslation();
  const [graphData, setgraphData] = useState({ allRequest: {}, allActive: {}, avgSpeed: {} });
  const [time, setTime] = useState('0');
  const [total, setTotal] = useState(0);
  const [pageDisabled, setPageDisabled] = useState(true);
  const [speedList, setSpeedList] = useState([]);
  const { tenantId, appId } = useParams();
  const state = useRef('inactive');
  const timer = useRef<any>(null);

  const initData = () => {
    const tradeChart = echarts.init(document.getElementById('trade'));
    const speedChart = echarts.init(document.getElementById('speed'));
    const top5UserChart = echarts.init(document.getElementById('top5User'));
    getAnalysisDataByTime(top5UserChart, speedChart, tradeChart);
    window.addEventListener('resize', function () {
      tradeChart.resize();
      speedChart.resize();
      top5UserChart.resize();
    });
  }

  const dataInit = () => {
    if (state.current === 'active') {
      timer.current && clearInterval(timer.current);
      initData();
      timer.current = setInterval(() => {
        initData()
      }, 60000);
    }
  }

  useEffect(() => {
    dataInit();
  }, [time]);

  useEffect(() => {
    getAppInfo(tenantId, appId).then((res) => {
      if (res.data.attributes?.latest_version || res.data.state === 'active') {
        state.current = 'active';
        dataInit();
        setPageDisabled(false);
      } else {
        setPageDisabled(true);
      }
    });
    return () => {
      timer.current && clearInterval(timer.current);
      state.current = 'inactive';
    };
  }, []);


  // 设置Tab数据
  const setTabData = (metrics: any) => {
    const allRequest = { title: t('totalRequestNum'), num: metrics?.total_requests?.value ?? 0, unit: t('num'), sign: 'down', percent: 2 };
    const allActive = { title: t('totalPV'), num: metrics?.total_active_users?.value ?? 0, unit: t('num'), sign: 'up', percent: 2 };
    const avgSpeed = { title: t('averRspSpeed'), num: metrics?.average_response_time?.value ?? 0, unit: 'ms', sign: 'down', percent: 2 };
    setgraphData({ allRequest, allActive, avgSpeed })
  }

  // 设置Top5用户数据
  const setTop5UserData = (topUsers: any, top5UserChart) => {
    const x = (topUsers ?? []).map(user => (user?.createUser || ''));
    const y = (topUsers ?? []).map(user => (user?.accessCount || 0));
    top5UserOption.xAxis.data = x;
    top5UserOption.series[0].data = y;
    top5UserChart.setOption(top5UserOption);
  }

  const order = ['below 500ms', '501-1000ms', '1001ms-2000ms', 'above 2000ms'];

  // 设置平均响应速度
  const setAvargeSpeed = (avgResponseRange, speedChart) => {
    let totalData = 0;
    const data = Object.keys(avgResponseRange || {}).map(item => {
      totalData += avgResponseRange[item]?.count || 0;
      return ({
        value: avgResponseRange[item]?.count || 0,
        name: avgResponseRange[item]?.range || ''
      })
    });
    setTotal(totalData);
    data.sort((start, next) => {
      return order.indexOf(start.name) - order.indexOf(next.name)
    });
    speedOption.series[0].data = data;
    speedChart.setOption(speedOption);
  }

  // 设置用户访问趋势
  const setUserTrade = (userAccessData, speedChart) => {
    const xData = (userAccessData || []).map(item => (item.time_unit))
    const xValue = (userAccessData || []).map(item => (item.access_count))
    tradeOption.xAxis.data = xData;
    tradeOption.series[0].data = xValue;
    speedChart.setOption(tradeOption);
  }

  // 获取分析数据
  const getAnalysisDataByTime = async (top5UserChart, speedChart, tradeChart) => {
    try {
      const res = await getAnalysisData({
        appId: appId,
        timeType: time,
      });
      setTabData(res?.basicMetrics);
      setTop5UserData(res?.topUsers, top5UserChart);
      setAvargeSpeed(res?.avgResponseRange, speedChart);
      setUserTrade(res?.userAccessData, tradeChart)
      if (res && res.avgResponseRange) {
        let avgResponseRange = Object.values(res?.avgResponseRange);
        avgResponseRange.sort((start, next) => {
          return order.indexOf(start.range) - order.indexOf(next.range)
        });
        setSpeedList(avgResponseRange);
      }
    } catch (error) {

    }
  }

  return (
    <div className={setSpaClassName('app-analyzed')}>
      <div className='select'>
        <div className='analyse'>{t('analyse')}</div>
        <Select
          className='select-time'
          defaultValue={
            time
          }
          style={{ width: 220 }}
          disabled={pageDisabled}
          onChange={setTime}
          options={timeOption}
        />
      </div>
      <div className='cards'>
        <AnalyseCard info={graphData.allRequest} />
        <AnalyseCard info={graphData.allActive} />
        <AnalyseCard info={graphData.avgSpeed} />
      </div>
      <div className='chart-cards' style={{height: 390}}>
        <ChartCard info={{ id: 'top5User', title: `Top5${t('user')}`}} />
        <Card className='chart-card full-card'>
          <div className='chart-title'>{t('averRspSpeed')}</div>
          <div style={{ display: 'flex' }}>
            <div id='speed' style={{ width: 600, height: 250 }} />
            <div className='speed-number'>
              <div className='number'>{total}</div>
              <div>{t('dataPieces')}</div>
            </div>
          </div>
          <div style={{
            margin: '-169px 600px'
          }}>{
              speedList.map((item, index) => {
                return (<div style={{
                  marginBottom: 8,
                  fontSize: 12
                }} key={index}>{item.count}</div>)
              })
            }</div>
        </Card>
      </div>
      <div className='chart-cards' style={{height: 305}}>
        <ChartCard info={{ id: 'trade', title: t('userAccessTrend'), width: '100%'}} />
      </div>
    </div>
  );
};

export default AppAnalyse;
