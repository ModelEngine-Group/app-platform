/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.dto;

import modelengine.jade.common.query.PageQueryParam;
import modelengine.jade.knowledge.enums.KnowledgeTypeEnum;

import lombok.Data;
import modelengine.fitframework.serialization.annotation.SerializeStrategy;

import java.util.List;

/**
 * Edm 知识库查询参数。
 *
 * @author 何嘉斌
 * @since 2024-09-25
 */
@Data
@SerializeStrategy(include = SerializeStrategy.Include.NON_NULL)
public class ListKnowledgeQueryParam extends PageQueryParam {
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
}