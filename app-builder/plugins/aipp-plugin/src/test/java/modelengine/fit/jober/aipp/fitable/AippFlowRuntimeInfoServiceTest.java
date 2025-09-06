/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import modelengine.fit.jober.aipp.service.AippFlowRuntimeInfoService;
import modelengine.fit.jober.aipp.service.impl.AippFlowRuntimeInfoServiceImpl;
import modelengine.fit.jober.entity.consts.NodeTypes;
import modelengine.fit.runtime.entity.Parameter;
import modelengine.fit.runtime.entity.RuntimeData;
import modelengine.fit.waterflow.domain.enums.FlowNodeStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * runtimeInfo服务测试用例.
 *
 * @author 张越
 * @since 2024-07-29
 */
@ExtendWith(MockitoExtension.class)
public class AippFlowRuntimeInfoServiceTest {
    @Mock
    private AppTaskInstanceService appTaskInstanceService;

    @Mock
    private AppTaskService appTaskService;

    @Mock
    private AppBuilderRuntimeInfoRepository repository;

    private AippFlowRuntimeInfoService service;

    /**
     * 初始化.
     */
    @BeforeEach
    void setUp() {
        this.service = new AippFlowRuntimeInfoServiceImpl(this.repository, this.appTaskInstanceService,
                this.appTaskService);
    }

    /**
     * 测试用例.
     */
    @Test
    void shouldOptionalEmptyWhenNoRuntimeInfo() {
        doReturn(Optional.of(AppTask.asEntity().setTaskId("version1").setAppId("app1").build())).when(
                this.appTaskService).getLatest(anyString(), anyString(), any(OperationContext.class));

        Optional<AppTaskInstance> result = Optional.of(AppTaskInstance.asEntity().setFlowTraceId("trace1").build());
        doReturn(result).when(this.appTaskInstanceService)
                .getInstance(anyString(), anyString(), any(OperationContext.class));

        doReturn(Collections.emptyList()).when(this.repository).selectByTraceId(anyString());

        // when
        Optional<RuntimeData> optionalRuntimeData = this.service.getRuntimeData("app1", "", "instance1",
                new OperationContext());

        // then
        assertFalse(optionalRuntimeData.isPresent());
    }

    /**
     * 测试用例.
     */
    @Test
    void shouldThrowIllegalStateExceptionWhenNoStartNodeInfo() {
        doReturn(Optional.of(AppTask.asEntity().setTaskId("version1").setAppId("app1").build())).when(
                this.appTaskService).getLatest(anyString(), anyString(), any(OperationContext.class));

        Optional<AppTaskInstance> instanceOp = Optional.of(
                AppTaskInstance.asEntity().setFlowTraceId("trace1").build());
        doReturn(instanceOp).when(this.appTaskInstanceService)
                .getInstance(anyString(), anyString(), any(OperationContext.class));

        List<AppBuilderRuntimeInfo> infos = new ArrayList<>();
        infos.add(AppBuilderRuntimeInfo.builder().nodeType(NodeTypes.STATE.getType()).build());
        doReturn(infos).when(this.repository).selectByTraceId(anyString());

        // when
        // then
        assertThrows(IllegalStateException.class, () -> {
            this.service.getRuntimeData("app1", "", "instance1", new OperationContext());
        });
    }

    /**
     * 测试用例.
     */
    @Test
    void shouldBeNormalWhenInfosIsRight() {
        // before
        mockData();

        // when
        Optional<RuntimeData> optionalRuntimeData = this.service.getRuntimeData("app1", "", "instance1",
                new OperationContext());

        // then
        assertTrue(optionalRuntimeData.isPresent());
        RuntimeData runtimeData = optionalRuntimeData.get();
        assertEquals(runtimeData.getTraceId(), "trace1");
        assertEquals(runtimeData.getFlowDefinitionId(), "flow1");
        assertEquals(runtimeData.getStartTime(), 1);
        assertEquals(runtimeData.getEndTime(), 31);
        assertTrue(runtimeData.isPublished());
        assertEquals(runtimeData.getAippInstanceId(), "instance1");
        assertEquals(runtimeData.getExecuteTime(), 30);
        assertEquals(runtimeData.getNodeInfos().get(0).getNodeId(), "node1");
        assertEquals(runtimeData.getNodeInfos().get(0).getNodeType(), NodeTypes.START.getType());
        assertEquals(runtimeData.getNodeInfos().get(0).getStartTime(), 1);
        assertEquals(runtimeData.getNodeInfos().get(0).getRunCost(), 9);
        assertEquals(runtimeData.getNodeInfos().get(0).getStatus(), FlowNodeStatus.ARCHIVED.name());
        assertEquals(runtimeData.getNodeInfos().get(0).getParameters().get(0).getInput(), "1");
        assertEquals(runtimeData.getNodeInfos().get(0).getParameters().get(0).getOutput(), "2");
        assertEquals(runtimeData.getNodeInfos().get(1).getNodeId(), "node2");
        assertEquals(runtimeData.getNodeInfos().get(1).getNodeType(), NodeTypes.END.getType());
        assertEquals(runtimeData.getNodeInfos().get(1).getStartTime(), 11);
        assertEquals(runtimeData.getNodeInfos().get(1).getRunCost(), 20);
        assertEquals(runtimeData.getNodeInfos().get(1).getStatus(), FlowNodeStatus.ERROR.name());
        assertEquals(runtimeData.getNodeInfos().get(1).getErrorMsg(), "11111");
        assertEquals(runtimeData.getNodeInfos().get(1).getParameters().get(0).getInput(), "3");
        assertEquals(runtimeData.getNodeInfos().get(1).getParameters().get(0).getOutput(), "4");
    }

    private void mockData() {
        doReturn(Optional.of(AppTask.asEntity().setTaskId("version1").setAppId("app1").build())).when(
                this.appTaskService).getLatest(anyString(), anyString(), any(OperationContext.class));

        Optional<AppTaskInstance> result = Optional.of(AppTaskInstance.asEntity().setFlowTraceId("trace1").build());
        doReturn(result).when(this.appTaskInstanceService)
                .getInstance(anyString(), anyString(), any(OperationContext.class));

        List<AppBuilderRuntimeInfo> infos = new ArrayList<>();
        Parameter startParameter = new Parameter();
        startParameter.setInput("1");
        startParameter.setOutput("2");
        infos.add(AppBuilderRuntimeInfo.builder()
                .startTime(1)
                .endTime(10)
                .flowDefinitionId("flow1")
                .traceId("trace1")
                .nodeId("node1")
                .instanceId("instance1")
                .nodeType(NodeTypes.START.getType())
                .status(FlowNodeStatus.ARCHIVED.name())
                .published(true)
                .parameters(Collections.singletonList(startParameter))
                .build());

        Parameter endParameter = new Parameter();
        endParameter.setInput("3");
        endParameter.setOutput("4");
        infos.add(AppBuilderRuntimeInfo.builder()
                .startTime(11)
                .endTime(31)
                .flowDefinitionId("flow1")
                .traceId("trace1")
                .nodeId("node2")
                .instanceId("instance1")
                .nodeType(NodeTypes.END.getType())
                .errorMsg("11111")
                .status(FlowNodeStatus.ERROR.name())
                .published(true)
                .parameters(Collections.singletonList(endParameter))
                .build());
        doReturn(infos).when(this.repository).selectByTraceId(anyString());
    }
}
