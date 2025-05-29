/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.common.RangedResultSet;

import java.util.List;
import java.util.Optional;

/**
 * 应用任务服务.
 *
 * @author 张越
 * @since 2025-01-03
 */
public interface AppTaskService {
    /**
     * 创建应用任务.
     *
     * @param task 任务创建参数.
     * @param context 操作人上下文信息.
     * @return {@link AppTask} 对象.
     */
    AppTask createTask(AppTask task, OperationContext context);

    /**
     * 修改应用任务数据.
     *
     * @param task 任务对象.
     * @param context 操作人上下文信息.
     */
    void updateTask(AppTask task, OperationContext context);

    /**
     * 通过id删除应用任务.
     *
     * @param taskId 应用任务id.
     * @param context 操作人上下文信息.
     */
    void deleteTaskById(String taskId, OperationContext context);

    /**
     * 获分页查询指定应用的已发布元数据列表，按更新时间倒序。
     *
     * @param appSuiteId 应用的唯一标识的 {@link String}
     * @param offset 表示偏移量的 {@code long}。
     * @param limit 表示单页最大数量的 {@code int}。
     * @param context 表示操作人上下文的 {@link OperationContext}。
     * @return {@link List}{@code <}{@link AppTask}{@code >} 应用任务列表.
     */
    List<AppTask> getPublishedByPage(String appSuiteId, long offset, int limit, OperationContext context);

    /**
     * 按条件获取最新的被创建出来的应用任务.
     *
     * @param appSuiteId 应用的唯一标识.
     * @param aippType aipp类型.
     * @param status 状态.
     * @param context 操作人上下文信息.
     * @return {@link Optional}{@code <}{@link AppTask}{@code >} 应用任务.
     */
    Optional<AppTask> getLatestCreate(String appSuiteId, String aippType, String status, OperationContext context);

    /**
     * 按条件获取最新的被创建出来的应用任务.
     *
     * @param appSuiteId 应用的唯一标识.
     * @param aippType aipp类型.
     * @param context 操作人上下文信息.
     * @return {@link Optional}{@code <}{@link AppTask}{@code >} 应用任务.
     */
    Optional<AppTask> getLatestCreate(String appSuiteId, String aippType, OperationContext context);

    /**
     * 通过uniqueName获取应用任务.
     *
     * @param uniqueName 任务在store中的唯一标识.
     * @param context 操作人上下文信息.
     * @return {@link Optional}{@code <}{@link AppTask}{@code >} 应用任务.
     */
    Optional<AppTask> getLatest(String uniqueName, OperationContext context);

    /**
     * 根据应用id，版本号获取最新被修改的第一个数据.
     *
     * @param appSuiteId 应用的唯一标识.
     * @param version 版本号.
     * @param context 操作人上下文信息.
     * @return {@link Optional}{@code <}{@link AppTask}{@code >} 应用任务.
     */
    Optional<AppTask> getLatest(String appSuiteId, String version, OperationContext context);

    /**
     * 根据条件，查询最新的第一个数据.
     *
     * @param query 查询参数.
     * @param context 操作人上下文信息.
     * @return {@link Optional}{@code <}{@link AppTask}{@code >} 应用任务.
     */
    Optional<AppTask> getLatest(AppTask query, OperationContext context);

    /**
     * 获取最新的任务.
     *
     * @param query 查询参数.
     * @param context 操作人上下文信息.
     * @return {@link RangedResultSet}{@code <}{@link AppTask}{@code >} 应用任务列表.
     */
    RangedResultSet<AppTask> getTasks(AppTask query, OperationContext context);

    /**
     * 按查询条件获取最新创建的所有任务.
     *
     * @param appSuiteId 应用的唯一标识.
     * @param aippType aipp类型.
     * @param status 状态.
     * @param ctx 操作人上下文信息.
     * @return {@link List}{@code <}{@link AppTask}{@code >} 应用任务列表.
     */
    List<AppTask> getTaskList(String appSuiteId, String aippType, String status, OperationContext ctx);

    /**
     * 按查询条件获取最新创建的所有任务.
     *
     * @param query 查询参数.
     * @param ctx 操作人上下文信息.
     * @return {@link List}{@code <}{@link AppTask}{@code >} 应用任务列表.
     */
    List<AppTask> getTaskList(AppTask query, OperationContext ctx);

    /**
     * 获取preview类型的任务.
     *
     * @param appSuiteId 应用的唯一标识.
     * @param ctx 操作人上下文信息.
     * @return {@link List}{@code <}{@link AppTask}{@code >} 应用任务列表.
     */
    List<AppTask> getPreviewTasks(String appSuiteId, OperationContext ctx);

    /**
     * 按查询条件获取最新创建的所有任务，并按创建时间排序
     *
     * @param appId 应用版本的唯一标识.
     * @param ctx 操作人上下文信息.
     * @return {@link List}{@code <}{@link AppTask}{@code >} 应用任务列表.
     */
    List<AppTask> getTasksByAppId(String appId, OperationContext ctx);

    /**
     * 按查询条件获取最新创建的所有任务.
     *
     * @param appId 应用版本的唯一标识.
     * @param aippType aipp类型.
     * @param ctx 操作人上下文信息.
     * @return {@link List}{@code <}{@link AppTask}{@code >} 应用任务列表.
     */
    List<AppTask> getTasksByAppId(String appId, String aippType, OperationContext ctx);

    /**
     * 通过任务id获取任务.
     *
     * @param taskId 任务id.
     * @param context 操作人上下文信息.
     * @return {@link Optional}{@code <}{@link AppTask}{@code >} 应用任务.
     */
    Optional<AppTask> getTaskById(String taskId, OperationContext context);

    /**
     * 获取任务，如果不存在，则抛出TASK_NOT_FOUND异常.
     *
     * @param taskId 任务id.
     * @param context 操作人上下文.
     * @return {@link AppTask} 任务.
     */
    AppTask retrieveById(String taskId, OperationContext context);
}
