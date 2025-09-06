/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity.consts;

import java.util.Arrays;
import java.util.Optional;

/**
 * 节点类型的枚举
 *
 * @author 夏斐
 * @since 2023/12/14
 */
public enum NodeTypes {
    START("START", "开始节点"),
    END("END", "结束节点"),
    STATE("STATE", "运行节点"),
    CONDITION("CONDITION", "条件节点"),
    ;

    private String type;

    private String name;

    NodeTypes(String type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * of
     *
     * @param type type
     * @return Optional<NodeTypes>
     */
    public static Optional<NodeTypes> of(String type) {
        return Arrays.stream(NodeTypes.values()).filter(value -> value.getType().equals(type)).findFirst();
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
