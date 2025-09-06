/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * app 查询条件参数。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppQueryParams {
    @RequestQuery(name = "ids", required = false)
    @Property(description = "查询的id列表")
    private List<String> ids;

    @RequestQuery(name = "name", required = false)
    @Property(description = "查询的名字")
    private String name;

    @RequestQuery(name = "state", required = false)
    @Property(description = "查询的状态")
    private String state;

    @RequestQuery(name = "excludeNames", required = false)
    @Property(description = "排除的名字")
    private List<String> excludeNames;

    @RequestQuery(value = "offset", defaultValue = "0", required = false)
    @Property(description = "偏移量")
    private int offset;

    @RequestQuery(name = "limit", defaultValue = "10", required = false)
    @Property(description = "每页查询条数")
    private int limit;

    @RequestQuery(name = "type", defaultValue = "app", required = false)
    @Property(description = "查询类型")
    private String type;
}
