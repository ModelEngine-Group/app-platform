/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.condition.FormQueryCondition;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.mapper.AppBuilderFormMapper;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.serializer.impl.AppBuilderFormSerializer;
import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 应用表单仓库功能实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
public class AppBuilderFormRepositoryImpl implements AppBuilderFormRepository {
    private final AppBuilderFormMapper appBuilderFormMapper;
    private final AppBuilderFormSerializer serializer;
    private final AppBuilderFormPropertyRepository formPropertyRepository;

    public AppBuilderFormRepositoryImpl(AppBuilderFormMapper appBuilderFormMapper,
            AppBuilderFormPropertyRepository formPropertyRepository) {
        this.appBuilderFormMapper = appBuilderFormMapper;
        this.formPropertyRepository = formPropertyRepository;
        this.serializer = new AppBuilderFormSerializer();
    }

    @Override
    public AppBuilderForm selectWithId(String id) {
        AppBuilderForm appBuilderForm = this.serializer.deserialize(this.appBuilderFormMapper.selectWithId(id));
        if (appBuilderForm == null) {
            return null;
        }
        appBuilderForm.setFormPropertyRepository(this.formPropertyRepository);
        return appBuilderForm;
    }

    @Override
    public List<AppBuilderForm> selectWithType(String type, String tenantId) {
        return this.appBuilderFormMapper.selectWithType(type, tenantId)
                .stream()
                .filter(Objects::nonNull)
                .map(appBuilderFormPO -> {
                    AppBuilderForm appBuilderForm = this.serializer.deserialize(appBuilderFormPO);
                    appBuilderForm.setFormPropertyRepository(this.formPropertyRepository);
                    return appBuilderForm;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void insertOne(AppBuilderForm appBuilderForm) {
        this.appBuilderFormMapper.insertOne(this.serializer.serialize(appBuilderForm));
    }

    @Override
    public void updateOne(AppBuilderForm appBuilderForm) {
        this.appBuilderFormMapper.updateOne(this.serializer.serialize(appBuilderForm));
    }

    @Override
    public void delete(List<String> ids) {
        this.appBuilderFormMapper.delete(ids);
    }

    @Override
    public long countWithType(String type, String tenantId) {
        return this.appBuilderFormMapper.countWithType(type, tenantId);
    }

    @Override
    public AppBuilderForm selectWithName(String name, String tenantId) {
        return this.serializer.deserialize(this.appBuilderFormMapper.selectWithName(name, tenantId));
    }

    @Override
    public List<AppBuilderForm> selectWithCondition(FormQueryCondition cond) {
        return this.appBuilderFormMapper.selectWithCondition(cond)
                .stream()
                .filter(Objects::nonNull)
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public long countWithCondition(FormQueryCondition cond) {
        return this.appBuilderFormMapper.countWithCondition(cond);
    }
}
