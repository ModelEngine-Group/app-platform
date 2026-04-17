/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.domain.EndNodeStatus;
import modelengine.fit.jober.aipp.mapper.EndNodeStatusMapper;
import modelengine.fit.jober.aipp.repository.EndNodeStatusRepository;
import modelengine.fit.jober.aipp.serializer.impl.EndNodeStatusSerializer;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * EndNodeStatusRepository 实现类.
 *
 * @author 张越
 * @since 2026-04-01
 */
@Component
public class EndNodeStatusRepositoryImpl implements EndNodeStatusRepository {
    private final EndNodeStatusMapper mapper;
    private final EndNodeStatusSerializer serializer;

    public EndNodeStatusRepositoryImpl(EndNodeStatusMapper mapper) {
        this.mapper = mapper;
        this.serializer = new EndNodeStatusSerializer();
    }

    @Override
    public List<EndNodeStatus> selectByTraceId(String traceId) {
        return this.mapper.selectByTraceId(traceId)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public List<EndNodeStatus> selectByInstanceId(String instanceId) {
        return this.mapper.selectByInstanceId(instanceId)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public void insertOne(EndNodeStatus endNodeStatus) {
        this.mapper.insertOne(this.serializer.serialize(endNodeStatus));
    }

    @Override
    public List<Long> getExpiredEndNodeStatuses(int expiredDays, int limit) {
        return this.mapper.getExpiredEndNodeStatuses(expiredDays, limit);
    }

    @Override
    public void deleteEndNodeStatuses(List<Long> endNodeStatusIds) {
        if (CollectionUtils.isEmpty(endNodeStatusIds)) {
            return;
        }
        this.mapper.deleteEndNodeStatuses(endNodeStatusIds);
    }
}
