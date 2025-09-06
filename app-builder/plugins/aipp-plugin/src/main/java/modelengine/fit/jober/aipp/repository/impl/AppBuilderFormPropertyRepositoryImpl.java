/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.mapper.AppBuilderFormPropertyMapper;
import modelengine.fit.jober.aipp.po.AppBuilderFormPropertyPo;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.serializer.impl.AppBuilderFormPropertySerializer;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用表单属性仓库功能实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
public class AppBuilderFormPropertyRepositoryImpl implements AppBuilderFormPropertyRepository {
    private final AppBuilderFormPropertyMapper appBuilderFormPropertyMapper;

    private final AppBuilderFormPropertySerializer serializer;

    public AppBuilderFormPropertyRepositoryImpl(AppBuilderFormPropertyMapper appBuilderFormPropertyMapper) {
        this.appBuilderFormPropertyMapper = appBuilderFormPropertyMapper;
        this.serializer = new AppBuilderFormPropertySerializer();
    }

    @Override
    public List<AppBuilderFormProperty> selectWithFormId(String formId) {
        return this.appBuilderFormPropertyMapper.selectWithFormId(formId)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppBuilderFormProperty> selectWithAppId(String appId) {
        return this.appBuilderFormPropertyMapper.selectWithAppId(appId)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public AppBuilderFormProperty selectWithId(String id) {
        return this.serializer.deserialize(this.appBuilderFormPropertyMapper.selectWithId(id));
    }

    @Override
    public void insertOne(AppBuilderFormProperty appBuilderFormProperty) {
        this.appBuilderFormPropertyMapper.insertOne(this.serializer.serialize(appBuilderFormProperty));
    }

    @Override
    public void insertMore(List<AppBuilderFormProperty> appBuilderFormProperties) {
        List<AppBuilderFormPropertyPo> pos = appBuilderFormProperties.stream()
                .map(this.serializer::serialize)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pos)) {
            this.appBuilderFormPropertyMapper.insertMore(pos);
        }
    }

    @Override
    public void updateOne(AppBuilderFormProperty appBuilderFormProperty) {
        this.appBuilderFormPropertyMapper.updateOne(this.serializer.serialize(appBuilderFormProperty));
    }

    @Override
    public void updateMany(List<AppBuilderFormProperty> appBuilderFormProperties) {
        List<AppBuilderFormPropertyPo> pos = appBuilderFormProperties.stream()
                .map(this.serializer::serialize)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(appBuilderFormProperties)) {
            this.appBuilderFormPropertyMapper.updateMany(pos);
        }
    }

    @Override
    public void deleteMore(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        this.appBuilderFormPropertyMapper.deleteMore(ids);
    }

    @Override
    public void deleteByFormId(List<String> formIds) {
        if (CollectionUtils.isEmpty(formIds)) {
            return;
        }
        this.appBuilderFormPropertyMapper.deleteByFormIds(formIds);
    }

    @Override
    public void deleteByAppIds(List<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return;
        }
        this.appBuilderFormPropertyMapper.deleteByAppIds(appIds);
    }
}
