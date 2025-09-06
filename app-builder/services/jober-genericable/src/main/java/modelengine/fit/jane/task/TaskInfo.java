/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 表示任务定义的信息。
 *
 * <p>可通过 {@link TaskInfos#lookupTaskType(TaskInfo, String)} 方法查找任务定义中包含的指定唯一标识的任务类型。</p>
 *
 * @author 梁济时
 * @since 2023-11-07
 */
public class TaskInfo {
    private String id;

    private String name;

    private List<TaskPropertyInfo> properties;

    private List<TaskTypeInfo> types;

    public TaskInfo() {
        this(null, null, null, null);
    }

    public TaskInfo(String id, String name, List<TaskPropertyInfo> properties, List<TaskTypeInfo> types) {
        this.id = id;
        this.name = name;
        this.properties = properties;
        this.types = types;
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

    public List<TaskPropertyInfo> getProperties() {
        return properties;
    }

    public void setProperties(List<TaskPropertyInfo> properties) {
        this.properties = properties;
    }

    public List<TaskTypeInfo> getTypes() {
        return types;
    }

    public void setTypes(List<TaskTypeInfo> types) {
        this.types = types;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && getClass() == obj.getClass()) {
            TaskInfo another = (TaskInfo) obj;
            return Objects.equals(this.getId(), another.getId()) && Objects.equals(this.getName(), another.getName())
                    && CollectionUtils.equals(this.getProperties(), another.getProperties()) && CollectionUtils.equals(
                    this.getTypes(), another.getTypes());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        List<TaskPropertyInfo> actualProperties = nullIf(this.getProperties(), Collections.emptyList());
        List<TaskTypeInfo> actualTypes = nullIf(this.getTypes(), Collections.emptyList());
        List<Object> objects = new ArrayList<>(actualProperties.size() + actualTypes.size() + 3);
        objects.add(this.getClass());
        objects.add(this.getId());
        objects.add(this.getName());
        objects.addAll(actualProperties);
        objects.addAll(actualTypes);
        return Arrays.hashCode(objects.toArray());
    }

    @Override
    public String toString() {
        return StringUtils.format("[id={0}, name={1}, properties={2}, types={3}]", this.getId(), this.getName(),
                CollectionUtils.toString(this.getProperties()), CollectionUtils.toString(this.getTypes()));
    }
}
