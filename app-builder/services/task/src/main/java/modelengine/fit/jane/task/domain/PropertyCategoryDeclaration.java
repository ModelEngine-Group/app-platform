/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fit.jane.task.util.UndefinableValue;

/**
 * 为属性的类目配置提供声明。
 *
 * @author 梁济时
 * @since 2023-08-22
 */
public class PropertyCategoryDeclaration {
    private UndefinableValue<String> value;

    private UndefinableValue<String> category;

    public PropertyCategoryDeclaration() {
        this(null, null);
    }

    public PropertyCategoryDeclaration(UndefinableValue<String> value, UndefinableValue<String> category) {
        this.value = value;
        this.category = category;
    }

    public UndefinableValue<String> getValue() {
        return value;
    }

    public void setValue(UndefinableValue<String> value) {
        this.value = value;
    }

    public UndefinableValue<String> getCategory() {
        return category;
    }

    public void setCategory(UndefinableValue<String> category) {
        this.category = category;
    }
}
