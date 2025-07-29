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

/**
 * 会话信息
 *
 * @author 翟卉馨
 * @since 2024-05-29
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    @Property(description = "message id")
    private String msgId;

    @Property(description = "logType")
    private String logType;

    @Property(description = "create time")
    private String createTime;

    @Property(description = "msg")
    private String logData;

    @Property(description = "aipp id")
    private String aippId;

    @Property(description = "app name")
    private String appName;

    @Property(description = "app icon")
    private String appIcon;
}