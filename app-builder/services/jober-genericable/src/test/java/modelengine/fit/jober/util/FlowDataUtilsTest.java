/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.util;

import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songyongtan
 * @since 2024/10/15
 */
class FlowDataUtilsTest {
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

    @Test
    @DisplayName("getFlowInstanceId成功")
    void shouldGetSuccessfullyWhenGetFlowInstanceIdGivenValidData() {
        Map<String, Object> flowData = getFlowData(getContextData(Arrays.asList("trace1"), "contextId"), null);
        String traceId = FlowDataUtils.getFlowInstanceId(flowData);
        Assertions.assertEquals("trace1", traceId);
    }

    @Test
    @DisplayName("getFlowInstanceId失败场景：traceIds为空")
    void shouldThrowExceptionWhenGetFlowInstanceIdGivenEmptyTraceIds() {
        Map<String, Object> flowData = getFlowData(getContextData(new ArrayList<>(), "contextId"), null);
        JobberException exception =
                Assertions.assertThrows(JobberException.class, () -> FlowDataUtils.getFlowInstanceId(flowData));
        Assertions.assertEquals(10000006, exception.getCode());
    }

    @Test
    @DisplayName("getContextData成功")
    void shouldGetSuccessfullyWhenGetContextDataGivenValidData() {
        Map<String, Object> flowData = getFlowData(getContextData(new ArrayList<>(), "contextId1"), null);
        Map<String, Object> contextData = FlowDataUtils.getContextData(flowData);
        Assertions.assertTrue(contextData.containsKey("contextId"));
        Assertions.assertEquals("contextId1", contextData.get("contextId"));
    }

    @Test
    @DisplayName("getContextData失败场景：contextData不存在")
    void shouldThrowExceptionWhenGetContextDataGivenNoContextDataKey() {
        JobberException exception =
                Assertions.assertThrows(JobberException.class, () -> FlowDataUtils.getContextData(new HashMap<>()));
        Assertions.assertEquals(10000006, exception.getCode());
    }

    @Test
    @DisplayName("getBusinessData成功")
    void shouldGetSuccessfullyWhenGetBusinessDataGivenValidData() {
        Map<String, Object> flowData =
                getFlowData(null, MapBuilder.get(() -> new HashMap<String, Object>()).put("hello", "1").build());
        Map<String, Object> businessData = FlowDataUtils.getBusinessData(flowData);
        Assertions.assertTrue(businessData.containsKey("hello"));
        Assertions.assertEquals("1", businessData.get("hello"));
    }

    @Test
    @DisplayName("getBusinessData失败场景：contextData不存在")
    void shouldThrowExceptionWhenGetBusinessDataGivenNoBusinessDataKey() {
        JobberException exception =
                Assertions.assertThrows(JobberException.class, () -> FlowDataUtils.getBusinessData(new HashMap<>()));
        Assertions.assertEquals(10000006, exception.getCode());
    }

    @Test
    @DisplayName("getFlowDataId成功")
    void shouldGetSuccessfullyWhenGetFlowDataIdGivenValidData() {
        Map<String, Object> flowData = getFlowData(getContextData(null, "id1"), null);
        String flowDataId = FlowDataUtils.getFlowDataId(flowData);
        Assertions.assertEquals("id1", flowDataId);
    }

    @Test
    @DisplayName("getFlowDataId失败场景：contextId不存在")
    void shouldThrowExceptionWhenGetGetFlowDataIdGivenEmptyContextId() {
        Map<String, Object> flowData = getFlowData(getContextData(null, ""), null);
        JobberException exception =
                Assertions.assertThrows(JobberException.class, () -> FlowDataUtils.getFlowDataId(flowData));
        Assertions.assertEquals(10000006, exception.getCode());
    }
}