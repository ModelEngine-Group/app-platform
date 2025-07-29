/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;

import java.util.Arrays;

/**
 * 实例状态枚举
 *
 * @author 刘信宏
 * @since 2023-12-15
 */
public enum MetaInstStatusEnum {
    /**
     * 未执行
     */
    READY(0),
    /**
     * 运行中
     */
    RUNNING(1),

    /**
     * 已成功
     */
    ARCHIVED(2),

    /**
     * 执行失败
     */
    ERROR(3),

    /**
     * 已终止
     */
    TERMINATED(4);

    private final int status;

    MetaInstStatusEnum(int status) {
        this.status = status;
    }

    /**
     * 返回枚举值
     *
     * @return 枚举值
     */
    public short getValue() {
        return (short) status;
    }

    /**
     * 根据状态名获取枚举值
     *
     * @param status 状态名
     * @return 枚举值
     * @throws AippParamException 当输入的状态名无法匹配到任何枚举值时，抛出此
     */
    public static MetaInstStatusEnum getMetaInstStatus(String status) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, status));
    }
}
