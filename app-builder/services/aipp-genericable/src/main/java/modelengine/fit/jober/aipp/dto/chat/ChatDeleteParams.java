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
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Property;

/**
 * 删除会话的参数。
 *
 * @author 曹嘉美
 * @since 2025-01-13
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDeleteParams {
    @RequestQuery(value = "appId", required = false)
    @Property(description = "应用的唯一标识符")
    private String appId;

    @RequestQuery(value = "chatId", required = false)
    @Property(description = "要删除的聊天会话的唯一标识符，若没有指定，则全部删除")
    private String chatId;
}
