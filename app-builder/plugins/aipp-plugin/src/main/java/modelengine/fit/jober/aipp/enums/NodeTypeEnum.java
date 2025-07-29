/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

/**
 * 节点类型的枚举类。
 *
 * @author 邱晓霞
 * @since 2024-08-26
 */
public enum NodeTypeEnum {
    /**
     * 基础节点。
     */
    BASIC("basic"),

    /**
     * 评估节点。
     */
    EVALUATION("evaluation");

    private final String type;

    NodeTypeEnum(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}
