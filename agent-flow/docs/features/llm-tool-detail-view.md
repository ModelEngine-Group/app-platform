# 大模型节点工具详情查看功能

## 功能概述

为大模型节点的工具卡片添加了查看详情功能，用户可以点击眼睛图标跳转到对应工具流的详情页面。

## 实现细节

### 1. 数据存储

在选择工具时，系统会自动提取并存储以下信息：
- **appId**: 从 `selectedData.runnables.APP.appId` 提取
- **tenantId**: 从 `selectedData.schema.parameters.properties.tenantId.default` 提取

这些信息会保存在 `tools` 的 `value` 数组中，每个工具项包含 `appId` 和 `tenantId` 字段。

### 2. UI 增强

每个工具卡片显示：
- **工具图标** (左侧)
- **工具名称和类型** (中间)
- **版本号** (右侧)
- **眼睛图标** (右侧，仅当存在 appId 和 tenantId 时显示) - 用于查看详情
- **删除图标** (最右侧)

眼睛图标按钮特性：
- **仅在工具包含 appId 和 tenantId 时显示**
- 鼠标悬停时显示"工具详情"提示
- 在禁用状态下不可点击
- 点击时不会触发父元素的点击事件

### 3. 跳转逻辑

点击眼睛图标时：
1. 从工具配置中获取 `appId` 和 `tenantId`
2. 从 graph configs 中获取 `endpoint` 配置（如果未配置，使用当前域名）
3. 构建跳转 URL: `{endpoint}/app-develop/{tenantId}/add-flow/${appId}?type=workFlow`
4. 在新标签页中打开目标页面

### 4. 配置说明

如果需要自定义 endpoint，可以在 configs 中配置：

```javascript
configs.push({
  node: 'llmNodeState',
  urls: {
    endpoint: 'https://your-domain.com'
  }
});
```

如果未配置 endpoint，系统会自动使用 `window.location.origin`。

## 修改文件列表

1. **SkillForm.jsx**
   - 导入 `EyeOutlined` 图标
   - 在 `onSelect` 中提取 `appId` 和 `tenantId`
   - 添加 `handleViewDetails` 函数实现跳转逻辑
   - 添加 `renderViewIcon` 函数渲染眼睛图标（包含条件判断）
   - 在工具卡片上显示眼睛图标

2. **LlmFormWrapper.jsx**
   - 在 `processToolData` 函数中，从 `tool.value` 获取 `appId` 和 `tenantId`
   - 将 `appId` 和 `tenantId` 添加到 `toolOptions` 中

3. **reducers/reducers.js**
   - 更新 `AddSkillReducer` 中的 `newSkill` 对象
   - 将 `appId` 和 `tenantId` 作为新的字段存储

## 数据结构

大模型节点中的工具数据结构示例：

```javascript
{
  inputParams: [
    {
      name: "tools",
      value: [
        {
          id: "uuid",
          type: "String",
          from: "Input",
          value: "tool-unique-name",
          appId: "app-123",      // 新增
          tenantId: "tenant-456", // 新增
          name: "工具名称",       // updateTools 时更新
          tags: ["TOOL"],         // updateTools 时更新
          version: "1.0.0"        // updateTools 时更新
        }
      ]
    }
  ]
}
```

## 工作流程

1. **用户选择工具**: 用户点击"添加技能"按钮选择工具
2. **数据提取**: `onSelect` 回调从选择的数据中提取 `appId` 和 `tenantId`
3. **数据存储**: `AddSkillReducer` 将工具信息（包括 `appId` 和 `tenantId`）存储到 config 中
4. **获取详情**: `getSkillInfo` 函数从后端获取工具的详细信息（名称、标签、版本等）
5. **构建选项**: `processToolData` 函数构建 `toolOptions`，同时保留 `appId` 和 `tenantId`
6. **更新显示**: `updateTools` reducer 更新工具的显示信息
7. **渲染卡片**: `SkillContent` 组件渲染工具卡片，包括眼睛图标（如果有 `appId` 和 `tenantId`）

## 注意事项

1. **眼睛图标显示条件**: 只有当工具同时包含 `appId` 和 `tenantId` 信息时，才会显示眼睛图标
2. **老数据兼容**: 对于不包含 `appId` 和 `tenantId` 的老数据，不显示眼睛图标，只显示工具名称、版本和删除功能
3. 跳转在新标签页中打开，不影响当前编辑状态
4. 即使没有配置 endpoint，系统也会使用当前域名作为默认值
5. 工具信息通过 `getSkillInfo` 从后端获取，`appId` 和 `tenantId` 从本地 config 中保留

## 兼容性

- 不会影响现有功能
- 对于不包含 `appId` 和 `tenantId` 的旧数据，不显示眼睛图标，工具卡片正常显示所有其他功能
- 所有更改向后兼容，老数据可以正常使用
- 与循环节点和并行节点的实现保持一致，使用相同的数据提取逻辑

## 与其他节点的对比

| 特性 | 循环节点 | 并行节点 | 大模型节点 |
|------|---------|---------|-----------|
| 数据提取 | 相同 | 相同 | 相同 |
| 存储位置 | toolInfo 对象 | plugin.value 数组 | tools.value 数组 |
| 显示位置 | 工具卡片右侧 | 折叠面板 header | 工具卡片右侧 |
| 跳转逻辑 | 相同 | 相同 | 相同 |
| 配置节点名 | loopNodeState | parallelNodeState | llmNodeState |
| 额外处理 | - | - | 需要通过 getSkillInfo 获取工具详情 |

## 使用示例

```javascript
// 用户选择工具时，系统会自动提取数据
{
  uniqueName: "my-tool",
  name: "我的工具",
  tags: ["WATER_FLOW"],
  version: "1.0.0",
  runnables: {
    APP: {
      appId: "app-123"
    }
  },
  schema: {
    parameters: {
      properties: {
        tenantId: {
          default: "tenant-456"
        }
      }
    }
  }
}

// 存储后的数据结构
{
  id: "uuid",
  type: "String",
  from: "Input",
  value: "my-tool",
  appId: "app-123",
  tenantId: "tenant-456"
}

// 点击眼睛图标后跳转到
// https://your-domain.com/app-develop/tenant-456/add-flow/app-123?type=workFlow
```

