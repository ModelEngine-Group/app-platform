/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.mockito.Mockito.mock;

import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.mapper.AppChatNumMapper;
import modelengine.fit.jober.aipp.service.impl.AppChatSessionServiceImpl;

import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.flowable.emitter.DefaultEmitter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Optional;

/**
 * {@link AppChatSessionService} 的测试类
 *
 * @author 陈潇文
 * @since 2024-10-15
 */
public class AppChatSessionServiceImplTest {
    private AppChatSessionService appChatSessionService;

    private final AppChatNumMapper mockMapper = mock(AppChatNumMapper.class);

    @BeforeEach
    void before() {
        this.appChatSessionService = new AppChatSessionServiceImpl(this.mockMapper);
    }

    @Test
    @DisplayName("测试添加")
    void testAddChatSession() {
        Emitter<Object> e = new DefaultEmitter<>();
        this.appChatSessionService.addSession("hello", new ChatSession<>(e, "123", true, Locale.ENGLISH));
        Optional<ChatSession<Object>> hello =
                Assertions.assertDoesNotThrow(() -> this.appChatSessionService.getSession("hello"));
        Assertions.assertTrue(hello.isPresent());
        Assertions.assertEquals(e, hello.get().getEmitter());
    }

    @Test
    @DisplayName("测试删除")
    void testRemoveSession() {
        Emitter<Object> e = new DefaultEmitter<>();
        this.appChatSessionService.addSession("hello", new ChatSession<>(e, "123", true, Locale.ENGLISH));
        this.appChatSessionService.removeSession("hello");
        Optional<ChatSession<Object>> hello =
                Assertions.assertDoesNotThrow(() -> this.appChatSessionService.getSession("hello"));
        Assertions.assertFalse(hello.isPresent());
    }
}
