/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.integration;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.jade.store.entity.query.TaskQuery;
import modelengine.jade.store.entity.transfer.TaskData;
import modelengine.jade.store.service.EcoTaskService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 表示 任务 的集成测试用例集。
 *
 * @author 李金绪
 * @since 2024-09-18
 */
@IntegrationTest(scanPackages = "modelengine.jade.store")
@Sql(before = "sql/create/task.sql")
@DisplayName("Task 集成测试")
public class TaskIntegrationTest {
    @Fit
    private EcoTaskService ecoTaskService;

    @Test
    @Sql(before = {"sql/create/task.sql", "sql/insert/task.sql"})
    @DisplayName("测试查询单个任务")
    void shouldOkWhenGetTask() {
        TaskData task = this.ecoTaskService.getTask("depth-estimation");
        assertThat(task.getToolUniqueName()).isEqualTo("name2");
    }

    @Test
    @Sql(before = {"sql/create/task.sql", "sql/insert/task.sql"})
    @DisplayName("测试查询任务列表")
    void shouldOkWhenGetTasks() {
        TaskQuery taskQuery = new TaskQuery("name2", null, null);
        List<TaskData> tasks = this.ecoTaskService.getTasks(taskQuery);
        assertThat(tasks.size()).isEqualTo(1);
        assertThat(tasks.get(0).getTaskName()).isEqualTo("depth-estimation");
    }
}
