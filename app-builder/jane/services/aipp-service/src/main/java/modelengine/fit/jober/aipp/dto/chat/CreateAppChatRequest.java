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
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 创建app会话的请求体
 *
 * @author 姚江
 * @since 2024-07-23
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppChatRequest {
    @Property(description = "app的id", name = "app_id")
    private String appId;

    @Property(description = "会话id", name = "chat_id")
    private String chatId;

    @Property(description = "问题", name = "question")
    private String question;

    @Property(description = "context", name = "context")
    private Context context;

    /**
     * 判断是否有标记的应用。
     *
     * @return 表示是否有标记的应用的 {@code boolean}。
     */
    public boolean hasAtOtherApp() {
        return StringUtils.isNotBlank(getContext().getAtChatId()) || StringUtils.isNotBlank(getContext().getAtAppId());
    }

    /**
     * 本类表示对话的上下文
     */
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Context {
        @Property(description = "是否使用历史记录", name = "use_memory")
        private Boolean useMemory;

        @Property(description = "用户自定义输入", name = "user_context")
        private Map<String, Object> userContext;

        @Property(description = "at其它应用", name = AippConst.BS_AT_APP_ID)
        private String atAppId;

        @Property(description = "at其它应用的对话", name = AippConst.BS_AT_CHAT_ID)
        private String atChatId;

        @Property(description = "产品的信息", name = "dimension")
        private String dimension;

        @Property(description = "产品的id信息", name = "dimension_id")
        private String dimensionId;
    }
}
