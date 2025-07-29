/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 大模型会话参数。
 *
 * @author 曹嘉美
 * @since 2025-01-15
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppWsParams {
    @Property(description = "方法名")
    private String method;

    @Property(description = "请求的唯一标识符", name = "request_id")
    private String requestId;

    @Property(description = "参数")
    private Object params;
}
