/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common;

/**
 * 类型不支持异常
 *
 * @author 杨祥宇
 * @since 2024/2/2
 */
public class TypeNotSupportException extends JoberGenericableException {
    public TypeNotSupportException(String message) {
        super(message);
    }

    public TypeNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }
}
