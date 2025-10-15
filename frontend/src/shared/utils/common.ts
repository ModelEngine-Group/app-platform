import { useLocation } from 'react-router-dom';
import { useCallback, useMemo, useState } from 'react';
import { pick, find, filter } from 'lodash';
import { Message } from '@/shared/utils/message';
import { storage } from '../storage';
import i18n from '@/locale/i18n';
import { getAppCategories } from "@/shared/http/aipp";
import { nanoid } from "nanoid";

/**
 * 获取url中status的类型
 *
 * @param name status类型
 * @returns {string} status的值
 */
export const getQueryStatus = (name) => {
  return (() => {
    const { search } = useLocation();
    return useMemo(() => new URLSearchParams(search), [search]);
  })().get(name);
};

/**
 * 获取url指定位置的path变量
 *
 * @param index 数组下表
 * @returns {*}
 */
export const getQueryVariableByIndex = (index) => {
  const query = window.location.pathname.split('/');
  return query[index];
}

/**
 * 根据变量名获取url的path变量
 *
 * @param props
 * @param variable path变量
 * @returns {*}
 */
export const getUrlVariableByName = (props, variable) => {
  return props.match.params[variable];
}

/**
* 使用合并状态的自定义 Hook
* @param {Object} initialState - 初始状态
* @return {Array} 返回一个数组，包含当前状态和一个用于更新状态的函数
*/
export const useMergeState = (initialState) => {
  const [state, setState] = useState(initialState);
  const setMergeState = useCallback((newState) => setState((prevState) => ({ ...prevState, ...newState })), []);
  return [state, setMergeState];
};


/**
* 将传入的文本进行转换处理，去除空格后，如果长度不为0，则进行marked解析，否则返回空字符串
*
* @param {string} text - 需要进行转换的文本
* @return {string} 返回处理后的文本，如果文本为空或者为空字符串，则返回空字符串
*/
export const trans = (text) => {
  text = urlify(text);
  try {
    let textItem = JSON.parse(text);
    let textHtml = setFileType(textItem);
    if (textHtml.length) {
      return textHtml;
    }
    return text.replaceAll('<br>', '');
  } catch {
    if (text?.trim().length) {
      return text.replaceAll('<br>', '');
    }
  }
  return '';
}

// 消息类型判断
const setFileType = (textItem) => {
  let isObject = Object.prototype.toString.call(textItem) === '[object Object]';
  let isArray = Object.prototype.toString.call(textItem) === '[object Array]';
  let htmlStr = ''
  if (isObject) {
    if (textItem.mime.indexOf('image') !== -1) {
      htmlStr = `<img src=data:${textItem.mime};base64,${textItem.data} />`
    } else if (textItem.mime.indexOf('audio') !== -1) {
      htmlStr = `<audio src=data:${textItem.mime};base64,${textItem.data} controls></audio>`
    }
  } else if (isArray) {
    textItem.forEach(item => {
      if (item.mime.indexOf('image') !== -1) {
        htmlStr += `<img src=data:${item.mime};base64,${item.data} /><br />`
      } else if (item.mime.indexOf('audio') !== -1) {
        htmlStr += `<audio src=data:${item.mime};base64,${item.data} controls></audio><br />`
      }
    })
  }
  return htmlStr;
}

// 日期方法
export const formatDateTime = (dateString) => {
  if (!dateString) {
    return '-'
  }
  let date = new Date(dateString);
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hour = date.getHours();
  const minute = date.getMinutes();
  const second = date.getSeconds();
  return `${year}-${pad(month)}-${pad(day)} ${pad(hour)}:${pad(minute)}:${pad(
    second
  )}`;
}
const pad = (num) => {
  return num.toString().padStart(2, '0');
}
/**
* 获取一个随机生成的唯一ID
*
* @export
* @return {string} 返回一个由随机数生成的唯一ID
*/
export const getUiD = () => {
  const random = window.crypto || window.msCrypto;
  let arrayList = new Uint32Array(3);
  random.getRandomValues(arrayList);
  return random.getRandomValues(arrayList).join('');
}
/**
* 将指定值复制到剪贴板
*
* @param {string} val - 需要复制的值
* @throws {Error} 如果无法访问剪贴板，将抛出错误
*/
export const toClipboard = (val) => {
  if (navigator.clipboard && navigator.permissions && window.self === window.top) {
    navigator.clipboard.writeText(val);
    Message({ type: 'success', content: i18n.t('copySucceeded') });
  } else {
    let textArea = document.createElement('textArea');
    textArea.value = val;
    textArea.style.width = 0;
    textArea.style.position = 'fixed';
    textArea.style.left = '-999px';
    textArea.style.top = '10px';
    textArea.setAttribute('readonly', 'readonly');
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand('copy');
    document.body.removeChild(textArea);
    Message({ type: 'success', content: i18n.t('copySucceeded') })
  }
}
/**
* 防抖函数，用于限制高频触发事件的执行频率
*
* @export
* @param {Function} fn 需要防抖的函数
* @param {number} wait 防抖的时间间隔
* @return {Function} 返回一个新的函数，该函数在指定的时间间隔内只执行一次
*/
export const debounce = (fn, wait) => {
  let timer
  return (...args) => {
    const context = this
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      timer = null
      fn.apply(context, args)
    }, wait)
  }
}
/**
* 判断一个字符串是否为合法的JSON字符串
*
* @param {string} str - 需要判断的字符串
* @return {boolean} 如果是合法的JSON字符串，返回true，否则返回false
*/
export const isJsonString = (str) => {
  try {
    if (typeof JSON.parse(str) === 'object') {
      return true;
    }
  } catch (e) {
    return false;
  }
  return false;
}

/**
* 将文本中的URL转换为链接的HTML格式
*
* @param {string} text - 需要转换的文本
* @return {string} 返回转换后的HTML格式文本
*/
export const urlify = (text) => {
  if (text) {
    const urlRegex = /(\b(https?):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
    return text.replace(urlRegex, (url) => {
      return `<a href='${url}' target='_blank'>${url}</a>`;
    })
  }
  return text
}

/**
* 版本号比较函数
* @param {string} preVersion - 需要比较的版本号，默认为空字符串
* @param {string} lastVersion - 参照的版本号，默认为空字符串
* @return {number} 返回比较结果，-1表示preVersion小于lastVersion，1表示preVersion大于lastVersion，0表示两者相等
*/
export const versionStringCompare = (preVersion = '', lastVersion = '') => {
  let sources = preVersion.split('.');
  let dests = lastVersion.split('.');
  let maxL = Math.max(sources.length, dests.length);
  let result = 0;
  for (let i = 0; i < maxL; i++) {
    let preValue = sources.length > i ? sources[i] : 0;
    let preNum = isNaN(Number(preValue)) ? preValue.charCodeAt() : Number(preValue);
    let lastValue = dests.length > i ? dests[i] : 0;
    let lastNum = isNaN(Number(lastValue)) ? lastValue.charCodeAt() : Number(lastValue);
    if (preNum < lastNum) {
      result = -1;
      break;
    } else if (preNum > lastNum) {
      result = 1;
      break;
    }
  }
  return result;
}

/**
 * 更新浏览器缓存里的应用会话Id
 * @param {String} chatId 会话Id
 * @param {String} appId 应用Id
 * @Return void
 */
export const updateChatId = function (chatId, appId) {
  let appChatMap = storage.get('appChatMap') || {};
  appChatMap[appId] = { chatId: chatId };
  storage.set('appChatMap', appChatMap);
};


/**
 * 获取各个配置信息的值，作为更新应用配置的参数
 * @param {Object} sourceData 应用详情的configFormProperties参数
 * @param {Object} target 最后扁平化的配置对象
 * @return {Object|Array|String}
 */
export const getConfigValue = (sourceData, target = {}) => {
  return (sourceData || []).reduce((acc, item) => {
    if (!item) return acc;
    acc[item.name] = pick(item, ['dataType', 'defaultValue', 'id', 'name', 'nodeId', 'from']);
    return item.children ? {
      ...getConfigValue(item.children, acc),
      ...acc
    } : acc;
  }, { ...target });
};

/**
 * 获取应用某一个配置项的值
 * @param {Object} appInfo 应用详情
 * @param {String} name 需要获取的配置的名称
 * @return {Object|Array|String}
 */
export const findConfigValue = (appInfo, name) => {
  if (!appInfo.configFormProperties || !appInfo.flowGraph?.appearance) {
    return null;
  }
  const configItem = findConfigItem(appInfo.configFormProperties, name);
  if (!configItem) {
    return null;
  }
  if (configItem.from === 'graph') {
    const graphOperator = createGraphOperator(JSON.stringify(appInfo.flowGraph.appearance));
    return graphOperator.getConfig(configItem.defaultValue);
  }
  return configItem.defaultValue;
};

/**
 * 获取应用某一个配置项
 * @param {Array} configStructure 配置结构
 * @param {String} configName 需要获取的配置的名称
 * @return {Object}
 */
export const findConfigItem = (configStructure, configName) => {
  if (!Array.isArray(configStructure)) {
    return null;
  }
  for (let index = 0; index < configStructure.length; index++) {
    const item = configStructure[index];
    if (item?.name === configName) {
      return item;
    }
    if (item?.children?.length) {
      const result = findConfigItem(item.children, configName);
      if (result) {
        return result;
      }
    }
  }
  return null;
}

/**
 * 验证文件是否符合要求
 * @param {Object} file - 需要验证的文件对象
 * @return {boolean} 如果文件符合要求，返回true，否则返回false
 */
export const fileValidate = (file, fileTypes = ['jpg', 'png', 'jpeg', 'gif'], size = 1) => {
  const nameArr = file.name.split('.');
  const fileEnd = nameArr[nameArr.length - 1];
  const fileSize = file.size / (1024 * size);
  if (fileSize > 1024) {
    Message({ type: 'warning', content: `${file.name}${i18n.t('cannotExceed')}${size}M` });
    return false
  }
  if (!fileTypes.includes(fileEnd.toLowerCase())) {
    Message({ type: 'warning', content: `${i18n.t('onlySupported')} ${fileTypes.join(',')} ${i18n.t('filesOfType')}` });
    return false
  }
  return true
}

/**
 * 获取cookie中cname字段的值
 * @param {String} cname 需要获取的字段
 * @return {String} 字段的值
 */
export function getCookie (cname)  {
  const name = `${cname}=`;
  const ca = document.cookie.split(';');
  for (let i = 0; i < ca.length; i++) {
    const c = ca[i].trim();
    if (c.indexOf(name) === 0) {
      return c.substring(name.length, c.length);
    }
  }
  return '';
}

/**
 * 设置cookie中cname字段的值
 * @param {String} cname 需要设置的字段
 * @param {String} cvalue 需要设置的字段值
 */
export function setCookie (cname, cvalue) {
  document.cookie = `${cname}=${cvalue};path=/`;
}

/**
 * 判断输入是否为空
 * @param {String} val 输入字段
 * @return {Boolean} 判断的结果
 */
export const isInputEmpty = (val) => {
  return val === '' || val === null || val === undefined;
};

// 获取当前时间
export const getCurrentTime = () => {
  let str = new Date().toTimeString().substring(0, 8);
  return str
}

/**
 * 获取应用、模板分类列表
 * @param {string} tenantId 租户ID
 * @param {boolean} isCreation 是否为创建应用
 */
export const queryAppCategories = async (tenantId : String, isCreation: boolean) => {
  const res = await getAppCategories(tenantId);
  if (res && res.code === 0) {
    const tabs = res.data.map(item => (
      {
        key: item.id,
        label: item.name
      }
    ))
    return isCreation ? tabs : [{key: 'all', label: i18n.t('all')}, ...tabs];
  }
}

// 表单地址判断修改
export const formEnv = () => {
  let isSpa = false;
  if (process.env.PACKAGE_MODE === 'spa') {
    isSpa = true;
  }
  return isSpa;
}

export const setSpaClassName = (name:string) => {
  if (formEnv()) {
    return `${name} ${name}-spa`
  }
  return name
}

/**
 * 获取对话多输入配置
 * @param {Object} curAppInfo 应用详情
 * @return {Array} 对话多输入配置
 */
export const getConfiguration = (curAppInfo: any) => {
  if (!curAppInfo?.flowGraph?.appearance) {
    return [];
  }
  const graphOperator = createGraphOperator(JSON.stringify(curAppInfo.flowGraph.appearance));
  const paramsList = graphOperator.getStartNodeInputParams() || [];
  if (paramsList?.length !== 1) {
    Message({ type: 'error', content: i18n.t('startingNodeTip') });
    return [];
  }
  const inputData = find(paramsList[0], ['name', 'input']);
  const inputList = inputData?.value || [];
  return filter(inputList, (input) => input.name !== 'Question' && input.isVisible) || [];
};

/**
 * 获取应用graph里开始节点的appConfig配置
 *
 * @param {Object} appInfo 应用详情
 * @return {Object}
 */
export const getAppConfig = (appInfo) => {
  if (!appInfo?.flowGraph?.appearance) {
    return {};
  }
  const graphOperator = createGraphOperator(JSON.stringify(appInfo.flowGraph.appearance));
  const nodeList  = graphOperator.getShapeIdsByType('startNodeStart');
  return graphOperator.getConfig(nodeList).appConfig;
}

/**
 * 生成随机用户名
 *
 * @return string 用户名
 */
export const generateUniqueName = () => {
  return 'guest-' + nanoid(16);
}

// ==================== GraphOperator ====================

// 数据类型常量
const DATA_TYPES = {
  STRING: 'String',
  INTEGER: 'Integer',
  BOOLEAN: 'Boolean',
  NUMBER: 'Number',
  OBJECT: 'Object',
  ARRAY: 'Array',
};

// 来源类型常量
const FROM_TYPE = {
  EXPAND: 'Expand',
  INPUT: 'Input',
  REFERENCE: 'Reference',
};

/**
 * 将配置数据转换为结构体
 * @param config 配置数据
 * @return {{}|*} 结构体
 */
const configToStruct = (config: any) => {
  if (config.type === DATA_TYPES.ARRAY) {
    return config.value.map(v => configToStruct(v));
  } else if (config.type === DATA_TYPES.OBJECT) {
    const obj = {};
    config.value.forEach((item: any) => {
      obj[item.name] = configToStruct(item);
    });
    return obj;
  } else {
    return Object.prototype.hasOwnProperty.call(config, 'value') ? config.value : config;
  }
};

/**
 * 创建画布操纵器
 * @param graphString 画布字符串
 * @return {{}} 画布操纵器对象
 */
export const createGraphOperator = (graphString: string) => {
  const graph = JSON.parse(graphString);
  const shapes = graph.pages[0].shapes;

  const getInputParams = (shape: any) => {
    if (shape.type === 'startNodeStart') {
      return shape.flowMeta.inputParams;
    } else if (shape.type === 'endNodeEnd') {
      return shape.flowMeta.callback.converter.entity.inputParams;
    } else {
      return shape.flowMeta.jober.converter.entity.inputParams;
    }
  };

  const getConfigByKeys = (keys: string[]) => {
    if (!Array.isArray(keys)) {
      throw new Error('Expected keys to be an array');
    }

    if (keys.length === 0) {
      return null;
    }

    const tmpKeys = [...keys];
    const shapeId = tmpKeys.shift();
    const shape = getShapeById(shapeId);
    const inputParams = getInputParams(shape);
    if (!inputParams) {
      throw new Error('Expected inputParams exists');
    }
    let config = {type: DATA_TYPES.OBJECT, value: inputParams};
    while (tmpKeys.length > 0 && config && config.value) {
      const key = tmpKeys.shift();
      config = config.value.find(v => v.name === key);
    }
    return config;
  };

  const getShapeById = (shapeId: string) => {
    return shapes.find((shape) => shape.id === shapeId);
  };

  return {
    /**
     * 获取配置信息
     * @param keys 键值数组
     * @return {{}|*|null} 配置信息
     */
    getConfig: (keys: string[]) => {
      const config = getConfigByKeys(keys);
      return config ? configToStruct(config) : null;
    },

    /**
     * 获取画布中开始节点的入参信息
     * @return {array} 开始节点入参信息
     */
    getStartNodeInputParams: (): any[] => {
      return shapes.filter(shape => shape.type === 'startNodeStart').map(startNode => startNode.flowMeta.inputParams);
    },

    /**
     * 根据节点类型获取对应节点id列表
     * @param type 节点类型
     * @returns {array} 对应节点id列表
     */
    getShapeIdsByType: (type: string): string[] => {
      return shapes.filter((shape) => shape.type === type).map((shape) => shape.id);
    },

    /**
     * 获取画布数据
     * @return {string} 画布数据
     */
    getGraph: (): string => {
      return JSON.stringify(graph);
    }
  };
};
