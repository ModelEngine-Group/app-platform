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
public class CreateAppChatRsp {
    @Property(description = "chat id")
    @JsonProperty("chat_id")
    private String chatId;
}
