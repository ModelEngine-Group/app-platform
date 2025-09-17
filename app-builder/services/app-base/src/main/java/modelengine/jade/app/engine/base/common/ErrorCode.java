/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.common;

/**
 * 错误码。
 *
 * @author 陈潇文
 * @since 2024-05-27
 */
public interface ErrorCode {
    /**
     * 获取错误码。
     *
     * @return 表示错误码的 {@code int}。
     */
    int getErrorCode();

    /**
     * 获取错误提示信息。
     *
     * @return 表示错误信息的 {@link String}。
     */
    String getMessage();
}
