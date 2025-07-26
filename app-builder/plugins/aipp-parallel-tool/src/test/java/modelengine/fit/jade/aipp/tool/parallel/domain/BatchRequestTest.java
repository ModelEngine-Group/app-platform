/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.aipp.tool.parallel.domain;

import modelengine.fel.tool.service.ToolExecuteService;
import modelengine.fit.jade.aipp.tool.parallel.entities.Config;
import modelengine.fit.jade.aipp.tool.parallel.entities.ToolCall;
import modelengine.fit.jade.aipp.tool.parallel.support.AippInstanceStatus;
import modelengine.fit.jade.aipp.tool.parallel.support.TaskExecutor;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link BatchRequest} 的测试类。
 *
 * @author 宋永坦
 * @since 2025-04-29
 */
@ExtendWith(MockitoExtension.class)
class BatchRequestTest {
    @Mock
    private ToolExecuteService toolExecuteService;

    @Mock
    private TaskExecutor taskExecutor;

    @Mock
    private AippInstanceStatus aippInstanceStatus;

    @Test
    void shouldCallExecutorByConcurrencyWhenPostGivenToolCall() {
        List<ToolCall> toolCalls = Arrays.asList(ToolCall.builder().uniqueName("u1").args(new HashMap<>()).build(),
                ToolCall.builder().uniqueName("u2").args(new HashMap<>()).build(),
                ToolCall.builder().uniqueName("u3").args(new HashMap<>()).build());
        Config config = Config.builder().concurrency(2).build();
        Mockito.doNothing().when(this.taskExecutor).post(Mockito.any());

        BatchRequest batchRequest = new BatchRequest(toolCalls,
                config,
                this.toolExecuteService,
                this.taskExecutor,
                this.aippInstanceStatus,
                null);
        batchRequest.post();

        Mockito.verify(this.taskExecutor, Mockito.times(config.getConcurrency())).post(Mockito.any());
    }

    @Test
    void shouldGetResultWhenAwaitGivenToolCallSuccessfully() {
        List<ToolCall> toolCalls =
                Arrays.asList(ToolCall.builder().uniqueName("u1").args(new HashMap<>()).outputName("1").build(),
                        ToolCall.builder()
                                .uniqueName("u2")
                                .args(MapBuilder.<String, Object>get().put("a", 1).build())
                                .outputName("2")
                                .build());
        Config config = Config.builder().concurrency(1).build();
        Mockito.doAnswer((Answer<Void>) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(this.taskExecutor).post(Mockito.any());
        Mockito.when(this.aippInstanceStatus.isRunning(Mockito.any())).thenReturn(true);
        Mockito.when(this.toolExecuteService.execute(Mockito.eq(toolCalls.get(0).getUniqueName()),
                Mockito.eq("{}"))).thenReturn("1");
        Mockito.when(this.toolExecuteService.execute(Mockito.eq(toolCalls.get(1).getUniqueName()),
                Mockito.eq("{\"a\":1}"))).thenReturn("\"2\"");

        BatchRequest batchRequest = new BatchRequest(toolCalls,
                config,
                this.toolExecuteService,
                this.taskExecutor,
                this.aippInstanceStatus,
                null);
        batchRequest.post();
        Map<String, Object> result = batchRequest.await();

        Mockito.verify(this.taskExecutor, Mockito.times(toolCalls.size())).post(Mockito.any());
        Assertions.assertEquals(toolCalls.size(), result.size());
        Assertions.assertInstanceOf(Integer.class, result.get(toolCalls.get(0).getOutputName()));
        Assertions.assertEquals(1, result.get(toolCalls.get(0).getOutputName()));
        Assertions.assertInstanceOf(String.class, result.get(toolCalls.get(1).getOutputName()));
        Assertions.assertEquals("2", result.get(toolCalls.get(1).getOutputName()));
    }

    @Test
    void shouldThrowExceptionWhenAwaitGivenToolCallException() {
        List<ToolCall> toolCalls = Arrays.asList(ToolCall.builder().uniqueName("u1").args(new HashMap<>()).build(),
                ToolCall.builder().uniqueName("u2").args(new HashMap<>()).build());
        Config config = Config.builder().concurrency(1).build();
        Mockito.doAnswer((Answer<Void>) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(this.taskExecutor).post(Mockito.any());
        Mockito.when(this.aippInstanceStatus.isRunning(Mockito.any())).thenReturn(true);
        Mockito.when(this.toolExecuteService.execute(Mockito.eq(toolCalls.get(0).getUniqueName()), Mockito.eq("{}")))
                .thenThrow(new IllegalArgumentException("wrong argument"));

        BatchRequest batchRequest = new BatchRequest(toolCalls,
                config,
                this.toolExecuteService,
                this.taskExecutor,
                this.aippInstanceStatus,
                null);
        batchRequest.post();
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, batchRequest::await);

        Assertions.assertTrue(exception.getMessage().endsWith("uniqueName=u1, index=0, errorMessage=wrong argument]"));
    }

    @Test
    void shouldNotExecuteRemainToolWhenAwaitGivenInstanceNotRunning() {
        List<ToolCall> toolCalls = Arrays.asList(ToolCall.builder().uniqueName("u1").args(new HashMap<>()).build(),
                ToolCall.builder().uniqueName("u2").args(new HashMap<>()).build());
        Config config = Config.builder().concurrency(1).build();
        Mockito.doAnswer((Answer<Void>) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(this.taskExecutor).post(Mockito.any());
        Mockito.when(this.toolExecuteService.execute(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn("1");
        Map<String, Object> context = MapBuilder.<String, Object>get().put("instanceId", "1").build();
        Mockito.when(this.aippInstanceStatus.isRunning(Mockito.same(context))).thenReturn(true).thenReturn(false);

        BatchRequest batchRequest = new BatchRequest(toolCalls,
                config,
                this.toolExecuteService,
                this.taskExecutor,
                this.aippInstanceStatus,
                context);
        batchRequest.post();
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, batchRequest::await);

        Mockito.verify(this.taskExecutor, Mockito.times(2)).post(Mockito.any());
        Mockito.verify(this.toolExecuteService, Mockito.times(1))
                .execute(Mockito.any(String.class), Mockito.any(String.class));
        Assertions.assertTrue(exception.getMessage()
                .endsWith("errorMessage=The instance is not running. [context={instanceId=1}]]"));
    }
}