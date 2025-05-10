# 画布 # 自定义组件开发说明

## 前提条件

* 开发工具建议使用 VSCode

* 基础环境: Node.js 版本 >= 18, npm 版本 >= 10

* React 组件建议使用 Ant Design (版本: 4.24.13)

## 操作步骤

### 约束条件

上传的组件包必须是 zip 压缩包，解压后文件大小不得超过 5M，且必须包含三部分：

* build 文件夹: 表单代码打包后的静态资源

* config.json: 表单的输入输出参配置文件

* form.jpg/png/jpeg: 表单预览图，大小不得超过 1M

## 开发组件代码

### 创建文件

* 在 `/src/components` 目录下创建 `.tsx` 类型的组件文件

### 表单获取流程数据

用于初始化表单数据：

```tsx
const { data, terminateClick, resumingClick, restartClick } = useContext(DataContext);
```

`data` 为 json 数据，结构与 config.json 的输入参配置一致

### 表单调用内置接口

**1. 终止对话 terminateClick()**

```tsx
<Button onClick={onTerminateClick}>终止对话</Button>

const onTerminateClick = () => {
  terminateClick({ content: "终止会话" });
}
```

**注意：结束节点不能调用 terminateClick**

**2. 继续对话 resumingClick()**

```tsx
<Button onClick={onResumeClick}>继续对话</Button>

const onResumeClick = () => {
  resumingClick({ params: { a: "hello", b: 1 } });
}
```

**注意：结束节点不能调用 resumingClick**

**3. 重新对话 restartClick()**

```tsx
<Button onClick={onRestartClick}>重新对话</Button>

const onRestartClick = () => {
  restartClick({ params: { a: "hello", b: 1 } });
}
```

**注意：如果在智能表单节点使用，需先调 terminateClick 再 restartClick**

### 调用外部接口

* 要求接口支持跨域

### 使用图片

* 图片文件放在 `/src/assets/images`

* 路径: `./src/assets/images/xxx.png`

```tsx
<img src="./src/assets/images/empty.png" alt="" height="100px" width="100px"/>
```

### 表单样式文件

* 可以在 `/src/styles` 目录下添加 `.scss` 样式文件

### 调试表单

```bash
npm install
npm start
```

* 模拟数据 app.tsx:

```ts
receiveData: {
  data: { a: "你好", b: "Demo1" },
  uniqueId: 10,
  origin: "http://127.0.0.1:3350",
  tenantId: "fh47kl"
}
```

### 打包

```bash
npm run build
```

## 表单输入输出参 config.json

### 基础规范

* 格式需符合[json schema规范](https://json-schema.apifox.cn/)

* 格式示例：

```json
{
  "schema": {
    "parameters": {
      "type": "object",
      "required": ["a", "b"],
      "properties": {
        "a": { "type": "string", "default": "haha" },
        "b": { "type": "string", "default": "heihei" }
      }
    },
    "return": {
      "type": "object",
      "properties": {
        "a": { "type": "string" },
        "b": { "type": "string" }
      }
    }
  }
}
```

* 最外层parameters字段是入参，入参第一层必须type为object。

* 必须包含name，支持中文、英文、数字、空格、中划线、下划线组合。

* 可以包含description, 对参数进行描述。

* 必须包含parameters。

* 必须包含required, 内容不可以为properties下参数名之外的参数名。

* 可以包含order, 若写必须为properties下所有参数名的列表；若不写，则默认按照properties下所有参数名的顺序。

* 必须包含return，return字段是出参。

## 表单预览图

* 名称: form.jpg/png/jpeg

* 大小: 不超过 1M

## 打包规则

* 包含 build/、config.json、form.png

* 将build文件夹、config.json、form.png打成zip压缩包，压缩包名称支持大小写英文、中文和数字的字符串,可以包含中划线(-)和下划线(_),但不能以中划线(-)和下划线(_)开头或结尾。
