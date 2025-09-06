import serviceConfig from './httpConfig';
import i18n from '@/locale/i18n';
import { get } from './http';
import { getCookie } from '@/shared/utils/common';
import { ERROR_CODES } from './httpError';
const { AIPP_URL } = serviceConfig;

// 工具流调试应用
/**
 * @param {string} tenantId - 租户ID
 * @param {any} params - 调试参数
 * @return {Promise} 返回一个Promise对象，resolve的参数为调试结果
 * @throws {Error} 如果请求失败，会抛出错误
 */
export function workflowDebug(tenantId: string, params: any) {
  return new Promise((resolve, reject) => {
    fetch(`${AIPP_URL}/${tenantId}/water_flow_chat_debug`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': getCookie('__Host-X-Auth-Token'),
        'X-Csrf-Token': getCookie('__Host-X-Csrf-Token')
      },
      body: JSON.stringify(params)
    }).then((res) => {
      sseError(res, resolve);
    })
  });
}
// 调试对话
/**
 * @param {string} tenantId - 租户ID。
 * @param {any} params - 请求参数。
 * @param {boolean} isDebug - 是否是调试状态
 * @param {boolean} isAuto - 是否自动上传
 * @return {Promise} 返回一个Promise对象，该对象在请求成功时解析为响应对象。
 * @throws {Error} 如果请求失败，Promise对象将被拒绝。
 */
export function sseChat(tenantId: string, params: any, isDebug: boolean, isAuto: boolean = false) {
  let url = `${AIPP_URL}/${tenantId}/${isDebug ? 'app_chat_debug' : 'app_chat'}`
  return new Promise((resolve, reject) => {
    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Auto-Chat-On-Upload': isAuto,
        'X-Auth-Token': getCookie('__Host-X-Auth-Token'),
        'X-Csrf-Token': getCookie('__Host-X-Csrf-Token')
      },
      body: JSON.stringify(params)
    }).then((res) => {
      sseError(res, resolve);
    })
  });
}
// 人工干预
/**
 * @param {string} tenantId - 租户ID
 * @param {string} instanceId - 实例ID
 * @param {any} params - 需要保存的参数
 * @param {string} logId -历史记录ID
 * @param {boolean} isDebug -是否是调试状态
 * @return {Promise} 返回一个Promise对象，resolve的参数为请求的响应
 * @throws {Error} 如果请求失败，会抛出错误
 */
export function saveContent(tenantId: string, instanceId: string, params: any, logId:string = '', isDebug: boolean = false) {
  let url = `${AIPP_URL}/${tenantId}/app/instances/${instanceId}/log/${logId}?is_debug=${isDebug}`
  return new Promise((resolve, reject) => {
    fetch(url, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': getCookie('__Host-X-Auth-Token'),
        'X-Csrf-Token': getCookie('__Host-X-Csrf-Token')
      },
      body: JSON.stringify(params)
    }).then((res) => {
      sseError(res, resolve);
    })
  });
}
// 获取version
/**
 * @param {string} tenantId - 租户ID
 * @param {string} appId - 应用ID
 * @return {Promise} 返回一个Promise，其解析的结果是一个对象，包含了版本信息
 * @throws {Error} 如果请求失败，会抛出错误
 */
export function getTestVersion(tenantId: string, appId: string) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/aipp?isDebug=true`);
}
// 请求对话接口（溯源）
/**
 * @param {string} tenantId - 租户ID
 * @param {string} instanceId - 实例ID
 * @param {any} params - 需要保存的参数
 * @return {Promise} 返回一个Promise对象，resolve的参数为请求的响应
 * @throws {Error} 如果请求失败，会抛出错误
 */
export function saveChart(tenantId: string, instanceId: string, params: any) {
  let url = `${AIPP_URL}/${tenantId}/instances/${instanceId}`
  return new Promise((resolve, reject) => {
    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': getCookie('__Host-X-Auth-Token'),
        'X-Csrf-Token': getCookie('__Host-X-Csrf-Token')
      },
      body: JSON.stringify(params)
    }).then((res) => {
      sseError(res, resolve);
    })
  });
}

// featch sse 错误处理
const sseError = (res, resolve) => {
  const { status } = res;
  if (ERROR_CODES[status]) {
    resolve({ status:status, msg: ERROR_CODES[status]})
    return;
  }
  const contentType = res.headers.get('content-type');
  if (contentType.indexOf('text/event-stream') !== -1) {
    resolve(res);
  } else {
    let resJson = {}
    res.text().then(resText => {
      try {
        resJson = JSON.parse(resText);
      } catch {
        resJson = { status: 500, suppressed: i18n.t('requestFailed') }
      }
      resolve(resJson)
    });
  }
}
