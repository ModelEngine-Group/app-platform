/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

import lombok.Getter;

/**
 * 表示重新对话的模式。
 *
 * @author 黄夏露
 * @since 2024-07-12
 */
@Getter
public enum RestartModeEnum {
    /**
     * 覆盖式。
     */
    OVERWRITE("overwrite"),

    /**
     * 增长式。
     */
    INCREMENT("increment");

    private final String mode;

    RestartModeEnum(String mode) {
        this.mode = mode;
    }
}
