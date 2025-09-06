/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 应用实例的创建数据类.
 *
 * @author 张越
 * @since 2025-01-08
 */
public class TaskInstanceCreateEntity extends TaskInstanceEntity<TaskInstanceCreateEntity> {
    private static final String DEFAULT_NAME = "无标题";

    TaskInstanceCreateEntity(String taskId, String creator, String name) {
        super();
        this.setTaskId(taskId)
                .setCreator(creator)
                .setCreateTime(LocalDateTime.now())
                .setProgress("0")
                .setName(Optional.ofNullable(name).orElse(DEFAULT_NAME));
    }

    @Override
    public TaskInstanceCreateEntity self() {
        return this;
    }
}
