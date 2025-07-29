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
 * 会话的消息。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Property(description = "消息的唯一标识符", name = "message_id")
    private String msgId;

    @Property(description = "role", name = "role")
    private String role;

    @Property(description = "创建时间", name = "create_time")
    private String createTime;

    @Property(description = "消息内容类型", name = "content_type")
    private Integer contentType;

    @Property(description = "消息内容", name = "content")
    private List<String> content;

    @Property(description = "parent id", name = "parent_id")
    private String parentId;

    @Property(description = "children id", name = "children_id")
    private String childrenId;

    @Property(description = "应用名称", name = "app_name")
    private String appName;

    @Property(description = "app icon", name = "app_icon")
    private String appIcon;
}