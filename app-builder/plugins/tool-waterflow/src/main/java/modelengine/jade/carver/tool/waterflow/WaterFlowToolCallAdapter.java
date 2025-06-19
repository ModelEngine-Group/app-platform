/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.waterflow;

import modelengine.fel.core.tool.ToolCall;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fit.jade.tool.SyncToolCall;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.UuidUtils;
import modelengine.jade.carver.tool.waterflow.invoker.ToolInvoker;
import modelengine.jade.store.service.ToolService;

import java.util.List;
import java.util.Map;

/**
 * 表示 {@link SyncToolCall} 的实现。
 *
 * @author 刘信宏
 * @since 2024-4-17
 */
@Component
public class WaterFlowToolCallAdapter implements SyncToolCall {
    private static final String DEFAULT_USER_ID = "jade";

    private final ToolService toolService;
    private final List<ToolInvoker> toolInvokers;

    /**
     * {@link WaterFlowToolCallAdapter} 的构造方法。
     *
     * @param toolService 表示查询服务的 {@link ToolService}。
     * @param toolInvokers 表示执行能力列表的 {@link ToolInvoker}。
     */
    public WaterFlowToolCallAdapter(ToolService toolService, List<ToolInvoker> toolInvokers) {
        this.toolService = toolService;
        this.toolInvokers = toolInvokers;
    }

    @Override
    @Fitable(id = "app-factory")
    public String call(String uniqueName, String toolArgs, Map<String, Object> toolContext) {
        ToolCall toolCall = ToolCall.custom()
                .id(UuidUtils.randomUuidString())
                .name(uniqueName)
                .index(0)
                .arguments(toolArgs)
                .build();
        ToolInvoker toolInvoker = this.getToolInvoker(toolCall.name());
        return toolInvoker.invoke(toolCall,
                (toolContext == null || toolContext.isEmpty()) ? MapBuilder.<String, Object>get()
                        .put(AippConst.CONTEXT_USER_ID, DEFAULT_USER_ID)
                        .build() : toolContext);
    }

    private ToolInvoker getToolInvoker(String uniqueName) {
        ToolData toolData = Validation.notNull(this.toolService.getTool(uniqueName),
                StringUtils.format("Cannot find tool. [uniqueName={0}]", uniqueName));

        return this.getToolInvoker(toolData);
    }

    private ToolInvoker getToolInvoker(ToolData toolData) {
        return Validation.notNull(this.toolInvokers.stream()
                        .filter(toolInvoker -> toolInvoker.match(toolData))
                        .findAny()
                        .orElse(null),
                StringUtils.format("Cannot find tool invoker. [uniqueName={0}]", toolData.getUniqueName()));
    }
}
