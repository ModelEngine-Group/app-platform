/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fit.jober.aipp.service.impl.RuntimeInfoServiceImpl;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.entity.consts.NodeTypes;
import modelengine.fit.waterflow.domain.enums.FlowNodeStatus;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.entity.FlowNodePublishInfo;
import modelengine.fit.waterflow.entity.FlowPublishContext;
import modelengine.fitframework.flowable.emitter.DefaultEmitter;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * publishSubscriber测试用例.
 *
 * @author 张越
 * @since 2024-07-29
 */
@ExtendWith(MockitoExtension.class)
public class FlowPublishSubscriberTest {
    @Mock
    private AppBuilderRuntimeInfoRepository repository;

    private FlowPublishSubscriber flowPublishSubscriber;

    @Mock
    private AppChatSessionService appChatSessionService;

    @Mock
    private ToolExceptionHandle toolExceptionHandle;

    @Mock
    private AppTaskService appTaskService;

    @Mock
    private AppVersionService appVersionService;

    @Mock
    private AppTaskInstanceService appTaskInstanceService;

    /**
     * 初始化.
     */
    @BeforeEach
    void setUp() {
        RuntimeInfoServiceImpl runtimeInfoService = new RuntimeInfoServiceImpl(null, this.appTaskService,
                this.appTaskInstanceService, this.appVersionService);
        this.flowPublishSubscriber = new FlowPublishSubscriber(this.repository, this.toolExceptionHandle,
                this.appChatSessionService, null, runtimeInfoService);
    }

    /**
     * 测试用例.
     */
    @Test
    void shouldAttributesMatchWhenOnPublish() {
        // before
        FlowPublishContext context = this.buildFlowPublishContext();
        FlowNodePublishInfo publishInfo = this.buildFlowNodePublishInfo(context);

        doReturn(Optional.of(AppTask.asEntity().setAppId("app1").build())).when(this.appTaskService)
                .getLatest(anyString(), anyString(), any(OperationContext.class));

        AppVersion appVersion = mock(AppVersion.class);
        when(appVersion.isPublished()).thenReturn(true);
        doReturn(appVersion).when(this.appVersionService).retrieval(anyString());

        AtomicReference<AppBuilderRuntimeInfo> reference = new AtomicReference<>();
        doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            AppBuilderRuntimeInfo message = ObjectUtils.cast(args[0]);
            reference.set(message);
            return null;
        }).when(this.repository).insertOne(any(AppBuilderRuntimeInfo.class));
        Mockito.when(toolExceptionHandle.getFixErrorMsg(any(FlowErrorInfo.class), eq(Locale.ENGLISH), anyBoolean()))
                .thenReturn("errorMessage");

        FlowErrorInfo flowErrorInfo = this.buildFlowErrorInfo();
        publishInfo.setErrorMsg(flowErrorInfo);

        ChatSession<Object> chatSession = new ChatSession<>(new DefaultEmitter<>(), "123", true, Locale.ENGLISH);
        Mockito.when(this.appChatSessionService.getSession(anyString())).thenReturn(Optional.of(chatSession));
        // when
        this.flowPublishSubscriber.publishNodeInfo(publishInfo);

        // then
        AppBuilderRuntimeInfo info = reference.get();
        assertEquals(info.getTraceId(), "trace1");
        assertEquals(info.getNodeId(), "node1");
        assertEquals(info.getFlowDefinitionId(), "flow1");
        assertEquals(info.getInstanceId(), "instance1");
        assertEquals(info.getNodeType(), NodeTypes.START.getType());
        assertEquals(info.getStatus(), FlowNodeStatus.ARCHIVED.name());
        assertTrue(info.isPublished());
        assertEquals(info.getParameters().size(), 1);
        assertEquals(info.getParameters().get(0).getInput(), "1");
        assertEquals(info.getParameters().get(0).getOutput(), "2");
    }

    private Map<String, Object> buildBusinessData() {
        Map<String, Object> params = new HashMap<>();
        params.put("input", 1);
        params.put("output", 2);

        List<Map<String, Object>> nodeInfo = new ArrayList<>();
        nodeInfo.add(params);

        Map<String, Object> executeInfo = new HashMap<>();
        executeInfo.put("node1", nodeInfo);

        Map<String, Object> internal = new HashMap<>();
        internal.put("executeInfo", executeInfo);

        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, "instance1");
        businessData.put(AippConst.BS_AIPP_ID_KEY, "app1");
        businessData.put(AippConst.BS_AIPP_VERSION_KEY, "version1");
        businessData.put(AippConst.BS_APP_LANGUAGE, "\"en\"");

        OperationContext context = new OperationContext();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(context));
        businessData.put("_internal", internal);
        return businessData;
    }

    private FlowPublishContext buildFlowPublishContext() {
        FlowPublishContext context = new FlowPublishContext();
        context.setTraceId("trace1");
        context.setStatus(FlowNodeStatus.ARCHIVED.name());
        context.setCreateAt(LocalDateTime.now());
        context.setArchivedAt(LocalDateTime.now());
        context.setStage("after");
        return context;
    }

    private FlowNodePublishInfo buildFlowNodePublishInfo(FlowPublishContext context) {
        FlowNodePublishInfo publishInfo = new FlowNodePublishInfo();
        publishInfo.setNodeId("node1");
        publishInfo.setNodeType(NodeTypes.START.getType());
        publishInfo.setFlowDefinitionId("flow1");
        publishInfo.setErrorMsg(null);
        publishInfo.setFlowContext(context);
        publishInfo.setBusinessData(this.buildBusinessData());
        return publishInfo;
    }

    private FlowErrorInfo buildFlowErrorInfo() {
        FlowErrorInfo flowErrorInfo = new FlowErrorInfo();
        flowErrorInfo.setErrorCode(10000);
        flowErrorInfo.setErrorMessage("errorMessage");
        return flowErrorInfo;
    }
}
