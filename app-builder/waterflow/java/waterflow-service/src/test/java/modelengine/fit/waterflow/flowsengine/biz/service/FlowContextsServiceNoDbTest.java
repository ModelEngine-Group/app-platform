/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.waterflow.entity.OperationContext;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.MethodNameLoggerExtension;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowDefinitionQueryService;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowQueryService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Node;

import modelengine.fit.waterflow.service.FlowRuntimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 沒有db的FlowContextsService测试
 *
 * @author 夏斐
 * @since 2024/4/24
 */
@ExtendWith(MethodNameLoggerExtension.class)
public class FlowContextsServiceNoDbTest {
    private TraceOwnerService traceOwnerService;
    private FlowRuntimeService flowRuntimeService;

    private FlowDefinitionRepo flowDefinitionRepo;

    private FlowDefinitionQueryService definitionQueryService;

    private FlowQueryService flowQueryService;

    private FlowContextRepo flowContextRepo;

    @BeforeEach
    void setUp() {
        traceOwnerService = Mockito.mock(TraceOwnerService.class);
        flowDefinitionRepo = Mockito.mock(FlowDefinitionRepo.class);
        QueryFlowContextPersistRepo queryFlowContextPersistRepo = Mockito.mock(QueryFlowContextPersistRepo.class);
        flowDefinitionRepo = Mockito.mock(FlowDefinitionRepo.class);
        flowContextRepo = Mockito.mock(FlowContextRepo.class);
        FlowContextMessenger flowContextMessenger = Mockito.mock(FlowContextMessenger.class);
        FlowTraceRepo flowTraceRepo = Mockito.mock(FlowTraceRepo.class);
        FlowLocks flowLocks = Mockito.mock(FlowLocks.class);
        definitionQueryService = Mockito.mock(FlowDefinitionQueryService.class);
        flowQueryService = Mockito.mock(FlowQueryService.class);
        flowRuntimeService = new FlowRuntimeServiceImpl(flowDefinitionRepo, flowContextRepo, flowContextMessenger,
                queryFlowContextPersistRepo, flowTraceRepo, null, flowLocks, traceOwnerService, null,
                false, null, definitionQueryService, flowQueryService);
    }

    @Test
    @DisplayName("测试恢复一个异步jober")
    public void testResumeAsyncJober() {
        FlowNode mockFlowNode = mock(FlowNode.class);
        when(mockFlowNode.getMetaId()).thenReturn("jadeMockId");
        FlowJober mockFlowJober = mock(FlowJober.class);
        FlowDataConverter flowDataConverter = mock(FlowDataConverter.class);
        Map<String, Object> convertNullOutput = new HashMap<>();
        Map<String, Object> convertNullSecondLayerOutput = new HashMap<>();
        convertNullSecondLayerOutput.put("secondLayerK1", null);
        convertNullOutput.put("k1", null);
        convertNullOutput.put("k2", convertNullSecondLayerOutput);
        when(flowDataConverter.convertOutput(eq(null))).thenReturn(convertNullOutput);
        when(mockFlowJober.getConverter()).thenReturn(flowDataConverter);
        when(mockFlowNode.getJober()).thenReturn(mockFlowJober);
        From from = mock(From.class);
        FlowDefinition flowDefinition = mock(FlowDefinition.class);
        when(flowDefinition.convertToFlow(any(), any(), any())).thenReturn(from);
        Node node = mock(Node.class);
        when(node.getNodeType()).thenReturn(FlowNodeType.START);
        String position = "state1";
        when(from.findNodeFromFlow(any(), eq(position))).thenReturn(node);
        when(flowDefinitionRepo.findByStreamId(anyString())).thenReturn(flowDefinition);
        when(flowDefinition.getFlowNode(anyString())).thenReturn(mockFlowNode);
        when(definitionQueryService.findByStreamId(anyString())).thenReturn(flowDefinition);
        when(flowQueryService.getPublisher(null)).thenReturn(from);

        FlowData flowData = FlowData.builder()
                .operator("a")
                .startTime(LocalDateTime.now())
                .businessData(new HashMap<>())
                .contextData(new HashMap<>())
                .build();
        FlowContext<FlowData> flowContext = new FlowContext<>("streamId", "rootId", flowData,
                Collections.singleton("traceId"), "nodeId");
        flowContext.setPosition(position);
        flowContext.setStatus(FlowNodeStatus.PROCESSING);
        Map<String, Object> newBusinessData = new HashMap<>();
        Map<String, Object> secondLayerMap = new HashMap<>();
        secondLayerMap.put("secondLayerK1", "secondLayerV1");
        newBusinessData.put("k1", "v1");
        newBusinessData.put("k2", secondLayerMap);
        when(flowContextRepo.getByIds(anyList())).thenReturn(Collections.singletonList(flowContext));
        flowRuntimeService.resumeAsyncJob(
                Collections.singletonList(flowContext.getId()), Collections.singletonList(newBusinessData),
                getOperationContext());
        testResumeAsyncJoberAssertions(position, node, flowContext);
    }

    private static void testResumeAsyncJoberAssertions(String position, Node node, FlowContext<FlowData> flowContext) {
        ArgumentCaptor<List> captorPre = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> captorAfter = ArgumentCaptor.forClass(List.class);
        verify(node).afterProcess(captorPre.capture(), captorAfter.capture());
        List<FlowContext<FlowData>> pre = captorPre.getValue();
        List<FlowContext<FlowData>> after = captorAfter.getValue();
        Assertions.assertEquals(1, pre.size());
        Assertions.assertEquals(FlowNodeStatus.PROCESSING, pre.get(0).getStatus());
        Assertions.assertEquals(1, after.size());
        Assertions.assertEquals(flowContext.getStreamId(), after.get(0).getStreamId());
        Assertions.assertEquals(flowContext.getRootId(), after.get(0).getRootId());
        Assertions.assertEquals(position, after.get(0).getPosition());
        Assertions.assertEquals(flowContext.getId(), after.get(0).getPrevious());
        Assertions.assertEquals(3, after.get(0).getData().getBusinessData().size());
    }

    @Test
    @DisplayName("测试异步job失败能正常设置的场景")
    public void shouldSetJoberFailedWhenFailAsyncJobGivenError() {
        From from = mock(From.class);
        FlowDefinition flowDefinition = mock(FlowDefinition.class);
        when(flowDefinition.convertToFlow(any(), any(), any())).thenReturn(from);
        String position = "state1";
        Node node = mock(Node.class);
        when(node.getNodeType()).thenReturn(FlowNodeType.START);
        when(from.findNodeFromFlow(any(), eq(position))).thenReturn(node);
        when(flowDefinitionRepo.findByStreamId(anyString())).thenReturn(flowDefinition);
        when(flowQueryService.getPublisher(anyString())).thenReturn(from);

        FlowData flowData = FlowData.builder()
                .operator("a")
                .startTime(LocalDateTime.now())
                .businessData(new HashMap<>())
                .contextData(new HashMap<>())
                .build();
        FlowContext<FlowData> flowContext = new FlowContext<>("streamId", "rootId", flowData, Collections.singleton("traceId"), "nodeId");
        flowContext.setPosition(position);
        flowContext.setStatus(FlowNodeStatus.PROCESSING);
        Map<String, Object> newBusinessData = new HashMap<>();
        newBusinessData.put("k1", "v1");
        WaterflowParamException expectException = new WaterflowParamException(INPUT_PARAM_IS_INVALID, "xxx");
        List<FlowContext<FlowData>> expectContexts = Collections.singletonList(flowContext);
        when(flowContextRepo.getByIds(anyList())).thenReturn(expectContexts);
        flowRuntimeService.failAsyncJob(expectContexts.stream().map(FlowContext::getId).collect(Collectors.toList()),
                expectException, getOperationContext());

        ArgumentCaptor<List> captorPre = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Exception> captorException = ArgumentCaptor.forClass(Exception.class);
        verify(node).setFailed(captorPre.capture(), captorException.capture());
        List<FlowContext<FlowData>> pre = captorPre.getValue();
        Exception exception = captorException.getValue();
        Assertions.assertEquals(expectContexts, pre);
        Assertions.assertEquals(FlowNodeStatus.PROCESSING, pre.get(0).getStatus());
        Assertions.assertEquals(expectException, exception);
    }

    @Test
    @DisplayName("failAsyncJob传入空pre时抛出异常")
    public void shouldThrowWhenFailAsyncJobWithEmptyPre() {
        WaterflowParamException expectException = new WaterflowParamException(INPUT_PARAM_IS_INVALID, "xxx");

        WaterflowParamException thrown = Assertions.assertThrows(WaterflowParamException.class,
                () -> flowRuntimeService.failAsyncJob(Collections.emptyList(), expectException,
                        getOperationContext()));
        Assertions.assertEquals(INPUT_PARAM_IS_INVALID.getErrorCode(), thrown.getCode());
    }

    private OperationContext getOperationContext() {
        return OperationContext.custom().operator("xx").operatorIp("0.0.0.1").tenantId("framework").build();
    }
}
