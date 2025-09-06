/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity.task;

import java.util.List;
import java.util.Objects;

/**
 * 表示任务数据源。
 *
 * @author 陈镕希
 * @since 2023-08-29
 */
public class TaskSource {
    private String id;

    private String name;

    private String app;

    private String type;

    private List<TaskTrigger> triggers;

    /**
     * TaskSource
     */
    public TaskSource() {
    }

    public TaskSource(String id, String name, String app, String type, List<TaskTrigger> triggers) {
        this.id = id;
        this.name = name;
        this.app = app;
        this.type = type;
        this.triggers = triggers;
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

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TaskTrigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<TaskTrigger> triggers) {
        this.triggers = triggers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskSource that = (TaskSource) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(app, that.app)
                && Objects.equals(type, that.type) && Objects.equals(triggers, that.triggers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, app, type, triggers);
    }

    @Override
    public String toString() {
        return "TaskSource{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", app='" + app + '\'' + ", type='"
                + type + '\'' + ", triggers=" + triggers + '}';
    }
}
