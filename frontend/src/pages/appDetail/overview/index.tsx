/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Modal, Button, Divider, Spin } from 'antd';
import { getAppInfo, getAppInfoByVersion, exportApp } from '@/shared/http/aipp';
import { deleteAppApi } from '@/shared/http/appDev';
import { Message } from '@/shared/utils/message';
import { useHistory, useParams } from 'react-router';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setAppInfo } from "@/store/appInfo/appInfo";
import { findConfigValue } from '@/shared/utils/common';
import { exportJson } from '@/shared/utils/chat';
import { convertImgPath } from '@/common/util';
import { useTranslation } from "react-i18next";
import knowledgeImg from '@/assets/images/knowledge/knowledge-base.png';
import complateImg from '@/assets/images/ai/complate.png';
import publishImg from '@/assets/images/ai/publish.png';
import userImg from '@/assets/images/ai/user.jpg';
import WarningIcon from '@/assets/images/warning_icon.svg';
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
  const [open, setOpen] = useState('');
  const [detail, setDetail] = useState<any>({});
  const [loading, setLoading] = useState(false);
  const [exportLoading, setExportLoading] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
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
  // 应用导出
  const handleExportApp = async () => {
    try{
      setExportLoading(true);
      const res = await exportApp(tenantId, detail.id);
      exportJson(res, detail.name);
    } finally {
      setExportLoading(false);
    }
  }
  // 删除应用
  const  deleteApp = async () => {
    const storage = {
      appId: detail.id,
      type: 'deleteApp'
    }
    try {
      setDeleteLoading(true);
      const res: any = await deleteAppApi(tenantId, detail.id);
      if (res.code === 0) {
        setOpen(false);
        Message({ type: 'success', content: t('deleteAppSuccess') });
        localStorage.setItem('storageMessage', JSON.stringify(storage));
        navigate('/app-develop');
      } else {
        Message({ type: 'error', content: res.msg || t('deleteFail') });
      }
    } finally {
      setDeleteLoading(false);
    }
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
                  {
                    <div className='app-btn'>
                      { !readOnly && <Button type='primary' loading={btnLoading} onClick={gotoArrange}>{t('toArrange')}</Button> }
                      <Button loading={exportLoading} onClick={handleExportApp}>{t('export')}</Button>
                      { !readOnly && <Button loading={deleteLoading} onClick={() => setOpen(true)}>{t('delete')}</Button> }
                    </div>
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
          <Divider style={{ margin: 0, backgroundColor: 'rgb(230, 230, 230)' }} />
          <div>
            <div className='remarks-content'>
              <div className='remarks'>
                <div className='left'>{t('prologue')}</div>
                <div className='right'>{opening}</div>
              </div>
            </div>
          </div>
        </div>
        <div className='detail-card'>
          <PublicCard url={detail.chatUrl} type='URL' detail={detail}  />
          <PublicCard url={`/${process.env.PACKAGE_MODE === 'spa' ? `agent/v1/api/${tenantId}` : 'api/jober'}`} type='API' auth={readOnly} detail={detail} />
        </div>
      </div>
      {/* 删除弹窗 */}
      <Modal
        title={
          <div className='delete-title'>
            <img src={WarningIcon}></img>
            <span>{t('deleteAppModalTitle')}</span>
          </div>
      }
        width='380px'
        open={open}
        centered
        onOk={() => deleteApp()}
        onCancel={() => setOpen(false)}
        okButtonProps={{ loading: deleteLoading }}
        okText={t('ok')}
        cancelText={t('cancel')}
        destroyOnClose
      >
        <p>{t('deleteAppModalAlert')}</p>
      </Modal>
    </Spin>
  )
}
export default AppOverview;
