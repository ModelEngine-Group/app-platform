/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task;

import modelengine.fit.jane.RangeInfo;
import modelengine.fit.jane.RangedResultSetInfo;
import modelengine.fit.jober.entity.OperationContext;
import modelengine.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 为管理指定数据源中的任务实例提供支持。
 *
 * @author 梁济时
 * @since 2023-11-20
 */
public interface SourcedTaskInstanceService {
    /**
     * 创建任务实例。
     *
     * @param taskSource 表示待创建的任务实例所属的数据源的 {@link TaskSourceInfo}。
     * @param info 表示实例信息的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     * @param context 表示当前操作的上下文信息的 {@link OperationContext}。
     * @return 表示新创建的任务实例的 {@link TaskInstanceInfo}。
     */
    @Genericable("ddaa2216ed8a4366af8fa6cf6e8bacf9")
    TaskInstanceInfo create(TaskSourceInfo taskSource, Map<String, String> info, OperationContext context);

    /**
     * 修改任务实例。
     *
     * @param taskSource 表示待修改的任务实例所属的数据源的 {@link TaskSourceInfo}。
     * @param instanceId 表示任务实例的唯一标识的 {@link String}。
     * @param info 表示实例信息的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     * @param context 表示当前操作的上下文信息的 {@link OperationContext}。
     */
    @Genericable("314757dfb09e47c4b613f98cd086cb25")
    void patch(TaskSourceInfo taskSource, String instanceId, Map<String, String> info, OperationContext context);

    /**
     * 删除任务实例。
     *
     * @param taskSource 表示待删除的任务实例所属的数据源的 {@link TaskSourceInfo}。
     * @param instanceId 表示任务实例的唯一标识的 {@link String}。
     * @param context 表示当前操作的上下文信息的 {@link OperationContext}。
     */
    @Genericable("667bc18d3528473c8510b34829c80ce9")
    void delete(TaskSourceInfo taskSource, String instanceId, OperationContext context);

    /**
     * 检索指定唯一标识的任务实例。
     *
     * @param taskSource 表示待的任检索务实例所属的数据源的 {@link TaskSourceInfo}。
     * @param instanceId 表示任务实例的唯一标识的 {@link String}。
     * @param context 表示当前操作的上下文信息的 {@link OperationContext}。
     * @return 若存在该任务实例，则为表示该任务实例的 {@link TaskInstanceInfo}，否则为 {@code null}。
     */
    @Genericable("fefe9bc6358642a4ac997832db549920")
    TaskInstanceInfo retrieve(TaskSourceInfo taskSource, String instanceId, OperationContext context);

    /**
     * 查询符合条件的任务实例。
     *
     * @param taskSource 表示待查询的任务实例所属的数据源的 {@link TaskSourceInfo}。
     * @param filter 表示任务实例的筛选条件的 {@link TaskInstanceFilterInfo}。
     * @param range 表示待查找的任务实例在全量结果集中的范围的 {@link RangeInfo}。
     * @param context 表示当前操作的上下文信息的 {@link OperationContext}。
     * @return 表示查找到的任务实例的结果的 {@link RangedResultSetInfo}{@code <}{@link TaskInstanceInfo}{@code >}。
     */
    @Genericable("805d46f4137e41909d81a7e469e2534a")
    RangedResultSetInfo<TaskInstanceInfo> list(TaskSourceInfo taskSource, TaskInstanceFilterInfo filter,
            RangeInfo range, OperationContext context);
}
