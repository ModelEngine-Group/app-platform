/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new;

import static modelengine.fit.jober.aipp.constants.AippConst.TASK_ID_KEY;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.task_new.condition.MetaInstanceCondition;
import modelengine.fit.task_new.converter.ConvertorUtils;
import modelengine.fit.task_new.entity.MetaInstance;
import modelengine.fit.task_new.repository.MetaInstanceRepository;
import modelengine.fit.task_new.util.UUIDUtil;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;
import modelengine.fitframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Meta 实例服务层实现
 *
 * @author 邬涨财
 * @since 2025-03-31
 */
@Component
public class MetaInstanceServiceImpl implements MetaInstanceService {
    private static final Logger log = Logger.get(MetaInstanceServiceImpl.class);

    private static final int LIMIT = 1000;

    private final MetaInstanceRepository metaInstanceRepository;

    private final int expiredDays;

    public MetaInstanceServiceImpl(MetaInstanceRepository metaInstanceRepository,
            @Value("${task.expiredDays}") int expiredDays) {
        this.metaInstanceRepository = metaInstanceRepository;
        this.expiredDays = expiredDays;
    }

    @Override
    public Instance createMetaInstance(String versionId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context) {
        MetaInstance instance = ConvertorUtils.toMetaInstance(instanceDeclarationInfo, context);
        instance.setTaskId(versionId);
        String instanceId = UUIDUtil.uuid();
        instance.setId(instanceId);
        this.metaInstanceRepository.insertOne(instance);
        return ConvertorUtils.toInstance(instanceId, instanceDeclarationInfo);
    }

    @Override
    public void patchMetaInstance(String versionId, String instanceId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context) {
        MetaInstance instance = ConvertorUtils.toMetaInstance(instanceDeclarationInfo, context);
        instance.setTaskId(versionId);
        instance.setId(instanceId);
        this.metaInstanceRepository.updateOne(instance);
    }

    @Override
    public void deleteMetaInstance(String versionId, String instanceId, OperationContext context) {
        this.metaInstanceRepository.delete(Collections.singletonList(instanceId));
    }

    @Override
    public RangedResultSet<Instance> list(String versionId, MetaInstanceFilter filter, long offset, int limit,
            OperationContext context) {
        throw new IllegalStateException("Unsupported function");
    }

    @Override
    public RangedResultSet<Instance> list(String versionId, long offset, int limit, OperationContext context) {
        MetaInstanceCondition.MetaInstanceConditionBuilder builder =
                MetaInstanceCondition.builder().metaIds(Collections.singletonList(versionId));
        return this.getInstances(builder, offset, limit);
    }

    @Override
    public RangedResultSet<Instance> list(List<String> ids, long offset, int limit, OperationContext context) {
        MetaInstanceCondition.MetaInstanceConditionBuilder builder = MetaInstanceCondition.builder().ids(ids);
        return this.getInstances(builder, offset, limit);
    }

    private RangedResultSet<Instance> getInstances(MetaInstanceCondition.MetaInstanceConditionBuilder builder,
            long offset, int limit) {
        MetaInstanceCondition condition = builder.offset(offset).limit(limit).build();
        List<MetaInstance> metaInstances = this.metaInstanceRepository.select(condition);
        long count = this.metaInstanceRepository.count(condition);
        List<Instance> instances = metaInstances.stream().map(ConvertorUtils::toInstance).toList();
        return RangedResultSet.create(instances, offset, limit, count);
    }

    @Override
    public String getMetaVersionId(String id) {
        RangedResultSet<Instance> instances = this.list(Collections.singletonList(id), 0, 1, null);
        Map<String, String> info = instances.getResults().get(0).getInfo();
        return info.get(TASK_ID_KEY);
    }

    @Override
    public Instance retrieveById(String instanceId, OperationContext context) {
        RangedResultSet<Instance> resultSet = this.list(Collections.singletonList(instanceId), 0, 1, null);
        return resultSet.getResults().get(0);
    }

    /**
     * 每天凌晨 3 点定时清理超期指定天数的任务实例数据。
     */
    @Scheduled(strategy = Scheduled.Strategy.CRON, value = "0 0 3 * * ?")
    public void taskInstanceDbCleanSchedule() {
        log.info("Start clean task instance db");
        try {
            while (true) {
                List<String> expiredInstanceIds = this.metaInstanceRepository.getExpiredInstanceIds(expiredDays, LIMIT);
                if (CollectionUtils.isEmpty(expiredInstanceIds)) {
                    break;
                }
                this.metaInstanceRepository.forceDelete(expiredInstanceIds);
            }
        } catch (Exception e) {
            log.error("Error clean task instance db, exception:", e);
        }
        log.info("Finish clean task instance db");
    }
}
