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
 * meta 实例排序条件, key名称转换
 *
 * @author 刘信宏
 * @since 2023-12-15
 */
@Getter
public enum MetaInstSortKeyEnum {
    START_TIME(AippConst.INST_CREATE_TIME_KEY),
    END_TIME(AippConst.INST_FINISH_TIME_KEY);

    private final String key;

    MetaInstSortKeyEnum(String key) {
        this.key = key;
    }

    /**
     * 根据输入的key获取对应的MetaInstSortKeyEnum枚举值
     *
     * @param key 输入的key
     * @return 对应的MetaInstSortKeyEnum枚举值
     * @throws AippParamException 当输入的key不在枚举值中时，抛出参数异常
     */
    public static MetaInstSortKeyEnum getInstSortKey(String key) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, key));
    }
}
