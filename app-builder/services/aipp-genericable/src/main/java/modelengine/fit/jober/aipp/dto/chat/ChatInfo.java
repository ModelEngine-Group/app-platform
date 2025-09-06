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
import java.util.Map;

/**
 * 会话的详细信息。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatInfo {
    @Property(description = "app 的唯一标识符", name = "app_id")
    private String appId;

    @Property(description = "app 的版本", name = "app_version")
    private String version;

    @Property(description = "会话的唯一标识符", name = "chat_id")
    private String chatId;

    @Property(description = "会话的名字", name = "chat_name")
    private String chatName;

    @Property(description = "主应用会话的唯一标识符", name = "origin_chat_id")
    private String originChatId;

    @Property(description = "属性", name = "attributes")
    private Map<String, Object> attributes;

    @Property(description = "对话内容列表", name = "msg_list")
    private List<ChatMessage> messageList;

    @Property(description = "当前应用实例的唯一标识符", name = "current_instance_id")
    private String msgId;

    @Property(description = "更新时间", name = "update_time")
    private String updateTime;

    @Property(description = "最新信息", name = "recent_info")
    private String recentInfo;

    @Property(description = "更新的时间戳", name = "update_time_timestamp")
    private Long updateTimeStamp;

    @Property(description = "当前时间", name = "current_time_timestamp")
    private Long currentTime;

    @Property(description = "总数", name = "total")
    private Integer total;
}
