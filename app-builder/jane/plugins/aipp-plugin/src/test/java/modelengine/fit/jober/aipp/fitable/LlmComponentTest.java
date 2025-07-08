/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static modelengine.fit.jober.aipp.TestUtils.mockFailAsyncJob;
import static modelengine.fit.jober.aipp.TestUtils.mockResumeFlow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.aipp.prompt.PromptBuilder;
import modelengine.fit.jade.aipp.prompt.PromptMessage;
import modelengine.fit.jade.aipp.prompt.PromptStrategy;
import modelengine.fit.jade.aipp.prompt.UserAdvice;
import modelengine.fit.jade.aipp.prompt.repository.PromptBuilderChain;
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
import modelengine.fit.waterflow.entity.FlowErrorInfo;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.support.FlatChatMessage;
import modelengine.fel.core.chat.support.ToolMessage;
import modelengine.fel.core.template.StringTemplate;
import modelengine.fel.core.template.support.DefaultStringTemplate;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fel.core.tool.ToolInfo;
import modelengine.fel.core.tool.ToolProvider;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.operators.patterns.AbstractAgent;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.aipp.prompt.PromptBuilder;
import modelengine.fit.jade.aipp.prompt.PromptMessage;
import modelengine.fit.jade.aipp.prompt.PromptStrategy;
import modelengine.fit.jade.aipp.prompt.UserAdvice;
import modelengine.fit.jade.aipp.prompt.repository.PromptBuilderChain;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jober.aipp.TestUtils;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.fel.WaterFlowAgent;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AippLogStreamService;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.jade.common.globalization.LocaleService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
    private ToolProvider toolProvider;
    @Mock
    private AippLogService aippLogService;
    @Mock
    private AippLogStreamService aippLogStreamService;
    @Mock
    private AppTaskInstanceService appTaskInstanceService;
    @Mock
    private PromptBuilderChain promptBuilderChain;
    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);
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

    private AbstractAgent<Prompt, Prompt> buildStubAgent(AiProcessFlow<Prompt, Prompt> flow) {
        return new AbstractAgent<Prompt, Prompt>() {
            @Override
            protected AiProcessFlow<Prompt, Prompt> buildFlow() {
                return flow;
            }
        };
    }

    private AbstractAgent<Prompt, Prompt> getWaterFlowAgent(ChatModel model, boolean isAsyncTool) {
        return new WaterFlowAgent(getToolProvider(isAsyncTool), model, ChatOption.custom().build());
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

    private static ToolProvider getToolProvider(boolean isAsyncTool) {
        return new ToolProvider() {
            @Override
            public FlatChatMessage call(ToolCall toolCall, Map<String, Object> toolContext) {
                return FlatChatMessage.from(
                        new ToolMessage("1", JsonUtils.toJsonString(TestUtils.DUMMY_CHILD_INSTANCE_ID)));
            }

            @Override
            public List<ToolInfo> getTool(List<String> name) {
                Map<String, Object> context = new HashMap<>();
                context.put("isAsync", isAsyncTool);
                ToolInfo tool = ToolInfo.custom().extensions(context).build();
                return Collections.singletonList(tool);
            }
        };
    }

    @Test
    @Disabled("多线程阻塞，无法唤醒")
    void shouldOkWhenWaterFlowAgentWithoutAsyncTool() throws InterruptedException {
        // stub
        this.prepareModel();
        AbstractAgent<Prompt, Prompt> agent = this.getWaterFlowAgent(this.buildChatStreamModel(null), false);
        LlmComponent llmComponent = getLlmComponent(agent);

        // mock
        Mockito.doNothing().when(aippLogStreamService).send(any());
        CountDownLatch countDownLatch = mockResumeFlow(flowInstanceService);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            AppTaskInstance instance = ObjectUtils.cast(invocation.getArgument(0));
            Assertions.assertEquals("0123", instance.getEntity().getLlmOutput());
            return null;
        }).when(this.appTaskInstanceService).update(any(), any());

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
    }

    @Test
    void shouldFailWhenWaterFlowAgentThrowException() throws InterruptedException {
        // stub
        this.prepareModel();
        AbstractAgent<Prompt, Prompt> agent = this.getWaterFlowAgent(this.buildChatStreamModel("exceptionMsg"), false);
        LlmComponent llmComponent = getLlmComponent(agent);

        // mock
        CountDownLatch countDownLatch = mockFailAsyncJob(flowInstanceService);

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
    }

    @Test
    @Disabled("多线程阻塞，无法唤醒")
    void shouldOkWhenWaterFlowAgentWithAsyncTool() throws InterruptedException {
        // stub
        this.prepareModel();
        AbstractAgent<Prompt, Prompt> agent = this.getWaterFlowAgent(this.buildChatStreamModel(null), true);
        LlmComponent llmComponent = getLlmComponent(agent);

        AtomicInteger resCnt = new AtomicInteger(0);

        // mock
        CountDownLatch countDownLatch = mockResumeFlow(flowInstanceService);

        Mockito.doAnswer((Answer<Void>) invocation -> {
            AppTaskInstance instance = ObjectUtils.cast(invocation.getArgument(0));
            String childInstanceId = instance.getEntity().getChildInstanceId();
            if (childInstanceId != null) {
                Assertions.assertEquals(TestUtils.DUMMY_CHILD_INSTANCE_ID, childInstanceId);
                Map<String, Object> businessData = new HashMap<>();
                businessData.put(AippConst.BS_AIPP_FINAL_OUTPUT, "tool_data");
                businessData.put(AippConst.PARENT_INSTANCE_ID, TestUtils.DUMMY_FLOW_INSTANCE_ID);
                businessData.put(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM, true);
                businessData.put(AippConst.BS_LLM_ENABLE_LOG, false);
                llmComponent.callback(TestUtils.buildFlowDataWithExtraConfig(businessData, null));
            } else {
                resCnt.getAndIncrement();
                Assertions.assertEquals("0123", instance.getEntity().getLlmOutput());
            }
            return null;
        }).when(this.appTaskInstanceService).update(any(), any());
        Mockito.when(toolProvider.getTool(any())).thenReturn(Collections.emptyList());

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
        Assertions.assertEquals(1, resCnt.get());
    }

    @Test
    void shouldOkWhenNoTool() throws InterruptedException {
        // stub
        this.prepareModel();
        AiProcessFlow<Prompt, Prompt> testAgent = AiFlows.<Prompt>create()
                .map(m -> ObjectUtils.<Prompt>cast(ChatMessages.from(new AiMessage("bad"))))
                .close();
        AbstractAgent<Prompt, Prompt> agent = this.buildStubAgent(testAgent);
        LlmComponent llmComponent = getLlmComponent(agent);

        // mock
        CountDownLatch countDownLatch = mockResumeFlow(flowInstanceService);

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
    }

    @Test
    void shouldFailedWhenNoTool() throws InterruptedException {
        // stub
        this.prepareModel();
        AiProcessFlow<Prompt, Prompt> testAgent = AiFlows.<Prompt>create().just(m -> {
            int err = 1 / 0;
        }).close();
        AbstractAgent<Prompt, Prompt> agent = this.buildStubAgent(testAgent);
        LlmComponent llmComponent = new LlmComponent(flowInstanceService,
                toolProvider,
                agent,
                aippLogService,
                null,
                serializer,
                aippModelCenter,
                promptBuilderChain,
                this.appTaskInstanceService,
                null);

        // mock
        CountDownLatch countDownLatch = mockFailAsyncJob(flowInstanceService);

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
    }

    @Test
    void shouldOkWhenUseWorkflowNoReturn() throws InterruptedException {
        AtomicReference<Prompt> prompt = new AtomicReference<>();
        // stub
        this.prepareModel();
        AiProcessFlow<Prompt, Prompt> testAgent = AiFlows.<Prompt>create()
                .just(prompt::set)
                .map(m -> ObjectUtils.<Prompt>cast(ChatMessages.from(new ToolMessage("1", "\"tool_async\""))))
                .close();
        AbstractAgent<Prompt, Prompt> agent = this.buildStubAgent(testAgent);
        LlmComponent llmComponent = new LlmComponent(flowInstanceService,
                toolProvider,
                agent,
                aippLogService,
                null,
                serializer,
                aippModelCenter,
                promptBuilderChain,
                this.appTaskInstanceService,
                null);

        // mock
        CountDownLatch countDownLatch = mockResumeFlow(flowInstanceService);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            AppTaskInstance instance = ObjectUtils.cast(invocation.getArgument(0));
            String childInstanceId = instance.getEntity().getChildInstanceId();
            Assertions.assertEquals("tool_async", childInstanceId);
            Map<String, Object> businessData = new HashMap<>();
            businessData.put(AippConst.PARENT_INSTANCE_ID, TestUtils.DUMMY_FLOW_INSTANCE_ID);
            businessData.put(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM, false);
            llmComponent.callback(TestUtils.buildFlowDataWithExtraConfig(businessData, null));
            return null;
        }).when(this.appTaskInstanceService).update(any(), any());

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
        assertThat(prompt.get().messages()).hasSize(3);
    }

    @Test
    void shouldOkWhenUseWorkflowNormalReturn() throws InterruptedException {
        // stub
        this.prepareModel();
        AtomicBoolean flag = new AtomicBoolean(false);
        List<Prompt> prompts = new ArrayList<>();
        AiProcessFlow<Prompt, Prompt> testAgent = AiFlows.<Prompt>create().just(m -> prompts.add(m)).map(m -> {
            ChatMessages chatMessages = ChatMessages.from(m.messages());
            if (flag.get()) {
                chatMessages.add(new AiMessage("bad"));
            } else {
                chatMessages.add(new AiMessage("", Collections.singletonList(ToolCall.custom().id("id").build())));
                chatMessages.add(new ToolMessage("1", "\"tool_async\""));
            }
            return ObjectUtils.<Prompt>cast(chatMessages);
        }).just(m -> flag.set(true)).close();
        AbstractAgent<Prompt, Prompt> agent = this.buildStubAgent(testAgent);
        LlmComponent llmComponent = new LlmComponent(flowInstanceService,
                toolProvider,
                agent,
                aippLogService,
                null,
                serializer,
                aippModelCenter,
                promptBuilderChain,
                this.appTaskInstanceService,
                null);

        // mock
        CountDownLatch countDownLatch = mockResumeFlow(flowInstanceService);

        Mockito.doAnswer((Answer<Void>) invocation -> {
            AppTaskInstance instance = ObjectUtils.cast(invocation.getArgument(0));
            generateBusinessDataAndCallBack(instance.getEntity().getChildInstanceId(), instance.getEntity().getInfos(),
                    llmComponent);
            return null;
        }).when(this.appTaskInstanceService).update(any(), any());
        Mockito.when(toolProvider.getTool(any())).thenReturn(Collections.emptyList());

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();

        assertThat(prompts).hasSize(2);
        Assertions.assertEquals(3, prompts.get(0).messages().size());
        List<? extends ChatMessage> messages = prompts.get(1).messages();
        assertThat(messages.get(messages.size() - 1).text()).isEqualTo("tool_data");
        assertThat(messages).hasSize(5);
    }

    private void generateBusinessDataAndCallBack(String childInstanceId, Map<String, Object> value,
            LlmComponent llmComponent) {
        if (childInstanceId != null) {
            Assertions.assertEquals("tool_async", childInstanceId);
            Map<String, Object> businessData = new HashMap<>();
            businessData.put(AippConst.BS_AIPP_FINAL_OUTPUT, "tool_data");
            businessData.put(AippConst.PARENT_INSTANCE_ID, TestUtils.DUMMY_FLOW_INSTANCE_ID);
            businessData.put(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM, true);
            llmComponent.callback(TestUtils.buildFlowDataWithExtraConfig(businessData, null));
        } else {
            Assertions.assertEquals("bad", value.get("llmOutput"));
        }
    }

    @Test
    void shouldOkWhenUseMaxMemoryRounds() throws InterruptedException {
        // stub
        this.prepareModel();
        AiProcessFlow<Prompt, Prompt> testAgent =
                AiFlows.<Prompt>create()
                        .just(m -> {
                            List<? extends ChatMessage> messages = m.messages();
                            Assertions.assertEquals(2, messages.size());
                        })
                        .map(m -> ObjectUtils.<Prompt>cast(ChatMessages.from(new AiMessage("bad"))))
                        .close();
        AbstractAgent<Prompt, Prompt> agent = this.buildStubAgent(testAgent);
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
    void shouldFailLLmNodeWhenHandleGivenWorkflowException() throws InterruptedException {
        // given
        this.prepareModel();
        AbstractAgent<Prompt, Prompt> agent = this.getWaterFlowAgent(this.buildChatStreamModel(null), true);
        LlmComponent llmComponent = getLlmComponent(agent);

        CountDownLatch countDownLatch = mockFailAsyncJob(flowInstanceService);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            AppTaskInstance instance = ObjectUtils.cast(invocation.getArgument(0));
            Assertions.assertNotNull(instance.getEntity().getChildInstanceId());
            Map<String, Object> businessData = new HashMap<>();
            businessData.put(AippConst.PARENT_INSTANCE_ID, TestUtils.DUMMY_FLOW_INSTANCE_ID);
            llmComponent.handleException("nodeId", TestUtils.buildFlowDataWithExtraConfig(businessData, null),
                    new FlowErrorInfo(123, "error", null, null, null, null));
            return null;
        }).when(this.appTaskInstanceService).update(any(), any());

        // when
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));

        // then
        countDownLatch.await();
    }

    @Test
    void shouldFailWhenDebugAndLlmNotAvailable() throws InterruptedException {
        // given
        AbstractAgent<Prompt, Prompt> agent = this.getWaterFlowAgent(this.buildChatStreamModel(null), true);
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

    private LlmComponent getLlmComponent(final AbstractAgent<Prompt, Prompt> agent) {
        return new LlmComponent(flowInstanceService,
                toolProvider,
                agent,
                aippLogService,
                aippLogStreamService,
                serializer,
                aippModelCenter,
                promptBuilderChain,
                this.appTaskInstanceService,
                null);
    }

    private void prepareModel() {
        Mockito.when(toolProvider.getTool(any())).thenReturn(Collections.emptyList());
        doAnswer(invocationOnMock -> {
            Object advice = invocationOnMock.getArgument(0);
            Object context = invocationOnMock.getArgument(1);
            return new PromptBuilderStub().build(ObjectUtils.cast(advice), ObjectUtils.cast(context));
        }).when(this.promptBuilderChain).build(any(), any());

        when(this.aippModelCenter.getModelAccessInfo(any(), any(), any())).thenReturn(
                ModelAccessInfo.builder().tag("tag").build());
    }
}