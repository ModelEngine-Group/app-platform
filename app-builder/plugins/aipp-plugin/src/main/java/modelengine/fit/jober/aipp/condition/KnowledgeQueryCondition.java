/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fit.http.annotation.RequestQuery;

/**
 * 知识库查询条件集
 *
 * @author 黄夏露
 * @since 2024-04-23
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeQueryCondition {
    @RequestQuery(name = "name", required = false)
    private String name;
}
