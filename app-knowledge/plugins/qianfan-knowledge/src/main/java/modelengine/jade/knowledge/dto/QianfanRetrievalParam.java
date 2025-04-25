/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.dto;

import lombok.Builder;
import lombok.Data;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * qianfan 知识库检索查询参数。
 *
 * @author 陈潇文
 * @since 2025-04-25
 */
@Data
@Builder
public class QianfanRetrievalParam {
    /**
     * 返回前多少的条目。
     */
    private int top;
    /**
     * 检索策略。
     */
    private String type;
    /**
     * 检索query。
     */
    private String query;
    /**
     * 指定知识库的id集合。
     */
    @Property(description = "knowledgebase_ids", name = "knowledgebase_ids")
    private List<String> knowledgebaseIds;
    /**
     * 检索配置。
     */
    @Property(description = "pipeline_config", name = "pipeline_config")
    private QianfanPipelineConfigQueryParam pipelineConfig;
}
