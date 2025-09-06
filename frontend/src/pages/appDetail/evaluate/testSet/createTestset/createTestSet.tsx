/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Button, Form, Input, Space, Drawer, message } from 'antd';
import { useParams } from 'react-router-dom';
import {
  createEvalData,
  editEvalData,
  getEvalDataEvaluate,
} from '@/shared/http/appEvaluate';
import UpdataSet from './upload';
import { useTranslation } from "react-i18next";

interface props {
  visible: boolean;
  createCallback: Function;
  type?: string;
  detailInfo?: any;
  realAppId?: any;
}

interface DataSetInterface {
  ouput: string;
  input: string;
  datasetId: string | null;
}

// 创建参数
interface CreateData {
  data?: DataSetInterface[];
  schema: string;
  appId: string;
  name: string;
  description: string;
  contents: any;
}

// 创建数据集
type FieldType = {
  datasetName: string;
  description: string;
  data: DataSetInterface[];
};

const CreateSet = ({ visible, createCallback, type, detailInfo, realAppId }: props) => {
  const { t } = useTranslation();
  const [createOpen, setCreateOpen] = useState(false);

  const [form] = Form.useForm<FieldType>();

  const { appId } = useParams();

  const [getData, setGetData] = useState<any>([]);
  const [treeDatas, setTreeDatas] = useState([]);
  const [editTreeData, setEditTreeData] = useState<any>([]);
  const [getIndex, setGetIndex] = useState(1);
  const [getPage, setGetPage] = useState(10);
  const [total, setTotal] = useState(0);
  const [isOpenModal, setIsOpenModal] = useState(false);
  
  useEffect(() => {
    setCreateOpen(visible);
  });

  useEffect(() => {
    if (type === 'edit') {
      let versionParam: any = detailInfo?.versions?.map((item: any) => item.version);
      getContentsData(versionParam);
    }
  }, [visible, getIndex, getPage, isOpenModal]);

  const closeDrawer = () => {
    form.resetFields();
    createCallback('cancel', {});
  };

  // 构建参数
  const buildData = (res: FieldType): CreateData => {
    let arr: any[] = [];
    treeDatas.forEach((item: any) => item.checked && arr.push(getData.contents[item.keyIndex]));
    let data: CreateData = {
      appId: realAppId || appId as string,
      name: res.datasetName,
      description: res.description,
      contents: arr,
      schema: getData.schema,
    };
    return data;
  };

  const editParams = (res: any) => {
    let edit: any = {
      id: detailInfo.id,
      name: res.datasetName,
      description: res.description,
    };
    return edit;
  };

  // 点击确认按钮
  const clickSubmit = async () => {
    try {
      if (getData.length === 0 && type === 'create') {
        return message.error(t('evaluationUploadTips'));
      } else {
        const res = await form.validateFields();
        type === 'create'
          ? await createEvalData(buildData(res))
          : await editEvalData(editParams(res));
        closeDrawer();
      }
    } catch (error) {}
  };

  const getContentsData = async (versionParam: any) => {
    try {
      const res = await getEvalDataEvaluate({
        datasetId: detailInfo.id,
        version: 9007199254740991, // 传入参数为js最大值，2的53次方
        pageIndex: getIndex,
        pageSize: getPage,
      });
      setTotal(res?.data?.total);
      setEditTreeData(res?.data?.items);
    } catch {}
  };

  return (
    <Drawer
      title={type === 'create' ? t('createTestSet') : t('editTestSet')}
      width={800}
      open={createOpen}
      onClose={closeDrawer}
      maskClosable={false}
      destroyOnClose={true}
      footer={
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Space>
            <Button style={{ minWidth: 96 }} onClick={closeDrawer}>
              {t('cancel')}
            </Button>
            <Button type='primary' style={{ minWidth: 96 }} onClick={clickSubmit}>
              {t('ok')}
            </Button>
          </Space>
        </div>
      }
    >
      <Form<FieldType> form={form} layout='vertical'>
        <Form.Item
          label={t('testSetName')}
          name='datasetName'
          required
          initialValue={type === 'edit' ? detailInfo.name : ''}
          rules={[{ required: true, message: t('cannotBeEmpty') } ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label={t('testSetDescription')}
          name='description'
          required
          initialValue={type === 'edit' ? detailInfo.description : ''}
          rules={[{ required: true, message: t('cannotBeEmpty') }]}
        >
          <Input />
        </Form.Item>
      </Form>
      <Form>
        <Form.Item label={t('upload')} required colon={false} style={{ marginBottom: '0px' }}></Form.Item>
        <div>
          <UpdataSet
            editData={treeDatas}
            getData={setGetData}
            setTreeDatas={setTreeDatas}
            type={type}
            contents={editTreeData}
            setGetIndex={setGetIndex}
            setGetPage={setGetPage}
            total={total}
            setIsOpenModal={setIsOpenModal}
            datasetId={detailInfo?.id}
            refreshTestsetData={getContentsData}
          />
        </div>
      </Form>
    </Drawer>
  );
};

export default CreateSet;
