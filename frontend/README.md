## 技术栈

- 框架: React 18.3.1 + TypeScript 5.4.5

- UI库: Ant Design 4.24.13

- 状态管理: Redux Toolkit + React-Redux

- 路由: React Router 5.3.4

- 构建工具: Webpack 5.89.0

- 样式: SCSS + Less

- 国际化: i18next + react-i18next

- 微前端: single-spa-react

## 代码结构

### 根目录配置文件
```plaintext
├── package.json             # 项目依赖和脚本配置
├── tsconfig.json            # TypeScript配置
├── webpack.common.js        # Webpack通用配置 -独立环境
├── webpack.common.single.js # Webpack通用配置 -合并环境
├── webpack.dev.js           # 开发环境配置
├── webpack.prod.js          # 生产环境配置 --独立环境
├── webpack.prod.single.js   #生产环境配置 - 合并环境
└── proxy.conf.json          # 代理配置 -本地开发使用
```
###  src/ 源代码目录

#### 入口文件
```plaintext
src/
├── index.tsx                # 应用入口，支持微前端
├── app.tsx                  # 主应用组件
└── index.html               # HTML模板
```
#### 路由配置
```plaintext
src/router/
└── route.ts                 # 路由配置，定义所有页面路由
```
#### 样式文件
```plaintext
src/styles/
├── index.scss              # 主样式文件
├── common.scss             # 通用样式
├── workSpace.scss          # 通用样式
├── antStyle.scss           # Ant Design样式覆盖
├── appengine-bg.scss       # 背景图样式
└── appengine-bg-spa.scss   # 背景图样式 -合并环境
```
#### 国际化
```plaintext
src/locale/
├── i18n.ts                 # 国际化配置
├── zh_CN.json              # 中文语言包
└── en_US.json              # 英文语言包
```
#### 公共组件

- **这里的组件是公共使用的，会在多个页面都出现，修改时需要注意要兼容所有场景**
- **对于使用达到两次及以上的组件，就可以提取到公共组件里来**
```plaintext
components/
├── layout/                # 布局组件
│   ├── index.tsx          # 主布局组件
│   └── style.scoped.scss  # 布局样式
├── icons/                 # 图标组件 -放一些svg图
│   ├── index.tsx          # 图标入口
│   ├── app.tsx            # 应用图标
│   ├── base.tsx           # 基础图标
│   ├── knowledge-icon.tsx # 知识库图标
│   ├── model.tsx          # 模型图标
│   ├── node-icon.tsx      # 节点图标
│   ├── plugin.tsx         # 插件图标
│   └── table.tsx          # 表格图标
├── appCard/               # 应用卡片组件
├── empty/                 # 空状态组件
├── file-upload/           # 文件上传组件
├── go-back/               # 返回按钮组件
├── img-upload/            # 图片上传组件
├── pagination/            # 分页组件
├── plugin-card/           # 插件卡片组件
├── table-calendar-search/ # 表格日历搜索
├── table-filter/          # 表格过滤器
├── table-text-search/     # 表格文本搜索
├── timeLine/              # 时间线组件
└── upload/                # 上传组件
```
#### 页面组件
```plaintext
pages/
├── addFlow/              # 工作流编排页面
│   ├── index.tsx         # 主页面
│   ├── config.ts         # 配置 --给elsa使用，具体可以问镕希
│   ├── components/       # 子组件(16个文件)
│   ├── styles/           # 样式文件(7个)
│   └── utils/            # 工具函数
├── aippIndex/            # 应用配置页面
├── appDetail/            # 应用详情页
│   ├── overview/         # 预览页
│   ├── feedback/         # 反馈页
│   ├── evaluate/         # 评估页
│   └── analyse/          # 分析页
├── appDev/               # 应用开发页
├── apps/                 # 应用市场页
├── chatEngineHome/       # 首页应用
├── chatPreview/          # 聊天对话页  -这里比较复杂，拆分组件比较多
│   ├── components/       # 组件列表
│   └── utils/            #公共方法
├── chatRunning/          # 聊天运行页
├── components/           # 页面公共组件 -- 建议后续与公共组件目录合并
├── configForm/           # 配置表单页
│   ├── configUi/         # 应用配置config组件
│   └── index.tsx         # 应用配置页
├── detailFlow/           # 工作流详情页
├── helper.ts             # 帮助函数
├── httpTool/             # HTTP工具节点
├── intelligent-form/     # 智能表单页
└── plugin/               # 插件页面
```
#### 共享工具
```plaintext
shared/
├── eventsource-parser/  # 事件源解析器 -sse接口使用
├── hooks/               # 自定义Hooks --react机制
├── http/                # HTTP请求模块
│   ├── http.ts          # HTTP基础配置
│   ├── httpConfig.ts    # HTTP配置
│   ├── httpError.ts     # 错误处理
│   ├── aipp.ts          # AIPP相关API
│   ├── appBuilder.ts    # 应用构建API
│   ├── appDev.ts        # 应用开发API
│   ├── appEvaluate.ts   # 应用评估API
│   ├── apps.ts          # 应用API
│   ├── chat.ts          # 聊天API
│   ├── form.ts          # 表单API
│   ├── guest.ts         # 访客模式API
│   ├── knowledge.ts     # 知识库API
│   ├── plugin.ts        # 插件API
│   └── sse.ts           # SSE相关API
├── storage/             # 本地缓存工具方法
└── utils/               # 通用工具函数
```
#### 状态管理

**这个是用的redux，用来管理一些全局使用或者修改的变量**
**如果有变量是多个组件都使用的，建议放在这里**
```plaintext
store/
├── index.ts              # Store入口
├── store.ts              # Store配置
├── hook.ts               # Redux Hooks
├── appConfig/            # 应用配置状态
├── appInfo/              # 应用信息状态
├── chatStore/            # 聊天状态
├── collection/           # 收藏状态
├── common/               # 通用状态
├── flowTest/             # 流程测试状态
└── toolHttp/             # HTTP工具状态
```
#### 静态资源
```plaintext
assets/
├── icon.js              # 图标配置
├── images/              # 图片资源
│   ├── ai/              # AI相关图片
│   ├── appConfig/       # 应用配置图片
│   ├── appdevelop/      # 应用开发图片
│   ├── knowledge/       # 知识库图片
│   ├── model/           # 模型图片
│   ├── pluginModal/     # 插件模态框图片
│   └── source/          # 图片
├── svg/                 # SVG图标
└── tinymce/             # 富文本编辑器资源 用的是tinymce
├── lang/                # 语言包
└── skins/               # 皮肤样式
```
#### 通用方法
```plaintext
common/
├── dataUtil.ts           # 数据处理工具
└── util.ts               # 通用工具函数
```
#### 对话页面插件
```plaintext
plugins/
├── manifest.json        # 插件清单
├── plugin.js            # 插件脚本
└── plugin.sh            # 插件构建脚本
```
## 代码开发

### 本地启动
1. 在fit-framework代码仓，打包elsa
   ```bash
   cd fit-elsa/
   npm install
   npm run build:debug
   cd ../fit-elsa-react
   npm install
   npm run build
   ```
2. 在app-platform仓，修改frontend/proxy.conf.json文件
   ```plaintext
   修改接口代理的target为本地调试的后端域名，如http://localhost:8080
   示例：
   "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "pathRewrite": {
      "^/api/jober": "",
      "^/api": ""
     }
    }
   ```
3. 在app-platform仓，修改frontend/package.json文件

   更新elsa依赖包的地址，更新为当前代码仓，相对于本地fit-framework仓中打包elsa文件的路径，以下为示例:
   ```ts
   "@fit-elsa/elsa-core": "file:../../../fitframework-github/fit-framework/framework/elsa/fit-elsa",
   "@fit-elsa/elsa-react": "file:../../../fitframework-github/fit-framework/framework/elsa/fit-elsa-react"
   ```
4. 启动代码

   app-platform仓的frontend/下执行：
    ```bash
   npm install --force
   npm start
   ```
