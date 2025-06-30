/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


package modelengine.fit.waterflow.appfactory.fitable;

import static modelengine.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;

import modelengine.fit.jade.waterflow.AippFlowDefinitionService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.common.exceptions.JobberParamException;
import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;
import modelengine.fit.waterflow.graph.util.FlowDefinitionParseUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Objects;

/**
 * {@link FlowDefinitionService}实现类
 *
 * @author 杨祥宇
 * @since 2023/12/12
 */
@Component
public class AippFlowDefinitionServiceImpl implements AippFlowDefinitionService {
    private final FlowDefinitionService flowsService;

    public AippFlowDefinitionServiceImpl(FlowDefinitionService flowsService) {
        this.flowsService = flowsService;
    }

    @Override
    @Fitable("c35226173ac9411886b239ce097b6eaa")
    public FlowDefinitionResult createFlowDefinition(String definitionData, OperationContext context) {
        Validation.notBlank(definitionData, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "definitionData"));
        return flowsService.createFlows(definitionData, convertOperationContext(context));
    }

    @Override
    @Fitable("cee425da76cd4cc99040ed10a2c91d8f")
    public FlowDefinitionResult updateFlowDefinition(String flowDefinitionId, String definitionData,
            OperationContext context) {
        Validation.notBlank(flowDefinitionId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowDefinitionId"));
        Validation.notBlank(definitionData, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "definitionData"));
        return flowsService.updateFlows(flowDefinitionId, definitionData, convertOperationContext(context));
    }

    @Override
    @Fitable("cee425da76cd4cc99040ed10a2c91d80")
    public List<FlowDefinitionResult> getFlowDefinitionByMetaIdAndPartVersion(String metaId, String version,
            OperationContext context) {
        return this.flowsService.findFlowsByMetaIdAndPartVersion(metaId, version);
    }

    @Override
    @Fitable("cee425da76cd4cc99040ed10a2c91d81")
    public String getParsedGraphData(String flowViewData, String version) {
        return FlowDefinitionParseUtils.getParsedGraphData(JSONObject.parseObject(flowViewData), version);
    }

    private FlowDefinitionResult buildFlowDefinitionResult(FlowDefinitionPO po) {
        FlowDefinitionResult result = new FlowDefinitionResult(po.getDefinitionId());
        result.setMetaId(po.getMetaId());
        result.setName(po.getName());
        result.setTenantId(po.getTenant());
        result.setVersion(po.getVersion());
        result.setStatus(po.getStatus());
        result.setGraph(po.getGraph());
        return result;
    }

    @Override
    @Fitable("1f7b0c6d61ddbb3ec78ee51112386a44")
    public void deleteFlows(String flowId, OperationContext context) {
        flowsService.deleteFlows(flowId, convertOperationContext(context));
    }

    @Override
    @Fitable("2b2f0128e632508a2d9fd1783c54e1ac")
    public void deleteFlows(String metaId, String version, OperationContext context) {
        flowsService.deleteFlows(metaId, version, convertOperationContext(context));
    }

    public static modelengine.fit.waterflow.entity.OperationContext convertOperationContext(OperationContext context) {
        if (Objects.isNull(context)) {
            return modelengine.fit.waterflow.entity.OperationContext.custom().build();
        }
        return modelengine.fit.waterflow.entity.OperationContext.custom()
                .tenantId(context.getTenantId())
                .operator(context.getOperator())
                .operatorIp(context.getOperatorIp())
                .sourcePlatform(context.getSourcePlatform())
                .langage(context.getLanguage())
                .build();
    }
}
