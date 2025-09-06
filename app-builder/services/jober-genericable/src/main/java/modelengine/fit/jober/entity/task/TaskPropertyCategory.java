/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity.task;

import java.util.Objects;

/**
 * 表示任务属性类别。
 *
 * @author 陈镕希
 * @since 2023-08-29
 */
public class TaskPropertyCategory {
    private String value;

    private String category;

    /**
     * TaskPropertyCategory
     */
    public TaskPropertyCategory() {
    }

    public TaskPropertyCategory(String value, String category) {
        this.value = value;
        this.category = category;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskPropertyCategory that = (TaskPropertyCategory) o;
        return Objects.equals(value, that.value) && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, category);
    }

    @Override
    public String toString() {
        return "TaskPropertyCategory{" + "value='" + value + '\'' + ", category='" + category + '\'' + '}';
    }
}
