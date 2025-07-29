/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new.repository.impl;

import modelengine.fit.task_new.condition.MetaInstanceCondition;
import modelengine.fit.task_new.entity.MetaInstance;
import modelengine.fit.task_new.mapper.MetaInstanceMapper;
import modelengine.fit.task_new.repository.MetaInstanceRepository;
import modelengine.fit.task_new.serializer.impl.MetaInstanceSerializer;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Meta 实例数据库 Repo 层实现。
 *
 * @author 邬涨财
 * @since 2025-03-31
 */
@Component
public class MetaInstanceRepositoryImpl implements MetaInstanceRepository {
    private final MetaInstanceMapper metaInstanceMapper;
    private final MetaInstanceSerializer serializer;

    public MetaInstanceRepositoryImpl(MetaInstanceMapper metaInstanceMapper) {
        this.metaInstanceMapper = metaInstanceMapper;
        this.serializer = new MetaInstanceSerializer();
    }

    @Override
    public void insertOne(MetaInstance metaInstance) {
        this.metaInstanceMapper.insertOne(this.serializer.serialize(metaInstance));
    }

    @Override
    public void updateOne(MetaInstance metaInstance) {
        this.metaInstanceMapper.updateOne(this.serializer.serialize(metaInstance));
    }

    @Override
    public void delete(List<String> ids) {
        this.metaInstanceMapper.delete(ids);
    }

    @Override
    public List<MetaInstance> select(MetaInstanceCondition cond) {
        return this.metaInstanceMapper.select(cond)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public long count(MetaInstanceCondition cond) {
        return this.metaInstanceMapper.count(cond);
    }

    @Override
    public List<String> getExpiredInstanceIds(int expiredDays, int limit) {
        return this.metaInstanceMapper.getExpiredInstanceIds(expiredDays, limit);
    }

    @Override
    public void forceDelete(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        this.metaInstanceMapper.forceDelete(ids);
    }
}
