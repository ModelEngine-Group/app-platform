/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;

import lombok.Getter;

import java.util.Arrays;

/**
 * aipp meta对应状态
 *
 * @author 刘信宏
 * @since 2023/12/15
 */
@Getter
public enum AippMetaStatusEnum {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String code;

    AippMetaStatusEnum(String code) {
        this.code = code;
    }

    /**
     * 根据code获取对应的AippMetaStatusEnum枚举值
     *
     * @param code code
     * @return AippMetaStatusEnum
     */
    public static AippMetaStatusEnum getAippMetaStatus(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, code));
    }
}