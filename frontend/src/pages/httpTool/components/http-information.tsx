/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useContext } from 'react';
import { Form, Select, Input, Button, TreeSelect } from 'antd';
import { HttpContext } from '../config/context';
import HttpTable from './http-table';
import HttpTableTitle from './http-table-head';
import { updateTable, getDefaultExpandedRowKeys, convertHttpMap } from '../utils';
import { inputTypeOption, inputObjOption, inputArrOption, paramsArrayOption } from '../config';
import { createHttp } from '@/shared/http/plugin';
import { useAppSelector } from '@/store/hook';
import { deepClone } from '../../chatPreview/utils/chat-process';
import { useTranslation } from 'react-i18next';

const { Option } = Select;

const InformationConfiguration = (props: any) => {
  const { t } = useTranslation();
  const { setStepCurrent } = props;
  const [form] = Form.useForm();
  const [protocol, setProtocol] = useState('http');
  const [option, setOption] = useState('GET');
  const [loading, setLoading] = useState(false);
  const [expandedRowKeys, setExpandedRowKeys] = useState([]);
  const { httpInfo, setHttpInfo } = useContext(HttpContext);
  const typeArray = ['array<string>', 'array<integer>', 'array<boolean>'];
  const bodyType = ['Body/JSON'];
  const tableData = useAppSelector((state) => (state.toolHttpStore as any).inputData);

  const selectBefore = (
    <Select value={protocol} onChange={(e) => setProtocol(e)}>
      <Option value='http'>http://</Option>
      <Option value='https'>https://</Option>
    </Select>
  );
  const options = [
    {
      value: 'GET',
      label: 'GET',
    },
    {
      value: 'POST',
      label: 'POST',
    },
    {
      value: 'DELETE',
      label: 'DELETE',
    },
    {
      value: 'FETCH',
      label: 'FETCH',
    },
  ];
  const tableColumn = [
    {
      title: () => <HttpTableTitle title={t('paramName')} required />,
      dataIndex: 'name',
      key: 'name',
      render: (name: string) => <Input disabled={true} defaultValue={name} />,
    },
    {
      title: <HttpTableTitle title={t('paramType')} required />,
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => <div>{setTypeOption(type)}</div>,
    },
    {
      title: <HttpTableTitle title={t('requestType')} required />,
      dataIndex: 'requestType',
      render: (requestType: string, record: any) => {
        return (
          <div>
            {record.isShow && (
              <TreeSelect
                treeLine={true}
                onChange={(e) => valueChange(e, 'requestType', record, 'select')}
                disabled={record.deep > 1 && record.requestType === 'NONE'}
                defaultValue={record.requestType}
                value={record.requestType}
                treeData={getOptions(record)}
                style={{ width: '100%' }}
              />
            )}
          </div>
        );
      },
    },
    {
      title: () => <HttpTableTitle title={t('paramMapping')} />,
      dataIndex: 'mapping',
      key: 'mapping',
      render: (mapping: string, record: any) => (
        <div>
          {record.isShow && record.requestType !== 'NONE' && (
            <Input
              onChange={(e) => valueChange(e, 'mapping', record)}
              defaultValue={mapping}
              placeholder={t('plsEnter')}
              maxLength={64}
            />
          )}
        </div>
      ),
    },
  ];
  // 设置参数类型
  const setTypeOption = (type: string) => {
    let arrayType = paramsArrayOption.filter((item) => item.value === type)[0];
    if (arrayType) {
      return arrayType.label;
    }
    return type;
  };
  // 获取请求类型下拉
  const getOptions = (record: any) => {
    if (record.type === 'object' || record.type === 'array<object>') {
      return inputObjOption;
    } else if (typeArray.includes(record.type)) {
      return inputArrOption;
    } else {
      return inputTypeOption;
    }
  };
  // 递归请求类型
  const getShow = (e: any, isInit = false, hide = false, current = undefined) => {
    e.forEach((item: any) => {
      if (item.deep === 1) {
        if (bodyType.includes(item.requestType)) {
          hide = true;
        } else {
          hide = false;
        }
      }
      if (item.deep === 1 && item.type !== 'array') {
        item.isShow = true;
      } else {
        hide ? (item.isShow = false) : (item.isShow = true);
        if ((current as any)?.type === 'object' && item.type === 'object') {
          item.requestType = 'NONE';
        }
        if (item.type === 'array<object>') {
          item.isShow = true;
          item.requestType = 'NONE';
        }
      }
      if (item.type === 'array') {
        item.isShow = false;
      }
      if (typeArray.includes(item.type) && isInit) {
        item.requestType = 'HEADER';
      }
      if (item.children && item.children.length > 0) {
        getShow(item.children, isInit, hide, item);
      }
    });
    getExpandRow(e);
  };
  useEffect(() => {
    getShow(tableData, true);
  }, []);
  // 数据变化
  const valueChange = (e: any, key: string, record: any, type = 'input') => {
    let value = '';
    if (type === 'input') {
      value = e.target.value;
    } else {
      value = e;
    }
    updateTable(tableData, record.rowKey, key, value);
    getShow(tableData);
    getExpandRow(tableData);
  };
  // 数据更新后获取展开数据
  const getExpandRow = (data: any) => {
    let expandList = getDefaultExpandedRowKeys(data);
    setExpandedRowKeys(expandList);
  };
  // method类型切换
  const onChange = (value: string) => {
    setOption(value);
  };
  // 请求参数
  const requestParam = (httpInfo: any) => {
    const createHttpParam = {
      name: httpInfo.schema.name,
      version: '1.0.0',
      definitionGroups: [
        {
          name: httpInfo.schema.name,
          definitions: [
            {
              schema: httpInfo.schema,
            },
          ],
        },
      ],
      toolGroups: [
        {
          definitionGroupName: httpInfo.schema.name,
          name: httpInfo.schema.name,
          tools: [
            {
              runnables: httpInfo.runnables,
              extensions: httpInfo.extensions,
              definitionName: httpInfo.schema.name,
            },
          ],
        },
      ],
      icon: 'icon',
    };
    return createHttpParam;
  };
  // 确定
  const confirm = async (type: string) => {
    const formParams = await form.validateFields();
    let httpArr = formParams.url.split('/');
    let pathArr = httpArr.slice();
    pathArr.shift();
    httpInfo.runnables.HTTP.method = formParams.method;
    httpInfo.runnables.HTTP.domain = httpArr[0] || '';
    httpInfo.runnables.HTTP.pathPattern = pathArr.length ? `/${pathArr.join('/')}` : '';
    httpInfo.runnables.HTTP.protocol = protocol;
    let requestData = convertHttpMap(tableData);
    httpInfo.runnables.HTTP.mappings = requestData;
    const confirmHttpInfo = deepClone(httpInfo);
    let output = confirmHttpInfo.schema.return.properties.output;
    confirmHttpInfo.schema.return = output;
    setHttpInfo(httpInfo);
    if (type === 'prev') {
      setStepCurrent(1);
    } else {
      setLoading(true);
      let params = requestParam(confirmHttpInfo);
      createHttp(params)
        .then((res: any) => {
          if (res.code === 0) {
            window.history.back();
            setLoading(false);
          }
        })
        .finally(() => {
          setLoading(false);
        });
    }
  };

  useEffect(() => {
    if (httpInfo.schema?.name) {
      const HTTP = httpInfo.runnables?.HTTP;
      form.setFieldValue('method', HTTP?.method);
      form.setFieldValue('url', `${HTTP?.domain}${HTTP?.pathPattern}`);
    } else {
      form.setFieldValue('method', 'GET');
    }
  }, [httpInfo]);

  return (
    <div className='http-tool-input'>
      <div className='http-tool-title'>{t('connectConfiguration')}</div>
      <Form form={form} layout='vertical' autoComplete='off'>
        <div style={{ display: 'flex' }}>
          <Form.Item
            label={t('method')}
            name='method'
            rules={[{ required: true, message: t('methodMessage') }]}
          >
            <Select
              style={{ width: 96 }}
              defaultValue={option}
              options={options}
              onChange={onChange}
            />
          </Form.Item>
          <Form.Item
            label={t('URL')}
            name='url'
            rules={[{ required: true, message: t('urlMessage') }]}
            style={{ marginLeft: '8px' }}
          >
            <Input addonBefore={selectBefore} style={{ width: 696 }} maxLength={300} showCount />
          </Form.Item>
        </div>
      </Form>
      <div className='http-tool-title'>{t('paramConfiguration')}</div>
      <HttpTable
        columns={tableColumn}
        data={tableData}
        expandedRowKeys={expandedRowKeys}
        setExpandedRowKeys={setExpandedRowKeys}
      />
      <div className='http-tool-footer'>
        <Button onClick={() => confirm('prev')}>{t('previousStep')}</Button>
        <Button loading={loading} type='primary' onClick={() => confirm('next')}>
          {t('ok')}
        </Button>
      </div>
    </div>
  );
};

export default InformationConfiguration;
