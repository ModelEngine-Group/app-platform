/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity.task;

import java.util.List;
import java.util.Objects;

/**
 * 表示任务类别触发器。
 *
 * @author 陈镕希
 * @since 2023-08-29
 */
public class TaskCategoryTrigger {
    private String category;

    private List<String> fitableIds;

    /**
     * TaskCategoryTrigger
     */
    public TaskCategoryTrigger() {
    }

    public TaskCategoryTrigger(String category, List<String> fitableIds) {
        this.category = category;
        this.fitableIds = fitableIds;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getFitableIds() {
        return fitableIds;
    }

    public void setFitableIds(List<String> fitableIds) {
        this.fitableIds = fitableIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskCategoryTrigger that = (TaskCategoryTrigger) o;
        return Objects.equals(category, that.category) && Objects.equals(fitableIds, that.fitableIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, fitableIds);
    }

    @Override
    public String toString() {
        return "TaskCategoryTrigger{" + "category='" + category + '\'' + ", fitableIds=" + fitableIds + '}';
    }
}
