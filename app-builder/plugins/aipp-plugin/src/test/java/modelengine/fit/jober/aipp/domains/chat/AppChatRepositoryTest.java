/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.domains.chat.repository.AppChatRepository;
import modelengine.fit.jober.aipp.domains.chat.repository.impl.AppChatRepositoryImpl;
import modelengine.fit.jober.aipp.dto.chat.ChatCreateEntity;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.service.DatabaseBaseTest;

import modelengine.fitframework.annotation.Fit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * {@link AppChatRepository} 的测试类。
 *
 * @author 孙怡菲
 * @since 2025-02-20
 */
public class AppChatRepositoryTest extends DatabaseBaseTest {
    @Fit
    private final AippChatMapper aippChatMapper = sqlSessionManager.openSession(true).getMapper(AippChatMapper.class);

    private AppChatRepository appChatRepository;

    @BeforeEach
    void setUp() {
        this.appChatRepository = new AppChatRepositoryImpl(this.aippChatMapper);
    }

    @Test
    @DisplayName("测试查询")
    void TestGetChatById() {
        String chatId = "003f0cd8dcfb4aca88af34d8f85750d2";
        String userId = "tester 12345678";
        String appId = "ebc5afee8bd94c5eb5d36da049396864";

        Optional<QueryChatRsp> result = this.appChatRepository.getChatById(chatId, userId);

        Assertions.assertEquals(appId, result.get().getAppId());
    }

    @Test
    @DisplayName("测试保存chat")
    void TestSaveChat() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("test", "123");
        ChatCreateEntity mockChatEntity = ChatCreateEntity.builder()
            .appId("appId")
            .appVersion("1.0.0")
            .attributes(attributes)
            .chatName("chat")
            .chatId("chatId")
            .taskInstanceId("instanceId")
            .build();
        OperationContext context = new OperationContext();
        context.setOperator("test1");

        AippChatMapper aippChatMapperMock = mock(AippChatMapper.class);
        AppChatRepository appChatRepositoryMock = new AppChatRepositoryImpl(aippChatMapperMock);

        appChatRepositoryMock.saveChat(mockChatEntity, context);

        verify(aippChatMapperMock, times(1)).insertChat(any());
        verify(aippChatMapperMock, times(1)).insertWideRelationship(any());
    }
}
