/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.mockito.Mockito.mock;

import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.mapper.AppChatNumMapper;
import modelengine.fit.jober.aipp.service.impl.AppChatSessionServiceImpl;
import modelengine.fit.jober.aipp.service.impl.AppChatSseServiceImpl;

import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.flowable.emitter.DefaultEmitter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.Optional;

/**
 * {@link AppChatSseService} 的测试类
 *
 * @author 姚江
 * @since 2024-08-01
 */
@ExtendWith(MockitoExtension.class)
public class AppChatSseServiceImplTest {
    private AppChatSseService appChatSseService;

    @Mock
    private Emitter<Object> emitter;

    @Mock
    private AippLogMapper aippLogMapper;

    private AppChatSessionService appChatSessionService;
    private final AppChatNumMapper mockMapper = mock(AppChatNumMapper.class);

    @BeforeEach
    void before() {
        this.appChatSessionService = new AppChatSessionServiceImpl(mockMapper);
        this.appChatSseService = new AppChatSseServiceImpl(aippLogMapper, appChatSessionService);
    }

    @Test
    @DisplayName("测试获取")
    void testGetEmitter() {
        Emitter<Object> e = new DefaultEmitter<>();
        this.appChatSessionService.addSession("hello",
                new ChatSession<>(e, "123", true, Locale.ENGLISH));
        Optional<ChatSession<Object>> hello =
                Assertions.assertDoesNotThrow(() -> this.appChatSseService.getEmitter("hello"));
        Assertions.assertTrue(hello.isPresent());
        Assertions.assertEquals(e, hello.get().getEmitter());
    }

    @Test
    @DisplayName("测试发送最后的消息")
    void testSendLastData() {
        this.appChatSessionService.addSession("hello", new ChatSession<>(emitter, "123", true, Locale.ENGLISH));
        Assertions.assertDoesNotThrow(() -> appChatSseService.send("hello", "hello"));
        Assertions.assertDoesNotThrow(() -> appChatSseService.sendLastData("hello", "hello"));
        Mockito.verify(emitter, Mockito.times(2)).emit("hello");
        Mockito.verify(emitter, Mockito.times(1)).complete();
    }

    @Test
    @DisplayName("测试发送最后的消息到祖先")
    void testSendLastAncestor() {
        this.appChatSessionService.addSession("hello", new ChatSession<>(emitter, "123", true, Locale.ENGLISH));
        Mockito.when(aippLogMapper.getParentPath("hello world")).thenReturn("world/hello");
        Assertions.assertDoesNotThrow(() -> appChatSseService.sendToAncestorLastData("hello world", "hello"));
        Mockito.verify(emitter, Mockito.times(1)).emit("hello");
        Mockito.verify(emitter, Mockito.times(1)).complete();
    }
}
