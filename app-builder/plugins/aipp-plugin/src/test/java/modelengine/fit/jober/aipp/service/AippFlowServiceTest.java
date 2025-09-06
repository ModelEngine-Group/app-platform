/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static modelengine.fit.jober.common.ErrorCodes.FLOW_GRAPH_SAVE_ERROR;
import static modelengine.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jober.aipp.common.PageResponse;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippForbiddenException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.condition.AippQueryCondition;
import modelengine.fit.jober.aipp.condition.PaginationCondition;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDetailDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AippOverviewRspDto;
import modelengine.fit.jober.aipp.enums.AippMetaStatusEnum;
import modelengine.fit.jober.aipp.service.impl.AippFlowServiceImpl;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.jober.common.exceptions.JobberParamException;
import modelengine.fit.jober.entity.task.TaskProperty;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AippFlowServiceTest {
    private static final String DUMMY_FLOW_CONFIG_ID = "testFlowConfigId";

    private static final String DUMMY_FLOW_CONFIG_VERSION = "1.0.0";

    private static final String DUMMY_META_VERSION_NEW = "1.0.1";

    private static final String DUMMY_META_VERSION_OLD = "1.0.0";

    @InjectMocks
    private AippFlowServiceImpl aippFlowServiceImpl;

    @Mock
    private FlowsService flowsServiceMock;

    @Mock
    private AppTaskService appTaskService;

    @BeforeEach
    void setUp() {
        // setup
    }

    Meta GenTestMeta() {
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime modifyTime = LocalDateTime.now();
        Meta expectMeta = new Meta();
        expectMeta.setName("testName");
        expectMeta.setId("testId");
        expectMeta.setCreator("testUser");
        expectMeta.setCreationTime(createTime);
        expectMeta.setLastModificationTime(modifyTime);
        expectMeta.setVersion(DUMMY_META_VERSION_OLD);

        Map<String, Object> attribute = new HashMap<>();
        attribute.put(AippConst.ATTR_FLOW_CONFIG_ID_KEY, DUMMY_FLOW_CONFIG_ID);
        attribute.put(AippConst.ATTR_VERSION_KEY, DUMMY_FLOW_CONFIG_VERSION);
        attribute.put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode());
        attribute.put(AippConst.ATTR_APP_ID_KEY, "appId1");
        expectMeta.setAttributes(attribute);
        return expectMeta;
    }

    Meta GenerateInactiveMeta() {
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime modifyTime = LocalDateTime.now();
        Meta expectMeta = new Meta();
        expectMeta.setName("testName");
        expectMeta.setId("testId");
        expectMeta.setVersion(DUMMY_META_VERSION_NEW);
        expectMeta.setCreator("testUser");
        expectMeta.setCreationTime(createTime);
        expectMeta.setLastModificationTime(modifyTime);

        Map<String, Object> attribute = new HashMap<>();
        attribute.put(AippConst.ATTR_FLOW_CONFIG_ID_KEY, DUMMY_FLOW_CONFIG_ID);
        attribute.put(AippConst.ATTR_BASELINE_VERSION_KEY, DUMMY_META_VERSION_OLD);
        attribute.put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode());
        expectMeta.setAttributes(attribute);
        return expectMeta;
    }

    OperationContext GenTestOperationContext() {
        OperationContext context = new OperationContext();
        context.setTenantId("testTenantId");
        context.setOperator("testOperator");
        return context;
    }

    @Test
    @Disabled
    void testQueryAippDetailThenOk() {
        final String defaultVersion = "1.0.0";
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowId(DUMMY_FLOW_CONFIG_ID);
        flowInfo.setVersion(defaultVersion);
        flowInfo.setConfigData("{\"id\": \"testFlowConfigId\"}");
        flowInfo.setFlowDefinitionId("testFlowDefinitionId");

        when(this.appTaskService.getLatest(any(), any(), any())).thenReturn(Optional.of(AppTask.asEntity()
                .setAppSuiteId("testAippId")
                .setName("testMeta")
                .setCreator("testUser")
                .setFlowConfigId(DUMMY_FLOW_CONFIG_ID)
                .setVersion(defaultVersion)
                .setStatus(AippMetaStatusEnum.INACTIVE.getCode())
                .build()));

        when(flowsServiceMock.getFlows(anyString(), anyString(), any(OperationContext.class))).thenReturn(flowInfo);

        Rsp<AippDetailDto> rsp = aippFlowServiceImpl.queryAippDetail("testAippId", defaultVersion,
                GenTestOperationContext());

        Assertions.assertEquals(0, rsp.getCode());
        Assertions.assertTrue(rsp.getData().getFlowViewData().containsKey("id"));
        Assertions.assertEquals(DUMMY_FLOW_CONFIG_ID, rsp.getData().getFlowViewData().get("id"));
    }

    @Test
    void testQueryAippDetailWithGetFlowsErrorThenFail() {
        final String defaultVersion = "1.0.0";
        when(this.appTaskService.getLatest(any(), any(), any())).thenReturn(Optional.of(AppTask.asEntity()
                .setAppSuiteId("testAippId")
                .setName("testMeta")
                .setCreator("testUser")
                .setFlowConfigId(DUMMY_FLOW_CONFIG_ID)
                .setVersion(defaultVersion)
                .setStatus(AippMetaStatusEnum.INACTIVE.getCode())
                .build()));

        doThrow(new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId")).when(flowsServiceMock)
                .getFlows(eq(DUMMY_FLOW_CONFIG_ID), eq(defaultVersion), any(OperationContext.class));

        Assertions.assertThrows(AippException.class, () -> {
            this.aippFlowServiceImpl.queryAippDetail("testAippId", defaultVersion, GenTestOperationContext());
        });
    }

    @Test
    @Disabled
    void shouldSetDraftVersionWhenCallListAippWithInactiveAipp() {
        Meta expectMeta = GenerateInactiveMeta();

        PageResponse<AippOverviewRspDto> rsp = aippFlowServiceImpl.listAipp(
                AippQueryCondition.builder().name("testName").build(),
                PaginationCondition.builder().pageNum(1).pageSize(10).build(), GenTestOperationContext());

        Assertions.assertEquals(1, rsp.getTotal());
        List<AippOverviewRspDto> data = rsp.getItems();
        Assertions.assertEquals(1, data.size());
        Assertions.assertEquals(expectMeta.getName(), data.get(0).getName());
        Assertions.assertEquals(expectMeta.getId(), data.get(0).getAippId());
        Assertions.assertEquals(expectMeta.getCreator(), data.get(0).getCreator());
        Assertions.assertEquals(expectMeta.getCreationTime(), data.get(0).getCreatedAt());
        Assertions.assertEquals(expectMeta.getLastModificationTime(), data.get(0).getUpdatedAt());
        Assertions.assertNull(data.get(0).getPublishAt());
        Assertions.assertEquals(expectMeta.getVersion(), data.get(0).getDraftVersion());
        Assertions.assertEquals(expectMeta.getAttributes().get(AippConst.ATTR_BASELINE_VERSION_KEY),
                data.get(0).getVersion());
        Assertions.assertEquals(expectMeta.getAttributes().get(AippConst.ATTR_META_STATUS_KEY),
                data.get(0).getStatus());
    }

    @Test
    @Disabled
    void testCreateAippWithInvalidNameThenFail() {
        Assertions.assertThrows(AippParamException.class,
                () -> aippFlowServiceImpl.create(AippDto.builder().build(), GenTestOperationContext()));

        Assertions.assertThrows(AippParamException.class,
                () -> aippFlowServiceImpl.create(AippDto.builder().name("").build(), GenTestOperationContext()));

        Assertions.assertThrows(AippParamException.class,
                () -> aippFlowServiceImpl.create(AippDto.builder().name("  ").build(), GenTestOperationContext()));
    }

    @Test
    @Disabled
    void testCreateAippThenOk() {
        AippDto aipp = AippDto.builder().name("testAippName").build();

        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowId(DUMMY_FLOW_CONFIG_ID);
        flowInfo.setVersion(DUMMY_FLOW_CONFIG_VERSION);

        when(flowsServiceMock.createFlows(any(String.class), any(OperationContext.class))).thenReturn(flowInfo);
        when(this.appTaskService.createTask(any(), any())).thenAnswer(var -> {
            AppTask task = var.getArgument(0);
            Assertions.assertEquals(task.getEntity().getName(), aipp.getName());
            Assertions.assertEquals(task.getEntity().getFlowConfigId(), flowInfo.getFlowId());
            Assertions.assertEquals(task.getEntity().getVersion(), flowInfo.getVersion());
            Assertions.assertEquals(task.getEntity().getStatus(), AippMetaStatusEnum.INACTIVE.getCode());
            List<TaskProperty> props = task.getEntity().getProperties();
            for (int i = 0; i < props.size(); i++) {
                Assertions.assertEquals(props.get(i).getName(), AippConst.STATIC_META_ITEMS.get(i).getKey());
            }
            return task.getEntity().clone().setAppSuiteId("testMetaId").build();
        });

        AippCreateDto rsp = aippFlowServiceImpl.create(aipp, GenTestOperationContext());
        Assertions.assertEquals(rsp.getAippId(), "testMetaId");
    }

    @Test
    @Disabled
    void testUpdateAippWithInvalidConditionThenFail() {
        Meta expectMeta = GenTestMeta();
        expectMeta.getAttributes().replace(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.ACTIVE.getCode());
        String aippId = expectMeta.getId();

        // test update active aipp
        Assertions.assertThrows(AippForbiddenException.class, () -> {
            AippDto aippDto = new AippDto();
            aippDto.setId(aippId);
            aippDto.setVersion(DUMMY_META_VERSION_OLD);
            aippFlowServiceImpl.update(aippDto, GenTestOperationContext());
        });

        // test blank name
        expectMeta.getAttributes().replace(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode());
        AippDto aipp = AippDto.builder().name(" ").build();
        Assertions.assertThrows(AippParamException.class, () -> {
            AippDto aippDto = new AippDto();
            aippDto.setId(aippId);
            aippDto.setVersion(DUMMY_META_VERSION_OLD);
            aippFlowServiceImpl.update(aippDto, GenTestOperationContext());
        });

        aipp.setName("");
        Assertions.assertThrows(AippParamException.class, () -> {
            AippDto aippDto = new AippDto();
            aippDto.setId(aippId);
            aippDto.setVersion(DUMMY_META_VERSION_OLD);
            aippFlowServiceImpl.update(aipp, GenTestOperationContext());
        });

        Assertions.assertThrows(AippParamException.class, () -> {
            AippDto aippDto = new AippDto();
            aippDto.setId(aippId);
            aippDto.setVersion(DUMMY_META_VERSION_OLD);
            aippFlowServiceImpl.update(new AippDto(), GenTestOperationContext());
        });
    }

    @Test
    @Disabled
    void testUpdateAippThenOk() {
        Meta expectMeta = GenTestMeta();
        String aippId = expectMeta.getId();
        AippDto aipp = AippDto.builder().name(expectMeta.getName()).description("testDescription").build();

        aipp.setId(aippId);
        AippCreateDto rsp = aippFlowServiceImpl.update(aipp, GenTestOperationContext());
        Assertions.assertEquals(rsp.getAippId(), aippId);

        // flowData not null or empty
        Map<String, Object> flowData = new HashMap<>();
        flowData.put("testFlowKey", "testFlowData");
        aipp.setFlowViewData(flowData);
        doReturn(new FlowInfo()).when(flowsServiceMock)
                .updateFlows(eq((String) expectMeta.getAttributes().get(AippConst.ATTR_FLOW_CONFIG_ID_KEY)),
                        eq((String) expectMeta.getAttributes().get(AippConst.ATTR_VERSION_KEY)), any(),
                        any(OperationContext.class));
        rsp = this.aippFlowServiceImpl.update(aipp, GenTestOperationContext());
        Assertions.assertEquals(rsp.getAippId(), aippId);
    }

    @Test
    void testUpdateAippWithUpdateFlowsFailThenFail() {
        AippDto aipp = AippDto.builder().name("testMeta").description("testDescription").version("1.0.0").build();
        when(this.appTaskService.getLatest(any(), any(), any())).thenReturn(Optional.of(
                AppTask.asEntity()
                        .setName("testMeta")
                        .setAppSuiteId("testAippId")
                        .setCreator("testUser")
                        .setCreationTime(LocalDateTime.now())
                        .setLastModificationTime(LocalDateTime.now())
                        .setVersion(DUMMY_META_VERSION_OLD)
                        .setFlowConfigId(DUMMY_FLOW_CONFIG_ID)
                        .setAttributeVersion(DUMMY_FLOW_CONFIG_VERSION)
                        .setStatus(AippMetaStatusEnum.INACTIVE.getCode())
                        .setAppId("appId1")
                        .build()));

        doAnswer(var -> {
            AppTask task = var.getArgument(0);
            Assertions.assertEquals("testMeta", task.getEntity().getName());
            Assertions.assertEquals(aipp.getDescription(), task.getEntity().getDescription());
            return null;
        }).when(this.appTaskService).updateTask(any(), any());

        aipp.setId("testAippId");
        AippCreateDto rsp = aippFlowServiceImpl.update(aipp, GenTestOperationContext());
        Assertions.assertEquals(rsp.getAippId(), "testAippId");

        // flowData not null or empty
        Map<String, Object> flowData = new HashMap<>();
        flowData.put("testFlowKey", "testFlowData");
        aipp.setFlowViewData(flowData);
        doThrow(new JobberException(FLOW_GRAPH_SAVE_ERROR, "1", "1.0.0")).when(flowsServiceMock)
                .updateFlows(eq(DUMMY_FLOW_CONFIG_ID), eq(DUMMY_META_VERSION_OLD), any(), any(OperationContext.class));
        Assertions.assertThrows(AippException.class, () -> {
            this.aippFlowServiceImpl.update(aipp, GenTestOperationContext());
        });
    }

    @Test
    void testDeleteAippWithDeleteFlowsErrorThenFail() {
        final String defaultVersion = "1.0.0";
        when(this.appTaskService.getLatest(any(), any(), any())).thenReturn(Optional.of(
                AppTask.asEntity()
                        .setName("testMeta")
                        .setAppSuiteId("testAippId")
                        .setCreator("testUser")
                        .setCreationTime(LocalDateTime.now())
                        .setLastModificationTime(LocalDateTime.now())
                        .setVersion(defaultVersion)
                        .setFlowConfigId(DUMMY_FLOW_CONFIG_ID)
                        .setAttributeVersion(defaultVersion)
                        .setStatus(AippMetaStatusEnum.INACTIVE.getCode())
                        .setAppId("appId1")
                        .build()));

        doThrow(new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId")).when(flowsServiceMock)
                .deleteFlows(eq(DUMMY_FLOW_CONFIG_ID), eq(defaultVersion), any(OperationContext.class));

        Assertions.assertThrows(AippException.class, () -> {
            this.aippFlowServiceImpl.deleteAipp("testAippId", defaultVersion, GenTestOperationContext());
        });
    }
}
