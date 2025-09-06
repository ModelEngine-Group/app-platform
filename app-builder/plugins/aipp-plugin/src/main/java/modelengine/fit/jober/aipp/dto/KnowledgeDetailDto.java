/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 知识库详细信息
 *
 * @author 黄夏露
 * @since 2024-04-23
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDetailDto {
    @Property(description = "知识库 id", name = "id")
    private Long id;

    @Property(description = "知识库名称", name = "name")
    private String name;

    @Property(description = "知识库描述", name = "description")
    private String description;
}
