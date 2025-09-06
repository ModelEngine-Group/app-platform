/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.announcement.service.impl;

import modelengine.fitframework.annotation.Component;
import modelengine.jade.app.engine.announcement.mapstruct.mapper.AnnouncementInfoStructMapper;
import modelengine.jade.app.engine.announcement.po.AnnouncementInfoPo;
import modelengine.jade.app.engine.announcement.service.AnnouncementInfoService;
import modelengine.jade.app.engine.announcement.vo.AnnouncementInfoDisplayVo;
import modelengine.jade.app.engine.announcement.dto.AnnouncementInfoDto;
import modelengine.jade.app.engine.announcement.mapper.AnnouncementInfoMapper;
import modelengine.jade.app.engine.announcement.vo.AnnouncementInfoDisplayDataVo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公告信息服务实现。
 *
 * @author 张圆
 * @since 2024-06-18
 */
@Component
public class AnnouncementInfoServiceImpl implements AnnouncementInfoService {
    private final AnnouncementInfoMapper announcementInfoMapper;

    public AnnouncementInfoServiceImpl(AnnouncementInfoMapper announcementInfoMapper) {
        this.announcementInfoMapper = announcementInfoMapper;
    }

    @Override
    public AnnouncementInfoDisplayDataVo queryAnnouncements() {
        Date currentTime = new Date();
        List<AnnouncementInfoPo> announcementInfoPoList = announcementInfoMapper.getAnnouncementInfo(currentTime);
        if (announcementInfoPoList.isEmpty()) {
            return null;
        }

        List<AnnouncementInfoDisplayVo> announcementInfoDisplayVoList =
                AnnouncementInfoStructMapper.INSTANCE.poToVo(announcementInfoPoList);
        AnnouncementInfoPo latestAnnouncement =
                announcementInfoPoList.get(announcementInfoPoList.size() - 1);
        LocalDateTime latestCreateTime = latestAnnouncement.getCreateTime();
        Map<String, List<AnnouncementInfoDisplayVo>> groupedByType =
                announcementInfoDisplayVoList.stream()
                        .collect(Collectors.groupingBy(AnnouncementInfoDisplayVo::getAnnouncementType));

        AnnouncementInfoDisplayDataVo data = new AnnouncementInfoDisplayDataVo();
        data.setAnnouncementsByType(groupedByType);
        data.setLatestCreateTime(latestCreateTime);

        return data;
    }

    @Override
    public Long addAnnouncement(AnnouncementInfoDto announcementDto) {
        AnnouncementInfoPo announcementInfoPo = AnnouncementInfoStructMapper.INSTANCE.dtoToPO(announcementDto);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        announcementInfoPo.setCreateTime(now);
        announcementInfoMapper.insert(announcementInfoPo);
        return announcementInfoPo.getId();
    }
}
