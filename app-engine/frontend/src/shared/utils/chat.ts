import store from '@/store/store';

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