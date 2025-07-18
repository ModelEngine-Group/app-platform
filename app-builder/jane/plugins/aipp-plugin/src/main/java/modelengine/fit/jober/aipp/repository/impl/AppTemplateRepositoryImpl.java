/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.condition.TemplateQueryCondition;
import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.mapper.AppTemplateMapper;
import modelengine.fit.jober.aipp.repository.AppTemplateRepository;
import modelengine.fit.jober.aipp.serializer.impl.AppTemplateSerializer;
import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AppTemplateRepository 接口的实现类。
 *
 * @author 方誉州
 * @since 2025-01-02
 */
@Component
public class AppTemplateRepositoryImpl implements AppTemplateRepository {
    private final AppTemplateMapper appTemplateMapper;
    private final AppTemplateSerializer serializer;

    public AppTemplateRepositoryImpl(AppTemplateMapper appTemplateMapper, IconConverter iconConverter) {
        this.appTemplateMapper = appTemplateMapper;
        this.serializer = new AppTemplateSerializer(iconConverter);
    }

    @Override
    public List<AppTemplate> selectWithCondition(TemplateQueryCondition cond) {
        return this.appTemplateMapper.selectWithCondition(cond).stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public int countWithCondition(TemplateQueryCondition cond) {
        return this.appTemplateMapper.countWithCondition(cond);
    }

    @Override
    public AppTemplate selectWithId(String templateId) {
        return this.serializer.deserialize(this.appTemplateMapper.selectWithId(templateId));
    }

    @Override
    public void insertOne(AppTemplate appTemplate) {
        this.appTemplateMapper.insertOne(this.serializer.serialize(appTemplate));
    }

    @Override
    public void deleteOne(String templateId) {
        this.appTemplateMapper.deleteOne(templateId);
    }

    @Override
    public void increaseUsage(String templateId) {
        this.appTemplateMapper.increaseUsage(templateId);
    }

    @Override
    public void updateLike(String templateId, long delta) {
        this.appTemplateMapper.updateLike(templateId, delta);
    }

    @Override
    public void updateCollection(String templateId, long delta) {
        this.appTemplateMapper.updateCollection(templateId, delta);
    }
}
