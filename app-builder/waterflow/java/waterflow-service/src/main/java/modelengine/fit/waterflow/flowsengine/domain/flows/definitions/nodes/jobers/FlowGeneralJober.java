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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FlowNGeneralJober
 * 流程通用接口
 *
 * @author 杨祥宇
 * @since 2023/10/09
 */
public class FlowGeneralJober extends FlowJober {
    private static final Logger log = Logger.get(FlowGeneralJober.class);

    @Override
    protected List<FlowData> executeJober(List<FlowData> inputs) {
        List<Map<String, Object>> contextData = getInputs(inputs);
        List<Map<String, Object>> outputEntities = new ArrayList<>();
        for (String fitableId : fitables) {
            try {
                outputEntities = fitableInvoke(contextData, fitableId);
            } catch (FitException ex) {
                log.error("General jober invoker error, fitable id: {}.", getFitableId(ex));
                log.error("Exception", ex);
                throw new WaterflowException(ex, ErrorCodes.FLOW_GENERAL_JOBER_INVOKE_ERROR);
            }
            log.info("Remote invoke success,nodeId: {}, fitable id is {}.", this.nodeMetaId, fitableId);
        }
        return convertToFlowData(outputEntities, inputs.get(0));
    }
}
