/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import lombok.NoArgsConstructor;
import modelengine.fitframework.inspection.Validation;

/**
 * Schema 条目信息基类。
 *
 * @author 邱晓霞
 * @since 2024-10-15
 */
@NoArgsConstructor
public class SchemaItem {
    private String type;
    private String name;
    private String description;

    /**
     * 初始化 {@link SchemaItem} 对象。
     *
     * @param type 表示条目类型的 {@link String}。
     * @param name 表示条目名称的 {@link String}。
     * @param description 表示条目描述的 {@link String}。
     */
    public SchemaItem(String type, String name, String description) {
        this.type = Validation.notBlank(type, "The type cannot be blank.");
        this.name = Validation.notBlank(name, "The name cannot be blank.");
        this.description = Validation.notBlank(description, "The description cannot be blank.");
    }

    /**
     * 获取条目类型。
     *
     * @return 表示条目类型的 {@link String}。
     */
    public String type() {
        return this.type;
    }

    /**
     * 获取条目描述。
     *
     * @return 表示条目描述的 {@link String}。
     */
    public String description() {
        return this.description;
    }

    /**
     * 获取条目名称。
     *
     * @return 表示条目名称的 {@link String}。
     */
    public String name() {
        return this.name;
    }
}
