/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.waterflow.invoker;

import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fel.core.tool.ToolCall;

import java.util.Map;

/**
 * 工具执行器的包装类
 *
 * @author songyongtan
 * @since 2024/12/25
 */
public abstract class ToolInvokerDecorator implements ToolInvoker {
    private final ToolInvoker decorated;

    /**
     * 构造函数
     *
     * @param decorated 被包装的对象
     */
    protected ToolInvokerDecorator(ToolInvoker decorated) {
        this.decorated = decorated;
    }

    @Override
    public String invoke(ToolCall toolCall, Map<String, Object> toolContext) {
        return this.decorated.invoke(toolCall, toolContext);
    }

    @Override
    public boolean match(ToolData toolData) {
        return this.decorated.match(toolData);
    }

    /**
     * 查询被装饰的对象
     *
     * @return 被装饰的对象
     */
    protected ToolInvoker getDecorated() {
        return this.decorated;
    }
}
