/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity.task;

import java.util.List;
import java.util.Map;

/**
 * 表示任务属性。
 *
 * @author 陈镕希
 * @since 2023-08-29
 */
public class TaskProperty {
    private String id;

    private String name;

    private String dataType;

    private int sequence;

    private String description;

    private boolean isRequired;

    private boolean isIdentifiable;

    private String scope;

    private Map<String, Object> appearance;

    private List<TaskPropertyCategory> categories;

    /**
     * TaskProperty
     */
    public TaskProperty() {
    }

    public TaskProperty(String id, String name, String dataType, int sequence, String description, boolean isRequired,
            boolean isIdentifiable, String scope, Map<String, Object> appearance,
            List<TaskPropertyCategory> categories) {
        this.id = id;
        this.name = name;
        this.dataType = dataType;
        this.sequence = sequence;
        this.description = description;
        this.isRequired = isRequired;
        this.isIdentifiable = isIdentifiable;
        this.scope = scope;
        this.appearance = appearance;
        this.categories = categories;
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public boolean isIdentifiable() {
        return isIdentifiable;
    }

    public void setIdentifiable(boolean isIdentifiable) {
        this.isIdentifiable = isIdentifiable;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Map<String, Object> getAppearance() {
        return appearance;
    }

    public void setAppearance(Map<String, Object> appearance) {
        this.appearance = appearance;
    }

    public List<TaskPropertyCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<TaskPropertyCategory> categories) {
        this.categories = categories;
    }
}
