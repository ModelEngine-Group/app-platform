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

import java.util.List;

/**
 * 会话消息
 *
 * @author 翟卉馨
 * @since 2024-05-29
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageInfo {
    @Property(description = "message id", name = "message_id")
    private String msgId;

    @Property(description = "role", name = "role")
    private String role;

    @Property(description = "create time", name = "create_time")
    private String createTime;

    @Property(description = "content type", name = "content_type")
    private Integer contentType;

    @Property(description = "content", name = "content")
    private List<String> content;

    @Property(description = "parent id", name = "parent_id")
    private String parentId;

    @Property(description = "children id", name = "children_id")
    private String childrenId;

    @Property(description = "app name", name = "app_name")
    private String appName;

    @Property(description = "app icon", name = "app_icon")
    private String appIcon;
}