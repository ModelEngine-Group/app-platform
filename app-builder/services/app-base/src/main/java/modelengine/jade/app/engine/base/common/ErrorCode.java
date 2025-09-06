/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.common;

/**
 * 错误码
 *
 * @since 2024-5-27
 *
 */
public interface ErrorCode {
    /**
     * 获取错误码
     *
     * @return 错误码
     */
    int getErrorCode();

    /**
     * 获取错误提示信息
     *
     * @return 错误信息
     */
    String getMessage();
}
