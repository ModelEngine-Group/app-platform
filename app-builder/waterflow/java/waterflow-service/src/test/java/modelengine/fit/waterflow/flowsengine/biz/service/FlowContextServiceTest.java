/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import static modelengine.fit.waterflow.spi.FlowCompletedService.FLOW_CALLBACK_GENERICABLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alibaba.fastjson.JSON;

import modelengine.fit.waterflow.entity.OperationContext;
import modelengine.fit.ohscript.util.UUIDUtil;
import modelengine.fit.waterflow.DatabaseBaseTest;
import modelengine.fit.waterflow.FlowsDataBaseTest;
import modelengine.fit.waterflow.MethodNameLoggerExtension;
import modelengine.fit.waterflow.entity.FlowStartDTO;
import modelengine.fit.waterflow.entity.FlowStartInfo;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowDefinitionQueryService;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowQueryService;
import modelengine.fit.waterflow.flowsengine.biz.service.entity.FlowsErrorInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocksMemo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.DefaultFlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.DefaultFlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import modelengine.fit.waterflow.flowsengine.fitable.TraceServiceImpl;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FitableUsageMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowDefinitionMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowTraceMapper;
import modelengine.fit.waterflow.spi.FlowCompletedService;
import modelengine.fit.waterflow.spi.lock.InvalidDistributedLockNotify;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * RestartContextSchedule对应测试类
 *
 * @author 杨祥宇
 * @since 2024/4/24
 */
@ExtendWith(MethodNameLoggerExtension.class)
class FlowContextServiceTest extends DatabaseBaseTest {
    private final Parser parser = new FlowParser(null);

    private FlowRuntimeServiceImpl flowContextsService;

    private FlowTraceRepo traceRepo;

    private FlowContextPersistRepo contextPersistRepo;

    private DefaultFlowDefinitionRepo flowDefinitionRepo;

    private static FlowLocks locks = new FlowLocksMemo();

    private FlowContextPersistMessenger messenger = new FlowContextPersistMessenger(null);

    private static InvalidDistributedLockNotify notify = Mockito.mock(InvalidDistributedLockNotify.class);

    private static final TraceOwnerService traceOwnerService = new DefaultTraceOwnerService(locks, notify);

    private FlowTraceMapper traceMapper;

    private FlowContextMapper contextMapper;

    private FlowDefinitionMapper flowDefinitionMapper;

    private FitableUsageMapper fitableUsageMapper;

    private FlowRetryRepo retryRepo;

    private BrokerClient brokerClient;

    @Mock
    private TraceServiceImpl traceService;

    private FlowDefinitionQueryService definitionQueryService;

    private FlowQueryService flowQueryService;

    @BeforeEach
    void setUp() {
        traceMapper = sqlSessionManager.getMapper(FlowTraceMapper.class);
        traceRepo = new DefaultFlowTraceRepo(traceMapper);
        contextMapper = sqlSessionManager.getMapper(FlowContextMapper.class);
        FlowRetryMapper retryMapper = sqlSessionManager.getMapper(FlowRetryMapper.class);
        retryRepo = new DefaultFlowRetryRepo(retryMapper);
        contextPersistRepo = new FlowContextPersistRepo(contextMapper, traceRepo, retryRepo, traceOwnerService, 10,
                false, 1);
        flowDefinitionMapper = sqlSessionManager.getMapper(FlowDefinitionMapper.class);
        fitableUsageMapper = sqlSessionManager.getMapper(FitableUsageMapper.class);
        flowDefinitionRepo = new DefaultFlowDefinitionRepo(flowDefinitionMapper, fitableUsageMapper, parser);
        QueryFlowContextPersistRepo queryContextRepo = new QueryFlowContextPersistRepo(contextMapper);
        brokerClient = Mockito.mock(BrokerClient.class);
        definitionQueryService = Mockito.mock(FlowDefinitionQueryService.class);
        flowQueryService = Mockito.mock(FlowQueryService.class);
        flowContextsService = new FlowRuntimeServiceImpl(flowDefinitionRepo, contextPersistRepo, messenger,
                queryContextRepo, traceRepo, retryRepo, locks, traceOwnerService, traceService, false,
                brokerClient, definitionQueryService, flowQueryService);
    }

    @Nested
    @DisplayName("FlowService功能测试")
    class RestartContextTest extends FlowsDataBaseTest {
        private String sql = "handler/flowContextService/saveData.sql";

        private String deleteSql = "handler/flowContextService/cleanData.sql";

        @AfterEach
        void down() {
            executeSqlInFile(deleteSql);
        }

        @Test
        @DisplayName("测试使用definitionId启动流程成功")
        void testStartFlowsByDefinitionIdSuccess() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setDefinitionId(UUIDUtil.uuid());
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            when(flowQueryService.getPublisher(anyString())).thenReturn(definition.convertToFlow(contextPersistRepo,
                    messenger, locks));
            when(definitionQueryService.findByDefinitionId(anyString())).thenReturn(definition);

            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            FlowStartInfo startInfo = new FlowStartInfo(flowData.getOperator(), flowData.getStartTime(),
                    flowData.getBusinessData());

            FlowStartDTO flowStartDTO = flowContextsService.startFlows(definition.getDefinitionId(), startInfo, null);

            Assertions.assertTrue(traceOwnerService.isOwn(flowStartDTO.getTraceId()));
        }

        @Test
        @DisplayName("测试使metaId和version启动流程成功")
        void testStartFlowsByStreamIdSuccess() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            when(flowQueryService.getPublisher(anyString())).thenReturn(definition.convertToFlow(contextPersistRepo,
                    messenger, locks));
            when(definitionQueryService.findByStreamId(anyString())).thenReturn(definition);

            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");

            FlowStartDTO flowStartDTO = flowContextsService.startFlows(definition.getMetaId(), definition.getVersion(),
                    JSON.toJSONString(flowData));

            Assertions.assertTrue(traceOwnerService.isOwn(flowStartDTO.getTraceId()));
        }

        @Test
        @DisplayName("测试使metaId和version在某个trans下启动流程成功")
        void testStartFlowsByTransSuccess() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            String transId = UUIDUtil.uuid();
            when(flowQueryService.getPublisher(anyString())).thenReturn(definition.convertToFlow(contextPersistRepo,
                    messenger, locks));
            when(definitionQueryService.findByStreamId(anyString())).thenReturn(definition);

            FlowStartDTO flowStartDTO = flowContextsService.startFlowsWithTrans(definition.getMetaId(),
                    definition.getVersion(), transId, JSON.toJSONString(flowData));

            Assertions.assertTrue(traceOwnerService.isOwn(flowStartDTO.getTraceId()));
        }

        @Test
        @DisplayName("测试获取流程错误信息成功")
        void testGetFlowErrorInfoSuccess() {
            executeSqlInFile(sql);
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            String traceId = "10174dc2b03e4e15a7611ad3e66e736e";
            when(definitionQueryService.findByStreamId(anyString())).thenReturn(definition);

            List<FlowsErrorInfo> flowErrorInfo = flowContextsService.getFlowErrorInfo(traceId);

            Assertions.assertEquals(1, flowErrorInfo.size());
            Assertions.assertEquals(10007501, flowErrorInfo.get(0).getContextErrorInfo().getErrorCode());
        }

        @Test
        @DisplayName("测试根据transId终止流程成功")
        void testTerminateFlowsSuccess() {
            executeSqlInFile(sql);
            String traceId = "11174dc2b03e4e15a7611ad3e66e736e";
            String transId = "1114ead3de8b4dd484c80f77c562b698";
            OperationContext operationContext = OperationContext.custom()
                    .tenantId("")
                    .operator("yxy")
                    .operatorIp("")
                    .sourcePlatform("")
                    .langage("")
                    .build();

            flowContextsService.terminateFlowsByTransId(transId, operationContext);

            FlowTrace flowTrace = traceRepo.find(traceId);
            List<FlowContext<FlowData>> contexts = contextPersistRepo.findByTraceId(traceId);
            Assertions.assertEquals(FlowTraceStatus.TERMINATE, flowTrace.getStatus());
            Assertions.assertEquals(1, contexts.size());
            Assertions.assertEquals(FlowNodeStatus.TERMINATE, contexts.get(0).getStatus());
        }

        @Test
        @DisplayName("测试计算trace完成状态成功")
        void testCalculateFlowTraceStatusArchivedSuccess() {
            executeSqlInFile(sql);
            FlowDefinition definition = initCalculateFlowTraceStatusFlowDefinition();
            when(definitionQueryService.findByStreamId(anyString())).thenReturn(definition);

            String traceId = "archivedTrace1";
            String transId = "archivedTrans1";
            traceOwnerService.own(traceId, transId);

            flowContextsService.calculateFlowTraceStatus();

            FlowTrace flowTrace = traceRepo.find(traceId);
            Assertions.assertEquals(FlowTraceStatus.ARCHIVED, flowTrace.getStatus());
        }

        @Test
        @DisplayName("测试计算trace失败状态")
        void testCalculateFlowTraceStatusErrorSuccess() {
            executeSqlInFile(sql);
            FlowDefinition definition = initCalculateFlowTraceStatusFlowDefinition();
            when(definitionQueryService.findByStreamId(anyString())).thenReturn(definition);
            String traceId = "errorTrace1";
            String transId = "errorTrans1";
            traceOwnerService.own(traceId, transId);

            flowContextsService.calculateFlowTraceStatus();

            FlowTrace flowTrace = traceRepo.find(traceId);
            Assertions.assertEquals(FlowTraceStatus.ERROR, flowTrace.getStatus());
        }

        @Test
        @DisplayName("测试计算trace部分失败状态")
        void testCalculateFlowTraceStatusPartialErrorSuccess() {
            executeSqlInFile(sql);
            FlowDefinition definition = initCalculateFlowTraceStatusFlowDefinition();
            when(definitionQueryService.findByStreamId(anyString())).thenReturn(definition);
            String traceId = "partialErrorTrace1";
            String transId = "partialErrorTrans1";
            traceOwnerService.own(traceId, transId);

            flowContextsService.calculateFlowTraceStatus();

            FlowTrace flowTrace = traceRepo.find(traceId);
            Assertions.assertEquals(FlowTraceStatus.PARTIAL_ERROR, flowTrace.getStatus());
        }

        @Test
        @DisplayName("测试计算trace终止状态")
        void testCalculateFlowTraceStatusTerminateSuccess() {
            executeSqlInFile(sql);
            FlowDefinition definition = initCalculateFlowTraceStatusFlowDefinition();
            when(definitionQueryService.findByStreamId(anyString())).thenReturn(definition);
            String traceId = "terminateTrace1";
            String transId = "terminateTrans1";
            traceOwnerService.own(traceId, transId);

            flowContextsService.calculateFlowTraceStatus();

            FlowTrace flowTrace = traceRepo.find(traceId);
            Assertions.assertEquals(FlowTraceStatus.TERMINATE, flowTrace.getStatus());
        }

        @Test
        @DisplayName("测试计算trace运行中状态")
        void testCalculateFlowTraceStatusRunningSuccess() {
            executeSqlInFile(sql);
            initCalculateFlowTraceStatusFlowDefinition();
            String traceId = "runningTrace1";
            String transId = "runningTrans1";
            traceOwnerService.own(traceId, transId);

            flowContextsService.calculateFlowTraceStatus();

            FlowTrace flowTrace = traceRepo.find(traceId);
            Assertions.assertEquals(FlowTraceStatus.RUNNING, flowTrace.getStatus());
        }

        private FlowDefinition initCalculateFlowTraceStatusFlowDefinition() {
            String jsonData = getJsonData(getFilePath("flows_calculate_trace_status.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setDefinitionId(UUIDUtil.uuid());
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            return definition;
        }

        @Test
        @DisplayName("测试流程计算完成后回调成功")
        void testFlowCompletedCallbackSuccessful() {
            executeSqlInFile(sql);
            String traceId = "22274dc2b03e4e15a7611ad3e66e736e";
            String transId = "2224ead3de8b4dd484c80f77c562b698";
            traceOwnerService.own(traceId, transId);
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setDefinitionId(UUIDUtil.uuid());
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            Router router = Mockito.mock(Router.class);
            Invoker invoker = Mockito.mock(Invoker.class);
            when(brokerClient.getRouter(FlowCompletedService.class, FLOW_CALLBACK_GENERICABLE))
                    .thenReturn(router);
            when(router.route(any(FitableIdFilter.class))).thenReturn(invoker);
            when(invoker.timeout(anyLong(), any(TimeUnit.class))).thenReturn(invoker);
            when(definitionQueryService.findByStreamId(anyString())).thenReturn(definition);

            flowContextsService.calculateFlowTraceStatus();

            verify(brokerClient, times(1)).getRouter(FlowCompletedService.class, FLOW_CALLBACK_GENERICABLE);
        }

        @Test
        @DisplayName("测试根据streamId成功查询运行中、错误、已完成的context")
        void testFindContextStatusViewByStreamIdsSuccess() {
            executeSqlInFile(sql);
            String streamId = "executor-2.0.0";

            List<Map<String, Object>> result = flowContextsService.findContextStatusViewByStreamIds(
                    Collections.singletonList(streamId), null);

            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(1, result.get(0).get("allContexts"));
            Assertions.assertEquals(1, result.get(0).get("runningContexts"));
            Assertions.assertEquals(1, result.get(0).get("errorContexts"));
        }

        @Override
        protected String getFilePathPrefix() {
            return "flows/executors/";
        }
    }
}