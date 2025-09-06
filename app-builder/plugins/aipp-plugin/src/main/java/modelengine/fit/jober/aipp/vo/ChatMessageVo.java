/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.util.Map;

/**
 * 对话VO
 *
 * @author 姚江
 * @since 2024-07-27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageVo {
    private String data;

    private String type;

    @Property(name = "message_id")
    private String messageId;

    private String status;

    private Map<String, Object> extension;
}
