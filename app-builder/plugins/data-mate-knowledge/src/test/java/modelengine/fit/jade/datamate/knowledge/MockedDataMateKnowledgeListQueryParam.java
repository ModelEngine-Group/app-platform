/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.datamate.knowledge;

import lombok.Data;
import modelengine.fitframework.serialization.annotation.SerializeStrategy;

/**
 * 表示 {@link modelengine.fit.jade.datamate.knowledge.dto.DataMateKnowledgeListQueryParam} 类的测试类实现。
 *
 * @author songyongtan
 * @since 2026-02-11
 */
@Data
@SerializeStrategy(include = SerializeStrategy.Include.NON_NULL)
public class MockedDataMateKnowledgeListQueryParam {
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
