/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_GRAPH_DATA_PARSE_FAILED;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_VALIDATE_ERROR;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;
import modelengine.fit.waterflow.entity.OperationContext;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.waterflow.DatabaseBaseTest;
import modelengine.fit.waterflow.FlowsDataBaseTest;
import modelengine.fit.waterflow.MethodNameLoggerExtension;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.DefaultFlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.DefaultFlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.QueryFlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowDefinitionStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.FlowValidator;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FitableUsageMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowDefinitionMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowTraceMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * FlowsService对应测试类
 *
 * @author 杨祥宇
 * @since 2023/9/4
 */
@ExtendWith(MethodNameLoggerExtension.class)
class FlowsServiceTest extends DatabaseBaseTest {
    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowDefinition/cleanData.sql");
    }

    @Nested
    @DisplayName("测试流程定义service")
    class FlowServiceTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/services/";

        private final String sqlPath = "handler/flowDefinition/saveData.sql";

        private final FlowParser parser = new FlowParser(null);

        private final FlowValidator flowValidator = new FlowValidator(new ArrayList<>());

        private DefaultFlowDefinitionRepo flowDefinitionRepo;

        private FlowContextPersistRepo flowContextPersistRepo;

        private QueryFlowDefinitionRepo queryFlowDefinitionRepo;

        private FlowDefinitionService flowsService;

        @Mock
        private FitableUsageMapper fitableUsageMapper;

        @BeforeEach
        void setUp() {
            FlowDefinitionMapper flowDefinitionMapper = sqlSessionManager.openSession(true)
                    .getMapper(FlowDefinitionMapper.class);
            FlowContextMapper flowContextMapper = sqlSessionManager.openSession(true)
                    .getMapper(FlowContextMapper.class);
            FlowTraceMapper flowTraceMapper = sqlSessionManager.openSession(true).getMapper(FlowTraceMapper.class);
            FlowTraceRepo flowTraceRepo = new DefaultFlowTraceRepo(flowTraceMapper);
            FlowRetryMapper flowRetryMapper = sqlSessionManager.openSession(true).getMapper(FlowRetryMapper.class);
            FlowRetryRepo flowRetryRepo = new DefaultFlowRetryRepo(flowRetryMapper);
            flowDefinitionRepo = new DefaultFlowDefinitionRepo(flowDefinitionMapper, fitableUsageMapper, parser);
            Integer defaultLimitation = 5;
            boolean useLimit = false;
            flowContextPersistRepo = new FlowContextPersistRepo(flowContextMapper, flowTraceRepo, flowRetryRepo, null,
                    defaultLimitation, useLimit, 1);
            queryFlowDefinitionRepo = new QueryFlowDefinitionRepo(flowDefinitionMapper);
            flowsService = new FlowsDefinitionServiceImpl(parser, flowValidator, flowDefinitionRepo, flowContextPersistRepo,
                    queryFlowDefinitionRepo);
        }

        @Test
        @DisplayName("测试flows service创建流程定义成功")
        public void testFlowsServiceCreateTestSuccess() {
            String jsonData = getJsonData(getFilePath("flows_create_with_state_node_and_status_active.json"));
            OperationContext operationContext = getOperationContext();

            FlowDefinitionResult flows = flowsService.createFlows(jsonData, operationContext);

            Assertions.assertEquals("创建流程定义时状态为active", flows.getName());
        }

        @Test
        @DisplayName("测试flows service创建流程解析失败")
        public void testFlowsServiceCreateParseFailed() {
            String jsonData = getJsonData(getFilePath("flows_create_with_state_node_and_status_active.json"));
            OperationContext operationContext = getOperationContext();
            FlowParser flowParser = Mockito.mock(FlowParser.class);
            when(flowParser.parse(anyString())).thenThrow(new WaterflowException(FLOW_GRAPH_DATA_PARSE_FAILED));
            flowsService = new FlowsDefinitionServiceImpl(flowParser, flowValidator, flowDefinitionRepo, flowContextPersistRepo,
                    queryFlowDefinitionRepo);
            WaterflowException ex = assertThrows(WaterflowException.class,
                    () -> flowsService.createFlows(jsonData, operationContext));

            Assertions.assertEquals(FLOW_GRAPH_DATA_PARSE_FAILED.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("测试flows service创建流程校验失败")
        public void testFlowsServiceCreateValidFailed() {
            String jsonData = getJsonData(getFilePath("flows_create_with_state_node_and_status_active.json"));
            OperationContext operationContext = getOperationContext();
            FlowValidator validator = Mockito.mock(FlowValidator.class);
            doThrow(new WaterflowException(FLOW_VALIDATE_ERROR)).when(validator).validate(any(FlowDefinition.class));
            flowsService = new FlowsDefinitionServiceImpl(parser, validator, flowDefinitionRepo, flowContextPersistRepo,
                    queryFlowDefinitionRepo);
            WaterflowException ex = assertThrows(WaterflowException.class,
                    () -> flowsService.createFlows(jsonData, operationContext));

            Assertions.assertEquals(FLOW_VALIDATE_ERROR.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("测试flows service根据租户Id查询流程定义成功")
        public void testFlowServiceFindByTenantTestSuccess() {
            executeSqlInFile(sqlPath);

            List<FlowDefinitionResult> flows = flowsService.findFlowsByTenant(getOperationContext());

            Assertions.assertEquals(2, flows.size());
        }

        @Test
        @DisplayName("测试flows service根据流程名称和版本查询流程定义成功")
        public void testFlowServiceFindByNameVersionTestSuccess() {
            executeSqlInFile(sqlPath);

            FlowDefinitionResult flow = flowsService.findFlowsByName("流水线创建", "0.0.1", getOperationContext());

            Assertions.assertEquals("0001", flow.getMetaId());
        }

        @Test
        @DisplayName("测试flows service更新流程状态成功")
        public void testFlowServiceUpdateStatusTestSuccess() {
            String jsonData1 = getJsonData(getFilePath("flows_create_with_state_node_and_status_active.json"));
            String jsonData2 = getJsonData(getFilePath("flows_update_with_state_node_and_status_inactive.json"));
            FlowDefinitionResult beforeFlow = flowsService.createFlows(jsonData1, getOperationContext());
            flowsService.updateFlows(beforeFlow.getFlowDefinitionId(), jsonData2, getOperationContext());
            FlowDefinitionResult afterFlow = flowsService.findFlowsById(beforeFlow.getFlowDefinitionId(), getOperationContext());

            Assertions.assertEquals(FlowDefinitionStatus.INACTIVE.getCode(), afterFlow.getStatus());
        }

        @Test
        @DisplayName("测试flows service根据metaId和version查询流程定义成功")
        public void testFlowServiceQueryFlowTestSuccess() {
            executeSqlInFile(sqlPath);

            FlowDefinitionResult definitionPO = flowsService.findFlowsByMetaIdAndVersion("0001", "0.0.1",
                    getOperationContext());

            Assertions.assertEquals(FlowDefinitionStatus.ACTIVE.getCode(), definitionPO.getStatus());
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }

        private OperationContext getOperationContext() {
            return OperationContext.custom().operator("xxx").operatorIp("0.0.0.1").tenantId("xxx").build();
        }

        @Test
        @DisplayName("测试自动生成ohScript代码成功")
        public void testGetScript() {
            List<String> fitableIds = new ArrayList<>();
            fitableIds.add("11111111");
            fitableIds.add("222222222");
            String expectedScript = "let context1 = entity{\n" +
                    "    .id = \"11111111\";\n" +
                    ".async = true;\n" +
                    ".format = \"cbor\";\n" +
                    "};\n" +
                    "let f1 = fit::handleTask(context1);\n" +
                    "\n" +
                    "let context2 = entity{\n" +
                    "    .id = \"222222222\";\n" +
                    ".async = true;\n" +
                    ".format = \"cbor\";\n" +
                    "};\n" +
                    "let f2 = fit::handleTask(context2);\n" +
                    "\n" +
                    "ext::context >> f1 >> f2";
            String actualScript = flowsService.getScript(fitableIds);
            Assertions.assertEquals(expectedScript, actualScript);
        }
    }
}