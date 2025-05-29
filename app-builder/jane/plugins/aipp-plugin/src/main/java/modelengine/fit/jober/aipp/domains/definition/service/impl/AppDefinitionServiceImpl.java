/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.definition.service.impl;

import lombok.AllArgsConstructor;
import modelengine.fit.jade.waterflow.AippFlowDefinitionService;
import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.definition.service.AppDefinitionService;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 定义服务实现类
 *
 * @author 张越
 * @since 2025-02-08
 */
@Component
@AllArgsConstructor
public class AppDefinitionServiceImpl implements AppDefinitionService {
    private final AippFlowDefinitionService flowDefinitionService;

    @Override
    public FlowDefinitionResult getSameFlowDefinition(AippDto aippDto) {
        String metaId = aippDto.getMetaId();
        String version = aippDto.getVersion();
        List<FlowDefinitionResult> flowDefinitions = this.flowDefinitionService.getFlowDefinitionByMetaIdAndPartVersion(
                metaId, version + "-", null);
        String parsedGraphData = this.flowDefinitionService.getParsedGraphData(
                JsonUtils.toJsonString(aippDto.getFlowViewData()), version);
        Map<String, Object> aippFlowDefinitionMapping = this.buildFlowDefinition(parsedGraphData);
        return flowDefinitions.stream().limit(1).filter(definition -> {
            Map<String, Object> map = this.buildFlowDefinition(definition.getGraph());
            return this.compareMaps(map, aippFlowDefinitionMapping);
        }).findAny().orElse(null);
    }

    private Map<String, Object> buildFlowDefinition(String flowDefinition) {
        Map<String, Object> parsedFlowDefinitionMapping = JsonUtils.parseObject(flowDefinition);

        // 这边 name 和 version 不需要比较
        parsedFlowDefinitionMapping.remove(AippConst.FLOW_CONFIG_NAME);
        parsedFlowDefinitionMapping.remove(AippConst.FLOW_CONFIG_VERSION_KEY);
        return parsedFlowDefinitionMapping;
    }

    private boolean compareMaps(Map<String, Object> map1, Map<String, Object> map2) {
        if (map1 == map2) {
            return true;
        }
        if (map1 == null || map2 == null) {
            return false;
        }
        if (map1.size() != map2.size()) {
            return false;
        }
        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            String key = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = map2.get(key);
            if (!map2.containsKey(key) || !this.isSameObject(value1, value2)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSameObject(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        if (obj1 instanceof Map && obj2 instanceof Map) {
            return compareMaps(ObjectUtils.cast(obj1), ObjectUtils.cast(obj2));
        }
        return Objects.equals(obj1, obj2);
    }
}
