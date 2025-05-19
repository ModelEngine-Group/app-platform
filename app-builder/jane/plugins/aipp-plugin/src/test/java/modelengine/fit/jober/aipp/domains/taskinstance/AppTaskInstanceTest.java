/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import static modelengine.fit.jober.aipp.constants.AippConst.BS_CHAT_ID;
import static modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance.GENERICABLE_ID;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.business.MemoryTypeEnum;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.log.AppLog;
import modelengine.fit.jober.aipp.domains.log.repository.AippLogRepository;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.MemoryConfigDto;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.entity.FlowInstanceResult;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * {@link AppTaskInstance} 的测试类。
 *
 * @author 张越
 * @since 2025-01-12
 */
public class AppTaskInstanceTest {
    private AppTaskInstanceFactory factory;
    private AppTaskInstanceService appTaskInstanceService;
    private FlowInstanceService flowInstanceService;
    private BrokerClient client;
    private AppChatSseService appChatSSEService;
    private AippChatMapper aippChatMapper;
    private AippLogRepository aippLogRepository;

    @BeforeEach
    public void setUp() throws Exception {
        this.appTaskInstanceService = mock(AppTaskInstanceService.class);
        this.flowInstanceService = mock(FlowInstanceService.class);
        this.client = mock(BrokerClient.class);
        this.appChatSSEService = mock(AppChatSseService.class);
        this.aippLogRepository = mock(AippLogRepository.class);
        this.aippChatMapper = mock(AippChatMapper.class);
        this.factory = new AppTaskInstanceFactory(this.flowInstanceService, this.client,
                this.appChatSSEService, this.aippChatMapper, this.aippLogRepository);
    }

    @Test
    @DisplayName("测试asCreate")
    public void testAsCreate() {
        TaskInstanceCreateEntity entity = AppTaskInstance.asCreate("taskId", "zy", "name");
        assertEquals("taskId", entity.getTaskId());
        assertEquals("zy", entity.getCreator());
        assertEquals("name", entity.getName());
    }

    @Test
    @DisplayName("测试asUpdate")
    public void testAsUpdate() {
        TaskInstanceUpdateEntity entity = AppTaskInstance.asUpdate("taskId", "instanceId");
        assertEquals("taskId", entity.getTaskId());
        assertEquals("instanceId", entity.getInstanceId());
    }

    @Test
    @DisplayName("测试asQuery")
    public void testAsQuery() {
        TaskInstanceQueryEntity entity = AppTaskInstance.asQuery("create_at", "desc").build().getEntity();
        assertEquals("create_at", entity.getOrder());
        assertEquals("desc", entity.getSort());
    }

    @Test
    @DisplayName("测试 isRunning")
    public void testIsRunning() {
        AppTaskInstance instance = AppTaskInstance.asEntity().setStatus(MetaInstStatusEnum.RUNNING.name()).build();
        assertTrue(instance.isRunning());

        AppTaskInstance instance1 = AppTaskInstance.asEntity().setStatus(MetaInstStatusEnum.READY.name()).build();
        assertFalse(instance1.isRunning());
    }

    @Test
    @DisplayName("测试 is")
    public void testIs() {
        AppTaskInstance instance = AppTaskInstance.asEntity().setStatus(MetaInstStatusEnum.RUNNING.name()).build();
        assertFalse(instance.is(MetaInstStatusEnum.ARCHIVED));
        assertTrue(instance.is(MetaInstStatusEnum.ARCHIVED, MetaInstStatusEnum.RUNNING));
    }

    @Test
    @DisplayName("测试 getParentId, 但parentPath为空")
    public void getParentIdShouldReturnNullWhenIsEmpty() {
        AppTaskInstance instance = this.factory.create(new Instance(), "taskId", this.appTaskInstanceService);
        when(this.aippLogRepository.getParentPath(any())).thenReturn("");
        String parentId = instance.getParentInstanceId();
        assertNull(parentId);
    }

    @Test
    @DisplayName("测试 getParentId, 但parentPath为空")
    public void getParentIdWhenIsNotEmpty() {
        AppTaskInstance instance = this.factory.create(new Instance(), "taskId", this.appTaskInstanceService);
        when(this.aippLogRepository.getParentPath(any())).thenReturn("xxx/bbb");
        String parentId = instance.getParentInstanceId();
        assertEquals("bbb", parentId);
    }

    @Nested
    @DisplayName("测试 run 接口")
    class TestRun {
        @Test
        @DisplayName("当上下文中没有AppTask对象时，抛出Aipp异常.")
        public void testNoAppTaskThrowsAippException() {
            // given.
            Instance instance = new Instance();
            instance.setId("task_instance_1");
            AppTaskInstance appTaskInstance = AppTaskInstanceTest.this.factory.create(instance, "taskId",
                    appTaskInstanceService);

            Map<String, Object> businessData = new HashMap<>();
            businessData.put(BS_CHAT_ID, "chat_id");

            OperationContext context = buildOperation();
            RunContext runContext = new RunContext(businessData, context);

            // when.
            // then.
            assertThrows(AippException.class, () -> appTaskInstance.run(runContext));
        }

        @Test
        @DisplayName("测试带有memory，并且类型是byConversationTurn")
        public void testShouldUseMemoryAndMemoryTypeIsByConversationTurn() {
            // given.
            List<Map<String, Object>> memoryConfigs = modelengine.fit.jober.aipp.domains.taskinstance.TestUtils.buildMemoryConfigs(true,
                    MemoryTypeEnum.BY_CONVERSATION_TURN.type(), "10");

            Map<String, Object> businessData = new HashMap<>();
            businessData.put(BS_CHAT_ID, "chat_id");

            OperationContext context = buildOperation();
            RunContext runContext = new RunContext(businessData, context);
            runContext.setMemoryConfig(memoryConfigs);

            AppTask appTask = mock(AppTask.class);
            when(appTask.getEntity()).thenReturn(
                    AppTask.asEntity().setFlowDefinitionId("flow_1").setAppSuiteId("app_1"));
            runContext.setAppTask(appTask);

            AppTaskInstance logInstance = mock(AppTaskInstance.class);
            when(AppTaskInstanceTest.this.aippChatMapper.selectFormerInstanceByChat(any(), anyInt())).thenReturn(
                    List.of("instance1"));
            when(AppTaskInstanceTest.this.appTaskInstanceService.getInstance(anyString(), anyString(),
                    any())).thenReturn(Optional.of(logInstance));

            AppLog appLog = mock(AppLog.class);
            when(logInstance.getLogs()).thenReturn(List.of(appLog));

            when(AppTaskInstanceTest.this.flowInstanceService.startFlow(any(), any(), any())).thenReturn(
                    new FlowInstanceResult("trace1"));
            doNothing().when(AppTaskInstanceTest.this.appTaskInstanceService).update(any(), any());

            // when
            Instance instance = new Instance();
            instance.setId("task_instance_1");
            AppTaskInstance appTaskInstance = AppTaskInstanceTest.this.factory.create(instance, "taskId",
                    appTaskInstanceService);
            appTaskInstance.run(runContext);

            // then.
            assertEquals(true, businessData.get(AippConst.BS_AIPP_USE_MEMORY_KEY));
            verify(AppTaskInstanceTest.this.flowInstanceService, times(1)).startFlow(any(), any(), any());

            ArgumentCaptor<AppTaskInstance> appTaskInstanceCaptor = ArgumentCaptor.forClass(AppTaskInstance.class);
            verify(AppTaskInstanceTest.this.appTaskInstanceService, times(1)).update(appTaskInstanceCaptor.capture(),
                    any());
            AppTaskInstance instanceArg = appTaskInstanceCaptor.getValue();
            assertEquals("taskId", instanceArg.getTaskId());
            assertEquals("task_instance_1", instanceArg.getId());
        }

        @Test
        @DisplayName("测试带有memory，并且类型是NotUserMemory")
        public void testShouldUseMemoryButNotUse() {
            // given.
            List<Map<String, Object>> memoryConfigs = modelengine.fit.jober.aipp.domains.taskinstance.TestUtils.buildMemoryConfigs(true,
                    MemoryTypeEnum.NOT_USE_MEMORY.type(), "");

            Map<String, Object> businessData = new HashMap<>();
            businessData.put(BS_CHAT_ID, "chat_id");

            OperationContext context = buildOperation();
            RunContext runContext = new RunContext(businessData, context);
            runContext.setMemoryConfig(memoryConfigs);

            AppTask appTask = mock(AppTask.class);
            when(appTask.getEntity()).thenReturn(
                    AppTask.asEntity().setFlowDefinitionId("flow_1").setAppSuiteId("app_1"));
            runContext.setAppTask(appTask);

            when(AppTaskInstanceTest.this.flowInstanceService.startFlow(any(), any(), any())).thenReturn(
                    new FlowInstanceResult("trace1"));
            doNothing().when(AppTaskInstanceTest.this.appTaskInstanceService).update(any(), any());

            // when
            Instance instance = new Instance();
            instance.setId("task_instance_1");
            AppTaskInstance appTaskInstance = AppTaskInstanceTest.this.factory.create(instance, "taskId",
                    appTaskInstanceService);
            appTaskInstance.run(runContext);

            // then.
            assertEquals(false, businessData.get(AippConst.BS_AIPP_USE_MEMORY_KEY));
        }

        @Test
        @DisplayName("测试带有memory，并且类型是Customizing，但不存在fitableId")
        public void testShouldUseMemoryAndCustomizingButFitableIdNotExists() {
            // given.
            List<Map<String, Object>> memoryConfigs = modelengine.fit.jober.aipp.domains.taskinstance.TestUtils.buildMemoryConfigs(true,
                    MemoryTypeEnum.CUSTOMIZING.type(), null);

            Map<String, Object> businessData = new HashMap<>();
            businessData.put(BS_CHAT_ID, "chat_id");

            OperationContext context = buildOperation();
            RunContext runContext = new RunContext(businessData, context);
            runContext.setMemoryConfig(memoryConfigs);

            AppTask appTask = mock(AppTask.class);
            when(appTask.getEntity()).thenReturn(
                    AppTask.asEntity().setFlowDefinitionId("flow_1").setAppSuiteId("app_1"));
            runContext.setAppTask(appTask);

            when(AppTaskInstanceTest.this.flowInstanceService.startFlow(any(), any(), any())).thenReturn(
                    new FlowInstanceResult("trace1"));
            doNothing().when(AppTaskInstanceTest.this.appTaskInstanceService).update(any(), any());

            // when
            Instance instance = new Instance();
            instance.setId("task_instance_1");
            AppTaskInstance appTaskInstance = AppTaskInstanceTest.this.factory.create(instance, "taskId",
                    appTaskInstanceService);
            appTaskInstance.run(runContext);

            // then.
            verify(AppTaskInstanceTest.this.client, times(0)).getRouter(eq(GENERICABLE_ID));
        }

        @Test
        @DisplayName("测试带有memory，并且类型是Customizing， 存在fitableId")
        public void testShouldUseMemoryAndCustomizingButFitableIdExists() {
            // given.
            List<Map<String, Object>> memoryConfigs = modelengine.fit.jober.aipp.domains.taskinstance.TestUtils.buildMemoryConfigs(true,
                    MemoryTypeEnum.CUSTOMIZING.type(), "fitable_id_1");

            Map<String, Object> businessData = new HashMap<>();
            businessData.put(BS_CHAT_ID, "chat_id");

            OperationContext context = buildOperation();
            RunContext runContext = new RunContext(businessData, context);
            runContext.setMemoryConfig(memoryConfigs);

            AppTask appTask = mock(AppTask.class);
            when(appTask.getEntity()).thenReturn(
                    AppTask.asEntity().setFlowDefinitionId("flow_1").setAppSuiteId("app_1"));
            runContext.setAppTask(appTask);

            when(AppTaskInstanceTest.this.flowInstanceService.startFlow(any(), any(), any())).thenReturn(
                    new FlowInstanceResult("trace1"));
            doNothing().when(AppTaskInstanceTest.this.appTaskInstanceService).update(any(), any());

            Router router = mock(Router.class);
            Invoker invoker = mock(Invoker.class);
            when(AppTaskInstanceTest.this.client.getRouter(anyString())).thenReturn(router);
            when(router.route(any())).thenReturn(invoker);
            when(invoker.invoke(any(), any(), any(), any())).thenReturn(new ArrayList<>());

            // when
            Instance instance = new Instance();
            instance.setId("task_instance_1");
            AppTaskInstance appTaskInstance = AppTaskInstanceTest.this.factory.create(instance, "taskId",
                    appTaskInstanceService);
            appTaskInstance.run(runContext);

            // then.
            assertEquals(0,
                    ObjectUtils.<List<Map<String, Object>>>cast(businessData.get(AippConst.BS_AIPP_MEMORIES_KEY))
                            .size());
            verify(invoker, times(1)).invoke(anyMap().isEmpty(), eq("app_1"), eq(NORMAL.name()), eq(context));
        }

        @Test
        @DisplayName("测试带有memory，并且类型是UserSelect")
        @SuppressWarnings("unchecked")
        public void testShouldUseMemoryAndTypeIsUserSelect() {
            // given.
            List<Map<String, Object>> memoryConfigs = modelengine.fit.jober.aipp.domains.taskinstance.TestUtils.buildMemoryConfigs(true,
                    MemoryTypeEnum.USER_SELECT.type(), "");

            Map<String, Object> businessData = new HashMap<>();
            businessData.put(BS_CHAT_ID, "chat_id");

            OperationContext context = buildOperation();
            RunContext runContext = new RunContext(businessData, context);
            runContext.setMemoryConfig(memoryConfigs);

            AppTask appTask = mock(AppTask.class);
            when(appTask.getEntity()).thenReturn(
                    AppTask.asEntity().setFlowDefinitionId("flow_1").setAppSuiteId("app_1"));
            runContext.setAppTask(appTask);

            Instance instance = new Instance();
            instance.setId("task_instance_1");

            ChatSession<Object> session = Mockito.mock(ChatSession.class);

            // when
            AppTaskInstance appTaskInstance = AppTaskInstanceTest.this.factory.create(instance, "taskId",
                    appTaskInstanceService);
            appTaskInstance.run(runContext, session);

            // then.
            ArgumentCaptor<MemoryConfigDto> captor = ArgumentCaptor.forClass(MemoryConfigDto.class);
            verify(AppTaskInstanceTest.this.appChatSSEService, times(1)).sendToAncestorLastData(eq("task_instance_1"),
                    captor.capture());
            assertEquals(MemoryTypeEnum.USER_SELECT.type(), captor.getValue().getMemory());
            assertEquals("task_instance_1", captor.getValue().getInstanceId());
            assertSame(businessData, captor.getValue().getInitContext());
        }

        @Test
        @DisplayName("测试不带有memory")
        public void testShouldNotUseMemory() {
            // given.
            List<Map<String, Object>> memoryConfigs = modelengine.fit.jober.aipp.domains.taskinstance.TestUtils.buildMemoryConfigs(false,
                    MemoryTypeEnum.CUSTOMIZING.type(), "fitable_id_1");

            Map<String, Object> businessData = new HashMap<>();
            businessData.put(BS_CHAT_ID, "chat_id");
            businessData.put(AippConst.BS_AIPP_MEMORIES_KEY, List.of(MapBuilder.get().put("xxx", "111").build()));

            OperationContext context = buildOperation();
            RunContext runContext = new RunContext(businessData, context);
            runContext.setMemoryConfig(memoryConfigs);

            AppTask appTask = mock(AppTask.class);
            when(appTask.getEntity()).thenReturn(
                    AppTask.asEntity().setFlowDefinitionId("flow_1").setAppSuiteId("app_1"));
            runContext.setAppTask(appTask);

            when(AppTaskInstanceTest.this.flowInstanceService.startFlow(any(), any(), any())).thenReturn(
                    new FlowInstanceResult("trace1"));
            doNothing().when(AppTaskInstanceTest.this.appTaskInstanceService).update(any(), any());

            // when
            Instance instance = new Instance();
            instance.setId("task_instance_1");
            AppTaskInstance appTaskInstance = AppTaskInstanceTest.this.factory.create(instance, "taskId",
                    appTaskInstanceService);
            appTaskInstance.run(runContext);

            // then.
            assertEquals(0,
                    ObjectUtils.<List<Map<String, Object>>>cast(businessData.get(AippConst.BS_AIPP_MEMORIES_KEY))
                            .size());
        }

        @Test
        @DisplayName("测试不带有memory，memoryType是user_select")
        public void testShouldNotUseMemoryAndMemoryTypeIsUserSelect() {
            // given.
            List<Map<String, Object>> memoryConfigs = modelengine.fit.jober.aipp.domains.taskinstance.TestUtils.buildMemoryConfigs(false,
                    MemoryTypeEnum.USER_SELECT.type(), "fitable_id_1");

            Map<String, Object> businessData = new HashMap<>();
            businessData.put(BS_CHAT_ID, "chat_id");
            businessData.put(AippConst.BS_AIPP_MEMORIES_KEY, List.of(MapBuilder.get().put("xxx", "111").build()));

            OperationContext context = buildOperation();
            RunContext runContext = new RunContext(businessData, context);
            runContext.setMemoryConfig(memoryConfigs);

            AppTask appTask = mock(AppTask.class);
            when(appTask.getEntity()).thenReturn(
                    AppTask.asEntity().setFlowDefinitionId("flow_1").setAppSuiteId("app_1"));
            runContext.setAppTask(appTask);

            when(AppTaskInstanceTest.this.flowInstanceService.startFlow(any(), any(), any())).thenReturn(
                    new FlowInstanceResult("trace1"));
            doNothing().when(AppTaskInstanceTest.this.appTaskInstanceService).update(any(), any());

            // when
            Instance instance = new Instance();
            instance.setId("task_instance_1");
            AppTaskInstance appTaskInstance = AppTaskInstanceTest.this.factory.create(instance, "taskId",
                    appTaskInstanceService);
            appTaskInstance.run(runContext);

            // then.
            List<Map<String, Object>> memories = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_MEMORIES_KEY));
            assertEquals("111", memories.get(0).get("xxx"));
        }
    }

    private OperationContext buildOperation() {
        OperationContext context = new OperationContext();
        context.setOperator("张越");
        return context;
    }

    @Nested
    @DisplayName("测试 getPath 接口")
    class TestGetPath {
        @Test
        @DisplayName("测试没有parent的情况.")
        public void testNoParent() {
            // given.
            when(AppTaskInstanceTest.this.aippLogRepository.getParentPath(anyString())).thenReturn("");

            Instance instance = new Instance();
            instance.setId("task_instance_1");
            AppTaskInstance appTaskInstance = AppTaskInstanceTest.this.factory.create(instance, "taskId",
                    appTaskInstanceService);

            // when.
            String path = appTaskInstance.getPath(buildOperation());

            // then.
            assertEquals("/task_instance_1", path);
        }

        @Test
        @DisplayName("测试有parent的情况.")
        public void testHasParent() {
            // given.
            when(AppTaskInstanceTest.this.aippLogRepository.getParentPath(anyString())).thenReturn(
                    "/instance_parent_1");

            AppTaskInstance parent = mock(AppTaskInstance.class);
            when(AppTaskInstanceTest.this.appTaskInstanceService.getInstance(anyString(), anyString(),
                    any())).thenReturn(Optional.of(parent));
            when(parent.getPath(any())).thenReturn("/instance_parent_1");

            Instance instance = new Instance();
            instance.setId("task_instance_1");
            AppTaskInstance appTaskInstance = AppTaskInstanceTest.this.factory.create(instance, "taskId",
                    appTaskInstanceService);

            // when.
            String path = appTaskInstance.getPath(buildOperation());

            // then.
            assertEquals("/instance_parent_1/task_instance_1", path);
        }
    }
}
