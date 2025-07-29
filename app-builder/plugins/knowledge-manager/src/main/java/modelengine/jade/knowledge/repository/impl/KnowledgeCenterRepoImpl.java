/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.repository.impl;

import modelengine.fitframework.annotation.Component;
import modelengine.jade.knowledge.condition.KnowledgeConfigQueryCondition;
import modelengine.jade.knowledge.mapper.KnowledgeConfigMapper;
import modelengine.jade.knowledge.po.KnowledgeConfigPo;
import modelengine.jade.knowledge.repository.KnowledgeCenterRepo;

import java.util.List;

/**
 * 表示用户知识库配置操作数据库接口 {@link KnowledgeCenterRepo} 的实现。
 *
 * @author 陈潇文
 * @since 2025-04-22
 */
@Component
public class KnowledgeCenterRepoImpl implements KnowledgeCenterRepo {
    private final KnowledgeConfigMapper knowledgeConfigMapper;

    public KnowledgeCenterRepoImpl(KnowledgeConfigMapper knowledgeConfigMapper) {
        this.knowledgeConfigMapper = knowledgeConfigMapper;
    }

    @Override
    public void insertKnowledgeConfig(KnowledgeConfigPo knowledgeConfigPo) {
        this.knowledgeConfigMapper.insert(knowledgeConfigPo);
    }

    @Override
    public void updateKnowledgeConfig(KnowledgeConfigPo knowledgeConfigPo) {
        this.knowledgeConfigMapper.update(knowledgeConfigPo);
    }

    @Override
    public void deleteKnowledgeConfigById(Long id) {
        this.knowledgeConfigMapper.deleteById(id);
    }

    @Override
    public List<KnowledgeConfigPo> listKnowledgeConfigByCondition(KnowledgeConfigQueryCondition cond) {
        return this.knowledgeConfigMapper.listByCondition(cond);
    }

    @Override
    public void updateOthersIsDefaultFalse(KnowledgeConfigQueryCondition cond) {
        this.knowledgeConfigMapper.updateOthersIsDefaultFalse(cond);
    }

    @Override
    public void updateNewestIsDefaultTrue(KnowledgeConfigQueryCondition cond) {
        this.knowledgeConfigMapper.updateNewestIsDefaultTrue(cond);
    }
}
