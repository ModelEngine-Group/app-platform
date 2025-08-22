/*************************************************此处为人工表单示例***************************************************/
/*********************************************receiveData.data为表单的初始化入参数据***********************************************/
/*******************************************terminateClick为调用终止对话接口的回调方法**********************************/
/**************************************resumingClick为调用继续对话接口的回调方法***************************************/
/***************************************restartClick为调用重新对话接口的回调方法**************************************/

/*************************************************请勿修改或删除该范围内代码**************************************************/
let receiveData = {};

document.addEventListener("DOMContentLoaded", function () {
  const handleMessage = (event) => {
    receiveData = event.data;
    initializeFormData(receiveData.data);
    window.removeEventListener("message", handleMessage);
  };
  window.addEventListener("message", handleMessage);
  window.parent.postMessage({ type: 'app-engine-form-ready', uniqueId }, '*');

  const ro = new ResizeObserver((entries) => {
    entries.forEach((entry) => {
      const height = entry.contentRect.height;
      window.parent.postMessage(
        { type: "app-engine-form-resize", height, uniqueId },
        "*"
      );
    });
  });
  ro.observe(document.querySelector("#element"));
  return () => {
    ro.unobserve(document.querySelector("#element"));
    ro.disconnect();
  };
});

// 获取uniqueId
const getQueryParams = (url) => {
  const regex = /uniqueId=([a-zA-Z0-9-]+)/;
  const match = url.match(regex);
  if (match && match.length > 1) {
    return match[1];
  } else {
    return null;
  }
};
const uniqueId = getQueryParams(window.location.href);
/*************************************************请勿修改或删除该范围内代码**************************************************/

// 初始化表单数据
const initializeFormData = (data) => {
  document.getElementById('inputA').value = data.a;
  document.getElementById('selectB').value = data.b;
}

// 获取表单数据
const getFormData = () => {
  return {
    a: document.getElementById('inputA').value,
    b: document.getElementById('selectB').value
  }
}

// 终止对话
const terminateClick = (params) => {
  window.parent.postMessage(
    { type: "app-engine-form-terminate", ...params, uniqueId },
    receiveData.origin
  );
};

// 继续对话
const resumingClick = (params) => {
  window.parent.postMessage(
    { type: "app-engine-form-resuming", ...params, uniqueId },
    receiveData.origin
  );
};

// 重新对话
const reStartClick = (params) => {
  window.parent.postMessage(
    { type: "app-engine-form-restart", ...params, uniqueId },
    receiveData.origin
  );
};

document.getElementById('selectB').addEventListener('change', (event) => {
  document.getElementById('selectB').value = event.target.value;
});

document.getElementById('inputA').addEventListener('input', (event) => {
  document.getElementById('inputA').value = event.target.value;
});

document.querySelector('.resuming').addEventListener('click', (event) => {
  event.preventDefault();
  resumingClick({ params: getFormData() });
});

document.querySelector('.reStart').addEventListener('click', (event) => {
  event.preventDefault();
  reStartClick({ params: getFormData() });
});

document.querySelector('.terminate').addEventListener('click', (event) => {
  event.preventDefault();
  terminateClick({ content: '终止会话' });
});