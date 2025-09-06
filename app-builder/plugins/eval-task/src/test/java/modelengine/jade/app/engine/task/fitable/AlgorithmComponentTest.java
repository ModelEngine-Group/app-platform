/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.fitable;

import static modelengine.fit.jober.aipp.constants.AippConst.BS_DATA_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.CONTEXT_DATA_KEY;
import static modelengine.jade.app.engine.task.code.EvalTaskRetCode.EVAL_ALGORITHM_TOOL_ERROR;
import static modelengine.jade.app.engine.task.code.EvalTaskRetCode.EVAL_TASK_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.task.exception.EvalTaskException;
import modelengine.jade.app.engine.task.service.EvalAlgorithmService;
import modelengine.jade.app.engine.task.service.EvalInstanceService;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fel.tool.service.ToolExecuteService;

import com.fasterxml.jackson.databind.ObjectMapper;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.jade.store.service.ToolService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评估算法节点测试类
 *
 * @author 兰宇晨
 * @since 2024-08-24
 */
@FitTestWithJunit(includeClasses = AlgorithmComponentTest.class)
public class AlgorithmComponentTest {
    private static final String NODE_ID = "skyfangNodeId";
    private static final String NODE_NAME = "skyfangNodeName";
    private static final String ARGS = "{\"key1\":\"Sky\"}";

    @Fit
    private ObjectSerializer serializer;
    private ObjectMapper mapper;
    private ToolExecuteService toolExecuteService;
    private ToolService toolService;
    private EvalAlgorithmService evalAlgorithmService;
    private EvalInstanceService evalInstanceService;

    @BeforeEach
    void before() {
        this.toolExecuteService = mock(ToolExecuteService.class);
        this.toolService = mock(ToolService.class);
        this.evalAlgorithmService = mock(EvalAlgorithmService.class);
        this.evalInstanceService = mock(EvalInstanceService.class);
        this.mapper = new ObjectMapper();
    }

    private List<Map<String, Object>> buildFlowData(Map<String, Object> businessData, Map<String, Object> contextData) {
        Map<String, Object> flowData = new HashMap<>();
        flowData.put(BS_DATA_KEY, businessData);
        flowData.put(CONTEXT_DATA_KEY, contextData);
        return Collections.singletonList(flowData);
    }

    private Map<String, Object> genBusinessData() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("algorithmArgs", this.serializer.deserialize(this.ARGS, Map.class));
        businessData.put("isDebug", false);
        businessData.put("passScore", 10.1);
        businessData.put("uniqueName", "sky algo");
        return businessData;
    }

    private Map<String, Object> genContextData() {
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("nodeName", this.NODE_NAME);
        contextData.put("nodeMetaId", this.NODE_ID);
        contextData.put("flowTraceIds", Collections.singletonList("skyFangTrace"));
        return contextData;
    }

    @Test
    @DisplayName("算法节点数据流转正常")
    void shouldOkWhenUseAlgorithmComponent() {
        when(this.toolExecuteService.execute(anyString(), anyString())).thenReturn("100.2");
        when(this.evalAlgorithmService.exist(anyString())).thenReturn(false);
        when(this.evalInstanceService.getTaskIdByTraceId("skyFangTrace")).thenReturn(1L);

        ToolData fakeData = new ToolData();
        fakeData.setSchema(new HashMap<>());
        when(this.toolService.getTool(anyString())).thenReturn(fakeData);

        AlgorithmComponent algorithmComponent = new AlgorithmComponent(this.serializer,
                this.toolExecuteService,
                this.toolService,
                this.evalAlgorithmService,
                this.evalInstanceService);

        List<Map<String, Object>> flowData = buildFlowData(genBusinessData(), genContextData());
        List<Map<String, Object>> resultFlowData = algorithmComponent.handleTask(flowData);

        Map<String, Object> actualBusinessOutput = new HashMap<>();
        Map<String, Object> actualOutput = new HashMap<>();

        Map<String, Object> args = this.serializer.deserialize(this.ARGS, Map.class);
        actualOutput.put("input", args);
        actualOutput.put("nodeId", this.NODE_ID);
        actualOutput.put("nodeName", this.NODE_NAME);

        actualOutput.put("passScore", 10.1);
        actualOutput.put("isPass", true);
        actualOutput.put("score", 100.2);
        actualBusinessOutput.put("output", actualOutput);

        assertThat(resultFlowData).hasSize(1);
        Map<String, Object> resultBusinessData = ObjectUtils.cast(flowData.get(0).get(BS_DATA_KEY));
        assertThat(resultBusinessData).hasFieldOrPropertyWithValue("output", actualOutput);
    }

    @Test
    @DisplayName("算法插件返回值错误，抛出异常。")
    void shouldNotOkWhenUseInvalidAlgorithmTool() {
        when(this.toolExecuteService.execute(anyString(), anyString())).thenReturn("sky fang");
        when(this.evalAlgorithmService.exist(anyString())).thenReturn(false);
        when(this.evalInstanceService.getTaskIdByTraceId("skyFangTrace")).thenReturn(1L);

        ToolData fakeData = new ToolData();
        fakeData.setSchema(new HashMap<>());
        when(this.toolService.getTool(anyString())).thenReturn(fakeData);

        AlgorithmComponent algorithmComponent = new AlgorithmComponent(this.serializer,
                this.toolExecuteService,
                this.toolService,
                this.evalAlgorithmService,
                this.evalInstanceService);

        List<Map<String, Object>> flowData = buildFlowData(genBusinessData(), genContextData());
        assertThatThrownBy(() -> algorithmComponent.handleTask(flowData)).isInstanceOf(EvalTaskException.class)
                .hasFieldOrPropertyWithValue("code", EVAL_ALGORITHM_TOOL_ERROR.getCode());
        ;
    }

    @Test
    @DisplayName("缺少参数导致算法节点流转失败")
    void shouldNotOkWhenUseAlgorithmComponentMissingParam() {
        AlgorithmComponent algorithmComponent = new AlgorithmComponent(this.serializer,
                this.toolExecuteService,
                this.toolService,
                this.evalAlgorithmService,
                this.evalInstanceService);
        List<Map<String, Object>> flowData = Collections.singletonList(new HashMap<>());
        assertThatThrownBy(() -> algorithmComponent.handleTask(flowData)).isInstanceOf(EvalTaskException.class)
                .hasFieldOrPropertyWithValue("code", EVAL_TASK_CONTEXT.getCode());
    }
}
