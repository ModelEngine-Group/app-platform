/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect } from 'react';
import { Upload } from 'antd';
import type { UploadProps } from 'antd';
import JSZip from 'jszip';
import { bytesToSize } from '@/common/util';
import { getCookie } from '@/shared/utils/common';
import { Message } from '@/shared/utils/message';
import { fileValidate } from '@/shared/utils/common';
import { useTranslation } from 'react-i18next';
import { existDefs } from '@/shared/http/plugin';
import uploadImg from '@/assets/images/ai/upload.png';
import complateImg from '@/assets/images/ai/complate.png';
import deleteImg from '@/assets/images/ai/delete.png';
import './index.scoped.scss';

/**
 * 拖拽点击上传抽屉弹窗
 *
 * @fileList 解压文件数据列表
 * @addFileData 添加解压文件数据
 * @removeFileData 删除解压文件数据
 * @setRepeatData 解压文件获取重复工具数据
 */
const DraggerUpload = (props) => {
  const { t } = useTranslation();
  const [fileList, setFileList] = useState([]);
  const cLocale = getCookie('locale').toLocaleLowerCase();
  const fileKeys = ['plugin.json', 'tools.json', 'weather-1.0-SNAPSHOT.jar'];
  const fileTool = ['tools', 'name', 'definitionGroupName'];
  const fileToolDefinitionGroups = ['name', 'definitions'];
  const fileToolJSON = ['version', 'definitionGroups', 'toolGroups'];
  const regex = /^[a-zA-Z0-9\u4e00-\u9fa5][a-zA-Z0-9\u4e00-\u9fa5_-]*$/;

  // 上传文件默认行为
  const customRequest = async (val) => {
    let regex = /[\u4e00-\u9fa5]/;
    if (regex.test(val.file.name)) {
      Message({ type: 'warning', content: `${val.file.name} ${t('fileNameError')}` });
      return;
    }
    if (fileValidate(val.file, ['zip'], 100)) {
      val.onSuccess();
      let fileObj: any = {};
      let hasTool = false;
      const zip = new JSZip();
      const res = await zip.loadAsync(val?.file);
      fileObj[val.file.uid] = [];
      Object.keys(res.files).forEach((item, index) => {
        if (!res.files[item].dir && item.indexOf('tools.json') !== -1) {
          hasTool = true;
          res.file(item)?.async('blob').then((data) => {
              let fileStr = new File([data], item, { type: 'application/json' });
              fileStr.text().then((res) => {
                const toolJson: any = JSON.parse(res);
                try {
                  if (toolJson.version) {
                    missingFiles(toolJson, val, fileObj);
                  } else {
                    const fileJson = validatePlugin(toolJson, '');
                    if (fileJson) {
                      fileObj[val.file.uid].push(fileJson);
                      props.addFileData(fileObj, val.file);
                    }
                  }
                } catch {
                  if (!toolJson.version) {
                    let str = `${val.file.name} tools.json ${t('fileParseError')}`;
                    if (cLocale === 'en-us') {
                      str = `${t('fileParseError')} ${val.file.name} tools.json`;
                    }
                    Message({ type: 'warning', content: str });
                  }
                }
              });
            });
        }
      });
      if (!hasTool) {
        const noFiles = fileKeys.filter((item) => !Object.keys(res.files).includes(item));
        Message({ type: 'warning', content: t('toolError', { textName: noFiles }) });
      }
    }
  };

  // 判断数组里面是否有相同元素
  const containsDuplicate = (arr: any) => {
    let set = new Set(arr);
    if (arr.length !== set.size) {
      return true;
    } else {
      return false;
    }
  };

  // 判断定义组名是否重复
  const isDefinitionGroupsNameDuplicate = (item: any, type: string) => {
    if (item && type === 'definitionGroupName') {
      Message({ type: 'warning', content: t('defineGroupNameRepeat') });
      return
    }
    if (item && type === 'toolGroupsName') {
      Message({ type: 'warning', content: t('toolGroupNameRepeat') });
      return
    }
  };
  
  // 缺失文件判断
  const missingFiles = (toolJson: any, val: any, fileObj: any) => {
    let toolGroupsNameFormat: any = [];
    let definitionGroupNameFormat: any = [];
    let definitionNames: any = [];
    let toolGroupsTools: any = [];
    let definitionsInfo: any = [];
    let toolGroupsNameSplicing :any =[];
    let objEmpty = false;
    const filterToolJSON = fileToolJSON.filter((item) => !Object.keys(toolJson).includes(item));
    if (filterToolJSON.length === 0) {
      toolJson.definitionGroups.forEach((e: any) => {
        definitionGroupNameFormat.push(e.name);
        let filterDefinitionGroups = fileToolDefinitionGroups.filter((item) => !Object.keys(e).includes(item));
        definitionNames.push(filterDefinitionGroups);
        e.definitions && definitionsInfo.push(...e.definitions);
      });
      toolJson.toolGroups.forEach((e: any) => {
        toolGroupsNameFormat.push(e.definitionGroupName);
        toolGroupsNameSplicing.push(e.definitionGroupName.concat(e.name));
        let filterTools = fileTool.filter((item) => !Object.keys(e).includes(item));
        toolGroupsTools.push(...filterTools);
      });
      const toolGroupsFormat = toolGroupsNameFormat.find((item: any) => !regex.test(item));
      const definitionNull = definitionNames.find((item: any) => item);
      const definitionGroupFormat = definitionGroupNameFormat.find((item: any) => !regex.test(item));
      const toolGroupsNull = toolGroupsTools.find((item: any) => item);
      definitionGroupNameFormat.length > 0 && isDefinitionGroupsNameDuplicate(containsDuplicate(definitionGroupNameFormat), 'definitionGroupName');
      toolGroupsNameSplicing.length > 0 && isDefinitionGroupsNameDuplicate(containsDuplicate(toolGroupsNameSplicing), 'toolGroupsName');
      definitionsInfo.forEach((item: any) => {
        if (Object.keys(item).length === 0) {
          objEmpty = true;
          return;
        }
      });
      if (definitionNull && definitionNull.length > 0) {
        Message({
          type: 'warning',
          content: t('errorTips2', { textName: val.file.name, errorName: definitionNull }),
        });
      } else if (toolGroupsNull && toolGroupsNull.length > 0) {
        Message({
          type: 'warning',
          content: t('errorTips3', { textName: val.file.name, errorName: toolGroupsNull }),
        });
      } else if (toolGroupsFormat && toolGroupsFormat.length > 0) {
        Message({
          type: 'warning',
          content: t('errorTips4', { errorName: toolGroupsFormat }),
        });
      } else if (definitionGroupFormat && definitionGroupFormat.length > 0) {
        Message({
          type: 'warning',
          content: t('errorTips4', { errorName: definitionGroupFormat }),
        });
      } else if (objEmpty) {
        Message({
          type: 'warning',
          content: t('errorTips0', { textName: val.file.name }),
        });
      } else {
        customFile(toolJson, fileObj, val);
      }
    } else {
      Message({
        type: 'warning',
        content: t('errorTips1', { textName: val.file.name, errorName: filterToolJSON }),
      });
    }
  };

  // 解压包判断
  const customFile = (toolJson: any, fileObj: any, val: any) => {
    let definitionGroupNameArr: any = [];
    let toolGroupsArr: any = [];
    if (toolJson.version) {
      toolJson?.toolGroups?.forEach((item: any) => {
        toolJson?.definitionGroups.forEach((ite: any) => {
          if (ite.name === item.definitionGroupName) {
            definitionGroupNameArr.push(...ite.definitions);
            item.tools.forEach((e: any) => {
              if (e.definitionGroupName === '') {
                e.definitionGroupName = ite.name;
              } else {
                e.definitionGroupName = item.definitionGroupName;
              }
              e.name = item.name;
            });
            toolGroupsArr.push(...item.tools);
          }
        });
      });
      definitionGroupNameArr.forEach((Item: any) => {
        toolGroupsArr.forEach((eItem: any) => {
          if (Item.schema.name === eItem.definitionName) {
            eItem.schema.parameters.properties = Item.schema.parameters.properties;
          }
        });
      });
      const fileJson = validatePlugin(toolGroupsArr, 'tool');
      if (fileJson) {
        fileObj[val.file.uid].push(fileJson);
        props.addFileData(fileObj, val.file);
      }
    }
  };

  // 插件上传前格式校验
  const validatePlugin = (toolJson, type: string) => {
    const reg = /^[\u4e00-\u9fa5a-zA-Z0-9\s_-]+$/;
    let fileNameList: string[] = [];
    let toolsArr = toolJson.tools || toolJson;
    let confirmArr: object[] = [];
    let hasSameTool = false;
    let definitionGroupNameArr: any = [];
    toolsArr.forEach((item) => {
      let name = item.schema.name;
      let description = item.schema.description;
      if (name && name.trim().length > 0) {
        if (fileNameList.includes(name)) {
          hasSameTool = true;
        } else {
          fileNameList.push(name);
        }
        if (name.length < 65 && reg.test(name)) {
          confirmArr.push(item);
        }
      }
      type !== '' && definitionGroupNameArr.push(item.definitionGroupName);
    });
    const uniqueArr = definitionGroupNameArr.filter(
      (value: any, index: any, self: any) => self.indexOf(value) === index
    );
    confirmArr = confirmArr.slice(0, 100);
    if (hasSameTool) {
      Message({ type: 'warning', content: `${t('nameError')}` });
      return;
    }
    type !== '' && existDefs(uniqueArr).then((res: any) => {
      props.setRepeatData(res.data)
    });
    return {
      tools: confirmArr,
    };
  };

  // 删除解压文件
  const onRemove = (id) => {
    props.removeFileData(id);
  };

  // 解压文件上传前校验
  const beforeUpload = (file) => {
    let name = fileList.filter((item) => item.name === file.name)[0];
    if (name) {
      Message({ type: 'warning', content: `${file.name} ${t('fileUploaded')}` });
      return false;
    }
    return true;
  };

  // 上传参数
  const uploadProps: UploadProps = {
    name: 'file',
    customRequest,
    beforeUpload,
    showUploadList: false,
    ...props,
  };

  useEffect(() => {
    setFileList(props.fileList || []);
  }, [props.fileList]);

  return (
    <div>
      <Upload.Dragger {...uploadProps}>
        <p className='ant-upload-drag-icon'>
          <img width={32} height={32} src={uploadImg} />
        </p>
        <p className='ant-upload-text'>{t('fileUploadContent1')}</p>
        <p className='ant-upload-hint'>{t('fileUploadContent2')}</p>
        <p className='ant-upload-hint'>{t('fileUploadContent3')}</p>
      </Upload.Dragger>
      <div className='file-upload-list'>
        {fileList?.map((item) => (
          <div className='file-item' key={item.uid}>
            <div className='file-item-left'>
              <span className='file-name'>{item.name}</span>
              <span>({bytesToSize(item.size)})</span>
            </div>
            <div className='file-item-right'>
              <img src={complateImg} />
              <img src={deleteImg} onClick={() => onRemove(item.uid)} />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default DraggerUpload;
