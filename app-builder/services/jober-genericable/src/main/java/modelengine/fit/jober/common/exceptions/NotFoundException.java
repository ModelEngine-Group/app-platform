/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.exceptions;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.entity.OperationContext;

/**
 * 找不到资源异常类。
 *
 * @author 陈镕希
 * @since 2023-08-08
 */
public class NotFoundException extends JobberException {
    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     */
    public NotFoundException(ErrorCodes error) {
        super(error);
    }

    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context 请求头信息
     * @param args 额外参数。
     */
    public NotFoundException(ErrorCodes error, OperationContext context, Object... args) {
        super(error, context, args);
    }

    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context 请求头信息
     */
    public NotFoundException(ErrorCodes error, OperationContext context) {
        super(error, context);
    }

    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param args 额外参数。
     */
    public NotFoundException(ErrorCodes error, Object... args) {
        super(error, args);
    }
}
