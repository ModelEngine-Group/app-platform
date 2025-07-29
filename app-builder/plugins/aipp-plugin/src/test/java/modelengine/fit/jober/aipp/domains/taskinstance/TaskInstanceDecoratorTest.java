/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domains.business.MemoryTypeEnum;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fit.jober.aipp.service.AppChatSseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link TaskInstanceDecorator} 的测试类。
 *
 * @author 张越
 * @since 2025-01-12
 */
public class TaskInstanceDecoratorTest {
    private AppTaskInstanceService appTaskInstanceService;
    private AippLogService logService;
    private AppChatSseService appChatSSEService;
    private AppChatSessionService appChatSessionService;

    @BeforeEach
    public void setUp() throws Exception {
        this.appTaskInstanceService = mock(AppTaskInstanceService.class);
        this.logService = mock(AippLogService.class);
        this.appChatSSEService = mock(AppChatSseService.class);
        this.appChatSessionService = mock(AppChatSessionService.class);
    }

    @Test
    @DisplayName("测试testChat，但session为null")
    public void testChatWhenSessionIsNull() {
        AppTaskInstance instance = mock(AppTaskInstance.class);
        RunContext runContext = new RunContext(new HashMap<>(), new OperationContext());
        doNothing().when(instance).run(any(), any());
        TaskInstanceDecorator.create(instance)
                .chat(this.appChatSessionService, this.appChatSSEService)
                .run(runContext, null);
        verify(this.appChatSessionService, times(0)).addSession(any(), any());
    }

    @Test
    @DisplayName("测试testChat，session不为null")
    @SuppressWarnings("unchecked")
    public void testChatWhenSessionIsNotNull() {
        AppTaskInstance instance = mock(AppTaskInstance.class);
        List<Map<String, Object>> memoryConfigs = TestUtils.buildMemoryConfigs(true, MemoryTypeEnum.CUSTOMIZING.type(),
                "");
        RunContext runContext = new RunContext(new HashMap<>(), new OperationContext());
        runContext.setMemoryConfig(memoryConfigs);
        doNothing().when(instance).run(any(), any());
        TaskInstanceDecorator.create(instance)
                .chat(this.appChatSessionService, this.appChatSSEService)
                .run(runContext, Mockito.mock(ChatSession.class));
        verify(this.appChatSessionService, times(1)).addSession(any(), any());
        verify(this.appChatSSEService, times(2)).send(any(), any());
    }

    @Test
    @DisplayName("测试testChat，session不为null，但memory类型是UserSelect")
    @SuppressWarnings("unchecked")
    public void testChatWhenSessionIsNotNullButMemoryIsUserSelect() {
        AppTaskInstance instance = mock(AppTaskInstance.class);
        List<Map<String, Object>> memoryConfigs = TestUtils.buildMemoryConfigs(true, MemoryTypeEnum.USER_SELECT.type(),
                "");
        RunContext runContext = new RunContext(new HashMap<>(), new OperationContext());
        runContext.setMemoryConfig(memoryConfigs);
        doNothing().when(instance).run(any(), any());
        TaskInstanceDecorator.create(instance)
                .chat(this.appChatSessionService, this.appChatSSEService)
                .run(runContext, Mockito.mock(ChatSession.class));
        verify(this.appChatSessionService, times(1)).addSession(any(), any());
        verify(this.appChatSSEService, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("测试exceptionLog，但session为null")
    public void testExceptionLogWhenThrowException() {
        AppTaskInstance instance = mock(AppTaskInstance.class);
        RunContext runContext = new RunContext(new HashMap<>(), new OperationContext());
        doThrow(new AippException(AippErrCode.FLOW_ERROR)).when(instance).run(any(), any());
        TaskInstanceDecorator.create(instance)
                .exceptionLog(this.appTaskInstanceService, this.logService)
                .run(runContext, null);
        verify(this.appTaskInstanceService, times(1)).update(any(), any());
        verify(this.logService, times(1)).insertLogWithInterception(any(), any(), any());
    }

    @Test
    @DisplayName("测试exceptionLog和Chat一起生效")
    @SuppressWarnings("unchecked")
    public void testChatAndExceptionLog() {
        AppTaskInstance instance = mock(AppTaskInstance.class);
        RunContext runContext = new RunContext(new HashMap<>(), new OperationContext());
        doNothing().when(instance).run(any(), any());
        TaskInstanceDecorator.create(instance)
                .chat(this.appChatSessionService, this.appChatSSEService)
                .exceptionLog(this.appTaskInstanceService, this.logService)
                .run(runContext, Mockito.mock(ChatSession.class));
        verify(this.appChatSessionService, times(1)).addSession(any(), any());
        verify(this.appChatSSEService, times(2)).send(any(), any());
        verify(this.appTaskInstanceService, times(0)).update(any(), any());
        verify(this.logService, times(0)).insertLogWithInterception(any(), any(), any());
    }
}
