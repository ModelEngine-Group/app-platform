/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.TASK_NOT_FOUND;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_AIPP_QUESTION_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.service.impl.AppChatServiceImpl;
import modelengine.fit.jober.aipp.util.CacheUtils;
import modelengine.fitframework.flowable.Choir;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 为 {@link AppChatService} 提供测试
 *
 * @author 姚江
 * @since 2024-08-01
 */
@ExtendWith(MockitoExtension.class)
public class AppChatServiceImplTest {
    private AppChatService appChatService;

    @Mock
    private AppVersionService appVersionService;

    @Mock
    private AppTaskInstanceService appTaskInstanceService;

    @BeforeEach
    void before() {
        this.appChatService = new AppChatServiceImpl(this.appVersionService);
        CacheUtils.clear();
    }

    @Test
    @DisplayName("测试对话方法")
    void testChat() {
        String chatAppId = "chat";
        String atChatAppId = "atChat";
        Map<String, Object> context = new HashMap<>();
        context.put("user_1", true);
        context.put("user_2", atChatAppId);
        CreateAppChatRequest hello = CreateAppChatRequest.builder()
                .appId(chatAppId).question("你好").chatId("hello")
                .context(CreateAppChatRequest.Context.builder().useMemory(true).atAppId(atChatAppId)
                        .userContext(context).build())
                .build();
        OperationContext operationContext = new OperationContext();
        when(this.appVersionService.run(any(CreateAppChatRequest.class), any(OperationContext.class))).thenReturn(
                mock(Choir.class));

        // when.
        Choir<Object> objectChoir =
                Assertions.assertDoesNotThrow(() -> this.appChatService.chat(hello, operationContext, false));

        // then.
        Assertions.assertNotNull(objectChoir);
    }

    @Test
    @DisplayName("测试重新对话")
    void testRestartChat() {
        // when.
        // then.
        Assertions.assertDoesNotThrow(() -> this.appChatService.restartChat("1",
                new HashMap<>(),
                new OperationContext()));
    }

    @Test
    @DisplayName("测试重新对话：没找到对应的对话")
    void testRestartChatFailedNoChat() {
        Map<String, Object> context = new HashMap<>();
        context.put("user_1", true);
        context.put("user_2", "nofind");
        doThrow(new AippException(TASK_NOT_FOUND)).when(appVersionService).restart(any(), any(), any());
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> this.appChatService.restartChat("1", context, new OperationContext()));
        Assertions.assertEquals(90002909, exception.getCode());
    }

    @Test
    @DisplayName("应用对话时，没有传入合法Question")
    @Disabled
    void testChatWithInvalidQuestion() {
        String chatAppId = "chat";
        String atChatAppId = "atChat";
        Map<String, Object> context = new HashMap<>();
        context.put("user_1", true);
        context.put("user_2", atChatAppId);
        OperationContext operationContext = new OperationContext();

        CreateAppChatRequest hello = CreateAppChatRequest.builder()
            .appId(chatAppId)
            .chatId("hello")
            .context(CreateAppChatRequest.Context.builder()
                .useMemory(true)
                .atAppId(atChatAppId)
                .userContext(context)
                .build())
            .build();

        // question的长度在1-20000之间，在应用的场景下为必填
        testInvalidQuestion(hello, "", operationContext);
        testInvalidQuestion(hello, null, operationContext);
        String testInput = java.util.stream.Stream.generate(() -> "A").limit(20001).collect(Collectors.joining());
        testInvalidQuestion(hello, testInput, operationContext);
    }

    private void testInvalidQuestion(CreateAppChatRequest hello, String question, OperationContext operationContext) {
        hello.setQuestion(question);
        doThrow(new AippParamException(INPUT_PARAM_IS_INVALID, BS_AIPP_QUESTION_KEY)).when(appVersionService)
                .run(any(), any());
        AippParamException exception = Assertions.assertThrows(AippParamException.class,
            () -> this.appChatService.chat(hello, operationContext, false));
        Assertions.assertEquals(exception.getMessage(), "非法参数: Question。");
    }
}
