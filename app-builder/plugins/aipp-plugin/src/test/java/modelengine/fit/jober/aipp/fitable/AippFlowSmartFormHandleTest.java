/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AppBuilderFormService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link AippFlowSmartFormHandle} 的测试类。
 *
 * @author 吴宇伦
 * @since 2024-09-09
 */
@ExtendWith(MockitoExtension.class)
public class AippFlowSmartFormHandleTest {
    @Mock
    private AppBuilderFormService appBuilderFormService;

    @Mock
    private AppTaskService appTaskService;

    @Mock
    private AppTaskInstanceService appTaskInstanceService;

    @Mock
    private AppChatSseService appChatSseService;

    @Mock
    private AippLogService aippLogService;

    @Mock
    private AppVersionService appVersionService;

    private AippFlowSmartFormHandle service;

    @BeforeEach
    void setUp() {
        this.service = new AippFlowSmartFormHandle(this.appBuilderFormService, this.appChatSseService,
                this.aippLogService, this.appTaskService, this.appTaskInstanceService, this.appVersionService, null,
                null, null);
    }

    @Test
    void testHandleSmartForm() {
        List<Map<String, Object>> flowData = Collections.singletonList(MapBuilder.<String, Object>get()
                .put(AippConst.BS_DATA_KEY, MapBuilder.<String, Object>get()
                        .put(AippConst.BS_NODE_ID_KEY, "123")
                        .put(AippConst.PARENT_INSTANCE_ID, "123")
                        .put(AippConst.BS_AIPP_INST_ID_KEY, "123")
                        .put(AippConst.BS_CHAT_ID, "123")
                        .put(AippConst.BS_HTTP_CONTEXT_KEY, "{\"account\":\"123\"}")
                        .put(AippConst.BS_AT_CHAT_ID, "atChatId")
                        .put(AippConst.BS_META_VERSION_ID_KEY, "version")
                        .put(AippConst.CONTEXT_APP_ID, "123")
                        .build())
                .build());
        Map<String, Object> defaultValue = new HashMap<>();
        defaultValue.put("hello", "world");
        AppBuilderFormProperty formProperty =
                AppBuilderFormProperty.builder().formId("id1").name("fp1").defaultValue(defaultValue).build();
        List<AppBuilderFormProperty> list = new ArrayList<>();
        list.add(formProperty);
        AppBuilderForm form = AppBuilderForm.builder().id("id1").name("form1").tenantId("tenantId").build();
        Mockito.when(this.appBuilderFormService.selectWithId(anyString())).thenReturn(form);
        AppVersion mockApp = mock(AppVersion.class);
        Mockito.when(this.appVersionService.retrieval(anyString())).thenReturn(mockApp);
        Mockito.when(mockApp.getFormProperties()).thenReturn(list);
        this.service.handleSmartForm(flowData, "111");
        verify(this.appChatSseService, times(1)).sendToAncestorLastData(any(), any());
    }
}
