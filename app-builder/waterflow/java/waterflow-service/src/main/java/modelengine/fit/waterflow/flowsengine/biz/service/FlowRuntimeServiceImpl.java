/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import static modelengine.fit.waterflow.ErrorCodes.ENTITY_NOT_FOUND;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_NODE_MAX_TASK;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_NODE_NOT_FOUND;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_RETRY_JOBER_UPDATE_DATABASE_FAILED;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_START_ERROR;
import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.waterflow.common.Constant.BUSINESS_DATA_IGNORED_KEYS;
import static modelengine.fit.waterflow.common.Constant.BUSINESS_DATA_INTERNAL_KEY;
import static modelengine.fit.waterflow.common.Constant.BUSINESS_DATA_KEY;
import static modelengine.fit.waterflow.common.Constant.OPERATOR_KEY;
import static modelengine.fit.waterflow.common.Constant.STREAM_ID_SEPARATOR;
import static modelengine.fit.waterflow.common.Constant.TO_BATCH_KEY;
import static modelengine.fit.waterflow.common.Constant.TRACE_EXCLUSIVE_STATUS_MAP;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowDefinitionStatus.INACTIVE;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.ProcessType.PROCESS;
import static modelengine.fit.waterflow.spi.FlowCompletedService.FLOW_CALLBACK_GENERICABLE;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.waterflow.entity.OperationContext;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.entity.FlowStartDTO;
import modelengine.fit.waterflow.entity.FlowStartInfo;
import modelengine.fit.waterflow.service.FlowRuntimeService;
import modelengine.fit.ohscript.util.UUIDUtil;
import modelengine.fit.waterflow.common.utils.SleepUtil;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.entity.FlowNodePublishInfo;
import modelengine.fit.waterflow.entity.FlowPublishContext;
import modelengine.fit.waterflow.entity.FlowTransCompletionInfo;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowDefinitionQueryService;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowQueryService;
import modelengine.fit.waterflow.flowsengine.biz.service.entity.FlowRetryInfo;
import modelengine.fit.waterflow.flowsengine.biz.service.entity.FlowsErrorInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.InterStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrans;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStage;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.IdGenerator;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.To;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Blocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Node;
import modelengine.fit.waterflow.flowsengine.domain.flows.utils.FlowExecuteInfoUtil;
import modelengine.fit.waterflow.flowsengine.fitable.TraceServiceImpl;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowContextPO;
import modelengine.fit.waterflow.flowsengine.utils.FlowUtil;
import modelengine.fit.waterflow.spi.FlowCompletedService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * 流程实例相关服务
 *
 * @author 杨祥宇
 * @since 2023/9/1
 */
@Component
public class FlowRuntimeServiceImpl implements FlowRuntimeService {
    private static final Logger LOG = Logger.get(FlowRuntimeServiceImpl.class);

    private static final String DEFAULT_FLOW_CALLBACK_FITABLE = "f58e3a82321c4de6b49fa9006b32e1c3";

    private final FlowDefinitionRepo definitionRepo;

    private final FlowContextRepo repo;

    private final FlowContextMessenger messenger;

    private final QueryFlowContextPersistRepo queryRepo;

    private final FlowTraceRepo traceRepo;

    private final FlowRetryRepo retryRepo;

    private final FlowLocks locks;

    private final TraceOwnerService traceOwnerService;

    private final TraceServiceImpl traceService;

    private final boolean isNeedFlowCallbackAdapt;

    private final BrokerClient brokerClient;

    private final List<String> traceRunningStatus = Arrays.asList(FlowTraceStatus.READY.name(),
            FlowNodeStatus.NEW.name(), FlowNodeStatus.PENDING.name(), FlowNodeStatus.RETRYABLE.name());

    private final FlowDefinitionQueryService definitionQueryService;

    private final FlowQueryService flowQueryService;

    public FlowRuntimeServiceImpl(FlowDefinitionRepo definitionRepo,
                                  @Fit(alias = "flowContextPersistRepo") FlowContextRepo repo,
                                  @Fit(alias = "flowContextPersistMessenger") FlowContextMessenger messenger,
                                  QueryFlowContextPersistRepo queryRepo, FlowTraceRepo traceRepo, FlowRetryRepo retryRepo,
                                  FlowLocks locks, TraceOwnerService traceOwnerService, TraceServiceImpl traceService,
                                  @Value("${jane.flowsEngine.isNeedFlowCallbackAdapt}")
                                  boolean isNeedFlowCallbackAdapt, BrokerClient brokerClient, FlowDefinitionQueryService
                                          definitionQueryService, FlowQueryService flowQueryService) {
        this.definitionRepo = definitionRepo;
        this.repo = repo;
        this.messenger = messenger;
        this.queryRepo = queryRepo;
        this.traceRepo = traceRepo;
        this.retryRepo = retryRepo;
        this.locks = locks;
        this.traceOwnerService = traceOwnerService;
        this.traceService = traceService;
        this.isNeedFlowCallbackAdapt = isNeedFlowCallbackAdapt;
        this.brokerClient = brokerClient;
        this.definitionQueryService = definitionQueryService;
        this.flowQueryService = flowQueryService;
    }

    private static boolean isContextRunning(FlowContextPO flowContextPO) {
        FlowNodeStatus contextStatus = FlowNodeStatus.valueOf(flowContextPO.getStatus());
        return FlowNodeStatus.NEW.equals(contextStatus) || FlowNodeStatus.PENDING.equals(contextStatus)
                || FlowNodeStatus.READY.equals(contextStatus) || FlowNodeStatus.RETRYABLE.equals(contextStatus);
    }

    @Override
    public FlowStartDTO startFlows(String flowId, String flowData, OperationContext operationContext) {
        return startFlows(flowId, FlowData.parseFromJson(flowData), operationContext);
    }

    @Override
    public FlowStartDTO startFlows(String flowId, FlowStartInfo startInfo, OperationContext operationContext) {
        FlowData data = FlowData.builder()
                .operator(startInfo.getOperator())
                .startTime(startInfo.getStartTime() == null ? LocalDateTime.now() : startInfo.getStartTime())
                .businessData(startInfo.getBusinessData())
                .build();
        return startFlows(flowId, data, operationContext);
    }

    private FlowStartDTO startFlows(String flowId, FlowData flowData, OperationContext operationContext) {
        LOG.info("[perf] [{}] startFlows start, flowId={}", System.currentTimeMillis(), flowId);
        Map<String, Object> contextData = ObjectUtils.cast(
                Optional.ofNullable(flowData.getContextData()).orElse(new HashMap<>()));
        flowData.setContextData(contextData);
        FlowDefinition flowDefinition = Optional.ofNullable(definitionQueryService.findByDefinitionId(flowId))
                .orElseThrow(() -> new WaterflowParamException(ENTITY_NOT_FOUND, "startFlows", flowId));
        if (flowDefinition.getStatus() == INACTIVE) {
            throw new WaterflowParamException(FLOW_START_ERROR);
        }
        From<FlowData> from = (From<FlowData>) flowQueryService.getPublisher(flowDefinition.getStreamId());
        if (flowDefinition.isEnableOutputScope()) {
            FlowUtil.cacheResultToNode(flowData.getBusinessData(), from.getId());
        }
        contextData.put("flowDefinitionId", flowId);
        contextData.put("nodeType", FlowNodeType.START.getCode());
        LocalDateTime startTime = LocalDateTime.now();
        FlowOfferId offerId = from.offer(flowData);
        LOG.info("[perf] [{}] startFlows offer end, flowId={}", System.currentTimeMillis(), flowId);
        this.publishStartNodeData(flowDefinition, offerId.getTraceId(), from, flowData, startTime);
        LOG.info("Flow1 has been started, the flow offer id is {}:{}.", offerId.getTrans().getId(),
                offerId.getTraceId());
        return new FlowStartDTO(offerId.getTrans().getId(), offerId.getTraceId());
    }

    @Override
    public FlowStartDTO startFlows(String metaId, String version, String flowData) {
        FlowDefinition flowDefinition = Optional.ofNullable(definitionQueryService.findByStreamId(metaId + version))
                .orElseThrow(() -> new WaterflowParamException(ENTITY_NOT_FOUND, "startFlows", metaId, version));
        if (flowDefinition.getStatus() == INACTIVE) {
            throw new WaterflowParamException(FLOW_START_ERROR);
        }
        From<FlowData> from = ObjectUtils.cast(flowQueryService.getPublisher(flowDefinition.getStreamId()));
        FlowOfferId offerId = from.offer(FlowData.parseFromJson(flowData));
        LOG.info("Flow2 has been started, the flow offer id is {}:{}.", offerId.getTrans().getId(),
                offerId.getTraceId());
        return new FlowStartDTO(offerId.getTrans().getId(), offerId.getTraceId());
    }

    @Override
    public FlowStartDTO startFlowsWithTrans(String metaId, String version, String transId, String flowData) {
        FlowDefinition flowDefinition = Optional.ofNullable(definitionQueryService.findByStreamId(metaId + version))
                .orElseThrow(() -> new WaterflowParamException(ENTITY_NOT_FOUND, "startFlowsWithTrans", metaId, version));
        if (flowDefinition.getStatus() == INACTIVE) {
            throw new WaterflowParamException(FLOW_START_ERROR);
        }
        From<FlowData> from = ObjectUtils.cast(flowQueryService.getPublisher(flowDefinition.getStreamId()));
        FlowOfferId offerId = from.offer(FlowData.parseFromJson(flowData), new FlowTrans(transId));
        LOG.info("Flow3 has been started, the flow offer id is {}:{}.", offerId.getTrans().getId(),
                offerId.getTraceId());
        return new FlowStartDTO(offerId.getTrans().getId(), offerId.getTraceId());
    }

    @Transactional
    public void deleteFlow(String transId) {
        List<String> traceIds = repo.getTraceByTransId(transId);
        repo.deleteByTransId(transId);
        traceRepo.deleteByIdList(traceIds);
    }

    public void offerFlowNode(String metaId, String version, String nodeMetaId, InterStream<FlowData> publisher) {
        FlowDefinition flowDefinition = Optional.ofNullable(definitionRepo.findByMetaIdAndVersion(metaId, version))
                .orElseThrow(() -> new WaterflowParamException(ENTITY_NOT_FOUND, "offerFlowNode", metaId, version));
        if (flowDefinition.getStatus() == INACTIVE) {
            throw new WaterflowParamException(FLOW_START_ERROR);
        }
        From<FlowData> from = ObjectUtils.cast(flowQueryService.getPublisher(flowDefinition.getStreamId()));
        Node<FlowData, ?> node = from.findNodeFromFlow(from, nodeMetaId);
        Validation.notNull(node, () -> new WaterflowParamException(FLOW_NODE_NOT_FOUND, nodeMetaId, metaId, version));
        node.offer(publisher);
    }

    @Override
    public void resumeFlows(String flowId, Map<String, Map<String, Object>> contexts) {
        if (MapUtils.isEmpty(contexts)) {
            return;
        }

        FlowDefinition flowDefinition = Optional.ofNullable(definitionQueryService.findByDefinitionId(flowId))
                .orElseThrow(() -> new WaterflowParamException(ENTITY_NOT_FOUND, "resumeFlows", flowId));
        if (flowDefinition.getStatus() == INACTIVE) {
            throw new WaterflowParamException(FLOW_START_ERROR);
        }

        Set<String> ids = contexts.keySet();
        List<FlowContext<FlowData>> exits = repo.getPendingAndSentByIds(new ArrayList(ids));
        if (CollectionUtils.isEmpty(exits)) {
            LOG.error("[resumeFlows] invalid context ids: {}", ids.toString());
            return;
        }
        String streamId = exits.get(0).getStreamId();
        String position = exits.get(0).getPosition();
        LOG.info("Start resume water flow, flow uuid: {}, stream id: {}, context ids: {}, position: {}", flowId,
                streamId, ids.toString(), position);
        String toBatch = UUIDUtil.uuid();
        From<FlowData> from = (From<FlowData>) flowQueryService.getPublisher(streamId);
        Blocks.Block<FlowData> block = from.getBlock(position);
        boolean enableOutputScope = flowDefinition.isEnableOutputScope();
        exits.forEach(c -> {
            Map<String, Object> changedValues = contexts.get(c.getId());
            contexts.remove(BUSINESS_DATA_INTERNAL_KEY);
            if (enableOutputScope) {
                FlowUtil.cacheResultToNode(cast(changedValues.get(BUSINESS_DATA_KEY)), block.getTarget().getId());
            }
            c.getData().setOperator(String.valueOf(changedValues.get(OPERATOR_KEY)));
            c.getData()
                    .setBusinessData(FlowUtil.mergeMaps(c.getData().getBusinessData(),
                            ObjectUtils.cast(changedValues.get(BUSINESS_DATA_KEY))));
            c.toBatch(toBatch);
        });
        repo.updateFlowDataAndToBatch(exits);
        block.process(exits);
        LOG.info("Resume water flow success, flow uuid: {}, stream id:{}, context ids: {}, position: {}", flowId,
                streamId, ids.toString(), position);
    }

    /**
     * 查询开始节点的context信息
     *
     * @param metaId 流程metaId标识 {@link String}
     * @param version 流程对应的版本 {@link String}
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 开始节点的context列表
     */
    public List<FlowContextPO> findStartNodeContexts(String metaId, String version, OperationContext operationContext) {
        return new ArrayList<>();
    }

    /**
     * 查询当前节点的context列表
     *
     * @param metaId 流程metaId标识 {@link String}
     * @param version version 流程对应的版本 {@link String}
     * @param nodeId 节点id标识 {@link String}
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return context列表
     */
    public List<FlowContextPO> findNodeContexts(String metaId, String version, String nodeId,
            OperationContext operationContext) {
        return new ArrayList<>();
    }

    /**
     * 查询流程实例状态统计视图
     *
     * @param metaId 流程metaId标识 {@link String}
     * @param version version 流程对应的版本 {@link String}
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return context列表
     */
    public List<FlowContext<FlowData>> findContextStatusViewCount(String metaId, String version,
            OperationContext operationContext) {
        return repo.findByStreamId(metaId, version);
    }

    /**
     * 根据streamId查询运行中、错误、已完成的context
     *
     * @param streamIds 流程定义streamId标识列表
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 运行中、错误、已完成的context的列表
     */
    public List<Map<String, Object>> findContextStatusViewByStreamIds(List<String> streamIds,
            OperationContext operationContext) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<FlowContextPO> flowContextPOS = queryRepo.findByStreamIdList(streamIds);
        Map<String, List<FlowContextPO>> flowContextMap = flowContextPOS.stream()
                .collect(Collectors.groupingBy(FlowContextPO::getStreamId));
        flowContextMap.values().forEach(list -> {
            List<FlowContextPO> start = list.stream()
                    .filter(c -> c.getPositionId().equals(c.getRootId()))
                    .collect(Collectors.toList());
            List<FlowContextPO> running = list.stream()
                    .filter(c -> !(FlowNodeStatus.ERROR.equals(FlowNodeStatus.valueOf(c.getStatus()))
                            || FlowNodeStatus.ARCHIVED.equals(FlowNodeStatus.valueOf(c.getStatus()))))
                    .collect(Collectors.toList());
            List<FlowContextPO> error = list.stream()
                    .filter(c -> FlowNodeStatus.ERROR.equals(FlowNodeStatus.valueOf(c.getStatus())))
                    .collect(Collectors.toList());
            Map<String, List<FlowContextPO>> flowContexts = new HashMap<>();
            flowContexts.put("start", start);
            flowContexts.put("running", running);
            flowContexts.put("error", error);
            Map<String, Object> view = flowContextCountViewOf(flowContexts);
            if (!view.isEmpty()) {
                result.add(view);
            }
        });
        return result;
    }

    /**
     * 转换flowContext的view数据
     *
     * @param flowContexts context
     * @return Map<String, Object>
     */
    public static Map<String, Object> flowContextCountViewOf(Map<String, List<FlowContextPO>> flowContexts) {
        Map<String, Object> view = new LinkedHashMap<>(4);
        if (flowContexts.isEmpty() || flowContexts.get("start").isEmpty()) {
            return view;
        }
        put(view, "allContexts", flowContexts.get("start").size());
        put(view, "runningContexts", flowContexts.get("running").size());
        put(view, "errorContexts", flowContexts.get("error").size());
        put(view, "streamId", flowContexts.get("start").get(0).getStreamId());
        return view;
    }

    private static void put(Map<String, Object> view, String key, Object value) {
        if (value != null) {
            view.put(key, value);
        }
    }

    /**
     * 根据流程实例traceId查询所有contexts
     *
     * @param traceId 流程实例traceId
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 流程实例traceId对应的contexts
     */
    public List<FlowContext<String>> findContextByTraceId(String traceId, OperationContext operationContext) {
        return repo.getContextsByTrace(traceId);
    }

    public List<FlowsErrorInfo> getFlowErrorInfo(String traceId) {
        List<FlowContext<FlowData>> errorContexts = repo.findErrorContextsByTraceId(traceId);
        FlowTrace flowTrace = Optional.ofNullable(traceRepo.find(traceId))
                .orElseThrow(() -> new WaterflowParamException(ENTITY_NOT_FOUND, traceId));
        FlowDefinition flowDefinition = definitionQueryService.findByStreamId(flowTrace.getStreamId());
        if (flowDefinition == null) {
            LOG.error("Get flow error info failed, flow definition not found");
            throw new WaterflowParamException(ENTITY_NOT_FOUND, traceId);
        }
        return errorContexts.stream().map(c -> {
            FlowNode flowNode = flowDefinition.getNodeMap().get(c.getPosition());
            String nodeName = null;
            if (flowNode == null) {
                nodeName = flowDefinition.getToNodeByEventId(c.getPosition()).getName();
            } else {
                nodeName = flowNode.getName();
            }
            return FlowsErrorInfo.builder()
                    .businessData(c.getData().getBusinessData())
                    .contextErrorInfo(c.getData().getErrorInfo())
                    .nodeId(c.getPosition())
                    .nodeName(nodeName)
                    .build();
        }).collect(Collectors.toList());
    }

    private void publishStartNodeData(FlowDefinition flowDefinition, String traceId, From<FlowData> from,
            FlowData flowData, LocalDateTime createAt) {
        Map<String, Object> businessData = flowData.getBusinessData();
        Map<String, Object> inputData = ObjectUtils.cast(
                businessData.getOrDefault("startNodeInputParams", new HashMap<String, Object>()));
        FlowNode startNode = flowDefinition.getFlowNode(FlowNodeType.START);
        String nodeId = startNode.getMetaId();
        if (!inputData.isEmpty()) {
            FlowExecuteInfoUtil.addInputMap2ExecuteInfoMap(flowData, inputData, nodeId, "start");
            FlowExecuteInfoUtil.addOutputMap2ExecuteInfoMap(flowData, inputData, nodeId, "start");
        }
        FlowPublishContext startContext = new FlowPublishContext(traceId, FlowNodeStatus.ARCHIVED.name(),
            FlowNodeStage.AFTER.name(), createAt, createAt, createAt);
        FlowNodePublishInfo flowNodePublishInfo = new FlowNodePublishInfo();
        flowNodePublishInfo.setFlowDefinitionId(flowDefinition.getDefinitionId());
        flowNodePublishInfo.setNodeId(nodeId);
        flowNodePublishInfo.setNodeType(FlowNodeType.START.getCode());
        flowNodePublishInfo.setNodeProperties(startNode.getProperties());
        flowNodePublishInfo.setBusinessData(businessData);
        flowNodePublishInfo.setFlowContext(startContext);
        flowNodePublishInfo.setNextPositionId(from.getSubscriptions().get(0).getId());
        flowNodePublishInfo.setErrorMsg(new FlowErrorInfo());
        traceService.publishNodeInfo(flowNodePublishInfo);
    }

    private List<FlowContextPO> getCurNodeContexts(List<FlowContextPO> contextPos, FlowNode node,
            Map<String, Set<FlowEvent>> fromEvents) {
        List<FlowContextPO> result = new ArrayList<>();
        Set<String> fromIds = Optional.ofNullable(fromEvents.get(node.getMetaId()))
                .map(es -> es.stream().map(FlowEvent::getFrom).collect(Collectors.toSet()))
                .orElse(new HashSet<>());
        Set<String> eventIds = Optional.ofNullable(fromEvents.get(node.getMetaId()))
                .map(es -> es.stream().map(FlowEvent::getMetaId).collect(Collectors.toSet()))
                .orElse(new HashSet<>());

        result.addAll(contextPos.stream()
                .filter(context -> "NEW".equals(context.getStatus()))
                .filter(context -> fromIds.contains(context.getPositionId()))
                .collect(Collectors.toList()));
        result.addAll(contextPos.stream()
                .filter(context -> !"NEW".equals(context.getStatus()))
                .filter(context -> (context.getPositionId().equals(node.getMetaId())) || eventIds.contains(
                        context.getPositionId()))
                .collect(Collectors.toList()));

        return result;
    }

    private String getTraceStatus(String traceId, FlowDefinition flowDefinition) {
        boolean isTerminated = repo.hasContextWithStatus(Collections.singletonList(FlowNodeStatus.TERMINATE.name()),
                traceId);
        if (isTerminated) {
            return FlowTraceStatus.TERMINATE.name();
        }

        boolean isRunning = repo.hasContextWithStatus(traceRunningStatus, traceId);
        if (isRunning) {
            return FlowTraceStatus.RUNNING.name();
        }
        boolean isArchived = repo.isAllContextStatus(Collections.singletonList(FlowNodeStatus.ARCHIVED.name()),
                traceId);
        if (isArchived) {
            return FlowTraceStatus.ARCHIVED.name();
        }
        boolean hasError = repo.hasContextWithStatus(Collections.singletonList(FlowNodeStatus.ERROR.name()), traceId);
        if (hasError) {
            String endNode = flowDefinition.getEndNode();
            boolean hasArchivedInEndNode = repo.hasContextWithStatusAtPosition(
                    Collections.singletonList(FlowNodeStatus.ARCHIVED.name()), traceId, endNode);
            if (hasArchivedInEndNode) {
                return FlowTraceStatus.PARTIAL_ERROR.name();
            }
            return FlowTraceStatus.ERROR.name();
        }
        return FlowTraceStatus.RUNNING.name();
    }

    @Override
    public void terminateFlows(String traceId, Map<String, Object> filter, OperationContext operationContext) {
        FlowTrace flowTrace = traceRepo.find(traceId);
        if (Objects.isNull(flowTrace)) {
            LOG.warn("The trace is not exist. traceId={}.", traceId);
            return;
        }
        if (TRACE_EXCLUSIVE_STATUS_MAP.get(FlowTraceStatus.TERMINATE.toString())
                .contains(flowTrace.getStatus().toString())) {
            LOG.warn("The trace cannot be terminated, traceId={}, currentStatus={}, start={}, end={}.",
                    flowTrace.getId(), flowTrace.getStatus().name(), flowTrace.getStartTime(), flowTrace.getEndTime());
            return;
        }

        List<FlowContext<String>> contextList = repo.findWithoutFlowDataByTraceId(traceId);
        Set<String> traceIdSet = contextList.stream()
                .flatMap(context -> context.getTraceId().stream())
                .collect(Collectors.toSet());

        traceRepo.updateStatus(new ArrayList<>(traceIdSet), FlowTraceStatus.TERMINATE.toString());
        repo.updateStatus(contextList, FlowNodeStatus.TERMINATE.toString(), null);
        LOG.info("The trace is terminated, traceId={}, currentStatus={}, start={}.", flowTrace.getId(),
                flowTrace.getStatus().name(), flowTrace.getStartTime());
    }

    @Override
    public void resumeAsyncJob(List<String> preIds, List<Map<String, Object>> businessDataList,
                               OperationContext operationContext) {
        LOG.info("resumeAsyncJob preContextId: {}", String.join(",", preIds));
        LocalDateTime currentTime = LocalDateTime.now();
        List<FlowContext<FlowData>> pre = repo.getByIds(preIds);
        FlowContext<FlowData> preContext = pre.get(0);
        String operator = Optional.ofNullable(operationContext)
                .flatMap(c -> Optional.ofNullable(c.operator()))
                .orElse(preContext.getData().getOperator());
        FlowDefinition flowDefinition = definitionQueryService.findByStreamId(preContext.getStreamId());
        boolean enableOutputScope = flowDefinition.isEnableOutputScope();
        From<FlowData> from = (From<FlowData>) flowQueryService.getPublisher(flowDefinition.getStreamId());
        Node<FlowData, FlowData> node = from.findNodeFromFlow(from, preContext.getPosition());
        String flowDataTemplate = preContext.getData().translateToJson();

        FlowNode currentFlowNode = flowDefinition.getFlowNode(preContext.getPosition());
        List<FlowContext<FlowData>> after = businessDataList.stream().map(data -> {
            FlowData flowData = FlowData.parseFromJson(flowDataTemplate);
            flowData.setOperator(operator);
            flowData.setStartTime(currentTime);
            BUSINESS_DATA_IGNORED_KEYS.forEach(data::remove);
            flowData.getBusinessData().putAll(data);
            Map<String, Object> newOutputMap = currentFlowNode.getJober().getConverter().convertOutput(null);
            updateMap(newOutputMap, flowData.getBusinessData());
            if (enableOutputScope) {
                FlowUtil.cacheResultToNode(newOutputMap, flowData.getBusinessData(), node.getId());
            }
            FlowExecuteInfoUtil.addOutputMap2ExecuteInfoMap(flowData, newOutputMap, currentFlowNode.getMetaId(),
                    "jober");
            return preContext.generate(flowData, preContext.getPosition(), currentTime);
        }).collect(Collectors.toList());

        LOG.info("resumeAsyncJob afterContextId: {}",
                after.stream().map(FlowContext::getId).collect(Collectors.joining(",")));
        node.afterProcess(pre, after);
    }

    private static void updateMap(Map<String, Object> newOutputMap, Map<String, Object> businessData) {
        for (Map.Entry<String, Object> entry : newOutputMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                updateValueFromBusinessData(newOutputMap, businessData, key);
            } else if (value instanceof Map) {
                updateNestedMap(businessData, key, value);
            }
        }
    }

    private static void updateValueFromBusinessData(Map<String, Object> newOutputMap, Map<String, Object> businessData,
            String key) {
        if (businessData.containsKey(key)) {
            newOutputMap.put(key, businessData.get(key));
        }
    }

    private static void updateNestedMap(Map<String, Object> businessData, String key, Object value) {
        Object businessDataValue = businessData.get(key);
        if (businessDataValue instanceof Map) {
            updateMap((Map<String, Object>) value, (Map<String, Object>) businessDataValue);
        }
    }

    @Override
    public void failAsyncJob(List<String> preIds, WaterflowException exception, OperationContext operationContext) {
        LOG.info("failAsyncJob preContextId={}, exception={}.", String.join(",", preIds),
                exception.getMessage());
        List<FlowContext<FlowData>> pre = this.repo.getByIds(preIds);
        if (pre.isEmpty()) {
            LOG.error("Set async job exception failed, pre context size is empty.");
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, "preContexts");
        }
        FlowContext<FlowData> preContext = pre.get(0);
        From<FlowData> from = (From<FlowData>) flowQueryService.getPublisher(preContext.getStreamId());
        Node<FlowData, FlowData> node = cast(from.findNodeFromFlow(from, preContext.getPosition()));
        node.setFailed(pre, exception);
    }

    public void terminateFlowsByTransId(String transId, OperationContext operationContext) {
        List<FlowContextPO> contexts = queryRepo.findWithoutFlowDataByTransIdList(Collections.singletonList(transId));
        if (contexts.isEmpty()) {
            LOG.warn("Flow context list is empty, flowTransId : {}", transId);
            return;
        }
        Map<String, Object> filter = new HashMap<>();
        contexts.stream()
                .map(FlowContextPO::getTraceId)
                .collect(Collectors.toSet())
                .forEach(traceId -> terminateFlows(traceId, filter, operationContext));
    }

    @Scheduled(strategy = Scheduled.Strategy.FIXED_RATE, value = "5000")
    public void calculateFlowTraceStatus() {
        try {
            List<String> traceIds = this.traceOwnerService.getTraces();
            if (traceIds.isEmpty()) {
                return;
            }
            List<FlowTrace> flowTraces = traceRepo.findTraceByIdList(traceIds);
            List<String> flowTraceIds = flowTraces.stream().map(IdGenerator::getId).collect(Collectors.toList());
            traceIds.forEach(traceId -> {
                if (!flowTraceIds.contains(traceId)) {
                    this.tryReleaseMissedTrace(traceId);
                }
            });
            flowTraces.forEach(trace -> {
                calculateTraceStatus(trace);
            });
        } catch (Throwable e) {
            LOG.error("The calculate flow trace status timer has error, traceSize={}, errorMessage={}.",
                    this.traceOwnerService.getTraces().size(), e.getMessage());
            LOG.error("Exception=", e);
        }
    }

    private void tryReleaseMissedTrace(String traceId) {
        LOG.info("Check the missed trace. id={}.", traceId);
        if (this.traceOwnerService.isInProtectTime(traceId)) {
            return;
        }
        LOG.info("The missed trace is expired. id={}.", traceId);
        this.traceOwnerService.release(traceId);
    }

    private void calculateTraceStatus(FlowTrace trace) {
        try {
            String transId = repo.getTransIdByTrace(trace.getId());
            if (StringUtils.isBlank(transId)) {
                LOG.info("The trace is not ready, traceId={}.", trace.getId());
                return;
            }
            FlowDefinition flowDefinition = definitionQueryService.findByStreamId(trace.getStreamId());
            if (flowDefinition == null) {
                LOG.warn("Flow definition is null, stream id:{}, trace:{}", trace.getStreamId(), trace.getId());
                this.traceOwnerService.release(trace.getId());
                return;
            }
            String status = getTraceStatus(trace.getId(), flowDefinition);
            if (!Objects.equals(status, FlowTraceStatus.RUNNING.name())) {
                updateTraceStatus(trace, transId, status, flowDefinition);
            }
        } catch (Throwable e) {
            LOG.error("Failed to calculate flow trace status, streamId={}, traceId={}.", trace.getStreamId(),
                    trace.getId(), e.getMessage());
            LOG.error("Exception=", e);
        }
    }

    private void updateTraceStatus(FlowTrace trace, String transId, String status,
                                   FlowDefinition flowDefinition) {
        LOG.info("The trace is completed, traceId={}, status={}.", trace.getId(), status);
        traceRepo.updateStatus(Collections.singletonList(trace.getId()), status);
        Lock transIdLock = locks.getDistributedLock(transId);
        transIdLock.lock();
        try {
            FlowTraceStatus transStatus = calculateTransStatus(transId);
            if (!Objects.equals(FlowTraceStatus.RUNNING, transStatus)) {
                LOG.info("The trans is completed, transId={}, status={}.", transId, transStatus);
                transFinishedCallback(transId, transStatus, flowDefinition.getFinishedCallbackFitables());
            }
        } finally {
            transIdLock.unlock();
        }
        LOG.debug("Start release trace lock, traceId={}.", trace.getId());
        this.traceOwnerService.release(trace.getId());
        LOG.info("Finish processing the trace, transId={}, traceId={}, status={}.", transId, trace.getId(), status);
    }

    private void transFinishedCallback(String transId, FlowTraceStatus transStatus, Set<String> callbackFitables) {
        String streamId = repo.getStreamIdByTransId(transId);
        String metaId = streamId.split(String.valueOf(STREAM_ID_SEPARATOR))[0];
        String version = streamId.split(String.valueOf(STREAM_ID_SEPARATOR))[1];
        List<String> traceIds = repo.getTraceByTransId(transId);
        FlowTransCompletionInfo info = new FlowTransCompletionInfo(metaId, version, transId, traceIds,
                transStatus.name());
        if (isNeedFlowCallbackAdapt && callbackFitables.isEmpty()) {
            // 兼容为空的逻辑，默认调用数据清洗回调实现
            callbackFitables.add(DEFAULT_FLOW_CALLBACK_FITABLE);
        }

        callbackFitables.forEach(fitable -> {
            try {
                brokerClient.getRouter(FlowCompletedService.class, FLOW_CALLBACK_GENERICABLE)
                        .route(new FitableIdFilter(fitable))
                        .timeout(5, TimeUnit.SECONDS)
                        .invoke(info);
            } catch (FitException ex) {
                LOG.error("Flow finished callback error, fitable id: {}.", fitable);
                LOG.error("Exception", ex);
            }
        });
    }

    /**
     * calculateTransStatus
     *
     * @param transId transId
     * @return FlowTraceStatus
     */
    public FlowTraceStatus calculateTransStatus(String transId) {
        List<String> traceIds = repo.getTraceByTransId(transId);
        List<FlowTrace> traces = traceRepo.findTraceByIdList(traceIds);
        boolean isAllTerminated = traces.stream()
                .allMatch(flowTrace -> Objects.equals(FlowTraceStatus.TERMINATE, flowTrace.getStatus()));
        boolean isAllArchived = traces.stream()
                .allMatch(flowTrace -> Objects.equals(FlowTraceStatus.ARCHIVED, flowTrace.getStatus()));
        boolean isAllError = traces.stream()
                .allMatch(flowTrace -> Objects.equals(FlowTraceStatus.ERROR, flowTrace.getStatus()));
        boolean hasArchivedContext = traces.stream()
                .anyMatch(flowTrace -> Objects.equals(FlowTraceStatus.PARTIAL_ERROR, flowTrace.getStatus())
                        || Objects.equals(FlowTraceStatus.ARCHIVED, flowTrace.getStatus()));
        boolean hasRunningTrace = traces.stream()
                .anyMatch(flowTrace -> Objects.equals(FlowTraceStatus.RUNNING, flowTrace.getStatus()));
        if (hasRunningTrace) {
            return FlowTraceStatus.RUNNING;
        } else if (isAllError) {
            return FlowTraceStatus.ERROR;
        } else if (isAllArchived) {
            return FlowTraceStatus.ARCHIVED;
        } else if (isAllTerminated) {
            return FlowTraceStatus.TERMINATE;
        } else if (hasArchivedContext) {
            return FlowTraceStatus.PARTIAL_ERROR;
        } else {
            return FlowTraceStatus.RUNNING;
        }
    }

    public boolean hasTerminatedTrace(String transId) {
        List<String> traceIds = repo.getTraceByTransId(transId);
        List<FlowTrace> traces = traceRepo.findTraceByIdList(traceIds);
        return traces.stream().anyMatch(trace -> Objects.equals(FlowTraceStatus.TERMINATE, trace.getStatus()));
    }

    public void deleteFlowContexts(List<String> contextIds, OperationContext operationContext) {
        repo.deleteByContextIds(contextIds);
    }

    public void updateTraceStatus(List<String> traceIds, FlowTraceStatus status) {
        traceRepo.updateStatus(traceIds, status.toString());
    }

    public List<String> findTraceIdsByContextIds(List<String> contextIds) {
        return repo.findTraceIdsByContextIds(contextIds);
    }
}
