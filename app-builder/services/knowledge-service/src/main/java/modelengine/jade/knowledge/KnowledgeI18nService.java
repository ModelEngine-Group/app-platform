/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import modelengine.jade.knowledge.enums.KnowledgeRetrievalParam;

import modelengine.fitframework.annotation.Genericable;

/**
 * 获取知识库国际化信息的类。
 *
 * @author 马朝阳
 * @since 2024-10-10
 */
public interface KnowledgeI18nService {
    /**
     * 根据搜索参数获取取参数名称和描述的国际化信息。
     *
     * @param paramType 表示检索方式 {@link KnowledgeRetrievalParam}。
     * @return 国际化信息的 {@link KnowledgeI18nInfo}
     */
    @Genericable("modelengine.jade.knowledge.localize.parameter")
    KnowledgeI18nInfo localizeText(KnowledgeRetrievalParam paramType);

    /**
     * 根据名称获取参数的国际化信息。
     *
     * @param name 表示检索方式 {@link String}。
     * @return 国际化信息的 {@link KnowledgeI18nInfo}
     */
    @Genericable("modelengine.jade.knowledge.localize.text")
    String localizeText(String name);
}
