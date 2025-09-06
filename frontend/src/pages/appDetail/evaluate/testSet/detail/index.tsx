/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { Button, Space, Drawer, Descriptions } from 'antd';
import React, { useEffect, useState } from 'react';
import TreeTable from '../createTestset/treeTable';
import { getEvalDataListDetail, getEvalDataEvaluate } from '@/shared/http/appEvaluate';
import { useTranslation } from "react-i18next";

interface props {
  visible: boolean;
  params?: any;
  detailCallback: Function;
  type?: string;
}

const SetDetail = ({ params, visible, detailCallback, type }: props) => {
  const { t } = useTranslation();
  const [detailData, setDetailData] = useState<any>([]);
  const [treeData, setTreeData] = useState<any>([]);
  const [getIndex, setGetIndex] = useState(1);
  const [getPage, setGetPage] = useState(10);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    getDatailData();
  }, []);

  const getDatailData = async () => {
    try {
      const res = await getEvalDataListDetail(params.id);
      setDetailData(res?.data);
    } catch (error) {}
  };

  useEffect(() => {
    if (type === 'detail') {
      let versionParam = params?.versions?.map((item: any) => item.version);
      getContentsData(versionParam);
    }
  }, [getPage, getIndex, visible]);

  // 获取参数
  const getParams = () => {
    let objPrams = {
      datasetId: params.id,
      version: 9007199254740991, // 传入参数为js最大值，2的53次方
      pageIndex: getIndex,
      pageSize: getPage,
    };
    return objPrams;
  };

  const getContentsData = async (versionParam: any) => {
    try {
      const res = await getEvalDataEvaluate(getParams());
      setTotal(res?.data?.total);
      setTreeData(res?.data?.items);
    } catch {}
  };
  
  const closeDrawer = () => {
    detailCallback();
  };

  return (
    <>
      <Drawer
        title={t('evaluationDetails')}
        width={800}
        open={visible}
        onClose={closeDrawer}
        maskClosable={false}
        destroyOnClose={true}
        footer={
          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Space>
              <Button style={{ minWidth: 96 }} onClick={closeDrawer}>
                {t('close')}
              </Button>
            </Space>
          </div>
        }
      >
        <Descriptions layout='vertical' colon={false}>
          <Descriptions.Item label={t('testSetName')}>{detailData.name}</Descriptions.Item>
          <Descriptions.Item label={t('testSetDescription')}>{detailData.description}</Descriptions.Item>
          <Descriptions.Item label={t('createdBy')}>{detailData.createdBy}</Descriptions.Item>
          <Descriptions.Item label={t('createdAt')}>{detailData.createdAt}</Descriptions.Item>
          <Descriptions.Item label={t('modificationTime')}>{detailData.updatedAt}</Descriptions.Item>
        </Descriptions>
        <TreeTable
          data={treeData}
          type={type}
          setGetIndex={setGetIndex}
          setGetPage={setGetPage}
          total={total}
        />
      </Drawer>
    </>
  );
};

export default SetDetail;
