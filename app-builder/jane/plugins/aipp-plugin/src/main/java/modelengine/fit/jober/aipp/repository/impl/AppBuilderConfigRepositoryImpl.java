/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.mapper.AppBuilderConfigMapper;
import modelengine.fit.jober.aipp.po.AppBuilderConfigPo;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.serializer.impl.AppBuilderConfigSerializer;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用属性仓库实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
public class AppBuilderConfigRepositoryImpl implements AppBuilderConfigRepository {
    private final AppBuilderConfigMapper appBuilderConfigMapper;
    private final AppBuilderConfigSerializer serializer;
    private final AppBuilderConfigPropertyRepository appBuilderConfigPropertyRepository;

    public AppBuilderConfigRepositoryImpl(AppBuilderConfigMapper appBuilderConfigMapper,
            AppBuilderConfigPropertyRepository appBuilderConfigPropertyRepository) {
        this.appBuilderConfigMapper = appBuilderConfigMapper;
        this.appBuilderConfigPropertyRepository = appBuilderConfigPropertyRepository;
        this.serializer = new AppBuilderConfigSerializer();
    }

    @Override
    public AppBuilderConfig selectWithId(String id) {
        return this.serializer.deserialize(this.appBuilderConfigMapper.selectWithId(id));
    }

    @Override
    public void insertOne(AppBuilderConfig appBuilderConfig) {
        this.appBuilderConfigMapper.insertOne(this.serializer.serialize(appBuilderConfig));
    }

    @Override
    public void updateOne(AppBuilderConfig appBuilderConfig) {
        this.appBuilderConfigMapper.updateOne(this.serializer.serialize(appBuilderConfig));
    }

    @Override
    public AppBuilderConfig selectWithAppId(String appId) {
        return this.serializer.deserialize(this.appBuilderConfigMapper.selectWithAppId(appId));
    }

    @Override
    public void delete(List<String> ids) {
        List<AppBuilderConfigPo> configPos = this.appBuilderConfigMapper.selectWithIds(ids);
        List<String> configIds = configPos.stream().map(AppBuilderConfigPo::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(configIds)) {
            this.appBuilderConfigMapper.delete(configIds);
            this.appBuilderConfigPropertyRepository.deleteByConfigIds(configIds);
        }
    }
}
