/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.time.LocalDateTime;

/**
 * chat与Instance map实体
 *
 * @author 吴穹
 * @since 2024-05-29
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatAndInstanceMap {
    @Property(description = "msg_id")
    private String msgId;

    @Property(description = "task_instance_wide_id")
    private String instanceId;

    @Property(description = "chat_session_id")
    private String chatId;

    @Property(description = "create_at")
    private LocalDateTime createTime;

    @Property(description = "create_by")
    private String creator;

    @Property(description = "update_at")
    private LocalDateTime updateTime;

    @Property(description = "update_by")
    private String updater;
}
