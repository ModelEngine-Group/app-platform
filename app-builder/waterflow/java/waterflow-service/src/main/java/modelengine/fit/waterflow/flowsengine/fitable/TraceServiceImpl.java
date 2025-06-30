/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.fitable;

import static modelengine.fit.waterflow.spi.FlowPublishService.PUBLISH_NODE_INFO_GENERICABLE;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.waterflow.ErrorCodes;
import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;
import modelengine.fit.waterflow.entity.OperationContext;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.entity.FlowNodePublishInfo;
import modelengine.fit.waterflow.entity.FlowPublishContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.spi.FlowCallbackService;
import modelengine.fit.waterflow.spi.FlowExceptionService;
import modelengine.fit.waterflow.spi.FlowPublishService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程实例回调关于流程信息推送的实现。
 *
 * @author 陈镕希
 * @since 2024-05-24
 */
@Component
public class TraceServiceImpl implements FlowCallbackService, FlowExceptionService {
    private final BrokerClient brokerClient;

    private final FlowDefinitionService flowsService;

    /**
     * 流程实例回调构造方法
     *
     * @param brokerClient 框架调度的 {@link BrokerClient}
     * @param flowsService 流程定义相关服务的 {@link FlowDefinitionService}。
     */
    public TraceServiceImpl(BrokerClient brokerClient, FlowDefinitionService flowsService) {
        this.brokerClient = brokerClient;
        this.flowsService = flowsService;
    }

    private static FlowNodePublishInfo constructFlowNodePublishInfo(Map<String, Object> context, String nodeId,
                                                                    FlowErrorInfo errorMessage, String status) {
        Map<String, Object> businessData = getValueOfSpecifyKey(context, Constant.BUSINESS_DATA_KEY);
        Map<String, Object> contextData = getValueOfSpecifyKey(context, Constant.CONTEXT_DATA);
        Set<String> traces = cast(context.get(Constant.TRACE_ID_KEY));
        FlowPublishContext flowContext = new FlowPublishContext(cast(traces.toArray()[0]), status,
                cast(contextData.get(Constant.NODE_STAGE_KEY)), cast(context.get("createAt")),
                cast(context.get("updateAt")), cast(context.get("archivedAt")));
        String flowDefinitionId = cast(contextData.get("flowDefinitionId"));
        FlowNodePublishInfo flowNodePublishInfo = new FlowNodePublishInfo();
        flowNodePublishInfo.setFlowDefinitionId(flowDefinitionId);
        flowNodePublishInfo.setNodeId(nodeId);
        flowNodePublishInfo.setNodeType(cast(contextData.get("nodeType")));
        flowNodePublishInfo.setNodeProperties(cast(contextData.get(Constant.NODE_PROPERTIES_KEY)));
        flowNodePublishInfo.setBusinessData(businessData);
        flowNodePublishInfo.setFlowContext(flowContext);
        flowNodePublishInfo.setErrorMsg(errorMessage);
        flowNodePublishInfo.setNextPositionId(cast(context.get("nextPositionId")));
        return flowNodePublishInfo;
    }

    private static <T> T getValueOfSpecifyKey(Map<String, Object> map, String specifyKey) {
        if (!map.containsKey(specifyKey)) {
            throw new WaterflowException(ErrorCodes.INPUT_PARAM_IS_EMPTY, specifyKey);
        }
        return cast(map.get(specifyKey));
    }

    @Fitable("modelengine.fit.jober.fitable.FlowInfoCallback")
    @Override
    public void callback(List<Map<String, Object>> contexts) {
        if (contexts.isEmpty()) {
            throw new WaterflowException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "contexts");
        }
        contexts.forEach(context -> {
            String nodeId = getValueOfSpecifyKey(context, Constant.NODE_ID_KEY);
            FlowNodePublishInfo flowNodePublishInfo = constructFlowNodePublishInfo(context, nodeId, new FlowErrorInfo(),
                    FlowNodeStatus.ARCHIVED.name());
            publishNodeInfo(flowNodePublishInfo);
        });
    }

    @Fitable("modelengine.fit.jober.fitable.FlowInfoException")
    @Override
    public void handleException(String nodeId, List<Map<String, Object>> contexts, FlowErrorInfo errorMessage) {
        if (contexts.isEmpty()) {
            throw new WaterflowException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "contexts");
        }
        contexts.forEach(context -> {
            // 由于notify之后才统一改的context的status为ERROR，所以这里需要手动设置一下
            context.put("status", FlowNodeStatus.ERROR.name());
            FlowNodePublishInfo flowNodePublishInfo = constructFlowNodePublishInfo(context, nodeId, errorMessage,
                    cast(context.get("status")));
            publishNodeInfo(flowNodePublishInfo);
        });
    }

    /**
     * 发布节点信息.
     *
     * @param flowNodePublishInfo 流程节点信息对象.
     */
    public void publishNodeInfo(FlowNodePublishInfo flowNodePublishInfo) {
        String flowDefinitionId = flowNodePublishInfo.getFlowDefinitionId();
        FlowDefinitionResult flowDefinition = flowsService.findFlowsById(flowDefinitionId, OperationContext.custom().build());
        for (String fitableId : flowDefinition.getPublishNodeFitables()) {
            this.brokerClient.getRouter(FlowPublishService.class, PUBLISH_NODE_INFO_GENERICABLE)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(flowNodePublishInfo);
        }
    }
}
