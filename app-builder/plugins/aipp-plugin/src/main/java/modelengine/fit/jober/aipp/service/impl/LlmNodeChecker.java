/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.enums.NodeType.LLM_NODE;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.store.service.PluginToolService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 大模型节点的checker
 *
 * @author 孙怡菲
 * @since 2024-11-25
 */
@Component
public class LlmNodeChecker extends AbstractNodeChecker {
    private static final String TOOL_NAME = "plugin";

    private final AippModelCenter fetchModelService;

    private final PluginToolService pluginToolService;

    public LlmNodeChecker(AippModelCenter fetchModelService, PluginToolService pluginToolService) {
        this.fetchModelService = fetchModelService;
        this.pluginToolService = pluginToolService;
    }

    @Override
    public List<CheckResult> validate(AppCheckDto appCheckDto, OperationContext context) {
        List<CheckResult> results = this.initialResults(appCheckDto, LLM_NODE.type());
        Map<String, CheckResult> resultMap = results.stream()
            .collect(Collectors.toMap(CheckResult::getNodeId, result -> result));

        List<ModelAccessInfo> modelInfos =
                fetchModelService.fetchModelList(AippConst.CHAT_MODEL_TYPE, null, context).getModels();
        List<String> uniqueNames = this.getAllUniqueNames(appCheckDto, TOOL_NAME);
        Map<String, Boolean> toolResults = this.getToolResult(pluginToolService, uniqueNames);

        appCheckDto.getNodeInfos().forEach(nodeInfo -> {
            String nodeId = nodeInfo.getNodeId();
            Optional<Map<String, Object>> modelConfig = nodeInfo.getConfigs()
                    .stream()
                    .filter(config -> StringUtils.equals(ObjectUtils.cast(config.get(CONFIG_NAME_KEY)), "accessInfo"))
                    .findFirst();
            modelConfig.ifPresent(modelConfigMap -> this.checkModel(modelConfigMap, nodeId, modelInfos, resultMap));
            this.checkTool(nodeInfo, TOOL_NAME, resultMap, toolResults);
        });
        results.forEach(this::checkValidation);
        return results;
    }

    private void checkModel(Map<String, Object> modelConfig, String nodeId, List<ModelAccessInfo> modelInfos,
            Map<String, CheckResult> resultMap) {
        if (isModelExists(modelConfig, modelInfos)) {
            return;
        }
        resultMap.get(nodeId).getConfigChecks().add(modelConfig);
    }

    private static boolean isModelExists(Map<String, Object> modelConfig, List<ModelAccessInfo> modelInfos) {
        return modelInfos.stream()
                .filter(info -> StringUtils.equals(info.getTag(), ObjectUtils.cast(modelConfig.get("tag"))))
                .anyMatch(info -> StringUtils.equals(info.getServiceName(),
                        ObjectUtils.cast(modelConfig.get("serviceName"))));
    }
}
