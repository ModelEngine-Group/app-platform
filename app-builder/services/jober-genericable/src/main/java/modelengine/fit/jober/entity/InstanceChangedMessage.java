/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

import java.util.List;

/**
 * notifyInstanceChanged使用的InstanceChangedMessage结构体。
 *
 * @author 陈镕希
 * @since 2023-07-06
 */
public class InstanceChangedMessage {
    /**
     * 变化的实例类型的 {@link String}。
     */
    private String type;

    /**
     * 实例所属租户名称的 {@link String}。
     */
    private String tenant;

    /**
     * 实例所属定义唯一标识的 {@link String}。
     */
    private String definitionId;

    /**
     * 变化的实例唯一标识的 {@link String}。
     */
    private String taskTypeId;

    /**
     * 三方系统的变化实例Id的 {@link String}。
     */
    private String sourceId;

    /**
     * 实例变化操作人的 {@link String}。
     */
    private String operator;

    /**
     * 实例下配置变化列表的 {@link List}{@code <}{@link ChangedProperty}{@code >}。
     */
    private List<ChangedProperty> changedProperties;

    /**
     * InstanceChangedMessage
     */
    public InstanceChangedMessage() {
    }

    public InstanceChangedMessage(String type, String tenant, String definitionId, String taskTypeId, String sourceId,
            String operator, List<ChangedProperty> changedProperties) {
        this.type = type;
        this.tenant = tenant;
        this.definitionId = definitionId;
        this.taskTypeId = taskTypeId;
        this.sourceId = sourceId;
        this.operator = operator;
        this.changedProperties = changedProperties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<ChangedProperty> getChangedProperties() {
        return changedProperties;
    }

    public void setChangedProperties(List<ChangedProperty> changedProperties) {
        this.changedProperties = changedProperties;
    }

    /**
     * ChangedProperty
     */
    public static class ChangedProperty {
        /**
         * 变化配置的key的 {@link String}。
         */
        private String key;

        /**
         * 配置变化前的value的 {@link String}。
         */
        private String oldValue;

        /**
         * 配置变化后的value的 {@link String}。
         */
        private String newValue;

        /**
         * ChangedProperty
         */
        public ChangedProperty() {
        }

        public ChangedProperty(String key, String oldValue, String newValue) {
            this.key = key;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getOldValue() {
            return oldValue;
        }

        public void setOldValue(String oldValue) {
            this.oldValue = oldValue;
        }

        public String getNewValue() {
            return newValue;
        }

        public void setNewValue(String newValue) {
            this.newValue = newValue;
        }
    }
}

