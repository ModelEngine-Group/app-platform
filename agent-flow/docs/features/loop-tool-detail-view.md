# 循环节点工具详情查看功能

## 功能概述

为循环节点的工具卡片添加了查看详情功能，用户可以点击眼睛图标跳转到对应工具流的详情页面。

## 实现细节

### 1. 数据存储

在选择工具时，系统会自动提取并存储以下信息：
- **appId**: 从 `selectedData.runnables.APP.appId` 提取
- **tenantId**: 从 `selectedData.schema.parameters.properties.tenantId.default` 提取

这些信息会保存在 `toolInfo` 对象中，随节点配置一起持久化。

### 2. UI 增强

每个选中的工具卡片显示：
- **工具名称** (左侧)
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
5. 触发 `VIEW_TOOL_DETAIL` 事件（供外部监听）

### 4. 配置说明

如果需要自定义 endpoint，可以在 configs 中配置：

```javascript
configs.push({
  node: 'loopNodeState',
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
   - 添加 `renderViewIcon` 函数渲染眼睛图标
   - 在工具卡片上显示眼睛图标

2. **LoopWrapper.jsx**
   - 更新 `handlePluginChange` 函数签名，接收 `appId` 和 `tenantId`
   - 更新 dispatch action，传递 `appId` 和 `tenantId`
   - 在组装 plugin 对象时包含 `appId` 和 `tenantId`

3. **reducers/reducers.js**
   - 更新 `ChangePluginByMetaDataReducer` 中的 `updateToolInfo` 函数
   - 将 `appId` 和 `tenantId` 存储到 `toolInfo` 中

4. **jadeFlowEntry.jsx**
   - 添加 `onViewToolDetail` 方法，允许外部订阅查看详情事件

## 事件监听示例

外部应用可以监听查看工具详情事件：

```javascript
jadeFlowAgent.onViewToolDetail((event) => {
  const { pluginId, pluginName, uniqueName, appId, tenantId } = event;
  
  console.log('查看工具详情:', {
    pluginId,
    pluginName,
    uniqueName,
    appId,
    tenantId
  });
  
  // 可以在这里实现自定义的详情展示逻辑
});
```

## 注意事项

1. **眼睛图标显示条件**: 只有当工具同时包含 `appId` 和 `tenantId` 信息时，才会显示眼睛图标
2. **老数据兼容**: 对于不包含 `appId` 和 `tenantId` 的老数据，不显示眼睛图标，只显示工具名称和删除图标
3. 跳转在新标签页中打开，不影响当前编辑状态
4. 即使没有配置 endpoint，系统也会使用当前域名作为默认值
5. 查看详情事件只有在点击眼睛图标时才会被触发

## 兼容性

- 不会影响现有功能
- 对于不包含 `appId` 和 `tenantId` 的旧数据，不显示眼睛图标，工具卡片正常显示工具名称和删除功能
- 所有更改向后兼容，老数据可以正常使用

