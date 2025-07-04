/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.variable.updater;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * 变量更新算子服务。
 *
 * @author 鲁为
 * @since 2025-09-26
 */
@Component
public class AippVariableUpdater implements FlowableService {
    private static final String UPDATE_VARIABLES = "updateVariables";
    private static final String INTERNAL = "_internal";
    private static final String OUTPUT_SCOPE = "outputScope";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    @Override
    @Fitable("modelengine.fit.jade.aipp.variable.updater")
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = this.getBusinessData(flowData);
        List<Map<String, Object>> updateVariables = ObjectUtils.cast(businessData.get(UPDATE_VARIABLES));
        Map<String, Object> internal = ObjectUtils.cast(businessData.get(INTERNAL));
        Map<String, Object> outputScope = ObjectUtils.cast(internal.get(OUTPUT_SCOPE));
        for (Map<String, Object> variable : updateVariables) {
            List<String> path = ObjectUtils.cast(variable.get(KEY));
            Object newValue = variable.get(VALUE);
            this.updateNestedMapByPath(outputScope, path, newValue);
        }
        return flowData;
    }

    private Map<String, Object> getBusinessData(List<Map<String, Object>> flowData) {
        if (flowData.isEmpty() || !flowData.get(0).containsKey(AippConst.BS_DATA_KEY)) {
            throw new JobberException(ErrorCodes.INPUT_PARAM_IS_EMPTY, AippConst.BS_DATA_KEY);
        }
        return ObjectUtils.cast(flowData.get(0).get(AippConst.BS_DATA_KEY));
    }

    private void updateNestedMapByPath(Map<String, Object> businessData, List<String> path, Object newValue) {
        if (businessData == null || path == null || path.isEmpty()) {
            return;
        }

        Map<String, Object> currentMap = businessData;

        for (int i = 0; i < path.size() - 1; i++) {
            String key = path.get(i);
            Object value = currentMap.get(key);

            if (value instanceof Map) {
                currentMap = ObjectUtils.cast(value);
            } else {
                throw new AippParamException(AippErrCode.DATA_TYPE_IS_NOT_SUPPORTED);
            }
        }

        String finalKey = path.get(path.size() - 1);
        currentMap.put(finalKey, newValue);
    }
}