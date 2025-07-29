# 自定义组件开发说明
## 前提条件
* 开发工具建议使用vscode， 基础安装要求建议node版本为18及以上, npm版本为10及以上。
* React组件建议使用antd组件库，模板默认版本为4.24.13。

## 操作步骤
### 约束条件
上传的组件包需要为zip压缩包，解压后大小不能超过5M，包含且只能包含三个部分：
* 表单代码打包的静态资源文件 build文件夹。
* 表单的出入参配置文件 config.json。
* 表单的预览图 form.jpg/form.png/form.jpeg 大小不能超过1M。
## 开发组件代码
### 创建文件
* 用户在 /src/components文件夹下创建自己的组件文件，类型为.tsx。
### 表单获取流程数据
在流程中使用智能表单节点或结束节点选用表单时，可以将前序节点的输出作为表单初始化的数据使用。假设表单需要的数据结构为：
```
{
   "a": "你好",
   "b": "Demo1"
}
```
在表单里使用如下代码：
data就是由流程传输给表单的，格式为{“a": "", "b": ""}的json数据，可以使用这个数据来初始化表单。
注意：这里data的数据结构与config.json的入参配置一致。
```
const {data, terminateClick, resumingClick, restartClick} = useContext(DataContext);
```
### 表单调用内置接口
平台提供了三个内置接口：继续对话（resumingClick），重新对话（restartClick），终止对话（terminateClick）。使用这几个内置接口，可以与流程进行交互。
在表单里使用如下代码，可以以调用方法的形式使用内置接口。
```
const {data, terminateClick, resumingClick, restartClick} = useContext(DataContext);
```
1. 终止对话接口
终止对话接口适用于配置在**智能表单**节点的表单，适用于用户希望在流程的中间过程中，想要终止本地对话的场景。具体的过程是：
```
应用流程进行到智能表单节点，流程暂停 ---> 用户与表单进行交互，触发终止对话接口 ---> 流程终止，对话结束
```
使用示例：
```
如果表单里有按钮用于触发终止对话：
<Button onClick={onTerminateClick}>终止对话</Button>

调用终止对话接口:
const onTerminateClick = () => {
   terminateClick({content: //字符串，终止对话后希望显示的文本});
}
如果希望点击终止对话后，显示的文本是"终止对话"
terminateClick({content: "终止会话"});
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
<Button onClick={onResumeClick}>继续对话</Button>

调用继续对话接口:
const onResumeClick = () => {
   resumingClick({params: //这里是表单的出参数据});
}
如果表单的出参有两个，String 类型的"a"，Int 类型的"b":
resumingClick({params: {a: "hello", b: 1}});
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
<Button onClick={onRestartClick}>重新对话</Button>

调用重新对话接口:
const onRestartClick = () => {
   restartClick({params: //这里是表单的出参数据});
}
如果表单的出参有两个，String 类型的"a"，Int 类型的"b":
restartClick({params: {a: "hello", b: 1}});
```
**注意：在智能表单节点使用的表单如果调用重新对话接口，需要先执行终止对话接口，将流程停止，再执行重新对话接口。**
### 表单调用外部接口
* 如果想在表单中调用非平台内置的接口，需要保证接口支持跨域调用。
### 表单使用图片
* 表单使用图片文件时，需要将图片放置在/src/assets/images目录下
* 表单路径需要写为"./src/assets/images/图片文件名
示例：
```
<img src="./src/assets/images/empty.png" alt="" height="100px" width="100px"/>
```
### 表单添加样式文件
* 可以在/src/styles目录下添加样式文件，请使用.scss类型
### 调试表单
```
执行 npm install
执行 npm start 
启动表单项目，可以查看样式是否符合预期
```
* 模拟数据传输
```
可以通过设置app.tsx文件里的receiveData来模拟数据传输给表单
示例 receiveData: {
   data: {
     "a": "你好",
     "b": "Demo1"
   }, // 真正的表单需要的输入
   uniqueId: 10, // 任意数字即可
   origin: http://127.0.0.1:3350, // 当前调试本地窗口origin
   tenantId: fh47kl // 任意uuid即可
}
```

### 打包表单代码
```
执行 npm run build 将组件打包在build文件夹里
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
          "default": "haha"
        },
        "b": {
          "type": "string",
          "default": "heihei"
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