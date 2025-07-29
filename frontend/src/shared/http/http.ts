/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import axios from "axios";
import i18n from "@/locale/i18n";
import store from '@/store/store';
import { Message } from '../utils/message';
import { getCookie } from "@/shared/utils/common";
import { ERROR_CODES } from './httpError'
import { setLoginStatus, setNoAuth } from '@/store/chatStore/chatStore';

let baseAxios = axios.create({
  headers: {
    common: {
      'X-Auth-Token': getCookie('__Host-X-Auth-Token'),
      'X-Csrf-Token': getCookie('__Host-X-Csrf-Token')
    }
  }
});
baseAxios.defaults.timeout = 100000;
baseAxios.defaults.withCredentials = true;
baseAxios.interceptors.request.use(
  (req) => {
    return req;
  },
  (error) => {
    return Promise.reject(error);
  }
);

baseAxios.interceptors.response.use(
  (response) => {
    if (response.data.code === undefined || response.data.code === 0 || response.data.code === 200 || response.data.code === '0') {
      return Promise.resolve(response.data);
    }
    Message({ type: 'error', content: response.data.msg || response.data.detail || i18n.t('requestFailed') });
    return Promise.resolve(response.data);
  },
  (error) => {
    if (error.response.status === 401) {
      store.dispatch(setLoginStatus(false));
      if (error.response.headers["fit-redirect-to-prefix"]) {
        window.location.href = error.response.headers["fit-redirect-to-prefix"] + window.location.href;
      } else {
        Message({ type: 'error', content: '登录失效且headers里面没有跳转登录的url' });
      }
    } else if (error.response.status === 403) {
      if (location.pathname.includes('/chat/') && !location.pathname.includes('/app/') && error.config.url.indexOf('/chat/') !== -1){
        store.dispatch(setNoAuth(true));
      }
    } else {
      return Promise.reject(error);
    }
  }
);

/**
 * 封装get方法
 * @param url  请求url
 * @param params  请求参数
 * @returns {Promise}
 */
export const get = (url, params = {}, _object = {}) => {
  return new Promise((resolve, reject) => {
    baseAxios
      .get(url, { params, headers: _object })
      .then((response) => {
        resolve(response);
      })
      .catch((error) => {
        msag(error);
        reject(error);
      });
  });
};

/**
 * 封装post请求
 * @param url
 * @param data
 * @returns {Promise}
 */

export const post = (url, data, _object = {}) => {
  return new Promise((resolve, reject) => {
    baseAxios.post(url, data, { headers: _object }).then(
      (response) => {
        //关闭进度条
        resolve(response);
      },
      (err) => {
        msag(err);
        reject(err);
      }
    );
  });
};

/**
 * 封装patch请求
 * @param url
 * @param data
 * @returns {Promise}
 */
export const patch = (url, data = {}, _object = {}) => {
  return new Promise((resolve, reject) => {
    baseAxios.patch(url, data, { headers: _object }).then(
      (response) => {
        resolve(response);
      },
      (err) => {
        msag(err);
        reject(err);
      }
    );
  });
};

/**
 * 封装put请求
 * @param url
 * @param data
 * @returns {Promise}
 */

export const put = (url, data = {}, _object = {}) => {
  return new Promise((resolve, reject) => {
    baseAxios.put(url, data, { headers: _object }).then(
      (response) => {
        resolve(response);
      },
      (err) => {
        msag(err);
        reject(err);
      }
    );
  });
};

/**
 * 封装delete请求
 * @param url
 * @param data
 * @returns {Promise}
 */

export const del = (url, data = {}, _object = {}) => {
  return new Promise((resolve, reject) => {
    baseAxios.delete(url, { data, headers: _object }).then(
      (response) => {
        resolve(response);
      },
      (err) => {
        msag(err);
        reject(err);
      }
    );
  });
};

/**
 * 封装get下载方法
 * @param url  请求url
 * @param params  请求参数
 * @returns {Promise}
 */
export const download = (url, params = {}, _object = {}) => {
  return new Promise((resolve, reject) => {
    baseAxios
      .get(url, { params, headers: _object, responseType: 'arraybuffer' })
      .then((response) => {
        resolve(response);
      })
      .catch((error) => {
        msag(error);
        reject(error);
      });
  });
};

const msag = (err) => {
  const { response } = err
  if (response) {
    const status = response.status;
    Message({ type: 'error', content: ERROR_CODES[status] || err.message || i18n.t('requestFailed')});
  }
};
