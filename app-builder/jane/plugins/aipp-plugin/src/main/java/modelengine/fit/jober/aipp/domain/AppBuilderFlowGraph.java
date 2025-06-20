/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import modelengine.fit.jober.aipp.util.JsonUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.knowledge.dto.KnowledgeDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 应用构建器流程图类
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
public class AppBuilderFlowGraph extends BaseDomain {
    private static final Logger LOGGER = Logger.get(AppBuilderFlowGraph.class);
    private static final String APP_BUILDER_DEFAULT_MODEL_NAME = "#app_builder_default_model_name#";
    private static final String APP_BUILDER_DEFAULT_SERVICE_NAME = "#app_builder_default_service_name#";
    private static final String APP_BUILDER_DEFAULT_TAG = "#app_builder_default_tag#";
    private static final String APP_BUILDER_DEFAULT_KNOWLEDGE_SET = "#app_builder_default_knowledge_set#";
    private static final int MODEL_LIST_SERVICE_NAME = 0;
    private static final int MODEL_LIST_TAG = 1;

    private String id;
    private String name;
    private String appearance;

    /**
     * 设置模型信息.
     *
     * @param modelInfo 模型信息.
     */
    public void setModelInfo(String[] modelInfo) {
        this.setAppearance(this.getAppearance()
                .replace(APP_BUILDER_DEFAULT_MODEL_NAME, modelInfo[MODEL_LIST_SERVICE_NAME])
                .replace(APP_BUILDER_DEFAULT_SERVICE_NAME, modelInfo[MODEL_LIST_SERVICE_NAME])
                .replace(APP_BUILDER_DEFAULT_TAG, modelInfo[MODEL_LIST_TAG]));
    }

    /**
     * 设置知识库信息.
     *
     * @param knowledgeInfo 知识库信息.
     */
    public void setKnowledgeInfo(KnowledgeDto knowledgeInfo) {
        this.setAppearance(this.getAppearance().replace(APP_BUILDER_DEFAULT_KNOWLEDGE_SET, knowledgeInfo.getGroupId()));
    }

    /**
     * 当创建app时，对应的执行逻辑.
     *
     * @param context 操作人上下文.
     */
    public void clone(OperationContext context) {
        LocalDateTime now = LocalDateTime.now();
        this.setId(Entities.generateId());
        this.setCreateBy(context.getOperator());
        this.setCreateAt(now);
        this.setUpdateBy(context.getOperator());
        this.setUpdateAt(now);
        this.resetGraphId();
    }

    /**
     * 重置 graph id.
     *
     */
    public void resetGraphId() {
        try {
            Map<String, Object> jsonAppearance = JSONObject.parseObject(this.getAppearance(),
                    new TypeReference<Map<String, Object>>() {});
            jsonAppearance.computeIfPresent("id", (k, v) -> this.getId());

            // 这里在创建应用时需要保证graph中的title+version唯一，否则在发布flow时会报错
            jsonAppearance.put("title", this.getId());

            // 动态修改graph中的model为可选model的第一个
            this.setAppearance(JSONObject.toJSONString(jsonAppearance));
        } catch (JSONException e) {
            LOGGER.error("Import config failed, cause: {}", e);
            throw new AippException(AippErrCode.IMPORT_CONFIG_FIELD_ERROR, "flowGraph.appearance");
        }
    }

    /**
     * 通过properties修改appearance.
     *
     * @param formProperties 新的表单属性列表.
     */
    public void updateByProperties(List<AppBuilderConfigFormPropertyDto> formProperties) {
        // 将dto的properties转成 {nodeId : {name:value, name:value},  ... }形式
        Map<String, Map<String, String>> nodeIdToPropertyNameValueMap = formProperties.stream()
                .filter(fp -> StringUtils.isNotBlank(fp.getNodeId()))
                .collect(Collectors.groupingBy(AppBuilderConfigFormPropertyDto::getNodeId))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
                        .stream()
                        .collect(Collectors.toMap(AppBuilderConfigFormPropertyDto::getName,
                                appBuilderConfigFormPropertyDto -> JsonUtils.toJsonString(
                                        appBuilderConfigFormPropertyDto.getDefaultValue())))));
        JSONObject oldAppearanceObject = JSONObject.parseObject(this.appearance);
        JSONObject page = ObjectUtils.cast(oldAppearanceObject.getJSONArray("pages").get(0));
        JSONArray shapes = page.getJSONArray("shapes");

        for (int j = 0; j < shapes.size(); j++) {
            JSONObject node = shapes.getJSONObject(j);
            String nodeId = node.getString("id");
            String type = node.getString("type");
            if (!StringUtils.equals(type, "startNodeStart") && !type.endsWith("NodeState")) {
                continue;
            }

            Map<String, String> nameValue = nodeIdToPropertyNameValueMap.get(nodeId);

            String flowMetaString = node.get("flowMeta").toString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode flowMeta = null;
            try {
                flowMeta = mapper.readTree(flowMetaString);
                JsonNode params = flowMeta.findPath("inputParams");
                for (int i = 0; i < params.size(); i++) {
                    JsonNode child = params.get(i);
                    processParam(child, nameValue);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Object tt = JSON.parse(flowMeta.toString());
            node.put("flowMeta", tt);
        }

        this.appearance = JSONObject.toJSONString(oldAppearanceObject);
    }

    private void processParam(JsonNode node, Map<String, String> params) {
        List<String> singleLayerParams = new ArrayList<>(Arrays.asList("model", "temperature", "systemPrompt"));
        List<String> doubleLayerParams = new ArrayList<>(Arrays.asList("tools", "workflows"));
        if (params == null) {
            return;
        }
        for (Map.Entry<String, String> param : params.entrySet()) {
            handleParam(node, param, singleLayerParams, doubleLayerParams);
        }
    }

    private void handleParam(JsonNode node, Map.Entry<String, String> param, List<String> singleLayerParams,
            List<String> doubleLayerParams) {
        if (StringUtils.equals(node.get("name").asText(), param.getKey())) {
            if (singleLayerParams.contains(param.getKey())) {
                this.handleParamTemperature(node, param);
                return;
            }

            if (doubleLayerParams.contains(param.getKey())) {
                ArrayNode valueArrayNode = convertList(param.getValue());
                ObjectUtils.<ObjectNode>cast(node).set("value", valueArrayNode);
                return;
            }

            if (StringUtils.equals("knowledge", param.getKey())) {
                this.handleParamKnowledge(node, param);
                return;
            }

            if (StringUtils.equals("memory", param.getKey())) {
                JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
                ArrayNode valueArrayNode = nodeFactory.arrayNode();
                Map<String, Object> res = JsonUtils.parseObject(param.getValue(), Map.class);
                if (Objects.equals(res.get("type"), "UserSelect")) {
                    this.parseUserSelect(res, valueArrayNode);
                } else {
                    this.parseOtherMemoryType(res, valueArrayNode);
                }
                ObjectUtils.<ObjectNode>cast(node).set("value", valueArrayNode);
            }
        }
    }

    private void handleParamTemperature(JsonNode node, Map.Entry<String, String> param) {
        if (StringUtils.equals(param.getKey(), "temperature")) {
            ObjectUtils.<ObjectNode>cast(node).put("value", JsonUtils.parseObject(param.getValue(), Float.class));
        } else {
            ObjectUtils.<ObjectNode>cast(node).put("value", JsonUtils.parseObject(param.getValue(), String.class));
        }
    }

    private ArrayNode convertList(String value) {
        String[] res = JsonUtils.parseObject(value, String[].class);
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

        List<Map<String, String>> re = Arrays.stream(res).map(this::convert).toList();

        ArrayNode valueArrayNode = nodeFactory.arrayNode();
        for (Map<String, String> rr : re) {
            ObjectNode mapNode = nodeFactory.objectNode();
            for (Map.Entry<String, String> entry : rr.entrySet()) {
                mapNode.put(entry.getKey(), entry.getValue());
            }
            valueArrayNode.add(mapNode);
        }
        return valueArrayNode;
    }

    private Map<String, String> convert(String value) {
        Map<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        return map;
    }

    private void handleParamKnowledge(JsonNode node, Map.Entry<String, String> param) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ArrayNode valueArrayNode = nodeFactory.arrayNode();
        List<Map<String, Object>> res =
                ObjectUtils.<List<Map<String, Object>>>cast(JsonUtils.parseObject(param.getValue(), List.class));
        res.forEach(r -> {
            ArrayNode valueArrayNode1 = nodeFactory.arrayNode();
            for (Map.Entry<String, Object> rr : r.entrySet()) {
                if (StringUtils.equals(rr.getKey(), "id")) {
                    valueArrayNode1.add(convertId(rr.getKey(), ObjectUtils.<Integer>cast(rr.getValue()).longValue()));
                } else {
                    valueArrayNode1.add(convertObject(rr.getKey(), String.valueOf(rr.getValue())));
                }
            }
            Map<String, Object> a = new HashMap<>();
            a.put("id", UUID.randomUUID().toString());
            a.put("type", "Object");
            a.put("from", "Expand");
            a.put("value", valueArrayNode1);
            ObjectNode mapNode = nodeFactory.objectNode();
            for (Map.Entry<String, Object> entry : a.entrySet()) {
                if (StringUtils.equals(entry.getKey(), "value")) {
                    mapNode.put(entry.getKey(), ObjectUtils.<JsonNode>cast(entry.getValue()));
                } else {
                    mapNode.put(entry.getKey(), ObjectUtils.<String>cast(entry.getValue()));
                }
            }
            valueArrayNode.add(mapNode);
        });
        ObjectUtils.<ObjectNode>cast(node).set("value", valueArrayNode);
    }

    private ObjectNode convertId(String key, Long value) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getKey(), "value")) {
                mapNode.put(entry.getKey(), ObjectUtils.<Long>cast(entry.getValue()));
            } else {
                mapNode.put(entry.getKey(), ObjectUtils.<String>cast(entry.getValue()));
            }
        }
        return mapNode;
    }

    private ObjectNode convertObject(String key, String value) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mapNode.put(entry.getKey(), entry.getValue());
        }
        return mapNode;
    }

    private void parseUserSelect(Map<String, Object> res, ArrayNode valueArrayNode) {
        for (Map.Entry<String, Object> resEntry : res.entrySet()) {
            if (Objects.equals(resEntry.getKey(), AippConst.MEMORY_SWITCH_KEY)) {
                this.checkEntryType(resEntry, Boolean.class);
                valueArrayNode.add(this.convertMemorySwitch(resEntry.getKey(), ObjectUtils.cast(resEntry.getValue())));
            } else if (Objects.equals(resEntry.getKey(), "value")) {
                valueArrayNode.add(this.convertValueForUserSelect(resEntry.getKey(),
                        String.valueOf(resEntry.getValue())));
            } else {
                valueArrayNode.add(this.convertObject(resEntry.getKey(), String.valueOf(resEntry.getValue())));
            }
        }
    }

    private void checkEntryType(Map.Entry<String, Object> entry, Class<?> clazz) {
        if (!clazz.isInstance(entry.getValue())) {
            LOGGER.error("Failed to update app configuration. [entryType={}]", entry.getValue().getClass().getName());
            throw new AippException(AippErrCode.UPDATE_APP_CONFIGURATION_FAILED);
        }
    }

    private ObjectNode convertMemorySwitch(String key, Boolean isOpenSwitch) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "Input");
        map.put("type", "Boolean");
        map.put("value", isOpenSwitch);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getKey(), "value")) {
                this.checkEntryType(entry, Boolean.class);
                mapNode.put(entry.getKey(), ObjectUtils.<Boolean>cast(entry.getValue()));
            } else {
                this.checkEntryType(entry, String.class);
                mapNode.put(entry.getKey(), ObjectUtils.<String>cast(entry.getValue()));
            }
        }
        return mapNode;
    }

    private ObjectNode convertValueForUserSelect(String key, String value) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "input");
        map.put("type", StringUtils.EMPTY);
        map.put("value", value);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mapNode.put(entry.getKey(), entry.getValue());
        }
        return mapNode;
    }

    private void parseOtherMemoryType(Map<String, Object> res, ArrayNode valueArrayNode) {
        for (Map.Entry<String, Object> resEntry : res.entrySet()) {
            if (Objects.equals(resEntry.getKey(), AippConst.MEMORY_SWITCH_KEY)) {
                this.checkEntryType(resEntry, Boolean.class);
                valueArrayNode.add(this.convertMemorySwitch(resEntry.getKey(), ObjectUtils.cast(resEntry.getValue())));
            } else {
                valueArrayNode.add(this.convertObject(resEntry.getKey(), String.valueOf(resEntry.getValue())));
            }
        }
    }
}
