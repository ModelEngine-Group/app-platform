/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.exceptions;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.entity.OperationContext;

/**
 * 错误请求异常类。
 *
 * @author 陈镕希
 * @since 2023-08-08
 */
public class BadRequestException extends JobberException {
    /**
     * 抛出错误请求异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     */
    public BadRequestException(ErrorCodes error) {
        super(error);
    }

    /**
     * 抛出错误请求异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context 请求头信息
     * @param args 额外参数。
     */
    public BadRequestException(ErrorCodes error, OperationContext context, Object... args) {
        super(error, context, args);
    }

    /**
     * 抛出错误请求异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context 请求头信息
     */
    public BadRequestException(ErrorCodes error, OperationContext context) {
        super(error, context);
    }

    /**
     * 抛出错误请求异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param args 额外参数。
     */
    public BadRequestException(ErrorCodes error, Object... args) {
        super(error, args);
    }
}
