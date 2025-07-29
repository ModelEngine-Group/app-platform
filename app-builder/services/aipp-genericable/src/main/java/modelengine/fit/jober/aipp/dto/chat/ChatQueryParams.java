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

/**
 * 会话历史的查询条件参数。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatQueryParams {
    @Property(description = "aipp的唯一标识符", name = "aipp_id")
    private String aippId;

    @Property(description = "aipp的版本", name = "aipp_version")
    private String aippVersion;

    @RequestQuery("offset")
    @Property(description = "偏移量", name = "offset")
    private Integer offset;

    @RequestQuery("limit")
    @Property(description = "每页条数限制", name = "limit")
    private Integer limit;

    @RequestQuery("appId")
    @Property(description = "应用的唯一标识符", name = "appId")
    private String appId;

    @Property(description = "应用版本", name = "app_version")
    private String appVersion;

    @RequestQuery("appState")
    @Property(description = "应用状态", defaultValue = "active", name = "appState")
    private String appState;
}
