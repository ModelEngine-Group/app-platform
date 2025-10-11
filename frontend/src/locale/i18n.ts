/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import i18n from 'i18next';
import { initReactI18next } from "react-i18next";
import en from './en_US.json';
import zh from './zh_CN.json';
import coreEn from '@fit-elsa/elsa/locales/en.json';
import coreZh from '@fit-elsa/elsa/locales/zh.json';
import agentEn from '@fit-elsa/agent-flow/locales/en.json';
import agentZh from '@fit-elsa/agent-flow/locales/zh.json';

const mergeTranslations = (...translationObjects) => {
  // 从右向左合并（右侧优先级更高）
  return translationObjects
    .filter(obj => obj) // 过滤掉假值（undefined/null
    .reduce((merged, current) => ({ ...merged, ...current }), {});
};

const resources = {
  en: {
    translation: mergeTranslations(coreEn, agentEn, en) // 优先级顺序: en > agentEn > coreEn
  },
  zh: {
    translation: mergeTranslations(coreZh, agentZh, zh) // 优先级顺序: zh > agentZh > coreZh
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
