/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.waterflow.ErrorCodes.NOT_SUPPORT;
import static modelengine.fit.waterflow.common.Constant.BUSINESS_DATA_INTERNAL_KEY;
import static modelengine.fit.waterflow.common.Constant.INTERNAL_OUTPUT_SCOPE_KEY;
import static modelengine.fitframework.util.ObjectUtils.cast;


import lombok.Getter;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fitframework.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 映射处理的抽象类
 *
 * @author 宋永坦
 * @since 2024/4/18
 */
public abstract class AbstractMappingProcessor implements MappingProcessor {
    private static final Logger LOG = Logger.get(AbstractMappingProcessor.class);

    private static Object getValueByPath(Map<String, Object> businessData, List<String> paths, String nodeMetaId,
        boolean isFallbackOnNodeDataMiss) {
        if (paths.isEmpty()) {
            return null;
        }
        // 优先从节点scope下查找，找不到后再从businessData平级查找
        Map<String, Object> internalMap = cast(
                Optional.ofNullable(businessData.get(BUSINESS_DATA_INTERNAL_KEY)).orElse(new HashMap<>()));
        Map<String, Object> outputScopeMap = cast(
                Optional.ofNullable(internalMap.get(INTERNAL_OUTPUT_SCOPE_KEY)).orElse(new HashMap<>()));
        Map<String, Object> nodeOutputMap = cast(
                Optional.ofNullable(outputScopeMap.get(nodeMetaId)).orElse(new HashMap<>()));

        ValueResult result = findValueByPath(nodeOutputMap, paths);
        if (result.isFound()) {
            return result.getValue();
        }
        // 兼容逻辑，如果没有从作用域空间找到，则尝试从businessData查找一次
        if (!isFallbackOnNodeDataMiss) {
            return null;
        }
        return findValueByPath(businessData, paths).getValue();
    }

    private static ValueResult findValueByPath(Map<String, Object> values, List<String> paths) {
        Optional<String> v;
        Object currentNode = values;
        for (String path : paths) {
            Map<String, Object> objectNode = cast(currentNode);
            if (objectNode != null && objectNode.containsKey(path)) {
                currentNode = objectNode.get(path);
            } else {
                return new ValueResult();
            }
        }
        return new ValueResult(currentNode);
    }

    @Override
    public Object generate(MappingNode mappingConfig, Map<String, Object> businessData) {
        if (MappingFromType.REFERENCE.equals(mappingConfig.getFrom())) {
            return this.generateReference(mappingConfig, businessData);
        } else if (MappingFromType.INPUT.equals(mappingConfig.getFrom())) {
            if (Objects.isNull(mappingConfig.getValue())) {
                return mappingConfig.getValue();
            }
            return this.generateInput(mappingConfig, businessData);
        } else if (MappingFromType.EXPAND.equals(mappingConfig.getFrom())) {
            return this.generateExpand(mappingConfig, businessData);
        } else {
            LOG.error("The from is invalid. from={}, name={}.", mappingConfig.getFrom(), mappingConfig.getName());
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, mappingConfig.getName());
        }
    }

    /**
     * 值类型的节点生成处理
     *
     * @param mappingConfig 映射配置， 调用者需要保证其中的value不为null
     * @param businessData 源数据
     * @return 生成的数据
     */
    protected abstract Object generateInput(MappingNode mappingConfig, Map<String, Object> businessData);

    /**
     * expand类型的参数处理
     *
     * @param mappingConfig 映射配置
     * @param businessData 源数据
     * @return 生成的数据
     */
    protected Object generateExpand(MappingNode mappingConfig, Map<String, Object> businessData) {
        throw new WaterflowParamException(NOT_SUPPORT, mappingConfig.getType().getCode());
    }

    private Object generateReference(MappingNode mappingConfig, Map<String, Object> businessData) {
        return getValueByPath(businessData, cast(mappingConfig.getValue()), mappingConfig.getReferenceNode(),
                mappingConfig.isFallbackOnNodeDataMiss());
    }

    /**
     * value结果
     *
     * @author 夏斐
     * @since 1.0
     */
    @Getter
    private static class ValueResult {
        private boolean isFound;

        private Object value;

        public ValueResult(boolean isFound, Object value) {
            this.isFound = isFound;
            this.value = value;
        }

        public ValueResult(Object value) {
            this(true, value);
        }

        public ValueResult() {
            this(false, null);
        }
    }
}
