/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace;

import static modelengine.fit.waterflow.common.Constant.TRACE_EXCLUSIVE_STATUS_MAP;

import lombok.RequiredArgsConstructor;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowTraceMapper;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowTracePO;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link FlowTraceRepo}默认实现类
 *
 * @author 杨祥宇
 * @since 2023/8/30
 */
@Component
@RequiredArgsConstructor
public class DefaultFlowTraceRepo implements FlowTraceRepo {
    private final FlowTraceMapper flowTraceMapper;

    @Override
    public void save(FlowTrace flowTrace) {
        if (find(flowTrace.getId()) == null) {
            flowTraceMapper.create(this.serializer(flowTrace));
        } else {
            flowTraceMapper.update(this.serializer(flowTrace));
        }
    }

    @Override
    public FlowTrace find(String traceId) {
        return this.serializer(flowTraceMapper.find(traceId));
    }

    @Override
    public List<FlowTrace> getByIds(List<String> traceIds) {
        return flowTraceMapper.findByTraceIdList(traceIds).stream().map(this::serializer).collect(Collectors.toList());
    }

    @Override
    public void delete(String streamId) {
        flowTraceMapper.delete(streamId);
    }

    @Override
    public void batchCreate(List<FlowTrace> flowTraces) {
        flowTraceMapper.batchCreate(flowTraces.stream().map(this::serializer).collect(Collectors.toList()));
    }

    @Override
    public void batchUpdate(List<FlowTrace> flowTraces) {
        flowTraceMapper.batchUpdate(flowTraces.stream().map(this::serializer).collect(Collectors.toList()));
    }

    @Override
    public void updateContextPool(List<String> traceList, List<String> contextList) {
        traceList.forEach(t -> {
            Set<String> contextPool = this.find(t).getContextPool();
            contextPool.addAll(contextList);
            flowTraceMapper.updateContextPool(t, String.join(", ", contextPool));
        });
    }

    @Override
    public void updateStatus(List<String> traceIds, String status) {
        flowTraceMapper.updateStatus(traceIds, status, LocalDateTime.now(), TRACE_EXCLUSIVE_STATUS_MAP.get(status));
    }

    @Override
    public List<String> findRunningTrace(List<String> applications) {
        return flowTraceMapper.findRunningTrace(applications);
    }

    @Override
    public List<FlowTrace> findTraceByIdList(List<String> traceIds) {
        List<FlowTracePO> flowTracePOS = flowTraceMapper.findByIdList(traceIds);
        return flowTracePOS.stream().map(this::serializer).collect(Collectors.toList());
    }

    @Override
    public void deleteByIdList(List<String> traceIds) {
        if (traceIds.isEmpty()) {
            return;
        }
        flowTraceMapper.deleteByIdList(traceIds);
    }

    @Override
    public List<String> getExpiredTrace(int expiredDays, int limit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(expiredDays);
        return flowTraceMapper.getExpiredTrace(expired, limit);
    }

    private FlowTracePO serializer(FlowTrace flowTrace) {
        String contextPool = String.join(", ", flowTrace.getContextPool());
        return FlowTracePO.builder()
                .traceId(flowTrace.getId())
                .operator(flowTrace.getOperator())
                .application(flowTrace.getApplication())
                .startNode(flowTrace.getStartNode())
                .contextPool(contextPool)
                .startTime(flowTrace.getStartTime())
                .endTime(flowTrace.getEndTime())
                .streamId(flowTrace.getStreamId())
                .status(flowTrace.getStatus().toString())
                .build();
    }

    private FlowTrace serializer(FlowTracePO flowTracePO) {
        if (flowTracePO == null) {
            return null;
        }
        FlowTrace flowTrace = new FlowTrace(flowTracePO.getTraceId());
        flowTrace.setOperator(flowTracePO.getOperator());
        flowTrace.setApplication(flowTracePO.getApplication());
        flowTrace.setStartNode(flowTracePO.getStartNode());
        Set<String> contextPool = new HashSet<>();
        if (StringUtils.isNotEmpty(flowTracePO.getContextPool())) {
            Collections.addAll(contextPool, flowTracePO.getContextPool().split(", "));
        }
        flowTrace.setContextPool(contextPool);
        flowTrace.setStartTime(flowTracePO.getStartTime());
        flowTrace.setEndTime(flowTracePO.getEndTime());
        flowTrace.setStreamId(flowTracePO.getStreamId());
        flowTrace.setStatus(FlowTraceStatus.valueOf(flowTracePO.getStatus()));
        return flowTrace;
    }
}
