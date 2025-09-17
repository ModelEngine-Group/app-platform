/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.common.resp;

import modelengine.jade.app.engine.base.common.ErrorCode;

import lombok.AllArgsConstructor;

/**
 * 基础应用错误码。
 *
 * @author 陈潇文
 * @since 2024-05-27
 */
@AllArgsConstructor
public enum ResponseCode implements ErrorCode {
    /**
     *
     */
    OK(0, "success"),

    /**
     * 参数不合法
     */
    INPUT_PARAM_IS_INVALID(70000000, "Invalid input param"),

    /**
     * 资源不存在
     */
    NOT_FOUND(70000001, "Cannot find entity"),

    /**
     * 未知服务器内部错误
     */
    UNKNOWN(70000002, "Server internal error");

    private final Integer code;

    private final String msg;

    @Override
    public int getErrorCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
