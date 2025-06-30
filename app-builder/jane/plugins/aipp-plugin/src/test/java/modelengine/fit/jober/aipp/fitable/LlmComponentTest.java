/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static modelengine.fit.jober.aipp.TestUtils.mockFailAsyncJob;
import static modelengine.fit.jober.aipp.TestUtils.mockResumeFlow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import modelengine.fel.engine.operators.models.ChatFlowModel;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.aipp.prompt.PromptBuilder;
import modelengine.fit.jade.aipp.prompt.PromptMessage;
import modelengine.fit.jade.aipp.prompt.PromptStrategy;
import modelengine.fit.jade.aipp.prompt.UserAdvice;
import modelengine.fit.jade.aipp.prompt.repository.PromptBuilderChain;
import modelengine.fit.jade.tool.SyncToolCall;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jober.aipp.TestUtils;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.fel.WaterFlowAgent;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AippLogStreamService;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.waterflow.domain.context.StateContext;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.template.StringTemplate;
import modelengine.fel.core.template.support.DefaultStringTemplate;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.operators.patterns.AbstractAgent;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.jade.store.service.ToolService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link LlmComponent}的测试集
 *
 * @author 易文渊
 * @since 2024-04-23
 */
@ExtendWith(MockitoExtension.class)
public class LlmComponentTest {
    @Mock
    private FlowInstanceService flowInstanceService;
    @Mock
    private ToolService toolService;
    @Mock
    private SyncToolCall syncToolCall;
    @Mock
    private AippLogService aippLogService;
    @Mock
    private AippLogStreamService aippLogStreamService;
    @Mock
    private AppTaskInstanceService appTaskInstanceService;
    @Mock
    private PromptBuilderChain promptBuilderChain;
    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);
    @Mock
    private AippModelCenter aippModelCenter;

    static class PromptBuilderStub implements PromptBuilder {
        @Override
        public Optional<PromptMessage> build(UserAdvice userAdvice, Map<String, Object> context) {
            String sysMessage = userAdvice.getBackground();
            StringTemplate template = new DefaultStringTemplate(userAdvice.getTemplate());
            String humanMessage = template.render(userAdvice.getVariables());
            return Optional.of(new PromptMessage(sysMessage, humanMessage));
        }

        @Override
        public PromptStrategy strategy() {
            return PromptStrategy.CUSTOM;
        }
    }

    private static Map<String, Object> buildLlmTestData() {
        Map<String, Object> businessData = new HashMap<>();
        // prompt
        HashMap<String, Object> prompt = new HashMap<>();
        prompt.put("template", "{{input}}");
        prompt.put("variables", new HashMap<String, String>() {
            {
                put("input", "good");
            }
        });
        businessData.put("prompt", prompt);
        businessData.put(AippConst.BS_AIPP_MEMORIES_KEY, Collections.singletonList(new HashMap<String, String>() {
            {
                put("question", "q1");
                put("answer", "a1");
            }
        }));
        businessData.put("tools", Collections.emptyList());
        businessData.put("workflows", Collections.emptyList());
        businessData.put("model", "test_model");
        businessData.put("temperature", 0.7);
        businessData.put("systemPrompt", "");
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(new OperationContext()));
        businessData.put(AippConst.BS_AIPP_ID_KEY, "LlmComponentTest");
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, TestUtils.DUMMY_FLOW_INSTANCE_ID);
        businessData.put(AippConst.BS_LLM_ENABLE_LOG, true);
        businessData.put(AippConst.BS_CHAT_ID, "31dc9d5f99a749dfbc9aac8322a24c92");
        return businessData;
    }

    private AbstractAgent buildStubAgent(AiProcessFlow<Prompt, ChatMessage> flow) {
        return new AbstractAgent(new ChatFlowModel(Mockito.mock(ChatModel.class), null)) {
            @Override
            protected Prompt doToolCall(List<ToolCall> toolCalls, StateContext ctx) {
                return null;
            }

            @Override
            protected AiProcessFlow<Prompt, ChatMessage> buildFlow() {
                return flow;
            }
        };
    }

    private AbstractAgent getWaterFlowAgent(ChatModel model) {
        return new WaterFlowAgent(this.syncToolCall, model);
    }

    private ChatModel buildChatStreamModel(String exceptionMsg) {
        List<ToolCall> toolCalls = Collections.singletonList(ToolCall.custom().id("id").build());

        AtomicInteger step = new AtomicInteger();
        return (prompt, chatOption) -> Choir.create(emitter -> {
            if (exceptionMsg != null) {
                emitter.fail(new IllegalStateException(exceptionMsg));
            }
            if (step.getAndIncrement() == 0) {
                emitter.emit(new AiMessage("tool_data", toolCalls));
                emitter.complete();
                return;
            }
            for (int i = 0; i < 4; i++) {
                emitter.emit(new modelengine.fel.core.chat.support.AiMessage(String.valueOf(i)));
            }
            emitter.complete();
        });
    }

    @Test
    void shouldFailWhenWaterFlowAgentThrowException() throws InterruptedException {
        // stub
        this.prepareModel();
        AbstractAgent agent = this.getWaterFlowAgent(this.buildChatStreamModel("exceptionMsg"));
        LlmComponent llmComponent = getLlmComponent(agent);

        // mock
        CountDownLatch countDownLatch = mockFailAsyncJob(flowInstanceService);

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
    }

    @Test
    void shouldOkWhenNoTool() throws InterruptedException {
        // stub
        this.prepareModel();
        AiProcessFlow<Prompt, ChatMessage> testAgent = AiFlows.<Prompt>create()
                .map(m -> ObjectUtils.<ChatMessage>cast(new AiMessage("bad")))
                .close();
        AbstractAgent agent = this.buildStubAgent(testAgent);
        LlmComponent llmComponent = getLlmComponent(agent);

        // mock
        CountDownLatch countDownLatch = mockResumeFlow(flowInstanceService);

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
        Mockito.verify(this.toolService, times(0)).getTool(any());
    }

    @Test
    void shouldFailedWhenNoTool() throws InterruptedException {
        // stub
        this.prepareModel();
        AiProcessFlow<Prompt, ChatMessage> testAgent = AiFlows.<Prompt>create().just(m -> {
            throw new RuntimeException("test");
        }).map(m -> ObjectUtils.<ChatMessage>cast(new AiMessage("bad"))).close();
        AbstractAgent agent = this.buildStubAgent(testAgent);
        LlmComponent llmComponent = new LlmComponent(flowInstanceService,
                this.toolService,
                agent,
                aippLogService,
                null,
                serializer,
                aippModelCenter,
                promptBuilderChain,
                this.appTaskInstanceService);

        // mock
        CountDownLatch countDownLatch = mockFailAsyncJob(flowInstanceService);

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
    }

    @Test
    void shouldOkWhenUseMaxMemoryRounds() throws InterruptedException {
        // stub
        this.prepareModel();
        AiProcessFlow<Prompt, ChatMessage> testAgent =
                AiFlows.<Prompt>create()
                        .just(m -> {
                            List<? extends ChatMessage> messages = m.messages();
                            Assertions.assertEquals(2, messages.size());
                        })
                        .map(m -> ObjectUtils.<ChatMessage>cast(new AiMessage("bad")))
                        .close();
        AbstractAgent agent = this.buildStubAgent(testAgent);
        LlmComponent llmComponent = getLlmComponent(agent);

        // mock
        CountDownLatch countDownLatch = mockResumeFlow(flowInstanceService);

        Map<String, Object> businessData = buildLlmTestData();
        businessData.put(AippConst.BS_MAX_MEMORY_ROUNDS, 0);
        businessData.put("systemPrompt", "system message");

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(businessData, null));
        countDownLatch.await();
    }

    @Test
    void shouldFailWhenDebugAndLlmNotAvailable() throws InterruptedException {
        // given
        AbstractAgent agent = this.getWaterFlowAgent(this.buildChatStreamModel(null));
        LlmComponent llmComponent = getLlmComponent(agent);

        CountDownLatch countDownLatch = mockFailAsyncJob(flowInstanceService);
        Map<String, String> accessInfo = MapBuilder.<String, String>get()
                .put("serviceName", "internal_test_model")
                .put("tag", "INTERNAL")
                .build();
        Map<String, Object> businessData = buildLlmTestData();
        businessData.put("isDebug", true);
        businessData.put("accessInfo", accessInfo);
        when(this.aippModelCenter.fetchModelList(any(), any(), any())).thenReturn(
                ModelListDto.builder().models(Collections.emptyList()).build());

        // when
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(businessData, null));

        // then
        countDownLatch.await();
    }

    private LlmComponent getLlmComponent(final AbstractAgent agent) {
        return new LlmComponent(flowInstanceService,
                this.toolService,
                agent,
                aippLogService,
                aippLogStreamService,
                serializer,
                aippModelCenter,
                promptBuilderChain,
                this.appTaskInstanceService);
    }

    private void prepareModel() {
        Mockito.lenient().when(this.toolService.getTool(any())).thenReturn(null);
        doAnswer(invocationOnMock -> {
            Object advice = invocationOnMock.getArgument(0);
            Object context = invocationOnMock.getArgument(1);
            return new PromptBuilderStub().build(ObjectUtils.cast(advice), ObjectUtils.cast(context));
        }).when(this.promptBuilderChain).build(any(), any());

        when(this.aippModelCenter.getModelAccessInfo(any(), any(), any())).thenReturn(
                ModelAccessInfo.builder().tag("tag").build());
    }
}