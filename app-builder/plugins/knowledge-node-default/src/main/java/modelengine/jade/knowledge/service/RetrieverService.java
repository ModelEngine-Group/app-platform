/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.service;

import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.entity.RetrieverServiceOption;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 检索节点服务。
 *
 * @author 刘信宏
 * @since 2024-09-27
 */
public interface RetrieverService {
    /**
     * 检索文档。
     *
     * @param query 表示问题内容的 {@link Object}。
     * @param knowledgeRepos 表示知识库标识列表的 {@link List}{@link <}{@link KnowledgeRepoInfo}{@link >}。
     * @param option 表示检索配置的 {@link RetrieverServiceOption}。
     * @return 表示文档内容的 {@link List}{@code <}{@link KnowledgeDocument}{@code >}。
     */
    @Genericable("modelengine.jade.knowledge.service.retrieve")
    List<KnowledgeDocument> invoke(Object query, List<KnowledgeRepoInfo> knowledgeRepos,
            RetrieverServiceOption option);
}
