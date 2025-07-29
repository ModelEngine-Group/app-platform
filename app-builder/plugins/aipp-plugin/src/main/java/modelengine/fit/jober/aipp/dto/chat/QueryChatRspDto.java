/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.util.List;
import java.util.Map;

/**
 * 对话详细信息返回对象
 *
 * @author 邬涨财
 * @since 2024-10-15
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryChatRspDto {
    @Property(description = "app id", name = "app_id")
    @JsonProperty("app_id")
    private String appId;

    @Property(description = "app version", name = "app_version")
    @JsonProperty("app_version")
    private String version;

    @Property(description = "aipp id", name = "aipp_id")
    @JsonProperty("aipp_id")
    private String aippId;

    @Property(description = "aipp version", name = "aipp_version")
    @JsonProperty("aipp_version")
    private String aippVersion;

    @Property(description = "chat id", name = "chat_id")
    @JsonProperty("chat_id")
    private String chatId;

    @Property(description = "chat name", name = "chat_name")
    @JsonProperty("chat_name")
    private String chatName;

    @Property(description = "origin chat id", name = "origin_chat_id")
    @JsonProperty("origin_chat_id")
    private String originChatId;

    @Property(description = "attributes", name = "attributes")
    @JsonProperty("attributes")
    private Map<String, Object> attributes;

    @Property(description = "message list", name = "msg_list")
    @JsonProperty("msg_list")
    private List<MessageInfo> massageList;

    @Property(description = "current msg id", name = "current_instance_id")
    @JsonProperty("current_instance_id")
    private String msgId;

    @Property(description = "update time", name = "update_time")
    @JsonProperty("update_time")
    private String updateTime;

    @Property(description = "recent info", name = "recent_info")
    @JsonProperty("recent_info")
    private String recentInfo;

    @Property(description = "update time", name = "update_time_timestamp")
    @JsonProperty("update_time_timestamp")
    private long updateTimeStamp;

    @Property(description = "current time", name = "current_time_timestamp")
    @JsonProperty("current_time_timestamp")
    private long currentTime;

    @Property(description = "total", name = "total")
    @JsonProperty("total")
    private Integer total;
}
