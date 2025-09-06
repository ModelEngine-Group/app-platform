/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.common.enums;

import static modelengine.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;

import lombok.Getter;
import modelengine.fit.jober.common.exceptions.JobberParamException;

import java.util.Arrays;

/**
 * 排序方向
 *
 * @author 刘信宏
 * @since 2023-12-15
 */
@Getter
public enum DirectionEnum {
    DESCEND("desc"),
    ASCEND("asc");

    private final String value;

    DirectionEnum(String value) {
        this.value = value;
    }

    /**
     * getDirection
     *
     * @param value value
     * @return DirectionEnum
     */
    public static DirectionEnum getDirection(String value) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "DirectionEnum", value));
    }
}
