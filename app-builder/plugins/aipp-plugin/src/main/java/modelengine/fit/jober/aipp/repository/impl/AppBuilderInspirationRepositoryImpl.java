/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.condition.InspirationQueryCondition;
import modelengine.fit.jober.aipp.mapper.AppBuilderInspirationMapper;
import modelengine.fit.jober.aipp.po.InspirationPo;
import modelengine.fit.jober.aipp.repository.AppBuilderInspirationRepository;
import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.Optional;

/**
 * AppBuilderInspirationRepository 实现类.
 *
 * @author 陈潇文
 * @since 2024-10-19
 */
@Component
public class AppBuilderInspirationRepositoryImpl implements AppBuilderInspirationRepository {
    private final AppBuilderInspirationMapper inspirationMapper;

    public AppBuilderInspirationRepositoryImpl(AppBuilderInspirationMapper inspirationMapper) {
        this.inspirationMapper = inspirationMapper;
    }

    @Override
    public Optional<String> findCustomCategoryId(String aippId, String parentId, String user) {
        return this.inspirationMapper.findCustomCategoryId(aippId, parentId, user);
    }

    @Override
    public void addCustomInspiration(InspirationPo inspirationPo) {
        this.inspirationMapper.insertOne(inspirationPo);
    }

    @Override
    public void updateCustomInspiration(String inspirationId, InspirationPo inspirationPo) {
        this.inspirationMapper.updateOne(inspirationId, inspirationPo);
    }

    @Override
    public void deleteCustomInspiration(String aippId, String categoryId, String inspirationId, String createUser) {
        this.inspirationMapper.deleteOne(aippId, categoryId, inspirationId, createUser);
    }

    @Override
    public List<InspirationPo> selectWithCondition(InspirationQueryCondition condition) {
        return this.inspirationMapper.selectWithCondition(condition);
    }
}
