/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.service;

import lombok.NoArgsConstructor;
import modelengine.fitframework.inspection.Validation;

/**
 * 知识库元数据。
 *
 * @author 马朝阳
 * @since 2024-10-23
 */
@NoArgsConstructor
public class KnowledgeRepoInfo {
    private String id;

    /**
     * 初始化 {@link KnowledgeRepoInfo} 对象。
     *
     * @param id 表示知识库标识的 {@link String}。
     */
    public KnowledgeRepoInfo(String id) {
        this.id = Validation.notBlank(id, "The repository id cannot be null.");
    }

    /**
     * 获取知识库标识。
     *
     * @return 表示知识库标识的 {@link String}。
     */
    public String id() {
        return id;
    }
}