/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common.exception;

import modelengine.fit.jane.common.entity.OperationContext;

/**
 * aipp禁止操作异常
 *
 * @author 刘信宏
 * @since 2024-01-31
 */
public class AippForbiddenException extends AippException {
    /**
     * 抛出禁止操作异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param context 请求头信息
     */
    public AippForbiddenException(OperationContext context, AippErrCode error) {
        super(context, error);
    }

    /**
     * 抛出禁止操作异常。
     *
     * @param context 请求头信息
     */
    public AippForbiddenException(OperationContext context) {
        super(context, AippErrCode.FORBIDDEN);
    }

    /**
     * 抛出禁止操作异常。
     */
    public AippForbiddenException() {
        super(AippErrCode.FORBIDDEN);
    }

    /**
     * 抛出禁止操作异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     */
    public AippForbiddenException(AippErrCode error) {
        super(error);
    }
}
