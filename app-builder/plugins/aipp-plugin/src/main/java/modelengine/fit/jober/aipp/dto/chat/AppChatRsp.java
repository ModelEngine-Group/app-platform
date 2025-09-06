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
 * 创建app会话的响应结果
 *
 * @author 姚江
 * @since 2024-07-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppChatRsp {
    @Property(description = "chat id", name = "chat_id")
    private String chatId;

    @Property(description = "at chat id", name = "at_chat_id")
    private String atChatId;

    private String status;

    private List<Answer> answer;

    @Property(description = "form instance id", name = "instance_id")
    private String instanceId;

    @Property(description = "log id", name = "log_id")
    private String logId;

    @Property(description = "extensions", name = "extensions")
    private Map<String, Object> extension;

    /**
     * Answer
     *
     * @author 姚江
     * @since 2024-07-29
     */
    @Data
    @Builder
    public static class Answer {
        Object content;
        String type;
        String msgId;
    }
}
