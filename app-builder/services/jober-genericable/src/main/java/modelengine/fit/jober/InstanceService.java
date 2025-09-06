/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober;

import modelengine.fit.jober.common.BadRequestException;
import modelengine.fit.jober.common.JoberGenericableException;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.ServerInternalException;
import modelengine.fit.jober.common.TooManyRequestException;
import modelengine.fit.jober.entity.InstanceInfo;
import modelengine.fit.jober.entity.InstanceQueryFilter;
import modelengine.fit.jober.entity.OperationContext;
import modelengine.fit.jober.entity.TaskEntity;
import modelengine.fit.jober.entity.instance.Instance;
import modelengine.fitframework.annotation.Genericable;

/**
 * 任务实例服务Genericable。
 *
 * @author 陈镕希
 * @since 2023-08-14
 */
public interface InstanceService {
    /**
     * 创建任务实例。
     *
     * @param taskId 任务定义唯一标识的 {@link String}。
     * @param instanceInfo 任务实例信息的 {@link InstanceInfo}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @return Instance
     * @throws JoberGenericableException 当调用过程发生异常。
     * @throws BadRequestException 当调用过程发生错误请求异常。
     * @throws TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "f1b88d9eb48b48959365a24e27dabb80")
    Instance createTaskInstance(String taskId, InstanceInfo instanceInfo, OperationContext context);

    /**
     * 更新任务实例。
     *
     * @param taskId 任务定义唯一标识的 {@link String}。
     * @param instanceId 任务实例唯一标识的 {@link String}。
     * @param instanceInfo 任务实例信息的 {@link InstanceInfo}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @throws JoberGenericableException 当调用过程发生异常。
     * @throws BadRequestException 当调用过程发生错误请求异常。
     * @throws TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "842d05ef61e84f50a82d706a80684de0")
    void patchTaskInstance(String taskId, String instanceId, InstanceInfo instanceInfo, OperationContext context);

    /**
     * 删除任务实例。
     *
     * @param taskId 任务定义唯一标识的 {@link String}。
     * @param instanceId 任务实例唯一标识的 {@link String}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @throws JoberGenericableException 当调用过程发生异常。
     * @throws BadRequestException 当调用过程发生错误请求异常。
     * @throws TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "9250577f119541089545cac679518833")
    void deleteTaskInstance(String taskId, String instanceId, OperationContext context);

    /**
     * 查询任务实例。
     *
     * @param taskId 表示实例所属任务唯一标识的 {@link String}。
     * @param filter 表示任务实例过滤器的 {@link InstanceQueryFilter}。
     * @param offset 表示查询到的任务定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的任务定义的结果集中的最大数量的 32 位整数。
     * @param isDeleted 表示是否查询删除表，true则代表要查询的是删除表。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link TaskEntity}{@code >}。
     */
    @Genericable(id = "3c6a58456e5948f39867a376980ed2fe")
    RangedResultSet<Instance> list(String taskId, InstanceQueryFilter filter, long offset, int limit, boolean isDeleted,
            OperationContext context);

    /**
     * 恢复被删除的任务实例。
     *
     * @param taskId 任务定义唯一标识的 {@link String}。
     * @param instanceId 任务实例唯一标识的 {@link String}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     */
    @Genericable(id = "0a43f5401d9b0a6e059ff48c5b167465")
    void recoverTaskInstance(String taskId, String instanceId, OperationContext context);
}
