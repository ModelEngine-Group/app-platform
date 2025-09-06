/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.knowledge;

import lombok.Data;
import modelengine.fitframework.serialization.annotation.SerializeStrategy;
import modelengine.jade.knowledge.dto.QianfanKnowledgeListQueryParam;

/**
 * 表示 {@link QianfanKnowledgeListQueryParam} 类的测试类实现。
 *
 * @author 陈潇文
 * @since 2025-05-08
 */
@Data
@SerializeStrategy(include = SerializeStrategy.Include.NON_NULL)
public class MockedQianfanKnowledgeListQueryParam {
    /**
     * 知识库查询的起始id。
     */
    private String marker;

    /**
     * 知识库查询的关键字。
     */
    private String keyword;

    /**
     * 查询的知识库数量上限。
     */
    private Integer maxKeys;
}
