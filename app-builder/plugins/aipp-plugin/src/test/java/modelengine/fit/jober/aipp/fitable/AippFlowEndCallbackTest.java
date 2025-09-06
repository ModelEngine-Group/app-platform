/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.formatter.ItemType;
import modelengine.fit.jade.aipp.formatter.MessageItem;
import modelengine.fit.jade.aipp.formatter.OutputFormatterChain;
import modelengine.fit.jade.aipp.formatter.OutputMessage;
import modelengine.fit.jade.aipp.formatter.constant.Constant;
import modelengine.fit.jade.aipp.formatter.support.ResponsibilityResult;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.TestUtils;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AppBuilderFormService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.jade.app.engine.metrics.service.ConversationRecordService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link AippFlowEndCallback} 的测试。
 *
 * @author 刘信宏
 * @since 2024-08-22
 */
@FitTestWithJunit(includeClasses = {AippFlowEndCallback.class})
class AippFlowEndCallbackTest {
    @Mock
    private AippLogService aippLogService;
    @Mock
    private ConversationRecordService conversationRecordService;
    @Mock
    private AppBuilderFormService formService;
    @Mock
    private AppChatSseService appChatSseService;
    @Mock
    private AppBuilderFormRepository formRepository;
    @Mock
    private AppBuilderAppFactory appFactory;
    @Mock
    private OutputFormatterChain formatterChain;
    @Mock
    private AppTaskService appTaskService;
    @Mock
    private AppTaskInstanceService appTaskInstanceService;

    @Fit
    private AippFlowEndCallback aippFlowEndCallback;

    @AfterEach
    void tearDown() {
        clearInvocations(this.conversationRecordService, this.aippLogService);
    }

    private static Map<String, Object> buildBusinessData() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("tools", Collections.emptyList());
        businessData.put("workflows", Collections.emptyList());
        businessData.put("model", "test_model");
        businessData.put("temperature", 0.7);
        businessData.put("systemPrompt", "");
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(new OperationContext()));
        businessData.put(AippConst.BS_AIPP_ID_KEY, "LlmComponentTest");
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, TestUtils.DUMMY_FLOW_INSTANCE_ID);
        businessData.put(AippConst.BS_AIPP_FINAL_OUTPUT, "tool_data");
        businessData.put(AippConst.PARENT_INSTANCE_ID, TestUtils.DUMMY_FLOW_INSTANCE_ID);
        businessData.put(AippConst.BS_META_VERSION_ID_KEY, "version");
        businessData.put(AippConst.INSTANCE_START_TIME, LocalDateTime.now());
        return businessData;
    }

    @Test
    void test_callback_should_ok_when_test_data_combination() {
        AppBuilderFormPropertyRepository formPropertyRepository = mock(AppBuilderFormPropertyRepository.class);
        AppBuilderForm appBuilderForm = new AppBuilderForm(formPropertyRepository);
        when(this.formService.selectWithId(anyString())).thenReturn(appBuilderForm);
        when(this.appTaskService.getTaskById(any(), any())).thenReturn(Optional.of(TestUtils.buildTask()));
        when(this.formatterChain.handle(any())).thenReturn(Optional.empty());

        this.aippFlowEndCallback.callback(TestUtils.buildFlowDataWithExtraConfig(buildBusinessData(), null));

        verify(this.conversationRecordService).insertConversationRecord(any());
    }

    @Test
    void test_callback_should_ok_when_final_output_with_map() {
        AppBuilderFormPropertyRepository formPropertyRepository = mock(AppBuilderFormPropertyRepository.class);
        AppBuilderForm appBuilderForm = new AppBuilderForm(formPropertyRepository);
        when(this.formService.selectWithId(anyString())).thenReturn(appBuilderForm);
        when(this.appTaskService.getTaskById(any(), any())).thenReturn(Optional.of(TestUtils.buildTask()));
        when(this.formatterChain.handle(any())).thenReturn(Optional.empty());

        Map<String, Object> businessData = buildBusinessData();
        businessData.put(AippConst.BS_AIPP_FINAL_OUTPUT, MapBuilder.<String, String>get()
                .put("key0", "value0").put("key1", "value1").build());
        this.aippFlowEndCallback.callback(TestUtils.buildFlowDataWithExtraConfig(businessData, null));

        verify(this.formatterChain).handle(argThat(args -> {
            Map<String, String> argMap = ObjectUtils.cast(args);
            assertThat(argMap.values().size()).isEqualTo(2);
            return true;
        }));
        verify(this.conversationRecordService).insertConversationRecord(any());
    }

    @Test
    void should_ok_when_callback_with_normal_formatter_chain() {
        AppBuilderFormPropertyRepository formPropertyRepository = mock(AppBuilderFormPropertyRepository.class);
        AppBuilderForm appBuilderForm = new AppBuilderForm(formPropertyRepository);
        when(this.formService.selectWithId(anyString())).thenReturn(appBuilderForm);
        when(this.appTaskService.getTaskById(any(), any())).thenReturn(Optional.of(TestUtils.buildTask()));
        doAnswer(args -> {
            Object argument = args.getArgument(0);
            return Optional.of(new ResponsibilityResult(new OutputMessageStub(argument), Constant.LLM_OUTPUT));
        }).when(this.formatterChain).handle(any());

        this.aippFlowEndCallback.callback(TestUtils.buildFlowDataWithExtraConfig(buildBusinessData(), null));
        verify(this.aippLogService).insertLogWithInterception(eq(AippInstLogType.META_MSG.name()), any(), any());
    }

    @Test
    @Disabled
    void test_callback_should_ok_when_test_with_form_data() {
        AppBuilderFormPropertyRepository formPropertyRepository = mock(AppBuilderFormPropertyRepository.class);
        AppBuilderForm appBuilderForm = new AppBuilderForm(formPropertyRepository);
        when(this.formService.selectWithId(anyString())).thenReturn(appBuilderForm);
        when(this.appTaskService.getTaskById(any(), any())).thenReturn(Optional.of(TestUtils.buildTask()));
        doNothing().when(this.appChatSseService).sendToAncestorLastData(anyString(), any());
        when(this.formRepository.selectWithId(anyString())).thenReturn(null);
        when(this.formatterChain.handle(any())).thenReturn(Optional.empty());
        AppBuilderApp app = AppBuilderApp.builder()
                .formProperties(Collections.emptyList())
                .build();
        when(this.appFactory.create("appId1")).thenReturn(app);

        Map<String, Object> businessData = buildBusinessData();
        businessData.put(AippConst.BS_END_FORM_ID_KEY, "testFormId");

        this.aippFlowEndCallback.callback(TestUtils.buildFlowDataWithExtraConfig(businessData, null));
        verify(this.conversationRecordService).insertConversationRecord(any());
        verify(this.aippLogService).insertLogWithInterception(eq(AippInstLogType.FORM.name()), any(), any());
    }

    static class MessageItemStub implements MessageItem {
        private final Object data;

        MessageItemStub(Object data) {
            this.data = data;
        }

        @Override
        public ItemType type() {
            return ItemType.TEXT_WITH_REFERENCE;
        }

        @Override
        public String data() {
            return data.toString();
        }

        @Override
        public Map<String, Object> reference() {
            return Collections.emptyMap();
        }
    }

    static class OutputMessageStub implements OutputMessage {
        private final Object data;

        OutputMessageStub(Object data) {
            this.data = data;
        }

        @Override
        public List<MessageItem> items() {
            return Collections.singletonList(new MessageItemStub(this.data));
        }

        @Override
        public String text() {
            return this.items().stream().map(MessageItem::data).collect(Collectors.joining("\n"));
        }
    }
}