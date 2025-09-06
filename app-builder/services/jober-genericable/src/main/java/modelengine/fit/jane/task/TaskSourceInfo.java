/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * 表示任务数据源。
 *
 * @author 梁济时
 * @since 2023-11-22
 */
public class TaskSourceInfo {
    private TaskInfo owningTask;

    private String typeId;

    private Map<String, Object> metadata;

    public TaskSourceInfo() {
        this(null, null, null);
    }

    public TaskSourceInfo(TaskInfo owningTask, String typeId, Map<String, Object> metadata) {
        this.owningTask = owningTask;
        this.typeId = typeId;
        this.metadata = metadata;
    }

    public TaskInfo getOwningTask() {
        return owningTask;
    }

    public void setOwningTask(TaskInfo owningTask) {
        this.owningTask = owningTask;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            TaskSourceInfo that = (TaskSourceInfo) obj;
            return Objects.equals(this.getOwningTask(), that.getOwningTask()) && Objects.equals(this.getTypeId(),
                    that.getTypeId()) && Objects.equals(this.getMetadata(), that.getMetadata());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.getClass(), this.getOwningTask(), this.getTypeId(), this.getMetadata()
        });
    }

    @Override
    public String toString() {
        return StringUtils.format("[owningTask={0}, typeId={1}, metadata={2}]", this.getOwningTask(), this.getTypeId(),
                this.getMetadata());
    }
}
