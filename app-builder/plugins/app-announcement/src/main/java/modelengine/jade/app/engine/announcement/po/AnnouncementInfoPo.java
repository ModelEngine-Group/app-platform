/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.announcement.po;

import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AnnouncementInfoPo类消息处理策略
 *
 * @author 张圆
 * @since 2024/06/18
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementInfoPo {
    @Property(description = "公告id")
    private Long id;

    @Property(description = "公告信息类型")
    private String announcementType;

    @Property(description = "公告内容")
    private String content;

    @Property(description = "详情地址")
    private String detailsUrl;

    @Property(description = "开始时间")
    private LocalDateTime startTime;

    @Property(description = "结束时间")
    private LocalDateTime endTime;

    @Property(description = "创建时间")
    private LocalDateTime createTime;
}
