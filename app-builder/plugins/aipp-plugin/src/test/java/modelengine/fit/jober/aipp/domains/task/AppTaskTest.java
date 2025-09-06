/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

import static modelengine.fit.jober.aipp.constants.AippConst.BS_AIPP_FILE_DESC_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.RESTART_MODE;
import static modelengine.fit.jober.aipp.enums.AippInstLogType.FORM;
import static modelengine.fit.jober.aipp.enums.AippInstLogType.QUESTION;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.PREVIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.log.repository.AippLogRepository;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.enums.AippMetaStatusEnum;
import modelengine.fit.jober.aipp.enums.JaneCategory;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.enums.RestartModeEnum;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * {@link AppTask} 的测试类。
 *
 * @author 张越
 * @since 2025-01-13
 */
public class AppTaskTest {
    private AppTaskFactory factory;
    private AippLogRepository aippLogRepository;
    private AppTaskInstanceService appTaskInstanceService;
    private FlowsService flowsService;
    private AppVersionRepository appVersionRepository;
    private AopAippLogService aopAippLogService;
    private AppTaskService appTaskService;
    private FlowInstanceService flowInstanceService;

    @BeforeEach
    public void setUp() {
        this.aippLogRepository = mock(AippLogRepository.class);
        this.appTaskInstanceService = mock(AppTaskInstanceService.class);
        this.flowsService = mock(FlowsService.class);
        AppChatSessionService appChatSessionService = mock(AppChatSessionService.class);
        this.flowInstanceService = mock(FlowInstanceService.class);
        this.appTaskService = mock(AppTaskService.class);
        this.appVersionRepository = mock(AppVersionRepository.class);
        AppBuilderFormPropertyRepository appBuilderFormPropertyRepository = mock(
                AppBuilderFormPropertyRepository.class);
        this.aopAippLogService = mock(AopAippLogService.class);
        AppChatSseService appChatSseService = mock(AppChatSseService.class);
        this.factory = new AppTaskFactory(this.aippLogRepository, this.appTaskInstanceService, this.flowsService,
                appChatSessionService, flowInstanceService, appBuilderFormPropertyRepository,
                this.aopAippLogService, appChatSseService);
    }

    @Test
    @DisplayName("asCreateEntity测试")
    public void testAsCreateEntity() {
        AppTask task = AppTask.asCreateEntity().build();
        assertEquals(JaneCategory.AIPP.name(), task.getEntity().getCategory());
        assertEquals(AippMetaStatusEnum.INACTIVE.getCode(), task.getEntity().getStatus());
        assertEquals(AippConst.STATIC_META_ITEMS.size(), task.getEntity().getProperties().size());
    }

    @Test
    @DisplayName("asUpdateEntity测试")
    public void testAsUpdateEntity() {
        AppTask task = AppTask.asUpdateEntity("task_id").build();
        assertEquals("task_id", task.getEntity().getTaskId());
    }

    @Test
    @DisplayName("测试isDraft")
    public void testIsDraft() {
        AppTask task = AppTask.asUpdateEntity("task_id").build();
        assertFalse(task.isDraft());

        task.getEntity().setBaseLineVersion("1.0");
        task.getEntity().setStatus("inactive");
        assertTrue(task.isDraft());
    }

    @Test
    @DisplayName("测试isActive")
    public void testIsActive() {
        AppTask task = AppTask.asUpdateEntity("task_id").build();
        assertFalse(task.isActive());

        task.getEntity().setStatus("active");
        assertTrue(task.isActive());
    }

    @Test
    @DisplayName("测试isUpgrade")
    public void testIsUpgrade() {
        AppTask task = AppTask.asUpdateEntity("task_id").build();
        assertTrue(task.isUpgrade("1.0"));

        task.getEntity().setStatus("active");
        assertTrue(task.isUpgrade("1.0"));

        task.getEntity().setVersion("1.0");
        task.getEntity().setStatus("inactive");
        assertFalse(task.isUpgrade("1.0"));
    }

    @Test
    @DisplayName("测试isBelongApp")
    public void testIsBelongApp() {
        AppTask task = AppTask.asUpdateEntity("task_id").build();
        assertFalse(task.isBelongApp("app_id"));

        task.getEntity().setAppId("app_id");
        assertTrue(task.isBelongApp("app_id"));
    }

    @Test
    @DisplayName("测试isPublished")
    public void testIsPublished() {
        AppTask task = AppTask.asUpdateEntity("task_id").build();
        assertFalse(task.isPublished());

        task.getEntity().setAippType(PREVIEW.name());
        assertFalse(task.isPublished());

        task.getEntity().setAippType(PREVIEW.name());
        task.getEntity().setStatus(AippMetaStatusEnum.ACTIVE.getCode());
        assertFalse(task.isPublished());

        task.getEntity().setAippType(NORMAL.name());
        task.getEntity().setStatus(AippMetaStatusEnum.INACTIVE.getCode());
        assertFalse(task.isPublished());

        task.getEntity().setAippType(NORMAL.name());
        task.getEntity().setStatus(AippMetaStatusEnum.ACTIVE.getCode());
        assertTrue(task.isPublished());
    }

    /**
     * 测试run方法
     */
    @Nested
    @DisplayName("测试run方法")
    public class TestRun {
        @Test
        @DisplayName("测试run有文件描述")
        public void testRunHasFileDescriptions() {
            // given.
            this.mockInstance();
            this.mockFlows();

            Map<String, Object> businessData = MapBuilder.<String, Object>get()
                    .put(AippConst.INST_NAME_KEY, "instance_name")
                    .put(RESTART_MODE, RestartModeEnum.INCREMENT.getMode())
                    .put(BS_AIPP_FILE_DESC_KEY,
                            List.of(MapBuilder.get().put("file_url", "https://xxx.com/bb.png").build()))
                    .build();
            RunContext context = new RunContext(businessData, new OperationContext());

            Meta meta = buildMeta();
            meta.setAttributes(
                    MapBuilder.<String, Object>get().put(AippConst.ATTR_FLOW_DEF_ID_KEY, "flow_definition_id").build());

            // when.
            AppTask appTask = AppTaskTest.this.factory.create(meta, AppTaskTest.this.appTaskService);
            appTask.run(context);

            // then.
            ArgumentCaptor<AppTaskInstance> captor = ArgumentCaptor.forClass(AppTaskInstance.class);
            verify(AppTaskTest.this.appTaskInstanceService, times(1)).createInstance(captor.capture(), any());
            AppTaskInstance createArg = captor.getValue();
            assertEquals("instance_name", createArg.getEntity().getName());

            verify(AppTaskTest.this.aopAippLogService, times(1)).insertLog(any());
            verify(AppTaskTest.this.appVersionRepository, times(0)).selectById(any());
            verify(AppTaskTest.this.flowsService, times(1)).getFlows(eq("flow_definition_id"), any());

            assertEquals("https://xxx.com/bb.png",
                    ObjectUtils.<List<String>>cast(businessData.get(AippConst.BS_AIPP_FILES_DOWNLOAD_KEY)).get(0));
        }

        @Test
        @DisplayName("测试run无文件描述，但是isIncrementMode")
        public void testRunNoFileDescriptionsButIsIncrementMode() {
            // given.
            this.mockInstance();
            this.mockFlows();

            Map<String, Object> businessData = MapBuilder.<String, Object>get()
                    .put(AippConst.INST_NAME_KEY, "instance_name")
                    .put(RESTART_MODE, RestartModeEnum.INCREMENT.getMode())
                    .build();
            RunContext context = new RunContext(businessData, new OperationContext());

            Meta meta = buildMeta();
            meta.setAttributes(
                    MapBuilder.<String, Object>get().put(AippConst.ATTR_FLOW_DEF_ID_KEY, "flow_definition_id").build());

            // when.
            AppTask appTask = AppTaskTest.this.factory.create(meta, AppTaskTest.this.appTaskService);
            appTask.run(context);

            // then.
            verify(AppTaskTest.this.aopAippLogService, times(1)).insertLog(any());
            assertNull(businessData.get(AippConst.BS_AIPP_FILES_DOWNLOAD_KEY));
        }

        @Test
        @DisplayName("测试run无文件描述，但是不是IncrementMode")
        public void testRunNoFileDescriptionsButIsNotIncrementMode() {
            // given.
            this.mockInstance();
            this.mockFlows();

            Map<String, Object> businessData = MapBuilder.<String, Object>get()
                    .put(AippConst.INST_NAME_KEY, "instance_name")
                    .put(RESTART_MODE, RestartModeEnum.OVERWRITE.getMode())
                    .build();
            RunContext context = new RunContext(businessData, new OperationContext());

            Meta meta = buildMeta();
            meta.setAttributes(
                    MapBuilder.<String, Object>get().put(AippConst.ATTR_FLOW_DEF_ID_KEY, "flow_definition_id").build());

            // when.
            AppTask appTask = AppTaskTest.this.factory.create(meta, AppTaskTest.this.appTaskService);
            appTask.run(context);

            // then.
            verify(AppTaskTest.this.aopAippLogService, times(1)).insertLog(any());
            assertNull(businessData.get(AippConst.BS_AIPP_FILES_DOWNLOAD_KEY));
        }

        @Test
        @DisplayName("测试run，有formId和formVersion")
        public void testRunWithFormIdAndFormVersion() {
            // given.
            this.mockInstance();
            this.mockFlows();

            AppVersion appVersion = mock(AppVersion.class);
            when(AppTaskTest.this.appVersionRepository.selectById(any())).thenReturn(Optional.of(appVersion));
            when(appVersion.getFormProperties()).thenReturn(List.of());

            Map<String, Object> businessData = MapBuilder.<String, Object>get()
                    .put(AippConst.INST_NAME_KEY, "instance_name")
                    .put(RESTART_MODE, RestartModeEnum.OVERWRITE.getMode())
                    .build();
            RunContext context = new RunContext(businessData, new OperationContext());

            Meta meta = buildMeta();
            meta.setAttributes(MapBuilder.<String, Object>get()
                    .put(AippConst.ATTR_FLOW_DEF_ID_KEY, "flow_definition_id")
                    .put(AippConst.ATTR_START_FORM_ID_KEY, "form_id")
                    .put(AippConst.ATTR_START_FORM_VERSION_KEY, "form_version")
                    .put(AippConst.ATTR_APP_ID_KEY, "app_id")
                    .build());

            // when.
            AppTask appTask = AppTaskTest.this.factory.create(meta, AppTaskTest.this.appTaskService);
            appTask.run(context);

            // then.
            ArgumentCaptor<AippLogCreateDto> captor = ArgumentCaptor.forClass(AippLogCreateDto.class);
            verify(AppTaskTest.this.aopAippLogService, times(2)).insertLog(captor.capture());
            List<AippLogCreateDto> createDtoList = captor.getAllValues();
            assertEquals(QUESTION.name(), createDtoList.get(0).getLogType());
            assertEquals(FORM.name(), createDtoList.get(1).getLogType());
        }

        private void mockFlows() {
            FlowInfo flowInfo = mock(FlowInfo.class);
            when(AppTaskTest.this.flowsService.getFlows(any(), any())).thenReturn(flowInfo);
            when(flowInfo.getInputParamsByName(any())).thenReturn(List.of());
        }

        private void mockInstance() {
            AppTaskInstance instance = mock(AppTaskInstance.class);
            when(AppTaskTest.this.appTaskInstanceService.createInstance(any(), any())).thenReturn(instance);
            when(instance.getId()).thenReturn("instance_id");
            doNothing().when(instance).run(any(), any());
            when(instance.getEntity()).thenReturn(AppTaskInstance.asEntity().setFlowTraceId("flow_1"));
        }
    }

    private Meta buildMeta() {
        Meta meta = new Meta();
        meta.setId("app_suite_id");
        meta.setVersion("1.0");
        meta.setVersionId("task_id");
        return meta;
    }

    /**
     * 测试cleanResource方法
     */
    @Nested
    @DisplayName("测试cleanResource方法")
    public class TestCleanResource {
        @Test
        @DisplayName("测试isActive状态时")
        public void testIsActive() {
            // given.
            Meta meta = buildMeta();
            meta.setAttributes(MapBuilder.<String, Object>get()
                    .put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.ACTIVE.getCode())
                    .put(AippConst.ATTR_FLOW_CONFIG_ID_KEY, "flow_config_1")
                    .build());

            AppTaskInstance instance = mock(AppTaskInstance.class);
            when(AppTaskTest.this.appTaskInstanceService.getInstanceStreamByTaskId(anyString(), anyInt(),
                    any())).thenReturn(Stream.of(instance));
            when(instance.isRunning()).thenReturn(true);
            when(instance.getEntity()).thenReturn(AppTaskInstance.asEntity().setFlowTraceId("flow_1"));
            when(instance.getId()).thenReturn("instance_1");

            doNothing().when(AppTaskTest.this.flowInstanceService).terminateFlows(any(), anyString(), any(), any());
            doNothing().when(AppTaskTest.this.appTaskInstanceService).update(any(), any());
            doNothing().when(AppTaskTest.this.aippLogRepository).deleteAippPreviewLog(anyString(), any());
            doNothing().when(AppTaskTest.this.flowsService).deleteFlowsWithoutElsa(anyString(), anyString(), any());
            doNothing().when(AppTaskTest.this.appTaskService).deleteTaskById(anyString(), any());

            // when.
            AppTask appTask = AppTaskTest.this.factory.create(meta, AppTaskTest.this.appTaskService);
            appTask.cleanResource(new OperationContext());

            // then.
            verify(AppTaskTest.this.appTaskInstanceService, times(1))
                    .getInstanceStreamByTaskId(eq("task_id"), eq(15), any());
            verify(AppTaskTest.this.flowInstanceService, times(1))
                    .terminateFlows(eq(null), eq("flow_1"), eq(Collections.emptyMap()), any());

            ArgumentCaptor<AppTaskInstance> captor = ArgumentCaptor.forClass(AppTaskInstance.class);
            verify(AppTaskTest.this.appTaskInstanceService, times(1))
                    .update(captor.capture(), any());
            AppTaskInstance instance1 = captor.getValue();
            assertEquals("task_id", instance1.getTaskId());
            assertEquals("instance_1", instance1.getId());
            assertEquals(MetaInstStatusEnum.TERMINATED.name(), instance1.getEntity().getStatus().orElse(null));

            verify(AppTaskTest.this.aippLogRepository, times(1))
                    .deleteAippPreviewLog(eq("app_suite_id"), any());

            verify(AppTaskTest.this.flowsService, times(1))
                    .deleteFlowsWithoutElsa(eq("flow_config_1"), eq("1.0"), any());

            verify(AppTaskTest.this.appTaskService, times(1))
                    .deleteTaskById(eq("task_id"), any());
        }

        @Test
        @DisplayName("测试isNotActive状态时")
        public void testIsInActive() {
            // given.
            Meta meta = buildMeta();
            meta.setAttributes(MapBuilder.<String, Object>get()
                    .put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode())
                    .put(AippConst.ATTR_FLOW_CONFIG_ID_KEY, "flow_config_1")
                    .build());

            doNothing().when(AppTaskTest.this.aippLogRepository).deleteAippPreviewLog(anyString(), any());
            doNothing().when(AppTaskTest.this.flowsService).deleteFlowsWithoutElsa(anyString(), anyString(), any());
            doNothing().when(AppTaskTest.this.appTaskService).deleteTaskById(anyString(), any());

            // when.
            AppTask appTask = AppTaskTest.this.factory.create(meta, AppTaskTest.this.appTaskService);
            appTask.cleanResource(new OperationContext());

            // then.
            verify(AppTaskTest.this.appTaskInstanceService, times(0))
                    .getInstanceStreamByTaskId(anyString(), anyInt(), any());
            verify(AppTaskTest.this.flowInstanceService, times(0))
                    .terminateFlows(anyString(), anyString(), eq(Collections.emptyMap()), any());
            verify(AppTaskTest.this.aippLogRepository, times(0))
                    .deleteAippPreviewLog(anyString(), any());
        }

        @Test
        @DisplayName("测试deleteFlows时抛出异常")
        public void testDeleteFlowsThrowsException() {
            // given.
            Meta meta = buildMeta();
            meta.setAttributes(MapBuilder.<String, Object>get()
                    .put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode())
                    .put(AippConst.ATTR_FLOW_CONFIG_ID_KEY, "flow_config_1")
                    .build());

            doThrow(new JobberException(ErrorCodes.FLOW_DEFINITION_DELETE_ERROR)).when(AppTaskTest.this.flowsService)
                    .deleteFlowsWithoutElsa(anyString(), anyString(), any());
            doNothing().when(AppTaskTest.this.appTaskService).deleteTaskById(anyString(), any());

            // when.
            // then.
            AppTask appTask = AppTaskTest.this.factory.create(meta, AppTaskTest.this.appTaskService);
            assertThrows(AippException.class, () -> appTask.cleanResource(new OperationContext()));
        }
    }
}
