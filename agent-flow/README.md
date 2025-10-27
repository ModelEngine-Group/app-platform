# @fit-elsa/agent-flow

## 简介

@fit-elsa/agent-flow 是基于React的前端应用模块，为 @fit-elsa/elsa 核心框架提供React封装和UI组件。

## 功能亮点

### 集成React能力
- 基于Context的上下文传递
- 节点渲染缓存：React.memo + 自定义shouldComponentUpdate

### 集成Ant Design能力
- 基于Form组件的实时校验提示系统
- 基于Tree组件封装的节点上下文观察者机制

## 安装

```bash
npm install @fit-elsa/agent-flow @fit-elsa/elsa
```

## 快速开始

### 基本用法

```jsx
import React from 'react';
import { JadeFlow, createGraphOperator } from '@fit-elsa/agent-flow';

function MyFlowApp() {
  // 创建图形操作器
  const graphOperator = createGraphOperator();
  
  // 处理图形数据变更
  const handleGraphChange = (graphData) => {
    console.log('图形数据已变更:', graphData);
  };
  
  return (
    <div style={{ width: '100%', height: '100vh' }}>
      <JadeFlow
        graphOperator={graphOperator}
        onGraphChange={handleGraphChange}
        height={600}
        width={800}
      />
    </div>
  );
}

export default MyFlowApp;
```

### 多会话示例

```jsx
import React, { useState } from 'react';
import { MultiConversation, MultiConversationContent } from '@fit-elsa/agent-flow';

function MultiConvApp() {
  const [selectedConversation, setSelectedConversation] = useState(null);
  
  const conversations = [
    { id: '1', title: '会话 1', content: '这是第一个会话的内容' },
    { id: '2', title: '会话 2', content: '这是第二个会话的内容' }
  ];
  
  return (
    <div style={{ display: 'flex', height: '100vh' }}>
      <div style={{ width: '300px', borderRight: '1px solid #ccc' }}>
        <MultiConversation
          conversations={conversations}
          onSelectConversation={setSelectedConversation}
          selectedConversationId={selectedConversation?.id}
        />
      </div>
      <div style={{ flex: 1, padding: '20px' }}>
        {selectedConversation && (
          <MultiConversationContent
            conversation={selectedConversation}
          />
        )}
      </div>
    </div>
  );
}

export default MultiConvApp;
```

## 组件API

### JadeFlow

主要的流程图组件，用于显示和编辑流程图。

**属性**:
- `graphOperator`: 图形操作器实例
- `onGraphChange`: 图形数据变更时的回调函数
- `height`: 流程图高度
- `width`: 流程图宽度
- `readOnly`: 是否只读模式
- `locale`: 语言设置

### MultiConversation

多会话列表组件，用于显示和选择多个会话。

**属性**:
- `conversations`: 会话列表数据
- `onSelectConversation`: 选择会话时的回调函数
- `selectedConversationId`: 当前选中的会话ID

### MultiConversationContent

会话内容组件，用于显示选中会话的详细内容。

**属性**:
- `conversation`: 会话对象数据

### createGraphOperator

创建图形操作器的函数，用于管理图形数据和操作。

## 开发

### 构建命令

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

## 许可证

MIT License

## 版权

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/