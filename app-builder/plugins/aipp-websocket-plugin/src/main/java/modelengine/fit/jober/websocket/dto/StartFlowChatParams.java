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
 * 启动流程参数。
 *
 * @author 曹嘉美
 * @since 2025-01-15
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StartFlowChatParams extends TenantParams {
    @Property(description = "实例的唯一标识符", name = "instance_id")
    private String instanceId;

    @Property(description = "初始化上下文", name = "init_context")
    private Map<String, Object> initContext;
}
