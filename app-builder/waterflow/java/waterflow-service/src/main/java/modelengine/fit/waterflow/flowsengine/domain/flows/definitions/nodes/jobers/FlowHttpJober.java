/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers;

import modelengine.fit.waterflow.ErrorCodes;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;

import java.util.List;
import java.util.Map;

/**
 * http的jober实现
 *
 * @author 晏钰坤
 * @since 2023/10/18
 */
public class FlowHttpJober extends FlowJober {
    private static final Logger log = Logger.get(FlowHttpJober.class);

    private static final String HTTP_JOBER_FITABLE = "93f0b03c2ff94a46af5ace0088a8ce22";

    @Override
    protected List<FlowData> executeJober(List<FlowData> inputs) {
        List<Map<String, Object>> contextData = getInputs(inputs);
        List<Map<String, Object>> outputs;
        try {
            outputs = fitableInvoke(contextData, HTTP_JOBER_FITABLE);
        } catch (FitException ex) {
            log.error("Http jober invoker error, fitable id: {}.", getFitableId(ex));
            log.error("Exception", ex);
            throw new WaterflowException(ex, ErrorCodes.FLOW_HTTP_JOBER_INVOKE_ERROR);
        }
        log.info("Remote invoke success,nodeId: {}, fitable id is {}.", this.nodeMetaId, HTTP_JOBER_FITABLE);
        return convertToFlowData(outputs, inputs.get(0));
    }
}

