/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common.exception;

import modelengine.fit.jane.common.entity.OperationContext;

/**
 * json解析异常
 *
 * @author 熊以可
 * @since 2024-02-23
 */
public class AippJsonDecodeException extends AippException {
    public AippJsonDecodeException(OperationContext context, String message) {
        super(context, AippErrCode.JSON_DECODE_FAILED, message);
    }

    public AippJsonDecodeException(String message) {
        super(AippErrCode.JSON_DECODE_FAILED, message);
    }
}
