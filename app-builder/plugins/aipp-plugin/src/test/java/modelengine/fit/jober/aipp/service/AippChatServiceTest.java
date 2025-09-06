/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.chat.repository.AppChatRepository;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRspDto;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppMapper;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.service.impl.AippChatServiceImpl;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

/**
 * AippChatServiceTest
 *
 * @author 方誉州
 * @since 2024-09-02
 */
@FitTestWithJunit(includeClasses = AippChatServiceImpl.class)
public class AippChatServiceTest {
    @Fit
    private AippChatService aippChatService;

    @Mock
    private AippChatMapper aippChatMapper;

    @Mock
    private MetaService metaService;

    @Mock
    private AppBuilderAppMapper appBuilderAppMapper;

    @Mock
    private AppTaskService appTaskService;

    @Mock
    private AippLogService aippLogService;

    @Mock
    private AppVersionRepository appVersionRepository;

    @Mock
    private AippLogMapper aippLogMapper;

    @Mock
    private AppChatRepository appChatRepository;

    @Mock
    private AppBuilderAppRepository appRepository;

    @Test
    void testQueryChatList() {
        QueryChatRequest body = QueryChatRequest.builder()
                .appId("test-appid")
                .offset(0)
                .limit(10)
                .build();
        QueryChatRsp rsp = QueryChatRsp.builder()
                .attributes("{\"state\": \"active\", \"instId\": \"f2070d7ee84c4aa787a609807dc75957\"}")
                .updateTime(Timestamp.valueOf(LocalDateTime.now()).toString())
                .build();
        when(aippChatMapper.selectChatList(any(), any(), any())).thenReturn(Collections.singletonList(rsp));
        when(aippChatMapper.selectMsgByInstanceIds(any())).thenReturn(new ArrayList<>());
        when(aippChatMapper.getChatListCount(any(), anyString(), anyString())).thenReturn(1L);
        RangedResultSet<QueryChatRspDto> rangedResultSet =
                aippChatService.queryChatList(body, new OperationContext());
        assertThat(rangedResultSet.getResults().size()).isEqualTo(1);
        assertThat(rangedResultSet.getResults().get(0).getMsgId()).isEqualTo("f2070d7ee84c4aa787a609807dc75957");
    }
}
