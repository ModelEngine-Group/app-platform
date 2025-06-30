/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import modelengine.fit.waterflow.entity.OperationContext;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.entity.FlowStartDTO;
import modelengine.fit.waterflow.entity.FlowStartInfo;
import modelengine.fit.waterflow.entity.JoberErrorInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowContextPO;
import modelengine.fit.waterflow.flowsengine.utils.FlowUtil;
import modelengine.fit.waterflow.service.FlowRuntimeService;
import modelengine.fit.waterflow.service.SingleFlowRuntimeService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_EXECUTE_ASYNC_JOBER_FAILED;
import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.waterflow.common.Constant.SYSTEM_PARAMETER_NODE_KEY;

/**
 * SingleFlowRuntimeService实现类
 *
 * @author yangxiangyu
 * @since 2025/2/24
 */
@Component
public class SingleFlowRuntimeServiceImpl implements SingleFlowRuntimeService {
    private static final Logger log = Logger.get(SingleFlowRuntimeServiceImpl.class);

    private final FlowRuntimeService flowRuntimeService;
    private final FlowTraceRepo traceRepo;
    private final QueryFlowContextPersistRepo contextRepo;
    private final FlowContextRepo<FlowData> repo;

    public SingleFlowRuntimeServiceImpl(FlowRuntimeService flowRuntimeService, FlowTraceRepo traceRepo, QueryFlowContextPersistRepo contextRepo, FlowContextRepo<FlowData> repo) {
        this.flowRuntimeService = flowRuntimeService;
        this.traceRepo = traceRepo;
        this.contextRepo = contextRepo;
        this.repo = repo;
    }

    @Override
    public FlowStartDTO startFlow(String flowDefinitionId, FlowStartInfo startInfo, OperationContext context) {
        FlowUtil.cacheResultToNode(startInfo.getBusinessData(), SYSTEM_PARAMETER_NODE_KEY);
        return this.flowRuntimeService.startFlows(flowDefinitionId, startInfo, context);
    }

    @Override
    public void resumeFlow(String flowDefinitionId, String instanceId, Map<String, Object> request, OperationContext context) {
        FlowContextPO flowContextPo = this.findContext(flowDefinitionId, instanceId, FlowNodeStatus.PENDING);
        Map<String, Map<String, Object>> resumeContext = new HashMap<>();
        resumeContext.put(flowContextPo.getContextId(), request);
        this.flowRuntimeService.resumeFlows(flowDefinitionId, resumeContext);
    }

    private FlowContextPO findContext(String flowDefinitionId, String traceId, FlowNodeStatus pending) {
        Validation.notBlank(flowDefinitionId, () -> new WaterflowParamException(INPUT_PARAM_IS_EMPTY, "flowDefinitionId"));
        Validation.notBlank(traceId, () -> new WaterflowParamException(INPUT_PARAM_IS_EMPTY, "traceId"));
        FlowTrace flowTrace = this.traceRepo.find(traceId);
        if (flowTrace == null) {
            log.error("Flow trace is null.");
            throw new WaterflowException(INPUT_PARAM_IS_INVALID, traceId);
        }
        List<String> contextIds = new ArrayList<>(flowTrace.getContextPool());
        List<FlowContextPO> pendingContexts = this.contextRepo.findByContextIdList(contextIds)
                .stream()
                .filter(c -> pending.name().equals(c.getStatus()))
                .toList();
        if (pendingContexts.size() != 1) {
            log.error("Flow resume failed, pending context size:{}", pendingContexts.size());
            throw new WaterflowException(INPUT_PARAM_IS_INVALID, traceId);
        }
        return pendingContexts.get(0);
    }

    @Override
    public void terminateFlows(String flowDefinitionId, String instanceId, Map<String, Object> filter, OperationContext operationContext) {
        this.flowRuntimeService.terminateFlows(instanceId, filter, operationContext);
    }

    @Override
    public void resumeAsyncJob(String flowDataId, Map<String, Object> businessData, OperationContext operationContext) {
        log.info("[resumeAsyncJob]. flowDataId={}.", flowDataId);
        FlowContext<FlowData> context = this.repo.getById(flowDataId);
        if (!isAllowResumeOrFailAsyncJob(context)) {
            log.warn("Failed to resumeAsyncJob, the context status is wrong. instanceId={}, flowDataId={}, status={}.",
                    context == null ? "NotFound" : context.getTraceId().toString(), flowDataId,
                    context == null ? "NotFound" : context.getStatus().name());
            return;
        }
        this.flowRuntimeService.resumeAsyncJob(Collections.singletonList(flowDataId),
                Collections.singletonList(businessData), operationContext);
    }

    @Override
    public void failAsyncJob(String flowDataId, JoberErrorInfo errorInfo, OperationContext operationContext) {
        log.info("[failAsyncJob]. flowDataId={}, errorInfo={}.", flowDataId, errorInfo.getMessage());
        FlowContext<FlowData> context = this.repo.getById(flowDataId);
        if (!isAllowResumeOrFailAsyncJob(context)) {
            log.warn("Failed to failAsyncJob, the context status is wrong. instanceId={}, flowDataId={}, status={}.",
                    context == null ? "NotFound" : context.getTraceId().toString(), flowDataId,
                    context == null ? "NotFound" : context.getStatus().name());
            return;
        }
        this.flowRuntimeService.failAsyncJob(Collections.singletonList(flowDataId),
                new WaterflowException(FLOW_EXECUTE_ASYNC_JOBER_FAILED, errorInfo), operationContext);
    }

    private static boolean isAllowResumeOrFailAsyncJob(FlowContext<FlowData> context) {
        return context != null && FlowNodeStatus.PROCESSING.equals(context.getStatus());
    }
}
