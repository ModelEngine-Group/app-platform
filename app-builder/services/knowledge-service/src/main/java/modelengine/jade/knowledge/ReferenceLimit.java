/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import lombok.Data;
import modelengine.jade.knowledge.enums.ReferenceType;

import lombok.NoArgsConstructor;
import modelengine.fitframework.inspection.Validation;

/**
 * 引用上限。
 *
 * @author 刘信宏
 * @since 2024-10-08
 */
@Data
@NoArgsConstructor
public class ReferenceLimit {
    private String type;
    private int value;

    /**
     * 初始化 {@link ReferenceLimit} 对象。
     *
     * @param type 表示检索方式的 {@link String}。
     * @param value 表示检索方式描述的 {@link String}。
     */
    public ReferenceLimit(ReferenceType type, int value) {
        Validation.notNull(type, "The reference limit type cannot be null.");
        this.type = type.value();
        this.value = Validation.notNegative(value, "The value cannot be negatived.");
    }

    /**
     * 获取检索方式标识。
     *
     * @return 表示检索方式标识的 {@link String}。
     */
    public String type() {
        return this.type;
    }

    /**
     * 获取检索方式描述。
     *
     * @return 表示检索方式描述的 {@link String}。
     */
    public int value() {
        return this.value;
    }
}
