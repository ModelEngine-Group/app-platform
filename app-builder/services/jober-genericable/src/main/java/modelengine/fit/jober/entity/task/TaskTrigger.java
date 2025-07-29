/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity.task;

import java.util.Objects;

/**
 * 表示任务触发器。
 *
 * @author 陈镕希
 * @since 2023-08-29
 */
public class TaskTrigger {
    private String id;

    private String propertyId;

    private String fitableId;

    /**
     * TaskTrigger
     */
    public TaskTrigger() {
    }

    public TaskTrigger(String id, String propertyId, String fitableId) {
        this.id = id;
        this.propertyId = propertyId;
        this.fitableId = fitableId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getFitableId() {
        return fitableId;
    }

    public void setFitableId(String fitableId) {
        this.fitableId = fitableId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskTrigger that = (TaskTrigger) o;
        return Objects.equals(id, that.id) && Objects.equals(propertyId, that.propertyId) && Objects.equals(fitableId,
                that.fitableId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, propertyId, fitableId);
    }

    @Override
    public String toString() {
        return "TaskTrigger{" + "id='" + id + '\'' + ", propertyId='" + propertyId + '\'' + ", fitableId='" + fitableId
                + '\'' + '}';
    }
}
