/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.enums;

import lombok.Getter;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;

import java.util.Arrays;

import static modelengine.fit.waterflow.ErrorCodes.ENUM_CONVERT_FAILED;

/**
 * 流程定义对应状态
 *
 * @author 杨祥宇
 * @since 2023/9/25
 */
@Getter
public enum FlowDefinitionStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String code;

    FlowDefinitionStatus(String code) {
        this.code = code;
    }

    /**
     * getFlowDefinitionStatus
     *
     * @param code code
     * @return FlowDefinitionStatus
     */
    public static FlowDefinitionStatus getFlowDefinitionStatus(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowDefinitionStatus", code));
    }
}
