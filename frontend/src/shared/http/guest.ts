import { del, get, post, put, patch } from './http';
import serviceConfig from './httpConfig';
import { generateUniqueName, getCookie } from '@/shared/utils/common';
import { sseError } from '@/shared/utils/chat';

const { AIPP_URL, PLUGIN_URL } = serviceConfig;

// 创建带guestName的headers
const withGuestHeaders = (options = {}) => {
  let guestName = localStorage.getItem('guest-name');
  if (!guestName) {
    guestName = generateUniqueName();
    localStorage.setItem('guest-name', guestName);
  }

  if (options.headers) {
    options.headers['X-Guest-Username'] = guestName;
    return options;
  }

  return {
    ...options,
    'X-Guest-Username': guestName
  };
};

// 获取应用是否打开游客模式
export function getAppGuestIsOpen(path: string) {
  return get(`${AIPP_URL}/guest/${path}/is_open`, {}, withGuestHeaders());
}

// 获取游客模式下应用的详情
export function getGuestModeAppInfo(path) {
  return get(`${AIPP_URL}/guest/${path}`, {}, withGuestHeaders());
}

// 获取aipp_id和aipp_version
export function getGuestModePublishAppId(tenantId: string, appId: string) {
  return get(`${AIPP_URL}/guest/${tenantId}/app/${appId}/latest_published`, {}, withGuestHeaders());
}

// 终止当前对话
export function guestModeStopInstance(tenantId: string, instanceId: string, params: any) {
  if (params.logId) {
    return put(
      `${AIPP_URL}/guest/${tenantId}/instances/${instanceId}/terminate/log/${params.logId}`,
      params, withGuestHeaders()
    );
  }
  return put(`${AIPP_URL}/guest/${tenantId}/instances/${instanceId}/terminate`, params, withGuestHeaders());
}

// 获取会话历史对话
export function getGuestModeChatRecentLog(tenantId, chatId, appId) {
  return get(`${AIPP_URL}/guest/${tenantId}/log/app/${appId}/chat/${chatId}`, {}, withGuestHeaders());
}

// 用户自勾选删除历史记录
export function guestModeClearChat(appId, list) {
  return del(`${AIPP_URL}/guest/${appId}/log/logs`, list, withGuestHeaders());
}

// 对话接口
export function guestModeSseChat(
  tenantId: string,
  params: any,
  isDebug: boolean,
  isAuto: boolean = false
) {
  let url = `${AIPP_URL}/guest/${tenantId}/${isDebug ? 'app_chat_debug' : 'app_chat'}`;
  return new Promise((resolve, reject) => {
    fetch(url, withGuestHeaders({
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Auto-Chat-On-Upload': isAuto,
        'X-Auth-Token': getCookie('__Host-X-Auth-Token'),
        'X-Csrf-Token': getCookie('__Host-X-Csrf-Token'),
      },
      body: JSON.stringify(params),
    })).then((res) => {
      sseError(res, resolve);
    });
  });
}

// 继续会话接口
export function guestModeResumeChat(
  tenantId: string,
  instanceId: string,
  params: any,
  logId: string = '',
  isDebug: boolean = false
) {
  let url = `${AIPP_URL}/guest/${tenantId}/app/instances/${instanceId}/log/${logId}?is_debug=${isDebug}`;
  return new Promise((resolve, reject) => {
    fetch(url, withGuestHeaders({
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': getCookie('__Host-X-Auth-Token'),
        'X-Csrf-Token': getCookie('__Host-X-Csrf-Token'),
      },
      body: JSON.stringify(params),
    })).then((res) => {
      sseError(res, resolve);
    });
  });
}

// 重新对话接口
export function guestModeRestartChat(tenantId: string, instanceId: string, params: any) {
  let url = `${AIPP_URL}/guest/${tenantId}/instances/${instanceId}`;
  return new Promise((resolve, reject) => {
    fetch(url, withGuestHeaders({
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': getCookie('__Host-X-Auth-Token'),
        'X-Csrf-Token': getCookie('__Host-X-Csrf-Token'),
      },
      body: JSON.stringify(params),
    })).then((res) => {
      sseError(res, resolve);
    });
  });
}

// 获取点赞点灭详情
export function guestModeQueryFeedback(id) {
  const url = `${PLUGIN_URL}/guest/feedback/${id}`;
  return get(url, {}, withGuestHeaders());
}

// 点赞点灭
export function guestModeFeedbacksRq(params) {
  const url = `${PLUGIN_URL}/guest/feedback`;
  return post(url, params, withGuestHeaders());
}

// 更新点赞点灭
export function guestModeUpdateFeedback(id, data) {
  const url = `${PLUGIN_URL}/guest/feedback/${id}`;
  return patch(url, data, withGuestHeaders());
}

// 取消点赞点灭
export function guestModeDeleteFeedback(id) {
  const url = `${PLUGIN_URL}/guest/feedback/${id}`;
  return del(url, {}, withGuestHeaders());
}

// 获取灵感大全类别
export function queryGuestModeDepartMent(tenantId, appId, debug = false) {
  return get(`${AIPP_URL}/guest/${tenantId}/app/${appId}/prompt?isDebug=${debug}`, {}, withGuestHeaders());
}

// 添加灵感大全
export function guestModeAddInspiration(tenantId, appId, id, data) {
  return post(`${AIPP_URL}/guest/${tenantId}/app/${appId}/prompt/${id}`, data, withGuestHeaders());
}

// 编辑灵感大全
export function guestModeEditInspiration(tenantId, appId, categoryId, id, data) {
  return put(
    `${AIPP_URL}/guest/${tenantId}/app/${appId}/prompt/${categoryId}/inspiration/${id}`,
    data, withGuestHeaders()
  );
}

// 删除灵感大全
export function guestModeDeleteInspiration(tenantId, appId, categoryId, id) {
  return del(`${AIPP_URL}/guest/${tenantId}/app/${appId}/prompt/${categoryId}/inspiration/${id}`, {}, withGuestHeaders());
}

// 获取灵感大全列表数据
export function queryGuestModeInspiration(tenantId, appId, categoryId, debug = false) {
  return get(`${AIPP_URL}/guest/${tenantId}/app/${appId}/prompt/${categoryId}?isDebug=${debug}`, {}, withGuestHeaders());
}

// 猜你想问
export function getGuestModeRecommends(params) {
  const url = `${PLUGIN_URL}/v1/api/guest/recommend`;
  return post(url, params, withGuestHeaders());
}

// 删除历史对话记录
export function clearGuestModeInstance(tenantId, appId, type) {
  return del(`${AIPP_URL}/guest/${tenantId}/log/app/${appId}?type=${type}`, {}, withGuestHeaders());
}

// 多文件上传
export function guestModeUploadMultipleFile(tenantId, appId, data) {
  return post(`${AIPP_URL}/guest/${tenantId}/files?app_id=${appId}`, data, withGuestHeaders({
    'Content-Type': 'multipart/form-data',
  }));
}

//清空APP的历史对话
export function clearGuestModeChatHistory(tenantId, appId, params = {}) {
  let url = `${PLUGIN_URL}/v1/api/guest/${tenantId}/chat?app_id=${appId}`;
  return del(url, params, withGuestHeaders());
}

// 查询应用对话列表
export function getGuestModeChatList(tenantId, params) {
  const url = `${PLUGIN_URL}/v1/api/guest/${tenantId}/chat/chat_list`;
  return post(url, params, withGuestHeaders());
}
