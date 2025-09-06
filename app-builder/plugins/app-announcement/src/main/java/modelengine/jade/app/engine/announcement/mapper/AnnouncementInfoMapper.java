/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.announcement.mapper;

import modelengine.jade.app.engine.announcement.po.AnnouncementInfoPo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 公告信息相关的db接口。
 *
 * @author 张圆
 * @since 2024-06-18
 */
@Mapper
public interface AnnouncementInfoMapper {
    /**
     * 插入一个公告信息。
     *
     * @param announcementInfoPo 表示公告信息实体类的 {@link AnnouncementInfoPo}
     */
    void insert(AnnouncementInfoPo announcementInfoPo);

    /**
     * 获取公告信息列表
     *
     * @param currentTime 表示当前时间 {@link Date}。
     * @return 表示公告信息列表的 {@link List}{@code <}{@link AnnouncementInfoPo}{@code >}
     */
    List<AnnouncementInfoPo> getAnnouncementInfo(@Param("currentTime") Date currentTime);
}
