/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import modelengine.fit.dynamicform.DynamicFormMetaService;
import modelengine.fit.dynamicform.DynamicFormService;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.service.impl.AippRunTimeServiceImpl;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.globalization.LocaleService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

/**
 * 为{@link modelengine.fit.jober.aipp.service.impl.AippRunTimeServiceImpl} 提供测试
 *
 * @author 姚江
 * @since 2024-08-13
 */
@ExtendWith(MockitoExtension.class)
public class AippRuntimeServiceImplTest {
    @InjectMocks
    private AippRunTimeServiceImpl aippRunTimeService;

    @Mock
    private AopAippLogService aopAippLogServiceMock;
    @Mock
    private DynamicFormMetaService dynamicFormMetaServiceMock;
    @Mock
    private DynamicFormService dynamicFormService;
    @Mock
    private FlowInstanceService flowInstanceService;
    @Mock
    private UploadedFileManageService uploadedFileManageService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpClassicClientFactory httpClientFactory;
    @Mock
    private AippLogService aippLogService;
    @Mock
    private BrokerClient client;
    @Mock
    private AppBuilderFormRepository formRepository;
    @Mock
    private AppBuilderFormPropertyRepository formPropertyRepository;
    @Mock
    private FlowsService flowsService;
    @Mock
    private AippStreamService aippStreamService;
    @Mock
    private AopAippLogService aopAippLogService;
    @Mock
    private AppChatSseService appChatSSEService;
    @Mock
    private AippLogService logService;
    @Mock
    private AppBuilderAppFactory appFactory;
    @Mock
    private LocaleService localeService;
    private MockedStatic<UserContextHolder> opContextHolderMock;
    @Mock
    private AppChatSessionService appChatSessionService;
    @Mock
    private Emitter<Object> emitter;
    @Mock
    private AppTaskService appTaskService;
    @Mock
    private AppTaskInstanceService appTaskInstanceService;
    @Mock
    private AppVersionService appVersionService;

    @BeforeEach
    void setUp() {
        this.opContextHolderMock = mockStatic(UserContextHolder.class);
        opContextHolderMock.when(UserContextHolder::get).thenReturn(new UserContext("Jane", "127.0.0.1", "en"));
    }

    @AfterEach
    void teardown() {
        this.opContextHolderMock.close();
    }

    @Test
    @DisplayName("测试startFlowWithUserSelectMemory方法")
    void testStartFlowWithUserSelectMemory() {
        Map<String, Object> businessData = MapBuilder.<String, Object>get().build();
        Map<String, Object> initContext =
                MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, businessData).build();
        AppTaskInstance appTaskInstance = Mockito.mock(AppTaskInstance.class);
        AppTask appTask = Mockito.mock(AppTask.class);
        when(appTaskService.getTaskById(any(), any())).thenReturn(Optional.of(appTask));
        when(this.appTaskInstanceService.getTaskId(eq("instanceId"))).thenReturn("versionId");
        when(this.appTaskInstanceService.getInstance(any(), any(), any())).thenReturn(Optional.of(appTaskInstance));

        Assertions.assertDoesNotThrow(() -> this.aippRunTimeService.startFlowWithUserSelectMemory("instanceId",
                initContext,
                new OperationContext(),
                true));
    }

    @Test
    @DisplayName("测试createAippInstance方法")
    @Disabled
    void testCreateAippInstance() {
        OperationContext context = new OperationContext();
        context.setTenantId("testTenantId");
        context.setOperator("UT 123456");
        Map<String, Object> initContext = this.genInitContext();
        AppTask appTask = Mockito.mock(AppTask.class);
        when(this.appTaskService.getLatest(any(), any(), any())).thenReturn(Optional.of(appTask));
        Assertions.assertDoesNotThrow(() -> this.aippRunTimeService.createAippInstance("aipp_id",
                "version",
                initContext,
                context));
    }

    @Test
    @DisplayName("测试createLatestAippInstanceByAppId方法")
    void TestCreateLatestAippInstanceByAppId() {
        OperationContext context = new OperationContext();
        context.setTenantId("testTenantId");
        context.setOperator("UT 123456");
        Map<String, Object> businessData = MapBuilder.<String, Object>get().build();
        Map<String, Object> initContext =
                MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, businessData).build();

        AppTask appTask = Mockito.mock(AppTask.class);

        AppVersion appVersion = mock(AppVersion.class);
        when(appVersion.getLatestTask(any())).thenReturn(appTask);

        when(this.appVersionService.retrieval(anyString())).thenReturn(appVersion);

        Assertions.assertDoesNotThrow(
            () -> this.aippRunTimeService.createLatestAippInstanceByAppId("app_id", true, initContext, context));
    }

    private Map<String, Object> genInitContext() {
        Map<String, Object> businessData =
                MapBuilder.<String, Object>get().put(AippConst.BS_AIPP_QUESTION_KEY, "你好").build();
        return MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, businessData).build();
    }
}
