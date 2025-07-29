/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.flow.graph.repo;

import static modelengine.fit.jober.common.ErrorCodes.ENTITY_NOT_FOUND;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.jober.common.exceptions.JobberParamException;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import modelengine.fit.jane.flow.graph.entity.FlowGraphDefinition;
import modelengine.fit.jane.flow.graph.entity.FlowGraphQueryParam;
import modelengine.fit.jane.flow.graph.entity.FlowSaveEntity;
import modelengine.fit.jane.flow.graph.entity.elsa.GraphParam;
import modelengine.fit.jane.flow.graph.entity.elsa.response.GetPageResponse;
import modelengine.fit.jane.flow.graph.entity.elsa.response.SaveFlowsResponse;
import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.transaction.DataAccessException;

import org.apache.ibatis.exceptions.PersistenceException;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 调用流程引擎flowGraph repo
 *
 * @author 杨祥宇
 * @since 2023/12/13
 */
@Alias("DbFlowGraphRepo")
@Component
public class DbFlowsGraphRepo implements FlowsGraphRepo {
    private static final Logger log = Logger.get(DbFlowsGraphRepo.class);

    private static final String DEFAULT_FLOW_GRAPH_VERSION = "1.0.0";

    private final FlowGraphRepo flowGraphRepo;

    public DbFlowsGraphRepo(FlowGraphRepo flowGraphRepo) {
        this.flowGraphRepo = flowGraphRepo;
    }

    @Override
    public GetPageResponse getPages(String user, String cookie, String graphData) {
        throw new JobberException(ErrorCodes.NOT_SUPPORT, "getPages");
    }

    @Override
    public SaveFlowsResponse saveFlows(String user, String cookie, String graphData) {
        throw new JobberException(ErrorCodes.NOT_SUPPORT, "saveFlows");
    }

    @Override
    public int saveFlow(FlowSaveEntity flowSaveEntity, OperationContext context) {
        FlowGraphDefinition graphDefinition = null;
        try {
            graphDefinition = this.covert(flowSaveEntity, context);
        } catch (JSONException | NullPointerException e) {
            log.error("Convert flowSaveEntity failed. [json={}]", flowSaveEntity.getGraphData(), e);
            return -1;
        }
        try {
            this.flowGraphRepo.save(graphDefinition);
        } catch (DataAccessException | PersistenceException e) {
            log.error("Save flow graph in table failed. [json={}]", flowSaveEntity.getGraphData(), e);
            return -1;
        }
        return 0;
    }

    @Override
    public int upgradeFlows(GraphParam param) {
        throw new UnsupportedOperationException("upgradeFlows not support");
    }

    @Override
    public String getFlow(FlowSaveEntity flowSaveEntity, OperationContext context) {
        FlowGraphDefinition flowGraphDefinition = this.flowGraphRepo.findFlowByFlowIdAndVersion(flowSaveEntity.getId(),
                flowSaveEntity.getVersion());
        if (flowGraphDefinition == null) {
            log.error("Can not find flow graph in table with id {0}, version {1}", flowSaveEntity.getId(),
                    flowSaveEntity.getVersion());
            throw new JobberParamException(ENTITY_NOT_FOUND, "flowGraph",
                    flowSaveEntity.getId() + "/" + flowSaveEntity.getVersion());
        }
        return flowGraphDefinition.getGraphData();
    }

    @Override
    public int deleteFlow(FlowSaveEntity flowSaveEntity, OperationContext context) {
        try {
            this.flowGraphRepo.delete(flowSaveEntity.getId(), flowSaveEntity.getVersion());
        } catch (JobberException | DataAccessException | PersistenceException e) {
            log.error("Delete document in table failed. [documentId={}]", flowSaveEntity.getId(), e);
            return -1;
        }
        return 0;
    }

    @Override
    public RangedResultSet<FlowGraphDefinition> getFlowList(FlowGraphQueryParam queryParam, OperationContext context) {
        return this.flowGraphRepo.getFlowList(queryParam.getFlowIds(), queryParam.getCreatUser(),
                queryParam.getOffset(), queryParam.getLimit());
    }

    private FlowGraphDefinition covert(FlowSaveEntity flowSaveEntity, OperationContext context) {
        JSONObject parsedData = JSONObject.parseObject(flowSaveEntity.getGraphData());
        String name = parsedData.getString("title");
        LocalDateTime currentTime = LocalDateTime.now();
        return FlowGraphDefinition.builder()
                .flowId(flowSaveEntity.getId())
                .version(Optional.ofNullable(flowSaveEntity.getVersion()).orElse(DEFAULT_FLOW_GRAPH_VERSION))
                .tenant(context.getTenantId())
                .status(flowSaveEntity.getStatus())
                .name(name)
                .graphData(flowSaveEntity.getGraphData())
                .createdBy(context.getOperator())
                .createdAt(currentTime)
                .updatedBy(context.getOperator())
                .updatedAt(currentTime)
                .previous(flowSaveEntity.getPrevious())
                .isDeleted(false)
                .build();
    }
}
