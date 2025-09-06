/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useRef, useState } from 'react';
import { CloseOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import { Button, Checkbox, Drawer, Select, Table, Tag, Spin, Empty, Tooltip } from 'antd';
import { GetProp } from 'antd/lib';
import { v4 as uuidv4 } from 'uuid';
import DraggerUpload from '@/pages/plugin/draggerUpload';
import RepeatTools from './repeatTools';
import { uploadPlugin } from '@/shared/http/plugin';
import { Message } from '@/shared/utils/message';
import { recursion } from '../../helper';
import { useTranslation } from 'react-i18next';
import i18n from '@/locale/i18n';
import knowledgeImg from '@/assets/images/knowledge/knowledge-base.png';
import complateImg from '@/assets/images/ai/complate.png';
import infoUploadImg from '@/assets/images/ai/info-upload.png';
import downImg from '@/assets/images/ai/down.png';
import PluginWarningImg from '@/assets/images/plugin/plugin-warning.png';
import '../style.scoped.scss';

const uploadSpaceOptions = [
  { value: 'user', label: i18n.t('personalSpace') },
  { value: 'team', label: i18n.t('aTeam') },
];

const columns = [
  {
    title: i18n.t('paramName'),
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: i18n.t('paramType'),
    dataIndex: 'type',
    key: 'type',
  },
  {
    title: i18n.t('paramDescription'),
    dataIndex: 'description',
    key: 'description',
  },
];

const UploadToolDrawer = ({ openSignal, refreshPluginList }) => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [result, setResult] = useState([]);
  const [checkedList, setCheckedList] = useState<any>([]);
  const [fileList, setFileList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [repeatData, setRepeatData] = useState([]);
  const [isShow, setIsShow] = useState(false);
  const pluginList = useRef([]);
  const fileData = useRef([]);
  const tipsStr = t('zipDescription');
  const onCheckChange: GetProp<typeof Checkbox.Group, 'onChange'> = (checkedValues) => {
    pluginList.current.forEach(pItem => {
      pItem.checkedNum = 0;
      checkedValues.forEach(cItem => {
        if (cItem.uid === pItem.key) {
          pItem.checkedNum += 1
        }
      })
    });
    setResult([...pluginList.current]);
    setCheckedList(checkedValues);
  };

  const onChangeSpace = (value) => { };
  // 添加数据
  const addFileData = (data, file) => {
    if (fileData.current.length > 4) {
      Message({ type: 'warning', content: t('maxUploadTips') })
      return;
    };
    fileData.current = [file];
    setFileList(fileData.current);
    Object.keys(data).forEach(key => {
      data[key].forEach(item => {
        let pluginObj: any = {
          open: true,
          checkedNum: 0
        };
        pluginObj.key = key;
        pluginObj.name = file.name;
        if (item.tools && Array.isArray(item.tools)) {
          let list = item.tools.map(tItem => {
            return {
              ...tItem,
              uid: key,
              open: false,
              id: uuidv4(),
              parameterEntities: setTableData(tItem.schema?.parameters)
            }
          });
          pluginObj.list = list;
        }
        pluginList.current = [pluginObj];
        setResult([...pluginList.current]);
      });
    });
    setCheckedList([]);
  }
  // 设置表格数据
  const setTableData = (data) => {
    let list = [];
    if (data.properties) {
      Object.keys(data.properties).forEach(item => {
        let aItem = { ...data.properties[item], name: item };
        list.push(aItem);
      })
    }
    return list
  }
  // 删除数据
  const removeFileData = (uid) => {
    pluginList.current = pluginList.current.filter((item) => item.key !== uid);
    let checkList = checkedList.filter((item) => item.uid !== uid);
    fileData.current = fileData.current.filter((item) => item.uid !== uid);
    setResult(pluginList.current);
    setCheckedList(checkList);
    setFileList(fileData.current);
    setRepeatData([]);
  };
  // 确定
  const confirm = () => {
    if (!checkedList.length) {
      Message({ type: 'warning', content: t('noPluginSelected') });
      return
    }
    let nameArr: any = [];
    let uidArr: any = [];
    checkedList.forEach((item: any) => {
      if (item.definitionGroupName) {
        nameArr.push(
          `${item.definitionGroupName}.${item.name}.${item.definitionName}.${item.schema.name}`
        );
      } else {
        nameArr.push(item.schema.name);
      }
      !uidArr.includes(item.uid) && uidArr.push(item.uid);
    });
    let fileConfirmList = fileData.current.filter(item => uidArr.includes(item.uid));
    customRequest(fileConfirmList, nameArr);
  }
  // 全选反选
  const pluginCheckAll = (e, item) => {
    e.stopPropagation();
    let pItemIndex = pluginList.current.findIndex(fItem => fItem.key === item.key);
    let checkAllItem = pluginList.current[pItemIndex];
    let checkedIdList = checkedList.map(item => item.id);
    let checkNum = checkAllItem.checkedNum || 0;
    if (checkNum === checkAllItem.list.length) {
      checkAllItem.checkedNum = 0;
      setCheckedList([]);
    } else {
      checkAllItem.checkedNum = checkAllItem.list.length;
      checkAllItem.list.forEach(item => {
        if (!checkedIdList.includes(item.id)) {
          checkedList.push(item);
        }
      });
      setCheckedList(checkedList);
    };
    setResult([...pluginList.current]);
  }
  // 上传文件
  const customRequest = (fileArr, nameArr) => {
    setLoading(true);
    let formData = new FormData();
    fileArr.forEach(item => {
      formData.append('file', item);
    });
    uploadPlugin(formData, nameArr).then(res => {
      if (res.code === 0) {
        setOpen(false);
        Message({ type: 'success', content: t('operationSucceeded') });
        refreshPluginList();
      } else {
        setLoading(false);
      }
    }).catch(() => {
      setLoading(false);
    });
  }
  const itemClick = (item) => {
    pluginList.current.forEach(pItem => {
      if (item.key === pItem.key) {
        pItem.open = !pItem.open
      }
    })
    setResult([...pluginList.current]);
  }
  const pluginDetailClick = (item, lItem) => {
    let pItemIndex = pluginList.current.findIndex(fItem => fItem.key === item.key);
    pluginList.current[pItemIndex].list.forEach(cItem => {
      if (cItem.id === lItem.id) {
        cItem.open = !cItem.open
      }
    });
    setResult([...pluginList.current]);
  }
  const dataReset = () => {
    pluginList.current = [];
    fileData.current = [];
    setOpen(true);
    setResult([]);
    setCheckedList([]);
    setFileList([]);
    setLoading(false);
    setRepeatData([]);
  };
  useEffect(() => {
    if (openSignal > 0) {
      dataReset();
    }
  }, [openSignal]);

  return (
    <Drawer
      title={t('uploadTool')}
      placement='right'
      closeIcon={false}
      width={650}
      open={open}
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
            style={{ width: 90 }}
            disabled={loading}
            onClick={() => {
              setOpen(false);
            }}
          >
            {t('cancel')}
          </Button>
          <Button
            style={{ width: 90, backgroundColor: '#2673e5', color: '#ffffff' }}
            onClick={confirm}
            loading={loading}
          >
            {t('ok')}
          </Button>
        </div>
      }
    >
      <div className='upload-info-head'>
        <img src={infoUploadImg} />
        <span>{t('uploadTip')}</span>
      </div>
      <Spin tip={t('uploadingPlugin')} size='small' spinning={loading}>
        <div>
          {t('uploadTo')} 
          <Select
            disabled={true}
            defaultValue='user'
            className='select-space'
            onChange={onChangeSpace}
            options={uploadSpaceOptions}
          />
          <Tooltip
            color='#ffffff'
            placement='right'
            overlayInnerStyle={{ color: '#212121', padding: '12px', lineHeight: '24px' }}
            title={tipsStr}
            trigger='click'
          >
            <QuestionCircleOutlined style={{ fontSize: '18px', marginLeft: '12px' }} />
          </Tooltip>
          <DraggerUpload
            accept='.zip'
            addFileData={addFileData}
            fileList={fileList}
            removeFileData={removeFileData}
            style={{ margin: '10px 0' }}
            setRepeatData={setRepeatData}
          />
          {repeatData.length > 0 && (
            <div className='upload-repeat-tips'>
              <span>
                <img
                  src={PluginWarningImg}
                  style={{ marginRight: '8px' }}
                />
                {t('defineGroupTips')}
              </span>
              <span
                onClick={() => setIsShow(true)}
                style={{ color: 'rgb(38,115,229)', cursor: 'pointer' }}
              >
                {t('checkMore')}
              </span>
            </div>
          )}
          <Checkbox.Group style={{ width: '100%' }} value={checkedList} onChange={onCheckChange}>
            {
              result?.map(item => (
                <div className={['collapse-plugin', item.open ? 'collapse-plugin-open' : ''].join(' ')} key={item.key}>
                  <div className='collapse-plugin-head' onClick={() => itemClick(item)}>
                    <div className='head-left'>
                      <img src={downImg} />
                      <span>{item.name}</span>
                    </div>
                    <div className='head-right'>
                      {item?.list?.length > 0 && <img src={complateImg} />}
                      <span className='text'>{item?.list?.length > 0 ? t('parsedSuccessfully') : t('parsingFailed')}</span>
                      <span>{item?.list?.length}/{item.checkedNum}</span>
                      <span className='check-text' onClick={(e) => pluginCheckAll(e, item)}>{item.checkedNum === item?.list?.length ? '取消全选' : '全选'}</span>
                    </div>
                  </div>
                  <div className='collapse-plugin-content'>
                    {item?.list?.length === 0 && <Empty description={t('noData')} />}
                    {item.list?.map((lItem, index) => (
                      <div className='param-card' key={index}>
                        <div style={{ float: 'right' }}>
                          <Checkbox value={lItem} />
                        </div>
                        <div className='card-header-left'>
                          <img src={knowledgeImg} />
                          <div>
                            <div style={{ fontSize: 20, marginBottom: 8, wordBreak: 'break-all' }}>{lItem.schema?.name}</div>
                            <div className='card-user'>
                              {lItem?.tags?.map((tag: string, index: number) => {
                                if (tag.trim().length > 0) {
                                  return <Tag style={{ margin: 0 }} key={index}> {tag}</Tag>
                                }
                              })}
                              <span className='card-detail-btn' onClick={() => pluginDetailClick(item, lItem)}>{t('viewParam')}</span>
                            </div>
                          </div>
                        </div>
                        <div className='card-des'>{lItem?.schema?.description}</div>
                        <div className='card-table' style={{ display: lItem.open ? 'block' : 'none' }}>
                          {recursion(lItem?.parameterEntities) as any}
                          <Table
                            scroll={{ y: 120 }}
                            dataSource={lItem?.parameterEntities}
                            columns={columns}
                            rowKey='name'
                            pagination={false}
                            scroll={{ y: 200 }}
                          />
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              ))
            }
          </Checkbox.Group>
        </div>
      </Spin>
      {isShow && <RepeatTools isShow={isShow} setIsShow={setIsShow} repeatData={repeatData} />}
    </Drawer>
  );
};

export default UploadToolDrawer;
