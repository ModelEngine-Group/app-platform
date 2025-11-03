/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.domain.division.service.DomainDivisionService;
import modelengine.fit.jade.waterflow.AippFlowDefinitionService;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippJsonDecodeException;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.converters.ConverterFactory;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domains.app.AppFactory;
import modelengine.fit.jober.aipp.domains.app.service.AppDomainService;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.AppVersionFactory;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigDto;
import modelengine.fit.jober.aipp.dto.AppBuilderNodeConfigsDto;
import modelengine.fit.jober.aipp.dto.AppBuilderSaveConfigDto;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.factory.AppTemplateFactory;
import modelengine.fit.jober.aipp.factory.CheckerFactory;
import modelengine.fit.jober.aipp.genericable.entity.AippCreate;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.service.impl.AppBuilderAppServiceImpl;
import modelengine.fit.jober.aipp.service.impl.RetrievalNodeChecker;
import modelengine.fit.jober.aipp.util.ConvertUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.knowledge.KnowledgeCenterService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 姚江
 * @since 2024-04-29
 */
@ExtendWith(MockitoExtension.class)
public class AppBuilderAppServiceImplTest {
    @Mock
    private UploadedFileManageService uploadedFileService;
    @Mock
    private AppTaskService appTaskService;
    @Mock
    private AppVersionService appVersionService;
    @Mock
    private AppDomainService appDomainService;
    @Mock
    private AppFactory appDomainFactory;
    @Mock
    private AppTemplateFactory templateFactory;

    private ConverterFactory converterFactory;

    @Mock
    private AippFlowDefinitionService aippFlowDefinitionService;

    @Mock
    private FlowDefinitionService flowDefinitionService;

    private AppBuilderAppServiceImpl appBuilderAppService;

    private MockedStatic<ConvertUtils> mockConvertUtils;

    @Mock
    private KnowledgeCenterService knowledgeCenterService;

    @Mock
    private DomainDivisionService domainDivisionService;

    @Mock
    private AppBuilderAppRepository appBuilderAppRepository;

    @BeforeEach
    public void before() {
        this.converterFactory = mock(ConverterFactory.class);
        appBuilderAppService = new AppBuilderAppServiceImpl(this.templateFactory,
                this.uploadedFileService,
                this.appTaskService,
                this.appVersionService,
                this.appDomainService,
                this.appDomainFactory,
                this.converterFactory,
                this.knowledgeCenterService,
                this.domainDivisionService,
                true,
                this.appBuilderAppRepository);
        mockConvertUtils = mockStatic(ConvertUtils.class);

    }

    @AfterEach
    void tearDown() {
        mockConvertUtils.close();
    }

    /**
     * 测试 case: appSuiteId 为空时抛出 IllegalArgumentException
     */
    @Test
    void testUpdateNodeConfigs_WhenAppSuiteIdIsEmpty_ShouldThrowException() {
        AppBuilderNodeConfigsDto nodeConfigs = new AppBuilderNodeConfigsDto("", Collections.emptyMap());
        OperationContext context = new OperationContext();

        assertThrows(IllegalArgumentException.class, () ->
                this.appBuilderAppService.updateNodeConfigs(nodeConfigs, context));
    }

    /**
     * 测试 case: getLatestCreatedByAppSuiteId 返回空 Optional 应抛出 AippException
     */
    @Test
    void testUpdateNodeConfigs_WhenGetLatestReturnsEmpty_ShouldThrowAippException() {
        String appSuiteId = "test-suite-id";
        AppBuilderNodeConfigsDto nodeConfigs = new AppBuilderNodeConfigsDto(appSuiteId, Collections.emptyMap());
        OperationContext context = new OperationContext();

        when(appVersionService.getLatestCreatedByAppSuiteId(appSuiteId)).thenReturn(Optional.empty());

        assertThrows(AippException.class, () ->
                appBuilderAppService.updateNodeConfigs(nodeConfigs, context));
    }

    /**
     * 测试 case: 更新当前版本
     */
    @Test
    void testUpdateNodeConfigs_WhenNotPublished_ShouldUpdateCurrentVersionDirectly() {
        String appSuiteId = "test-suite-id";
        Map<String, Object> nodeConfigsMap = new HashMap<>();
        nodeConfigsMap.put("nodeKey",
                Collections.singletonMap("valueParams", Collections.singletonMap("key", "newValue")));

        AppBuilderNodeConfigsDto nodeConfigs = new AppBuilderNodeConfigsDto(appSuiteId, nodeConfigsMap);
        OperationContext context = new OperationContext();

        AppVersion mockAppVersion = mock(AppVersion.class);
        AppBuilderFlowGraph mockFlowGraph = mock(AppBuilderFlowGraph.class);

        when(mockAppVersion.isPublished()).thenReturn(false);
        when(mockAppVersion.getFlowGraph()).thenReturn(mockFlowGraph);
        when(mockFlowGraph.getAppearance()).thenReturn(
                "{\"pages\":[{\"shapes\":[{\"id\":\"nodeKey\","
                        + "\"flowMeta\":{\"jober\":{\"converter\":{\"entity\":{\"inputParams\":[{\"name\":\"key\","
                        + "\"value\":\"oldValue\"}]}}}}}]}]}");

        when(appVersionService.getLatestCreatedByAppSuiteId(appSuiteId)).thenReturn(Optional.of(mockAppVersion));
        doNothing().when(appVersionService).updateGraph(any(), any(), any());
        assertDoesNotThrow(() -> appBuilderAppService.updateNodeConfigs(nodeConfigs, context));
        verify(appVersionService, times(1)).updateGraph(any(), any(), any());
    }

    /**
     * 测试 case: pages字段不是数组时应抛出 AippException
     */
    @Test
    void testUpdateNodeConfigs_WhenPagesIsNotArray_ShouldThrowAippException() {
        String appSuiteId = "test-suite-id";
        Map<String, Object> nodeConfigsMap = new HashMap<>();
        nodeConfigsMap.put("nodeKey",
                Collections.singletonMap("valueParams", Collections.singletonMap("key", "newValue")));

        AppBuilderNodeConfigsDto nodeConfigs = new AppBuilderNodeConfigsDto(appSuiteId, nodeConfigsMap);
        OperationContext context = new OperationContext();

        AppVersion mockAppVersion = mock(AppVersion.class);
        AppBuilderFlowGraph mockFlowGraph = mock(AppBuilderFlowGraph.class);

        when(mockAppVersion.isPublished()).thenReturn(false);
        when(mockAppVersion.getFlowGraph()).thenReturn(mockFlowGraph);
        when(mockFlowGraph.getAppearance()).thenReturn("{\"pages\":\"not-an-array\"}");

        when(appVersionService.getLatestCreatedByAppSuiteId(appSuiteId)).thenReturn(Optional.of(mockAppVersion));
        AippException exception = assertThrows(AippException.class, () ->
                appBuilderAppService.updateNodeConfigs(nodeConfigs, context));
        assertEquals(AippErrCode.NODE_CONFIG_UPDATE_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("节点配置更新失败，失败原因：Pages is not list"));
    }

    /**
     * 测试 case: page配置不是Map类型时应抛出 AippException
     */
    @Test
    void testUpdateNodeConfigs_WhenPageIsNotMap_ShouldThrowAippException() {
        String appSuiteId = "test-suite-id";
        Map<String, Object> nodeConfigsMap = new HashMap<>();
        nodeConfigsMap.put("nodeKey",
                Collections.singletonMap("valueParams", Collections.singletonMap("key", "newValue")));

        AppBuilderNodeConfigsDto nodeConfigs = new AppBuilderNodeConfigsDto(appSuiteId, nodeConfigsMap);
        OperationContext context = new OperationContext();

        AppVersion mockAppVersion = mock(AppVersion.class);
        AppBuilderFlowGraph mockFlowGraph = mock(AppBuilderFlowGraph.class);

        when(mockAppVersion.isPublished()).thenReturn(false);
        when(mockAppVersion.getFlowGraph()).thenReturn(mockFlowGraph);
        when(mockFlowGraph.getAppearance()).thenReturn(
                "{\"pages\":[\"not-a-map\"]}");

        when(appVersionService.getLatestCreatedByAppSuiteId(appSuiteId)).thenReturn(Optional.of(mockAppVersion));

        AippException exception = assertThrows(AippException.class, () ->
                appBuilderAppService.updateNodeConfigs(nodeConfigs, context));

        assertEquals(AippErrCode.NODE_CONFIG_UPDATE_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("The first element of pages is not of map type"));
    }

    /**
     * 测试 case: shapes字段不是列表类型时应抛出 AippException
     */
    @Test
    void testUpdateNodeConfigs_WhenShapesIsNotList_ShouldThrowAippException() {
        String appSuiteId = "test-suite-id";
        Map<String, Object> nodeConfigsMap = new HashMap<>();
        nodeConfigsMap.put("nodeKey",
                Collections.singletonMap("valueParams", Collections.singletonMap("key", "newValue")));

        AppBuilderNodeConfigsDto nodeConfigs = new AppBuilderNodeConfigsDto(appSuiteId, nodeConfigsMap);
        OperationContext context = new OperationContext();

        AppVersion mockAppVersion = mock(AppVersion.class);
        AppBuilderFlowGraph mockFlowGraph = mock(AppBuilderFlowGraph.class);

        when(mockAppVersion.isPublished()).thenReturn(false);
        when(mockAppVersion.getFlowGraph()).thenReturn(mockFlowGraph);
        when(mockFlowGraph.getAppearance()).thenReturn(
                "{\"pages\":[{\"shapes\":\"not-a-list\"}]}");

        when(appVersionService.getLatestCreatedByAppSuiteId(appSuiteId)).thenReturn(Optional.of(mockAppVersion));

        AippException exception = assertThrows(AippException.class, () ->
                appBuilderAppService.updateNodeConfigs(nodeConfigs, context));

        assertEquals(AippErrCode.NODE_CONFIG_UPDATE_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("Shapes is not an list type"));
    }

    /**
     * 测试 case: flowMeta字段不是Map类型时应抛出 AippException
     */
    @Test
    void testUpdateNodeConfigs_WhenFlowMetaIsNotMap_ShouldThrowAippException() {
        String appSuiteId = "test-suite-id";
        Map<String, Object> nodeConfigsMap = new HashMap<>();
        nodeConfigsMap.put("nodeKey",
                Collections.singletonMap("valueParams", Collections.singletonMap("key", "newValue")));

        AppBuilderNodeConfigsDto nodeConfigs = new AppBuilderNodeConfigsDto(appSuiteId, nodeConfigsMap);
        OperationContext context = new OperationContext();

        AppVersion mockAppVersion = mock(AppVersion.class);
        AppBuilderFlowGraph mockFlowGraph = mock(AppBuilderFlowGraph.class);

        when(mockAppVersion.isPublished()).thenReturn(false);
        when(mockAppVersion.getFlowGraph()).thenReturn(mockFlowGraph);
        when(mockFlowGraph.getAppearance()).thenReturn(
                "{\"pages\":[{\"shapes\":[{\"id\":\"nodeKey\",\"flowMeta\":\"not-a-map\"}]}]}");

        when(appVersionService.getLatestCreatedByAppSuiteId(appSuiteId)).thenReturn(Optional.of(mockAppVersion));

        AippException exception = assertThrows(AippException.class, () ->
                appBuilderAppService.updateNodeConfigs(nodeConfigs, context));

        assertEquals(AippErrCode.NODE_CONFIG_UPDATE_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("FlowMeta is not map"));
    }

    /**
     * 测试 case: converter字段不是Map类型时应抛出 AippException
     */
    @Test
    void testUpdateNodeConfigs_WhenConverterIsNotMap_ShouldThrowAippException() {
        String appSuiteId = "test-suite-id";
        Map<String, Object> nodeConfigsMap = new HashMap<>();
        nodeConfigsMap.put("nodeKey",
                Collections.singletonMap("valueParams", Collections.singletonMap("key", "newValue")));

        AppBuilderNodeConfigsDto nodeConfigs = new AppBuilderNodeConfigsDto(appSuiteId, nodeConfigsMap);
        OperationContext context = new OperationContext();

        AppVersion mockAppVersion = mock(AppVersion.class);
        AppBuilderFlowGraph mockFlowGraph = mock(AppBuilderFlowGraph.class);

        when(mockAppVersion.isPublished()).thenReturn(false);
        when(mockAppVersion.getFlowGraph()).thenReturn(mockFlowGraph);
        when(mockFlowGraph.getAppearance()).thenReturn(
                "{\"pages\":[{\"shapes\":[{\"id\":\"nodeKey\"," +
                        "\"flowMeta\":{\"jober\":{\"converter\":\"not-a-map\"}}}]}]}");

        when(appVersionService.getLatestCreatedByAppSuiteId(appSuiteId)).thenReturn(Optional.of(mockAppVersion));

        AippException exception = assertThrows(AippException.class, () ->
                appBuilderAppService.updateNodeConfigs(nodeConfigs, context));

        assertEquals(AippErrCode.NODE_CONFIG_UPDATE_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("Converter is not map"));
    }

    /**
     * 测试 case: entity字段不是Map类型时应抛出 AippException
     */
    @Test
    void testUpdateNodeConfigs_WhenEntityIsNotMap_ShouldThrowAippException() {
        String appSuiteId = "test-suite-id";
        Map<String, Object> nodeConfigsMap = new HashMap<>();
        nodeConfigsMap.put("nodeKey",
                Collections.singletonMap("valueParams", Collections.singletonMap("key", "newValue")));

        AppBuilderNodeConfigsDto nodeConfigs = new AppBuilderNodeConfigsDto(appSuiteId, nodeConfigsMap);
        OperationContext context = new OperationContext();

        AppVersion mockAppVersion = mock(AppVersion.class);
        AppBuilderFlowGraph mockFlowGraph = mock(AppBuilderFlowGraph.class);

        when(mockAppVersion.isPublished()).thenReturn(false);
        when(mockAppVersion.getFlowGraph()).thenReturn(mockFlowGraph);
        when(mockFlowGraph.getAppearance()).thenReturn(
                "{\"pages\":[{\"shapes\":[{\"id\":\"nodeKey\"," +
                        "\"flowMeta\":{\"jober\":{\"converter\":{\"entity\":\"not-a-map\"}}}}]}]}");

        when(appVersionService.getLatestCreatedByAppSuiteId(appSuiteId)).thenReturn(Optional.of(mockAppVersion));

        AippException exception = assertThrows(AippException.class, () ->
                appBuilderAppService.updateNodeConfigs(nodeConfigs, context));

        assertEquals(AippErrCode.NODE_CONFIG_UPDATE_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("entity is not map"));
    }

    /**
     * 测试 case: JSON 解析失败应捕获并转换为 AippJsonDecodeException
     */
    @Test
    void testUpdateNodeConfigs_WhenJsonParseFails_ShouldThrowAippJsonDecodeException() {
        String appSuiteId = "test-suite-id";
        Map<String, Object> nodeConfigsMap = new HashMap<>();
        nodeConfigsMap.put("nodeKey", Collections.singletonMap("valueParams", Collections.singletonList(
                Collections.singletonMap("key", "newValue"))));

        AppBuilderNodeConfigsDto nodeConfigs = new AppBuilderNodeConfigsDto(appSuiteId, nodeConfigsMap);
        OperationContext context = new OperationContext();

        AppVersion mockAppVersion = mock(AppVersion.class);
        AppBuilderFlowGraph mockFlowGraph = mock(AppBuilderFlowGraph.class);

        when(mockAppVersion.isPublished()).thenReturn(false);
        when(mockAppVersion.getFlowGraph()).thenReturn(mockFlowGraph);
        when(mockFlowGraph.getAppearance()).thenReturn("{invalid-json}");

        when(appVersionService.getLatestCreatedByAppSuiteId(appSuiteId)).thenReturn(Optional.of(mockAppVersion));

        assertThrows(AippJsonDecodeException.class, () ->
                appBuilderAppService.updateNodeConfigs(nodeConfigs, context));
    }

    /**
     * 为 {@link AppBuilderAppServiceImpl#updateFlow(String, OperationContext)} 提供测试
     */
    @Nested
    @DisplayName("测试根据是否更新debug")
    class TestUpdateFlow {
        @Test
        @DisplayName("测试需要更新")
        public void testTrue() {
            AppVersion appVersion = mock(AppVersion.class);
            when(appVersion.getData()).thenReturn(new AppBuilderAppPo());
            when(appVersionService.retrieval("hello")).thenReturn(appVersion);
            when(appVersion.isUpdated()).thenReturn(true);
            AppBuilderAppDto appDto = AppBuilderAppDto.builder().attributes(new HashMap<>()).build();
            AippDto aippDto = AippDto.builder().flowViewData(new HashMap<>()).build();
            mockConvertUtils.when(() -> ConvertUtils.convertToAippDtoFromAppBuilderAppDto(appDto)).thenReturn(aippDto);

            appBuilderAppService.updateFlow("hello", new OperationContext());

            verify(appVersion, times(1)).preview(any(), any(), any());
            verify(appVersionService, times(1)).update(appVersion);
        }
    }

    @Test
    @DisplayName("更新 config")
    void testUpdateConfig() {
        AppBuilderAppPo appPo = AppBuilderAppPo.builder()
            .name("oldName")
            .status(AppState.PUBLISHED.getName())
            .build();
        AppVersion appVersion = spy(mockAppVersion(appPo));
        when(appVersionService.retrieval(anyString())).thenReturn(appVersion);
        doNothing().when(appVersion).updateConfig(any(), any(), any());
        doNothing().when(appVersion).updateGraph(any(), any());
        OperationContext context = new OperationContext();
        context.setOperator("tester");

        this.appBuilderAppService.updateConfig("testId", new AppBuilderConfigDto(), new ArrayList<>(), context);

        verify(appVersionService, times(1)).update(appVersion);
        Assertions.assertEquals(appVersion.getData().getUpdateBy(), "tester");
    }

    @Test
    @DisplayName("测试删除app成功")
    public void testDeleteApp() {
        OperationContext context = new OperationContext();
        String appId = "testId";

        this.appBuilderAppService.delete(appId, context);

        verify(appDomainService, times(1)).deleteByAppId(appId, context);
    }

    @Test
    @DisplayName("测试查询单个app成功")
    public void testQuerySucceed() {
        AppVersion appVersion = mock(AppVersion.class);
        when(appVersionService.getByAppId(anyString())).thenReturn(Optional.of(appVersion));
        when(appVersion.getData()).thenReturn(AppBuilderAppPo.builder().appSuiteId("id1").build());
        when(appVersionService.getFirstCreatedByAppSuiteId(anyString())).thenReturn(Optional.of(appVersion));
        when(converterFactory.convert(any(), any())).thenReturn(AppBuilderAppDto.builder().aippId("id1").build());
        assertDoesNotThrow(() -> appBuilderAppService.query("testId", new OperationContext()));
    }

    @Test
    @DisplayName("测试查询单个app失败，抛出对应异常")
    public void testQueryFailed() {
        when(appVersionService.getByAppId(anyString())).thenReturn(Optional.empty());

        AippException exception = Assertions.assertThrows(AippException.class,
                () -> appBuilderAppService.query("testId", new OperationContext()));

        Assertions.assertEquals(AippErrCode.APP_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试查询最新编排流程图")
    void testQueryByPathValidPath() {
        String validPath = "YGHmQFJE5ZaFW4wl";
        AppVersion appVersion = mock(AppVersion.class);
        when(appVersionService.getByPath(anyString())).thenReturn(Optional.of(appVersion));
        when(converterFactory.convert(any(), any())).thenReturn(AppBuilderAppDto.builder().build());
        assertDoesNotThrow(() -> appBuilderAppService.queryByPath(validPath));
    }

    @Test
    @DisplayName("测试查询无效路径")
    void testQueryByPathInvalidPath() {
        String invalidPath = "invalidPath";
        AippException exception = assertThrows(AippException.class, () -> {
            appBuilderAppService.queryByPath(invalidPath);
        });
        assertEquals("路径格式无效", exception.getMessage());
    }

    @Test
    @DisplayName("测试查询app最新发布版本")
    void testQueryLatestPublish() {
        String appId = "appId";
        String suiteId = "suiteId";
        String version = "1.0.0";
        AppBuilderAppPo appPo = AppBuilderAppPo.builder().appSuiteId(suiteId).appId(appId).build();
        AppVersion appVersion = spy(mockAppVersion(appPo));
        AppTask appTask = AppTask.asEntity().setVersion(version).setAppId(appId).build();
        when(appVersionService.retrieval(any())).thenReturn(appVersion);
        doReturn(appTask).when(appVersion).getLatestPublishedTask(any());

        AippCreate aippCreate = this.appBuilderAppService.queryLatestPublished(appId, new OperationContext());

        Assertions.assertEquals(version, aippCreate.getVersion());
        Assertions.assertEquals(suiteId, aippCreate.getAippId());
        Assertions.assertEquals(appId, aippCreate.getAppId());
    }

    @Nested
    @DisplayName("更新app测试")
    class TestUpgrade {
        @Test
        @DisplayName("测试Dto为空时更新app失败")
        void testUpdateWhenDtoIsNull() {
            AippException exception = assertThrows(AippException.class,
                    () -> appBuilderAppService.updateApp("appId", null, new OperationContext()));
            Assertions.assertEquals(AippErrCode.INVALID_OPERATION.getErrorCode(), exception.getCode());
        }

        @Test
        @DisplayName("测试Dto不为空时更新成功")
        void testUpdateAppWhenAppChanges() throws AippTaskNotFoundException {
            String appId = "45698235b3d24209aefd59eb7d1c3322";
            AppBuilderAppDto appDto = AppBuilderAppDto.builder().name("newName").build();
            OperationContext context = new OperationContext();
            when(appVersionService.update(anyString(), any(AppBuilderAppDto.class),
                    any(OperationContext.class))).thenReturn(mock(AppVersion.class));
            appBuilderAppService.updateApp(appId, appDto, context);
            verify(appVersionService, times(1)).update(appId, appDto, context);
        }
    }

    @Test
    @DisplayName("测试保存应用配置")
    void testSaveConfig() {
        String appId = "45698235b3d24209aefd59eb7d1c3322";
        AppBuilderSaveConfigDto appBuilderSaveConfigDto = new AppBuilderSaveConfigDto();
        OperationContext context = new OperationContext();
        AppVersion appVersion = mock(AppVersion.class);
        when(appVersionService.update(anyString(), any(AppBuilderSaveConfigDto.class),
                any(OperationContext.class))).thenReturn(appVersion);

        this.appBuilderAppService.saveConfig(appId, appBuilderSaveConfigDto, context);

        verify(appVersionService, times(1)).update(appId, appBuilderSaveConfigDto, context);
    }

    @Test
    @DisplayName("校验模型节点配置")
    void testModelNode() {
        String testNode = "{\"type\":\"knowledgeRetrievalNodeState\",\"nodeInfos\":[{\"nodeId\":\"jadenthrcv\","
                + "\"nodeName\":\"知识检索\",\"configs\":[{\"id\":14,\"configName\":\"knowledgeRepos\",\"name\":\"k14\","
                + "\"description\":\"\",\"type\":\"VECTOR\",\"createdAt\":\"2024-12-02 12:41:14\",\"checked\":true},"
                + "{\"id\":2,\"configName\":\"knowledgeRepos\",\"name\":\"k2\",\"description\":\"\","
                + "\"type\":\"VECTOR\"," + "\"createdAt\":\"2024-12-02 12:37:17\",\"checked\":true}]}]}";
        AppCheckDto appCheckDto = JsonUtils.parseObject(testNode, AppCheckDto.class);
        try (MockedStatic<CheckerFactory> mockedStatic = mockStatic(CheckerFactory.class)) {
            mockedStatic.when(() -> CheckerFactory.getChecker(anyString())).thenReturn(new RetrievalNodeChecker());
            List<CheckResult> results =
                    this.appBuilderAppService.checkAvailable(Collections.singletonList(appCheckDto), null);
            Assertions.assertTrue(results.isEmpty());
        }
    }

    @Test
    @DisplayName("测试获取应用列表")
    void testListApplication() {
        String tenantId = "tenantId";
        long offset = 0L;
        int limit = 10;
        AppQueryCondition condition = new AppQueryCondition();

        AppBuilderAppPo appPo = new AppBuilderAppPo();
        RangedResultSet<AppVersion> mockResultSet = RangedResultSet.create(
            Collections.singletonList(mockAppVersion(appPo)), 0, 10, 1L);
        when(appVersionService.pageListByTenantId(condition, tenantId, offset, limit)).thenReturn(mockResultSet);
        when(converterFactory.convert(any(), any())).thenReturn(AppBuilderAppMetadataDto.builder().build());
        when(this.domainDivisionService.getUserGroupId()).thenReturn("g1");

        OperationContext context = new OperationContext();
        context.setTenantId(tenantId);
        RangedResultSet<AppBuilderAppMetadataDto> resultSet =
                this.appBuilderAppService.list(condition, context, offset, limit).getData();

        assertEquals(1, resultSet.getResults().size());
        assertEquals(offset, resultSet.getRange().getOffset());
        assertEquals(limit, resultSet.getRange().getLimit());
        assertEquals(1, resultSet.getRange().getTotal());
        verify(appVersionService, times(1)).pageListByTenantId(condition, tenantId, offset, limit);
    }

    @Test
    @DisplayName("测试更新访客模式")
    void testUpdateGuestMode() {
        this.appBuilderAppService.updateGuestMode("path", true);
        verify(this.appBuilderAppRepository, times(1)).updateGuestMode(anyString(), anyBoolean());
    }

    /**
     * mock 应用版本对象.
     *
     * @param appPo 数据对象.
     * @return {@link AppVersion} 应用版本对象.
     */
    public static AppVersion mockAppVersion(AppBuilderAppPo appPo) {
        AppVersionFactory appVersionFactory = new AppVersionFactory(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                20000,
                300,
                null,
                "/var/share",
                null, "/chat/%s");
        if (StringUtils.isBlank(appPo.getConfigId())) {
            appPo.setConfigId("defaultConfigId");
        }
        return appVersionFactory.create(appPo, null);
    }
}
