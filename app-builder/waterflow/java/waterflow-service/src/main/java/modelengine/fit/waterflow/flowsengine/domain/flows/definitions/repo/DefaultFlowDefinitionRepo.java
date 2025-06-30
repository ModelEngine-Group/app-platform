/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.RequiredArgsConstructor;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.ohscript.util.UUIDUtil;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import modelengine.fit.waterflow.flowsengine.persist.entity.FlowStreamInfo;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FitableUsageMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowDefinitionMapper;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static modelengine.fit.waterflow.ErrorCodes.ENTITY_NOT_FOUND;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_ALREADY_EXIST;

/**
 * DefaultFlowDefinitionRepo
 * {@link FlowDefinitionRepo} 的默认实现
 *
 * @author 晏钰坤
 * @since 2023/8/15
 */
@Component
@RequiredArgsConstructor
public class DefaultFlowDefinitionRepo implements FlowDefinitionRepo {
    private static final Logger log = Logger.get(DefaultFlowDefinitionRepo.class);

    private final FlowDefinitionMapper flowDefinitionMapper;

    private final FitableUsageMapper fitableUsageMapper;

    private final Parser parser;

    @Override
    @Transactional
    public FlowDefinition save(FlowDefinition entity, String graphData) {
        Optional<FlowDefinitionPO> flowDefinition = Optional.ofNullable(
                flowDefinitionMapper.findByMetaIdAndVersion(entity.getMetaId(), entity.getVersion()));
        if (flowDefinition.isPresent()) {
            log.error("Duplicate metaId: {} , version: {}", entity.getMetaId(), entity.getVersion());
            throw new WaterflowException(FLOW_ALREADY_EXIST, "Not allowed to have the same metaId and version.");
        }
        flowDefinition = Optional.ofNullable(
                flowDefinitionMapper.findByFlowNameAndVersion(entity.getName(), entity.getVersion()));
        if (flowDefinition.isPresent()) {
            log.error("Duplicate name: {} , version: {}", entity.getName(), entity.getVersion());
            throw new WaterflowException(FLOW_ALREADY_EXIST, "Not allowed to have the same name and version.");
        }
        entity.setDefinitionId(UUIDUtil.uuid());
        flowDefinitionMapper.create(this.serializer(entity, graphData), LocalDateTime.now());
        String definitionId = entity.getDefinitionId();
        List<String> fitableIds = getAllFitables(graphData);
        if (!fitableIds.isEmpty()) {
            fitableUsageMapper.save(definitionId, fitableIds);
        }
        return entity;
    }

    @Override
    public FlowDefinition find(String definitionId) {
        FlowDefinitionPO flowDefinitionPO = Optional.ofNullable(flowDefinitionMapper.find(definitionId))
                .orElseThrow(() -> {
                    log.error("Cannot find flow definition by ID {}.", definitionId);
                    return new WaterflowException(ENTITY_NOT_FOUND, "FlowDefinition", definitionId);
                });
        return serializer(flowDefinitionPO);
    }

    @Override
    @Transactional
    public void delete(String flowDefinitionId) {
        flowDefinitionMapper.delete(flowDefinitionId);
        fitableUsageMapper.deleteByDefinitionId(flowDefinitionId);
    }

    @Override
    public List<FlowDefinitionPO> findByTenantId(String tenantId) {
        return flowDefinitionMapper.findByTenantId(tenantId);
    }

    @Override
    public FlowDefinitionPO findByFlowNameVersion(String name, String version) {
        return flowDefinitionMapper.findByFlowNameAndVersion(name, version);
    }

    @Override
    public FlowDefinition findByStreamId(String streamId) {
        String[] ids = streamId.split(String.valueOf(Constant.STREAM_ID_SEPARATOR), 2);
        String metaId = ids[0];
        String version = ids[1];
        return this.serializer(flowDefinitionMapper.findByMetaIdAndVersion(metaId, version));
    }

    @Override
    public void update(FlowDefinition flowDefinition, String graphData) {
        flowDefinitionMapper.update(serializer(flowDefinition, graphData));
    }

    @Override
    public FlowDefinition findByMetaIdAndVersion(String metaId, String version) {
        FlowDefinitionPO definitionPO = flowDefinitionMapper.findByMetaIdAndVersion(metaId, version);
        return Optional.ofNullable(definitionPO).map(this::serializer).orElse(null);
    }

    @Override
    public List<FlowDefinition> findByStreamIdList(List<String> streamIds) {
        List<FlowStreamInfo> streams = new ArrayList<>();
        streamIds.stream().forEach(s -> {
            String[] ids = s.split(String.valueOf(Constant.STREAM_ID_SEPARATOR));
            String metaId = ids[0];
            String version = ids[1];
            streams.add(new FlowStreamInfo(metaId, version));
        });

        return flowDefinitionMapper.findByStreamIdList(streams)
                .stream()
                .map(this::serializer)
                .collect(Collectors.toList());
    }

    private FlowDefinitionPO serializer(FlowDefinition entity, String graphData) {
        return FlowDefinitionPO.builder()
                .definitionId(entity.getDefinitionId())
                .metaId(entity.getMetaId())
                .name(entity.getName())
                .version(entity.getVersion())
                .tenant(entity.getTenant())
                .createdBy(entity.getCreatedBy())
                .graph(graphData)
                .status(entity.getStatus().getCode())
                .build();
    }

    private FlowDefinition serializer(FlowDefinitionPO entityPO) {
        if (entityPO == null) {
            return null;
        }
        String graph = JSON.parse(entityPO.getGraph()).toString();
        FlowDefinition flowDefinition = parser.parse(graph);
        flowDefinition.setDefinitionId(entityPO.getDefinitionId());
        flowDefinition.setReleaseTime(entityPO.getCreatedAt());
        return flowDefinition;
    }

    private List<String> getAllFitables(String graphData) {
        JSONArray nodes = JSONObject.parseObject(graphData).getJSONArray("nodes");
        Set<String> fitableIds = new HashSet<>();
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (!node.containsKey("jober")) {
                continue;
            }
            JSONObject jober = node.getJSONObject("jober");
            if (jober.containsKey("fitables")) {
                fitableIds.addAll(ObjectUtils.<List<String>>cast(jober.get("fitables")));
            }
        }
        return new ArrayList<>(fitableIds);
    }
}
