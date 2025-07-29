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

import java.util.List;

/**
 * 为Form提供查询条件
 *
 * @author 陈潇文
 * @since 2024-11-25
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormQueryCondition {
    private String tenantId;
    private String type;
    private Long offset;
    private int limit;
    private String name;
    private String id;
    private String createBy;
    private List<String> excludeNames;
}
