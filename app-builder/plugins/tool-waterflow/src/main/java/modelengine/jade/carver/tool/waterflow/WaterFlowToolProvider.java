/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.waterflow;

import modelengine.fel.core.chat.support.FlatChatMessage;
import modelengine.fel.core.chat.support.ToolMessage;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fel.core.tool.ToolInfo;
import modelengine.fel.core.tool.ToolProvider;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示 {@link ToolProvider} 的默认实现。
 *
 * @author 刘信宏
 * @since 2024-4-17
 */
@Component
public class WaterFlowToolProvider implements ToolProvider, SyncToolCall {
    private final ToolService toolService;
    private static final Logger logger = Logger.get(WaterFlowToolProvider.class);
    private final List<ToolInvoker> toolInvokers;

    /**
     * 创建默认工具提供者。
     *
     * @param toolService 表示查询服务的 {@link ToolService}。
     * @param toolInvokers 表示执行能力列表的 {@link ToolInvoker}。
     */
    public WaterFlowToolProvider(ToolService toolService, List<ToolInvoker> toolInvokers) {
        this.toolService = toolService;
        this.toolInvokers = toolInvokers;
    }

    @Override
    @Fitable(id = "app-factory")
    public FlatChatMessage call(ToolCall toolCall, Map<String, Object> toolContext) {
        ToolInvoker toolInvoker = this.getToolInvoker(toolCall.name());
        logger.warn("toolName:{}, toolArgs:{}.", toolCall.name(), toolCall.arguments());
        return FlatChatMessage.from(new ToolMessage(toolCall.id(), toolInvoker.invoke(toolCall, toolContext)));
    }

    @Override
    @Fitable(id = "app-factory")
    public List<ToolInfo> getTool(List<String> uniqueNames) {
        return uniqueNames.stream()
                .map(this.toolService::getTool)
                .filter(Objects::nonNull)
                .map(toolData -> this.getToolInvoker(toolData).getToolInfo(toolData))
                .collect(Collectors.toList());
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

    @Override
    @Fitable(id = "app-factory")
    public String call(String uniqueName, String toolArgs) {
        ToolCall toolCall = ToolCall.custom()
                .id(UuidUtils.randomUuidString())
                .name(uniqueName)
                .index(0)
                .arguments(toolArgs)
                .build();
        ToolInvoker toolInvoker = this.getToolInvoker(toolCall.name());
        Map<String, Object> toolContext =
                MapBuilder.<String, Object>get().put(AippConst.CONTEXT_USER_ID, "jade").build();
        return toolInvoker.invoke(toolCall, toolContext);
    }
}
