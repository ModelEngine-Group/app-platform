/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.mapper;

import modelengine.jade.knowledge.po.KnowledgeConfigPo;

import java.util.List;

/**
 * 表示知识库配置 Mapper。
 *
 * @author 陈潇文
 * @since 2025-04-22
 */
public interface KnowledgeConfigMapper {
    /**
     * 插入一条知识库配置信息。
     *
     * @param knowledgeConfigPo 表示知识库配置数据的 {@link KnowledgeConfigPo}。
     */
    void insert(KnowledgeConfigPo knowledgeConfigPo);

    /**
     * 更新一条知识库配置信息。
     * @param knowledgeConfigPo 表示知识库配置数据的 {@link KnowledgeConfigPo}。
     */
    void update(KnowledgeConfigPo knowledgeConfigPo);

    /**
     * 删除一条知识库配置信息。
     * @param id 表示知识库配置id的 {@link int}。
     */
    void deleteById(int id);

    /**
     * 根据用户id查询用户的知识库配置列表。
     * @param userId 表示用户id的 {@link String}。
     * @return 该用户可用的知识库配置列表 {@link List}{@code <}{@link KnowledgeConfigPo}{@code >}。
     */
    List<KnowledgeConfigPo> listByUserId(String userId);
}
