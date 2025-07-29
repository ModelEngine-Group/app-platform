/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common.exception;

import modelengine.fit.jane.common.entity.OperationContext;

/**
 * aipp参数异常
 *
 * @author 刘信宏
 * @since 2024-01-31
 */
public class AippParamException extends AippException {
    /**
     * 抛出Aipp参数错误异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     */
    public AippParamException(AippErrCode error) {
        super(error);
    }

    /**
     * 抛出Aipp参数错误异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param context 请求头信息
     * @param paramName 参数名称。
     */
    public AippParamException(OperationContext context, AippErrCode error, String paramName) {
        super(context, error, paramName);
    }

    /**
     * 抛出无入参版本的Aipp参数错误异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param context 请求头信息
     */
    public AippParamException(OperationContext context, AippErrCode error) {
        super(context, error);
    }

    /**
     * 抛出Aipp参数错误异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param paramName 参数名称。
     */
    public AippParamException(AippErrCode error, String paramName) {
        super(error, paramName);
    }
}
