/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import modelengine.jade.knowledge.enums.KnowledgeTypeEnum;

import java.util.Arrays;

/**
 * 表示外部知识库所支持类型的枚举。
 *
 * @author 马朝阳
 * @since 2024-11-04
 */
public enum KnowledgeTypeExternalEnum {
    /**
     * 向量。
     */
    VECTOR(KnowledgeTypeEnum.VECTOR.value()),

    /**
     * 表格。
     */
    RDB(KnowledgeTypeEnum.RDB.value()),

    /**
     * 表格。
     */
    TABLE(KnowledgeTypeEnum.RDB.value()),

    /**
     * 键值对。
     */
    KV(KnowledgeTypeEnum.KV.value()),

    /**
     * 键值对。
     */
    GRAPH(KnowledgeTypeEnum.KV.value()),

    /**
     * 其它类型。
     */
    OTHER(KnowledgeTypeEnum.OTHER.value());

    private final String value;

    KnowledgeTypeExternalEnum(String value) {
        this.value = value;
    }

    /**
     * 获取知识库类型的值。
     *
     * @return 表示属性值的 {@link String}。
     */
    public String value() {
        return this.value;
    }

    /**
     * 根据给定的知识库类型，返回对应的枚举值。
     *
     * @param value 用于匹配枚举值的 {@link String}。
     * @return 返回匹配的枚举值的 {@link KnowledgeTypeExternalEnum}。
     */
    public static KnowledgeTypeExternalEnum from(String value) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(KnowledgeTypeExternalEnum.OTHER);
    }
}
