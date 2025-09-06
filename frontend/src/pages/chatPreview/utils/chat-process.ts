import { isJsonString, getUiD } from '@/shared/utils/common';
import { queryInspirationSelect } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import i18n from "@/locale/i18n";
import { v4 as uuidv4 } from 'uuid';

// 历史会话消息处理
export const historyChatProcess = (res) => {
  let chatArr = [];
  res.data.forEach((item) => {
    let questionObj:any = { type: 'send', sendType: 'text' };
    if (item.question) {
      questionObj.logId = item.question.logId;
      let logData = JSON.parse(item.question.logData);
      if (item.question.logType === 'QUESTION_WITH_FILE') {
        const { question, files } = JSON.parse(logData.msg);
        questionObj.content = question;
        questionObj.fileList = files;
      } else {
        questionObj.content = logData.msg;
      }
      if (item.question.logType !== 'HIDDEN_QUESTION') {
        chatArr.push(questionObj);
      }
    }
    if (item.instanceLogBodies.length) {
      item.instanceLogBodies.forEach((aItem) => {
        let msgHistory = '';
        let referenceMeta: any = [];
        let msg = JSON.parse(aItem.logData).msg;
        let msgData = [];
        // 文生图对话记录
        if (isJsonString(msg)) {
          msgData = JSON.parse(msg);
        }
        let pictureList = [];
        if (aItem.logType === 'META_MSG') {
          let metaMsg = msg;
          let metaMsgData = JSON.parse(metaMsg);
          msgHistory = metaMsgData.data;
          referenceMeta.push(metaMsgData.reference);
        } else if (Array.isArray(msgData) && msgData?.length && msgData.every(item => item.data && item.mime)) {
          msgHistory = '';
          pictureList = msgData;
        } else {
          msgHistory = msg || '';
        }
        let answerObj:any = {
          content: msgHistory,
          loading: false,
          openLoading: false,
          checked: false,
          logId: aItem.logId,
          type: 'receive',
          instanceId: item.instanceId,
          finished: true,
          messageType: 'history',
          recieveType: 'msg',
          feedbackStatus: -1,
          appName: item.appName,
          appIcon: item.appIcon,
          status: item.status
        };
        if (pictureList.length) {
          answerObj.pictureList = pictureList;
        }
        if (aItem.logType === 'META_MSG') {
          answerObj.reference = referenceMeta;
          answerObj.msgType = aItem.logType;
        }
        if (item.appName) {
          answerObj.isAt = true;
        }
        if (aItem.logType === 'FORM') {
          let data = JSON.parse(aItem.logData);
          let formAppearance = JSON.parse(data.formAppearance);
          let formData = JSON.parse(data.formData);
          answerObj.recieveType = 'form';
          answerObj.path = formAppearance.iframeUrl || '';
          answerObj.formConfig = {
            instanceId: item.instanceId,
            version: '',
            aippId: '',
            formName: formAppearance[0]?.name || 'normal',
            type: 'history',
            logId: aItem.logId,
            formAppearance,
            formData,
            startTime: data.startTime || '',
            _internal: data._internal ||'',
            nodeId: data.nodeId ||'',
            status: item.status
          };
        }
        if (aItem.logType === 'FILE') {
          answerObj.type = 'send';
          answerObj.recieveType = 'file';
          let { file_type } = JSON.parse(msgHistory);
          answerObj.sendType = fileTypeSet(file_type);
        }
        if (isJsonString(msgHistory)) {
          let msgObj = JSON.parse(msgHistory);
          if (msgObj.chartData && msgObj.chartType) {
            answerObj.recieveType = 'chart';
            answerObj.chartConfig = msgObj;
          }
        }
        chatArr.push(answerObj);
      });
    }
  });
  return chatArr;
};
// 灵感大全设置下拉列表
export const inspirationProcess = (tenantId, data, prompItem, appInfo) => {
  let { prompt, promptVarData } = prompItem;
  let promptArr = [];
  for (let i = 0; i < data.length; i++) {
    let selectItem = promptVarData.filter((sItem) => sItem.var === data[i])[0];
    let options = [];
    let selectStr = '';
    if (selectItem) {
      if (selectItem.sourceType === 'fitable') {
        let params = { appId: appInfo.id, appType: appInfo.state === 'active' ? 'NORMAL' : 'PREVIEW' };
        const res:any = queryInspirationSelect(tenantId, 'GetQAFromLog', params);
        if (res.code === 0) {
          options = res.data || [];
        }
        selectStr = `<div class='chat-focus' contenteditable='false' data-type='${data[i]}' style='min-width: 40px;'>${escapeHTML(selectItem.var || '')}</div>`;
      } else {
        options = selectItem ? selectItem.sourceInfo.split(';') : [];
        options = options.filter((item) => item.length > 0);
        selectStr = `<div class='chat-focus' contenteditable='false' data-type='${data[i]}' style='min-width: 40px;'>${escapeHTML(options[0] || '')}</div>`;

      }
      selectItem.options = options;
      promptArr.push(selectItem);
      prompt = prompt.replaceAll(new RegExp('\\{\{' + data[i] + '\\}\}', 'g'), selectStr);
    }
  }
  return { prompt, promptArr }
};
//  发送消息前验证与数据拼装
export const sendProcess = (chatRunning, value, chatFileList) => {
  const chatInitObj = JSON.parse(JSON.stringify({
    content: '',
    type: 'send',
    checked: false
  }));
  if (!value.trim().length && !chatFileList?.length) {
    return;
  }
  if (chatRunning) {
    Message({ type: 'warning', content: i18n.t('tryLater') });
    return;
  }
  chatInitObj.logId = getUiD();
  if (chatFileList) {
    chatInitObj.fileList = JSON.parse(JSON.stringify(chatFileList));
  }
  chatInitObj.sendType = 'text';
  chatInitObj.content = value;
  return chatInitObj;
};
// 自勾选数据处理
export const reportProcess = (list, listRef) => {
  let memoriesList = [];
  let reportArr = list.map(item => {
    let reportItem = JSON.parse(item.query);
    return reportItem;
  })
  let questionArr = reportArr.filter(item => item.type === 'send');
  let answerArr = reportArr.filter(item => item.type === 'receive');
  questionArr.forEach(item => {
    let obj = {
      question: { query: item.content }
    };
    let index = listRef.current.findIndex(lItem => lItem.logId === item.logId);
    let answerLogId = listRef.current[index + 1].logId;
    let answerItem = answerArr.filter(aItem => aItem.logId === answerLogId)[0];
    if (answerItem) {
      let content = isJsonString(answerItem.content) ? JSON.parse(answerItem.content) : answerItem.content;
      if (content.chartData) {
        let arr = []
        content.chartData?.forEach(cItem => {
          arr.push(JSON.parse(cItem));
        });
        content.chartData = arr;
      } else {
        content = { answer: content }
      }
      obj.answer = content
    } else {
      obj.answer = '';
    }
    memoriesList.push(obj);
  });
  answerArr.forEach(item => {
    let obj = {
      answer: item.content
    };
    let index = listRef.current.findIndex(lItem => lItem.logId === item.logId);
    let questionLogId = listRef.current[index - 1].logId;
    let questionItem = questionArr.filter(qItem => qItem.logId === questionLogId)[0];
    if (!questionItem) {
      obj.question = '';
      memoriesList.push(obj);
    }
  });
  return memoriesList;
};
// 流式接收消息数据处理
export const messageProcess = (instanceId, messageData, atAppInfo:any = undefined) => {
  let obj:any = {
    loading: false,
    openLoading: false,
    content: '',
    recieveType: 'form',
    type: 'receive',
    finished: true,
    checked: false,
    messageType: 'form',
    expiredList: [],
    status: messageData.status,
    logId: messageData.log_id,
    instanceId,
    path: messageData.formAppearance.iframeUrl || '',
    formConfig: {
      instanceId,
      parentInstanceId: messageData.parentInstanceId || '',
      type: 'edit',
      logId: messageData.log_id,
      formAppearance: messageData.formAppearance,
      formData: messageData.formData || {},
      startTime: messageData.startTime || '',
      _internal: messageData._internal ||'',
      nodeId: messageData.nodeId ||'',
      status: messageData.status
    }
  }
  if (atAppInfo) {
    obj.appName = atAppInfo.name;
    obj.appIcon = atAppInfo.attributes.icon;
    obj.isAt = true;
  }
  return obj;
};
// 流式接收消息数据处理
export const messageProcessNormal = (log, atAppInfo, messageData) => {
  let msg = '';
  let pictureList = [];
  let content = log.content;
  if (isJsonString(content)) {
    content = JSON.parse(content);
  };
  let referenceMeta: any = [];
  if (log.type === 'META_MSG') {
    let metaMsg = typeof(log.content) === 'string' ? JSON.parse(log.content) : log.content;
    msg = metaMsg.data;
    referenceMeta.push(metaMsg.reference);
  } else if (Array.isArray(content) && content.every(item => item.data && item.mime)) {
    msg = '';
    pictureList = content;
  } else {
    msg = log.content || '';
  }
  const regex = /```markdown(.*?)```/g;
  const replacedArr = msg.match(regex);
  let markdowned = msg.indexOf('```');
  if (replacedArr && replacedArr.length) {
    replacedArr.forEach((item) => {
      let str = item.substring(11, item.length - 3);
      msg = msg.replace(item, str);
    });
  }
  let recieveChatItem:any = {
    content: msg,
    loading: false,
    openLoading: false,
    checked: false,
    logId: log.msgId || uuidv4(),
    msgId: log.msgId,
    markdownSyntax: markdowned !== -1,
    type: 'receive',
    recieveType: 'msg',
    instanceId: messageData.instance_id,
    feedbackStatus: -1,
  };
  if (pictureList.length) {
    recieveChatItem.pictureList = pictureList;
  }
  if (log.type === 'META_MSG') {
    recieveChatItem.reference = referenceMeta;
    recieveChatItem.msgType = log.type;
  }
  if (atAppInfo) {
    recieveChatItem.appName = atAppInfo.name;
    recieveChatItem.appIcon = atAppInfo.attributes.icon;
    recieveChatItem.isAt = true;
  }
  return {
    msg,
    recieveChatItem,
  };
};
// 深拷贝
export const deepClone = (obj, hash = new WeakMap()) => {
  if (typeof obj !== 'object' || obj === null) {
    return obj;
  }
  if (obj instanceof Date) {
    return new Date(obj.getTime());
  }
  if (obj instanceof RegExp) {
    return new RegExp(obj);
  }
  if (Array.isArray(obj)) {
    return obj.map(item => deepClone(item));
  }
  const newObj = {};
  for (let key in obj) {
    if (obj.hasOwnProperty(key)) {
      newObj[key] = deepClone(obj[key]);
    }
  }
  return newObj;
};
// 文件类型设置
export const fileTypeSet = (type) => {
  type = type.toLowerCase();
  const audioType = ['mp3', 'wav', 'wmv'];
  const videoType = ['mp4', 'm2v', 'mkv', 'rmvb', 'wmv', 'avi', 'flv', 'mov', 'm4v'];
  const imgType = ['png', 'jpg', 'jpeg', 'bmp', 'gif'];
  const extrasType = ['rar', 'zip', 'exe', 'msi'];
  const docType = ['pdf', 'docx'];
  let fileType = '';
  if (audioType.includes(type)) {
    fileType = 'audio';
  } else if (videoType.includes(type)) {
    fileType = 'video';
  } else if (imgType.includes(type)) {
    fileType = 'image';
  } else if (extrasType.includes(type)) {
    fileType = 'extras';
  } else if (docType.includes(type)) {
    fileType = 'doc';
  } else {
    fileType = 'file';
  }
  return fileType
};
// 滚动底部
export const scrollBottom = () => {
  const messageBox = document.getElementById('chat-list-dom');
  messageBox?.scrollTo({
    top: messageBox.scrollHeight,
    behavior: 'instant',
  });
};

function escapeHTML(str) {
  return str.replace(/"/g, '&quot;')
    .replace(/'/g, '&apos;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');
}
