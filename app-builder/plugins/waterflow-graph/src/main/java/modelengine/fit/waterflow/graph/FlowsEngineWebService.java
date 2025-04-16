/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


package modelengine.fit.waterflow.graph;

import static modelengine.fit.jober.common.ErrorCodes.FLOW_DEFINITION_DELETE_ERROR;
import static modelengine.fit.jober.common.ErrorCodes.FLOW_GRAPH_DATA_PARSE_FAILED;
import static modelengine.fit.jober.common.ErrorCodes.FLOW_GRAPH_SAVE_ERROR;
import static modelengine.fit.jober.common.ErrorCodes.NOT_SUPPORT;
import static modelengine.fit.jober.common.ErrorCodes.UN_EXCEPTED_ERROR;

import modelengine.fit.jade.waterflow.FlowsEngineService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.flow.graph.entity.FlowGraphDefinition;
import modelengine.fit.jane.flow.graph.entity.FlowGraphQueryParam;
import modelengine.fit.jane.flow.graph.entity.FlowSaveEntity;
import modelengine.fit.jane.flow.graph.entity.elsa.GraphParam;
import modelengine.fit.jane.flow.graph.entity.elsa.response.GetPageResponse;
import modelengine.fit.jane.flow.graph.repo.FlowsGraphRepo;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.common.exceptions.JobberException;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.waterflow.biz.task.TagService;
import modelengine.fit.waterflow.biz.util.ControllerUtil;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import modelengine.fit.waterflow.graph.util.FlowDefinitionParseUtils;
import modelengine.fit.waterflow.service.FlowRuntimeService;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程查询bff封装
 *
 * @author 杨祥宇
 * @since 2023/10/11
 */
public class FlowsEngineWebService implements FlowsEngineService {
    private static final Logger log = Logger.get(FlowsEngineWebService.class);

    private static final String UNPUBLISHED_STATUS = "unpublished";

    private static final String PUBLISHED_STATUS = "active";

    private static final String FLOW_GRAPH = "FLOW GRAPH";

    private static final String TEMP_VERSION = "1.0.0";

    private final FlowDefinitionService flowsService;

    private final FlowRuntimeService flowRuntimeService;

    private final FlowsGraphRepo flowsGraphRepo;

    private final Authenticator authenticator;

    private final TagService tagService;

    private final QueryFlowContextPersistRepo queryFlowContextPersistRepo;

    public FlowsEngineWebService(FlowRuntimeService flowRuntimeService, FlowDefinitionService flowsService,
                                 FlowsGraphRepo flowsGraphRepo, Authenticator authenticator, TagService tagService,
                                 QueryFlowContextPersistRepo queryFlowContextPersistRepo) {
        this.flowRuntimeService = flowRuntimeService;
        this.flowsService = flowsService;
        this.flowsGraphRepo = flowsGraphRepo;
        this.authenticator = authenticator;
        this.tagService = tagService;
        this.queryFlowContextPersistRepo = queryFlowContextPersistRepo;
    }

    @Override
    @Transactional
    public FlowInfo createFlows(FlowSaveEntity flowSaveEntity, OperationContext context) {
        flowSaveEntity.setStatus(UNPUBLISHED_STATUS);
        return saveFlow(flowSaveEntity, context);
    }

    @Override
    @Transactional
    public FlowInfo publishFlows(FlowSaveEntity flowSaveEntity, OperationContext context) {
        flowSaveEntity.setStatus(PUBLISHED_STATUS);
        if (flowSaveEntity.getDefinitionData() == null) {
            flowSaveEntity.setDefinitionData(convertToFlowDefinition(flowSaveEntity));
        }
        FlowDefinitionResult flows = flowsService.createFlows(flowSaveEntity.getDefinitionData(),
                convertOperationContext(context));
        FlowInfo flowInfo = saveFlow(flowSaveEntity, context);
        return publishFlowsHandle(flowInfo, flows);
    }

    @Override
    public FlowInfo publishFlowsWithoutElsa(FlowSaveEntity flowSaveEntity, OperationContext context) {
        FlowInfo flowInfo = this.buildFlowInfo(flowSaveEntity);
        FlowDefinitionResult flows = flowsService.createFlows(flowSaveEntity.getDefinitionData(),
                convertOperationContext(context));
        return publishFlowsHandle(flowInfo, flows);
    }

    private FlowInfo publishFlowsHandle(FlowInfo flowInfo, FlowDefinitionResult flows) {
        flowInfo.setFlowDefinitionId(flows.getFlowDefinitionId());
        flowInfo.setFlowNodes(flows.getFlowNodes());
        return flowInfo;
    }

    @Override
    public FlowInfo updateFlows(FlowSaveEntity flowSaveEntity, OperationContext context) {
        return createFlows(flowSaveEntity, context);
    }

    /**
     * 升级流程
     *
     * @param flowSaveEntity 流程保存实体
     * @param context 操作上下文
     * @return 流程信息
     */
    public FlowInfo upgradeFlows(FlowSaveEntity flowSaveEntity, OperationContext context) {
        GraphParam graphParam = new GraphParam();
        graphParam.setGraphId(flowSaveEntity.getId());
        graphParam.setVersion(flowSaveEntity.getVersion());
        graphParam.setJson(flowSaveEntity.getGraphData());
        int ans = flowsGraphRepo.upgradeFlows(graphParam);
        if (ans != 0) {
            String errMsg = String.format(Locale.ROOT, "upgrade flows failed. ans=%s graphParam=%s", ans,
                    JSON.toJSONString(graphParam));
            log.error(errMsg);
            throw new JobberException(UN_EXCEPTED_ERROR, errMsg);
        }
        return this.buildFlowInfo(flowSaveEntity);
    }

    private FlowInfo buildFlowInfo(FlowSaveEntity flowSaveEntity) {
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowId(flowSaveEntity.getId());
        flowInfo.setVersion(flowSaveEntity.getVersion());
        return flowInfo;
    }

    @Override
    public FlowInfo getFlows(String flowId, String version, OperationContext context) {
        FlowSaveEntity flowSaveEntity = new FlowSaveEntity();
        flowSaveEntity.setId(flowId);
        flowSaveEntity.setVersion(version);
        String flowGraphData = flowsGraphRepo.getFlow(flowSaveEntity, context);
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowNodes(flowsService.getFlowDefinitionByGraphData(
                        FlowDefinitionParseUtils.getParsedGraphData(JSONObject.parseObject(flowGraphData), version)).getFlowNodes());
        flowInfo.setConfigData(flowGraphData);
        flowInfo.setFlowId(flowId);
        flowInfo.setVersion(version);
        return flowInfo;
    }

    @Override
    @Transactional
    public int forceDeleteFlows(String flowId, String version, OperationContext context) {
        int ans = deleteFlowGraph(flowId, version, context);
        flowsService.forceDeleteFlows(flowId, version, convertOperationContext(context));
        return ans;
    }

    private int deleteFlowGraph(String flowId, String version, OperationContext context) {
        FlowSaveEntity flowSaveEntity = new FlowSaveEntity();
        flowSaveEntity.setId(flowId);
        flowSaveEntity.setVersion(version);
        int ans = flowsGraphRepo.deleteFlow(flowSaveEntity, context);
        if (ans != 0) {
            log.error("delete flows failed.");
            throw new JobberException(UN_EXCEPTED_ERROR, "deleteFlows");
        }
        return ans;
    }

    @Override
    @Transactional
    public int deleteFlows(String flowId, String version, OperationContext context) {
        Integer flowContextNum = queryFlowContextPersistRepo.findUnarchivedContextCountByMetaId(flowId, version);
        if (flowContextNum != 0) {
            log.warn("The flow definition has related running flow, cannot be deleted");
            throw new JobberException(FLOW_DEFINITION_DELETE_ERROR);
        }
        int ans = deleteFlowGraph(flowId, version, context);
        flowsService.deleteFlows(flowId, version, convertOperationContext(context));
        return ans;
    }

    @Override
    public void deleteFlowsWithoutElsa(String flowId, String version, OperationContext context) {
        flowsService.deleteFlows(flowId, version, convertOperationContext(context));
    }

    @Override
    public List<FlowGraphDefinition> findFlowsByFlowId(String flowId) {
        throw new JobberException(NOT_SUPPORT, "findFlowsByFlowId");
    }

    @Override
    public GetPageResponse findFlowDefinitionsPage(String data, String tenantId) {
        throw new JobberException(NOT_SUPPORT, "findFlowDefinitionsPage");
    }

    @Override
    public RangedResultSet<FlowGraphDefinition> findFlowList(String createdBy, List<String> tags, int offset, int limit,
            OperationContext context) {
        FlowGraphQueryParam queryParam = new FlowGraphQueryParam();
        queryParam.setCreatUser(createdBy);
        queryParam.setLimit(limit);
        queryParam.setOffset(offset);
        RangedResultSet<FlowGraphDefinition> flowList = RangedResultSet.create(new ArrayList<>(), offset, limit, 0);
        if (tags.isEmpty()) {
            flowList = flowsGraphRepo.getFlowList(queryParam, context);
        } else {
            List<String> flowIds = tagService.list(FLOW_GRAPH, tags);
            if (!flowIds.isEmpty()) {
                queryParam.setFlowIds(flowIds);
                flowList = flowsGraphRepo.getFlowList(queryParam, context);
            }
        }
        List<FlowGraphDefinition> resList = flowList.getResults();
        List<String> allIds = resList.stream().map(FlowGraphDefinition::getFlowId).collect(Collectors.toList());
        Map<String, List<String>> tagMap = tagService.list(FLOW_GRAPH, allIds, null);
        resList.forEach(
                flowGraphDefinition -> flowGraphDefinition.setTags(tagMap.get(flowGraphDefinition.getFlowId())));
        return RangedResultSet.create(resList, offset, limit, flowList.getRange().getTotal());
    }

    private FlowInfo saveFlow(FlowSaveEntity flowSaveEntity, OperationContext context) {
        int ans = flowsGraphRepo.saveFlow(flowSaveEntity, context);
        if (ans != 0) {
            log.error("Create flows failed, flow id: {}, version: {}.",
                    flowSaveEntity.getId(), flowSaveEntity.getVersion());
            throw new JobberException(FLOW_GRAPH_SAVE_ERROR, flowSaveEntity.getId(), flowSaveEntity.getVersion());
        }
        return this.buildFlowInfo(flowSaveEntity);
    }

    private String convertToFlowDefinition(FlowSaveEntity flowSaveEntity) {
        String formatData;
        try {
            JSONObject parsedData = JSONObject.parseObject(flowSaveEntity.getGraphData());
            formatData = FlowDefinitionParseUtils.getParsedGraphData(parsedData, flowSaveEntity.getVersion());
        } catch (JSONException | FitException | NullPointerException e) {
            log.error("parse graph data failed.");
            throw new JobberException(FLOW_GRAPH_DATA_PARSE_FAILED);
        }
        return formatData;
    }

    private String getCookie(HttpClassicServerRequest request) {
        return request.headers().first("cookie").orElse(StringUtils.EMPTY);
    }

    private modelengine.fit.jane.task.util.OperationContext contextOf(HttpClassicServerRequest request,
            String tenantId) {
        return ControllerUtil.contextOf(request, tenantId, this.authenticator);
    }

    @Override
    public FlowInfo getFlowDefinitionById(String definitionId, OperationContext context) {
        FlowDefinitionResult flowDefinition = flowsService.findFlowsById(definitionId, convertOperationContext(context));
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowDefinitionId(definitionId);
        flowInfo.setFlowId(flowDefinition.getMetaId());
        flowInfo.setVersion(flowDefinition.getVersion());
        flowInfo.setFlowNodes(flowDefinition.getFlowNodes());
        return flowInfo;
    }

    public static modelengine.fit.waterflow.entity.OperationContext convertOperationContext(OperationContext context) {
        return modelengine.fit.waterflow.entity.OperationContext.custom()
                .tenantId(context.getTenantId())
                .operator(context.getOperator())
                .operatorIp(context.getOperatorIp())
                .sourcePlatform(context.getSourcePlatform())
                .langage(context.getLanguage())
                .build();
    }
}
