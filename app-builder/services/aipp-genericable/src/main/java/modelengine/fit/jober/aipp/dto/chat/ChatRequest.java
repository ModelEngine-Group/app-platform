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

import java.util.Map;

/**
 * 创建 app 会话的请求。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    @Property(description = "会话id", name = "chatId")
    private String chatId;

    @Property(description = "问题", name = "question")
    private String question;

    @Property(description = "context", name = "context")
    private Context context;

    /**
     * 本类表示对话的上下文
     */
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Context {
        @Property(description = "是否使用历史记录", name = "useMemory")
        private Boolean useMemory;

        @Property(description = "用户自定义输入", name = "userContext")
        private Map<String, Object> userContext;

        @Property(description = "at其它应用", name = "atAppId")
        private String atAppId;

        @Property(description = "at其它应用的对话", name = "atChatId")
        private String atChatId;

        @Property(description = "产品的信息", name = "dimension")
        private String dimension;

        @Property(description = "产品的id信息", name = "dimensionId")
        private String dimensionId;
    }
}
