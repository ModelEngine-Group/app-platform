/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.datamate.knowledge;

import lombok.Data;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * 表示 {@link modelengine.fit.jade.datamate.knowledge.dto.DataMateRetrievalParam} 类的测试类实现。
 *
 * @author songyongtan
 * @since 2026-02-11
 */
@Data
public class MockedDataMateRetrievalParam {
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
