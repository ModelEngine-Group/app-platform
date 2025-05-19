/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.enums.NodeType.TOOL_INVOKE_NODE;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.store.service.PluginToolService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 自定义插件节点的checker
 *
 * @author 孙怡菲
 * @since 2024-11-25
 */
@Component
public class ToolInvokeNodeChecker extends AbstractNodeChecker {
    private static final String TOOL_NAME = "toolInvokeNodeState";

    private final PluginToolService pluginToolService;

    public ToolInvokeNodeChecker(PluginToolService pluginToolService) {
        this.pluginToolService = pluginToolService;
    }

    @Override
    public List<CheckResult> validate(AppCheckDto appCheckDto, OperationContext context) {
        List<CheckResult> results = initialResults(appCheckDto, TOOL_INVOKE_NODE.type());
        Map<String, CheckResult> resultMap = results.stream()
            .collect(Collectors.toMap(CheckResult::getNodeId, result -> result));
        Map<String, Boolean> toolResults = this.getToolResult(pluginToolService,
            this.getAllUniqueNames(appCheckDto, TOOL_NAME));
        appCheckDto.getNodeInfos().forEach(nodeInfo -> {
            this.checkTool(nodeInfo, TOOL_NAME, resultMap, toolResults);
        });
        results.forEach(this::checkValidation);
        return results;
    }
}
