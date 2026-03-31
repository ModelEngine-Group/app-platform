/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.utils.FlowUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * FlowData 类型数据的多输入合并器
 * 用于 fan-in 场景下将多条 FlowData 输入合并为单条处理
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public class FlowDataMerger implements Processors.Merger<FlowData> {

    @Override
    public FlowContext<FlowData> merge(List<FlowContext<FlowData>> contexts) {
        if (contexts == null || contexts.isEmpty()) {
            return null;
        }
        FlowContext<FlowData> baseContext = contexts.get(0);
        FlowData mergedFlowData = mergeFlowData(contexts);
        return baseContext.convertData(mergedFlowData, baseContext.getId());
    }

    private FlowData mergeFlowData(List<FlowContext<FlowData>> contexts) {
        FlowData first = contexts.get(0).getData();
        Map<String, Object> businessData = new HashMap<>(
                Optional.ofNullable(first.getBusinessData()).orElseGet(HashMap::new));
        Map<String, Object> contextData = new HashMap<>(
                Optional.ofNullable(first.getContextData()).orElseGet(HashMap::new));
        Map<String, Object> passData = new HashMap<>(
                Optional.ofNullable(first.getPassData()).orElseGet(HashMap::new));

        contexts.stream().skip(1).map(FlowContext::getData).forEach(flowData -> {
            businessData.putAll(FlowUtil.mergeMaps(businessData,
                    Optional.ofNullable(flowData.getBusinessData()).orElseGet(HashMap::new)));
            contextData.putAll(FlowUtil.mergeMaps(contextData,
                    Optional.ofNullable(flowData.getContextData()).orElseGet(HashMap::new)));
            passData.putAll(FlowUtil.mergeMaps(passData,
                    Optional.ofNullable(flowData.getPassData()).orElseGet(HashMap::new)));
        });
        return FlowData.builder()
                .operator(first.getOperator())
                .startTime(first.getStartTime())
                .businessData(businessData)
                .contextData(contextData)
                .passData(passData)
                .errorMessage(first.getErrorMessage())
                .errorInfo(first.getErrorInfo())
                .build();
    }
}
