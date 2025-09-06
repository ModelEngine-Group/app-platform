/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * MetricsFeedbackVO类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsFeedbackVo {
    private Long id;
    private String question;
    private String answer;
    private LocalDateTime createTime;
    private Long responseTime;
    private String createUser;
    private Integer userFeedback;
    private String userFeedbackText;
}
