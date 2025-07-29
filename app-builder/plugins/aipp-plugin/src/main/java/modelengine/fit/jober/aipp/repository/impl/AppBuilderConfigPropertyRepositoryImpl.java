/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderConfigProperty;
import modelengine.fit.jober.aipp.mapper.AppBuilderConfigPropertyMapper;
import modelengine.fit.jober.aipp.po.AppBuilderConfigPropertyPo;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.serializer.impl.AppBuilderConfigPropertySerializer;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用属性表单仓库实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
public class AppBuilderConfigPropertyRepositoryImpl implements AppBuilderConfigPropertyRepository {
    private final AppBuilderConfigPropertyMapper appBuilderConfigPropertyMapper;

    private final AppBuilderConfigPropertySerializer serializer;

    public AppBuilderConfigPropertyRepositoryImpl(AppBuilderConfigPropertyMapper appBuilderConfigPropertyMapper) {
        this.appBuilderConfigPropertyMapper = appBuilderConfigPropertyMapper;
        this.serializer = new AppBuilderConfigPropertySerializer();
    }

    @Override
    public List<AppBuilderConfigProperty> selectWithConfigId(String configId) {
        return this.appBuilderConfigPropertyMapper.selectWithConfigId(configId)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public AppBuilderConfigProperty selectWithId(String id) {
        return this.serializer.deserialize(this.appBuilderConfigPropertyMapper.selectWithId(id));
    }

    @Override
    public void insertOne(AppBuilderConfigProperty appBuilderConfigProperty) {
        this.appBuilderConfigPropertyMapper.insertOne(this.serializer.serialize(appBuilderConfigProperty));
    }

    @Override
    public void insertMore(List<AppBuilderConfigProperty> appBuilderConfigProperties) {
        List<AppBuilderConfigPropertyPo> pos = appBuilderConfigProperties.stream()
                .map(this.serializer::serialize)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pos)) {
            this.appBuilderConfigPropertyMapper.insertMore(pos);
        }
    }

    @Override
    public void updateOne(AppBuilderConfigProperty appBuilderConfigProperty) {
        this.appBuilderConfigPropertyMapper.updateOne(this.serializer.serialize(appBuilderConfigProperty));
    }

    @Override
    public int deleteMore(List<String> ids) {
        return CollectionUtils.isEmpty(ids) ? 0 : this.appBuilderConfigPropertyMapper.deleteMore(ids);
    }

    @Override
    public void deleteByConfigIds(List<String> configIds) {
        this.appBuilderConfigPropertyMapper.deleteByConfigIds(configIds);
    }
}
