/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.constants.AippConst;

import lombok.Getter;

import java.util.Arrays;

/**
 * 表单端点
 *
 * @author 刘信宏
 * @since 2023/12/15
 */
@Getter
public enum FormEdgeEnum {
    START(AippConst.ATTR_START_FORM_ID_KEY, AippConst.ATTR_START_FORM_VERSION_KEY),
    END(AippConst.ATTR_END_FORM_ID_KEY, AippConst.ATTR_END_FORM_VERSION_KEY);

    private final String formIdKey;
    private final String versionKey;

    FormEdgeEnum(String formIdKey, String versionKey) {
        this.formIdKey = formIdKey;
        this.versionKey = versionKey;
    }

    /**
     * 根据输入的字符串获取对应的枚举值
     *
     * @param edge 输入的字符串
     * @return 对应的枚举值
     * @throws AippParamException 当输入的字符串不能匹配到任何枚举值时，抛出此异常
     */
    public static FormEdgeEnum getFormEdge(String edge) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(edge))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, edge));
    }
}
