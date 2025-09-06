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
 * 对话信息返回对象
 *
 * @author 邬涨财
 * @since 2024-10-15
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatInfoRspDto {
    @Property(description = "chat id", name = "chat_id")
    @JsonProperty("chat_id")
    private String chatId;
}
