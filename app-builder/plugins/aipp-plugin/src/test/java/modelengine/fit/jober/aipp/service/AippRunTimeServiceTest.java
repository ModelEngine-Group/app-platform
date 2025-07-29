/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.dynamicform.DynamicFormMetaService;
import modelengine.fit.dynamicform.DynamicFormService;
import modelengine.fit.dynamicform.entity.FormMetaItem;
import modelengine.fit.dynamicform.entity.FormMetaQueryParameter;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jober.aipp.TestUtils;
import modelengine.fit.jober.aipp.common.PageResponse;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippForbiddenException;
import modelengine.fit.jober.aipp.condition.AippInstanceQueryCondition;
import modelengine.fit.jober.aipp.condition.PaginationCondition;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.convertor.FormMetaConvertor;
import modelengine.fit.jober.aipp.dto.AippInstanceDto;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstSortKeyEnum;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.service.impl.AippRunTimeServiceImpl;
import modelengine.fit.jober.common.RangeResult;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.jober.entity.FlowInstanceResult;
import modelengine.fit.jober.entity.task.TaskProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fit.http.client.HttpClassicClientFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@Disabled
public class AippRunTimeServiceTest {
    private static final String DUMMY_START_FORM_ID = "test_start_form_id";
    private static final String DUMMY_START_FORM_VERSION = "test_start_form_version";
    private static final String DUMMY_AIPP_ID = "main_aipp_id";
    private static final String DUMMY_AIPP_VERSION = "main_aipp_version";
    private static final String DUMMY_META_VERSION_ID = "meta_version_id";
    private static final String DUMMY_AIPP_NAME = "test_aipp_name";
    private static final String DUMMY_CREATOR = "test_aipp_creator";
    private static final String DUMMY_INST_ID = "main_inst_id";
    private static final String DUMMY_INST_NAME = "test_inst_name";
    private static final String DUMMY_FLOW_DEF_ID = "test_flow_def_id";
    private static final String DUMMY_FLOW_TRACE_ID = "test_flow_trace_id";
    private static final String DUMMY_CURR_FORM_ID = "test_cur_form_id";
    private static final String DUMMY_CURR_FORM_VERSION_ID = "test_cur_form_id";
    private static final String DUMMY_PROPS_KEY = "test_props_key";
    private static final String DUMMY_PROPS_VALUE = "test_props_value";
    private static final String DUMMY_FLOW_INST_ID = "test_flow_inst_id";

    @InjectMocks
    private AippRunTimeServiceImpl runTimeService;

    @Mock
    private AopAippLogService aopAippLogServiceMock;
    @Mock
    private DynamicFormMetaService dynamicFormMetaServiceMock;
    @Mock
    private MetaService metaServiceMock;
    @Mock
    private DynamicFormService dynamicFormServiceMock;
    @Mock
    private MetaInstanceService metaInstanceServiceMock;
    @Mock
    private FlowInstanceService flowInstanceServiceMock;
    @Mock
    private UploadedFileManageService uploadedFileManageServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpClassicClientFactory httpClientFactoryMock;

    OperationContext genTestOpContext() {
        OperationContext context = new OperationContext();
        context.setTenantId("testTenantId");
        return context;
    }

    RangedResultSet<Instance> genTestRangedResultSet(String status) {
        Map<String, String> info = new HashMap<>();
        info.put(AippConst.INST_STATUS_KEY, status);
        info.put(AippConst.INST_CURR_FORM_ID_KEY, DUMMY_CURR_FORM_ID);
        info.put(AippConst.INST_CURR_FORM_VERSION_KEY, DUMMY_CURR_FORM_VERSION_ID);
        info.put(AippConst.INST_NAME_KEY, DUMMY_INST_NAME);
        info.put(AippConst.INST_FLOW_INST_ID_KEY, DUMMY_FLOW_INST_ID);
        Instance inst = new Instance(DUMMY_INST_ID, info, null);
        return RangedResultSet.create(Collections.singletonList(inst), new RangeResult(0, 1, 1));
    }

    @Test
    void shouldOkWhenCreateInstance() {
        Instance inst = new Instance(DUMMY_INST_ID, null, null);
        Mockito.doReturn(inst).when(metaInstanceServiceMock).createMetaInstance(any(), any(), any());

        Meta expectMeta = MetaBuilder.custom().putAttr(AippConst.ATTR_FLOW_DEF_ID_KEY, DUMMY_FLOW_DEF_ID).build();
        when(metaServiceMock.list(any(MetaFilter.class),
                eq(true),
                eq(0L),
                eq(1),
                any(OperationContext.class))).thenReturn(RangedResultSet.create(Collections.singletonList(expectMeta),
                0L,
                1,
                1L));

        Mockito.doAnswer(var -> {
            String flowDefId = var.getArgument(0);
            Assertions.assertEquals(flowDefId, DUMMY_FLOW_DEF_ID);
            return new FlowInstanceResult(DUMMY_FLOW_TRACE_ID);
        }).when(flowInstanceServiceMock).startFlow(eq(DUMMY_FLOW_DEF_ID), any(), any());

        Mockito.doAnswer(var -> {
            InstanceDeclarationInfo info = var.getArgument(2);
            Assertions.assertTrue(info.getInfo().getDefined());
            Assertions.assertTrue(info.getInfo().getValue().containsKey(AippConst.INST_FLOW_INST_ID_KEY));
            return null;
        }).when(metaInstanceServiceMock).patchMetaInstance(any(), any(), any(), any());

        Mockito.doReturn(Collections.emptyList()).when(dynamicFormMetaServiceMock).query(any());

        Map<String, Object> initContext = Collections.singletonMap(AippConst.BS_INIT_CONTEXT_KEY, new HashMap<>());
        String instId =
                runTimeService.createAippInstance(DUMMY_AIPP_ID, DUMMY_AIPP_VERSION, initContext, genTestOpContext());
        Assertions.assertEquals(DUMMY_INST_ID, instId);
        verify(uploadedFileManageServiceMock, times(1)).addFileRecord(any(), any(), any(), any());
    }

    @Test
    void shouldFailedWhenDeleteInvalidAipp() {
        Meta meta = MetaBuilder.custom().build();
        TestUtils.mockMetaListReturnSingleItem5(meta, metaServiceMock);

        RangedResultSet<Instance> res =
                RangedResultSet.create(Collections.singletonList(new Instance()), new RangeResult(0, 0, 0));
        Mockito.doReturn(res).when(metaInstanceServiceMock).list(any(), any(), eq(0L), eq(1), any());
        Assertions.assertThrows(JobberException.class,
                () -> runTimeService.deleteAippInstance(DUMMY_AIPP_ID,
                        DUMMY_AIPP_VERSION,
                        DUMMY_INST_ID,
                        genTestOpContext()));

        Instance inst = new Instance(DUMMY_INST_ID,
                Collections.singletonMap("invalid_key", MetaInstStatusEnum.ARCHIVED.name()),
                null);
        res.setResults(Collections.singletonList(inst));
        res.setRange(new RangeResult(0, 1, 1));

        Assertions.assertThrows(AippException.class,
                () -> runTimeService.deleteAippInstance(DUMMY_AIPP_ID,
                        DUMMY_AIPP_VERSION,
                        DUMMY_INST_ID,
                        genTestOpContext()));
    }

    @Test
    void shouldFailedWhenDeleteRunningAipp() {
        Meta meta = MetaBuilder.custom().build();
        TestUtils.mockMetaListReturnSingleItem5(meta, metaServiceMock);
        RangedResultSet<Instance> res = genTestRangedResultSet(MetaInstStatusEnum.RUNNING.name());
        Mockito.doReturn(res).when(metaInstanceServiceMock).list(any(), any(), eq(0L), eq(1), any());

        AippForbiddenException exception = Assertions.assertThrows(AippForbiddenException.class,
                () -> runTimeService.deleteAippInstance(DUMMY_AIPP_ID,
                        DUMMY_AIPP_VERSION,
                        DUMMY_INST_ID,
                        genTestOpContext()));
        Assertions.assertEquals(AippErrCode.DELETE_INSTANCE_FORBIDDEN.getErrorCode(), exception.getCode());
    }

    @Test
    void shouldOkWhenDeleteArchivedAipp() {
        Meta meta = MetaBuilder.custom().build();
        TestUtils.mockMetaListReturnSingleItem5(meta, metaServiceMock);
        RangedResultSet<Instance> res = genTestRangedResultSet(MetaInstStatusEnum.ARCHIVED.name());
        Mockito.doReturn(res).when(metaInstanceServiceMock).list(any(), any(), eq(0L), eq(1), any());

        runTimeService.deleteAippInstance(DUMMY_AIPP_ID, DUMMY_AIPP_VERSION, DUMMY_INST_ID, genTestOpContext());
        verify(metaInstanceServiceMock, times(1)).deleteMetaInstance(any(), any(), any());
    }

    @Test
    void shouldOkWhenResumeInstance() {
        Meta meta = MetaBuilder.custom()
                .name(DUMMY_AIPP_NAME)
                .putAttr(AippConst.ATTR_FLOW_DEF_ID_KEY, DUMMY_FLOW_DEF_ID)
                .addProps(DUMMY_PROPS_KEY, DUMMY_PROPS_VALUE, "TEXT")
                .build();
        TestUtils.mockMetaListReturnSingleItem5(meta, metaServiceMock);

        Mockito.doNothing()
                .when(metaInstanceServiceMock)
                .patchMetaInstance(any(),
                        eq(DUMMY_INST_ID),
                        argThat(info -> info.getInfo().getValue().containsKey(DUMMY_PROPS_KEY)),
                        any());

        RangedResultSet<Instance> res = genTestRangedResultSet(MetaInstStatusEnum.ARCHIVED.name());
        Mockito.doReturn(res).when(metaInstanceServiceMock).list(any(), any(), eq(0L), eq(1), any());
        Mockito.doNothing()
                .when(flowInstanceServiceMock)
                .resumeFlow(eq(DUMMY_FLOW_DEF_ID), eq(DUMMY_FLOW_INST_ID), any(), any());

        Mockito.doNothing()
                .when(aopAippLogServiceMock)
                .insertLog(argThat(dto -> AippInstLogType.FORM.name().equals(dto.getLogType())));
        Mockito.doReturn(Collections.emptyList()).when(dynamicFormMetaServiceMock).query(any());

        Map<String, Object> businessData = new HashMap<String, Object>() {
            {
                put(DUMMY_PROPS_KEY, DUMMY_PROPS_VALUE);
            }
        };
        Map<String, Object> formArgs = Collections.singletonMap(AippConst.BS_DATA_KEY, businessData);
        Assertions.assertDoesNotThrow(() ->
                runTimeService.resumeAndUpdateAippInstance(DUMMY_INST_ID, formArgs, 1L, genTestOpContext(), true));
    }

    @Test
    void shouldOkWhenTerminateInstance() {
        Meta meta = MetaBuilder.custom().build();
        TestUtils.mockMetaListReturnSingleItem6(meta, metaServiceMock);

        Mockito.doNothing()
                .when(metaInstanceServiceMock)
                .patchMetaInstance(eq(DUMMY_META_VERSION_ID),
                        eq(DUMMY_INST_ID),
                        argThat(info -> info.getInfo().getValue().containsKey(AippConst.INST_STATUS_KEY)),
                        any());

        RangedResultSet<Instance> res = genTestRangedResultSet(MetaInstStatusEnum.RUNNING.name());
        Mockito.doReturn(res).when(metaInstanceServiceMock).list(any(), any(), eq(0L), eq(1), any());

        Mockito.doNothing()
                .when(aopAippLogServiceMock)
                .insertLog(argThat(dto -> AippInstLogType.MSG.name().equals(dto.getLogType())));

        Map<String, Object> msgArgs = new HashMap<>();
        runTimeService.terminateInstance(DUMMY_INST_ID, msgArgs, genTestOpContext());
        verify(flowInstanceServiceMock, times(1)).terminateFlows(any(), eq(DUMMY_FLOW_INST_ID), any(), any());
    }

    @Test
    void shouldThrowExceptionWhenTerminateFlowsFailed() {
        Meta meta = MetaBuilder.custom().build();

        RangedResultSet<Instance> res = genTestRangedResultSet(MetaInstStatusEnum.RUNNING.name());
        Mockito.doReturn(res).when(metaInstanceServiceMock).list(any(), any(), eq(0L), eq(1), any());

        Mockito.doReturn(meta).when(metaServiceMock).retrieve(any(), any());

        Mockito.doThrow(JobberException.class)
                .when(flowInstanceServiceMock)
                .terminateFlows(any(), any(), any(), any());

        Map<String, Object> msgArgs = new HashMap<>();
        Assertions.assertThrows(AippException.class, () -> {
            runTimeService.terminateInstance(DUMMY_INST_ID, msgArgs, genTestOpContext());
        });
    }

    @NoArgsConstructor
    @Data
    private static class MetaBuilder {
        private String id = DUMMY_AIPP_ID;
        private String name;
        private String category;
        private List<TaskProperty> properties = new ArrayList<>();
        private String creator;
        private LocalDateTime creationTime;
        private String lastModifier;
        private LocalDateTime lastModificationTime;
        private String tenant;
        private Map<String, Object> attributes = new HashMap<>();
        private String version = DUMMY_AIPP_VERSION;
        private String versionId = DUMMY_META_VERSION_ID;

        public static MetaBuilder custom() {
            return new MetaBuilder();
        }

        public MetaBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MetaBuilder putAttr(String key, Object value) {
            this.attributes.put(key, value);
            return this;
        }

        public MetaBuilder addProps(String key, String name, String type) {
            FormMetaItem item = new FormMetaItem(key, name, type, AippConst.STRING_LEN, null);
            properties.add(FormMetaConvertor.INSTANCE.toTaskProperty(item));
            return this;
        }

        public Meta build() {
            return new Meta(id,
                    name,
                    category,
                    properties,
                    creator,
                    creationTime,
                    lastModifier,
                    lastModificationTime,
                    tenant,
                    attributes,
                    version,
                    versionId);
        }
    }
}
