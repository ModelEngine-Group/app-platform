/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.announcement.service;

import modelengine.jade.app.engine.announcement.dto.AnnouncementInfoDto;
import modelengine.jade.app.engine.announcement.vo.AnnouncementInfoDisplayDataVo;

/**
 * 公告信息相关服务。
 *
 * @author 张圆
 * @since 2024-06-18
 */
public interface AnnouncementInfoService {
    /**
     * 获取公告信息
     *
     * @return 表示公告信息内容的 {@link AnnouncementInfoDisplayDataVo}
     */
    AnnouncementInfoDisplayDataVo queryAnnouncements();

    /**
     * 插入公告信息。
     *
     * @param announcementDto 公告信息 {@link AnnouncementInfoDto}
     * @return 公告id {@link Long}
     */
    Long addAnnouncement(AnnouncementInfoDto announcementDto);
}
