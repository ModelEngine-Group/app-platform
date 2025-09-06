<!-- /*************************************************此处为人工表单示例***************************************************/
/*********************************************receiveData为表单的初始化入参数据***********************************************/
/*******************************************terminateClick为调用终止对话接口的回调方法**********************************/
/**************************************resumingClick为调用继续对话接口的回调方法***************************************/
/***************************************restartClick为调用重新对话接口的回调方法**************************************/ -->
<template>
  <div class="form-warp">
    <el-form ref = "form" label-position="right" label-width="auto" :model="formData" style="max-width: 600px">
      <el-form-item label="image">
        <img src="../assets/images/empty.png" alt="" height="100px" width="100px"/>
      </el-form-item>
      <el-form-item label="a" prop="a">
        <el-input v-model="formData.a" />
      </el-form-item>
      <el-form-item label="b" prop="b">
        <el-select v-model="formData.b" placeholder="请选择">
          <el-option label="Demo1" value="demo1" />
          <el-option label="Demo2" value="demo2" />
        </el-select>
      </el-form-item>
      <el-form-item label="button">
        <div class="form-button-list">
          <el-button @click="handleClick">继续对话</el-button>
          <el-button @click="handleRestartClick">重新对话</el-button>
          <el-button @click="handleTerminateClick">终止对话</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
  import { defineProps, ref, inject, watch } from "vue";
  const props = defineProps({
    receiveData: {
      type: Object,
      required: false,
      default: () => { }
    }
  });
  const formData = ref({a: '', b: ''});
  const form = ref(null);
  const terminateClick = inject("terminateClick");
  const resumingClick = inject("resumingClick");
  const reStartClick = inject("reStartClick");

  // 初始化表单数据
  watch(() => props.receiveData,
      (newValue) => {
    if (newValue.data) {
      formData.value = newValue.data;
    }
  });

  // 继续对话
  const handleClick = () => {
    resumingClick(JSON.parse(JSON.stringify({ params: formData.value })));
  };

  // 重新对话
  const handleRestartClick = () => {
    reStartClick(JSON.parse(JSON.stringify({ params: formData.value })));
  };

  // 终止对话
  const handleTerminateClick = () => {
    terminateClick({content: "终止会话"});
  };
</script>

<style lang="scss">
  .form-wrap {
    padding: 20px;
    .form-button-list {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
</style>
