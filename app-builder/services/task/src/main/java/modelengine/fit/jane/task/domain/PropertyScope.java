/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

/**
 * 表示属性的适用范围。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
public enum PropertyScope {
    /**
     * 表示公开属性。
     */
    PUBLIC,

    /**
     * 表示私有属性。
     */
    PRIVATE,

    /**
     * 表示系统属性。
     */
    SYSTEM,
}
