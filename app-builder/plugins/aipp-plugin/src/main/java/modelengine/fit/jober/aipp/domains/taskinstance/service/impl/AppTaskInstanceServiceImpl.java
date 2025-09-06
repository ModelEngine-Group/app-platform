/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstanceFactory;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.util.MetaUtils;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 应用任务实例服务实现类.
 *
 * @author 张越
 * @since 2024-12-31
 */
@Component
@RequiredArgsConstructor
public class AppTaskInstanceServiceImpl implements AppTaskInstanceService {
    private final MetaInstanceService metaInstanceService;
    private final AppTaskInstanceFactory factory;

    @Override
    public Optional<AppTaskInstance> getInstance(String taskId, String taskInstanceId, OperationContext context) {
        Instance metaInst = this.metaInstanceService.retrieveById(taskInstanceId, context);
        return Optional.ofNullable(metaInst).map(i -> this.factory.create(i, taskId, this));
    }

    @Override
    public List<AppTaskInstance> getInstancesByTaskId(String taskId, int limit, OperationContext context) {
        return this.getInstanceStreamByTaskId(taskId, limit, context).collect(Collectors.toList());
    }

    @Override
    public Stream<AppTaskInstance> getInstanceStreamByTaskId(String taskId, int limit, OperationContext context) {
        return MetaUtils.getAllFromRangedResult(limit, os -> metaInstanceService.list(taskId, os, limit, context))
                .map(instance -> this.factory.create(instance, taskId, this));
    }

    @Override
    public void update(AppTaskInstance instance, OperationContext context) {
        InstanceDeclarationInfo declarationInfo = this.factory.toDeclarationInfo(instance);
        this.metaInstanceService.patchMetaInstance(instance.getTaskId(), instance.getId(), declarationInfo, context);
    }

    @Override
    public AppTaskInstance createInstance(AppTaskInstance instance, OperationContext context) {
        InstanceDeclarationInfo declarationInfo = this.factory.toDeclarationInfo(instance);
        Instance metaInst = this.metaInstanceService.createMetaInstance(instance.getTaskId(), declarationInfo, context);
        return this.factory.create(metaInst, instance.getTaskId(), this);
    }

    @Override
    public void delete(String taskId, String taskInstanceId, OperationContext context) {
        this.metaInstanceService.deleteMetaInstance(taskId, taskInstanceId, context);
    }

    @Override
    public String getTaskId(String taskInstanceId) {
        return this.metaInstanceService.getMetaVersionId(taskInstanceId);
    }

    @Override
    public Optional<AppTaskInstance> getInstanceById(String taskInstanceId, OperationContext context) {
        String taskId = this.getTaskId(taskInstanceId);
        Instance metaInst = this.metaInstanceService.retrieveById(taskInstanceId, context);
        return Optional.ofNullable(metaInst).map(i -> this.factory.create(i, taskId, this));
    }
}