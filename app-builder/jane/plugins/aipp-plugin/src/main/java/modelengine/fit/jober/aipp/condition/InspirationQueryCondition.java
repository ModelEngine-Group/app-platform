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
import modelengine.fit.http.annotation.RequestParam;

/**
 * 为inspiration提供查询条件
 *
 * @author 陈潇文
 * @since 2024-10-23
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspirationQueryCondition {
    private String aippId;
    @RequestParam(name = "parent_id", required = false)
    private String parentId;
    private String categoryId;
    private String createUser;
}
