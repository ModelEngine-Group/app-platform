/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.announcement.controller;

import modelengine.jade.app.engine.announcement.dto.AnnouncementInfoDto;
import modelengine.jade.app.engine.announcement.service.AnnouncementInfoService;
import modelengine.jade.app.engine.announcement.vo.AnnouncementInfoDisplayDataVo;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;

/**
 * 公告信息相关操作接口。
 *
 * @author 张圆
 * @since 2024-06-18
 */
@Component
@RequestMapping(path = "/announcement", group = "公告信息相关操作接口")
public class AnnouncementInfoController {
    private final AnnouncementInfoService announcementInfoService;

    public AnnouncementInfoController(AnnouncementInfoService announcementInfoService) {
        this.announcementInfoService = announcementInfoService;
    }

    /**
     * 获取公告信息列表
     *
     * @return 表示公告信息内容的 {@link AnnouncementInfoDisplayDataVo}
     */
    @GetMapping(description = "获取公告信息列表")
    public AnnouncementInfoDisplayDataVo queryAnnouncements() {
        return announcementInfoService.queryAnnouncements();
    }

    /**
     * 创建公告接口。
     *
     * @param announcementInfoDto 表示创建公告信息的参数的 {@link AnnouncementInfoDto}
     * @return 公告id {@link Long}
     */
    @CarverSpan(value = "operation.announcement.create")
    @PostMapping(description = "创建公告信息")
    public Long addAnnouncement(@RequestBody @SpanAttr("id:$.id") AnnouncementInfoDto announcementInfoDto) {
        return announcementInfoService.addAnnouncement(announcementInfoDto);
    }
}
