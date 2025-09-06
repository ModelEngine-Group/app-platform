/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta.definition;

import modelengine.fit.jober.entity.task.TaskProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 表示meta结构体。
 *
 * @author 陈镕希
 * @since 2023-12-08
 */
public class Meta {
    private String id;

    private String name;

    private String category;

    private List<TaskProperty> properties;

    private String creator;

    private LocalDateTime creationTime;

    private String lastModifier;

    private LocalDateTime lastModificationTime;

    private String tenant;

    private Map<String, Object> attributes;

    /**
     * Meta
     */
    public Meta() {
    }

    public Meta(String id, String name, String category, List<TaskProperty> properties, String creator,
            LocalDateTime creationTime, String lastModifier, LocalDateTime lastModificationTime, String tenant,
            Map<String, Object> attributes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.properties = properties;
        this.creator = creator;
        this.creationTime = creationTime;
        this.lastModifier = lastModifier;
        this.lastModificationTime = lastModificationTime;
        this.tenant = tenant;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<TaskProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<TaskProperty> properties) {
        this.properties = properties;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public LocalDateTime getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(LocalDateTime lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
