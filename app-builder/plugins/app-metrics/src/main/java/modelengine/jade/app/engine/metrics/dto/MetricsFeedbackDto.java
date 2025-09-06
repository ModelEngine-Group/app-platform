/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.dto;

import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * MetricsFeedbackDTO类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricsFeedbackDto {
    @Property(description = "应用id")
    @RequestQuery(name = "appId")
    private String appId;

    @Property(description = "用户提问")
    @RequestQuery(name = "question", required = false)
    private String question;

    @Property(description = "模型回答")
    @RequestQuery(name = "answer", required = false)
    private String answer;

    @Property(description = "用户提问时间")
    @RequestQuery(name = "createTime", required = false)
    private LocalDateTime createTime;

    @Property(description = "排序方式")
    @RequestQuery(name = "orderDirection", required = false)
    private String orderDirection;

    @Property(description = "按创建时间排序")
    @RequestQuery(name = "sortByCreateTime", required = false)
    private Boolean isSortByCreateTime;

    @Property(description = "按响应时间排序")
    @RequestQuery(name = "sortByResponseTime", required = false)
    private Boolean isSortByResponseTime;

    @Property(description = "用户")
    @RequestQuery(name = "createUser", required = false)
    private String createUser;

    @Property(description = "筛选起始时间")
    @RequestQuery(name = "startTime", required = false)
    private LocalDateTime startTime;

    @Property(description = "筛选结束时间")
    @RequestQuery(name = "endTime", required = false)
    private LocalDateTime endTime;

    @Property(description = "页码(1开始)", example = "1")
    @RequestQuery(name = "pageIndex", required = false, defaultValue = "1")
    @Range(min = 1, max = Integer.MAX_VALUE, message = "页码从1开始")
    private int pageIndex = 1;

    @Property(description = "每页大小", example = "10")
    @RequestQuery(name = "pageSize", required = false, defaultValue = "10")
    @Range(min = 1, max = 300, message = "每页尺寸范围[1, 300]")
    private int pageSize = 10;
}
