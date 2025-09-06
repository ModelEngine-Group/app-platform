<!--/*************************************************请勿修改或删除该文件**************************************************/-->
<template>
  <div id="element">
    <MyInput :receiveData="receiveData"></MyInput>
  </div>
</template>
<script setup>
  import { onMounted, ref, provide } from "vue";
  import MyInput from "./components/form.vue";
  import { getQueryParams } from "./utils/index";

  const receiveData = ref({});

  onMounted(() => {
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
      window.removeEventListener('message', handleMessage);

      ro.unobserve(document.querySelector("#element"));
      ro.disconnect();
    };
  });

  const uniqueId = getQueryParams(window.location.href);

  // 处理消息
  const handleMessage = (event) => {
    receiveData.value = event.data;
  };

  // 终止会话
  const terminateClick = (params) => {
    window.parent.postMessage(
      { type: "app-engine-form-terminate", ...params, uniqueId },
      receiveData.value.origin
    );
  };

  // 继续会话
  const resumingClick = (params) => {
    window.parent.postMessage(
      { type: "app-engine-form-resuming", ...params, uniqueId },
      receiveData.value.origin
    );
  };

  // 重新生成
  const reStartClick = (params) => {
    window.parent.postMessage(
      { type: "app-engine-form-restart", ...params, uniqueId },
      receiveData.value.origin
    );
  };

  provide("terminateClick", terminateClick);
  provide("resumingClick", resumingClick);
  provide("reStartClick", reStartClick);
</script>
