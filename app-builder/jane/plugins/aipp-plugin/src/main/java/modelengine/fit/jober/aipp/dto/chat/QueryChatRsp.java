/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.util.JsonUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 查询会话响应体
 *
 * @author 翟卉馨
 * @since 2024-05-29
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryChatRsp {
    @Property(description = "app id", name = "app_id")
    private String appId;

    @Property(description = "app version", name = "app_version")
    private String version;

    @Property(description = "aipp id", name = "aipp_id")
    private String aippId;

    @Property(description = "aipp version", name = "aipp_version")
    private String aippVersion;

    @Property(description = "chat id", name = "chat_id")
    private String chatId;

    @Property(description = "chat name", name = "chat_name")
    private String chatName;

    @Property(description = "origin chat id", name = "origin_chat_id")
    private String originChatId;

    @Property(description = "attributes", name = "attributes")
    private String attributes;

    @Property(description = "message list", name = "msg_list")
    private List<MessageInfo> massageList;

    @Property(description = "current msg id", name = "current_instance_id")
    private String msgId;

    @Property(description = "update time", name = "update_time")
    private String updateTime;

    @Property(description = "recent info", name = "recent_info")
    private String recentInfo;

    @Property(description = "update time", name = "update_time_timestamp")
    private long updateTimeStamp;

    @Property(description = "current time", name = "current_time_timestamp")
    private long currentTime;

    @Property(description = "total", name = "total")
    private Integer total;

    /**
     * chat是否是调试模式.
     *
     * @return true/false.
     */
    public boolean isDebug() {
        Map<String, Object> jsonAttributes = JsonUtils.parseObject(this.getAttributes());
        String state = ObjectUtils.cast(jsonAttributes.get(AippConst.ATTR_CHAT_STATE_KEY));
        return StringUtils.equals(AppState.INACTIVE.getName(), state);
    }
}
