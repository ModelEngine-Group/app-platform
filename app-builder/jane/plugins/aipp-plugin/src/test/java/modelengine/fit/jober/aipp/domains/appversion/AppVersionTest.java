/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_HAS_PUBLISHED;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_VERSION_HAS_ALREADY;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.NEW_VERSION_IS_LOWER;
import static modelengine.fit.jober.aipp.enums.AippMetaStatusEnum.ACTIVE;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;
import static modelengine.fit.jober.aipp.enums.AppTypeEnum.APP;
import static modelengine.fit.jober.aipp.enums.RestartModeEnum.OVERWRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.jade.store.service.ToolService;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.waterflow.AippFlowDefinitionService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.converters.ConverterFactory;
import modelengine.fit.jober.aipp.converters.impl.AppConfigToExportConfigConverter;
import modelengine.fit.jober.aipp.converters.impl.AppExportToAppPoConverter;
import modelengine.fit.jober.aipp.converters.impl.AppGraphToExportGraphConverter;
import modelengine.fit.jober.aipp.converters.impl.AppVersionToExportAppConverter;
import modelengine.fit.jober.aipp.converters.impl.AppVersionToTemplateConverter;
import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.chat.repository.AppChatRepository;
import modelengine.fit.jober.aipp.domains.definition.service.AppDefinitionService;
import modelengine.fit.jober.aipp.domains.log.AppLog;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import modelengine.fit.jober.aipp.dto.chat.ChatCreateEntity;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.dto.export.AppExportApp;
import modelengine.fit.jober.aipp.dto.export.AppExportConfig;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.dto.export.AppExportFlowGraph;
import modelengine.fit.jober.aipp.dto.export.AppExportForm;
import modelengine.fit.jober.aipp.dto.template.TemplateAppCreateDto;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AppCategory;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.enums.AppStatus;
import modelengine.fit.jober.aipp.enums.AppTypeEnum;
import modelengine.fit.jober.aipp.factory.AppTemplateFactory;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.knowledge.KnowledgeCenterService;
import modelengine.jade.knowledge.dto.KnowledgeDto;
import modelengine.jade.store.service.AppService;
import modelengine.jade.store.service.PluginService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link AppVersion} 的测试类。
 *
 * @author 张越
 * @since 2025-02-12
 */
public class AppVersionTest {
    private AppVersionFactory factory;
    private AppBuilderFormPropertyRepository formPropertyRepository;
    private AppTaskService appTaskService;
    private AppBuilderConfigRepository configRepository;
    private AppBuilderFormRepository formRepository;
    private AppBuilderConfigPropertyRepository configPropertyRepository;
    private AppBuilderFlowGraphRepository flowGraphRepository;
    private FlowsService flowsService;
    private AppService appService;
    private PluginService pluginService;
    private ToolService toolService;
    private AppChatRepository appChatRepository;
    private AppDefinitionService appDefinitionService;
    private AippLogService aippLogService;
    private UploadedFileManageService uploadedFileManageService;
    private AppTemplateFactory templateFactory;
    private AppTaskInstanceService appTaskInstanceService;
    private LocaleService localeService;
    private AippModelCenter aippModelCenter;
    private AppVersionRepository appVersionRepository;
    private AippFlowDefinitionService aippFlowDefinitionService;
    private FlowDefinitionService flowDefinitionService;
    private KnowledgeCenterService knowledgeCenterService;

    @BeforeEach
    public void setUp() {
        this.formPropertyRepository = mock(AppBuilderFormPropertyRepository.class);
        this.appTaskService = mock(AppTaskService.class);
        this.configRepository = mock(AppBuilderConfigRepository.class);
        this.formRepository = mock(AppBuilderFormRepository.class);
        this.configPropertyRepository = mock(AppBuilderConfigPropertyRepository.class);
        this.flowGraphRepository = mock(AppBuilderFlowGraphRepository.class);
        this.flowsService = mock(FlowsService.class);
        this.appService = mock(AppService.class);
        this.pluginService = mock(PluginService.class);
        this.toolService = mock(ToolService.class);
        this.appChatRepository = mock(AppChatRepository.class);
        this.appDefinitionService = mock(AppDefinitionService.class);
        this.aippLogService = mock(AippLogService.class);
        this.uploadedFileManageService = mock(UploadedFileManageService.class);
        this.templateFactory = mock(AppTemplateFactory.class);
        this.appTaskInstanceService = mock(AppTaskInstanceService.class);
        this.localeService = mock(LocaleService.class);
        this.aippModelCenter = mock(AippModelCenter.class);
        this.appVersionRepository = mock(AppVersionRepository.class);
        this.aippFlowDefinitionService = mock(AippFlowDefinitionService.class);
        this.flowDefinitionService = mock(FlowDefinitionService.class);
        this.knowledgeCenterService = mock(KnowledgeCenterService.class);
        ConverterFactory converterFactory = new ConverterFactory(
                List.of(new AppExportToAppPoConverter(), new AppConfigToExportConfigConverter(),
                        new AppGraphToExportGraphConverter(), new AppVersionToExportAppConverter(),
                        new AppVersionToTemplateConverter()));
        this.factory = new AppVersionFactory(this.formPropertyRepository,
                this.appTaskService,
                this.configRepository,
                this.formRepository,
                this.configPropertyRepository,
                this.flowGraphRepository,
                this.flowsService,
                this.appService,
                this.pluginService,
                this.toolService,
                this.appChatRepository,
                this.appDefinitionService,
                this.aippLogService,
                this.uploadedFileManageService,
                this.templateFactory,
                this.appTaskInstanceService,
                this.localeService,
                this.aippModelCenter,
                converterFactory,
                this.aippFlowDefinitionService,
                this.flowDefinitionService, 20000, 300, this.knowledgeCenterService, "/var/share");
    }

    /**
     * 基本测试.
     */
    @Nested
    @DisplayName("基本测试")
    public class BaseTest {
        @Test
        @DisplayName("属性测试")
        public void testAttributes() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setAttributes(JsonUtils.toJsonString(MapBuilder.get()
                    .put("icon", "icon_1")
                    .put("description", "description_1")
                    .put("greeting", "你好啊")
                    .put("app_type", "写作助手")
                    .build()));
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            // when.
            // then.
            assertEquals("icon_1", appVersion.getIcon());
            assertEquals("description_1", appVersion.getDescription());
            assertEquals("你好啊", appVersion.getGreeting());
            assertEquals("写作助手", appVersion.getClassification());
        }

        @Test
        @DisplayName("属性bool方法")
        public void testBoolFunction() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setAttributes(JsonUtils.toJsonString(MapBuilder.get()
                    .put(AippConst.ATTR_APP_IS_UPDATE, false)
                    .build()));
            data.setStatus(AppStatus.PUBLISHED.getName());
            data.setType(APP.code());
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            // when.
            // then.
            assertTrue(appVersion.isPublished());
            assertTrue(appVersion.isApp());
            assertFalse(appVersion.isUpdated());
        }
    }

    /**
     * 测试getBaselineCreateTime方法
     */
    @Nested
    @DisplayName("测试getBaselineCreateTime方法")
    public class TestGetBaselineCreateTime {
        @Test
        @DisplayName("测试有任务")
        public void testHasTasks() {
            // given.
            AppBuilderAppPo data = buildPoData();
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            LocalDateTime creationTime = LocalDateTime.now();
            AppTask appTask = mock(AppTask.class);
            when(AppVersionTest.this.appTaskService.getTasksByAppId(anyString(),
                    any(OperationContext.class))).thenReturn(List.of(appTask));
            when(appTask.getEntity()).thenReturn(AppTask.asEntity().setCreationTime(creationTime));

            // when.
            OperationContext context = new OperationContext();
            LocalDateTime baselineCreateTime = appVersion.getBaselineCreateTime(context);

            // then.
            assertEquals(creationTime, baselineCreateTime);
        }
    }

    private AppBuilderAppPo buildPoData() {
        return AppBuilderAppPo.builder().id("app_version_1").appId("app_version_1").appSuiteId("app_1").build();
    }

    /**
     * 测试publish方法
     */
    @Nested
    @DisplayName("测试publish方法")
    public class TestPublish {
        @Test
        @DisplayName("测试已发布")
        public void testPublishAlready() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setStatus(AppStatus.PUBLISHED.getName());
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);
            PublishContext context = new PublishContext(AppBuilderAppDto.builder().build(), new OperationContext());

            // when.
            AippException exception = assertThrows(AippException.class, () -> appVersion.publish(context));

            // then.
            assertEquals(APP_HAS_PUBLISHED.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("测试当前版本比已发布版本大")
        public void testCurrentVersionIsBiggerThanPublishVersion() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setVersion("1.0.1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);
            PublishContext context = new PublishContext(AppBuilderAppDto.builder().version("1.0.0").build(),
                    new OperationContext());

            // when.
            AippParamException paramException = assertThrows(AippParamException.class,
                    () -> appVersion.publish(context));

            // then.
            assertEquals(NEW_VERSION_IS_LOWER.getCode(), paramException.getCode());
        }

        @Test
        @DisplayName("测试发布版本已存在")
        @SuppressWarnings("unchecked")
        public void testPublishVersionExistsAlready() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setVersion("1.0.0");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            RangedResultSet<AppTask> resultSet = mock(RangedResultSet.class);
            when(AppVersionTest.this.appTaskService.getTasks(any(), any())).thenReturn(resultSet);
            when(resultSet.isEmpty()).thenReturn(false);

            PublishContext context = new PublishContext(AppBuilderAppDto.builder().version("1.0.1").build(),
                    new OperationContext());

            // when.
            AippException aippException = assertThrows(AippException.class, () -> appVersion.publish(context));

            // then.
            assertEquals(APP_VERSION_HAS_ALREADY.getCode(), aippException.getCode());
        }

        @Test
        @DisplayName("测试正常发布")
        @SuppressWarnings("unchecked")
        public void testPublishNormally() throws IOException {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setVersion("1.0.0");
            data.setFlowGraphId("graph_1");
            data.setConfigId("config_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);
            String appearance = IoUtils.content(AppVersionTest.class, "/appearance.txt");

            RangedResultSet<AppTask> resultSet = mock(RangedResultSet.class);
            when(AppVersionTest.this.appTaskService.getTasks(any(), any())).thenReturn(resultSet);
            when(resultSet.isEmpty()).thenReturn(true);

            this.mockGraph();
            this.mockConfig();
            FlowInfo flowInfo1 = this.mockFlows();
            when(AppVersionTest.this.appService.publishApp(any())).thenReturn("uniqueName_1");
            when(AppVersionTest.this.appTaskService.getPreviewTasks(anyString(), any())).thenReturn(
                    Collections.emptyList());
            when(AppVersionTest.this.appTaskService.createTask(any(), any())).thenReturn(null);

            // 发布上下文.
            PublishContext context = new PublishContext(this.buildPublishData(appearance), new OperationContext());

            // when.
            appVersion.publish(context);

            // then.
            this.verifyGraph(appearance);
            verify(AppVersionTest.this.formPropertyRepository, times(1)).updateMany(any());
            this.verifyFlows(flowInfo1, context);
            assertEquals("uniqueName_1", appVersion.getData().getUniqueName());
            this.verifyTask();
            this.verifyAppVersion(appVersion);
        }

        @Test
        @DisplayName("测试发布waterFlow")
        @SuppressWarnings("unchecked")
        public void testPublishWaterFlow() throws IOException {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setVersion("1.0.0");
            data.setFlowGraphId("graph_1");
            data.setConfigId("config_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);
            String appearance = IoUtils.content(AppVersionTest.class, "/appearance.txt");

            RangedResultSet<AppTask> resultSet = mock(RangedResultSet.class);
            when(AppVersionTest.this.appTaskService.getTasks(any(), any())).thenReturn(resultSet);
            when(resultSet.isEmpty()).thenReturn(true);

            when(AppVersionTest.this.pluginService.addPlugin(any())).thenReturn("");

            this.mockGraph();
            this.mockConfig();
            FlowInfo flowInfo1 = this.mockFlows();
            when(AppVersionTest.this.appTaskService.getPreviewTasks(anyString(), any())).thenReturn(
                    Collections.emptyList());
            when(AppVersionTest.this.appTaskService.createTask(any(), any())).thenReturn(null);

            // 发布上下文.
            AppBuilderAppDto publishData = this.buildPublishData(appearance);
            publishData.setType(AppCategory.WATER_FLOW.getType());
            PublishContext context = new PublishContext(publishData, new OperationContext());

            // when.
            appVersion.publish(context);

            // then.
            this.verifyGraph(appearance);
            verify(AppVersionTest.this.formPropertyRepository, times(1)).updateMany(any());
            this.verifyFlows(flowInfo1, context);
            assertNotNull(appVersion.getData().getUniqueName());
            this.verifyTask();
            this.verifyAppVersion(appVersion);
        }

        @Test
        @DisplayName("测试发布waterFlow并且升级的情况")
        @SuppressWarnings("unchecked")
        public void testPublishWaterFlowAndUpgrade() throws IOException {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setVersion("1.0.0");
            data.setFlowGraphId("graph_1");
            data.setConfigId("config_1");
            data.setUniqueName("uniqueName_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);
            String appearance = IoUtils.content(AppVersionTest.class, "/appearance.txt");

            RangedResultSet<AppTask> resultSet = mock(RangedResultSet.class);
            when(AppVersionTest.this.appTaskService.getTasks(any(), any())).thenReturn(resultSet);
            when(resultSet.isEmpty()).thenReturn(true);

            this.mockGraph();
            this.mockConfig();
            FlowInfo flowInfo1 = this.mockFlows();
            when(AppVersionTest.this.appTaskService.getPreviewTasks(anyString(), any())).thenReturn(
                    Collections.emptyList());
            when(AppVersionTest.this.appTaskService.createTask(any(), any())).thenReturn(null);
            when(AppVersionTest.this.toolService.upgradeTool(any())).thenReturn("uniqueName_2");

            // 发布上下文.
            AppBuilderAppDto publishData = this.buildPublishData(appearance);
            publishData.setType(AppCategory.WATER_FLOW.getType());
            PublishContext context = new PublishContext(publishData, new OperationContext());

            // when.
            appVersion.publish(context);

            // then.
            this.verifyGraph(appearance);
            verify(AppVersionTest.this.formPropertyRepository, times(1)).updateMany(any());
            this.verifyFlows(flowInfo1, context);
            assertEquals("uniqueName_2", appVersion.getData().getUniqueName());
            this.verifyTask();
            this.verifyAppVersion(appVersion);
        }

        private void mockGraph() {
            when(AppVersionTest.this.flowGraphRepository.selectWithId(anyString())).thenReturn(
                    AppBuilderFlowGraph.builder().build());
            doNothing().when(AppVersionTest.this.flowGraphRepository).updateOne(any());
        }

        private void mockConfig() {
            when(AppVersionTest.this.configRepository.selectWithId(anyString())).thenReturn(
                    AppBuilderConfig.builder().build());
            doNothing().when(AppVersionTest.this.configRepository).updateOne(any());
        }

        private FlowInfo mockFlows() {
            FlowInfo flowInfo = mock(FlowInfo.class);
            when(AppVersionTest.this.flowsService.createFlows(any(), any())).thenReturn(flowInfo);
            when(flowInfo.getFlowId()).thenReturn("flow_1");
            FlowInfo flowInfo1 = mock(FlowInfo.class);
            when(AppVersionTest.this.flowsService.publishFlows(anyString(), anyString(), anyString(),
                    any())).thenReturn(flowInfo1);
            when(flowInfo1.getFlowId()).thenReturn("flow_1");
            return flowInfo1;
        }

        private AppBuilderAppDto buildPublishData(String appearance) {
            AppBuilderFlowGraphDto appBuilderFlowGraphDto = AppBuilderFlowGraphDto.builder()
                    .appearance(JsonUtils.parseObject(appearance))
                    .name("graph_1")
                    .build();
            AppBuilderAppDto publishData = AppBuilderAppDto.builder()
                    .flowGraph(appBuilderFlowGraphDto)
                    .version("1.0.1")
                    .name("xxx")
                    .attributes(new HashMap<>())
                    .type(AppCategory.APP.getType())
                    .build();
            publishData.setIcon("icon_1");
            publishData.setDescription("description_1");
            publishData.setPublishedDescription("published_description_1");
            publishData.setPublishedUpdateLog("publish log");
            publishData.setConfigFormProperties(Collections.emptyList());
            return publishData;
        }

        private void verifyGraph(String appearance) {
            ArgumentCaptor<AppBuilderFlowGraph> graphCaptor = ArgumentCaptor.forClass(AppBuilderFlowGraph.class);
            verify(AppVersionTest.this.flowGraphRepository, times(1)).updateOne(graphCaptor.capture());
            AppBuilderFlowGraph graph = graphCaptor.getValue();
            assertEquals("graph_1", graph.getName());
            Map<String, Object> json = JsonUtils.parseObject(appearance);
            json.put("version", "1.0.1");
            assertEquals(JsonUtils.toJsonString(json), graph.getAppearance());
        }

        private void verifyFlows(FlowInfo flowInfo1, PublishContext context) {
            verify(AppVersionTest.this.flowsService, times(1)).createFlows(anyString(), any());
            verify(AppVersionTest.this.flowsService, times(1)).publishFlows(anyString(), anyString(), anyString(),
                    any());
            assertEquals(flowInfo1, context.getFlowInfo());
        }

        private void verifyTask() {
            ArgumentCaptor<AppTask> taskCaptor = ArgumentCaptor.forClass(AppTask.class);
            verify(AppVersionTest.this.appTaskService, times(1)).createTask(taskCaptor.capture(), any());
            AppTask task = taskCaptor.getValue();
            assertEquals("xxx", task.getEntity().getName());
            assertEquals(ACTIVE.getCode(), task.getEntity().getStatus());
            assertEquals("description_1", task.getEntity().getDescription());
            assertEquals("icon_1", task.getEntity().getIcon());
            assertEquals("published_description_1", task.getEntity().getPublishDescription());
            assertEquals("publish log", task.getEntity().getPublishLog());
            assertEquals("1.0.1", task.getEntity().getVersion());
            assertEquals("1.0.1", task.getEntity().getAttributeVersion());
            assertEquals(NORMAL.name(), task.getEntity().getAippType());
            assertEquals("flow_1", task.getEntity().getFlowConfigId());
        }

        private void verifyAppVersion(AppVersion appVersion) {
            assertEquals(AppState.PUBLISHED.getName(), appVersion.getData().getState());
            assertEquals(AppStatus.PUBLISHED.getName(), appVersion.getData().getStatus());
            assertEquals(true, appVersion.getData().getIsActive());
            assertEquals("1.0.1", appVersion.getData().getVersion());
            assertEquals("published_description_1", appVersion.getAttributes().get("publishedDescription"));
            assertEquals("publish log", appVersion.getAttributes().get("publishedUpdateLog"));
            assertEquals(true, appVersion.getAttributes().get("is_update"));
            assertNotNull(appVersion.getData().getPath());
        }
    }

    /**
     * 测试run方法
     */
    @Nested
    @DisplayName("测试run方法")
    public class TestRun {
        @Test
        @DisplayName("测试启动当前app.")
        public void testStartCurrentApp() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setType(APP.code());
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);
            appVersion.getAttributes().put("is_update", false);

            // 构建runContext.
            RunContext runContext = new RunContext(new HashMap<>(), new OperationContext());
            runContext.setDebug(true);
            runContext.setQuestion("123");

            AppTask appTask = mock(AppTask.class);
            when(AppVersionTest.this.appTaskService.getTasksByAppId(anyString(), any())).thenReturn(List.of(appTask));
            when(appTask.getEntity()).thenReturn(AppTask.asEntity().setFlowDefinitionId("flow_definition_id_1"));
            doNothing().when(appTask).run(any(), eq(null));

            FlowInfo flowInfo = mock(FlowInfo.class);
            when(AppVersionTest.this.flowsService.getFlows(anyString(), any())).thenReturn(flowInfo);
            when(flowInfo.getInputParamsByName(anyString())).thenReturn(
                    List.of(buildInputParam("Question", "String", "问题")));

            // when.
            appVersion.run(runContext, null);

            // then.
            assertNotNull(runContext.getChatId());
            assertEquals(OVERWRITE.getMode(), runContext.getRestartMode());
            verify(appTask, times(1)).run(any(), eq(null));
        }

        @Test
        @DisplayName("测试启动atApp")
        public void testStartAtApp() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setType(APP.code());
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);
            appVersion.getAttributes().put("is_update", false);

            // 构建runContext.
            RunContext runContext = new RunContext(new HashMap<>(), new OperationContext());
            runContext.setDebug(true);
            runContext.setQuestion("123");
            runContext.setAtAppId("app_version_2");

            AppVersion atAppVersion = mock(AppVersion.class);
            when(AppVersionTest.this.appVersionRepository.selectById(anyString())).thenReturn(
                    Optional.of(atAppVersion));
            when(atAppVersion.getData()).thenReturn(AppBuilderAppPo.builder()
                    .state(AppState.PUBLISHED.getName())
                    .appSuiteId("app_1")
                    .appId("app_version_2")
                    .version("1.0.2")
                    .build());

            doNothing().when(AppVersionTest.this.appChatRepository).saveChat(any(), any());

            // when.
            appVersion.run(runContext, null);

            // then.
            ArgumentCaptor<RunContext> captor = ArgumentCaptor.forClass(RunContext.class);
            verify(atAppVersion, times(1)).run(captor.capture(), any());
            RunContext atAppRunContext = captor.getValue();
            assertEquals("app_version_1", atAppRunContext.getOriginAppId());
            assertEquals("app_version_2", atAppRunContext.getAppId());

            ArgumentCaptor<ChatCreateEntity> chatCaptor = ArgumentCaptor.forClass(ChatCreateEntity.class);
            verify(AppVersionTest.this.appChatRepository, times(1)).saveChat(chatCaptor.capture(), any());
        }
    }

    private Map<String, Object> buildInputParam(String name, String type, String description) {
        return MapBuilder.<String, Object>get()
                .put("name", name)
                .put("type", type)
                .put("description", description)
                .put("isRequired", true)
                .put("isVisible", true)
                .build();
    }

    /**
     * 测试restart方法
     */
    @Nested
    @DisplayName("测试restart方法")
    public class TestRestart {
        @Test
        @DisplayName("测试正常重启")
        public void testRestartNormally() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setType(APP.code());
            data.setAppId("app_version_1");
            data.setAppSuiteId("app_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);
            appVersion.getAttributes().put("is_update", false);

            AppTaskInstance instance = mock(AppTaskInstance.class);
            when(AppVersionTest.this.appTaskInstanceService.getInstanceById(anyString(), any())).thenReturn(
                    Optional.of(instance));
            String attributes = "{\"state\":\"active\"}";
            when(instance.getChats()).thenReturn(List.of(QueryChatRsp.builder()
                    .appId("app_version_1")
                    .attributes(attributes)
                    .chatId("chat_1")
                    .build()));

            AppLog appLog = mock(AppLog.class);
            when(instance.getLogs()).thenReturn(List.of(appLog));
            when(appLog.getLogData()).thenReturn(AippInstLog.builder()
                    .logData(JsonUtils.toJsonString(MapBuilder.get().put("msg", "你在哪里?").build()))
                    .build());

            AppTask appTask = mock(AppTask.class);
            when(AppVersionTest.this.appTaskService.getTaskList(anyString(), anyString(), anyString(),
                    any())).thenReturn(List.of(appTask));
            when(appTask.getEntity()).thenReturn(AppTask.asEntity().setFlowDefinitionId("flow_definition_id_1"));
            doNothing().when(appTask).run(any(), eq(null));

            FlowInfo flowInfo = mock(FlowInfo.class);
            when(AppVersionTest.this.flowsService.getFlows(anyString(), any())).thenReturn(flowInfo);
            when(flowInfo.getInputParamsByName(anyString())).thenReturn(
                    List.of(buildInputParam("Question", "String", "问题")));

            // when.
            AtomicReference<RunContext> contextAtomicReference = new AtomicReference<>();
            appVersion.restart(instance, new HashMap<>(), null, new OperationContext(),
                    contextAtomicReference::set);

            // then.
            RunContext context = contextAtomicReference.get();
            assertEquals("app_version_1", context.getAppId());
            assertEquals("chat_1", context.getChatId());
            assertEquals(OVERWRITE.getMode(), context.getRestartMode());
            assertEquals("你在哪里?", context.getQuestion());
            assertNull(context.getAtChatId());
            verify(instance, times(1)).overWrite();
        }

        @Test
        @DisplayName("测试重启并带有atChatId")
        public void testRestartWithAtChatId() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setType(APP.code());
            data.setAppId("app_version_1");
            data.setAppSuiteId("app_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);
            appVersion.getAttributes().put("is_update", false);

            QueryChatRsp atChat = QueryChatRsp.builder().appId("app_version_2").chatId("chat_2").build();
            AppTaskInstance instance = mock(AppTaskInstance.class);
            when(AppVersionTest.this.appTaskInstanceService.getInstanceById(anyString(), any())).thenReturn(
                    Optional.of(instance));
            String attributes = "{\"state\":\"active\"}";
            when(instance.getChats()).thenReturn(List.of(QueryChatRsp.builder()
                    .appId("app_version_1")
                    .chatId("chat_1")
                    .attributes(attributes)
                    .build(), atChat));

            AppLog appLog = mock(AppLog.class);
            when(instance.getLogs()).thenReturn(List.of(appLog));
            when(appLog.getLogData()).thenReturn(AippInstLog.builder()
                    .logData(JsonUtils.toJsonString(MapBuilder.get().put("msg", "你在哪里?").build()))
                    .build());

            when(AppVersionTest.this.appChatRepository.getChatById(anyString(), anyString())).thenReturn(
                    Optional.of(atChat));

            AppVersion atAppVersion = mock(AppVersion.class);
            when(AppVersionTest.this.appVersionRepository.selectById(anyString())).thenReturn(
                    Optional.of(atAppVersion));
            when(atAppVersion.getData()).thenReturn(AppBuilderAppPo.builder()
                    .state(AppState.PUBLISHED.getName())
                    .appSuiteId("app_1")
                    .appId("app_version_2")
                    .version("1.0.2")
                    .build());

            doNothing().when(AppVersionTest.this.appChatRepository).saveChat(any(), any());

            // when.
            AtomicReference<RunContext> contextAtomicReference = new AtomicReference<>();
            OperationContext operationContext = new OperationContext();
            operationContext.setAccount("00xxxxx");
            appVersion.restart(instance, new HashMap<>(), null, operationContext, contextAtomicReference::set);

            // then.
            RunContext context = contextAtomicReference.get();
            assertEquals("chat_2", context.getAtChatId());
        }
    }

    /**
     * 测试onCreate方法
     */
    @Nested
    @DisplayName("测试onCreate方法")
    public class TestOnCreate {
        @Test
        @DisplayName("测试创建，graphId为空.")
        public void testWhenFlowGraphIdIsNull() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            // when.
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> appVersion.cloneVersion(null, "1.0.1", AppTypeEnum.APP.name(), new OperationContext()));

            // then.
            assertEquals("App flow graph id can not be null.", exception.getMessage());
        }

        @Test
        @DisplayName("测试创建，configId为空.")
        public void testConfigIdIsNull() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setFlowGraphId("flow_graph_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            AppBuilderFlowGraph flowGraph = mock(AppBuilderFlowGraph.class);
            when(AppVersionTest.this.flowGraphRepository.selectWithId(anyString())).thenReturn(flowGraph);
            when(AppVersionTest.this.aippModelCenter.fetchModelList(anyString(),
                    anyString(),
                    any())).thenReturn(mockModelList());
            doNothing().when(flowGraph).setModelInfo(any());
            doNothing().when(flowGraph).clone(any());
            mockKnowledge();

            // when.
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> appVersion.cloneVersion(null, "1.0.1", AppTypeEnum.APP.name(), new OperationContext()));

            // then.
            assertEquals("App config id can not be null.", exception.getMessage());
        }

        @Test
        @DisplayName("测试创建，dto为空.")
        public void testDtoIsNull() throws IOException {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setFlowGraphId("flow_graph_1");
            data.setId("app_version_1");
            data.setAppId("app_version_1");
            data.setConfigId("app_config_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            mockGraph();
            mockConfig();
            mockKnowledge();

            AppBuilderFormProperty formProperty = AppBuilderFormProperty.builder().build();
            when(AppVersionTest.this.formPropertyRepository.selectWithAppId(anyString())).thenReturn(
                    List.of(formProperty));

            mockPreview();

            // when.
            OperationContext operationContext = new OperationContext();
            operationContext.setTenantId("model-engine");
            operationContext.setOperator("zy z00xxxxx");
            appVersion.cloneVersion(null, "1.0.1", AppTypeEnum.APP.name(), operationContext);

            // then.
            assertEquals("flow_graph_1", appVersion.getData().getFlowGraphId());
            assertEquals("app_config_1", appVersion.getData().getConfigId());
            assertEquals(AppTypeEnum.APP.name(), appVersion.getData().getType());
            assertEquals("model-engine", appVersion.getData().getTenantId());
            assertEquals("zy z00xxxxx", appVersion.getData().getCreateBy());
            assertEquals("zy z00xxxxx", appVersion.getData().getUpdateBy());
            assertNotEquals("app_version_1", appVersion.getData().getId());
            assertNotEquals("app_version_1", appVersion.getData().getAppId());
            assertEquals(false, appVersion.getData().getIsActive());
            assertEquals(AppStatus.DRAFT.getName(), appVersion.getData().getStatus());
            assertEquals("1.0.1", appVersion.getData().getVersion());
            assertEquals("app_1", appVersion.getData().getAppSuiteId());
        }

        @Test
        @DisplayName("测试创建，dto不为空.")
        public void testDtoIsNotNull() throws IOException {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setFlowGraphId("flow_graph_1");
            data.setId("app_version_1");
            data.setAppId("app_version_1");
            data.setConfigId("app_config_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            mockKnowledge();
            mockGraph();
            mockConfig();

            AppBuilderFormProperty formProperty = AppBuilderFormProperty.builder().build();
            when(AppVersionTest.this.formPropertyRepository.selectWithAppId(anyString())).thenReturn(
                    List.of(formProperty));

            mockPreview();

            // when.
            OperationContext operationContext = new OperationContext();
            operationContext.setTenantId("model-engine");
            operationContext.setOperator("zy z00xxxxx");
            appVersion.cloneVersion(this.buildAppCreateDto(), "1.0.1", AppTypeEnum.APP.name(), operationContext);

            // then.
            assertEquals("这是一个描述", appVersion.getDescription());
            assertEquals("/icon.png", appVersion.getIcon());
            assertEquals("你好啊", appVersion.getGreeting());
            assertEquals(NORMAL.name(), appVersion.getData().getAppType());
            assertEquals("uniqueName_1", appVersion.getData().getUniqueName());
            assertEquals("uniqueName_1", appVersion.getAttributes().get("store_id"));
            assertEquals("name_1", appVersion.getData().getName());
            assertEquals(AppTypeEnum.TEMPLATE.name(), appVersion.getData().getType());
            assertEquals(AppCategory.APP.getCategory(), appVersion.getData().getAppCategory());
            assertEquals("app", appVersion.getData().getAppBuiltType());
        }

        private AppBuilderAppCreateDto buildAppCreateDto() {
            return AppBuilderAppCreateDto.builder()
                    .description("这是一个描述")
                    .icon("/icon.png")
                    .greeting("你好啊")
                    .appType(NORMAL.name())
                    .storeId("uniqueName_1")
                    .name("name_1")
                    .type(AppTypeEnum.TEMPLATE.name())
                    .appCategory(AppCategory.APP.getCategory())
                    .appBuiltType("app")
                    .build();
        }
    }

    private void mockKnowledge() {
        KnowledgeDto knowledgeDto = KnowledgeDto.builder()
                .description("description1")
                .groupId("group_id1")
                .name("name1")
                .build();
        List<KnowledgeDto> knowledgeDtos = Collections.singletonList(knowledgeDto);
        when(AppVersionTest.this.knowledgeCenterService.getSupportKnowledges(any())).thenReturn(knowledgeDtos);
    }

    private void mockPreview() {
        AppTask appTask = mock(AppTask.class);
        when(AppVersionTest.this.appTaskService.getTasksByAppId(anyString(), any())).thenReturn(List.of(appTask));
        when(appTask.isPublished()).thenReturn(true);
        when(appTask.getEntity()).thenReturn(AppTask.asEntity().setAppSuiteId("app_1").setVersion("1.0.1"));
    }

    private void mockConfig() {
        AppBuilderConfig config = mock(AppBuilderConfig.class);
        when(AppVersionTest.this.configRepository.selectWithId(anyString())).thenReturn(config);
        doNothing().when(config).clone(any(), any());
        when(config.getId()).thenReturn("app_config_1");
    }

    private void mockGraph() throws IOException {
        String appearance = IoUtils.content(AppVersionTest.class, "/appearance.txt");
        AppBuilderFlowGraph flowGraph = mock(AppBuilderFlowGraph.class);
        when(AppVersionTest.this.flowGraphRepository.selectWithId(anyString())).thenReturn(flowGraph);
        when(AppVersionTest.this.aippModelCenter.fetchModelList(anyString(),
                anyString(),
                any())).thenReturn(this.mockModelList());
        doNothing().when(flowGraph).setModelInfo(any());
        doNothing().when(flowGraph).clone(any());
        when(flowGraph.getId()).thenReturn("flow_graph_1");
        when(flowGraph.getAppearance()).thenReturn(appearance);
    }

    private ModelListDto mockModelList() {
        return ModelListDto.builder()
                .models(List.of(ModelAccessInfo.builder().serviceName("model_service_1").tag("inner").build()))
                .total(1)
                .build();
    }

    /**
     * 测试publishTemplate方法
     */
    @Nested
    @DisplayName("测试publishTemplate方法")
    public class TestPublishTemplate {
        @Test
        @DisplayName("测试成功")
        public void testSuccess() throws IOException {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setFlowGraphId("flow_graph_1");
            data.setId("app_version_1");
            data.setAppId("app_version_1");
            data.setConfigId("app_config_1");
            data.setAppCategory(AppCategory.APP.getCategory());
            data.setAttributes(JsonUtils.toJsonString(MapBuilder.<String, Object>get()
                    .put("icon", "/icon.png")
                    .put("description", "这是一个描述")
                    .build()));
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            AppBuilderFlowGraph flowGraph = AppBuilderFlowGraph.builder().build();
            when(AppVersionTest.this.flowGraphRepository.selectWithId(anyString())).thenReturn(flowGraph);

            when(AppVersionTest.this.uploadedFileManageService.copyIconFiles(anyString(), anyString(),
                    anyString())).thenReturn("./copiedIcon.png");

            doNothing().when(AppVersionTest.this.uploadedFileManageService)
                    .updateRecord(anyString(), anyString(), anyInt());

            mockConfig();

            // when.
            OperationContext operationContext = new OperationContext();
            operationContext.setOperator("zy z00xxxx");
            operationContext.setAccount("z00xxxx");
            TemplateInfoDto templateInfoDto = appVersion.publishTemplate(TemplateAppCreateDto.builder()
                    .name("模板1")
                    .appType(NORMAL.name())
                    .description("description_2")
                    .icon("/icon.png")
                    .build(), operationContext);

            // then.
            assertNotEquals("app_version_1", templateInfoDto.getId());
            assertEquals("模板1", templateInfoDto.getName());
            assertEquals(AppCategory.APP.getCategory(), templateInfoDto.getCategory());
            assertEquals("description_2", templateInfoDto.getDescription());
            assertEquals(NORMAL.name(), templateInfoDto.getAppType());
            assertEquals("./copiedIcon.png", templateInfoDto.getIcon());
            assertEquals("zy z00xxxx", templateInfoDto.getCreator());
        }
    }

    /**
     * 测试publishTemplate方法
     */
    @Nested
    @DisplayName("测试publishTemplate方法")
    public class TestUpgrade {
        @Test
        @DisplayName("测试成功")
        public void testSuccess() throws IOException {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setFlowGraphId("flow_graph_1");
            data.setId("app_version_1");
            data.setAppId("app_version_1");
            data.setConfigId("app_config_1");
            data.setVersion("1.0.0");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            mockKnowledge();
            mockGraph();
            mockConfig();
            mockModelList();
            mockPreview();

            // when.
            OperationContext operationContext = new OperationContext();
            appVersion.upgrade(AppBuilderAppCreateDto.builder().build(), APP.code(), operationContext);

            // then.
            assertEquals("1.0.0", appVersion.getAttributes().get("latest_version"));
            assertEquals("1.0.1", appVersion.getData().getVersion());
            assertEquals(AppState.INACTIVE.getName(), appVersion.getData().getState());
            assertEquals(false, appVersion.getData().getIsActive());
            assertEquals(AppStatus.DRAFT.getName(), appVersion.getData().getStatus());
        }
    }

    /**
     * 测试导入
     */
    @Nested
    @DisplayName("测试导入")
    public class TestImport {
        @Test
        @DisplayName("测试版本不匹配，报错")
        public void testException() throws IOException {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setId("app_version_1");
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            when(AppVersionTest.this.appVersionRepository.selectWithSimilarName(anyString())).thenReturn(
                    Collections.emptyList());

            mockPreview();

            // when.
            OperationContext operationContext = new OperationContext();
            operationContext.setTenantId("31f20efc7e0848deab6a6bc10fc30111");
            AppExportDto dto = this.buildExportDto();
            dto.setVersion("1.0.0");
            Map<String, String> exportMeta = MapBuilder.<String, String>get().put("version", "1.0.1").build();
            AippException exception = assertThrows(AippException.class,
                    () -> appVersion.importData(dto, "app_1", "", operationContext, exportMeta));

            // then.
            assertEquals(AippErrCode.IMPORT_CONFIG_UNMATCHED_VERSION.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("测试成功场景")
        public void testSuccess() throws IOException {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setId("app_version_1");
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            when(AppVersionTest.this.appVersionRepository.selectWithSimilarName(anyString())).thenReturn(
                    Collections.emptyList());

            mockPreview();

            // when.
            OperationContext operationContext = new OperationContext();
            operationContext.setTenantId("31f20efc7e0848deab6a6bc10fc30111");
            AppExportDto dto = this.buildExportDto();
            Map<String, String> exportMeta = MapBuilder.<String, String>get().put("version", "1.0.1").build();
            mockKnowledge();
            appVersion.importData(dto, "app_1", "", operationContext, exportMeta);

            // then.
            assertEquals("weather", appVersion.getData().getName());
            assertEquals("app", appVersion.getData().getType());
            assertEquals("basic", appVersion.getData().getAppBuiltType());
            assertEquals("1.0.0", appVersion.getData().getVersion());
            assertEquals("chatbot", appVersion.getData().getAppCategory());
            assertEquals("31f20efc7e0848deab6a6bc10fc30111", appVersion.getData().getTenantId());
            assertEquals(AppTypeEnum.TEMPLATE.code(), appVersion.getData().getAppType());
            assertEquals("app_1", appVersion.getData().getAppSuiteId());
            assertEquals(AppState.IMPORTING.getName(), appVersion.getData().getState());
            assertEquals("31f20efc7e0848deab6a6bc10fc30111", appVersion.getConfig().getTenantId());
            assertEquals("flow_1", appVersion.getFlowGraph().getName());
            assertTrue(appVersion.getFormProperties().isEmpty());
        }

        private AppExportDto buildExportDto() throws IOException {
            String appearance = IoUtils.content(AppVersionTest.class, "/appearance.txt");
            return AppExportDto.builder()
                    .version("1.0.1")
                    .app(AppExportApp.builder()
                            .name("weather")
                            .tenantId("31f20efc7e0848deab6a6bc10fc3021e")
                            .type("app")
                            .appBuiltType("basic")
                            .appCategory("chatbot")
                            .appType(AppTypeEnum.TEMPLATE.code())
                            .version("1.0.1")
                            .build())
                    .config(AppExportConfig.builder()
                            .form(AppExportForm.builder()
                                    .id("form_1")
                                    .appearance(JsonUtils.parseObject(appearance))
                                    .type("component")
                                    .formSuiteId("form_suite_1")
                                    .version("1.0.1")
                                    .build())
                            .configProperties(Collections.emptyList())
                            .build())
                    .flowGraph(AppExportFlowGraph.builder().name("flow_1").appearance(appearance).build())
                    .build();
        }
    }

    /**
     * 测试导出
     */
    @Nested
    @DisplayName("测试导出")
    public class TestExport {
        @Test
        @DisplayName("测试创建者和操作人不一致，抛出异常")
        public void testExceptionWhenCreateUserIsNotOperator() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setId("app_version_1");
            data.setAppId("app_version_1");
            data.setCreateBy("wla");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            // when.
            OperationContext operationContext = new OperationContext();
            operationContext.setOperator("zy");
            Map<String, String> exportMeta = MapBuilder.<String, String>get().put("version", "1.0.1").build();
            AippException exception = assertThrows(AippException.class,
                    () -> appVersion.export(operationContext, exportMeta));

            // then.
            assertEquals(AippErrCode.EXPORT_CONFIG_UNAUTHED.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("测试成功场景")
        public void testSuccess() {
            // given.
            AppBuilderAppPo data = this.buildData();
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            AppBuilderConfig appBuilderConfig = mock(AppBuilderConfig.class);
            when(AppVersionTest.this.configRepository.selectWithId(anyString())).thenReturn(appBuilderConfig);
            when(appBuilderConfig.getConfigProperties()).thenReturn(Collections.emptyList());
            when(appBuilderConfig.getForm()).thenReturn(this.buildForm());

            AppBuilderFlowGraph graph = mock(AppBuilderFlowGraph.class);
            when(AppVersionTest.this.flowGraphRepository.selectWithId(anyString())).thenReturn(graph);
            when(graph.getName()).thenReturn("mock_graph_1");
            when(AppVersionTest.this.aippFlowDefinitionService.getParsedGraphData(anyString(), anyString())).thenReturn("testFlowDefinition");
            doNothing().when(AppVersionTest.this.flowDefinitionService).validateDefinitionData(anyString());
            when(graph.getAppearance()).thenReturn("xxxxxxxx");

            // when.
            OperationContext operationContext = new OperationContext();
            operationContext.setName("zy");
            Map<String, String> exportMeta = MapBuilder.<String, String>get().put("version", "1.0.1").build();
            AppExportDto exportDto = appVersion.export(operationContext, exportMeta);

            // then.
            assertEquals("1.0.1", exportDto.getVersion());
            assertEquals("gameLand", exportDto.getApp().getName());
            assertEquals("31f20efc7e0848deab6a6bc10fc30111", exportDto.getApp().getTenantId());
            assertEquals("app", exportDto.getApp().getType());
            assertEquals("basic", exportDto.getApp().getAppBuiltType());
            assertEquals("1.0.1", exportDto.getApp().getVersion());
            assertEquals("chatbot", exportDto.getApp().getAppCategory());
            assertEquals(AppTypeEnum.TEMPLATE.code(), exportDto.getApp().getAppType());

            assertEquals("form_1", exportDto.getConfig().getForm().getId());
            assertEquals("smartForm", exportDto.getConfig().getForm().getName());
            assertEquals("component", exportDto.getConfig().getForm().getType());
            assertEquals("form_suite_1", exportDto.getConfig().getForm().getFormSuiteId());
            assertEquals("1.0.1_form", exportDto.getConfig().getForm().getVersion());
            assertTrue(exportDto.getConfig().getForm().getAppearance().isEmpty());

            assertEquals("mock_graph_1", exportDto.getFlowGraph().getName());
            assertEquals("xxxxxxxx", exportDto.getFlowGraph().getAppearance());
        }

        private AppBuilderForm buildForm() {
            return AppBuilderForm.builder()
                    .id("form_1")
                    .name("smartForm")
                    .appearance(new HashMap<>())
                    .type("component")
                    .formSuiteId("form_suite_1")
                    .version("1.0.1_form")
                    .build();
        }

        private AppBuilderAppPo buildData() {
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setId("app_version_1");
            data.setAppId("app_version_1");
            data.setCreateBy("zy");
            data.setName("gameLand");
            data.setTenantId("31f20efc7e0848deab6a6bc10fc30111");
            data.setType("app");
            data.setAppBuiltType("basic");
            data.setVersion("1.0.1");
            data.setAppCategory("chatbot");
            data.setAttributes(JsonUtils.toJsonString(new HashMap<>()));
            data.setAppType(AppTypeEnum.TEMPLATE.code());
            data.setConfigId("config_1");
            data.setFlowGraphId("graph_1");
            return data;
        }
    }

    /**
     * 测试preview
     */
    @Nested
    @DisplayName("测试preview")
    public class TestPreview {
        @Test
        @DisplayName("应用最新的任务是已发布状态")
        public void testLatestTaskIsPublished() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            AppTask appTask = mock(AppTask.class);
            when(AppVersionTest.this.appTaskService.getTasksByAppId(anyString(), any())).thenReturn(List.of(appTask));
            when(appTask.isPublished()).thenReturn(true);
            when(appTask.getEntity()).thenReturn(AppTask.asEntity().setAppSuiteId("app_1").setVersion("1.0.0"));

            // when.
            AippCreateDto aippCreateDto = appVersion.preview("1.0.0", null, new OperationContext());

            // then.
            assertEquals("app_1", aippCreateDto.getAippId());
            assertEquals("1.0.0", aippCreateDto.getVersion());
        }

        @Test
        @DisplayName("存在flowDefinition相同的task")
        public void testHasSameDefinitionTask() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            AppTask appTask = mock(AppTask.class);
            when(appTaskService.getTasksByAppId(anyString(), any())).thenReturn(Collections.emptyList());
            when(appDefinitionService.getSameFlowDefinition(any())).thenReturn(new FlowDefinitionResult());
            when(appTaskService.getTasks(any(), any())).thenReturn(RangedResultSet.create(List.of(appTask), 0, 1, 1));
            when(appTask.getEntity()).thenReturn(AppTask.asEntity().setAppSuiteId("app_2").setVersion("1.0.1"));

            // when.
            AippCreateDto aippCreateDto = appVersion.preview("1.0.0", new AippDto(), new OperationContext());

            // then.
            assertEquals("app_2", aippCreateDto.getAippId());
            assertEquals("1.0.1", aippCreateDto.getVersion());
        }

        @Test
        @DisplayName("baseLineVersion是预览版本")
        public void testBaseLineVersionIsPreview() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            when(appTaskService.getTasksByAppId(anyString(), any())).thenReturn(Collections.emptyList());
            when(appDefinitionService.getSameFlowDefinition(any())).thenReturn(null);

            // when.
            AippParamException exception = assertThrows(AippParamException.class,
                    () -> appVersion.preview("1.0.0-temp", new AippDto(), new OperationContext()));

            // then.
            assertEquals(AippErrCode.INPUT_PARAM_IS_INVALID.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("测试成功")
        public void testSuccess() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            when(appTaskService.getTasksByAppId(anyString(), any())).thenReturn(Collections.emptyList());
            when(appDefinitionService.getSameFlowDefinition(any())).thenReturn(null);

            AippDto aippDto = new AippDto();
            aippDto.setAppId("app_version_1");
            aippDto.setFlowViewData(
                    MapBuilder.<String, Object>get().put(AippConst.FLOW_CONFIG_ID_KEY, "flow_1").build());

            FlowInfo flowInfo = mock(FlowInfo.class);
            when(flowsService.publishFlowsWithoutElsa(anyString(), anyString(), anyString(), any())).thenReturn(
                    flowInfo);

            AppTask task = mock(AppTask.class);
            when(appTaskService.createTask(any(), any())).thenReturn(task);
            when(task.getEntity()).thenReturn(AppTask.asEntity().setAppSuiteId("app_3"));

            // when.
            AippCreateDto result = appVersion.preview("1.0.0", aippDto, new OperationContext());

            // then.
            assertEquals("app_3", result.getAippId());
            assertTrue(result.getVersion().contains("-"));
        }

        @Test
        @DisplayName("测试创建task抛出异常")
        public void testRetryException() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            when(appTaskService.getTasksByAppId(anyString(), any())).thenReturn(Collections.emptyList());
            when(appDefinitionService.getSameFlowDefinition(any())).thenReturn(null);

            AippDto aippDto = new AippDto();
            aippDto.setAppId("app_version_1");
            aippDto.setFlowViewData(
                    MapBuilder.<String, Object>get().put(AippConst.FLOW_CONFIG_ID_KEY, "flow_1").build());

            when(flowsService.publishFlowsWithoutElsa(anyString(), anyString(), anyString(), any())).thenThrow(
                    new JobberException(ErrorCodes.FLOW_ALREADY_EXIST));

            // when.
            AippException exception = assertThrows(AippException.class,
                    () -> appVersion.preview("1.0.0", aippDto, new OperationContext()));

            // then.
            assertEquals(AippErrCode.INVALID_FLOW_CONFIG.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("测试流程节点数量异常")
        public void testInvalidFlowNodeSizeException() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            when(appTaskService.getTasksByAppId(anyString(), any())).thenReturn(Collections.emptyList());
            when(appDefinitionService.getSameFlowDefinition(any())).thenReturn(null);

            AippDto aippDto = new AippDto();
            aippDto.setAppId("app_version_1");
            aippDto.setFlowViewData(
                    MapBuilder.<String, Object>get().put(AippConst.FLOW_CONFIG_ID_KEY, "flow_1").build());

            when(flowsService.publishFlowsWithoutElsa(anyString(), anyString(), anyString(), any())).thenThrow(
                    new JobberException(ErrorCodes.INVALID_FLOW_NODE_SIZE));

            // when.
            AippException exception = assertThrows(AippException.class,
                    () -> appVersion.preview("1.0.0", aippDto, new OperationContext()));

            // then.
            assertEquals(AippErrCode.INVALID_FLOW_NODE_SIZE.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("测试流程开始节点事件异常")
        public void testInvalidStartNodeEventException() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            when(appTaskService.getTasksByAppId(anyString(), any())).thenReturn(Collections.emptyList());
            when(appDefinitionService.getSameFlowDefinition(any())).thenReturn(null);

            AippDto aippDto = new AippDto();
            aippDto.setAppId("app_version_1");
            aippDto.setFlowViewData(
                    MapBuilder.<String, Object>get().put(AippConst.FLOW_CONFIG_ID_KEY, "flow_1").build());

            when(flowsService.publishFlowsWithoutElsa(anyString(), anyString(), anyString(), any())).thenThrow(
                    new JobberException(ErrorCodes.INVALID_START_NODE_EVENT_SIZE));

            // when.
            AippException exception = assertThrows(AippException.class,
                    () -> appVersion.preview("1.0.0", aippDto, new OperationContext()));

            // then.
            assertEquals(AippErrCode.INVALID_START_NODE_EVENT_SIZE.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("测试流程事件异常")
        public void testInvalidEventConfigException() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            when(appTaskService.getTasksByAppId(anyString(), any())).thenReturn(Collections.emptyList());
            when(appDefinitionService.getSameFlowDefinition(any())).thenReturn(null);

            AippDto aippDto = new AippDto();
            aippDto.setAppId("app_version_1");
            aippDto.setFlowViewData(
                    MapBuilder.<String, Object>get().put(AippConst.FLOW_CONFIG_ID_KEY, "flow_1").build());

            when(flowsService.publishFlowsWithoutElsa(anyString(), anyString(), anyString(), any())).thenThrow(
                    new JobberException(ErrorCodes.INVALID_EVENT_CONFIG));

            // when.
            AippException exception = assertThrows(AippException.class,
                    () -> appVersion.preview("1.0.0", aippDto, new OperationContext()));

            // then.
            assertEquals(AippErrCode.INVALID_EVENT_CONFIG.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("测试State类型节点事件异常")
        public void testInvalidStateNodeEventConfigException() {
            // given.
            AppBuilderAppPo data = new AppBuilderAppPo();
            data.setAppId("app_version_1");
            AppVersion appVersion = AppVersionTest.this.factory.create(data, AppVersionTest.this.appVersionRepository);

            when(appTaskService.getTasksByAppId(anyString(), any())).thenReturn(Collections.emptyList());
            when(appDefinitionService.getSameFlowDefinition(any())).thenReturn(null);

            AippDto aippDto = new AippDto();
            aippDto.setAppId("app_version_1");
            aippDto.setFlowViewData(
                    MapBuilder.<String, Object>get().put(AippConst.FLOW_CONFIG_ID_KEY, "flow_1").build());

            when(flowsService.publishFlowsWithoutElsa(anyString(), anyString(), anyString(), any())).thenThrow(
                    new JobberException(ErrorCodes.INVALID_STATE_NODE_EVENT_SIZE));

            // when.
            AippException exception = assertThrows(AippException.class,
                    () -> appVersion.preview("1.0.0", aippDto, new OperationContext()));

            // then.
            assertEquals(AippErrCode.INVALID_EVENT_CONFIG.getCode(), exception.getCode());
        }
    }
}
