/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.spi.FlowExceptionService;
import modelengine.jade.common.globalization.LocaleService;

import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.FitableNotFoundException;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.flowable.emitter.DefaultEmitter;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.common.globalization.LocaleService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link AippFlowExceptionHandle} 的测试类。
 *
 * @author 鲁为
 * @since 2024-08-20
 */
@ExtendWith(MockitoExtension.class)
public class AippFlowExceptionHandleTest {
    private static final String UI_WORD_KEY = "aipp.fitable.AippFlowExceptionHandle";

    private static final String UI_WORD_KEY_HINT = "aipp.fitable.AippFlowExceptionHandle.hint";

    private AippFlowExceptionHandle aippFlowExceptionHandle;

    @Mock
    private AippLogService aippLogService;

    @Mock
    private MetaInstanceService metaInstanceService;

    @Mock
    private LocaleService localeService;

    @Mock
    private AppChatSessionService appChatSessionService;

    @Mock
    private ToolExceptionHandle toolExceptionHandle;

    @Mock
    private BrokerClient brokerClient;

    @BeforeEach
    void setUp() {
        this.aippFlowExceptionHandle = new AippFlowExceptionHandle(this.aippLogService,
                this.metaInstanceService,
                this.localeService,
                this.appChatSessionService,
                this.toolExceptionHandle,
                this.brokerClient);
    }

    @Test
    @DisplayName("测试构造方法")
    void shouldSuccessWhenConstruct() {
        String opContext = "{\"tenantId\": \"test\"," + "\"operator\": \"test\"," + "\"globalUserId\":\"test\","
                + "\"account\": \"account\"," + "\"employeeNumber\": \"employeeNumber\"," + "\"name\": \"name\","
                + "\"operatorIp\": \"operatorIp\"," + "\"sourcePlatform\": \"sourcePlatform\","
                + "\"language\": \"language\"}";
        List<Map<String, Object>> flowData = Arrays.asList(MapBuilder.<String, Object>get()
                .put(AippConst.BS_DATA_KEY,
                        MapBuilder.<String, Object>get()
                                .put(AippConst.BS_HTTP_CONTEXT_KEY, opContext)
                                .put(AippConst.BS_META_VERSION_ID_KEY, "test")
                                .put(AippConst.BS_AIPP_INST_ID_KEY, "test")
                                .build())
                .build());
        ChatSession<Object> chatSession = new ChatSession<>(new DefaultEmitter<>(), "123", true, Locale.ENGLISH);
        Mockito.when(this.localeService.localize(any(Locale.class), eq(UI_WORD_KEY))).thenReturn("test");
        Mockito.when(this.localeService.localize(any(Locale.class), eq(UI_WORD_KEY_HINT))).thenReturn("test");
        Mockito.when(this.appChatSessionService.getSession(anyString())).thenReturn(Optional.of(chatSession));
        Mockito.when(this.toolExceptionHandle.getFixErrorMsg(any(), any(), any())).thenReturn("errorMessage");
        Instance instance = new Instance("id",
                MapBuilder.<String, String>get()
                        .put(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.RUNNING.name())
                        .build(),
                new ArrayList<>());
        Mockito.when(this.metaInstanceService.retrieveById(any(), any())).thenReturn(instance);
        FlowErrorInfo flowErrorInfo = new FlowErrorInfo();
        flowErrorInfo.setErrorCode(10000);
        flowErrorInfo.setErrorMessage("errorMessage");
        this.aippFlowExceptionHandle.handleException("nodeId", flowData, flowErrorInfo);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(this.aippLogService).insertErrorLog(stringCaptor.capture(), listCaptor.capture());
        String capturedString = stringCaptor.getValue();
        assertThat(capturedString).isEqualTo("test\ntest: errorMessage");
    }

    @Test
    void shouldCallParentExceptionHandlerWhenHandleGivenParent() {
        String nodeId = "nodeId";
        List<Map<String, Object>> flowData = Arrays.asList(MapBuilder.<String, Object>get()
                .put(AippConst.BS_DATA_KEY,
                        MapBuilder.<String, Object>get()
                                .put(AippConst.BS_AIPP_INST_ID_KEY, "instance")
                                .put(AippConst.PARENT_EXCEPTION_FITABLE_ID, "parent")
                                .build())
                .build());
        FlowErrorInfo flowErrorInfo = new FlowErrorInfo();
        flowErrorInfo.setErrorCode(10000);
        flowErrorInfo.setErrorMessage("errorMessage");

        Mockito.when(this.appChatSessionService.getSession(anyString())).thenReturn(Optional.empty());
        Router router = Mockito.mock(Router.class);
        Invoker invoker = Mockito.mock(Invoker.class);
        Mockito.when(router.route(ArgumentMatchers.argThat(arg -> (arg instanceof FitableIdFilter) && arg.toString()
                .equals("FitableIdFilter{fitableIds=[parent]}")))).thenReturn(invoker);
        Mockito.when(invoker.invoke(nodeId, flowData, flowErrorInfo)).thenReturn(null);
        Mockito.when(this.brokerClient.getRouter(FlowExceptionService.class,
                FlowExceptionService.HANDLE_EXCEPTION_GENERICABLE)).thenReturn(router);

        this.aippFlowExceptionHandle.handleException(nodeId, flowData, flowErrorInfo);

        Mockito.verify(invoker, times(1)).invoke(nodeId, flowData, flowErrorInfo);
    }

    @Test
    void shouldNotThrowWhenHandleGivenParentThrowFitException() {
        String nodeId = "nodeId";
        List<Map<String, Object>> flowData = Arrays.asList(MapBuilder.<String, Object>get()
                .put(AippConst.BS_DATA_KEY,
                        MapBuilder.<String, Object>get()
                                .put(AippConst.BS_AIPP_INST_ID_KEY, "instance")
                                .put(AippConst.PARENT_EXCEPTION_FITABLE_ID, "parent")
                                .build())
                .build());
        FlowErrorInfo flowErrorInfo = new FlowErrorInfo();
        flowErrorInfo.setErrorCode(10000);
        flowErrorInfo.setErrorMessage("errorMessage");

        Mockito.when(this.appChatSessionService.getSession(anyString())).thenReturn(Optional.empty());
        Mockito.when(this.brokerClient.getRouter(FlowExceptionService.class,
                        FlowExceptionService.HANDLE_EXCEPTION_GENERICABLE))
                .thenThrow(new FitableNotFoundException("not found"));

        Assertions.assertDoesNotThrow(() -> this.aippFlowExceptionHandle.handleException(nodeId,
                flowData,
                flowErrorInfo));
    }
}
