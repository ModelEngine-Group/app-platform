/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity.task;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表示任务定义。
 *
 * @author 陈镕希
 * @since 2023-08-29
 */
public class Task {
    private String id;

    private String name;

    private String category;

    private List<TaskProperty> properties;

    private List<TaskType> types;

    private List<TaskSource> sources;

    private List<TaskCategoryTrigger> categoryTriggers;

    private String creator;

    private LocalDateTime creationTime;

    private String lastModifier;

    private LocalDateTime lastModificationTime;

    private String tenant;

    /**
     * Task
     */
    public Task() {
    }

    public Task(String id, String name, String category, List<TaskProperty> properties, List<TaskType> types,
            List<TaskSource> sources, List<TaskCategoryTrigger> categoryTriggers, String creator,
            LocalDateTime creationTime, String lastModifier, LocalDateTime lastModificationTime, String tenant) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.properties = properties;
        this.types = types;
        this.sources = sources;
        this.categoryTriggers = categoryTriggers;
        this.creator = creator;
        this.creationTime = creationTime;
        this.lastModifier = lastModifier;
        this.lastModificationTime = lastModificationTime;
        this.tenant = tenant;
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

    public List<TaskType> getTypes() {
        return types;
    }

    public void setTypes(List<TaskType> types) {
        this.types = types;
    }

    public List<TaskSource> getSources() {
        return sources;
    }

    public void setSources(List<TaskSource> sources) {
        this.sources = sources;
    }

    public List<TaskCategoryTrigger> getCategoryTriggers() {
        return categoryTriggers;
    }

    public void setCategoryTriggers(List<TaskCategoryTrigger> categoryTriggers) {
        this.categoryTriggers = categoryTriggers;
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
}
