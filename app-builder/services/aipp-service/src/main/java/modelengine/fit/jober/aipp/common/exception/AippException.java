/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common.exception;

import lombok.Getter;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.jade.common.exception.ModelEngineException;

/**
 * Aipp 通用异常。
 *
 * @author 刘信宏
 * @since 2024-01-31
 */
public class AippException extends ModelEngineException {
    @Getter
    private OperationContext context;

    /**
     * 使用操作上下文和错误码初始化 {@link AippException}。
     *
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @param error 表示异常枚举的 {@link AippErrCode}。
     */
    public AippException(OperationContext context, AippErrCode error) {
        super(error);
        this.context = context;
    }

    /**
     * 使用错误码初始化 {@link AippException}。
     *
     * @param error 表示异常枚举的 {@link AippErrCode}。
     */
    public AippException(AippErrCode error) {
        super(error);
    }

    /**
     * 使用操作上下文、错误码和参数列表初始化 {@link AippException}。
     *
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @param error 表示异常枚举的 {@link AippErrCode}。
     * @param args 表示参数列表的 {@link Object}{@code []}。
     */
    public AippException(OperationContext context, AippErrCode error, Object... args) {
        super(error, args);
        this.context = context;
    }

    /**
     * 使用错误码和参数列表初始化 {@link AippException}。
     *
     * @param error 表示异常枚举的 {@link AippErrCode}。
     * @param args 表示参数列表的 {@link Object}{@code []}。
     */
    public AippException(AippErrCode error, Object... args) {
        super(error, args);
    }
}
