/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.announcement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.announcement.dto.AnnouncementInfoDto;
import modelengine.jade.app.engine.announcement.mapper.AnnouncementInfoMapper;
import modelengine.jade.app.engine.announcement.po.AnnouncementInfoPo;
import modelengine.jade.app.engine.announcement.service.impl.AnnouncementInfoServiceImpl;
import modelengine.jade.app.engine.announcement.vo.AnnouncementInfoDisplayDataVo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 用户公告测试代码。
 *
 * @author 张圆
 * @since 2024-06-18
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AnnouncementInfoServiceTest {
    @InjectMocks
    private AnnouncementInfoServiceImpl announcementService;
    @Mock
    private AnnouncementInfoMapper announcementInfoMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        announcementService = new AnnouncementInfoServiceImpl(announcementInfoMapper);
    }

    @Test
    @DisplayName("测试查询公告信息")
    public void testQueryAnnouncements() {
        // Assert that when there are no announcements, the result is null
        when(announcementInfoMapper.getAnnouncementInfo(any(Date.class))).thenReturn(Collections.emptyList());
        AnnouncementInfoDisplayDataVo result = announcementService.queryAnnouncements();
        assertNull(result);
        List<AnnouncementInfoPo> mockList = getAnnouncementInfoPo();

        when(announcementInfoMapper.getAnnouncementInfo(any(Date.class))).thenReturn(mockList);
        result = announcementService.queryAnnouncements();

        assertNotNull(result);
        assertEquals(2, result.getAnnouncementsByType().size());
        assertEquals(1, result.getAnnouncementsByType().get("Type1").size());
        assertEquals(1, result.getAnnouncementsByType().get("Type2").size());
    }


    private static List<AnnouncementInfoPo> getAnnouncementInfoPo() {
        LocalDateTime createTime = LocalDateTime.parse("2024-06-18 10:10:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        AnnouncementInfoPo mockAnnouncement = new AnnouncementInfoPo();
        mockAnnouncement.setId(1L);
        mockAnnouncement.setCreateTime(createTime);
        mockAnnouncement.setAnnouncementType("Type1");

        AnnouncementInfoPo anotherMockAnnouncement = new AnnouncementInfoPo();
        anotherMockAnnouncement.setId(2L);
        anotherMockAnnouncement.setCreateTime(createTime);
        anotherMockAnnouncement.setAnnouncementType("Type2");

        return Arrays.asList(mockAnnouncement, anotherMockAnnouncement);
    }

    @Test
    @DisplayName("测试新增公告信息")
    public void testAddAnnouncement() {
        // prepare test data
        AnnouncementInfoDto dto = new AnnouncementInfoDto();
        dto.setAnnouncementType("Type");
        dto.setContent("Content");
        dto.setDetailsUrl("https://example.com");
        dto.setStartTime("2024-05-18 10:10:00");
        dto.setEndTime("2024-06-18 10:10:00");

        doAnswer(invocation -> {
            AnnouncementInfoPo insertedPo = invocation.getArgument(0);
            insertedPo.setId(1L);
            return null;
        }).when(announcementInfoMapper).insert(any(AnnouncementInfoPo.class));

        Long id = announcementService.addAnnouncement(dto);
        ArgumentCaptor<AnnouncementInfoPo> captor = ArgumentCaptor.forClass(AnnouncementInfoPo.class);
        verify(announcementInfoMapper).insert(captor.capture());

        AnnouncementInfoPo insertedPo = captor.getValue();

        assertEquals(dto.getContent(), insertedPo.getContent());
        assertNotNull(insertedPo.getCreateTime());
        assertNotNull(id);
        assertEquals(Long.valueOf(1L), id);

        assertEquals("Type", dto.getAnnouncementType());
        assertEquals("Content", dto.getContent());
        assertEquals("https://example.com", dto.getDetailsUrl());
        assertEquals("2024-05-18 10:10:00", dto.getStartTime());
        assertEquals("2024-06-18 10:10:00", dto.getEndTime());
    }
}

