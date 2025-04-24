/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.config;

import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.knowledge.dto.KnowledgeDto;

import java.util.List;

/**
 * 知识库集参数。
 *
 * @author 陈潇文
 * @since 2025-04-24
 */
@Component
@AcceptConfigValues("knowledge")
public class KnowledgeConfig {
    private List<KnowledgeDto> support;

    /**
     * 获取支持使用的知识库集列表。
     * @return 表示支持使用的知识库集列表的 {@link List}{@code <}{@link KnowledgeDto}{@code >}。
     */
    public List<KnowledgeDto> getSupport() {
        return support;
    }

    /**
    * 设置支持使用的知识库集列表。
    * @param support 表示支持使用的知识库集列表的 {@link List}{@code <}{@link KnowledgeDto}{@code >}。
    */
    public void setSupport(List<KnowledgeDto> support) {
        this.support = support;
    }
}
