/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.repository;

import modelengine.jade.knowledge.condition.KnowledgeConfigQueryCondition;
import modelengine.jade.knowledge.po.KnowledgeConfigPo;

import java.util.List;

/**
 * 表示用户知识库配置操作数据库接口
 *
 * @author 陈潇文
 * @since 2025-04-22
 */
public interface KnowledgeCenterRepo {
    /**
     * 插入一条知识库配置信息。
     *
     * @param knowledgeConfigPo 表示知识库配置数据的 {@link KnowledgeConfigPo}。
     */
    void insertKnowledgeConfig(KnowledgeConfigPo knowledgeConfigPo);

    /**
     * 更新一条知识库配置信息。
     * @param knowledgeConfigPo 表示知识库配置数据的 {@link KnowledgeConfigPo}。
     */
    void updateKnowledgeConfig(KnowledgeConfigPo knowledgeConfigPo);

    /**
     * 删除一条知识库配置信息。
     * @param id 表示知识库配置id的 {@link int}。
     */
    void deleteKnowledgeConfigById(int id);

    /**
     * 根据用户id查询用户的知识库配置列表。
     * @param cond 表示查询条件的 {@link KnowledgeConfigQueryCondition}。
     * @return 该用户可用的知识库配置列表 {@link List}{@code <}{@link KnowledgeConfigPo}{@code >}。
     */
    List<KnowledgeConfigPo> listKnowledgeConfigByCondition(KnowledgeConfigQueryCondition cond);
}
