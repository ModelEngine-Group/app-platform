/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

import java.util.Objects;

/**
 * 任务实例的类目发生变化。
 *
 * @author 梁济时
 * @since 2023-08-28
 */
public class InstanceCategoryChanged extends InstanceMessage {
    private String newCategory;

    /**
     * InstanceCategoryChanged
     */
    public InstanceCategoryChanged() {
    }

    public InstanceCategoryChanged(String newCategory) {
        this.newCategory = newCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        InstanceCategoryChanged that = (InstanceCategoryChanged) o;
        return Objects.equals(newCategory, that.newCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), newCategory);
    }

    public String getNewCategory() {
        return newCategory;
    }

    public void setNewCategory(String newCategory) {
        this.newCategory = newCategory;
    }
}
