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
import modelengine.fitframework.annotation.Property;

/**
 * 查询会话请求提
 *
 * @author 翟卉馨
 * @since 2024-05-29
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryChatRequest {
    @Property(description = "aipp id", name = "aipp_id")
    private String aippId;

    @Property(description = "aipp version", name = "aipp_version")
    private String aippVersion;

    @Property(description = "offset", name = "offset")
    private Integer offset;

    @Property(description = "limit", name = "limit")
    private Integer limit;

    @Property(description = "app id", name = "app_id")
    private String appId;

    @Property(description = "app version", name = "app_version")
    private String appVersion;

    @Property(description = "app state", defaultValue = "active", name = "app_state")
    private String appState;
}