/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_GENERICALBE_JOBER_INVOKE_ERROR;
import static modelengine.fitframework.util.ObjectUtils.cast;

import com.alibaba.fastjson.JSON;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支持genericable调用的任务
 *
 * @author 宋永坦
 * @since 2024/4/22
 */
public class FlowGenericableJober extends FlowJober {
    private static final Logger LOG = Logger.get(FlowGenericableJober.class);

    @Setter
    @Getter
    private GenericableConfig genericableConfig;

    @Getter
    private String fitableId;

    /**
     * 读取fitableId信息, 该jober只支持调用一个fitable
     */
    public void loadFitableId() {
        this.fitableId = fitables.stream().findFirst().get();
    }

    @Override
    protected List<FlowData> executeJober(List<FlowData> inputs) {
        List<Map<String, Object>> inputData = getInputs(inputs);
        FlowData contextInfo = inputs.get(0);
        List<FlowData> result = new ArrayList<>(inputs.size());
        inputData.forEach(input -> {
            Object invokeResult;
            try {
                    invokeResult = this.brokerClient.getRouter(genericableConfig.id)
                        .route(new FitableIdFilter(this.fitableId))
                        .communicationType(CommunicationType.ASYNC)
                        .invoke(getArgs(input).toArray());
            } catch (FitException ex) {
                LOG.error("Genericable jober invoker error, fitable id: {}.", getFitableId(ex));
                LOG.error("Exception", ex);
                throw new WaterflowException(ex, FLOW_GENERICALBE_JOBER_INVOKE_ERROR);
            }
            LOG.info("Call fitable successfully, nodeId={}, fitable={}:{}.", this.nodeMetaId, genericableConfig.id,
                    fitableId);
            // 这里在本地调用的情况下返回的Object可能包含自定义的class类型，这里先进行序列化和反序列化达到我们想要的类型
            // 后续引擎会进行统一处理，待引擎能力完善后这里再删除（减少消耗）
            result.add(addResultToFlowData(JSON.parse(JSON.toJSONString(invokeResult)),
                    cast(input.get(Constant.BUSINESS_DATA_KEY)), contextInfo));
        });

        return result;
    }

    private List<Object> getArgs(Map<String, Object> input) {
        Map<String, Object> businessData = cast(input.get(Constant.BUSINESS_DATA_KEY));
        return genericableConfig.params.stream().map(businessData::get).collect(Collectors.toList());
    }

    /**
     * genericable的配置信息
     *
     * @author 宋永坦
     * @since 2024/4/22
     */
    @Builder
    @Getter
    public static class GenericableConfig {
        private String id;

        private List<String> params;
    }
}
