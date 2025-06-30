/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import modelengine.fit.waterflow.entity.JoberErrorInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.service.FlowRuntimeService;
import modelengine.fit.waterflow.service.SingleFlowRuntimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_EXECUTE_ASYNC_JOBER_FAILED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * FlowInstanceServiceImpl测试
 *
 * @author songyongtan
 * @since 2024/10/15
 */
@ExtendWith(MockitoExtension.class)
class FlowInstanceServiceImplTest {
    @Mock
    private FlowContextRepo<FlowData> flowContextRepo;

    @Mock
    private FlowRuntimeService flowRuntimeService;

    @Mock
    private SingleFlowRuntimeService singleFlowRuntimeService;

    @BeforeEach
    void setUp() {
        singleFlowRuntimeService = new SingleFlowRuntimeServiceImpl(flowRuntimeService, null, null, flowContextRepo) {
        };
    }

    @Test
    void shouldCallContextServiceWhenResumeAsyncJobGivenValidContextId() {
        String flowDataId = "contextId1";
        FlowContext<FlowData> context =
                new FlowContext<>("streamId", "rootId", new FlowData(), Collections.singleton("traceId"), "nodeId");
        context.setStatus(FlowNodeStatus.PROCESSING);
        when(flowContextRepo.getById(flowDataId)).thenReturn(context);
        doNothing().when(flowRuntimeService).resumeAsyncJob(anyList(), anyList(), any());
        HashMap<String, Object> businessData = new HashMap<>();
        singleFlowRuntimeService.resumeAsyncJob(flowDataId, businessData, null);
        verify(flowContextRepo, times(1)).getById(flowDataId);
        verify(flowRuntimeService, times(1)).resumeAsyncJob(anyList(),
                argThat(arg -> arg.contains(businessData)), any());
    }

    @Test
    void shouldDoNothingWhenResumeAsyncJobGivenWrongContextStatus() {
        String flowDataId = "contextId1";
        FlowContext<FlowData> context =
                new FlowContext<>("streamId", "rootId", new FlowData(), Collections.singleton("traceId"), "nodeId");
        context.setStatus(FlowNodeStatus.ARCHIVED);
        when(flowContextRepo.getById(flowDataId)).thenReturn(context);
        singleFlowRuntimeService.resumeAsyncJob(flowDataId, new HashMap<>(), null);
        verify(flowContextRepo, times(1)).getById(flowDataId);
        verify(flowRuntimeService, times(0)).resumeAsyncJob(any(), any(), any());
    }

    @Test
    void shouldCallContextServiceWhenFailAsyncJobGivenValidContextId() {
        String flowDataId = "contextId1";
        FlowContext<FlowData> context =
                new FlowContext<>("streamId", "rootId", new FlowData(), Collections.singleton("traceId"), "nodeId");
        context.setStatus(FlowNodeStatus.PROCESSING);
        when(flowContextRepo.getById(flowDataId)).thenReturn(context);
        doNothing().when(flowRuntimeService).failAsyncJob(anyList(), any(), any());
        singleFlowRuntimeService.failAsyncJob(flowDataId, new JoberErrorInfo("error"), null);
        verify(flowContextRepo, times(1)).getById(eq(flowDataId));
        verify(flowRuntimeService, times(1)).failAsyncJob(anyList(),
                argThat(arg -> arg.getCode() == FLOW_EXECUTE_ASYNC_JOBER_FAILED.getErrorCode()), any());
    }

    @Test
    void shouldDoNothingWhenFailAsyncJobGivenWrongContextStatus() {
        String flowDataId = "contextId1";
        FlowContext<FlowData> context =
                new FlowContext<>("streamId", "rootId", new FlowData(), Collections.singleton("traceId"), "nodeId");
        context.setStatus(FlowNodeStatus.ARCHIVED);
        when(flowContextRepo.getById(flowDataId)).thenReturn(context);
        singleFlowRuntimeService.resumeAsyncJob(flowDataId,
                new HashMap<>(), null);
        verify(flowContextRepo, times(1)).getById(flowDataId);
        verify(flowRuntimeService, times(0)).resumeAsyncJob(any(), any(), any());
    }
}