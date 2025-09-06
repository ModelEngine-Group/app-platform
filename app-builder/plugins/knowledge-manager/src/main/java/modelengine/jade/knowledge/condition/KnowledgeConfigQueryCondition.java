/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.condition;

import lombok.Builder;
import lombok.Data;

/**
 * 知识库配置查询条件集。
 *
 * @author 陈潇文
 * @since 2025-04-24
 */
@Builder
@Data
public class KnowledgeConfigQueryCondition {
    private Long id;

    private String groupId;

    private String userId;

    private String apiKey;

    private String knowledgeConfigId;

    private Integer isDefault;
}
