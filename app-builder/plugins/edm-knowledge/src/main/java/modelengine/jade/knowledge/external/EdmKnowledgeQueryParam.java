/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.external;

import modelengine.jade.knowledge.dto.ListKnowledgeQueryParam;
import modelengine.jade.knowledge.enums.KnowledgeTypeEnum;

import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fitframework.serialization.annotation.SerializeStrategy;

import java.util.List;

/**
 * 可序列化的 {@link ListKnowledgeQueryParam} 实现。
 *
 * @author 马朝阳
 * @since 2024-12-02
 */
@Setter
@NoArgsConstructor
@SerializeStrategy(include = SerializeStrategy.Include.NON_NULL)
public class EdmKnowledgeQueryParam {
    /**
     * 知识库名字。
     */
    private String name;

    /**
     * 知识库数据类型。
     */
    private List<KnowledgeTypeEnum> type;

    /**
     * 知识库状态。
     */
    private List<String> status;

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 每页数量
     */
    private Integer pageSize;
}