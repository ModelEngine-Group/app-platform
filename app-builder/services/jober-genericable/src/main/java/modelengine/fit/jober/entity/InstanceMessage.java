/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

import java.util.List;

/**
 * InstanceMessage实体类
 *
 * @author 梁济时
 * @since 2023-08-28
 */
public class InstanceMessage {
    private String taskId;

    private String instanceId;

    private String taskTypeId;

    private String sourceName;

    private String sourceApp;

    private String tenant;

    private String operator;

    private List<PropertyValue> primaries;

    /**
     * InstanceMessage
     */
    public InstanceMessage() {
    }

    public InstanceMessage(String taskId, String instanceId, String taskTypeId, String sourceName, String sourceApp,
            String tenant, String operator, List<PropertyValue> primaries) {
        this.taskId = taskId;
        this.instanceId = instanceId;
        this.taskTypeId = taskTypeId;
        this.sourceName = sourceName;
        this.sourceApp = sourceApp;
        this.tenant = tenant;
        this.operator = operator;
        this.primaries = primaries;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(String taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceApp() {
        return sourceApp;
    }

    public void setSourceApp(String sourceApp) {
        this.sourceApp = sourceApp;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<PropertyValue> getPrimaries() {
        return primaries;
    }

    public void setPrimaries(List<PropertyValue> primaries) {
        this.primaries = primaries;
    }
}
