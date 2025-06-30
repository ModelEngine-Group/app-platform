/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service.scheduletasks;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_START_ERROR;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.biz.service.TraceOwnerService;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowDefinitionQueryService;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowQueryService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowDefinitionStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.IdGenerator;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Node;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程实例状态计算定时器
 * 以flow trace为粒度
 *
 * @author 杨祥宇
 * @since 2024/2/20
 */
public class RestartContextSchedule {
    private static final Logger log = Logger.get(RestartContextSchedule.class);

    private static final List<String> APPLICATIONS = new ArrayList<>();

    private final FlowTraceRepo traceRepo;

    private final FlowContextPersistRepo contextPersistRepo;

    private final DefaultFlowDefinitionRepo flowDefinitionRepo;

    private final FlowLocks locks;

    private final FlowContextPersistMessenger messenger;

    private final TraceOwnerService traceOwnerService;

    private final FlowDefinitionQueryService definitionQueryService;

    private final FlowQueryService flowQueryService;

    public RestartContextSchedule(FlowTraceRepo traceRepo, FlowContextPersistRepo contextPersistRepo,
        DefaultFlowDefinitionRepo flowDefinitionRepo, FlowLocks locks, FlowContextPersistMessenger messenger,
        TraceOwnerService traceOwnerService, FlowDefinitionQueryService definitionQueryService,
        FlowQueryService flowQueryService) {
        this.traceRepo = traceRepo;
        this.contextPersistRepo = contextPersistRepo;
        this.flowDefinitionRepo = flowDefinitionRepo;
        this.locks = locks;
        this.messenger = messenger;
        this.traceOwnerService = traceOwnerService;
        this.definitionQueryService = definitionQueryService;
        this.flowQueryService = flowQueryService;
    }

    /**
     * 重启中断的context
     */
    @Scheduled(strategy = Scheduled.Strategy.FIXED_RATE, value = "60000")
    public void restartInterruptContext() {
        try {
            List<String> traceIds = traceRepo.findRunningTrace(APPLICATIONS);
            log.info("restartInterruptContext. traceIds:{}.", String.join(",", traceIds));
            restartContext(traceIds);
        } catch (Throwable e) {
            log.error("[restartInterruptContext] exception, errorMessage={}.", e.getMessage());
            log.error("[restartInterruptContext] exception=", e);
        }
    }

    private void restartContext(List<String> traceIds) {
        for (String traceId : traceIds) {
            try {
                if (this.traceOwnerService.isOwn(traceId)) {
                    continue;
                }
                if (!this.traceOwnerService.tryOwn(traceId, null)) {
                    continue;
                }
                log.warn("restartInterruptContext take over trace:{}", traceId);
                Optional<FlowTrace> flowTrace = Optional.ofNullable(traceRepo.find(traceId));
                if (!flowTrace.isPresent()) {
                    traceOwnerService.release(traceId);
                    continue;
                }
                String streamId = flowTrace.get().getStreamId();
                Optional<FlowDefinition> flowDefinitionOptional = Optional.ofNullable(
                        definitionQueryService.findByStreamId(streamId));
                if (!flowDefinitionOptional.isPresent()) {
                    traceOwnerService.release(traceId);
                    continue;
                }
                FlowDefinition flowDefinition = flowDefinitionOptional.get();
                if (flowDefinition.getStatus() == FlowDefinitionStatus.INACTIVE) {
                    throw new WaterflowParamException(FLOW_START_ERROR);
                }
                From<FlowData> flow = (From<FlowData>) flowQueryService.getPublisher(streamId);
                List<List<FlowContext<FlowData>>> restartContexts =
                        new ArrayList<>(contextPersistRepo.getRunningContextsByTraceId(traceId)
                                .stream()
                                .collect(Collectors.groupingBy(context -> context.getPosition() + context.getStatus()))
                                .values());
                restartContext(flow, restartContexts, flowDefinition);
            } catch (Throwable e) {
                log.error("Restart flow error, trace id: {}, errorMessage: {}.", traceId, e.getMessage());
                log.error("Exception=", e);
            }
        }
    }

    private void restartContext(From<FlowData> flow, List<List<FlowContext<FlowData>>> restartContexts,
            FlowDefinition flowDefinition) {
        restartContexts.forEach(contexts -> {
            restart(flow, flowDefinition, contexts);
        });
    }

    private void restart(From<FlowData> flow, FlowDefinition flowDefinition, List<FlowContext<FlowData>> contexts) {
        FlowContext<FlowData> context = contexts.get(0);
        if (Objects.equals(context.getStatus(), FlowNodeStatus.NEW)) {
            // 节点上new状态context
            FlowNode flowNode = flowDefinition.getFlowNode(context.getPosition());
            if (flowNode.belongTo(FlowNodeType.START)) {
                flow.offer(contexts, (c) -> {});
                return;
            }
            Node<FlowData, FlowData> node = flow.findNodeFromFlow(flow, context.getPosition());
            node.offer(contexts, (c) -> {});
        } else if (Objects.equals(context.getStatus(), FlowNodeStatus.PENDING) && !flowDefinition.getNodeMap()
                .containsKey(context.getPosition())) {
            // 线上pending状态context
            FlowNode flowNode = flowDefinition.getFromNodeByEvent(context.getPosition());
            if (flowNode.belongTo(FlowNodeType.START)) {
                flow.offer(contexts, (c) -> {});
                return;
            }
            Node<FlowData, FlowData> node = flow.findNodeFromFlow(flow, flowNode.getMetaId());
            node.offer(contexts, (c) -> {});
        } else if (Objects.equals(context.getStatus(), FlowNodeStatus.READY)) {
            // 节点上ready状态context加到重试列表
            List<List<FlowContext<FlowData>>> batchContextLists = new ArrayList<>(contexts.stream().collect(
                    Collectors.groupingBy(FlowContext::getToBatch)).values());
            batchContextLists.forEach(batchContexts -> {
                List<String> contextIds = batchContexts.stream().map(IdGenerator::getId)
                        .collect(Collectors.toList());
                contextPersistRepo.updateStatus(contextIds, FlowNodeStatus.RETRYABLE);
                contextPersistRepo.saveRetrySchedule(batchContexts);
            });
        } else {
            log.warn("Unknown context status, status={}, traceId={}, position={}.", context.getStatus(),
                    context.getTraceId(), context.getPosition());
        }
    }
}
