/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;
import modelengine.fit.jober.aipp.service.Checker;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.store.service.PluginToolService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 为checker提供工具方法
 *
 * @author 孙怡菲
 * @since 2024-11-25
 */
@Component
public abstract class AbstractNodeChecker implements Checker {
    /**
     * 配置名称的key
     */
    public static final String CONFIG_NAME_KEY = "configName";

    /**
     * 工具唯一标识的key
     */
    public static final String UNIQUE_NAME_KEY = "uniqueName";

    /**
     * 初始化校验结果
     *
     * @param appCheckDto 待校验的配置
     * @param nodeType 节点类型
     * @return 校验结果
     */
    public List<CheckResult> initialResults(AppCheckDto appCheckDto, String nodeType) {
        return appCheckDto.getNodeInfos()
                .stream()
                .map(info -> new CheckResult(info.getNodeId(), info.getNodeName(), nodeType, true, new ArrayList<>()))
                .collect(Collectors.toList());
    }

    /**
     * 检查工具、工具流配置是否可用
     *
     * @param pluginToolService PluginService
     * @param configs 待校验的uniqueName列表
     * @return 校验结果
     */
    public Map<String, Boolean> getToolResult(PluginToolService pluginToolService, List<String> configs) {
        List<Boolean> results = pluginToolService.hasPluginTools(configs);
        return IntStream.range(0, Math.min(configs.size(), results.size()))
                .boxed()
                .collect(Collectors.toMap(configs::get, results::get));
    }

    /**
     * 获取待校验的uniqueName列表
     *
     * @param appCheckDto 待校验的节点配置
     * @param configName 待校验的配置名
     * @return 待校验的uniqueName列表
     */
    public List<String> getAllUniqueNames(AppCheckDto appCheckDto, String configName) {
        Set<String> uniqueNames = new HashSet<>();
        appCheckDto.getNodeInfos().forEach(nodeInfo -> getUniqueNames(configName, nodeInfo, uniqueNames));
        return new ArrayList<>(uniqueNames);
    }

    private static void getUniqueNames(String configName, AppCheckDto.NodeInfo nodeInfo, Set<String> uniqueNames) {
        nodeInfo.getConfigs()
                .stream()
                .filter(config -> config.get(CONFIG_NAME_KEY).equals(configName))
                .forEach(c -> uniqueNames.add(ObjectUtils.cast(c.get(UNIQUE_NAME_KEY))));
    }

    /**
     * 根据工具查询结果生成配置检查结果
     *
     * @param nodeInfo 节点配置信息
     * @param configName 配置名
     * @param resultMap 校验结果
     * @param toolResults 工具校验结果
     */
    public void checkTool(AppCheckDto.NodeInfo nodeInfo, String configName, Map<String, CheckResult> resultMap,
            Map<String, Boolean> toolResults) {
        List<Map<String, Object>> toolConfigs = nodeInfo.getConfigs()
                .stream()
                .filter(config -> StringUtils.equals(ObjectUtils.cast(config.get(CONFIG_NAME_KEY)), configName))
                .toList();
        toolConfigs.forEach(toolConfig -> {
            if (toolResults.get(toolConfig.get(UNIQUE_NAME_KEY))) {
                return;
            }
            resultMap.get(nodeInfo.getNodeId()).getConfigChecks().add(toolConfig);
        });
    }

    /**
     * 校验节点合法性
     *
     * @param checkResult 检验结果结构体
     */
    public void checkValidation(CheckResult checkResult) {
        if (checkResult.getConfigChecks().isEmpty()) {
            return;
        }
        checkResult.setValid(false);
    }

    /**
     * 无需查询的配置项（例如表单、知识库）
     *
     * @param appCheckDto 待校验的配置
     * @param nodeType 节点类型
     * @return 校验结果
     */
    public List<CheckResult> invalidNodeConfig(AppCheckDto appCheckDto, String nodeType) {
        Map<String, AppCheckDto.NodeInfo> nodeInfoMap = appCheckDto.getNodeInfos()
                .stream()
                .collect(Collectors.toMap(AppCheckDto.NodeInfo::getNodeId, nodeInfo -> nodeInfo));

        List<CheckResult> results = initialResults(appCheckDto, nodeType);
        results.forEach(result -> generateResult(result, nodeInfoMap));
        return results;
    }

    private static void generateResult(CheckResult result, Map<String, AppCheckDto.NodeInfo> nodeInfoMap) {
        // 默认设置为无效
        result.setValid(false);

        // 获取匹配的 NodeInfo
        AppCheckDto.NodeInfo nodeInfo = nodeInfoMap.get(result.getNodeId());
        if (nodeInfo == null) {
            return;
        }
        List<Map<String, Object>> configChecks = nodeInfo.getConfigs();
        result.setConfigChecks(configChecks);

        // 如果没有配置项，则标记为有效
        if (configChecks.isEmpty()) {
            result.setValid(true);
        }
    }

    @Override
    public List<CheckResult> validate(AppCheckDto appCheckDto, OperationContext context) {
        return Collections.emptyList();
    }
}
