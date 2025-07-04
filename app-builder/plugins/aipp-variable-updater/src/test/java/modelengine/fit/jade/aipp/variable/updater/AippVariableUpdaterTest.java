/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.variable.updater;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link AippVariableUpdater}的测试方法。
 *
 * @author 鲁为
 * @since 2025-09-28
 */
@DisplayName("变量更新节点测试。")
public class AippVariableUpdaterTest {
    private static final String UPDATE_VARIABLES = "updateVariables";
    private static final String INTERNAL = "_internal";
    private static final String OUTPUT_SCOPE = "outputScope";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    private final AippVariableUpdater aippVariableUpdater = new AippVariableUpdater();

    @Test
    @DisplayName("修改 businessData 中指定路径的值。")
    void shouldModifySpecificPathValue() {
        Map<String, Object> flowDataInner =
                this.getFlowData(this.getContextData(List.of("trace1"), "contextId"), this.buildBusinessData());
        List<Map<String, Object>> flowData = List.of(flowDataInner);
        this.aippVariableUpdater.handleTask(flowData);
        assertEquals(ObjectUtils.<String>cast(ObjectUtils.<Map<String, Object>>cast(ObjectUtils.<Map<String, Object>>cast(
                        ObjectUtils.<Map<String, Object>>cast(ObjectUtils.<Map<String, Object>>cast(ObjectUtils.<Map<String, Object>>cast(
                                flowData.get(0).get("businessData")).get(INTERNAL)).get(OUTPUT_SCOPE)).get(
                                        "jade8xvdq4"))
                .get("output")).get("a")), "after");
    }

    @Test
    @DisplayName("当 path 不存在时，抛出异常。")
    void shouldThrowExceptionWhenPathDoesNotExist() {
        Map<String, Object> flowDataInner =
                this.getFlowData(this.getContextData(List.of("trace1"), "contextId"), this.buildInvalidBusinessData());
        List<Map<String, Object>> flowData = List.of(flowDataInner);
        assertThrows(AippParamException.class, () -> this.aippVariableUpdater.handleTask(flowData));
    }

    private Map<String, Object> getFlowData(Map<String, Object> contextData, Map<String, Object> businessData) {
        return MapBuilder.get(() -> new HashMap<String, Object>())
                .put("contextData", contextData)
                .put("businessData", businessData)
                .build();
    }

    private Map<String, Object> getContextData(List<String> traceIds, String contextId) {
        return MapBuilder.get(() -> new HashMap<String, Object>())
                .put("flowTraceIds", traceIds)
                .put("contextId", contextId)
                .build();
    }

    private Map<String, Object> buildBusinessData() {
        Map<Object, Object> output = MapBuilder.get().put("a", "before").build();
        Map<Object, Object> node = MapBuilder.get().put("output", output).build();
        Map<Object, Object> outputScope = MapBuilder.get().put("jade8xvdq4", node).build();
        return MapBuilder.<String, Object>get()
                .put(UPDATE_VARIABLES,
                        List.of(MapBuilder.get()
                                .put(KEY, List.of("jade8xvdq4", "output", "a"))
                                .put(VALUE, "after")
                                .build()))
                .put(INTERNAL, MapBuilder.get().put(OUTPUT_SCOPE, outputScope).build())
                .build();
    }

    private Map<String, Object> buildInvalidBusinessData() {
        List<Object> output = List.of("a", "before");
        Map<Object, Object> node = MapBuilder.get().put("output", output).build();
        Map<Object, Object> outputScope = MapBuilder.get().put("jade8xvdq4", node).build();
        return MapBuilder.<String, Object>get()
                .put(UPDATE_VARIABLES,
                        List.of(MapBuilder.get()
                                .put(KEY, List.of("jade8xvdq4", "output", "a"))
                                .put(VALUE, "after")
                                .build()))
                .put(INTERNAL, MapBuilder.get().put(OUTPUT_SCOPE, outputScope).build())
                .build();
    }
}
