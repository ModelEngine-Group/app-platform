/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * Aipp Websocket 的响应数据类。
 *
 * @author 方誉州
 * @since 2024-09-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AippWebsocketRsp {
    @Property(description = "表示流所属的请求的唯一 id")
    private String requestId;

    @Property(description = "流响应的状态码")
    private int code;

    @Property(description = "流响应异常时的异常信息")
    private String msg;

    @Property(description = "流返回的具体数据内容")
    private Object data;

    @Property(description = "表示流的结束状态", name = "completed")
    private boolean isCompleted;
}
