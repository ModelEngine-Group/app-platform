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
 * 并行节点的操作类型
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
public enum ParallelMode {
    ALL("all"),
    EITHER("either"),
    ;

    private final String code;

    ParallelMode(String code) {
        this.code = code;
    }

    /**
     * parseFrom
     *
     * @param code code
     * @return ParallelMode
     */
    public static ParallelMode parseFrom(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "ParallelMode", code));
    }
}
