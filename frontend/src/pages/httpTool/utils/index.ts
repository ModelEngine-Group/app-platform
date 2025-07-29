import { initParams } from '../config';
import { v4 as uuidv4 } from 'uuid';
/**
 * 对表格数据进行处理的函数
 * @param {Object} inputProp - 输入的对象，包含了表格的各种属性
 * @param {number} keydeep - 深度参数，默认为1
 * @param {string} reference - 引用参数，默认为undefined
 * @return {Array} outPut - 返回处理后的表格数据
 */
export const tabeleDataProcess = (inputProp, httpRunnables, keydeep = 1, reference = undefined) => {
  let typeArr = ['object', 'array'];
  let normalType = ['string', 'boolean', 'integer'];
  let outPut: any = [];
  let { required, properties } = inputProp;
  let keyArr = Object.keys(properties);
  if (keyArr && keyArr.length) {
    keyArr.forEach(item => {
      let obj: any = {
        name: item,
        required: false,
        type: properties[item].type,
        rowKey: `${keydeep}-${item}`,
        description: properties[item].description || '',
        children: [],
        reference: !reference ? item : `${reference}.${item}`,
        deep: keydeep
      };
      let { requestType, mappingKey } =  getRequestType(httpRunnables, obj.reference, obj.type)
      obj.requestType = requestType;
      obj.mapping = mappingKey;
      if (typeArr.includes(properties[item].type)) {
        let keyNum = keydeep + 1;
        obj.parent = true;
        if (properties[item].type === 'array') {
          let arrayItem = properties[item].items || {};
          let type = arrayItem.type || 'string';
          obj.children = tableArrayData(arrayItem, keyNum, `array<${type}>`, item);
          if (type === 'object') {
            obj.children[0].children = tabeleDataProcess(arrayItem, httpRunnables, keyNum, item);
          }
        } else {
          obj.children = tabeleDataProcess(properties[item], httpRunnables, keyNum, obj.reference);
        }
      } else {
        obj.defaultValue = properties[item].default || '';
      }
      if (required && required.includes(item)) {
        obj.required = true;
      }
      outPut.push(obj);
    })
  }
  return outPut;
}

// 数据回显
const tableArrayData = (obj, deep, type, reference) => {
  let arrayObj: any = {
    name: '[array Item]',
    required: false,
    type,
    rowKey: uuidv4(),
    disabled: true,
    description: obj.description || '',
    reference,
    children: [],
    requestType: '',
    deep
  };
  return [arrayObj];
}

// 获取请求类型
const getRequestType = (httpRunnables, reference, type) => {
  let requestType = type === 'object' ? 'NONE' : 'PATH_VARIABLE';
  let mappingKey = '';
  let httpItem = httpRunnables[reference];
  if (httpItem) {
    requestType = httpItem.httpSource || 'PATH_VARIABLE';
    mappingKey = httpItem.key || '';
  }
  return {
    mappingKey,
    requestType,
  };
}

/**
 * 获取默认展开行的键值
 * @param data 数据源，包含嵌套的子数据
 * @return 返回一个数组，包含所有需要默认展开的行的键值
 */
export const getDefaultExpandedRowKeys = (data) => {
  let expandKey: any = [];
  data.forEach(item => {
    if (item.children && item.children.length > 0) {
      expandKey.push(item.rowKey);
      let childrenResult = getDefaultExpandedRowKeys(item.children);
      expandKey = expandKey.concat(childrenResult);
    }
  })
  return expandKey
}

export const setHttpMap = (httpMap) => {
  let flatObj:any = {};
  if (httpMap !== null && typeof httpMap === 'object') {
    Object.keys(httpMap).forEach(item => {
      let httpItem = httpMap[item];
      Object.keys(httpItem).forEach(hItem => {
        if (hItem === '$' || hItem === '$[*]') {
          flatObj[item] = httpItem[hItem];
        } else if (hItem.indexOf('$[*].') !== -1) {
          let replaced = hItem.replace(/\$\[\*\]/g, item).replace(/\[\*\]/g, '');
          flatObj[replaced] = httpItem[hItem];
        } else {
          flatObj[`${item}.${hItem}`] = httpItem[hItem];
        }
      })
    });
  }
  return flatObj;
}


/**
 * 过滤表格数据，根据rowKey删除对应的数据行
 * @param data 表格数据，是一个对象数组
 * @param rowKey 需要删除的数据行的rowKey
 * @return 如果找到并删除了对应的数据行，返回true，否则返回false
 */
export const filterTable = (data, rowKey) => {
  for (let i = 0; i < data.length; i++) {
    const obj = data[i];
    if (obj.rowKey === rowKey) {
      data.splice(i, 1);
      return true;
    } else if (obj.children && obj.children.length > 0) {
      if (filterTable(obj.children, rowKey)) {
        return true;
      }
    }
  }
  return false;
}

/**
 * 更新表格数据
 * @param {Array} data 表格数据，是一个数组，数组的每一项是一个对象，对象中包含rowKey和其他属性
 * @param {string} rowKey 需要更新的行的标识
 * @param {string} key 需要更新的属性的键
 * @param {any} newVal 需要更新的属性的新值
 */
export const updateTable = (data, rowKey, key, newVal) => {
  for (let i = 0; i < data.length; i++) {
    const item = data[i];
    if (item.rowKey === rowKey) {
      updateDataProcess(item, key, newVal);
      return;
    } else if (item.children && item.children.length > 0) {
      updateTable(item.children, rowKey, key, newVal);
    }
  }
}

// 处理更新表格数据
const updateDataProcess = (item, key, newVal) => {
  const normalType = ['string', 'boolean', 'integer', 'array<string>', 'array<integer>', 'array<boolean>'];
  const bodyType = ['JSON', 'form-data', 'x-www-form-urlencoded', 'raw-text'];
  if (key === 'type' && newVal === 'array') {
    initParams.rowKey = uuidv4();
    initParams.deep = item.deep + 1;
    item.children = [{ ...initParams, type: 'array<string>', name: '[array Item]', disabled: true }];
  } else if (key === 'type' && normalType.includes(newVal)) {
    item.children = [];
  } else if (key === 'type' && newVal === 'object'){
    initParams.rowKey = uuidv4();
    initParams.deep = item.deep + 1;
    item.requestType = 'NONE';
    item.children = [{ ...initParams, type: 'string', name: '', }];
  }
  if (bodyType.includes(newVal)) {
    item[key] = `Body/${newVal}`;
  } else {
    item[key] = newVal;
  }
}

/**
 * 在树形结构中查找指定节点并添加子节点
 * @param {Array} tree 树形结构数组
 * @param {string} rowKey 需要查找的节点的唯一标识
 * @param {Object} newChild 需要添加的子节点
 * @return {boolean} 如果找到节点并添加子节点成功，返回true，否则返回false
 */
export const findNodeAndAddChild = (tree, rowKey, newChild) => {
  for (let node of tree) {
    if (node.rowKey === rowKey) {
      node.children.push(newChild);
      return true;
    }
    if (node.children && node.children.length > 0) {
      if (findNodeAndAddChild(node.children, rowKey, newChild)) {
        return true;
      }
    }
  }
  return false;
}

/**
 * 验证表格数据的函数
 * @param {Array} array - 需要验证的数据数组
 * @return {Object} 返回一个对象，包含验证后的数据数组和是否存在错误的标志
 */
export const validateTableData = (array) => {
  let keyArr = ['name'];
  function traverseAndCheck(obj) {
    if (typeof obj === 'object' && obj !== null) {
      obj.error = [];
      for (let key in obj) {
        if (obj.hasOwnProperty(key)) {
          if (keyArr.includes(key) && obj[key].trim().length === 0) {
            obj.error.push(key);
            obj[key] = '';
          }
          if (Array.isArray(obj[key]) && key !== 'error' && obj[key] !== null) {
            validateTableData(obj[key]);
          }
        }
      }
    }
  }
  array.forEach(item => traverseAndCheck(item));
  return array;
}
export const checkNamesNotEmpty = (data) => {
  for (let i = 0; i < data.length; i++) {
    const item = data[i];
    if (item.error && item.error.length > 0) {
      return true;
    }
    if (item.children && Array.isArray(item.children)) {
      if (checkNamesNotEmpty(item.children)) {
        return true;
      }
    }
  }
  return false;
}
// 数据拼接
export const convertArrayToObject = (array: any[], type = 'input', deep = false, noOrder = false) => {
  let result: any = {};
  let { requireList } = getRequiredKey(array);
  if (!deep && type === 'input') {
    result.required = requireList;
  }
  array.forEach(item => {
    const newItem: any = {
      type: arrayTypeProcess(item.type),
      description: item.description
    };
    if (item.defaultValue && item.defaultValue.length) {
      newItem.default = item.defaultValue
    }
    if (item.children && item.children.length > 0) {
      if (item.type === 'array') {
        newItem.items = convertArrayToObject(item.children, type, true, true);
      } else {
        let { requireList } = getRequiredKey(item.children);
        newItem.properties = convertArrayToObject(item.children, type, true);
        if (type === 'input') {
          newItem.required = requireList
        }
      }
    }
    noOrder ? result = newItem : result[item.name] = newItem;
  });
  return result;
}

// http请求方式拼接
export const convertHttpMap = (data, isInit = false) => {
  const result:any = {};
  let normalType = ['string', 'boolean', 'integer'];
  let bodyType = ['Body/JSON'];
  data.forEach(item => {
    if (normalType.includes(item.type)) {
      result[item.name] = {
        "$": {
          "key": item.name,
          "httpSource": item.requestType || "PATH_VARIABLE"
        }
      };
    } else {
      if (item.type === 'object') {
        if (bodyType.includes(item.requestType)) {
          result[item.name] = {
            $: {
              propertyPath: '',
              httpSource: 'OBJECT_ENTITY',
            },
          };
          item.children = [];
        } else {
          result[item.name] = {};
        }
      } else {
        result[item.name] = {
          "$[*]": {
            "key": item.name,
            "httpSource": item.requestType || "PATH_VARIABLE"
          }
        };
      }
      item.children.forEach(child => {
        if (child.type === 'object') {
          result[item.name][child.name] = {
            "key": child.name,
            "httpSource": child.requestType || "PATH_VARIABLE"
          };
          generateNestedStructure(result[item.name], isInit, child);
        } else if (child.type === 'array<string>') {
          result[item.name][`$[*]`] = {
            "key": item.name,
            "httpSource": child.requestType || "PATH_VARIABLE"
          };
        } else if ((child.type === 'array<object>')) {
          if (isInit) {
            result[item.name][`$[*]`] = {
              "key": '',
              "httpSource": child.requestType || "PATH_VARIABLE"
            };
            generateNestedStructure(result[item.name], isInit, child, `$[*]`);
          } else{
            if (result[item.name][`$[*]`].httpSource === 'NONE' || result[item.name][`$[*]`].httpSource ==='PATH_VARIABLE') {
              delete result[item.name][`$[*]`];
            }
            generateNestedStructure(result[item.name], isInit, child, `$[*]`);
          }
        } else {
          result[item.name][child.name] = {
            "key": child.name,
            "httpSource": child.requestType || "PATH_VARIABLE"
          };
        }
      });
    }
  });
  return result;
}
const generateNestedStructure = (parent, isInit = false, child, name = '',) => {
  child.children.forEach(nestedChild => {
    let normalType = ['string', 'boolean', 'integer'];
    let paramsName = name ? name : child.name;
    if (normalType.includes(nestedChild.type)) {
      parent[`${paramsName}.${nestedChild.name}`] = {
        "key": nestedChild.name,
        "httpSource": nestedChild.requestType || 'PATH_VARIABLE'
      };
    }
    if (nestedChild.type === 'object') {
      generateNestedStructure(parent, isInit, nestedChild, `${paramsName}.${nestedChild.name}`, );
    } else if (nestedChild.type === 'array') {
      let childItem = nestedChild.children[0] || {};
      if (childItem.type !== 'array<object>') {
        parent[`${paramsName}.${nestedChild.name}[*]`] = {
          "key": '',
          "httpSource": nestedChild.requestType || "PATH_VARIABLE"
        };
      } else {
        parent[`${paramsName}.${nestedChild.name}[*]`] = {
          "key": '',
          "httpSource": nestedChild.requestType || "PATH_VARIABLE"
        };
        generateNestedStructure(parent, isInit, childItem, `${paramsName}.${nestedChild.name}[*]` );
      }
    }
  });
}

// 数组数据拼接
const arrayTypeProcess = (type) => {
  if (type.indexOf('array<') !== -1) {
    if (type.indexOf('string') !== -1) {
      return 'string';
    } else {
      return 'object';
    }
  } else {
    return type;
  };
}

// 获取required的属性
const getRequiredKey = (arr) => {
  let requireList: any = [];
  let orderList: any = [];
  arr.forEach(item => {
    orderList.push(item.name);
    if (item.required) {
      requireList.push(item.name);
    }
  });
  return {
    requireList,
    orderList
  };
}

// 数据requestType拼接
export const requestArrayToObject = (array) => {
  const result: any = {};
  array.forEach(item => {
    const newItem: any = {
      type: item.type,
      description: item.description
    };
    if (item.defaultValue && item.defaultValue.length) {
      newItem.defaultValue = item.defaultValue
    }
    if (item.children && item.children.length > 0) {
      newItem.properties = convertArrayToObject(item.children);
    }
    result[item.name] = newItem;
  });
  return result;
}