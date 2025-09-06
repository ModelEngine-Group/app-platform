/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.announcement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fit.jane.common.validation.Pattern;
import modelengine.fit.jane.common.validation.Size;
import modelengine.fitframework.annotation.Property;

/**
 * AnnouncementInfoDto类消息处理策略
 *
 * @author 张圆
 * @since 2024/06/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementInfoDto {
    @Property(description = "公告id")
    private Long id;

    @Property(description = "公告信息类型")
    private String announcementType;

    @Size(min = 1, max = 100, message = "公告内容最长为100个字")
    @Property(description = "公告内容")
    private String content;

    @Pattern(regexp = "^https?://.*$|^$", message = "详情地址不合法，请检查，合法地址应该以：https/http开始")
    @Property(description = "详情地址")
    private String detailsUrl;

    @Property(description = "开始时间", example = "2024-06-18 10:10:00")
    private String startTime;

    @Property(description = "结束时间", example = "2024-06-18 10:10:00")
    private String endTime;
}
