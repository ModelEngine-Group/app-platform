/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示知识库集的传输对象。
 *
 * @author 陈潇文
 * @since 2025-04-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDto {
    /**
     * 知识库集groupId。
     */
    private String groupId;

    /**
     * 知识库集名称。
     */
    private String name;

    /**
     * 知识库集描述。
     */
    private String description;
}
