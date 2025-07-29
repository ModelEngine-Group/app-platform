/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { Button, Tag, Upload, message, Modal } from 'antd';
import { uploadTestSetFiles, addEvalData } from '@/shared/http/appEvaluate';
import { CheckCircleOutlined } from '@ant-design/icons';
import TreeTable from './treeTable';
import { useTranslation } from 'react-i18next';
import './createTestSet.scss';
interface Props {
  editData?: any;
  getData?: any;
  setTreeDatas?: any;
  type?: string;
  contents?: any;
  setGetIndex?: any;
  setGetPage?: any;
  total?: number;
  setIsOpenModal?: any;
  datasetId?: any;
  refreshTestsetData?: any;
}
const UploadEvalute = ({
  editData,
  getData,
  setTreeDatas,
  type,
  contents,
  setGetIndex,
  setGetPage,
  total,
  setIsOpenModal,
  datasetId,
  refreshTestsetData,
}: Props) => {
  const { t } = useTranslation();
  const [data, setData] = useState<any>([]);
  const [fileList, setFileList] = useState<any>([]);
  const [uploading, setUploading] = useState(false);
  const [tags, setTags] = useState<string[]>([]);
  const [fileSize, setFileSize] = useState(0);
  const [isShow, setIsShow] = useState(false);

  const handleClose = (removedTag: string) => {
    const newTags = tags.filter((tag) => tag !== removedTag);
    setTags(newTags);
  };

  const handleUpload = () => {
    setIsShow(true);
    setUploading(true);
    let render = new FileReader();
    render.readAsArrayBuffer(fileList);
    render.onload = async (e) => {
      try {
        let targetFile = e.target?.result;
        const res: any = await uploadTestSetFiles(targetFile);
        if (res?.code === 0) {
          getData(res?.data);
          setData(res?.data);
        }
      } catch {
      } finally {
        setUploading(false);
      }
    };
  };

  const beforeUpload = (file: any) => {
    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
      message.error(t('uploadTips1'));
    }
    setFileSize(file.size);
    setTags([file.name]);
    setFileList(file);
    return false;
  };

  const props = {
    accept: '.json',
    maxCount: 1,
    beforeUpload,
    showUploadList: false,
  };
  // 文件size转换
  const getFileSize = (size: any) => {
    const bytesSize = (bytes: number) => {
      if (bytes === 0) {
        return '0B';
      }
      let k = 1024;
      let sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
      let i = Math.floor(Math.log(bytes) / Math.log(k));
      return (bytes / Math.pow(k, i)).toPrecision(4) + ' ' + sizes[i];
    };
    return bytesSize(parseInt(size));
  };

  // 编辑测试集添加文件请求参数
  const buildData = () => {
    let arr: any[] = [];
    editData.forEach((item: any) => item.checked && arr.push(data?.contents[item.keyIndex]));
    let dataPrams = {
      datasetId: datasetId,
      contents: arr,
    };
    return dataPrams;
  };

  // 编辑测试集添加文件
  const handleAdd = async () => {
    const res: any = await addEvalData(buildData());
    if (res.code === 0) {
      handleCloseModal();
      setIsOpenModal(isShow);
      refreshTestsetData();
    }
  };
  // 关闭弹框
  const handleCloseModal = () => {
    setIsShow(false);
  };

  return (
    <>
      <div style={{ display: 'flex' }}>
        <div className='upload-test-set'>
          {tags.map<React.ReactNode>((tag) => {
            const tagElem = (
              <Tag
                key={tag}
                closable={true}
                style={{ userSelect: 'none', margin: '4px 11px' }}
                onClose={() => handleClose(tag)}
              >
                <span>
                  <span>{tag}</span>
                  <span style={{ marginLeft: '8px' }}>({getFileSize(fileSize)})</span>
                  <span style={{ marginLeft: '8px', color: 'green' }}>
                    {<CheckCircleOutlined />}
                  </span>
                </span>
              </Tag>
            );
            return tagElem;
          })}
        </div>
        <Upload {...props}>
          <Button>{t('uploadFile')}</Button>
        </Upload>
      </div>
      <div>
        <div>{t('uploadTips2')}</div>
      </div>
      <Button
        type='primary'
        onClick={handleUpload}
        disabled={tags.length === 0}
        loading={uploading}
        style={{ marginTop: 16 }}
      >
        {t('startParsing')}
      </Button>
      {type === 'edit' && isShow && (
        <Modal width={'800px'} open={isShow} onCancel={handleCloseModal} onOk={handleAdd}>
          <TreeTable
            data={data}
            treeDatas={setTreeDatas}
            setGetIndex={setGetIndex}
            setGetPage={setGetPage}
            total={data.length}
            type={type}
            show={isShow}
          />
        </Modal>
      )}
      <TreeTable
        data={type === 'edit' ? contents : data}
        treeDatas={setTreeDatas}
        setGetIndex={setGetIndex}
        setGetPage={setGetPage}
        total={type !== 'create' ? total : data.length}
        type={type}
        refreshTestsetData={refreshTestsetData}
      />
    </>
  );
};

export default UploadEvalute;
