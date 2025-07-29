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
 * 创建会话请求体
 *
 * @author 翟卉馨
 * @since 2024-05-29
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRequest {
    @Property(description = "aipp_id", name = "aipp_id")
    private String aippId;

    @Property(description = "aipp_version", name = "aipp_version")
    private String aippVersion;

    @Property(description = "init context", name = "init_context")
    private Map<String, Object> initContext;

    @Property(description = "chat_id", name = "chat_id")
    private String chatId;

    @Property(description = "origin_app", name = "origin_app")
    private String originApp;

    @Property(description = "origin_app_version", name = "origin_app_version")
    private String originAppVersion;
}
