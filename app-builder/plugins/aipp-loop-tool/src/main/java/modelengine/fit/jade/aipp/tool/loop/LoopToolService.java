/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.tool.loop;

import modelengine.fit.jade.aipp.tool.loop.entities.Config;
import modelengine.fit.jade.aipp.tool.loop.entities.ToolInfo;
import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.annotation.Property;
import modelengine.jade.carver.tool.annotation.Group;
import modelengine.jade.carver.tool.annotation.ToolMethod;

import java.util.List;
import java.util.Map;

/**
 * 循环工具节点服务
 *
 * @author 夏斐
 * @since 2025/3/10
 */
@Group(name = "LoopTool")
public interface LoopToolService {
    /**
     * 循环调用接口.
     *
     * @param args 请求参数
     * @param config 循环配置
     * @param toolInfo 工具信息
     * @return 节点结果
     */
    @ToolMethod(name = "loopTool", description = "用于循环执行工具")
    @Genericable("modelengine.jober.aipp.tool.loop")
    List<Object> loopTool(@Property(description = "循环调用工具时的入参", required = true) Map<String, Object> args,
            @Property(description = "循环调用的配置，指定循环的字段", required = true) Config config,
            @Property(description = "循环调用的工具信息", required = true) ToolInfo toolInfo,
            @Property(description = "循环调用工具时的上下文信息") Map<String, Object> context);
}
