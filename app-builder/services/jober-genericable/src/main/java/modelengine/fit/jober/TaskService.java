/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober;

import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.entity.OperationContext;
import modelengine.fit.jober.entity.TaskFilter;
import modelengine.fit.jober.entity.task.Task;
import modelengine.fitframework.annotation.Genericable;

/**
 * 任务定义服务Genericable。
 *
 * @author 陈镕希
 * @since 2023-08-29
 */
public interface TaskService {
    /**
     * 查询任务定义。
     *
     * @param filter 表示任务过滤器的 {@link TaskFilter}。
     * @param offset 表示查询到的任务定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的任务定义的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Task}{@code >}。
     */
    @Genericable(id = "7652ed77c5554a5d8bd984f47589d63e")
    RangedResultSet<Task> list(TaskFilter filter, long offset, int limit, OperationContext context);

    /**
     * 检索任务定义。
     *
     * @param taskId 表示待检索的任务定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的任务定义的 {@link Task}。
     */
    @Genericable(id = "5a7683b3b6ac495198efc492790a3a5f")
    Task retrieve(String taskId, OperationContext context);
}
