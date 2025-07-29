/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task.service.impl;

import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_UNIQUE_NAME;
import static modelengine.fit.jober.aipp.util.MetaUtils.getAllFromRangedResult;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.TaskQueryEntity;
import modelengine.fit.jober.aipp.domains.task.AppTaskFactory;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.enums.AippSortKeyEnum;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.enums.JaneCategory;
import modelengine.fit.jober.common.RangeResult;
import modelengine.fit.jober.common.RangedResultSet;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用任务服务实现类.
 *
 * @author 张越
 * @since 2025-01-03
 */
@Component
@RequiredArgsConstructor
public class AppTaskServiceImpl implements AppTaskService {
    private static final Logger LOGGER = Logger.get(AppTaskServiceImpl.class);
    private static final int DEFAULT_LIMIT = 10;

    private final MetaService metaService;
    private final AppTaskFactory factory;

    @Override
    public AppTask createTask(AppTask task, OperationContext context) {
        MetaDeclarationInfo declaration = this.factory.toMetaDeclaration(task);
        Meta meta = this.metaService.create(declaration, context);
        return this.factory.create(meta, this);
    }

    @Override
    public void updateTask(AppTask task, OperationContext context) {
        this.metaService.patch(task.getEntity().getTaskId(), this.factory.toMetaDeclaration(task), context);
    }

    @Override
    public void deleteTaskById(String taskId, OperationContext context) {
        this.metaService.delete(taskId, context);
    }

    @Override
    public List<AppTask> getPublishedByPage(String appSuiteId, long offset, int limit, OperationContext context) {
        AppTask task = AppTask.asQueryEntity(offset, limit)
                .addAppSuiteId(appSuiteId)
                .putQueryAttribute(AippConst.ATTR_AIPP_TYPE_KEY, AippTypeEnum.NORMAL.type())
                .putQueryAttribute(AippConst.ATTR_META_STATUS_KEY, AppState.PUBLISHED.getName())
                .addOrderBy(AippSortKeyEnum.UPDATE_AT.name(), DirectionEnum.DESCEND.name())
                .build();
        return this.list(task, context)
                .getResults()
                .stream()
                .map(r -> this.factory.create(r, this))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AppTask> getLatestCreate(String appSuiteId, String aippType, String status, OperationContext ctx) {
        return this.getLatest(AppTask.asQueryEntity(0, 1)
                .latest()
                .addAppSuiteId(appSuiteId)
                .putQueryAttribute(AippConst.ATTR_AIPP_TYPE_KEY, aippType)
                .putQueryAttribute(AippConst.ATTR_META_STATUS_KEY, status)
                .addOrderBy(AippSortKeyEnum.CREATE_AT.name(), DirectionEnum.DESCEND.name())
                .addCategory(JaneCategory.AIPP.name())
                .build(), ctx);
    }

    @Override
    public Optional<AppTask> getLatestCreate(String appSuiteId, String aippType, OperationContext ctx) {
        return this.getLatestCreate(appSuiteId, aippType, null, ctx);
    }

    @Override
    public Optional<AppTask> getLatest(String uniqueName, OperationContext context) {
        RangedResultSet<AppTask> resultSet = this.getTasks(
                AppTask.asQueryEntity(0, 1).latest().putQueryAttribute(ATTR_UNIQUE_NAME, uniqueName).build(),
                context);
        return resultSet.getFirst();
    }

    @Override
    public Optional<AppTask> getLatest(String appSuiteId, String version, OperationContext context) {
        return this.getLatest(AppTask.asQueryEntity(0, 1)
                .latest()
                .addAppSuiteId(appSuiteId)
                .addVersion(version)
                .addOrderBy()
                .addCategory(JaneCategory.AIPP.name())
                .build(), context);
    }

    @Override
    public Optional<AppTask> getLatest(AppTask query, OperationContext context) {
        RangedResultSet<AppTask> resultSet = this.getTasks(query, context);
        return resultSet.getFirst();
    }

    @Override
    public RangedResultSet<AppTask> getTasks(AppTask query, OperationContext context) {
        RangedResultSet<Meta> resultSet = this.list(query, context);
        List<AppTask> tasks = resultSet.getResults().stream().map(r -> this.factory.create(r, this)).toList();
        RangeResult range = resultSet.getRange();
        return RangedResultSet.create(tasks, range.getOffset(), range.getLimit(), range.getTotal());
    }

    @Override
    public List<AppTask> getTaskList(String appSuiteId, String aippType, String status, OperationContext ctx) {
        return getAllFromRangedResult(DEFAULT_LIMIT, (offset) -> {
            AppTask task = AppTask.asQueryEntity(offset, DEFAULT_LIMIT)
                    .addAppSuiteId(appSuiteId)
                    .putQueryAttribute(AippConst.ATTR_AIPP_TYPE_KEY, aippType)
                    .putQueryAttribute(AippConst.ATTR_META_STATUS_KEY, status)
                    .addOrderBy(AippSortKeyEnum.CREATE_AT.name(), DirectionEnum.DESCEND.name())
                    .addCategory(JaneCategory.AIPP.name())
                    .build();
            return this.list(task, ctx);
        }).map(r -> this.factory.create(r, this)).collect(Collectors.toList());
    }

    @Override
    public List<AppTask> getTaskList(AppTask query, OperationContext ctx) {
        TaskQueryEntity entity = query.getEntity();
        Function<Long, RangedResultSet<Meta>> function = (offset) -> this.metaService.list(entity.toMetaFilter(),
                entity.isLatest(), offset, DEFAULT_LIMIT, ctx);
        return getAllFromRangedResult(DEFAULT_LIMIT, function).map(r -> this.factory.create(r, this))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppTask> getPreviewTasks(String appSuiteId, OperationContext ctx) {
        return getAllFromRangedResult(DEFAULT_LIMIT, (offset) -> {
            AppTask task = AppTask.asQueryEntity(offset, DEFAULT_LIMIT)
                    .addAppSuiteId(appSuiteId)
                    .addOrderBy()
                    .putQueryAttribute(AippConst.ATTR_AIPP_TYPE_KEY, AippTypeEnum.PREVIEW.name())
                    .addCategory(JaneCategory.AIPP.name())
                    .build();
            return this.list(task, ctx);
        }).map(r -> this.factory.create(r, this)).collect(Collectors.toList());
    }

    @Override
    public List<AppTask> getTasksByAppId(String appId, OperationContext ctx) {
        return getAllFromRangedResult(DEFAULT_LIMIT, (offset) -> {
            AppTask task = AppTask.asQueryEntity(offset, DEFAULT_LIMIT)
                    .putQueryAttribute(AippConst.ATTR_APP_ID_KEY, appId)
                    .addOrderBy(AippSortKeyEnum.CREATE_AT.name(), DirectionEnum.DESCEND.name())
                    .build();
            return this.list(task, ctx);
        }).map(r -> this.factory.create(r, this)).collect(Collectors.toList());
    }

    @Override
    public List<AppTask> getTasksByAppId(String appId, String aippType, OperationContext ctx) {
        return getAllFromRangedResult(DEFAULT_LIMIT, (offset) -> {
            AppTask task = AppTask.asQueryEntity(offset, DEFAULT_LIMIT)
                    .putQueryAttribute(AippConst.ATTR_APP_ID_KEY, appId)
                    .putQueryAttribute(AippConst.ATTR_AIPP_TYPE_KEY, AippTypeEnum.getType(aippType).type())
                    .build();
            return this.list(task, ctx);
        }).map(r -> this.factory.create(r, this)).collect(Collectors.toList());
    }

    private RangedResultSet<Meta> list(AppTask query, OperationContext context) {
        TaskQueryEntity queryEntity = query.getEntity();
        return this.metaService.list(queryEntity.toMetaFilter(), queryEntity.isLatest(), queryEntity.getOffset(),
                queryEntity.getLimit(), context);
    }

    @Override
    public Optional<AppTask> getTaskById(String taskId, OperationContext context) {
        return Optional.ofNullable(this.metaService.retrieve(taskId, context)).map(r -> this.factory.create(r, this));
    }

    @Override
    public AppTask retrieveById(String taskId, OperationContext context) {
        return this.getTaskById(taskId, context)
                .orElseThrow(() -> {
                    LOGGER.error("The task is not found. [taskId={}]", taskId);
                    return new AippException(AippErrCode.TASK_NOT_FOUND);});
    }
}