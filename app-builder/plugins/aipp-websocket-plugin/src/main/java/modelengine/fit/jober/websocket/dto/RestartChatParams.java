/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import modelengine.fitframework.annotation.Property;

import java.util.Map;

/**
 * 重开会话参数。
 *
 * @author 曹嘉美
 * @since 2025-01-15
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RestartChatParams extends TenantParams {
    @Property(description = "当前实例的唯一标识符", name = "current_instance_id")
    private String currentInstanceId;

    @Property(description = "会话上下文信息", name = "additional_context")
    private Map<String, Object> additionalContext;
}
