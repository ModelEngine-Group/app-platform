/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import i18n from 'i18next';
import { initReactI18next } from "react-i18next";
import en from './en_US.json';
import zh from './zh_CN.json';
import coreEn from '@fit-elsa/elsa-core/locales/en.json';
import coreZh from '@fit-elsa/elsa-core/locales/zh.json';
import reactEn from '@fit-elsa/elsa-react/locales/en.json';
import reactZh from '@fit-elsa/elsa-react/locales/zh.json';

const mergeTranslations = (...translationObjects) => {
  // 从右向左合并（右侧优先级更高）
  return translationObjects
    .filter(obj => obj) // 过滤掉假值（undefined/null
    .reduce((merged, current) => ({ ...merged, ...current }), {});
};

const resources = {
  en: {
    translation: mergeTranslations(coreEn, reactEn, en) // 优先级顺序: en > reactEn > coreEn
  },
  zh: {
    translation: mergeTranslations(coreZh, reactZh, zh) // 优先级顺序: zh > reactZh > coreZh
  }
};

const getCookie = (cname) => {
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
