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

import java.util.List;

/**
 * 为app提供查询条件
 *
 * @author 邬涨财
 * @since 2024-05-06
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppQueryCondition {
    private String tenantId;

    @RequestParam(name = "type", required = false, defaultValue = "app")
    private String type;
    private List<String> ids;

    @RequestParam(name = "name", required = false)
    private String name;

    @RequestParam(name = "state", required = false)
    private String state;

    @RequestParam(name = "app_category", required = false)
    private String appCategory;
    private List<String> excludeNames;
    private String appSuiteId;
    private String orderBy;
    private String sort;
    private Long offset;
    private Integer limit;

    @RequestParam(name = "app_type", required = false)
    private String appType;
    private String createBy;
}
