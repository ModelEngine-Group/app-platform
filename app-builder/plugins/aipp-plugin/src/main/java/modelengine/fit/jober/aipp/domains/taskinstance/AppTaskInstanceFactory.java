/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jober.aipp.domains.log.repository.AippLogRepository;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.service.AppChatSseService;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.client.BrokerClient;

/**
 * {@link AppTaskInstance} 的工厂类
 *
 * @author 张越
 * @since 2024-12-31
 */
@Component
@RequiredArgsConstructor
public class AppTaskInstanceFactory {
    private final FlowInstanceService flowInstanceService;
    private final BrokerClient client;
    private final AppChatSseService appChatSSEService;
    private final AippChatMapper aippChatMapper;
    private final AippLogRepository aippLogRepository;

    /**
     * 将 {@link AppTaskInstance} 转换为 {@link InstanceDeclarationInfo} 对象.
     *
     * @param taskInstance {@link AppTaskInstance} 对象.
     * @return {@link InstanceDeclarationInfo} 对象.
     */
    public InstanceDeclarationInfo toDeclarationInfo(AppTaskInstance taskInstance) {
        return InstanceDeclarationInfo.custom()
                .info(taskInstance.getEntity().getInfos())
                .tags(taskInstance.getEntity().getTags())
                .build();
    }

    /**
     * 通过 {@link Instance} 和任务id创建一个实例对象.
     *
     * @param instance 任务实例对象.
     * @param taskId 任务id.
     * @param appTaskInstanceService 任务实例服务类.
     * @return {@link AppTaskInstance} 对象.
     */
    public AppTaskInstance create(Instance instance, String taskId, AppTaskInstanceService appTaskInstanceService) {
        AppTaskInstance appTaskInstance = new AppTaskInstance(appTaskInstanceService,
                this.flowInstanceService, this.client, this.appChatSSEService, this.aippChatMapper,
                this.aippLogRepository);
        appTaskInstance.getEntity().putInfos(instance.getInfo()).putTags(instance.getTags());
        appTaskInstance.setTaskId(taskId);
        appTaskInstance.setId(instance.getId());
        return appTaskInstance;
    }
}
