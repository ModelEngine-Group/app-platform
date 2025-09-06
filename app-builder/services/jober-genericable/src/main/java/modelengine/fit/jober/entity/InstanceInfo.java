/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

import java.util.List;
import java.util.Map;

/**
 * 任务实例相关信息。
 *
 * @author 陈镕希
 * @since 2023-08-28
 */
public class InstanceInfo {
    private String taskTypeId;

    private String sourceId;

    private Map<String, Object> info;

    private List<String> tags;

    /**
     * InstanceInfo
     */
    public InstanceInfo() {
    }

    public InstanceInfo(String taskTypeId, String sourceId, Map<String, Object> info, List<String> tags) {
        this.taskTypeId = taskTypeId;
        this.sourceId = sourceId;
        this.info = info;
        this.tags = tags;
    }

    public String getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(String taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}