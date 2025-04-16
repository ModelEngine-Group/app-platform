/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import lombok.RequiredArgsConstructor;
import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;
import modelengine.fit.jade.waterflow.entity.FlowNodeFormInfo;
import modelengine.fit.jade.waterflow.entity.FlowNodeInfo;
import modelengine.fit.waterflow.entity.OperationContext;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.QueryFlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowDefinitionStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTaskType;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.FlowValidator;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static modelengine.fit.waterflow.ErrorCodes.ENTITY_NOT_FOUND;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_DEFINITION_UPDATE_NOT_SUPPORT;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_GRAPH_DATA_PARSE_FAILED;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_VALIDATE_ERROR;
import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;

/**
 * 流程定义相关服务
 *
 * @author 杨祥宇
 * @since 2023/8/1
 */
@Component
@RequiredArgsConstructor
public class FlowsDefinitionServiceImpl implements FlowDefinitionService {
    private static final Logger log = Logger.get(FlowsDefinitionServiceImpl.class);

    private static final String DEFAULT_TENANT = "e3431aa80d224c278cdeb5003790e1a7";

    private final FlowParser flowParser;

    private final FlowValidator flowValidator;

    private final FlowDefinitionRepo flowDefinitionRepo;

    private final FlowContextPersistRepo flowContextPersistRepo;

    private final QueryFlowDefinitionRepo queryFlowDefinitionRepo;

    @Override
    public FlowDefinitionResult createFlows(String graphData, OperationContext context) {
        FlowDefinition flowsDefinition = convertToDefinition(graphData, context);
        flowDefinitionRepo.save(flowsDefinition, graphData);
        return convert(flowsDefinition, graphData);
    }

    @Override
    public void validateDefinitionData(String definitionData) {
        this.convertToDefinition(definitionData, OperationContext.custom().tenantId(DEFAULT_TENANT).build());
    }

    private FlowDefinition convertToDefinition(String graphData, OperationContext context) {
        Validation.notBlank(graphData, () -> new WaterflowParamException(INPUT_PARAM_IS_EMPTY, "graphData"));
        FlowDefinition flowsDefinition;
        try {
            flowsDefinition = flowParser.parse(graphData);
        } catch (WaterflowException ex) {
            throw new WaterflowException(FLOW_GRAPH_DATA_PARSE_FAILED, ex);
        }
        flowsDefinition.setTenant(context.tenantId());
        flowsDefinition.setCreatedBy(context.operator());
        try {
            flowValidator.validate(flowsDefinition);
        } catch (WaterflowException ex) {
            if (ex.getCode() != INPUT_PARAM_IS_INVALID.getErrorCode()) {
                throw ex;
            }
            throw new WaterflowException(FLOW_VALIDATE_ERROR, ex.getMessage());
        }
        return flowsDefinition;
    }

    @Override
    @Deprecated
    public FlowDefinitionResult updateFlows(String flowId, String graphData, OperationContext operationContext) {
        FlowDefinition flowDefinition = flowDefinitionRepo.find(flowId);
        FlowDefinition parseFlow = flowParser.parse(graphData);
        Validation.notNull(flowDefinition, () -> new WaterflowParamException(ENTITY_NOT_FOUND, "FlowDefinition", flowId));
        Validation.isTrue(FlowDefinitionStatus.ACTIVE.equals(flowDefinition.getStatus()),
                () -> new WaterflowParamException(FLOW_DEFINITION_UPDATE_NOT_SUPPORT, flowDefinition.getStatus()));
        parseFlow.setDefinitionId(flowDefinition.getDefinitionId());
        flowDefinitionRepo.update(parseFlow, graphData);
        return convert(parseFlow, graphData);
    }

    @Override
    @Transactional
    public void deleteFlows(String flowId, OperationContext context) {
        FlowDefinition flowDefinition = flowDefinitionRepo.find(flowId);
        Integer runningContextNum = flowContextPersistRepo.findRunningContextCountByMetaId(flowDefinition.getMetaId(),
                flowDefinition.getVersion());
        if (runningContextNum == 0) {
            flowDefinitionRepo.delete(flowId);
            flowContextPersistRepo.delete(flowDefinition.getMetaId(), flowDefinition.getVersion());
        }
    }

    @Override
    public void deleteFlows(String metaId, String version, OperationContext context) {
        FlowDefinition flowDefinition = flowDefinitionRepo.findByMetaIdAndVersion(metaId, version);
        if (flowDefinition == null) {
            return;
        }
        deleteFlows(flowDefinition.getDefinitionId(), context);
    }

    @Override
    @Transactional
    public void forceDeleteFlows(String metaId, String version, OperationContext context) {
        FlowDefinition flowDefinition = flowDefinitionRepo.findByMetaIdAndVersion(metaId, version);
        if (flowDefinition == null) {
            return;
        }
        flowDefinitionRepo.delete(flowDefinition.getDefinitionId());
        flowContextPersistRepo.delete(flowDefinition.getMetaId(), flowDefinition.getVersion());
    }

    @Override
    public List<FlowDefinitionResult> findFlowsByTenant(OperationContext context) {
        return flowDefinitionRepo.findByTenantId(context.tenantId())
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public FlowDefinitionResult findFlowsByName(String name, String version, OperationContext context) {
        return convert(flowDefinitionRepo.findByFlowNameVersion(name, version));
    }

    @Override
    public FlowDefinitionResult findFlowsById(String flowId, OperationContext context) {
        return convert(flowDefinitionRepo.find(flowId), null);
    }

    @Override
    public FlowDefinitionResult findFlowsByMetaIdAndVersion(String metaId, String version, OperationContext context) {
        return convert(queryFlowDefinitionRepo.findByMetaIdAndVersion(metaId, version));
    }

    @Override
    public List<FlowDefinitionResult> findFlowsByMetaIdAndPartVersion(String metaId, String version) {
        return queryFlowDefinitionRepo.findByMetaIdAndPartVersion(metaId, version)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, FlowDefinitionResult> findFlowsByStreamIds(List<String> streamIds) {
        List<FlowDefinitionPO> flowDefinitionPOList = queryFlowDefinitionRepo.findByStreamId(streamIds);
        return flowDefinitionPOList.stream()
                .collect(Collectors.toMap(
                        f -> f.getMetaId() + Constant.STREAM_ID_SEPARATOR + f.getVersion(), this::convert));
    }

    @Override
    public String getScript(List<String> fitableIds) {
        if (fitableIds.isEmpty()) {
            return "ext::context";
        }
        StringBuilder script = new StringBuilder();
        for (int i = 0; i < fitableIds.size(); i++) {
            script.append("let context")
                    .append(i + 1)
                    .append(" = entity{\n" + "    .id = \"")
                    .append(fitableIds.get(i))
                    .append("\";\n" + ".async = true;\n")
                    .append(".format = \"cbor\";\n" + "};\n")
                    .append("let f")
                    .append(i + 1)
                    .append(" = fit::handleTask(context")
                    .append(i + 1)
                    .append(");\n\n");
        }
        script.append("ext::context");
        for (int i = 0; i < fitableIds.size(); i++) {
            script.append(" >> f").append(i + 1);
        }
        return script.toString();
    }

    @Override
    public Map<String, Object> getFlowDefinitionByFitable(String fitableId, Integer offset, Integer limit) {
        log.info("Get flow definition by fitable id: {}", fitableId);
        Map<String, Object> res = new HashMap<>();
        res.put("definitions", queryFlowDefinitionRepo.findByFitableId(fitableId, offset, limit)
                .stream()
                .map(FlowsDefinitionServiceImpl::viewOfFlowDefinition)
                .collect(Collectors.toList()));
        res.put("total", queryFlowDefinitionRepo.getCountByFitableId(fitableId));
        return res;
    }

    private static void put(Map<String, Object> view, String key, Object value) {
        if (value != null) {
            view.put(key, value);
        }
    }

    @Override
    public Map<String, Integer> getCountByFitable(List<String> fitableIds) {
        log.info("Get flow definition count by fitable ids: {}", fitableIds);
        List<Map<String, Object>> result = queryFlowDefinitionRepo.selectFitableCounts(fitableIds);
        Map<String, Integer> formatResult = new HashMap<>();
        result.forEach(entry -> {
            formatResult.put(ObjectUtils.cast(entry.get("fitableId")),
                    Integer.parseInt(String.valueOf(entry.get("count"))));
        });
        return formatResult;
    }

    @Override
    public FlowDefinitionResult getFlowDefinitionByGraphData(String flowGraph) {
        return convert(flowParser.parse(flowGraph), flowGraph);
    }

    /**
     * 组装definition信息
     *
     * @param definition definition
     * @return Map<String, Object>
     */
    static Map<String, Object> viewOfFlowDefinition(FlowDefinitionPO definition) {
        Map<String, Object> view = new LinkedHashMap<>(4);
        FlowsDefinitionServiceImpl.put(view, "id", definition.getMetaId());
        FlowsDefinitionServiceImpl.put(view, "version", definition.getVersion());
        FlowsDefinitionServiceImpl.put(view, "name", definition.getName());
        FlowsDefinitionServiceImpl.put(view, "graph", definition.getGraph());
        return view;
    }

    private FlowDefinitionResult convert(FlowDefinition flowsDefinition, String graphData) {
        return new FlowDefinitionResult.Builder(flowsDefinition.getDefinitionId())
                .setGraph(graphData)
                .setMetaId(flowsDefinition.getMetaId())
                .setName(flowsDefinition.getName())
                .setTenantId(flowsDefinition.getTenant())
                .setStatus(flowsDefinition.getStatus().getCode())
                .setVersion(flowsDefinition.getVersion())
                .setPublishNodeFitables(flowsDefinition.getPublishNodeFitables())
                .setFlowNodes(getFlowNodeInfos(flowsDefinition.getNodeMap()))
                .build();
    }

    private FlowDefinitionResult convert(FlowDefinitionPO flowsDefinitionPo) {
        return new FlowDefinitionResult.Builder(flowsDefinitionPo.getDefinitionId())
                .setGraph(flowsDefinitionPo.getGraph())
                .setMetaId(flowsDefinitionPo.getMetaId())
                .setName(flowsDefinitionPo.getName())
                .setTenantId(flowsDefinitionPo.getTenant())
                .setStatus(flowsDefinitionPo.getStatus())
                .setVersion(flowsDefinitionPo.getVersion())
                .build();
    }

    private static List<FlowNodeInfo> getFlowNodeInfos(Map<String, FlowNode> nodeMap) {
        return nodeMap.entrySet().stream().map(entry -> {
            String nodeMetaId = entry.getKey();
            FlowNode node = entry.getValue();
            FlowNodeInfo flowNodeInfo = new FlowNodeInfo();
            flowNodeInfo.setId(nodeMetaId);
            flowNodeInfo.setName(node.getName());
            flowNodeInfo.setType(node.getType().getCode());
            FlowNodeFormInfo flowNodeForm = null;
            FlowTask task = node.getTask();
            if (task != null) {
                if (task.getTaskType() == FlowTaskType.AIPP_SMART_FORM) {
                    flowNodeForm = new FlowNodeFormInfo();
                    flowNodeForm.setFormId(task.getTaskId());
                    flowNodeForm.setVersion("1.0.0");
                }
            }
            flowNodeInfo.setFlowNodeForm(flowNodeForm);
            flowNodeInfo.setProperties(node.getProperties()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, item -> item.getValue())));
            return flowNodeInfo;
        }).collect(Collectors.toList());
    }
}
