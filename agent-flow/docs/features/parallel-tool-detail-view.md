# 并行节点工具详情查看功能

## 功能概述

为并行节点的工具卡片添加了查看详情功能，用户可以点击眼睛图标跳转到对应工具流的详情页面。

## 实现细节

### 1. 数据存储

在选择工具时，系统会自动提取并存储以下信息：
- **appId**: 从 `selectedData.runnables.APP.appId` 提取
- **tenantId**: 从 `selectedData.schema.parameters.properties.tenantId.default` 提取

这些信息会保存在每个工具的 `value` 数组中，随节点配置一起持久化。

### 2. UI 增强

每个并行任务的工具卡片的 header 显示：
- **工具输出名称** (左侧)
- **眼睛图标** (中间，仅当存在 appId 和 tenantId 时显示) - 用于查看详情
- **删除图标** (右侧)

眼睛图标按钮特性：
- **仅在工具包含 appId 和 tenantId 时显示**
- 鼠标悬停时显示"工具详情"提示
- 在禁用状态下不可点击
- 点击时不会触发父元素的点击事件

### 3. 跳转逻辑

点击眼睛图标时：
1. 从工具配置中获取 `appId` 和 `tenantId`
2. 从 graph configs 中获取 `endpoint` 配置（如果未配置，使用当前域名）
3. 构建跳转 URL: `{endpoint}/app-develop/{tenantId}/add-flow/{appId}?type=workFlow`
4. 在新标签页中打开目标页面

### 4. 配置说明

如果需要自定义 endpoint，可以在 configs 中配置：

```javascript
configs.push({
  node: 'parallelNodeState',
  urls: {
    endpoint: 'https://your-domain.com'
  }
});
```

如果未配置 endpoint，系统会自动使用 `window.location.origin`。

## 修改文件列表

1. **ParallelPluginItem.jsx**
   - 导入 `EyeOutlined` 图标
   - 添加 `handleViewDetails` 函数实现跳转逻辑
   - 添加 `renderViewIcon` 函数渲染眼睛图标（包含条件判断）
   - 在工具卡片 header 上显示眼睛图标

2. **ParallelTopBar.jsx**
   - 在 `onSelect` 中提取 `appId` 和 `tenantId`
   - 更新 `handlePluginAdd` 调用，传递 `appId` 和 `tenantId`

3. **ParallelWrapper.jsx**
   - 更新 `handlePluginAdd` 函数签名，接收 `appId` 和 `tenantId`
   - 更新 dispatch action，传递 `appId` 和 `tenantId`

4. **reducers/reducers.js**
   - 更新 `AddPluginByMetaDataReducer` 中的 `PLUGIN_INPUT` 对象
   - 将 `appId` 和 `tenantId` 作为新的字段存储到 plugin value 中

## 数据结构

并行节点中的每个工具数据结构示例：

```javascript
{
  id: "uuid",
  type: "Object",
  from: "Expand",
  value: [
    { name: "uniqueName", value: "tool-unique-name" },
    { name: "args", value: [...] },
    { name: "order", value: [...] },
    { name: "outputName", value: "task_1" },
    { name: "tags", value: [...] },
    { name: "appId", value: "app-123" },      // 新增
    { name: "tenantId", value: "tenant-456" } // 新增
  ]
}
```

## 注意事项

1. **眼睛图标显示条件**: 只有当工具同时包含 `appId` 和 `tenantId` 信息时，才会显示眼睛图标
2. **老数据兼容**: 对于不包含 `appId` 和 `tenantId` 的老数据，不显示眼睛图标，只显示工具名称和删除功能
3. 跳转在新标签页中打开，不影响当前编辑状态
4. 即使没有配置 endpoint，系统也会使用当前域名作为默认值
5. 每个并行任务独立显示眼睛图标（如果满足条件）

## 兼容性

- 不会影响现有功能
- 对于不包含 `appId` 和 `tenantId` 的旧数据，不显示眼睛图标，工具卡片正常显示输出名称和删除功能
- 所有更改向后兼容，老数据可以正常使用
- 与循环节点的实现保持一致，使用相同的数据提取逻辑

## 与循环节点的对比

| 特性 | 循环节点 | 并行节点 |
|------|---------|---------|
| 数据提取 | 相同 | 相同 |
| 存储位置 | toolInfo 对象 | plugin.value 数组 |
| 显示位置 | 工具卡片右侧 | 折叠面板 header 中间 |
| 跳转逻辑 | 相同 | 相同 |
| 配置节点名 | loopNodeState | parallelNodeState |

