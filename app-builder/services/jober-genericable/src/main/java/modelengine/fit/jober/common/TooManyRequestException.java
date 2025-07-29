/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common;

import modelengine.fitframework.exception.ErrorCode;

/**
 * 请求超出限制异常。
 *
 * @author 陈镕希
 * @since 2023-07-06
 */
@ErrorCode(429)
public class TooManyRequestException extends JoberGenericableException {
    /**
     * 使用异常信息初始化 {@link TooManyRequestException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public TooManyRequestException(String message) {
        super(message);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link TooManyRequestException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public TooManyRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
