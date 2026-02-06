/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.dto;

import lombok.Builder;
import lombok.Data;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * DataMate 知识库检索查询参数。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Data
@Builder
public class DataMateRetrievalParam {
    /**
     * 检索 query。
     */
    private String query;
    /**
     * 返回前多少的条目。
     */
    @Property(description = "topK", name = "topK")
    private Integer topK;
    /**
     * 相关性阈值。
     */
    @Property(description = "threshold", name = "threshold")
    private Double threshold;
    /**
     * 指定知识库的id集合。
     */
    @Property(description = "knowledgeBaseIds", name = "knowledgeBaseIds")
    private List<String> knowledgeBaseIds;
}

