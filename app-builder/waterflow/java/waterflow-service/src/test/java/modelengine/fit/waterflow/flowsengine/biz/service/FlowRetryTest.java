/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.READY;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.RETRYABLE;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.ProcessType.PROCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.waterflow.entity.OperationContext;
import modelengine.fit.ohscript.util.UUIDUtil;
import modelengine.fit.waterflow.DatabaseBaseTest;
import modelengine.fit.waterflow.FlowsDataBaseTest;
import modelengine.fit.waterflow.MethodNameLoggerExtension;
import modelengine.fit.waterflow.common.utils.SleepUtil;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowDefinitionQueryService;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowQueryService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.To;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowContextPO;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.util.IoUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * {@link FlowRetryService}对应测试类
 *
 * @author 晏钰坤
 * @since 2023/11/6
 */
@ExtendWith(MethodNameLoggerExtension.class)
class FlowRetryTest extends DatabaseBaseTest {
    private static final BrokerClient BROKER_CLIENT = Mockito.mock(BrokerClient.class);

    private static final Parser PARSER = new FlowParser(BROKER_CLIENT);

    @Nested
    @DisplayName("测试流程实例service")
    class FlowContextServiceTest extends FlowsDataBaseTest {
        private static final String SQL_FILE = "handler/flowContext/saveData.sql";

        private static final String FILE_PATH_PREFIX = "flows/executors/";

        private FlowRetryService flowRetryService;

        private FlowContextRepo flowContextRepo;

        private FlowContextMessenger flowContextMessenger;

        private FlowLocks flowLocks;

        private FlowTraceRepo flowTraceRepo;

        private FlowRetryRepo flowRetryRepo;

        private QueryFlowContextPersistRepo queryFlowContextPersistRepo;

        private FlowDefinitionRepo flowDefinitionRepo;

        private TraceOwnerService traceOwnerService;

        private FlowDefinitionQueryService definitionQueryService;

        private FlowQueryService flowQueryService;

        private MockedStatic<SleepUtil> sleepUtilMockedStatic;

        @BeforeEach
        void setUp() {
            FlowContextMapper flowContextMapper = sqlSessionManager.openSession(true)
                    .getMapper(FlowContextMapper.class);
            queryFlowContextPersistRepo = Mockito.mock(QueryFlowContextPersistRepo.class);
            flowDefinitionRepo = Mockito.mock(FlowDefinitionRepo.class);
            flowContextRepo = Mockito.mock(FlowContextRepo.class);
            flowContextMessenger = Mockito.mock(FlowContextMessenger.class);
            flowTraceRepo = Mockito.mock(FlowTraceRepo.class);
            flowRetryRepo = Mockito.mock(FlowRetryRepo.class);
            flowLocks = Mockito.mock(FlowLocks.class);
            traceOwnerService = Mockito.mock(TraceOwnerService.class);

            flowRetryService = new FlowRetryService(flowContextRepo, flowRetryRepo, flowLocks, traceOwnerService,
                    definitionQueryService, flowQueryService);

            sleepUtilMockedStatic = mockStatic(SleepUtil.class);
        }

        @AfterEach
        void tearDown() {
            sleepUtilMockedStatic.close();
        }

        @Test
        @DisplayName("测试重试自动任务成功")
        @Disabled("Mockito不支持mock内联的类")
        public void testRetryJoberSuccess() {
            String toBatchId = "toBatchId";
            FlowRetry flowRetry = new FlowRetry(toBatchId, "toBatch", LocalDateTime.now(), null, 0, 1);
            when(flowRetryRepo.filterByNextRetryTime(any(), anyList()))
                .thenReturn(Collections.singletonList(flowRetry))
                .thenReturn(Collections.emptyList());
            when(flowRetryRepo.updateRetryRecord(anyList())).thenReturn(1);

            String traceId = "traceId";
            String position = "position";
            String streamId = "streamId";
            FlowContext<FlowData> context = new FlowContext(streamId, "rootId", null,
                Collections.singleton(traceId), position);
            context.setStatus(RETRYABLE);
            context.toBatch(toBatchId);
            List<FlowContext<FlowData>> contexts = Collections.singletonList(context);

            when(flowContextRepo.getWithoutFlowDataByToBatch(Collections.singletonList(toBatchId)))
                .thenReturn(contexts)
                .thenReturn(Collections.emptyList());
            when(flowContextRepo.getByToBatch(Collections.singletonList(toBatchId))).thenReturn(contexts);

            // getFlowRetryInfo中查询重试关联节点使用
            From<FlowData> from = Mockito.mock(From.class);
            To<FlowData, Object> to = Mockito.mock(To.class);
            when(from.getSubscriber(position)).thenReturn(to);
            To.ProcessMode processMode = Mockito.mock(To.ProcessMode.class);
            when(to.getProcessMode()).thenReturn(processMode);
            doNothing().when(processMode).submit(same(to), anyList());
            // 执行retryByToBatch和updateRetryStatus使用锁
            Lock lock = Mockito.mock(Lock.class);
            when(flowLocks.getDistributedLock("retry-toBatchId")).thenReturn(lock);
            when(lock.tryLock()).thenReturn(true);
            when(flowLocks.getDistributedLock(
                flowLocks.streamNodeLockKey(streamId, position, PROCESS.toString()))).thenReturn(lock);
            when(traceOwnerService.isAnyOwn(any())).thenReturn(true);
            when(traceOwnerService.getTraces()).thenReturn(Collections.singletonList(traceId));
            sleepUtilMockedStatic.when(() -> SleepUtil.sleep(anyInt())).then((invocation -> null));

            flowRetryService.retryTask();

            verify(traceOwnerService, times(1)).isAnyOwn(any());
            verify(flowRetryRepo, times(2)).filterByNextRetryTime(any(), anyList());
            verify(flowContextRepo, times(2)).getWithoutFlowDataByToBatch(any());
            verify(flowContextRepo, times(1)).getByToBatch(any());
            verify(lock, times(1)).tryLock();
            verify(lock, times(2)).unlock();
            verify(flowRetryRepo, times(1)).updateRetryRecord(any());
            verify(flowContextRepo, times(1)).updateStatus(contexts, READY.toString(), position);

            assertNotNull(flowRetry.getLastRetryTime());
            assertEquals(1, flowRetry.getRetryCount());
        }

        @Test
        @DisplayName("测试重试自动任务成功")
        @Disabled
        public void testRetryTaskSuccess() {
            FlowRetry flowRetry = new FlowRetry("toBatchId", "toBatch", LocalDateTime.now(), null, 0, 1);
            FlowContext<FlowData> context = new FlowContext("streamId", "rootId", null,
                    Collections.singleton("traceId"), "position");
            context.setStatus(RETRYABLE);
            context.toBatch("toBatchId");
            List<FlowContext<FlowData>> contexts = Collections.singletonList(context);

            this.retry(flowRetry, contexts, context);

            List<FlowContext<FlowData>> ans = flowContextRepo.findByTraceId("traceId");
            Assertions.assertEquals(1, ans.size());
            Assertions.assertEquals(contexts.get(0), ans.get(0));
            assertNotNull(flowRetry.getLastRetryTime());
            assertNotEquals(0, flowRetry.getRetryCount());
        }

        @Test
        @DisplayName("测试重试数据为空时自动任务成功")
        public void testEmptyContextRetryTaskSuccess() {
            FlowRetry flowRetry = new FlowRetry("toBatchId", "toBatch", LocalDateTime.now(), null, 0, 1);
            FlowContext<FlowData> context = new FlowContext("streamId", "rootId", null,
                    Collections.singleton("traceId"), "position");
            context.setStatus(RETRYABLE);
            context.toBatch("toBatchId");
            List<FlowContext<FlowData>> contexts = Collections.singletonList(context);
            when(flowRetryRepo.filterByNextRetryTime(any(), anyList()))
                    .thenReturn(new ArrayList<>());
            when(flowContextRepo.getWithoutFlowDataByToBatch(anyList())).thenReturn(contexts);
            when(traceOwnerService.isAnyOwn(anySet())).thenReturn(true);

            flowRetryService.popRetryTask();

            verify(traceOwnerService).isAnyOwn(anySet());
        }

        private void retry(FlowRetry flowRetry, List<FlowContext<FlowData>> contexts, FlowContext<FlowData> context) {
            when(flowRetryRepo.filterByNextRetryTime(any(), anyList()))
                    .thenReturn(Collections.singletonList(flowRetry)).thenReturn(new ArrayList<>());
            when(flowContextRepo.getWithoutFlowDataByToBatch(anyList())).thenReturn(contexts);
            when(traceOwnerService.isAnyOwn(anySet())).thenReturn(true);

            From<FlowData> from = Mockito.mock(From.class);
            To<FlowData, Object> to = Mockito.mock(To.class);
            when(from.getSubscriber("position")).thenReturn(to);

            Lock lock = Mockito.mock(Lock.class);
            when(flowLocks.getDistributedLock("retry-toBatchId")).thenReturn(lock);
            when(lock.tryLock()).thenReturn(true);

            when(traceOwnerService.getTraces()).thenReturn(new ArrayList<>(context.getTraceId()));
            Lock nodeLock = Mockito.mock(Lock.class);
            when(flowLocks.getDistributedLock(flowLocks.streamNodeLockKey(any(), any(), anyString())))
                    .thenReturn(nodeLock);

            when(flowContextRepo.getByToBatch(anyList())).thenReturn(contexts);
            when(flowRetryRepo.updateRetryRecord(anyList())).thenReturn(1);

            when(to.getProcessMode()).thenReturn(To.ProcessMode.PRODUCING);

            when(flowContextRepo.findByTraceId("traceId")).thenReturn(contexts);

            flowRetryService.popRetryTask();
        }

        private List<FlowContextPO> getContextList() {
            List<FlowContextPO> list = new ArrayList<>();
            FlowContextPO po1 = FlowContextPO.builder()
                    .contextId(UUIDUtil.uuid())
                    .positionId("start1")
                    .status("ARCHIVED")
                    .traceId("traceId")
                    .build();
            list.add(po1);

            FlowContextPO po2 = FlowContextPO.builder()
                    .contextId(UUIDUtil.uuid())
                    .positionId("state1")
                    .status("ARCHIVED")
                    .traceId("traceId")
                    .build();
            list.add(po2);

            FlowContextPO po3 = FlowContextPO.builder()
                    .contextId(UUIDUtil.uuid())
                    .positionId("ender1")
                    .status("ARCHIVED")
                    .traceId("traceId")
                    .build();
            list.add(po3);
            return list;
        }

        private List<FlowContextPO> getConditionContextList() {
            List<FlowContextPO> list = new ArrayList<>();
            FlowContextPO po1 = FlowContextPO.builder().positionId("start1").status("ARCHIVED").build();
            FlowContextPO po2 = FlowContextPO.builder().positionId("state1").status("ARCHIVED").build();
            FlowContextPO po3 = FlowContextPO.builder().positionId("condi1").status("ARCHIVED").build();
            FlowContextPO po4 = FlowContextPO.builder().positionId("state2").status("ARCHIVED").build();
            FlowContextPO po5 = FlowContextPO.builder().positionId("state3").status("ARCHIVED").build();
            FlowContextPO po6 = FlowContextPO.builder().positionId("ender1").status("ARCHIVED").build();
            list.add(po1);
            list.add(po2);
            list.add(po3);
            list.add(po4);
            list.add(po5);
            list.add(po6);
            return list;
        }

        private List<FlowTrace> getFlowTrace() {
            return Collections.singletonList(
                    FlowTrace.builder().streamId("streamId").contextPool(new HashSet<>()).build());
        }

        /**
         * 获取json
         *
         * @param fileName 文件名
         * @return json信息
         */
        protected String getJsonData(String fileName) {
            try (InputStream in = IoUtils.resource(FlowContextServiceTest.class.getClassLoader(), fileName)) {
                return new String(IoUtils.read(in), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }

        private OperationContext getOperationContext() {
            return OperationContext.custom().operator("yyk").operatorIp("0.0.0.1").tenantId("framework").build();
        }
    }
}