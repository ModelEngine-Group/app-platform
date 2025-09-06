/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 表示任务定义中的属性信息。
 *
 * @author 梁济时
 * @since 2023-11-07
 */
public class TaskPropertyInfo {
    private String id;

    private String name;

    private String dataType;

    private String description;

    private Boolean isRequired;

    private Boolean isIdentifiable;

    private String scope;

    public TaskPropertyInfo() {
        this(null, null, null, null, null, null, null);
    }

    public TaskPropertyInfo(String id, String name, String dataType, String description, Boolean isRequired,
            Boolean isIdentifiable, String scope) {
        this.id = id;
        this.name = name;
        this.dataType = dataType;
        this.description = description;
        this.isRequired = isRequired;
        this.isIdentifiable = isIdentifiable;
        this.scope = scope;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Boolean getIsIdentifiable() {
        return isIdentifiable;
    }

    public void setIsIdentifiable(Boolean isIdentifiable) {
        this.isIdentifiable = isIdentifiable;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            TaskPropertyInfo another = (TaskPropertyInfo) obj;
            return Objects.equals(this.getId(), another.getId()) && Objects.equals(this.getName(), another.getName())
                    && Objects.equals(this.getDataType(), another.getDataType()) && Objects.equals(
                    this.getDescription(), another.getDescription()) && Objects.equals(this.getIsRequired(),
                    another.getIsRequired()) && Objects.equals(this.getIsIdentifiable(), another.getIsIdentifiable())
                    && Objects.equals(this.getScope(), another.getScope());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.getClass(), this.getId(), this.getName(), this.getDataType(), this.getDescription(),
                this.getIsRequired(), this.getIsIdentifiable(), this.getScope()
        });
    }

    @Override
    public String toString() {
        return StringUtils.format(
                "[id={0}, name={1}, dataType={2}, description={3}, required={4}, identifiable={5}, scope={6}]",
                this.getId(), this.getName(), this.getDataType(), this.getDescription(), this.getIsRequired(),
                this.getIsIdentifiable(), this.getScope());
    }
}
