/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useImperativeHandle, useRef } from 'react';
import { Form, Input, Image, Drawer, Button, Typography, Modal } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import { uploadForm, createForm, editForm } from '@/shared/http/form';
import { formEnv } from '@/shared/utils/common';
import UploadForm from './upload-form';
import UseHelp from './use-help';
import { Message } from '@/shared/utils/message';
import { getCookie } from '@/shared/utils/common';
import { TENANT_ID } from '../../chatPreview/components/send-editor/common/config';
import EmptyImg from '@/assets/images/empty_preview.svg';

/**
 * 创建表单drawer组件
 * @param formData 表单数据
 * @param isAddOperate 编辑模式
 * @param drawerRef 弹窗引用
 * @param refresh 刷新页面
 * @param formData 表单数据
 * @return {JSX.Element}
 * @constructor
 */
const FormDrawer = ({ formData, isAddOperate, drawerRef, refresh }) => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const [open, setOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [editLoading, setEditLoading] = useState(false);
  const [imgUrl, setImgUrl] = useState('');
  const [formId, setFormId] = useState('');
  const [file, setFile] = useState<any>(null);
  const [isUpload, setIsupload] = useState(false);
  const [operateLoading, setOperateLoading] = useState(false);
  const [showPreview, setShowPreview] = useState(false);
  const helpRef = useRef();
  const appearance = useRef<any>();
  const cLocale = getCookie('locale').toLocaleLowerCase();


  useImperativeHandle(drawerRef, () => {
    return { openOperateDrawer: () => setOpen(true) };
  });

  const drawerConfirm = () => {
    if (isAddOperate) {
      confirm();
    } else {
      form.validateFields().then(() => {
        setEditOpen(true);
      })
    }
  }

  // 新增、编辑确定按钮
  const confirm = () => {
    form.validateFields().then(() => {
      if (file && isUpload) {
        fileUpload(file);
      } else {
        formUpload(appearance.current);
      }
      setOperateLoading(true);
      setEditLoading(true);
    });
  };

  // 上传文件
  const fileUpload = async (file) => {
    const headers = {
      'attachment-filename': encodeURI(file.name || ''),
    };
    try {
      const formData = new FormData();
      formData.append('file', file);
      const res:any = await uploadForm(TENANT_ID, formData, headers);
      if (res.code === 0) {
        formUpload(res.data);
      } else {
        setOperateLoading(false);
        setEditLoading(false);
      }
    } catch (err) {
      setOperateLoading(false);
      setEditLoading(false);
      Message({ type: 'error', content: err.message || t('uploadFileFail') });
    }
  }
  // 上传表单
  const formUpload = async (data) => {
    const values = await form.validateFields();
    const params:any = {
      name: values.name,
      appearance: {
        ...data,
        description: values.describe,
      }
    };
    if (file) {
      params.appearance.fileSize = file.size;
    }
    try {
      if (isAddOperate) {
        const res:any = await createForm(TENANT_ID, params);
        if (res.code === 0) {
          refresh();
          Message({ type: 'success', content: t('operationSucceeded') })
          setOpen(false);
        }
      } else {
        params.version = formData.version;
        params.formSuiteId = formData.formSuiteId;
        const res:any = await editForm(TENANT_ID, formId, params);
        if (res.code === 0) {
          refresh();
          Message({ type: 'success', content: t('operationSucceeded') });
          setOpen(false);
        }
      }
    } finally {
      setOperateLoading(false);
      setEditOpen(false);
      setEditLoading(false);

    }
  }
  // 设置预览图
  const previewCallBack = (url) => {
    if (url) {
      setImgUrl(url);
      setShowPreview(true);
    } else {
      setImgUrl('');
      setShowPreview(false);
    }
  }
  // 图片上传成功
  const fileUploadCallBack = (file) => {
    if (file) {
      setFile(file);
      setIsupload(true);
      form.resetFields(['formComponentPackage']);
      form.setFieldValue('formComponentPackage', 'success');
    } else {
      setFile(null);
      setIsupload(false);
      form.setFieldValue('formComponentPackage', '');
    }
  }
  // 使用帮助
  const helpHandler = () => {
    helpRef.current?.openHelp();
  };

  useEffect(() => {
    form.resetFields(['name', 'formComponentPackage']);
    setIsupload(false);
    if (formData.id) {
      appearance.current = formData.appearance;
      form.setFieldValue('name', formData.name);
      form.setFieldValue('describe', appearance.current.description || '');
      form.setFieldValue('formComponentPackage', 'success');
      setImgUrl(appearance.current.imgUrl);
      setFormId(formData.id);
      setFile({
        name: appearance.current.fileName,
        size: appearance.current.fileSize,
      });
      setShowPreview(true);
    } else {
      form.setFieldsValue(formData);
      setFile(null);
      setShowPreview(false);
    }
  }, [formData]);

  return <>
    <Drawer
      title={isAddOperate ? t('createForm') : t('editForm')}
      className='intelligent-form'
      maskClosable={false}
      closable={false}
      open={open}
      width={570}
      extra={
        <CloseOutlined
          onClick={() => {
            setOpen(false);
          }}
        />
      }
      footer={
        <div className='drawer-footer'>
          <Button
            style={{ width: '90px' }}
            disabled={operateLoading}
            onClick={() => {
              setOpen(false);
            }}
          >
            {t('cancel')}
          </Button>
          <Button
            style={{ width: '90px', backgroundColor: '#2673e5', color: '#ffffff' }}
            onClick={drawerConfirm}
            loading={operateLoading}
          >
            {t('ok')}
          </Button>
        </div>
      }
    >
      <Form
        form={form}
        layout='vertical'
        autoComplete='off'
      >
        <Form.Item
          label={t('name')}
          name='name'
          rules={[{ required: true, message: t('plsEnterName') }, {
            type: 'string',
            max: 64,
            message: t('enterNameRule')
          }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label={t('uploadDesc')}
          name='describe'
        >
          <Input.TextArea rows={3} showCount maxLength={300} />
        </Form.Item>
        <Form.Item
          label={t('uploadIntelligentFormComponentPackage')}
          name='formComponentPackage'
          rules={[{ required: true, message: t('plsUploadIntelligentFormComponentPackage') }]}
        >
          <Typography.Link onClick={helpHandler} className='use-help'>
            {t('help')}
          </Typography.Link>
          <UploadForm 
            drawerOpen={open} 
            previewCallBack={previewCallBack} 
            fileUploadCallBack={fileUploadCallBack}
            isAddOperate={isAddOperate}
            file={file}
          />
        </Form.Item>
        <Form.Item
          label={t('componentPreview')}
          name='componentPreview'
        >
          <div className='preview-component'>
            {
              showPreview ? <Image
                width={'100%'}
                height={'100%'}
                src={ isUpload ? imgUrl : `${origin}/${formEnv() ? 'appbuilder' : 'api/jober'}/static/${imgUrl}`}
                style={{ objectFit: 'contain' }}
              /> : <div className='no-preview'>
                <img src={EmptyImg} alt="" />
                <div className='empty-tip'>{t('emptyPreview')}</div>
              </div>
            }
          </div>
        </Form.Item>
      </Form>
    </Drawer>
    {/* 编辑提示弹框 */}
    <Modal
      title={t('deleteModalEdit')}
      width='380px'
      open={editOpen}
      centered
      onOk={confirm}
      onCancel={() => setEditOpen(false)}
      okButtonProps={{ loading: editLoading }}
      okText={t('ok')}
      cancelText={t('cancel')}
    >
      { cLocale !== 'en-us' ? 
      <div>
        <span>{t('youWillEdit') + formData.name + t('form') + '，' + t('afterEdit')}</span>
        <span style={{ fontWeight: 700 }}>{t('deleteTip')}</span>
      </div> : 
      <div>
        <span>{ t('youWillEdit')}</span>
        <span style={{ fontWeight: 700, margin: '0 8px' }}>{formData.name}</span>
        <span>{t('afterEdit')}</span>
      </div> }
      
    </Modal>
    <UseHelp helpRef={helpRef}></UseHelp>
  </>
};

export default FormDrawer;
