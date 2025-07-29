/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

/**
 * 应用类型枚举
 *
 * @author 邬涨财
 * @since 2024-04-24
 */
public enum AppTypeEnum {
    APP("app"),
    TEMPLATE("template"),
    WORKFLOW("workflow");

    private final String code;

    AppTypeEnum(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }
}
