/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { useParams } from 'react-router-dom';
import { toClipboard } from '@/shared/utils/common';
import { Drawer, Tabs, Tooltip } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import { markedProcess } from '@/pages/chatPreview/utils/marked-process';
import copyCodeIcon from '@/assets/images/ai/copy_code.png';
import {
  apiListData,
  urlMap,
  wssAPIData,
  oldWssAPIData,
  errorCodeData,
  resCode,
  resOKCode,
  reqWssData,
  resWssData,
  HTTPMap,
  reqOldWssData,
  resOldWssData,
  resOldSseData,
  oldSseAPIData,
  oldUrlMap,
} from '../overview/common/config';
import { v4 as uuidv4 } from 'uuid';
import './styles/api.scss';

/**
 * API文档
 *
 * @param drawerOpen 抽屉是否打开.
 * @param url apiUrl.
 * @param setDrawerOpen 抽屉是否打开的方法.
 * @return {JSX.Element}
 * @constructor
 */
const DocumentDrawer = ({ drawerOpen, url, setDrawerOpen }) => {
  const { t } = useTranslation();
  const { appId, tenantId } = useParams();
  const [documentContent, setDocumentContent] = useState([]);
  const [documentContentData, setDocumentContentData] = useState([]);
  const [currentNav, setCurrentNav] = useState('');
  const [getErrorData, setGetErrorData] = useState<any>([]);
  const [tabsKey, setTabsKey] = useState('1');
  const [oldTabsKey, setOldTabsKey] = useState('1');
  const documentRef = useRef<any>(null);
  let oldWebsocketUrl = [
    '<font color=#d0cdcd>/agent/v1/api/{tenant_id}/app_chat</font>',
    '<font color=#d0cdcd>/agent/v1/api/{tenant_id}/ws</font>',
  ];
  let data: any = [];
  let wssData: any = [];
  let arrError: any = [];
  let filterStr = ['OK_MSG', 'OK_CODE'];
  let errorCodeInfo: any = [];
  let wssReq: any = '';
  let wssRes: any = '';
  let oldWssReq: any = '';
  let oldWssRes: any = '';
  let reqCodes: any = '';
  let resCodes: any = '';
  let oldReqCodes: any = '';
  let oldResCodes: any = '';
  let fitlerRes = ['Request Body', 'Response Body', 'Websocket responses', 'Websocket Request'];
  let dataList: any = [];
  let oldWssData: any = [];
  const wssList = [t('north.api.chat.summary'), t('north.api.oldChat.summary')];
  const needWssResList = [
    `${t('north.api.chat.summary')}`,
    `${t('north.api.restartChat.summary')}`,
  ];

  // 转换代码格式
  const tranlateCode = (content) => {
    const regex = /<code>(.*?)<\/code>/g;
    return content.replace(
      regex,
      `<div class='inner-code'>
      <div class='inner-code-title'>Code</div>
      <div class='inner-code-content'>$1</div>
    </div>`
    );
  };

  // 展示当前appId
  const displayAppId = (content) => {
    const regex = /<placeholder>(.*?)<\/placeholder>/g;
    return content.replace(regex, `<span>${appId}</span>`);
  };

  // 获取请求方法代码
  const buildMethodCodeHtml = (codeContent: any, index: number) => {
    return (
      <div className='inner-code' key={index}>
        <div className='inner-code-title'>{codeContent.title}</div>
        {codeContent.description && getHighLightMethod(codeContent.description)}
        <div
          className='inner-code-content'
          dangerouslySetInnerHTML={{ __html: codeContent.code }}
        ></div>
      </div>
    );
  };

  // 高亮请求方法
  const getHighLightMethod = (content) => {
    const index = content.indexOf(' ');
    return (
      <div className='inner-code-menthod'>
        <span style={{ color: '#40acff', fontWeight: 700 }}>{content.substring(0, index)}</span>
        <span style={{ marginLeft: '8px' }}>{content.substring(index)}</span>
      </div>
    );
  };

  // 获取错误码
  const buildErrorCodeHtml = (errArr: any) => {
    return errArr.map((err: any, index: number) => (
      <div className='error-item' key={index}>
        <div className='circle-item'></div>
        <div dangerouslySetInnerHTML={{ __html: markedProcess(err.field) }}></div>
        <div>&nbsp;&nbsp;，</div>
        <div dangerouslySetInnerHTML={{ __html: markedProcess(err.description) }}></div>
      </div>
    ));
  };

  // 获取请求参数的内容
  const buildParamsContentHtml = (params, type = 'param', hierarchy = 1) => {
    return params.map((param: any, index: number) => (
      <div key={index} className={`${hierarchy > 1 ? 'meta-item' : 'param-item'}`}>
        {param.title ? (
          <div className='body-title'>{param.title}</div>
        ) : (
          <div>
            {param.query && <div className='param-query'>{param.query}</div>}
            {(param.field || param.type) && (
              <div className='param-top'>
                {type === 'meta' && <div className='circle-item'></div>}
                {param.field && (
                  <span
                    className={`${type === 'meta' ? 'meta' : ''}`}
                    dangerouslySetInnerHTML={{ __html: markedProcess(param.field) }}
                  ></span>
                )}
                {param.type && (
                  <span dangerouslySetInnerHTML={{ __html: markedProcess(param.type) }}></span>
                )}
              </div>
            )}
            {param.description && (
              <div
                className='param-content'
                dangerouslySetInnerHTML={{
                  __html: markedProcess(displayAppId(t(param.description))),
                }}
              ></div>
            )}
          </div>
        )}
        {param.children?.length > 0 && (
          <div style={{ padding: hierarchy > 1 ? '0 30px' : '0 16px' }}>
            {buildParamsContentHtml(param.children, 'meta', hierarchy + 1)}
          </div>
        )}
      </div>
    ));
  };

  // 附录的错误码数据
  const getErrorCodeData = () => {
    errorCodeData.forEach((item) => {
      if (item.code > 0) {
        let errorObj: any = {};
        errorObj.field = item.code;
        errorObj.description = t(item.message);
        arrError.push(errorObj);
      }
    });
  };

  // 打开抽屉初始化数据
  const getInitData = () => {
    processWssData(wssAPIData, true);
    processWssData(oldWssAPIData, false);
    processSSEData();
  };

  // 组装SSE数据
  const processSSEData = () => {
    let resRefField = '';
    let reqRefField = '';
    let urls = urlMap;
    if (process.env.PACKAGE_MODE === 'spa') {
      Object.assign(urls, oldUrlMap);
    }
    const urlList = Object.values(urls);
    urlList.forEach((item: any, index: number) => {
      let apiDataPaths = (apiListData.paths as any)[item];
      Object.keys(apiDataPaths).forEach((method: any) => {
        let obj: any = {};
        let methods = apiDataPaths[method];
        obj.id =
          methods.summary === `${t('north.api.chat.summary')}API`
            ? '_0'
            : methods.summary === t('north.api.oldChat.summary')
              ? '_1'
              : `_${uuidv4()}`;
        obj.method = methods.operationId.split(' ')[0];
        obj.url = `<font color=#d0cdcd>${methods.operationId.split(' ')[1]}</font>`;
        obj.title = t(methods.summary);
        obj.content = t(methods.description);
        obj.operationId = methods.operationId;
        obj.children = [];
        obj.codes = [];
        processSseData(obj, methods, resRefField, reqRefField);
      });
    });
  };

  // 组装wss数据
  const processWssData = (apiData, isNew) => {
    let wssObj: any = {};
    wssObj.title = t(apiData.info.title);
    wssObj.url = `<font color=#d0cdcd>${apiData.servers[0].url}</font>`;
    wssObj.method = 'WSS';
    wssObj.id = `_${isNew ? 0 : 1}`;
    wssObj.content = t(apiData.paths['/chat'].description);
    wssObj.children = [];
    wssObj.codes = [];
    wssObj.children.push({
      title: 'First Request',
      content: t(apiData.paths['/chat']['First Request'].summary),
    });
    if (!isNew) {
      const getRequestInfo = getRequest(
        oldWssAPIData.paths['/chat'].parameters || [],
        oldWssAPIData.servers[0].url
      );
      wssObj.children.push({ title: 'Request', children: getRequestInfo });
    }
    processWssObj(
      wssObj,
      apiData,
      isNew ? reqWssData : reqOldWssData,
      isNew ? resWssData : resOldWssData,
      isNew
    );
    isNew ? wssData.push(wssObj) : oldWssData.push(wssObj);
  };

  // 处理SSE的children以及codes数据
  const processSseData = (obj: any, methods: any, resRefField: string, reqRefField: string) => {
    const getRequestInfo = getRequest(methods.parameters || [], methods.operationId);
    obj.children.push({ title: 'Request', children: getRequestInfo });
    if (methods.requestBody) {
      let requestDescription = `${obj.method} ${methods.operationId.split(' ')[1]}`;
      let application = Object.keys(methods.requestBody.content).join('');
      let requestBodyStrs = methods.requestBody.content[application].schema.$ref;
      reqRefField = requestBodyStrs.substring(requestBodyStrs.lastIndexOf('/') + 1);
      const reqBody = processSseRequestAndResponse(reqRefField);
      const req = processSseCodes(reqRefField);
      reqBody.length > 0 && obj.children.push({ title: 'Request Body', children: reqBody });
      obj.codes.push({ title: 'Request', description: requestDescription, code: req });
    }
    if (methods.responses) {
      let statusCode = Object.keys(methods.responses).join('');
      let application = Object.keys(methods.responses[statusCode].content).join('');
      let responses = methods.responses[statusCode].content[application].schema.$ref;
      resRefField = responses.substring(responses.lastIndexOf('/') + 1);
      errorCodeInfo.push(statusCode);
      const res = processSseCodes(resRefField);
      let getResponseCode;
      if (needWssResList.includes(obj.title)) {
        getResponseCode = resCodes;
      } else if (obj.title === t('north.api.oldChat.summary')) {
        getResponseCode = processWssCodes(resOldSseData);
      } else {
        getResponseCode = res;
      }
      obj.codes.push({ title: 'Response', code: getResponseCode });
    }
    const getRes = processSseRequestAndResponse(resRefField);
    const getDatas = [
      ...(obj.title === t('north.api.oldChat.summary')
        ? oldTabsKey === '1'
          ? processWssRequestAndResponse(oldSseAPIData)
          : oldWssRes
        : wssRes),
      ...getRes,
    ];
    let getArr;
    if (needWssResList.includes(obj.title) || obj.title === t('north.api.oldChat.summary')) {
      getArr = getDatas;
    } else {
      getArr = getRes;
    }
    obj.children.push({ title: 'Response', children: [processStatusCodes(errorCodeInfo)] });
    obj.children.push({ title: 'Response Body', children: getArr });
    dataList.push(obj);
    const resObj = toggleSseWssData(obj);
    data.push(resObj);
  };

  // 切换SSE、WSS替换数据
  const toggleSseWssData = (el: any) => {
    if (!wssList.includes(el.title)) {
      return el;
    }
    if (el.title === t('north.api.oldChat.summary')) {
      return oldTabsKey === '1' ? el : oldWssData[0];
    } else {
      return tabsKey === '1' ? el : wssData[0];
    }
  };

  // 处理SSE中的request数据
  const getRequest = (item: any, operationId: any) => {
    item.forEach((its: any) => {
      its.query = its.in;
      its.content = t(its.schema.description);
      its.field = `<div class=api-drawer-title >${its.name}</div>`;
      its.type = `<font color=#d0cdcd>${its.schema?.type}</font>`;
      its.operationId = operationId;
    });
    return item;
  };

  // SSE状态码重组
  const processStatusCodes = (errorCodeInfo: any) => {
    let statusObj = { field: '', description: '' };
    let statusCodeArr: any = [];
    errorCodeInfo.forEach((item: any) => {
      resOKCode.forEach((key) => {
        statusObj.field = `<div class=api-drawer-title>${item}</div>`;
        if (Number(item) === key.code) {
          statusObj.description = t(key.message);
        }
      });
    });
    statusCodeArr.push(statusObj);
    resCode.forEach((key) => {
      let statusCodeObj: any = {};
      statusCodeObj.field = `<div class=api-drawer-title>${key.code}</div>`;
      statusCodeObj.description = t(key.message);
      statusCodeArr.push(statusCodeObj);
    });
    let obj = { field: '<div class=param-query>HttpStatus</div>', children: statusCodeArr };
    return obj;
  };

  // SSE的Response Body、Request Body重组
  const processSseRequestAndResponse = (newStr: string) => {
    let sseFilter = ['object', 'array'];
    const properties = (apiListData.components.schemas as any)[newStr].properties || {};
    let resArr: any = [];
    const res = Object.keys(properties).filter((item) => !filterStr.includes(item));
    res.forEach((key) => {
      let obj: any = {};
      obj.field = `<div class=api-drawer-title>${key}</div>`;
      if (properties[key].type && !sseFilter.includes(properties[key].type)) {
        obj.type = `<font color=#d0cdcd>${properties[key].type}</font>`;
      } else {
        if (properties[key].$ref) {
          let schemaRef = properties[key].$ref;
          let refStr = schemaRef.substring(schemaRef.lastIndexOf('/') + 1);
          obj.children = processSseRequestAndResponse(refStr);
        } else {
          if (properties[key].type === 'array' && properties[key]['items'].$ref) {
            let itemsRef = properties[key]['items'].$ref;
            let itemsStr = itemsRef.substring(itemsRef.lastIndexOf('/') + 1);
            if (newStr === itemsStr) {
              properties[key]['items'] = [];
            }
            obj.children = processSseRequestAndResponse(itemsStr);
          } else {
            obj.children = [];
            Object.keys(properties[key]).forEach((ite) => {
              let childObj: any = {};
              if (sseFilter.includes(properties[key][ite].type)) {
                if (ite !== 'type') {
                  childObj.field = `<div class=api-drawer-title>${ite}</div>`;
                }
                if (properties[key][ite].type) {
                  childObj.type = `<font color=#d0cdcd>${properties[key][ite].type}</font>`;
                }
                obj.children.push(childObj);
              }
            });
          }
        }
      }
      if (properties[key].description) {
        obj.description = t(properties[key].description);
      }
      resArr.push(obj);
    });
    return resArr;
  };

  // SSE的Codes重组
  const processSseCodes = (reqStr: string) => {
    let baseType = ['string', 'integer', 'boolean', 'object'];
    const properties = (apiListData.components.schemas as any)[reqStr].properties || {};
    const res = Object.keys(properties).filter((item) => !filterStr.includes(item));
    let str = Object.keys(properties).length > 0 ? '<code>{\n  ' : '';
    res.forEach((item, index) => {
      let val: any = '';
      if (properties[item].description) {
        val = t(properties[item].description);
      }
      if (properties[item].$ref || properties[item]['items']) {
        let propertiesStr = properties[item].$ref || properties[item]['items'].$ref;
        let refStr = propertiesStr.substring(propertiesStr.lastIndexOf('/') + 1);
        val = buildSseSchemaString(refStr);
      }
      const newVal = baseType.includes(properties[item].type) ? `"${val}",\n` : `${val}`;
      str += `  "${item}": ${newVal}  `;
      if (index === res.length - 1) {
        str += `}\n</code>`;
      }
    });
    return str;
  };

  // SSE拼接为字符串
  const buildSseSchemaString = (refStr: any, deep = 0) => {
    let sseFilterArr = ['object', 'array'];
    let space = String.fromCharCode(160);
    let propertiesKey = (apiListData.components.schemas as any)[refStr];
    let properties = propertiesKey.properties || {};
    let str = deep === 0 ? '{\n' : '';
    Object.keys(properties).forEach((item, index) => {
      let val = properties[item];
      if (Object.keys(val).length === 0) {
        str += `${space.repeat(6 + deep)}"${item}": "",\n`;
      } else if (!sseFilterArr.includes(val.type) && val.type) {
        let values = val['examples'] ? val['examples'][0] : '';
        str += `${space.repeat(6 + deep)}"${item}": "${values}"${item === Object.keys(properties).at(-1) ? '' : ','}\n`;
      } else {
        if (val.$ref) {
          let itemsRefStr = val.$ref;
          let childRefStr = itemsRefStr.substring(itemsRefStr.lastIndexOf('/') + 1);
          const res = buildSseSchemaString(childRefStr, deep + 2);
          str += `${space.repeat(6 + deep)}"${item}": ${res === '' ? ' {' : ' {\n'}`;
          str += res === '' ? '},\n' : res;
        } else if (val.type === 'array') {
          if (val.items.$ref) {
            let itemsStr = val.items.$ref;
            let childStr = itemsStr.substring(itemsStr.lastIndexOf('/') + 1);
            str += `${space.repeat(6 + deep)}"${item}":${refStr === childStr ? '[],' : '{'}\n`;
            if (refStr === childStr) {
              return;
            }
            const res = buildSseSchemaString(childStr, deep + 2);
            str += res;
            if (index === Object.keys(val).length) {
              str += `${space.repeat(6 + deep)}\n`;
            }
          } else {
            let valStr = '';
            Object.keys(val).forEach((key) => {
              if (key !== 'type' && key !== 'description') {
                valStr += `"${key}": {}`;
              }
            });
            str += `${space.repeat(6 + deep)}"${item}": [\n${space.repeat(8 + deep)}${valStr}\n${space.repeat(6 + deep)}],\n`;
          }
        } else {
          str += `${space.repeat(6 + deep)}"${item}": "",\n`;
        }
      }
      if (index === Object.keys(properties).length - 1) {
        str += `${space.repeat(4 + deep)}}\n`;
      }
    });
    return str;
  };

  // 获取文档内容
  const getDocument = () => {
    setDocumentContent(data);
  };

  // 滚动到对应API列表
  const scrollToItem = (itemId: string) => {
    document.querySelector(itemId)?.scrollIntoView(true);
  };

  const handleAnchorClick = (e, target) => {
    e.preventDefault();
    if (target) {
      setCurrentNav(target);
      document.querySelector(target)?.scrollIntoView(true);
    }
  };

  const handleScroll = () => {
    const sections = document.querySelectorAll('.document-markdown section');
    const navLinks = document.querySelectorAll('.document-nav div');
    sections.forEach((section) => {
      const sectionTop = section.offsetTop;
      const sectionHeight = section.clientHeight;
      const sectionBottom = sectionTop + sectionHeight;
      const scrollTop = documentRef.current.scrollTop;
      if (scrollTop >= sectionTop - 100 && scrollTop < sectionBottom - 200) {
        navLinks.forEach((link) => link.classList.remove('current-nav'));
        const link: any = document.getElementById(`${section.id}-link`);
        link?.classList.add('current-nav');
      }
    });
  };

  const items = [
    { label: t('sseMode'), key: '1' },
    { label: 'Websocket', key: '2' },
  ];

  // SSE、Websocket切换
  const onChangeTabs = (key: string) => {
    setTabsKey(key);
  };

  // 老版接口SSE、Websocket切换
  const onOldChangeTabs = (key: string) => {
    setOldTabsKey(key);
  };

  // 组装wss中children以及codes数据
  const processWssObj = (obj: any, apiData: any, reqData: any, resData: any, isNew: boolean) => {
    let filterField = ['summary', 'description'];
    const pathsData: any = apiData.paths['/chat'];
    const res = Object.keys(pathsData).filter((key) => !filterField.includes(key));
    res.forEach((item) => {
      let wssObj: any = {};
      if (pathsData[item]['summary']) {
        wssObj.field = pathsData[item].summary;
      }
      if (pathsData[item]['description']) {
        wssObj.description = t(pathsData[item].description);
      }
      if (pathsData[item]['requestBody']) {
        wssObj.children = [];
        let pathsReqInfo = pathsData[item]['requestBody'];
        let appReqJson = Object.keys(pathsReqInfo.content).join('');
        let schemaReq = Object.keys(pathsReqInfo.content[appReqJson]).join('');
        let recursionData = pathsReqInfo.content[appReqJson][schemaReq];
        isNew
          ? (wssReq = processWssRequestAndResponse(recursionData || {}))
          : (oldWssReq = processWssRequestAndResponse(recursionData || {}));
        isNew ? (reqCodes = processWssCodes(reqData)) : (oldReqCodes = processWssCodes(reqData));
        obj.children.push({
          title: 'Websocket Request',
          content: t(pathsData[item]['description']),
          children: isNew ? wssReq : oldWssReq,
        });
        obj.codes.push({
          title: 'Websocket Request',
          code: reqCodes.join(''),
        });
      }
      if (pathsData[item]['content']) {
        wssObj.children = [];
        let pathsResData = pathsData[item]['content'];
        let appResJson = Object.keys(pathsResData).join('');
        let schemaRes = Object.keys(pathsResData[appResJson]).join('');
        let wssResData = pathsResData[appResJson][schemaRes];
        isNew
          ? (wssRes = processWssRequestAndResponse(wssResData))
          : (oldWssRes = processWssRequestAndResponse(wssResData));
        isNew ? (resCodes = processWssCodes(resData)) : (oldResCodes = processWssCodes(resData));
        obj.children.push({
          title: 'Websocket responses',
          content: t(pathsData[item]['description']),
          children: isNew ? wssRes : oldWssRes,
        });
        obj.codes.push({
          title: 'Websocket responses',
          code: isNew ? resCodes.join('') : oldResCodes.join(''),
        });
      }
    });
  };

  // 递归webSorcket的JSON数据
  const processWssRequestAndResponse = (data: any) => {
    let wssFilter = ['object', 'array'];
    let newArr: any = [];
    const properties = data['properties'] || {};
    Object.keys(properties).forEach((key) => {
      let obj: any = {};
      obj.field = `<div class=api-drawer-title>${key}</div>`;
      if (properties[key].type) {
        obj.type = `<font color=#d0cdcd>${properties[key].type}</font>`;
      }
      if (properties[key].description) {
        obj.description = t(properties[key].description);
      }
      if (wssFilter.includes(properties[key].type)) {
        let propertiesData = {};
        if (properties[key].type === 'object') {
          propertiesData = properties[key];
        } else {
          propertiesData = properties[key]['items'];
        }
        obj.children = processWssRequestAndResponse(propertiesData);
      }
      newArr.push(obj);
    });
    return newArr;
  };

  // 拼接wss中的codes
  const processWssCodes = (e: any) => {
    let codeStr: any = [];
    let wssFilterCodes = ['object', 'array'];
    e.forEach((key: any) => {
      let str = '<code> {\n  ';
      Object.keys(key).forEach((item, index) => {
        let val = '';
        let space = String.fromCharCode(160);
        if (wssFilterCodes.includes(typeof key[item]) && key[item]) {
          val = buildWssCodesString(key[item]);
        } else {
          val = key[item];
        }
        let newVal = !wssFilterCodes.includes(typeof key[item]) ? `"${val}"` : val;
        str += `${space.repeat(2)}"${item}": ${newVal},\n${space.repeat(2)}`;
        if (index === Object.keys(key).length - 1) {
          str += `}\n</code>`;
        }
      });
      codeStr.push(str);
    });
    return codeStr;
  };

  // 递归wss中codes字符拼接的内容
  const buildWssCodesString = (e: any, deep = 0) => {
    const space = (n: number) => String.fromCharCode(160).repeat(n);
    if (Array.isArray(e)) {
      let str = '[\n';
      e.forEach((item, idx) => {
        str += space(4 + deep * 2) + buildWssCodesString(item, deep + 1);
        if (idx < e.length - 1) str += ',\n';
      });
      str += '\n' + space(deep * 2) + ']';
      return str;
    } else if (typeof e === 'object' && e !== null) {
      let str = '{\n';
      const keys = Object.keys(e);
      if (keys.length === 0) return '{}';
      keys.forEach((key, idx) => {
        let val = e[key];
        let valStr =
          typeof val === 'object' && val !== null ? buildWssCodesString(val, deep + 1) : `"${val}"`;
        str += space(4 + deep * 2) + `"${key}": ${valStr}`;
        if (idx < keys.length - 1) str += ',\n';
      });
      str += '\n' + space(deep * 2) + '}';
      return str;
    } else {
      return `"${e}"`;
    }
  };

  // 关闭抽屉
  const closeDrawer = () => {
    setDrawerOpen(false);
    setTabsKey('1');
  };

  useEffect(() => {
    if (documentContent.length) {
      documentContent.forEach((item: any) => {
        let itemId = `#${item.id}`;
        document.querySelector(itemId)?.addEventListener('click', () => scrollToItem(itemId));
      });
    }
  }, [documentContent]);

  useEffect(() => {
    if (drawerOpen) {
      getDocument();
      getInitData();
      setDocumentContentData(dataList);
      getErrorCodeData();
      setGetErrorData(arrError);
      documentRef.current.addEventListener('scroll', handleScroll);
    } else {
      setDocumentContent([]);
    }
  }, [drawerOpen, tabsKey, oldTabsKey]);

  useEffect(() => {
    return () => {
      documentContent.forEach((item: any) => {
        let itemId = `#${item.id}`;
        document.querySelector(itemId)?.removeEventListener('click', () => scrollToItem(itemId));
      });
    };
  }, []);

  return (
    <Drawer
      title={t('ApiDocument')}
      width={1100}
      open={drawerOpen}
      onClose={closeDrawer}
      closable={false}
      className='api-drawer'
      extra={<CloseOutlined onClick={closeDrawer} />}
    >
      <div className='document-nav'>
        <div
          id='first-link'
          className={`nav-item ${currentNav === '#first' ? 'current-nav' : ''}`}
          onClick={(e) => handleAnchorClick(e, '#first')}
        >
          {t('basic')}URL
        </div>
        {process.env.PACKAGE_MODE === 'spa' && (
          <div
            id='header-link'
            className={`nav-item ${currentNav === '#header' ? 'current-nav' : ''}`}
            onClick={(e) => handleAnchorClick(e, '#header')}
          >
            {t('auth')}
          </div>
        )}
        <div className='nav-item'>API{t('list')}</div>
        {documentContentData.map((item: any) => {
          return (
            <Tooltip title={item.title} placement='left' key={item.id}>
              <div
                id={`${item.id}-link`}
                className={`nav-item-second ${currentNav === `#${item.id}` ? 'current-nav' : ''}`}
                onClick={(e) => handleAnchorClick(e, `#${item.id}`)}
              >
                {item.title}
              </div>
            </Tooltip>
          );
        })}
        <Tooltip title={t('errorCodes')} placement='left'>
          <div
            id='error-link'
            className={`nav-item ${currentNav === '#error' ? 'current-nav' : ''}`}
            onClick={(e) => handleAnchorClick(e, '#error')}
          >
            {t('errorCodes')}
          </div>
        </Tooltip>
      </div>
      <div className='document-markdown' ref={documentRef}>
        <div id='first'>
          <div className='content-title'>{t('basic')}URL</div>
          <div className='inner-code'>
            <div className='inner-code-title'>Code</div>
            <div className='inner-code-content inner-code-flex'>
              <span>{url}</span>
              <div style={{ cursor: 'pointer' }} onClick={() => toClipboard(url)}>
                <img src={copyCodeIcon} style={{ marginRight: 8 }} alt='' />
                <span>Copy</span>
              </div>
            </div>
          </div>
          <div className='inner-code'>
            <div className='inner-code-title'>Websocket</div>
            <div className='inner-code-content inner-code-flex'>
              <span>{url.replace(HTTPMap.http, 'ws')}</span>
              <div
                style={{ cursor: 'pointer' }}
                onClick={() => toClipboard(url.replace(HTTPMap.http, 'ws'))}
              >
                <img src={copyCodeIcon} style={{ marginRight: 8 }} alt='' />
                <span>Copy</span>
              </div>
            </div>
          </div>
        </div>
        {process.env.PACKAGE_MODE === 'spa' && (
          <div id='header'>
            <div className='content-title'>{t('auth')}</div>
            <div
              dangerouslySetInnerHTML={{ __html: markedProcess(tranlateCode(t('authTips'))) }}
            ></div>
          </div>
        )}
        <div>
          <div className='content-title'>{t('appInformation')}</div>
          <div>
            <span>tennatId：</span>
            {tenantId}
          </div>
          <div className='content-id'>
            <span>appId：</span>
            {appId}
          </div>
        </div>
        {documentContent.map((item: any, index) => (
          <section
            key={index}
            id={item.id}
            className={`item-content ${item.method ? 'method-item' : ''}`}
          >
            <div style={{ width: '100%' }}>
              <div
                className='content-title'
                dangerouslySetInnerHTML={{ __html: markedProcess(item.title) }}
              ></div>
              <div
                className='content-description'
                dangerouslySetInnerHTML={{ __html: markedProcess(tranlateCode(item.content)) }}
              ></div>
              {oldWebsocketUrl.includes(item.url) && (
                <div>
                  <Tabs
                    defaultActiveKey='1'
                    onChange={onOldChangeTabs}
                    items={items}
                    activeKey={oldTabsKey}
                  />
                </div>
              )}
              {item.method && (
                <div className='method-title'>
                  <div className='method-name'>{item.method}</div>
                  <span dangerouslySetInnerHTML={{ __html: item.url }}></span>
                </div>
              )}
              {item.children?.length > 0 &&
                item.children.map((body: any, i: number) => {
                  let res = fitlerRes.includes(body.title);
                  return (
                    <div key={i} className='method-body'>
                      <div style={{ width: item.codes.length ? 'calc(50% - 10px)' : '100%' }}>
                        <div className='body-title'>{body.title}</div>
                        {body.content && (
                          <div
                            className='body-description'
                            dangerouslySetInnerHTML={{
                              __html: markedProcess(tranlateCode(body.content)),
                            }}
                          ></div>
                        )}
                        {body.children?.length > 0 &&
                          (body.isError
                            ? buildErrorCodeHtml(body.children)
                            : buildParamsContentHtml(body.children))}
                      </div>
                      {res && (
                        <div key={i} style={{ width: '50%' }}>
                          {item.codes.map((code: any, idx: number) => {
                            let codeRes =
                              body.title === 'Request Body'
                                ? 'Request'
                                : body.title === 'Response Body'
                                  ? 'Response'
                                  : body.title;
                            return code.title === codeRes && buildMethodCodeHtml(code, idx);
                          })}
                        </div>
                      )}
                    </div>
                  );
                })}
            </div>
          </section>
        ))}
        <div id='error'>
          <div className='content-title error-title'>{t('errorCodes')}</div>
          <div>{getErrorData.length > 0 && buildErrorCodeHtml(getErrorData)}</div>
        </div>
      </div>
    </Drawer>
  );
};

export default DocumentDrawer;
