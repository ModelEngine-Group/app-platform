/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.enums;

/**
 * 表示前端知识库显示所支持类型的枚举。
 *
 * @author 何嘉斌
 * @since 2024-09-25
 */
public enum KnowledgeTypeEnum {
    /**
     * 向量。
     */
    VECTOR("VECTOR"),

    /**
     * 表格。
     */
    RDB("RDB"),

    /**
     * 键值对。
     */
    KV("KV"),

    /**
     * 其它类型。
     */
    OTHER("OTHER");

    private final String value;

    KnowledgeTypeEnum(String value) {
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
}