/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import modelengine.fit.jober.aipp.mapper.AppBuilderRuntimeInfoMapper;
import modelengine.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import modelengine.fit.jober.aipp.serializer.impl.AppBuilderRuntimeInfoSerializer;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AppBuilderRuntimeInfoRepository 实现类.
 *
 * @author 张越
 * @since 2024-07-29
 */
@Component
public class AppBuilderRuntimeInfoRepositoryImpl implements AppBuilderRuntimeInfoRepository {
    private final AppBuilderRuntimeInfoMapper mapper;
    private final AppBuilderRuntimeInfoSerializer serializer;

    public AppBuilderRuntimeInfoRepositoryImpl(AppBuilderRuntimeInfoMapper mapper) {
        this.mapper = mapper;
        this.serializer = new AppBuilderRuntimeInfoSerializer();
    }

    @Override
    public List<AppBuilderRuntimeInfo> selectByTraceId(String traceId) {
        return this.mapper.selectByTraceId(traceId)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public void insertOne(AppBuilderRuntimeInfo info) {
        this.mapper.insertOne(this.serializer.serialize(info));
    }

    @Override
    public List<Long> getExpiredRuntimeInfos(int expiredDays, int limit) {
        return this.mapper.getExpiredRuntimeInfos(expiredDays, limit);
    }

    @Override
    public void deleteRuntimeInfos(List<Long> runtimeInfoIds) {
        if (CollectionUtils.isEmpty(runtimeInfoIds)) {
            return;
        }
        this.mapper.deleteRuntimeInfos(runtimeInfoIds);
    }
}
