/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { getCurUser, getOmsCurUser, getUserRole } from '../shared/http/aipp';
import { v4 as uuidv4 } from 'uuid';
import { setUserRole, setReadOnly } from '@/store/chatStore/chatStore';
import store from '@/store/store';

export const getUser = async () => {
  let userName = localStorage.getItem('__account_name__');
  if (!userName) {
    const res = await getCurUser();
    localStorage.setItem('currentUserId', res.data.account);
    localStorage.setItem('currentUserIdComplete', res.data.account);
    localStorage.setItem('currentUser', res.data.chineseName);
  }
};

export const getOmsUser = async () => {
  let userName = localStorage.getItem('__account_name__');
  if (!userName) {
    const res = await getOmsCurUser();
    localStorage.setItem('currentUserId', res.data.account);
    localStorage.setItem('currentUserIdComplete', res.data.userId);
    localStorage.setItem('currentUser', res.data.chineseName);
  }
};

/**
 * 获取用户角色信息
 *
 * @return {Promise<void>} 无返回值
 */
export const getRole = async () => {
  const res:any = await getUserRole();
  if (res.code == 0 &&  res.data.roleName) {
    let userRole = res.data.roleName[0] || 'READ_ONLY';
    localStorage.setItem('__currentRole__', userRole);
    store.dispatch(setUserRole(userRole));
    store.dispatch(setReadOnly(userRole === 'READ_ONLY'));
  }
};

// 超过一千，转为K
export const FormatQaNumber = (val) => {
  if (!val) {
    return '--';
  }
  if (val < 1000) {
    return val + 'QA对';
  }
  return Math.floor(val / 1000) + 'k QA对';
};

/**
 * 对上传返回的参数进行递归处理
 * @param el 传入递归的初始数据
 */
export const recursion = (el: any, deep = 1) => {
  try {
    el.forEach((item: any) => {
      if (item.type === 'object') {
        item.key = uuidv4();
        if (item.properties) {
          item.children = Object.keys(item.properties).map((ite) => ({
            ...item.properties[ite],
            name: ite,
            key: uuidv4(),
          }));
          recursion(item.children, deep + 1);
        }
      }
      if (item.type === 'array') {
        item.key = uuidv4();
        if (item.items) {
          item.items.name = '';
          item.items.key = uuidv4();
          item.children = [item.items];
        } else {
          item.children = Object.keys(
            (typeof item.properties === 'object' && item.properties !== null && !Array.isArray(item.properties))
              ? item.properties
              : {}
          ).map(ite => ({
            ...(item.properties?.[ite] || {}),
            name: ite,
            key: uuidv4()
          }));
        }
        recursion(item.children, deep + 1);
      }
    });
  } catch (error){
    console.error(error);
  }
};
