/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ConversationRecordPo类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationRecordPo {
    private Long id;
    private String appId;
    private String question;
    private String answer;
    private String createUser;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
    private String instanceId;
}
