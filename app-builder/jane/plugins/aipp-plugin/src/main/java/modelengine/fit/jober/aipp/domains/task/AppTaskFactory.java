/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

import static modelengine.fit.jane.Undefinable.defined;
import static modelengine.fit.jober.aipp.util.UsefulUtils.doIfNotNull;

import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jober.aipp.domains.log.repository.AippLogRepository;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.entity.task.TaskProperty;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link AppTask} 的工厂类
 *
 * @author 张越
 * @since 2025-01-03
 */
@Component
@RequiredArgsConstructor
public class AppTaskFactory {
    private final AippLogRepository aippLogRepository;
    private final AppTaskInstanceService appTaskInstanceService;
    private final FlowsService flowsService;
    private final AppChatSessionService appChatSessionService;
    private final FlowInstanceService flowInstanceService;
    private final AppBuilderFormPropertyRepository appBuilderFormPropertyRepository;
    private final AopAippLogService aopAippLogService;
    private final AppChatSseService appChatSseService;

    /**
     * 将 {@link AppTask} 转换为 {@link MetaDeclarationInfo} 对象.
     *
     * @param task {@link AppTask} 对象.
     * @return {@link MetaDeclarationInfo} 对象.
     */
    public MetaDeclarationInfo toMetaDeclaration(AppTask task) {
        MetaDeclarationInfo info = new MetaDeclarationInfo();
        doIfNotNull(task.getEntity().getAppSuiteId(), v -> info.setBasicMetaTemplateId(defined(v)));
        doIfNotNull(task.getEntity().getName(), v -> info.setName(defined(v)));
        doIfNotNull(task.getEntity().getVersion(), v -> info.setVersion(defined(v)));
        doIfNotNull(task.getEntity().getCategory(), v -> info.setCategory(defined(v)));

        // 设置attributes.
        task.getEntity().visitAttributes(info::putAttribute);

        // 设置properties.
        List<MetaPropertyDeclarationInfo> metaProperties = task.getEntity()
                .getProperties()
                .stream()
                .map(this::toMetaProperty)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(metaProperties)) {
            info.setProperties(defined(metaProperties));
        }
        return info;
    }

    private MetaPropertyDeclarationInfo toMetaProperty(TaskProperty property) {
        MetaPropertyDeclarationInfo metaProperty = new MetaPropertyDeclarationInfo();
        metaProperty.setName(defined(property.getName()));
        metaProperty.setDataType(defined(property.getDataType()));
        metaProperty.setDescription(defined(property.getDescription()));
        return metaProperty;
    }

    /**
     * 通过 {@link Meta} 和任务id创建一个实例对象.
     *
     * @param meta 任务对象.
     * @param appTaskService 任务服务对象.
     * @return {@link AppTask} 对象.
     */
    public AppTask create(Meta meta, AppTaskService appTaskService) {
        AppTask appTask = new AppTask(this.aippLogRepository, this.appTaskInstanceService, this.flowsService,
                this.appChatSessionService, this.flowInstanceService, appTaskService,
                this.appBuilderFormPropertyRepository, this.aopAippLogService, this.appChatSseService);
        appTask.getEntity().loadFrom(meta);
        return appTask;
    }
}
