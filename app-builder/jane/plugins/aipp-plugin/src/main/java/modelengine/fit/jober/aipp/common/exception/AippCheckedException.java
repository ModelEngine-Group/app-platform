/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common.exception;

import modelengine.fit.jane.common.entity.OperationContext;

import lombok.Getter;

/**
 * aipp通用受检异常
 *
 * @author 余坤
 * @since 2024-01-31
 */
@Getter
public class AippCheckedException extends Exception {
    private OperationContext context;

    private modelengine.fit.jober.aipp.common.exception.AippErrCode error;

    /**
     * 抛出Aipp异常。
     *
     * @param context context
     * @param error 异常枚举的{@link modelengine.fit.jober.aipp.common.exception.AippErrCode}。
     */
    public AippCheckedException(OperationContext context,
            modelengine.fit.jober.aipp.common.exception.AippErrCode error) {
        this.context = context;
        this.error = error;
    }

    /**
     * 抛出Aipp异常。
     *
     * @param error 异常枚举的{@link modelengine.fit.jober.aipp.common.exception.AippErrCode}。
     */
    public AippCheckedException(AippErrCode error) {
        this(null, error);
    }
}
