/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class AippUtilTest {
    @Test
    @DisplayName("key重复出现场景")
    void testParsePromptWithMultiRepeatedKeys() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");
        businessData.put("key3", "value3");

        String testBasePrompt = "hello $(key1)$(key1) world $(key2)$(key1)!";
        String expectedPrompt = "hello value1value1 world value2value1!";
        Assertions.assertEquals(expectedPrompt, DataUtils.parsePrompt(businessData, testBasePrompt));
    }

    @Test
    @DisplayName("括号内包含空格")
    void testParsePromptWithBlankCharacters() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");
        businessData.put("key 3", "value3");

        String testBasePrompt = "hello $( key1)$(key1 ) world $( key2 )$(  key1  )$(key 3)!";
        String expectedPrompt = "hello value1value1 world value2value1value3!";
        Assertions.assertEquals(expectedPrompt, DataUtils.parsePrompt(businessData, testBasePrompt));
    }

    @Test
    @DisplayName("${}花括号格式")
    void testParsePromptWithCurlyBrace() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");
        businessData.put("key 3", "value3");

        String testBasePrompt = "hello ${ key1}${key1 } world ${ key2 }${  key1  }${key 3}!";
        String expectedPrompt = "hello value1value1 world value2value1value3!";
        Assertions.assertEquals(expectedPrompt, DataUtils.parsePrompt(businessData, testBasePrompt));
    }

    @Test
    @DisplayName("括号嵌套场景")
    void testParsePromptWithNestedBrace() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");

        String testBasePrompt = "hello ${key2 $(key1) }$(${key2 }) world";
        String expectedPrompt = "hello ${key2 value1 }$(value2) world";
        Assertions.assertEquals(expectedPrompt, DataUtils.parsePrompt(businessData, testBasePrompt));
    }

    @Test
    @DisplayName("括号混用场景-待屏蔽")
    void testParsePromptWithFixBrace() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");

        String testBasePrompt = "hello ${key1)${key2) world";
        String expectedPrompt = "hello value1value2 world";
        Assertions.assertEquals(expectedPrompt, DataUtils.parsePrompt(businessData, testBasePrompt));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "hello world"})
    @DisplayName("不包含key结构的场景")
    void testParsePromptWithoutKeys(String noKeyPrompt) {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");
        Assertions.assertEquals(noKeyPrompt, DataUtils.parsePrompt(businessData, noKeyPrompt));
    }

    @Test
    @DisplayName("getAgentParams成功")
    void testGetAgentParams() {
        List<Map<String, Object>> flowData = new ArrayList<>();
        Map<String, Object> agentParams = new HashMap<>();
        agentParams.put("hello", "1");
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_AGENT_PARAM_KEY, agentParams);
        Map<String, Object> zeroFlowData = new HashMap<>();
        zeroFlowData.put(AippConst.BS_DATA_KEY, businessData);
        flowData.add(zeroFlowData);
        Map<String, Object> resultAgentParams = Assertions.assertDoesNotThrow(() -> DataUtils.getAgentParams(flowData));
        Assertions.assertNotNull(resultAgentParams);
        Assertions.assertTrue(resultAgentParams.containsKey("hello"));
        Assertions.assertEquals("1", resultAgentParams.get("hello"));
    }

    @Test
    @DisplayName("getBusiness失败：列表为空")
    void testGetBusinessWithEmptyInputList() {
        List<Map<String, Object>> flowData = new ArrayList<>();
        JobberException exception =
                Assertions.assertThrows(JobberException.class, () -> DataUtils.getBusiness(flowData));
        Assertions.assertEquals(10000000, exception.getCode());
    }

    @Test
    @DisplayName("getBusiness失败：不包含businessData关键词")
    void testGetBusinessWithoutBusinessData() {
        List<Map<String, Object>> flowData = new ArrayList<>();
        flowData.add(new HashMap<>());
        JobberException exception =
                Assertions.assertThrows(JobberException.class, () -> DataUtils.getBusiness(flowData));
        Assertions.assertEquals(10000000, exception.getCode());
    }

    @Test
    @DisplayName("getBusiness成功")
    void testGetBusinessSuccess() {
        List<Map<String, Object>> flowData = new ArrayList<>();
        flowData.add(MapBuilder.get(() -> new HashMap<String, Object>())
                .put(AippConst.BS_DATA_KEY,
                        MapBuilder.get(() -> new HashMap<String, Object>()).put("hello", "1").build())
                .build());
        Map<String, Object> businessData = Assertions.assertDoesNotThrow(() -> DataUtils.getBusiness(flowData));
        Assertions.assertTrue(businessData.containsKey("hello"));
        Assertions.assertEquals("1", businessData.get("hello"));
    }

    @Test
    @DisplayName("getAgentId：全部场景")
    void testGetAgentId() {
        String excepted = StringUtils.EMPTY;
        Map<String, Object> contextData = new HashMap<>();
        Assertions.assertEquals(excepted, Assertions.assertDoesNotThrow(() -> DataUtils.getAgentId(contextData)));
        Map<String, Object> extraJober = new HashMap<>();
        contextData.put(AippConst.BS_EXTRA_CONFIG_KEY, extraJober);
        Assertions.assertEquals(excepted, Assertions.assertDoesNotThrow(() -> DataUtils.getAgentId(contextData)));
        extraJober.put(AippConst.BS_AGENT_ID_KEY, "id");
        Assertions.assertEquals("id", Assertions.assertDoesNotThrow(() -> DataUtils.getAgentId(contextData)));
    }

    @Test
    @DisplayName("getContextData失败：列表为空")
    void testGetContextDataWithEmptyInputList() {
        List<Map<String, Object>> flowData = new ArrayList<>();
        JobberException exception =
                Assertions.assertThrows(JobberException.class, () -> DataUtils.getContextData(flowData));
        Assertions.assertEquals(10000000, exception.getCode());
    }

    @Test
    @DisplayName("getContextData失败：不包含contextData关键词")
    void testGetContextDataWithoutContextData() {
        List<Map<String, Object>> flowData = new ArrayList<>();
        flowData.add(new HashMap<>());
        JobberException exception =
                Assertions.assertThrows(JobberException.class, () -> DataUtils.getContextData(flowData));
        Assertions.assertEquals(10000000, exception.getCode());
    }

    @Test
    @DisplayName("getContextData成功")
    void testGetContextDataSuccess() {
        List<Map<String, Object>> flowData = new ArrayList<>();
        flowData.add(MapBuilder.get(() -> new HashMap<String, Object>())
                .put(AippConst.CONTEXT_DATA_KEY,
                        MapBuilder.get(() -> new HashMap<String, Object>()).put("hello", "1").build())
                .build());
        Map<String, Object> businessData = Assertions.assertDoesNotThrow(() -> DataUtils.getContextData(flowData));
        Assertions.assertTrue(businessData.containsKey("hello"));
        Assertions.assertEquals("1", businessData.get("hello"));
    }

    @Test
    @DisplayName("getOpContext")
    void testGetOpContext() {
        Map<String, Object> build = MapBuilder.get(() -> new HashMap<String, Object>())
                .put(AippConst.BS_HTTP_CONTEXT_KEY, "{\"operator\":\"hello\"}")
                .build();
        OperationContext context = Assertions.assertDoesNotThrow(() -> DataUtils.getOpContext(build));
        Assertions.assertEquals("hello", context.getOperator());
    }

    @Test
    @DisplayName("getFlowTraceId：traceIds为空及traceIds不为空两个场景")
    void testGetFlowTraceId() {
        List<Map<String, Object>> flowData = new ArrayList<>();
        List<String> traceIds = new ArrayList<>();
        Map<String, Object> contextData = MapBuilder.get(() -> new HashMap<String, Object>())
                .put(AippConst.INST_FLOW_TRACE_IDS, traceIds)
                .build();
        flowData.add(MapBuilder.get(() -> new HashMap<String, Object>())
                .put(AippConst.CONTEXT_DATA_KEY, contextData)
                .build());
        JobberException exception =
                Assertions.assertThrows(JobberException.class, () -> DataUtils.getFlowTraceId(flowData));
        Assertions.assertEquals(10000006, exception.getCode());
        traceIds.add("11");
        String traceId = Assertions.assertDoesNotThrow(() -> DataUtils.getFlowTraceId(flowData));
        Assertions.assertEquals("11", traceId);
    }
}
