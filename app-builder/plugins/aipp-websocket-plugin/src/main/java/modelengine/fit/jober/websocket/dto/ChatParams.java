/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.dto;

import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import modelengine.fitframework.annotation.Property;

/**
 * 开启会话参数。
 *
 * @author 曹嘉美
 * @since 2025-01-15
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChatParams extends TenantParams {
    @Property(description = "是否调试模式", name = "is_debug")
    private Boolean isDebug = false;

    @Property(description = "创建会话的请求体")
    private CreateAppChatRequest data;
}
