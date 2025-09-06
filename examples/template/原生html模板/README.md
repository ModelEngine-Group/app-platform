# 自定义组件开发说明
## 前提条件
* 开发工具建议使用vscode

## 操作步骤
### 约束条件
上传的组件包需要为zip压缩包，解压后大小不能超过5M，包含且只能包含三个部分：
* 表单代码打包的静态资源文件 build文件夹。
* 表单的出入参配置文件 config.json。
* 表单的预览图 form.jpg/form.png/form.jpeg 大小不能超过1M。
## 开发组件代码
### 创建文件
* 用户在 根目录下建立html文件。
### 表单获取流程数据
在流程中使用智能表单节点或结束节点选用表单时，可以将前序节点的输出作为表单初始化的数据使用。假设表单需要的数据结构为：
```json
{
   "a": "你好",
   "b": "Demo1"
}
```
在表单里使用如下代码：
data就是由流程传输给表单的，格式为{“a": "", "b": ""}的json数据，可以使用这个数据来初始化表单。  

通信过程为：
1. 智能表单监听 `message` 消息后，发送消息通知流程，智能表单已经启动，可以接收流程数据。
2. 流程接收到 `app-engine-form-ready` 消息后，发送流程数据给智能表单。

代码如下：
```javascript
window.addEventListener('message', handleMessage);
window.parent.postMessage({ type: 'app-engine-form-ready', uniqueId }, '*');
```
注意：这里receiveData的数据结构与config.json的入参配置一致。
```javascript
// 具体代码可见index.js示例
let receiveData = {};
const handleMessage = (event) => {
    receiveData = event.data;
    initializeFormData(receiveData.data);
    window.removeEventListener("message", handleMessage);
  };
```
### 表单调用内置接口
平台提供了三个内置接口：继续对话（resumingClick），重新对话（restartClick），终止对话（terminateClick）。使用这几个内置接口，可以与流程进行交互。
在表单里使用如下代码，可以以调用方法的形式使用内置接口。
```javascript
  // 终止会话
const terminateClick = (params) => {
    window.parent.postMessage(
      { type: "app-engine-form-terminate", ...params, uniqueId },
      receiveData.origin
    );
  };
  // 继续会话
  const resumingClick = (params) => {
    window.parent.postMessage(
      { type: "app-engine-form-resuming", ...params, uniqueId },
      receiveData.origin
    );
  };
  // 重新生成
  const reStartClick = (params) => {
    window.parent.postMessage(
      { type: "app-engine-form-restart", ...params, uniqueId },
      receiveData.origin
    );
  };
```
1. 终止对话接口
终止对话接口适用于配置在**智能表单**节点的表单，适用于用户希望在流程的中间过程中，想要终止本地对话的场景。具体的过程是：
```
应用流程进行到智能表单节点，流程暂停 ---> 用户与表单进行交互，触发终止对话接口 ---> 流程终止，对话结束
```
使用示例：
```
如果表单里有按钮用于触发终止对话：
<button class="btn btn-primary terminate">终止对话</button>


调用终止对话接口:
document.querySelector('.terminate').addEventListener('click', (event) => {
  event.preventDefault();
  terminateClick({ content: '终止会话' });
});
如果希望点击终止对话后，显示的文本是"终止对话"
terminateClick({ content: "终止会话" });
```
**注意：在结束节点使用的表单如果调用终止对话接口，会出现错误。**
2. 继续对话接口
继续对话接口适用于配置在**智能表单**节点的表单，适用于用户希望在流程的中间过程使用表单，进行一次人工交互，交互结束后流程继续的场景。具体的过程是：
```
应用流程进行到智能表单节点，流程暂停 ---> 用户与表单进行交互，触发继续对话接口 ---> 流程继续进行到下一个节点
```
使用示例：
```
如果表单里有按钮用于触发继续对话：
<button type="submit" class="btn btn-primary resuming">继续对话</button>

调用继续对话接口:
document.querySelector('.resuming').addEventListener('click', (event) => {
  event.preventDefault();
  resumingClick({ params: getFormData() });
});
如果表单的出参有两个，String 类型的"a"，Int 类型的"b":
resumingClick({ params: {a: "hello", b: 1 }});
```
**注意：在结束节点使用的表单如果调用继续对话接口，会出现错误。**
3. 重新对话接口
   重新对话接口适用于配置在**结束**节点的表单，适用于用户希望在流程结束后，想使用相同的问题重新再发起一次对话。具体的过程是：
```
应用流程进行到结束节点，流程结束 ---> 表单展示流程输出，用户与表单进行交互，触发重新对话接口 ---> 再次从头发起一次流程
```
使用示例：
```
如果表单里有按钮用于触发重新对话：
<button type="submit" class="btn btn-primary reStart">重新对话</button>

调用重新对话接口:
document.querySelector('.reStart').addEventListener('click', (event) => {
  event.preventDefault();
  reStartClick({ params: getFormData() });
});
如果表单的出参有两个，String 类型的"a"，Int 类型的"b":
restartClick({ params: {a: "hello", b: 1 }});
```
**注意：在智能表单节点使用的表单如果调用重新对话接口，需要先执行终止对话接口，将流程停止，再执行重新对话接口。**
### 表单调用外部接口
* 如果想在表单中调用非平台内置的接口，需要保证接口支持跨域调用。
### 表单使用图片
* 表单使用图片文件时，需要将图片放置在/assets/images目录下
* 表单路径需要写为"./assets/images/图片文件名
示例：
```
  <img src="./assets/images/empty.png" alt="">
```
### 表单添加样式文件
* 可以在/src/styles目录下添加样式文件，请使用.css类型
### 调试表单
```
在浏览器点击查看index.html，可以查看是否符合预期
```
### 打包表单代码
```
  新建build文件夹，将表单文件index.html、index.js、assets文件夹、css文件夹放置在build文件夹
```
## 表单出入参配置
* 表单的出入参配置需要为json文件，名称为config.json。
* 文件内容表示表单的出入参的类型、描述以及参数顺序等信息，需要符合[json schema规范](https://json-schema.apifox.cn/)。
### 约束
* 最外层parameters字段是入参，入参第一层必须type为object。
* 必须包含name，支持中文、英文、数字、空格、中划线、下划线组合。
* 可以包含description, 对参数进行描述。
* 必须包含parameters。
* 必须包含required, 内容不可以为properties下参数名之外的参数名。
* 可以包含order, 若写必须为properties下所有参数名的列表；若不写，则默认按照properties下所有参数名的顺序。
* 必须包含return，return字段是出参。
### 示例
```
// 这是一个表单的出入参都为 字段名："a", 类型：String;
{
  "schema": {
    "parameters": {
      "type": "object",
      "required": [
        "a",
        "b"
      ],
      "properties": {
        "a": {
          "type": "string",
          "default": ""
        },
        "b": {
          "type": "string",
          "default": ""
        }
      }
    },
    "return": {
      "type": "object",
      "properties": {
        "a": {
          "type": "string"
        },
        "b": {
          "type": "string"
        }
      }
    }
  }
}
```
## 表单预览图
* 表单预览图的类型支持为.jpg/.png/.jpeg, 名称为form，大小不超过1M。
## 打包
* 将build文件夹、config.json、form.png打成zip压缩包，压缩包名称支持大小写英文、中文和数字的字符串,可以包含中划线(-)和下划线(_),但不能以中划线(-)和下划线(_)开头或结尾。