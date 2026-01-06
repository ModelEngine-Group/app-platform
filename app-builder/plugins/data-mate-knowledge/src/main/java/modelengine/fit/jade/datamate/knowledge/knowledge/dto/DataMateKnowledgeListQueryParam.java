/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.knowledge.dto;

import lombok.Builder;
import lombok.Data;
import modelengine.fitframework.serialization.annotation.SerializeStrategy;

/**
 * DataMate 知识库列表查询参数。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Data
@Builder
@SerializeStrategy(include = SerializeStrategy.Include.NON_NULL)
public class DataMateKnowledgeListQueryParam {
    /**
     * 页码，从0开始。
     */
    private Integer page;

    /**
     * 每页大小。
     */
    private Integer size;

    /**
     * 知识库名称过滤。
     */
    private String name;

    /**
     * 知识库描述过滤。
     */
    private String description;
}

