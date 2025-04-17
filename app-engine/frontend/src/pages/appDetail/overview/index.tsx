/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useMemo, useState } from 'react';
import { Button, Divider, Spin } from 'antd';
import { getAppInfo, getAppInfoByVersion } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { useHistory, useParams } from 'react-router';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setAppInfo } from "@/store/appInfo/appInfo";
import { findConfigValue, getAppConfig } from '@/shared/utils/common';
import { convertImgPath } from '@/common/util';
import { useTranslation } from "react-i18next";
import knowledgeImg from '@/assets/images/knowledge/knowledge-base.png';
import complateImg from '@/assets/images/ai/complate.png';
import publishImg from '@/assets/images/ai/publish.png';
import userImg from '@/assets/images/ai/user.jpg';
import PublicCard from './public-card';
import './style.scoped.scss';

/**
 * 应用详情概览组件
 *
 * @return {JSX.Element}
 * @constructor
 */
const AppOverview: React.FC = () => {
  const { t } = useTranslation();
  const navigate = useHistory().push;
  const { appId, tenantId } = useParams();
  const [opening, setOpening] = useState('');
  const [detail, setDetail] = useState<any>({});
  const [loading, setLoading] = useState(false);
  const [btnLoading, setBtnLoading] = useState(false);
  const [appIcon, setAppIcon] = useState('');
  const dispatch = useAppDispatch();
  const readOnly = useAppSelector((state) => state.chatCommonStore.readOnly);

  // 页面初始化
  useEffect(() => {
    setLoading(true);
    dispatch(setAppInfo({}));
    getAppInfo(tenantId, appId).then((res: any) => {
      setLoading(false);
      if (res.code === 0) {
        sessionStorage.setItem('evaluateDetails', JSON.stringify(res?.data));
        setDetail({ ...res.data });
        if (res.data?.attributes?.icon) {
          getImgPath(res.data.attributes.icon);
        }
      } else {
        Message({ type: 'error', content: res.message || t('requestFailed') });
      }
    }).catch(() => {
      setLoading(false);
    });
  }, []);

  // 获取图片
  const getImgPath = async (icon) => {
    const res: any = await convertImgPath(icon);
    setAppIcon(res);
  };

  // 去编排点击回调
  const gotoArrange = () => {
    setBtnLoading(true);
    getAppInfoByVersion(tenantId, appId).then((res: any) => {
      setBtnLoading(false);
      if (res.code === 0) {
        dispatch(setAppInfo({}));
        const newAppId = res.data.id;
        const aippId = res.data.aippId;

        let url = `/app-develop/${tenantId}/app-detail/${newAppId}`;
        if (aippId) {
          url += `/${aippId}`;
        }
        if (detail.appCategory === 'workflow') {
          url += '?type=chatWorkflow';
        }

        navigate(url);
      }
    }).catch(() => {
      setBtnLoading(false);
    })
  };

  useEffect(() => {
    const opening = findConfigValue(detail, 'opening');
    setOpening(opening || '-');
  }), [detail];


  return (
    <Spin spinning={loading}>
      <div className='tab-content'>
        <div className='content-inner'>
          <div className='inner-box'>
            <div className='details-content'>
              {appIcon ?
                <img width={100} height={100} src={appIcon} />
                :
                <img width={100} height={100} src={knowledgeImg} />
              }

              <div className='details-content' style={{ flexDirection: 'column' }}>
                <div className='detail-name'>
                  <span className='text'>{detail?.name || ''}</span>
                  {
                    (detail.attributes?.latest_version || detail.state === 'active') ?
                      (
                        <div className="status-tag">
                          <img src={complateImg} />
                          <span>{t('active')}</span>
                        </div>
                      ) :
                      (
                        <div className="status-tag">
                          <img src={publishImg} />
                          <span>{t('inactive')}</span>
                        </div>
                      )
                  }
                </div>
                <div className='detail-footer'>
                  <div className='icon'>
                  <img width={18} height={18} src={userImg} style={{ borderRadius: '50%' }} alt='' />
                    <span>{detail?.createBy || 'Admin'}</span>
                  </div>
                  <div className='create'>
                    <span>{t('createAt')}</span>
                    <span>{detail?.baselineCreateAt}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div className='app-desc' title={detail?.attributes?.description}>
            {detail?.attributes?.description}
          </div>
          <div className='app-btn'>
            { !readOnly && <Button type='primary' loading={btnLoading} onClick={gotoArrange}>{t('toArrange')}</Button> }
          </div>
          <Divider style={{ margin: 0, backgroundColor: 'rgb(230, 230, 230)' }} />
          <div>
            <div className='remarks-content'>
              <div className='remarks'>
                <span className='left'>{t('prologue')}</span>
                <span className='right'>{opening}</span>
              </div>
            </div>
          </div>
        </div>
        <div className='detail-card'>
          <PublicCard url={detail.chatUrl} type='URL' detail={detail}  />
          <PublicCard url={`/${process.env.PACKAGE_MODE === 'spa' ? `agent/v1/api/${tenantId}` : 'api/jober'}`} type='API' auth={readOnly} detail={detail} />
        </div>
      </div>
    </Spin>
  )
}
export default AppOverview;
