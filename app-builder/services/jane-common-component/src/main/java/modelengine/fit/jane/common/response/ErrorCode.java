/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.common.response;

/**
 * 错误码接口
 *
 * @author 刘信宏
 * @since 2024-2-2
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
