/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.enums.AppCategory;

import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 工具schema构建类.
 *
 * @author 张越
 * @since 2025-01-16
 */
public class ToolSchemaBuilder {
    private final PublishContext context;
    private final AppCategory appCategory;

    ToolSchemaBuilder(PublishContext context) {
        this.context = context;
        this.appCategory = AppCategory.findByType(this.context.getPublishData().getType())
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID));
    }

    /**
     * 创建构造器.
     *
     * @param context 发布上下文.
     * @return {@link ToolSchemaBuilder} 构建器对象.
     */
    public static ToolSchemaBuilder create(PublishContext context) {
        return new ToolSchemaBuilder(context);
    }

    /**
     * 构建.
     *
     * @return {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >} schema.
     */
    public Map<String, Object> build() {
        return MapBuilder.<String, Object>get()
                .put("name", this.context.getPublishData().getName())
                .put("description", this.context.getPublishData().getDescription())
                .put("parameters", this.buildParameters())
                .put("order", Arrays.asList("tenantId", "aippId", "version", "inputParams"))
                .put("return", this.buildReturn())
                .put("manualIntervention", Objects.equals(this.appCategory, AppCategory.WATER_FLOW))
                .build();
    }

    private Map<String, Object> buildParameters() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("type", "object");
        parameterMap.put("properties", this.buildProperties());
        parameterMap.put("required", Arrays.asList("tenantId", "aippId", "version", "inputParams"));
        return parameterMap;
    }

    private Map<String, Object> buildProperties() {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("tenantId",
                MapBuilder.get()
                        .put("type", "string")
                        .put("description", "the tenant id of the waterFlow tool")
                        .put("default", this.context.getOperationContext().getTenantId())
                        .build());
        propertiesMap.put("aippId",
                MapBuilder.get()
                        .put("type", "string")
                        .put("description", "the aipp id of the waterFlow tool")
                        .put("default", this.context.getPublishData().getAippId())
                        .build());
        propertiesMap.put("version",
                MapBuilder.get()
                        .put("type", "string")
                        .put("description", "the aipp version of the waterFlow tool")
                        .put("default", this.context.getPublishData().getVersion())
                        .build());
        propertiesMap.put("inputParams", this.buildInputParamsSchema());
        return propertiesMap;
    }

    private Map<String, Object> buildInputParamsSchema() {
        Map<String, Object> propertiesMapOfInputParam = new HashMap<>();
        List<String> required = new ArrayList<>();
        List<String> order = new ArrayList<>();
        Optional.ofNullable(this.context.getFlowInfo()).ifPresent(f -> {
            List<Map<String, Object>> inputParams = f.getInputParamsByName("input");
            inputParams.forEach(ip -> {
                String name = ip.getOrDefault("name", StringUtils.EMPTY).toString();
                String type = ObjectUtils.<String>cast(ip.getOrDefault("type", StringUtils.EMPTY)).toLowerCase();
                String description = ip.getOrDefault("description", StringUtils.EMPTY).toString();
                propertiesMapOfInputParam.put(name,
                        MapBuilder.get().put("type", type).put("description", description).build());
                if (ObjectUtils.cast(ip.getOrDefault("isRequired", false))) {
                    required.add(name);
                }
                order.add(name);
            });
        });
        return MapBuilder.<String, Object>get()
                .put("type", "object")
                .put("properties", propertiesMapOfInputParam)
                .put("required", required)
                .put("order", order)
                .build();
    }

    private Map<String, Object> buildReturn() {
        // 返参的具体属性信息暂不填充，需要考虑多end节点的情况
        return MapBuilder.<String, Object>get().put("type", "object").put("properties", new HashMap<>()).build();
    }
}
