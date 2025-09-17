import store from '@/store/store';
import i18n from '@/locale/i18n';
import { ERROR_CODES } from '../http/httpError';

/**
 * 检验是否有未结束的对话
 * @return {boolean} 如果有未结束的对话返回true，否则返回false
 */
export const isChatRunning = () => {
  const commonStore = store.getState().chatCommonStore;
  const chatList:any = commonStore.chatList || [];
  const chatRunning = commonStore.chatRunning || false;
  let hasRunning = chatList.filter(item => item.status === 'RUNNING')[0];
  if (chatRunning || hasRunning) {
    return true;
  }
  return false;
}

// 应用导出
export const exportJson = (data:any, name: string) => {
  const blob = new Blob([JSON.stringify(data)], { type: 'application/json' })
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `${name}应用.json`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
}

// featch sse 错误处理
export const sseError = (res, resolve) => {
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
