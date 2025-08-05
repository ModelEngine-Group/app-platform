# AppPlatform

**AppPlatform 是一个前沿的大模型应用工程，旨在通过集成的声明式编程和低代码配置工具，简化 AI 应用的开发过程。本工程为软件工程师和产品经理提供一个强大的、可扩展的环境，以支持从概念到部署的全流程 AI 应用开发。**
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/license/MIT)
[![JDK](https://img.shields.io/badge/JDK-17-green.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Node](https://img.shields.io/badge/node-20-red.svg)](https://nodejs.org/en/download)

## 核心架构

1. **AppPlatform 后端模块**

   AppPlatform 后端基于 [FIT](https://github.com/ModelEngine-Group/fit-framework/tree/main/framework/fit/java) 框架，采用插件化式开发，包含应用管理模块和功能扩展模块。其中应用管理模块为 AppPlatform 的核心模块，用于提供创建、管理、调试、运行和维护 AI 应用，该提供一个高效快捷的方式来开发具有复杂交互功能的 AI 应用。功能扩展模块通过组件节点的方式，丰富流程编排的能力，用户可根据需求自由组合，构建出符合业务逻辑的 AI 应用，该模块为组件节点的底层逻辑实现。应用流程运行基于 [Waterflow](https://github.com/ModelEngine-Group/fit-framework/tree/main/framework/waterflow/java) 框架，方便高效地对流程和数据进行组织和处理。

2. **AppPlatform 前端模块**

   AppPlatform 前端采用 React 框架进行开发，基于函数式组件构建，通过模块化设计实现了应用开发，应用市场，智能表单和插件管理等核心功能模块。其中应用开发模块为核心模块，提供可视化界面支持AI应用的完整生命周期管理，包含了应用创建，编排，调试，运行，和发布全流程；智能表单模块可通过 Json Schema 自动渲染可交互表单，与 AI 模型服务集成，实现表单填写与实时推理；插件模块支持开发者上传自定义插件扩展应用工程能力，并提供了插件安装和卸载等功能。此外，前端流程编排还基于 [Elsa 图形引擎](https://github.com/ModelEngine-Group/fit-framework/tree/main/framework/elsa)，Elsa 图形引擎是一款基于原生 JS 打造而成的先进图形处理工具。通过统一的数据格式，可以让图形跨平台跨应用进行展示和协作，为用户提供灵活、高性能的图形渲染与交互能力，适用于复杂可视化场景的开发需求。

---------

## 关键特性

1. **低代码图形化界面**：产品人员可以通过直观的图形界面创建 AI 应用，而无需深入了解底层代码即可进行高效的编辑和调试。同时支持多模型协同运作，使用户能够根据特定的业务需求，将不同的 AI 模型通过编排整合到同一个应用流程中。
2. **强大的算子与调度平台**：通过 FIT 与 Waterflow 框架，AppPlatform 提供了一个高效、可扩展的后端架构，支持 Java、Python 等多种主流编程语言的算子开发，并通过智能调度实现优化的执行效率。
3. **共享与协作**： AppPlatform 的底层包含 Store 模版，用于将所有开发的 AI 应用统一存储，以此支持跨项目的复用和协作。开发者可以根据需要组合这些应用，打造更大的解决方案，或者利用社区提供的工具和模型。在 AppPlatform 中， AI 应用不仅限于传统意义上的 “应用”，它们可以是 “函数”、“RAG”、“智能体”等任何可解释和可执行的组件。这些组件在 Store 中以 “工具” 的形式展现，其元数据不仅提供了必要的解释，还为智能体自动调度这些工具提供了基础。

---------

## 安装数据库

### Windows 系统

- 下载并安装 [PostgresSQL](https://www.postgresql.org/download/) （**支持版本 ≥ 14**）
- 初始化数据。进入 `shell` 目录，使用 `bash` 工具执行 `build_win.sh`（当前不支持 `cmd` 执行，待规划）：

```
cd shell
sh build_win.sh ${ip} ${port} ${username} ${password}
```

其中参数 ip、port、username、password 分别指的是数据库主机地址、数据库端口、数据用户名、数据库密码。该文件会初始化数据库内置数据，以及人工表单功能所需的数据。

### Linux 系统

待规划

## 后端环境配置

开发环境配置

- 开发环境：`IntelliJ IDEA`
- Java 17
- 代码格式化文件：[CodeFormatterFromIdea.xml](CodeFormatterFromIdea.xml)
- `Maven` 配置：推荐版本 Maven 3.8.8+
- FIT 框架编译产物：参考 [FIT 框架](https://github.com/ModelEngine-Group/fit-framework) 的`环境配置`构建编译产物

**构建命令**

```
mvn clean install
```

**输出目录**

```
build/
```

**目录调整**

需要将输出目录与 FIT 框架的编译产物结合。将输出目录的 `plugins` 目录下的所有文件复制到框架输出目录的 `plugins` 下，将 `shared` 目录下的所有文件复制到框架输出目录的 `shared` 下。

> 后端模块基于 [FIT](https://ModelEngine-Group/fit-framework) 框架，启动方式采用了 [FIT 动态插件](https://github.com/ModelEngine-Group/fit-framework/blob/main/docs/framework/fit/java/quick-start-guide/03.%20%E4%BD%BF%E7%94%A8%E6%8F%92%E4%BB%B6%E7%9A%84%E7%83%AD%E6%8F%92%E6%8B%94%E8%83%BD%E5%8A%9B.md) 方式。

打开框架输出目录的 `conf/fitframework.yml` 文件，找到如下配置项

```yml
fit:
  beans:
    packages:
    - 'modelengine.fitframework'
    - 'modelengine.fit'
```

加入数据库配置项，修改后的配置项如下所示：

```yml
fit:
  beans:
    packages:
    - 'modelengine.fitframework'
    - 'modelengine.fit'
  datasource:
     primary: 'sample-datasource' # 表示所选用的示例数据源。
     instances:
        sample-datasource:
           mode: 'shared' # 表示该数据源的模式，可选共享(shared)或独占(exclusive)模式。
           url: 'jdbc:postgresql://${ip}:${port}/' # 将 ip 换成数据库服务器的 ip 地址，将 port 换成数据库服务器监听的端口。
           username: '${username}' # 将 username 替换为数据库的名称。
           password: '${password}' # 将 password 替换为数据库的密码。
           druid:
              initialSize: ${initialSize} # 将 initialSize 替换为连接池的初始化连接数。
              minIdle: ${midIdle} # 将 minIdle 替换为连接池的最小空闲连接数。
              maxActive: ${maxActive} # 将 maxActive 替换为数据库连接池的最大活动连接数。
              # 可根据具体需求，添加连接池所需配置项。
```

**启动命令**

```
fit start -Dfit.profiles.active=prod
```

> 这里直接使用了 `fit` 命令，该命令请参考 `fit-framework` 项目的[指导手册](https://github.com/ModelEngine-Group/fit-framework/blob/main/docs/framework/fit/java/quick-start-guide/03.%20%E4%BD%BF%E7%94%A8%E6%8F%92%E4%BB%B6%E7%9A%84%E7%83%AD%E6%8F%92%E6%8B%94%E8%83%BD%E5%8A%9B.md)。
> 
> 当前，`app-platform` 使用了 `fit` 的 3.5.0-M6 版本，因此，如果采用手动编译，需要在 `fit-framework` 仓库中切换到 `v3.5.0-M6` 标签处进行编译构建操作。

---------

## 前端环境配置

- 开发环境：`WebStorm`、`Visual Studio Code`

- 环境要求：node.js  >= 20

- ELSA 框架编译产物：参考 [ELSA](https://github.com/ModelEngine-Group/fit-framework/blob/main/framework/elsa/README.md) 的编译构建章节

**修改 ELSA 依赖地址**

进入目录 `app-engine\frontend` ，搜索 `package.json` 文件的 ELSA 依赖地址：

```
"dependencies": {
    "@fit-elsa/elsa-core": "file:${fitElsaCoreUrl}",
    "@fit-elsa/elsa-react": "file:${fitElsaReactUrl}",
```

将 `${fitElsaCoreUrl}` 和 `${fitElsaReactUrl}` 分别改成 `ELSA` 框架编译产物 `fit-elsa-core` 和 `fit-react` 的目录地址即可。

**修改代理文件**

修改 `AppPlatform/frontend` 目录下的 `proxy.config.json` 文件，可以修改需要访问的后端地址。如本地后端地址是 `http://127.0.0.1:8080` 。可以按照如下示例配置：

```json
{
    "/api": {
       "target": "http://127.0.0.1:5520",
       "secure": false,
       "changeOrigin": true,
       "pathRewrite": {
          "^/api": ""
       }
    }
}
```

**依赖安装**

```
cd app-engine/frontend/
npm install
```

**打包构建**

```
npm run build
```

**启动命令**

```
npm run start
```
---------
## 快速开始

**模型配置**

在对话中使用大模型功能，需要对模型进行配置，包括大模型的地址和鉴权信息。
首先在首页的`应用市场`一栏中找到 `模型配置应用`，并点击该应用。点击右上角`创意灵感` 的`开始配置`，如下图所示：
![image-20250508203127410](doc\images\readme\model_config_inspiration.png)
然后点击回答的 `添加模型` 按钮，输入模型名称、API Key 和模型地址，并点击确认。此时模型添加成功。

**应用创建**

在首页的`应用开发`一栏中点击`创建空白应用`。如下所示：
![image-20250508204618312](doc\images\readme\app_create.png)
输入所要创建的应用名称和简介，并点击 `创建`按钮，即可创建 AI 应用。接着在跳转后的应用配置页面上，在 `大模型` 一栏中选择自定义配置的模型。此时即可在对话框进行对话。如下所示：
![image-20250508205124203](doc\images\readme\app_chat.png)

## 文档

您可以从`docs`目录查看项目的完整文档，文档包含 AppPlatform 的快速入门指南和用户指导手册。

**待完善**

## 贡献

欢迎贡献者加入本项目。
请阅读 [CONTRIBUTING.md](CONTRIBUTING.md)，这将指导您完成分支管理、标签管理、提交规则、代码审查等内容。遵循这些指导有助于项目的高效开发和良好协作。

## 联系我们

1. 如果发现问题，可以在该项目的 `Issue` 模块内提出。
