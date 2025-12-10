/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.AIPP_NAME_IS_DUPLICATE;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.AIPP_NAME_LENGTH_OUT_OF_BOUNDS;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_HAS_ALREADY;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_NAME_IS_INVALID;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_NOT_FOUND;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.domain.division.service.DomainDivisionService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.appversion.repository.impl.AppVersionRepositoryImpl;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.appversion.service.impl.AppVersionServiceImpl;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.chat.repository.AppChatRepository;
import modelengine.fit.jober.aipp.domains.log.AppLog;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import modelengine.fit.jober.aipp.dto.AppBuilderSaveConfigDto;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.enums.AppTypeEnum;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppMapper;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.util.MapBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

/**
 * {@link AppVersionService} 的测试类。
 *
 * @author 张越
 * @since 2025-02-12
 */
public class AppVersionServiceTest {
    private static final int NAME_LENGTH_MAXIMUM = 10;

    private AppVersionService appVersionService;
    private AppVersionRepository appVersionRepository;
    private AppChatRepository appChatRepository;
    private AppTaskInstanceService appTaskInstanceService;
    private UploadedFileManageService uploadedFileManageService;
    private AppBuilderConfigRepository configRepository;
    private AppBuilderFlowGraphRepository flowGraphRepository;
    private AppBuilderFormPropertyRepository formPropertyRepository;
    private AppBuilderConfigPropertyRepository configPropertyRepository;
    private AppTaskService appTaskService;
    private AppVersionFactory appVersionFactory;
    private AppBuilderAppMapper appBuilderAppMapper;
    private IconConverter iconConverter;
    private DomainDivisionService domainDivisionService;


    @BeforeEach
    public void setUp() {
        this.appVersionFactory = mock(AppVersionFactory.class);
        this.appBuilderAppMapper = mock(AppBuilderAppMapper.class);
        this.iconConverter = mock(IconConverter.class);
        this.appVersionRepository =
                new AppVersionRepositoryImpl(this.appBuilderAppMapper, this.appVersionFactory, this.iconConverter);
        this.appChatRepository = mock(AppChatRepository.class);
        this.appTaskInstanceService = mock(AppTaskInstanceService.class);
        this.uploadedFileManageService = mock(UploadedFileManageService.class);
        this.configRepository = mock(AppBuilderConfigRepository.class);
        this.flowGraphRepository = mock(AppBuilderFlowGraphRepository.class);
        this.formPropertyRepository = mock(AppBuilderFormPropertyRepository.class);
        this.configPropertyRepository = mock(AppBuilderConfigPropertyRepository.class);
        this.appTaskService = mock(AppTaskService.class);
        this.domainDivisionService = mock(DomainDivisionService.class);
        this.appVersionService = new AppVersionServiceImpl(this.appVersionRepository, this.appChatRepository,
                this.appTaskInstanceService, this.uploadedFileManageService, this.configRepository,
                this.flowGraphRepository, this.formPropertyRepository, this.configPropertyRepository,
                this.appTaskService,
                this.appVersionFactory,
                this.domainDivisionService,
                AppVersionServiceTest.NAME_LENGTH_MAXIMUM, true);
    }

    @Test
    @DisplayName("测试getByAppId")
    public void testGetByAppId() {
        // given.
        when(this.appVersionFactory.create(any(), any())).thenReturn(mock(AppVersion.class));
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(
                AppBuilderAppPo.builder().appSuiteId("app_1").build());

        // when.
        Optional<AppVersion> versionOptional = this.appVersionService.getByAppId("app_version_1");

        // then.
        assertTrue(versionOptional.isPresent());
        verify(this.appBuilderAppMapper, times(1)).selectWithId(eq("app_version_1"));
    }

    @Test
    @DisplayName("测试getByPath")
    public void testGetByPath() {
        // given.
        when(this.appVersionFactory.create(any(), any())).thenReturn(mock(AppVersion.class));
        when(this.appBuilderAppMapper.selectWithPath(anyString())).thenReturn(
                AppBuilderAppPo.builder().appSuiteId("app_1").build());

        // when.
        Optional<AppVersion> versionOptional = this.appVersionService.getByPath("/app_version_1");

        // then.
        assertTrue(versionOptional.isPresent());
        verify(this.appBuilderAppMapper, times(1)).selectWithPath(eq("/app_version_1"));
    }

    @Test
    @DisplayName("测试 retrieval")
    public void testRetrieval() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().appSuiteId("app_1").build();
        AppVersion result = mock(AppVersion.class);
        when(result.getData()).thenReturn(data);
        when(this.appVersionFactory.create(any(), any())).thenReturn(result);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);

        // when.
        AppVersion version = this.appVersionService.retrieval("app_version_1");

        // then.
        assertEquals("app_1", version.getData().getAppSuiteId());
    }

    @Test
    @DisplayName("测试 retrieval 异常")
    public void testRetrievalException() {
        // given.
        when(this.appVersionFactory.create(any(), any())).thenReturn(null);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(null);

        // when.
        AippException exception = assertThrows(AippException.class,
                () -> this.appVersionService.retrieval("app_version_1"));

        // then.
        assertEquals(APP_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试 getByAppSuiteId")
    public void testGetByAppSuiteId() {
        // given.
        when(this.appVersionFactory.create(any(), any())).thenReturn(null);
        when(this.appBuilderAppMapper.selectByAppSuiteId(anyString())).thenReturn(Collections.emptyList());

        // when.
        List<AppVersion> results = this.appVersionService.getByAppSuiteId("app_1");

        // then.
        assertEquals(0, results.size());
        verify(this.appBuilderAppMapper, times(1)).selectByAppSuiteId(eq("app_1"));
    }

    @Test
    @DisplayName("测试 run")
    public void testRun() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().appSuiteId("app_1").build();
        AppVersion result = mock(AppVersion.class);
        when(result.getData()).thenReturn(data);
        doNothing().when(result).run(any(), any());
        when(this.appVersionFactory.create(any(), any())).thenReturn(result);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        doNothing().when(this.appChatRepository).saveChat(any(), any());

        // when.
        CreateAppChatRequest request = CreateAppChatRequest.builder().context(new CreateAppChatRequest.Context())
                .question("123").appId("app_version_1").build();
        Choir<Object> choir = this.appVersionService.run(request, new OperationContext());
        choir.subscribe();

        // then.
        ArgumentCaptor<RunContext> captor = ArgumentCaptor.forClass(RunContext.class);
        verify(result, times(1)).run(captor.capture(), any());
        RunContext runContext = captor.getValue();
        assertEquals("123", runContext.getQuestion());
    }

    @Test
    @DisplayName("测试 debug")
    public void testDebug() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().appSuiteId("app_1").build();
        AppVersion result = mock(AppVersion.class);
        when(result.getData()).thenReturn(data);
        doNothing().when(result).debug(any(), any());
        when(this.appVersionFactory.create(any(), any())).thenReturn(result);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        doNothing().when(this.appChatRepository).saveChat(any(), any());

        // when.
        CreateAppChatRequest request = CreateAppChatRequest.builder().context(new CreateAppChatRequest.Context())
                .question("123").appId("app_version_1").build();
        Choir<Object> choir = this.appVersionService.debug(request, new OperationContext());
        choir.subscribe((subscription, o) -> {
            // then.
            ArgumentCaptor<RunContext> captor = ArgumentCaptor.forClass(RunContext.class);
            verify(result, times(1)).debug(captor.capture(), any());
            RunContext runContext = captor.getValue();
            assertEquals("123", runContext.getQuestion());
        });
    }

    @Test
    @DisplayName("测试 restart")
    public void testRestart() {
        // given.
        when(this.appTaskInstanceService.getTaskId(anyString())).thenReturn("task_1");

        AppTask appTask = mock(AppTask.class);
        when(this.appTaskService.getTaskById(anyString(), any())).thenReturn(Optional.of(appTask));
        when(appTask.getEntity()).thenReturn(AppTask.asEntity().setAppId("app_version_1"));

        AppBuilderAppPo data = AppBuilderAppPo.builder().appSuiteId("app_1").build();
        AppVersion result = mock(AppVersion.class);
        when(result.getData()).thenReturn(data);
        doNothing().when(result).restart(any(), anyMap(), any(), any(), any());
        when(this.appVersionFactory.create(any(), any())).thenReturn(result);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        doNothing().when(this.appChatRepository).saveChat(any(), any());
        AppTaskInstance instance = mock(AppTaskInstance.class);
        when(this.appTaskInstanceService.getInstanceById(anyString(), any())).thenReturn(
                Optional.of(instance));
        when(instance.getChats()).thenReturn(
                List.of(QueryChatRsp.builder().appId("app_version_1").chatId("chat_1").build()));

        AppLog appLog = mock(AppLog.class);
        when(instance.getLogs()).thenReturn(List.of(appLog));
        when(appLog.getLogData()).thenReturn(AippInstLog.builder()
                .logData(JsonUtils.toJsonString(MapBuilder.get().put("msg", "你在哪里?").build()))
                .build());

        // when.
        Choir<Object> choir = this.appVersionService.restart("instance_1", new HashMap<>(), new OperationContext());
        choir.subscribe((subscription, o) -> {
            // then.
            verify(appTaskInstanceService, times(1)).getTaskId(eq("instance_1"));
            verify(appTaskService, times(1)).getTaskById(eq("task_1"), any());
            verify(result, times(1)).restart(any(), anyMap(), any(), any(), any());
        });
    }

    @Test
    @DisplayName("测试 create")
    public void testCreate() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().appSuiteId("app_1").build();
        AppVersion result = mock(AppVersion.class);
        AppBuilderConfig config = mock(AppBuilderConfig.class);
        when(result.getData()).thenReturn(data);
        when(result.getIcon()).thenReturn("/icon.png");
        when(result.getConfig()).thenReturn(config);
        doNothing().when(result).cloneVersion(any(), anyString(), anyString(), any());
        when(config.getConfigProperties()).thenReturn(Collections.emptyList());
        when(this.appVersionFactory.create(any(), any())).thenReturn(result);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        when(this.domainDivisionService.getUserGroupId()).thenReturn("g1");
        this.mockSave();

        // when.
        AppVersion res = this.appVersionService.create("app_template_1",
                AppBuilderAppCreateDto.builder().name("myApp").appCategory("gory1").build(), new OperationContext());

        // then.
        assertEquals(result, res);
    }

    @Test
    @DisplayName("测试 create 异常")
    public void testCreateException() {
        // given.
        when(this.appBuilderAppMapper.selectWithCondition(any())).thenReturn(
                List.of(AppBuilderAppPo.builder().build()));

        // when.
        AippException exception = assertThrows(AippException.class,
                () -> this.appVersionService.create("app_template_1",
                        AppBuilderAppCreateDto.builder().appCategory("gory1").name("myApp").build(), new OperationContext()));

        // then.
        assertEquals(AIPP_NAME_IS_DUPLICATE.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试 createByTemplate")
    public void testCreateByTemplate() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().appSuiteId("app_1").build();
        AppVersion result = mock(AppVersion.class);
        AppBuilderConfig config = mock(AppBuilderConfig.class);
        when(result.getData()).thenReturn(data);
        when(result.getIcon()).thenReturn("/icon.png");
        when(result.getConfig()).thenReturn(config);
        doNothing().when(result).cloneVersion(any(), anyString(), anyString(), any());
        when(config.getConfigProperties()).thenReturn(Collections.emptyList());
        when(this.appVersionFactory.create(any(), any())).thenReturn(result);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        this.mockSave();

        // when.
        AppTemplate appTemplate = new AppTemplate();
        appTemplate.setName("myApp");
        appTemplate.setConfigId("config_1");
        appTemplate.setFlowGraphId("graph_1");
        appTemplate.setAttributes(new HashMap<>());
        AppVersion res = this.appVersionService.createByTemplate(appTemplate, new OperationContext());
        when(this.domainDivisionService.getUserGroupId()).thenReturn("g1");

        // then.
        assertEquals("config_1", res.getData().getConfigId());
        assertEquals("graph_1", res.getData().getFlowGraphId());
    }

    @Test
    @DisplayName("测试 upgrade")
    public void testUpgrade() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().appSuiteId("app_1").build();
        AppVersion result = mock(AppVersion.class);
        AppBuilderConfig config = mock(AppBuilderConfig.class);
        when(result.getData()).thenReturn(data);
        when(result.getIcon()).thenReturn("/icon.png");
        when(result.getConfig()).thenReturn(config);
        doNothing().when(result).upgrade(any(), anyString(), any());
        when(config.getConfigProperties()).thenReturn(Collections.emptyList());
        when(this.appVersionFactory.create(any(), any())).thenReturn(result);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        this.mockSave();

        // when.
        AppVersion res = this.appVersionService.upgrade("app_version_1", AppBuilderAppCreateDto.builder().build(),
                new OperationContext());

        // then.
        assertEquals("app_1", res.getData().getAppSuiteId());
    }

    @Test
    @DisplayName("测试 validateAppName 格式不匹配")
    public void testValidateAppNameFormatNotMatch() {
        // given.
        // when.
        AippParamException exception = assertThrows(AippParamException.class,
                () -> this.appVersionService.validateAppName("my$App", new OperationContext()));

        // then.
        assertEquals(APP_NAME_IS_INVALID.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试 validateAppName 为空")
    public void testValidateAppNameIsBlank() {
        // given.
        // when.
        AippParamException exception = assertThrows(AippParamException.class,
                () -> this.appVersionService.validateAppName("     ", new OperationContext()));

        // then.
        assertEquals(APP_NAME_IS_INVALID.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试 validateAppName 长度过长")
    public void testValidateAppNameLengthExceeds() {
        // given.
        // when.
        AippParamException exception = assertThrows(AippParamException.class,
                () -> this.appVersionService.validateAppName("myaappppppppppppppppp", new OperationContext()));

        // then.
        assertEquals(AIPP_NAME_LENGTH_OUT_OF_BOUNDS.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试 getLatestCreatedByAppId")
    public void testGetLatestCreatedByAppId() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().appSuiteId("app_1").name("myApp").build();
        AppVersion appVersion = mock(AppVersion.class);
        when(this.appBuilderAppMapper.selectWithCondition(any())).thenReturn(List.of(data));
        when(this.appVersionFactory.create(any(), any())).thenReturn(appVersion);
        when(appVersion.getData()).thenReturn(data);

        // when.
        Optional<AppVersion> versionOptional = this.appVersionService.getLatestCreatedByAppSuiteId("app_1");

        // then.
        assertTrue(versionOptional.isPresent());
        ArgumentCaptor<AppQueryCondition> appVersionCaptor = ArgumentCaptor.forClass(AppQueryCondition.class);
        verify(this.appBuilderAppMapper, times(1)).selectWithCondition(appVersionCaptor.capture());
        AppQueryCondition condition = appVersionCaptor.getValue();
        assertEquals("create_at", condition.getOrderBy().toLowerCase(Locale.ROOT));
        assertEquals("DESC", condition.getSort().toUpperCase(Locale.ROOT));
        assertEquals(0, condition.getOffset());
        assertEquals(1, condition.getLimit());
        assertEquals("app_1", condition.getAppSuiteId());
    }

    @Test
    @DisplayName("测试 pageListByTenantId")
    public void testPageListByTenantId() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().appSuiteId("app_1").name("myApp").build();
        AppVersion appVersion = mock(AppVersion.class);
        when(this.appBuilderAppMapper.selectByTenantIdWithPage(any(), anyString(), anyLong(), anyInt())).thenReturn(
                List.of(data));
        when(this.appVersionFactory.create(any(), any())).thenReturn(appVersion);
        when(appVersion.getData()).thenReturn(data);

        when(this.appBuilderAppMapper.countByTenantId(anyString(), any())).thenReturn(1L);

        // when.
        RangedResultSet<AppVersion> appVersionRangedResultSet = this.appVersionService.pageListByTenantId(
                AppQueryCondition.builder().build(), "31f20efc7e0848deab6a6bc10fc3021e", 0, 1);

        // then.
        assertFalse(appVersionRangedResultSet.isEmpty());
        assertEquals(appVersion, appVersionRangedResultSet.getResults().get(0));
    }

    @Test
    @DisplayName("测试 update version已发布")
    public void testUpdateVersionPublished() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().appSuiteId("app_1").name("myApp").build();
        AppVersion appVersion = mock(AppVersion.class);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        when(this.appVersionFactory.create(any(), any())).thenReturn(appVersion);
        when(appVersion.getData()).thenReturn(data);
        when(appVersion.isPublished()).thenReturn(true);

        // when.
        AippException exception = assertThrows(AippException.class,
                () -> this.appVersionService.update("app_version_1", AppBuilderAppDto.builder().build(),
                        new OperationContext()));

        // then.
        assertEquals(APP_HAS_ALREADY.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试 update version")
    public void testUpdateVersion() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder()
                .appSuiteId("app_1")
                .name("myApp")
                .state(AppState.IMPORTING.getName())
                .build();
        AppVersion appVersion = mock(AppVersion.class);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        when(this.appVersionFactory.create(any(), any())).thenReturn(appVersion);
        when(appVersion.getData()).thenReturn(data);
        when(appVersion.getIcon()).thenReturn("icon1").thenReturn("icon2");
        doNothing().when(appVersion).putAttributes(any());

        doNothing().when(this.appBuilderAppMapper).updateOne(any());
        doNothing().when(this.uploadedFileManageService).changeRemovable(anyString(), anyInt());
        doNothing().when(this.appBuilderAppMapper).updateOne(any());

        // Mock getPreviewTasks to return empty list (no name change in this test)
        when(this.appTaskService.getPreviewTasks(anyString(), any())).thenReturn(Collections.emptyList());

        // when.
        OperationContext context = new OperationContext();
        context.setOperator("zy");
        this.appVersionService.update("app_version_1", AppBuilderAppDto.builder()
                .name("myApp")
                .type(AppTypeEnum.APP.code())
                .appType(NORMAL.name())
                .state(AppState.INACTIVE.getName())
                .version("1.0.0")
                .build(), context);

        // then.
        assertEquals("zy", data.getUpdateBy());
        assertEquals(AppTypeEnum.APP.code(), data.getType());
        assertEquals(NORMAL.name(), data.getAppType());
        assertEquals(AppState.INACTIVE.getName(), data.getState());
        assertEquals("1.0.0", data.getVersion());
        verify(this.uploadedFileManageService, times(2)).changeRemovable(anyString(), anyInt());
    }

    @Test
    @DisplayName("测试 update version with name change")
    public void testUpdateVersionWithNameChange() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder()
                .appSuiteId("app_1")
                .name("oldAppName")
                .state(AppState.IMPORTING.getName())
                .build();
        AppVersion appVersion = mock(AppVersion.class);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        when(this.appVersionFactory.create(any(), any())).thenReturn(appVersion);
        when(appVersion.getData()).thenReturn(data);
        when(appVersion.getIcon()).thenReturn("icon1");
        doNothing().when(appVersion).putAttributes(any());

        doNothing().when(this.appBuilderAppMapper).updateOne(any());
        doNothing().when(this.uploadedFileManageService).changeRemovable(anyString(), anyInt());

        // Mock preview tasks
        AppTask previewTask = mock(AppTask.class);
        when(previewTask.getEntity()).thenReturn(AppTask.asEntity());
        when(this.appTaskService.getPreviewTasks(eq("app_1"), any())).thenReturn(List.of(previewTask));
        doNothing().when(this.appTaskService).updateTask(any(), any());

        // when.
        OperationContext context = new OperationContext();
        context.setOperator("zy");
        this.appVersionService.update("app_version_1", AppBuilderAppDto.builder()
                .name("newAppName")
                .type(AppTypeEnum.APP.code())
                .appType(NORMAL.name())
                .state(AppState.INACTIVE.getName())
                .version("1.0.0")
                .build(), context);

        // then.
        assertEquals("newAppName", data.getName());
        assertEquals("zy", data.getUpdateBy());
        // Verify that preview task was updated
        verify(this.appTaskService, times(1)).getPreviewTasks(eq("app_1"), any());
        verify(this.appTaskService, times(1)).updateTask(eq(previewTask), any());
    }

    @Test
    @DisplayName("测试 update 通过graph")
    public void testUpdateByGraph() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().build();
        AppVersion appVersion = mock(AppVersion.class);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        when(this.appVersionFactory.create(any(), any())).thenReturn(appVersion);
        when(appVersion.getData()).thenReturn(data);
        when(appVersion.isPublished()).thenReturn(false);
        doNothing().when(appVersion).putAttributes(any());

        AppBuilderFlowGraph graph = AppBuilderFlowGraph.builder().build();
        when(appVersion.getFlowGraph()).thenReturn(graph);

        AppBuilderConfig config = mock(AppBuilderConfig.class);
        when(appVersion.getConfig()).thenReturn(config);
        doNothing().when(config).updateByAppearance(anyString());

        doNothing().when(this.flowGraphRepository).updateOne(any());
        doNothing().when(this.configRepository).updateOne(any());
        doNothing().when(this.appBuilderAppMapper).updateOne(any());

        // when.
        OperationContext context = new OperationContext();
        context.setOperator("zy");
        this.appVersionService.update("app_version_1",
                AppBuilderFlowGraphDto.builder().name("myApp").appearance(new HashMap<>()).build(), context);

        // then.
        assertEquals("zy", graph.getUpdateBy());
        assertEquals("myApp", graph.getName());
        assertEquals("{}", graph.getAppearance());
        assertEquals("zy", data.getUpdateBy());
    }

    @Test
    @DisplayName("测试 update 通过graph，version已发布")
    public void testUpdateByGraphAndVersionIsPublished() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().build();
        AppVersion appVersion = mock(AppVersion.class);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        when(this.appVersionFactory.create(any(), any())).thenReturn(appVersion);
        when(appVersion.isPublished()).thenReturn(true);

        // when.
        OperationContext context = new OperationContext();
        context.setOperator("zy");
        AippException exception = assertThrows(AippException.class, () -> this.appVersionService.update("app_version_1",
                AppBuilderFlowGraphDto.builder().name("myApp").appearance(new HashMap<>()).build(), context));

        // then.
        assertEquals(APP_HAS_ALREADY.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试 update 通过config")
    public void testUpdateByConfig() {
        // given.
        AppBuilderAppPo data = AppBuilderAppPo.builder().build();
        AppVersion appVersion = mock(AppVersion.class);
        when(this.appBuilderAppMapper.selectWithId(anyString())).thenReturn(data);
        when(this.appVersionFactory.create(any(), any())).thenReturn(appVersion);
        when(appVersion.isPublished()).thenReturn(true);
        when(appVersion.getData()).thenReturn(data);

        AppBuilderFlowGraph flowGraph = AppBuilderFlowGraph.builder().build();
        when(appVersion.getFlowGraph()).thenReturn(flowGraph);

        // when.
        OperationContext context = new OperationContext();
        context.setOperator("zy");
        this.appVersionService.update("app_version_1", AppBuilderSaveConfigDto.builder()
                .graph("graphxxx")
                .input(List.of(AppBuilderConfigFormPropertyDto.builder().build()))
                .build(), context);

        // then.
        assertEquals("graphxxx", flowGraph.getAppearance());
        verify(this.formPropertyRepository, times(1)).updateMany(anyList());
        verify(this.flowGraphRepository, times(1)).updateOne(eq(flowGraph));
    }

    private void mockSave() {
        doNothing().when(this.appBuilderAppMapper).insertOne(any());
        doNothing().when(this.appChatRepository).saveChat(any(), any());
        doNothing().when(this.uploadedFileManageService).updateRecord(anyString(), anyString(), anyInt());
        doNothing().when(this.flowGraphRepository).insertOne(any());
        doNothing().when(this.configRepository).insertOne(any());
        doNothing().when(this.configPropertyRepository).insertMore(anyList());
        doNothing().when(this.formPropertyRepository).insertMore(anyList());
    }
}
