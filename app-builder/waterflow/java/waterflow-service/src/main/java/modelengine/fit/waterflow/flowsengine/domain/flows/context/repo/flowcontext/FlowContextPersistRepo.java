/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext;

import static modelengine.fit.waterflow.ErrorCodes.ENTITY_NOT_FOUND;
import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static modelengine.fit.waterflow.common.Constant.CONTEXT_EXCLUSIVE_STATUS_MAP;
import static modelengine.fit.waterflow.common.Constant.RETRY_INTERVAL;
import static modelengine.fit.waterflow.common.Constant.STREAM_ID_SEPARATOR;
import static modelengine.fit.waterflow.common.Constant.TO_BATCH_KEY;

import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.flowsengine.biz.service.TraceOwnerService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrans;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.IdGenerator;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors.Filter;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors.Validator;
import modelengine.fit.waterflow.flowsengine.persist.entity.FlowContextUpdateInfo;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowContextPO;
import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 持久化{@link FlowContext}对象到数据库核心类
 * 与{@link FlowContextMemoRepo}组成{@link FlowContextRepo}的不同实现
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Component
@Alias("flowContextPersistRepo")
public class FlowContextPersistRepo implements FlowContextRepo<FlowData> {
    private static final Logger log = Logger.get(FlowContextPersistRepo.class);

    private static final String TRACE_ID_SEPARATE = ", ";

    private static final String PASS_DATA = "system_key_pass_data";

    private final FlowContextMapper contextMapper;

    private final FlowTraceRepo traceRepo;

    private final FlowRetryRepo retryRepo;

    private final TraceOwnerService traceOwnerService;

    private final boolean useLimit;

    private final long maxRetryCount;

    private final Integer defaultLimitation;

    public FlowContextPersistRepo(FlowContextMapper contextMapper, FlowTraceRepo traceRepo, FlowRetryRepo retryRepo,
            TraceOwnerService traceOwnerService, @Value("${modelengine.limit}") Integer limit,
            @Value("${modelengine.useLimit}") boolean hasUseLimit,
            @Value("${jane.flowsEngine.retry.maxCount}") long maxRetryCount) {
        this.traceOwnerService = traceOwnerService;
        this.useLimit = hasUseLimit;
        this.contextMapper = contextMapper;
        this.traceRepo = traceRepo;
        this.retryRepo = retryRepo;
        this.defaultLimitation = limit;
        this.maxRetryCount = maxRetryCount;
    }

    /**
     * convertTextToSet
     *
     * @param textData textData
     * @return Set<String>
     */
    public static Set<String> convertTextToSet(String textData) {
        if (textData == null || "".equals(textData)) {
            return new HashSet<>();
        }
        Set<String> resultSet = new HashSet<>();
        if (!textData.isEmpty()) {
            String[] items = textData.split(", ");
            Collections.addAll(resultSet, items);
        }
        return resultSet;
    }

    @Override
    public List<FlowContext<FlowData>> getContextsByPosition(String streamId, List<String> posIds, String status) {
        List<String> traceIds = this.traceOwnerService.getTraces();
        if (traceIds.isEmpty()) {
            log.warn("There is no trace owned.");
            return Collections.emptyList();
        }
        List<FlowContextPO> pos = contextMapper.findByPositions(streamId, posIds, status, traceIds);
        if (pos.isEmpty()) {
            log.info("[getContextsByPosition] Empty contexts. traceIds={}, pos={}.", StringUtils.join(',', traceIds),
                    StringUtils.join(',', posIds));
        }
        return pos.stream().map(this::serializer).collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> getContextsByPosition(String streamId, String posId, String batchId,
            String status) {
        List<FlowContextPO> pos = contextMapper.findByPositionWithBatchId(streamId, posId, batchId, status);
        return pos.stream().map(this::serializer).collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<String>> findWithoutFlowDataByTraceId(String traceId) {
        return contextMapper.findWithoutFlowDataByTraceId(traceId)
                .stream()
                .map(this::serializerAsString)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<String>> getContextsByTrace(String traceId) {
        FlowTrace trace = traceRepo.find(traceId);
        if (!Optional.ofNullable(trace).isPresent() || CollectionUtils.isEmpty(trace.getContextPool())) {
            return new ArrayList<>();
        }
        List<FlowContextPO> pos = contextMapper.findByContextIdList(new ArrayList<>(trace.getContextPool()));
        return pos.stream().map(this::serializerAsString).collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> getContextsByTrace(String traceId, String status) {
        FlowTrace trace = traceRepo.find(traceId);
        if (!Optional.ofNullable(trace).isPresent() || CollectionUtils.isEmpty(trace.getContextPool())) {
            return new ArrayList<>();
        }
        List<FlowContextPO> pos = contextMapper.findByContextIdList(new ArrayList<>(trace.getContextPool()));
        return pos.stream()
                .filter(context -> status.equals(context.getStatus()))
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public void save(List<FlowContext<FlowData>> flowContexts) {
        if (flowContexts == null || flowContexts.size() == 0) {
            return;
        }
        FlowContextPO flowContextPO = contextMapper.find(flowContexts.get(0).getId());
        List<FlowContextPO> flowContextPOS = flowContexts.stream().map(this::serializer).collect(Collectors.toList());
        if (flowContextPO == null) {
            contextMapper.batchCreate(flowContextPOS);
        } else {
            batchUpdate(flowContextPOS);
        }
    }

    private void batchUpdate(List<FlowContextPO> flowContextPOS) {
        contextMapper.batchUpdate(flowContextPOS);
    }

    @Override
    public void update(List<FlowContext<FlowData>> contexts) {
        List<FlowContextPO> flowContextPOS = contexts.stream().map(this::serializer).collect(Collectors.toList());
        batchUpdate(flowContextPOS);
    }

    @Override
    public void saveWithoutPassData(List<FlowContext<FlowData>> flowContexts) {
        if (flowContexts == null || flowContexts.size() == 0) {
            return;
        }

        FlowContextPO flowContextPO = contextMapper.find(flowContexts.get(0).getId());
        List<FlowContextPO> flowContextPOS = flowContexts.stream().map(this::serializer).collect(Collectors.toList());
        if (flowContextPO == null) {
            contextMapper.batchCreate(flowContextPOS);
        } else {
            batchUpdate(flowContextPOS);
        }
    }

    @Override
    public void save(FlowContext<FlowData> context) {
        throw new WaterflowException(INPUT_PARAM_IS_EMPTY);
    }

    @Override
    public void updateToSent(List<FlowContext<FlowData>> contexts) {
        contextMapper.updateToSent(contexts.stream().map(IdGenerator::getId).collect(Collectors.toList()));
    }

    @Override
    public void updateToReady(List<FlowContext<FlowData>> flowContexts) {
        throw new WaterflowException(INPUT_PARAM_IS_EMPTY);
    }

    @Override
    public void updateFlowDataAndToBatch(List<FlowContext<FlowData>> contexts) {
        List<FlowContextPO> flowContextPOS = contexts.stream().map(this::serializer).collect(Collectors.toList());
        this.contextMapper.updateFlowDataAndToBatch(flowContextPOS);
    }

    @Override
    public void updateFlowData(Map<String, FlowData> flowDataList) {
        this.contextMapper.updateFlowData(flowDataList.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry<String, FlowData>::getKey,
                        entry -> entry.getValue().translateToJson())));
    }

    @Override
    public void updateStatus(List<FlowContext<FlowData>> contexts, String status, String position) {
        List<String> ids = contexts.stream().map(IdGenerator::getId).collect(Collectors.toList());
        LocalDateTime updateAt = LocalDateTime.now();
        LocalDateTime archivedAt = status.equals(FlowNodeStatus.ARCHIVED.toString()) ? updateAt : null;
        contextMapper.updateStatusAndPosition(ids, new FlowContextUpdateInfo(status, position, updateAt, archivedAt),
                CONTEXT_EXCLUSIVE_STATUS_MAP.get(status));
    }

    @Override
    public void updateToTerminated(List<String> traceIds) {
        List<FlowContext<String>> contexts = getContextsByTrace(traceIds.get(0));
        List<String> ids = contexts.stream().map(IdGenerator::getId).collect(Collectors.toList());
        String status = FlowTraceStatus.TERMINATE.toString();
        contextMapper.updateStatusAndPosition(ids,
                new FlowContextUpdateInfo(status, contexts.get(0).getPosition(), LocalDateTime.now(), null),
                CONTEXT_EXCLUSIVE_STATUS_MAP.get(status));

        traceRepo.updateStatus(traceIds, status);
    }

    @Override
    public boolean isTracesTerminate(List<String> traceIds) {
        return traceRepo.getByIds(traceIds)
                .stream()
                .anyMatch(flowTrace -> FlowTraceStatus.TERMINATE.equals(flowTrace.getStatus()));
    }

    @Override
    public List<FlowContext<FlowData>> getContextsByParallel(String parallelId) {
        return new ArrayList<>();
    }

    @Override
    public FlowContext<FlowData> getById(String id) {
        return Optional.ofNullable(contextMapper.find(id)).map(this::serializer).orElseThrow(() -> {
            log.error("Cannot find flow context by ID {}.", id);
            return new WaterflowException(ENTITY_NOT_FOUND, "FlowContext", id);
        });
    }

    @Override
    public List<FlowContext<FlowData>> getByToBatch(List<String> toBatchIds) {
        if (toBatchIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<FlowContextPO> pos = contextMapper.findByToBatch(toBatchIds);
        return pos.stream().map(this::serializer).collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> getPendingAndSentByIds(List<String> ids) {
        return contextMapper.findByContextIdList(ids)
                .stream()
                .filter(p -> p.getStatus().equals(FlowNodeStatus.PENDING.toString()))
                .filter(FlowContextPO::isSent)
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> getByIds(List<String> ids) {
        return contextMapper.findByContextIdList(ids).stream().map(this::serializer).collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> requestMappingContext(String streamId, List<String> subscriptions,
            Filter<FlowData> filter, Validator<FlowData> validator) {
        List<String> traces = this.traceOwnerService.getTraces();
        List<FlowContextPO> pos = contextMapper.findBySubscriptions(streamId, subscriptions,
                FlowNodeStatus.PENDING.toString(), traces);
        List<FlowContext<FlowData>> all = pos.stream().map(this::serializer).collect(Collectors.toList());
        List<FlowContext<FlowData>> filters = filter.process(all);
        return filters.stream().filter(c -> validator.check(c, filters)).collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> requestProducingContext(String streamId, List<String> subscriptions,
            Filter<FlowData> filter) {
        List<FlowContextPO> pos;
        List<String> traces = this.traceOwnerService.getTraces();
        if (traces.isEmpty()) {
            log.warn("There is no trace owned.");
            return Collections.emptyList();
        }
        if (useLimit) {
            pos = contextMapper.findSomeBySubscriptions(streamId, subscriptions, FlowNodeStatus.PENDING.toString(),
                    traces, defaultLimitation);
        } else {
            pos = contextMapper.findBySubscriptions(streamId, subscriptions, FlowNodeStatus.PENDING.toString(), traces);
        }
        List<FlowContext<FlowData>> result =
                filter.process(pos.stream().map(this::serializer).collect(Collectors.toList()));
        if (result.isEmpty()) {
            log.info("[requestProducingContext] Empty contexts. traceIds={}, pos={}, beforeSize={}, afterSize={}.",
                    StringUtils.join(',', traces), StringUtils.join(',', subscriptions), pos.size(), result.size());
        }
        return result;
    }

    @Override
    public List<FlowContext<FlowData>> findByStreamId(String metaId, String version) {
        String streamId = metaId + STREAM_ID_SEPARATOR + version;
        List<FlowContextPO> flowContextPOs = contextMapper.findByStreamId(streamId);
        return flowContextPOs.stream().map(this::serializer).collect(Collectors.toList());
    }

    @Override
    public Integer findRunningContextCountByMetaId(String metaId, String version) {
        String streamId = metaId + STREAM_ID_SEPARATOR + version;
        return contextMapper.findRunningContextCountByMetaId(streamId);
    }

    @Override
    public void delete(String metaId, String version) {
        String streamId = StringUtils.join(STREAM_ID_SEPARATOR, metaId, version);
        contextMapper.delete(streamId);
        traceRepo.delete(streamId);
    }

    @Override
    public void updateContextPool(List<FlowContext<FlowData>> after, Set<String> traces) {
        traceRepo.updateContextPool(new ArrayList<>(traces),
                after.stream().map(IdGenerator::getId).collect(Collectors.toList()));
    }

    @Override
    public void save(FlowTrace trace, FlowContext<FlowData> flowContext) {
        trace.setOperator(flowContext.getData().getOperator());
        trace.setApplication(flowContext.getData().getApplication());
        trace.setStartTime(flowContext.getData().getStartTime());
        traceRepo.save(trace);
    }

    @Override
    public FlowRetry getRetrySchedule(String entityId) {
        return retryRepo.getById(entityId);
    }

    @Override
    public boolean isMaxRetryCount(String entityId) {
        if (maxRetryCount == 0) {
            return true;
        }
        FlowRetry flowRetry = retryRepo.getById(entityId);
        if (flowRetry == null || maxRetryCount == -1) {
            return false;
        }
        return flowRetry.getRetryCount() >= maxRetryCount;
    }

    @Override
    public void createRetrySchedule(List<FlowRetry> flowRetryList) {
        retryRepo.save(flowRetryList);
    }

    @Override
    public void updateRetrySchedule(List<String> entityIdList, LocalDateTime nextRetryTime) {
        retryRepo.updateNextRetryTime(entityIdList, nextRetryTime);
    }

    @Override
    public void saveRetrySchedule(List<FlowContext<FlowData>> contexts) {
        String toBatch = contexts.get(0).getToBatch();
        FlowRetry flowRetry = this.getRetrySchedule(toBatch);
        if (flowRetry == null) {
            flowRetry = new FlowRetry(toBatch, TO_BATCH_KEY, LocalDateTime.now(), null, 0, 1);
            this.createRetrySchedule(Collections.singletonList(flowRetry));
        } else {
            LocalDateTime nextRetryTime = flowRetry.getLastRetryTime().plus(RETRY_INTERVAL, ChronoUnit.MILLIS);
            this.updateRetrySchedule(Collections.singletonList(flowRetry.getEntityId()), nextRetryTime);
        }
    }

    @Override
    public void deleteRetryRecord(List<String> entityIdList) {
        retryRepo.delete(entityIdList);
    }

    private FlowContextPO serializer(FlowContext<FlowData> context) {
        String traceId = String.join(TRACE_ID_SEPARATE, context.getTraceId());
        context.getData().getContextData().put("flowTransId", context.getTrans().getId());
        context.getData().getContextData().put("metaId", context.getId());
        context.getData().getBusinessData().put(PASS_DATA, context.getData().getPassData());
        context.getData().getContextData().put("contextId", context.getId());
        context.getData().getContextData().put("nodeMetaId", context.getPosition());
        context.getData().getContextData().put("flowTraceIds", new ArrayList<>(context.getTraceId()));
        FlowContextPO result = FlowContextPO.builder()
            .contextId(context.getId())
            .traceId(traceId)
            .transId(context.getTrans().getId())
            .rootId(context.getRootId())
            .streamId(context.getStreamId())
            .flowData(context.getData().translateToJson())
            .positionId(context.getPosition())
            .status(context.getStatus().toString())
            .parallel(context.getParallel())
            .parallelMode(context.getParallelMode())
            .previous(context.getPrevious())
            .batchId(context.getBatchId())
            .toBatch(context.getToBatch())
            .sent(context.isSent())
            .createAt(context.getCreateAt())
            .updateAt(context.getUpdateAt())
            .archivedAt(context.getArchivedAt())
            .build();
        context.getData().getBusinessData().remove(PASS_DATA);
        return result;
    }

    private FlowContext<FlowData> serializer(FlowContextPO po) {
        Set<String> traceId = convertTextToSet(po.getTraceId());
        FlowContext<FlowData> context = new FlowContext<>(po.getStreamId(), po.getRootId(), getFlowData(po), traceId,
                po.getPositionId(), po.getParallel(), po.getParallelMode(), LocalDateTime.now());
        convertOthers(po, context);
        return context;
    }

    private FlowContext<String> serializerAsString(FlowContextPO po) {
        Set<String> traceIds = convertTraceIds(po);
        FlowContext<String> context = new FlowContext<>(po.getStreamId(), po.getRootId(), po.getFlowData(), traceIds,
                po.getPositionId(), po.getParallel(), po.getParallelMode(), LocalDateTime.now());
        convertOthers(po, context);
        return context;
    }

    private Set<String> convertTraceIds(FlowContextPO po) {
        Set<String> traceIds = new HashSet<>();
        if (StringUtils.isNotEmpty(po.getTraceId())) {
            Collections.addAll(traceIds, po.getTraceId().split(TRACE_ID_SEPARATE));
        }
        return traceIds;
    }

    private <T> void convertOthers(FlowContextPO po, FlowContext<T> context) {
        context.setId(po.getContextId());
        context.setPrevious(po.getPrevious());
        context.setTrans(new FlowTrans(po.getTransId()));
        context.setStatus(FlowNodeStatus.valueOf(po.getStatus()));
        context.batchId(po.getBatchId());
        context.toBatch(po.getToBatch());
        context.setSent(po.isSent());
        context.setCreateAt(po.getCreateAt());
        context.setUpdateAt(po.getUpdateAt());
        context.setArchivedAt(po.getArchivedAt());
    }

    private FlowData getFlowData(FlowContextPO po) {
        FlowData flowData = FlowData.parseFromJson(po.getFlowData());
        flowData.setPassData(ObjectUtils.cast(flowData.getBusinessData().get(PASS_DATA)));
        flowData.getBusinessData().remove(PASS_DATA);
        return flowData;
    }

    /**
     * updateStatus
     *
     * @param contextId contextId
     * @param status status
     */
    public void updateStatus(List<String> contextId, FlowNodeStatus status) {
        contextMapper.updateStatus(contextId, status);
    }

    @Override
    public List<FlowContext<FlowData>> findByTraceId(String traceId) {
        return contextMapper.findByTraceId(traceId).stream().map(this::serializer).collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> findErrorContextsByTraceId(String traceId) {
        return contextMapper.findErrorContextByTraceId(traceId)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> findErrorContextsByTransId(String transId) {
        return contextMapper.findErrorContextByTransId(transId)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    /**
     * getRunningContextsByStreamIds
     *
     * @param streamIds streamIds
     * @return List<FlowContext < FlowData>>
     */
    public List<FlowContext<FlowData>> getRunningContextsByStreamIds(List<String> streamIds) {
        return contextMapper.findRunningContextByStreamIds(streamIds).stream().map(c -> {
            try {
                return this.serializer(c);
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<String> getRunningContextsIdByTransaction(String flowTransId) {
        return contextMapper.getRunningContextsIdByTransaction(flowTransId);
    }

    @Override
    public List<String> getRunningContextsIdByTraceId(String traceId) {
        return contextMapper.getRunningContextsIdByTraceId(traceId);
    }

    @Override
    public List<FlowContext<FlowData>> findFinishedContextsPagedByTransId(String flowTransId, String endNode,
            Integer pageNum, Integer limit) {
        return contextMapper.findFinishedContextsPagedByTransId(flowTransId, endNode, pageNum, limit)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> getEndContextsPagedByTransId(String flowTransId, String endNode, Integer pageNum,
            Integer limit) {
        return contextMapper.findEndContextsPagedByTransId(flowTransId, endNode, pageNum, limit)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> getErrorContextsPagedByTransId(String flowTransId, Integer pageNum,
            Integer limit) {
        return contextMapper.findErrorContextsPagedByTransId(flowTransId, pageNum, limit)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public String getStreamIdByTransId(String flowTransId) {
        return contextMapper.getStreamIdByTransId(flowTransId);
    }

    @Override
    public int findFinishedPageNumByTransId(String flowTransId, String endNode) {
        return contextMapper.findFinishedPageNumByTransId(flowTransId, endNode);
    }

    @Override
    public int findEndContextsPageNumByTransId(String flowTransId, String endNode) {
        return contextMapper.findEndContextsNumByTransId(flowTransId, endNode);
    }

    @Override
    public int findErrorContextsPageNumByTransId(String flowTransId) {
        return contextMapper.findErrorContextsNumByTransId(flowTransId);
    }

    @Override
    public List<String> getTraceByTransId(String transId) {
        return contextMapper.getTraceByTransId(transId);
    }

    @Override
    public void deleteByTransId(String transId) {
        contextMapper.deleteByTransId(transId);
    }

    @Override
    public List<FlowContext<FlowData>> findFinishedContextsPagedByTraceId(String traceId, String endNode,
            Integer pageNum, Integer limit) {
        return contextMapper.findFinishedContextsPagedByTraceId(traceId, endNode, pageNum, limit)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> getEndContextsPagedByTraceId(String traceId, String endNode, Integer pageNum,
            Integer limit) {
        return contextMapper.findEndContextsPagedByTraceId(traceId, endNode, pageNum, limit)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> getErrorContextsPagedByTraceId(String traceId, Integer pageNum, Integer limit) {
        return contextMapper.findErrorContextsPagedByTraceId(traceId, pageNum, limit)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public int findFinishedPageNumByTraceId(String traceId, String endNode) {
        return contextMapper.findFinishedPageNumByTraceId(traceId, endNode);
    }

    @Override
    public int findEndContextsPageNumByTraceId(String traceId, String endNode) {
        return contextMapper.findEndContextsNumByTraceId(traceId, endNode);
    }

    @Override
    public int findErrorContextsPageNumByTraceId(String traceId) {
        return contextMapper.findErrorContextsNumByTraceId(traceId);
    }

    @Override
    public List<FlowContext<FlowData>> getRunningContextsByTraceId(String traceId) {
        return contextMapper.getRunningContextsByTraceId(traceId)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public TraceOwnerService getTraceOwnerService() {
        return traceOwnerService;
    }

    @Override
    public void deleteByContextIds(List<String> contextIds) {
        contextMapper.deleteByContextIds(contextIds);
    }

    @Override
    public List<String> findTraceIdsByContextIds(List<String> contextIds) {
        return contextMapper.findTraceIdsByContextIds(contextIds);
    }

    @Override
    public List<FlowContext<FlowData>> findFinishedContextsByTransId(String flowTransId, String endNode) {
        return contextMapper.findFinishedContextsByTransId(flowTransId, endNode)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<FlowData>> findFinishedContextsByTraceId(String flowTraceId, String endNode) {
        return contextMapper.findFinishedContextsByTraceId(flowTraceId, endNode)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    @Override
    public void updateProcessStatus(List<FlowContext<FlowData>> contexts) {
        List<String> ids = contexts.stream().map(IdGenerator::getId).collect(Collectors.toList());

        String toBatch = contexts.get(0).getToBatch();
        String status = contexts.get(0).getStatus().toString();
        String position = contexts.get(0).getPosition();

        LocalDateTime updateAt = LocalDateTime.now();
        LocalDateTime archivedAt = status.equals(FlowNodeStatus.ARCHIVED.toString()) ? updateAt : null;

        contextMapper.updateProcessStatus(ids,
                new FlowContextUpdateInfo(toBatch, status, position, updateAt, archivedAt),
                CONTEXT_EXCLUSIVE_STATUS_MAP.get(status));
    }

    @Override
    public List<FlowContext<String>> getWithoutFlowDataByToBatch(List<String> toBatchIds) {
        if (toBatchIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<FlowContextPO> pos = contextMapper.findWithoutFlowDataByToBatch(toBatchIds);
        return pos.stream().map(this::serializerAsString).collect(Collectors.toList());
    }

    @Override
    public boolean hasContextWithStatus(List<String> statusList, String traceId) {
        if (statusList.isEmpty()) {
            return false;
        }
        int count = contextMapper.findCountByStatus(statusList, traceId);
        return count != 0;
    }

    @Override
    public boolean isAllContextStatus(List<String> statusList, String traceId) {
        if (statusList.isEmpty()) {
            return false;
        }
        int count = contextMapper.findCountNotInStatus(statusList, traceId);
        return count == 0;
    }

    @Override
    public boolean hasContextWithStatusAtPosition(List<String> statusList, String traceId, String position) {
        if (statusList.isEmpty()) {
            return false;
        }
        int count = contextMapper.findCountByStatusAtPosition(statusList, traceId, position);
        return count != 0;
    }

    @Override
    public String getTransIdByTrace(String traceId) {
        return contextMapper.getTransIdByTrace(traceId);
    }

    @Override
    public void deleteByTraceIdList(List<String> traceIdList) {
        if (CollectionUtils.isEmpty(traceIdList)) {
            return;
        }
        contextMapper.deleteByTraceIdList(traceIdList);
    }
}
