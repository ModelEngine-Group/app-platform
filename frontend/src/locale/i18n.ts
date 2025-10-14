/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import i18n from 'i18next';
import { initReactI18next } from "react-i18next";
import en from './en_US.json';
import zh from './zh_CN.json';

const resources = {
  en: {
    translation: en
  },
  zh: {
    translation: zh
  }
};

const getCookie = (cname: string) => {
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

i18n.use(initReactI18next).init({
  resources,
  fallbackLng: getCookie('locale') || 'zh-cn',
  interpolation: {
    escapeValue: false
  },
  returnNull: false
});

export default i18n;