/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import modelengine.fel.tool.annotation.Group;
import modelengine.fel.tool.annotation.ToolMethod;
import modelengine.jade.common.vo.PageVo;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 知识库服务。
 *
 * @author 刘信宏
 * @since 2024-09-09
 */
@Group(name = KnowledgeRepoService.STORE_DEF_GROUP_KNOWLEDGE)
public interface KnowledgeRepoService {
    /**
     * 在store中对应的定义组名称
     */
    String STORE_DEF_GROUP_KNOWLEDGE = "defGroup_knowledge";

    /**
     * listRepos的id
     */
    String GENERICABLE_LIST_REPOS = "modelengine.jade.knowledge.listRepos";

    /**
     * getProperty的id
     */
    String GENERICABLE_GET_PROPERTY = "modelengine.jade.knowledge.getProperty";

    /**
     * retrieve的id
     */
    String GENERICABLE_RETRIEVE = "modelengine.jade.knowledge.retrieve";

    /**
     * 查询知识库列表。
     *
     * @param apiKey 表示用户标识的 {@link String}。
     * @param param 表示知识库分页查询参数的 {@link ListRepoQueryParam}。
     * @return 表示知识库分页结果的 {@link PageVo}{@code <}{@link KnowledgeRepo}{@code >}。
     */
    @Genericable(GENERICABLE_LIST_REPOS)
    @ToolMethod(name = "listRepos", description = "获取知识库列表")
    PageVo<KnowledgeRepo> listRepos(String apiKey, ListRepoQueryParam param);

    /**
     * 查询知识库支持的检索参数信息。
     *
     * @param apiKey 表示用户标识的 {@link String}。
     * @return 表示检索参数信息的 {@link KnowledgeProperty}。
     */
    @Genericable(GENERICABLE_GET_PROPERTY)
    @ToolMethod(name = "getProperty", description = "获取支持的知识库类型")
    KnowledgeProperty getProperty(String apiKey);

    /**
     * 检索知识内容。
     *
     * @param apiKey 表示用户标识的 {@link String}。
     * @param option 表示检索参数的 {@link FlatKnowledgeOption}。
     * @return 表示知识内容的 {@link List}{@code <}{@link KnowledgeDocument}{@code >}。
     */
    @Genericable(GENERICABLE_RETRIEVE)
    @ToolMethod(name = "retrieve", description = "检索知识库")
    List<KnowledgeDocument> retrieve(String apiKey, FlatKnowledgeOption option);
}