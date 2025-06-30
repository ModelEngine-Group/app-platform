/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_EXECUTE_FITABLE_TASK_FAILED;
import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.waterflow.flowsengine.domain.flows.FlowsTestUtil.MAX_WAIT_TIME_MS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.ohscript.util.UUIDUtil;
import modelengine.fit.waterflow.DatabaseBaseTest;
import modelengine.fit.waterflow.FlowsDataBaseTest;
import modelengine.fit.waterflow.MethodNameLoggerExtension;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.flowsengine.biz.service.DefaultTraceOwnerService;
import modelengine.fit.waterflow.flowsengine.domain.flows.Activities;
import modelengine.fit.waterflow.flowsengine.domain.flows.Flows;
import modelengine.fit.waterflow.flowsengine.domain.flows.FlowsTestUtil;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMemoMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMemoRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocksMemo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.DefaultFlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Blocks;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowTraceMapper;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowRetryPO;
import modelengine.fit.waterflow.spi.lock.InvalidDistributedLockNotify;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.TargetNotFoundException;
import modelengine.fitframework.event.EventPublisher;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * flow context持久化测试
 *
 * @author 杨祥宇
 * @since 2023/8/23
 */
@ExtendWith(MethodNameLoggerExtension.class)
@DisplayName("流程实例在数据库中运行测试集合")
public class FlowContextPersistTest extends DatabaseBaseTest {
    private static final BrokerClient BROKER_CLIENT = Mockito.mock(BrokerClient.class);

    private static final Router ROUTER = Mockito.mock(Router.class);

    private static final Invoker INVOKER = Mockito.mock(Invoker.class);

    private static final Plugin PLUGIN = Mockito.mock(Plugin.class);

    private static final FitRuntime FIT_RUNTIME = Mockito.mock(FitRuntime.class);

    private static final EventPublisher EVENT_PUBLISHER = Mockito.mock(EventPublisher.class);

    private static final Parser PARSER = new FlowParser(BROKER_CLIENT);

    private static final FlowContextMapper FLOW_CONTEXT_MAPPER;

    private static final FlowTraceMapper FLOW_TRACE_MAPPER;

    private static final FlowTraceRepo FLOW_TRACE_REPO;

    private static final FlowRetryMapper FLOW_RETRY_MAPPER;

    private static final FlowRetryRepo FLOW_RETRY_REPO;

    private static final FlowContextRepo<FlowData> REPO;

    private static final FlowContextMessenger MEMO_MESSENGER = new FlowContextMemoMessenger();

    private static final FlowContextMessenger PERSIST_MESSENGER = new FlowContextPersistMessenger(PLUGIN);

    private static final FlowLocks LOCKS = new FlowLocksMemo();

    static {
        sqlSessionManager.startManagedSession(true);
        FLOW_CONTEXT_MAPPER = sqlSessionManager.getMapper(FlowContextMapper.class);
        FLOW_TRACE_MAPPER = sqlSessionManager.getMapper(FlowTraceMapper.class);
        FLOW_TRACE_REPO = new DefaultFlowTraceRepo(FLOW_TRACE_MAPPER);
        FLOW_RETRY_MAPPER = sqlSessionManager.getMapper(FlowRetryMapper.class);
        FLOW_RETRY_REPO = Mockito.mock(FlowRetryRepo.class);
        InvalidDistributedLockNotify invalidDistributedLockNotify = new InvalidDistributedLockNotify() {
            @Override
            public void subscribe(Consumer<Lock> consumer) {
            }

            @Override
            public void notify(Lock invalidLock) {
            }
        };
        Integer defaultLimitation = 5;
        boolean useLimit = false;
        REPO = new FlowContextPersistRepo(FLOW_CONTEXT_MAPPER, FLOW_TRACE_REPO, FLOW_RETRY_REPO,
                new DefaultTraceOwnerService(LOCKS, invalidDistributedLockNotify), defaultLimitation,
                useLimit, 10);
    }

    @Nested
    @DisplayName("测试stream flowContext持久化")
    class StreamFlowContextPersistTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/convertors/";

        @Test
        @DisplayName("测试只包含map的context持久化成功")
        void testFlowContextPersistWithMapSuccess() {
            List<FlowData> result = new ArrayList<>();
            FlowData data = genFlowData("url", "www.123.com");

            Flows.<FlowData>create(REPO, MEMO_MESSENGER, LOCKS)
                    .map(i -> i)
                    .close(r -> result.add(r.get().getData()))
                    .offer(data);
            FlowsTestUtil.waitSingle(() -> result);
            assertEquals(data.getBusinessData().get("url"), result.get(0).getBusinessData().get("url"));
        }

        @Test
        @DisplayName("测试m->n持久化成功")
        void testFlowContextPersistWithProduceSuccess() {
            List<FlowData> result = new ArrayList<>();
            FlowData data2 = genFlowData("name", "yxy");
            FlowData data3 = genFlowData("result", "success");
            Flows.ProcessFlow<FlowData> flow = Flows.<FlowData>create(REPO, MEMO_MESSENGER, LOCKS).produce(i -> {
                i.add(data2);
                return i;
            }).produce(i -> {
                i.add(data3);
                return i;
            }).close(r -> result.addAll(r.getAll().stream().map(FlowContext::getData).collect(Collectors.toList())));

            FlowData data = genFlowData("url", "www.123.com");
            flow.offer(data);
            FlowsTestUtil.waitSize(() -> result, 3);
            Assertions.assertEquals(3, result.size());
            assertEquals(data.getBusinessData().get("application"),
                    result.get(0).getBusinessData().get("application"));
            assertEquals(data2.getBusinessData().get("name"), result.get(1).getBusinessData().get("name"));
            assertEquals(data3.getBusinessData().get("result"),
                    result.get(2).getBusinessData().get("result"));

            FlowData data1 = genFlowData("applyService", "fitable");
            flow.offer(data1);
            FlowsTestUtil.waitSize(() -> result, 6);
            Assertions.assertEquals(6, result.size());
        }

        @Test
        @DisplayName("测试带有block流程实例持久化成功")
        void testFlowContextPersistWithBlockSuccess() {
            List<FlowData> result = new ArrayList<>();
            FlowData data = genFlowData("url", "www.123.com");
            FlowData data1 = genFlowData("applyService", "fitable");
            FlowData data2 = genFlowData("name", "yxy");
            FlowData data3 = genFlowData("result", "success");

            FlowData[] inputs = {data, data1};
            Blocks.FilterBlock<FlowData> block = new Blocks.FilterBlock<>();

            Activities.Start<FlowData, FlowData, Flows.ProcessFlow<FlowData>>
                    start = Flows.create(REPO, MEMO_MESSENGER, LOCKS);
            Flows.ProcessFlow<FlowData> flow = start.produce(i -> {
                i.add(data2);
                return i;
            }).block(block).produce(i -> {
                i.add(data3);
                return i;
            }).close(r -> result.addAll(r.getAll().stream().map(FlowContext::getData).collect(Collectors.toList())));

            String traceId = flow.offer(inputs).getTraceId();
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                    contextSupplier(REPO, traceId, start.getSubscriptionsId().get(0), FlowNodeStatus.PENDING), 2);
            Assertions.assertEquals(0, result.size());

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            String toBatch = UUIDUtil.uuid();
            contexts.forEach(c -> c.toBatch(toBatch));
            REPO.updateFlowDataAndToBatch(contexts);
            block.process(contexts);
            FlowsTestUtil.waitSize(() -> result, 4);
            Assertions.assertEquals(4, result.size());
        }

        @Test
        @DisplayName("测试带有condition节点流程实例持久化成功")
        void testFlowContextPersistWithCondition() {
            List<FlowData> result = new ArrayList<>();
            FlowData data = genFlowData("url", "www.123.com");
            FlowData data1 = genFlowData("applyService", "fitable");

            Flows.ProcessFlow<FlowData> flow = Flows.<FlowData>create(REPO, MEMO_MESSENGER, LOCKS)
                    .conditions()
                    .match(i -> i.getData().getBusinessData().equals(data.getBusinessData()))
                    .just(i -> i.getBusinessData().put("url", "success"))
                    .match(i -> i.getData().getBusinessData().equals(data1.getBusinessData()))
                    .just(i -> i.getBusinessData().put("applyService", "success"))
                    .others(input -> input)
                    .close(r -> result.add(r.get().getData()));

            flow.offer(data);
            FlowsTestUtil.waitSingle(() -> result);
            FlowData data2 = genFlowData("url", "success");
            assertEquals(data2.getBusinessData(), result.get(0).getBusinessData());

            result.clear();
            flow.offer(data1);
            FlowsTestUtil.waitSingle(() -> result);
            FlowData data3 = genFlowData("applyService", "success");
            assertEquals(data3.getBusinessData(), result.get(0).getBusinessData());
        }

        @Test
        @DisplayName("测试一个节点不同实例context查找某一个实例context成功")
        void testFlowContextPersistWithMoreThanOneContextInNode() {
            List<FlowData> result = new ArrayList<>();
            FlowData data = genFlowData("url", "www.123.com");

            Blocks.FilterBlock<FlowData> block = new Blocks.FilterBlock<>();

            Activities.Start<FlowData, FlowData, Flows.ProcessFlow<FlowData>>
                    start = Flows.create(REPO, MEMO_MESSENGER, LOCKS);
            Flows.ProcessFlow<FlowData> flow = start.produce(i -> {
                result.clear();
                result.addAll(i);
                return i;
            }).block(block).produce(i -> i).close();

            String traceId1 = flow.offer(data).getTraceId();
            List<FlowContext<FlowData>> contexts1 = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, traceId1, start.getSubscriptionsId().get(0), FlowNodeStatus.PENDING));
            Assertions.assertEquals(0, result.size());

            FlowData data1 = genFlowData("applyService", "fitable");
            String traceId2 = flow.offer(data1).getTraceId();
            List<FlowContext<FlowData>> contexts2 = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, traceId2, start.getSubscriptionsId().get(0), FlowNodeStatus.PENDING));
            Assertions.assertEquals(0, result.size());

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            String toBatch1 = UUIDUtil.uuid();
            contexts1.forEach(c -> c.toBatch(toBatch1));
            REPO.updateFlowDataAndToBatch(contexts1);
            block.process(contexts1);
            FlowsTestUtil.waitSingle(() -> result);
            Assertions.assertEquals(1, result.size());
            assertEquals(data.getBusinessData().get("application"),
                    result.get(0).getBusinessData().get("application"));
            assertEquals(data.getBusinessData().get("url"), result.get(0).getBusinessData().get("url"));
            result.clear();

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            String toBatch2 = UUIDUtil.uuid();
            contexts2.forEach(c -> c.toBatch(toBatch2));
            REPO.updateFlowDataAndToBatch(contexts2);
            block.process(contexts2);
            FlowsTestUtil.waitSingle(() -> result);
            Assertions.assertEquals(1, result.size());
            assertEquals(data1.getBusinessData().get("application"),
                    result.get(0).getBusinessData().get("application"));
            assertEquals(data1.getBusinessData().get("url"), result.get(0).getBusinessData().get("url"));
        }

        private FlowData genFlowData(String key, String value) {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("application", "framework");
            businessData.put(key, value);
            return FlowData.builder()
                    .operator("yxy")
                    .startTime(LocalDateTime.now())
                    .businessData(businessData)
                    .contextData(new HashMap<>())
                    .build();
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @DisplayName("流程实例自动流转1TO1在数据库持久化的场景测试集合")
    class FlowAutoExecutorInPersistTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1只有state节点的场景")
        void testFlowsExecutorWithOnlyStateNode1To1() {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                contextSupplier(memRepo, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED), 1,
                MAX_WAIT_TIME_MS);
            Assertions.assertNotNull(contexts);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(memRepo, traceId);

            assertFlowsExecutorWithOnlyStateNode1To1(flowData, contexts, all);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1只有state节点第一个节点错误的场景")
        void testFlowsExecutorStateNodeWithErrorForFirstNode1To1() {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            flowDefinition.setMetaId(stateError1);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            String metaId = "state1";
            FlowNode flowNode = flowDefinition.getFlowNode(metaId);
            FlowJober flowJoberMocker = mock(FlowJober.class);
            flowNode.setJober(flowJoberMocker);
            WaterflowParamException expectException = new WaterflowParamException(INPUT_PARAM_IS_INVALID, "xxx");
            when(flowJoberMocker.execute(any())).thenThrow(expectException);
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                contextSupplier(memRepo, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ERROR), 1,
                MAX_WAIT_TIME_MS);
            Assertions.assertNotNull(contexts);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(memRepo, traceId);
            assertFlowsExecutorStateNodeWithException(flowDefinition, metaId, contexts, all, 2, expectException);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1只有state节点第二个节点错误的场景")
        void testFlowsExecutorStateNodeWithErrorForSecondNode1To1() {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            flowDefinition.setMetaId(stateError2);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            String metaId = "state2";
            FlowNode flowNode = flowDefinition.getFlowNode(metaId);
            FlowJober flowJoberMocker = mock(FlowJober.class);
            flowNode.setJober(flowJoberMocker);
            WaterflowParamException expectException = new WaterflowParamException(INPUT_PARAM_IS_INVALID, "xxx");
            when(flowJoberMocker.execute(any())).thenThrow(expectException);
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                contextSupplier(memRepo, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ERROR), 1,
                MAX_WAIT_TIME_MS);
            Assertions.assertNotNull(contexts);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(memRepo, traceId);
            assertFlowsExecutorStateNodeWithException(flowDefinition, metaId, contexts, all, 3, expectException);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支1通过场景")
        void testFlowsExecutorWithConditionNodeFirstBranchTrue() {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(memRepo, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(memRepo, traceId);

            assertFlowsExecutorWithConditionNodeFirstBranchTrue(flowData, contexts, all);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支1驳回场景")
        void testFlowsExecutorWithConditionNodeFirstBranchFalse() {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            Map<String, Object> businessData = flowsExecutorWithConditionNodeFirstBranchTrue();
            businessData.put("cmc", new HashMap<String, Boolean>() {
                {
                    put("approved", false);
                }
            });
            FlowData flowData = getFlowData(businessData, "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(memRepo, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(memRepo, traceId);

            assertFlowsExecutorWithConditionNodeFirstFalseBranch(flowData, contexts, all);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支2驳回场景")
        void testFlowsExecutorWithConditionNodeSecondBranchFalse() {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            Map<String, Object> businessData = flowsExecutorWithConditionNodeFirstBranchTrue();
            businessData.put("committer", new HashMap<String, Boolean>() {
                {
                    put("approved", false);
                }
            });
            FlowData flowData = getFlowData(businessData, "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(memRepo, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(memRepo, traceId);

            assertFlowsExecutorWithConditionNodeSecondFalseBranch(flowData, contexts, all);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点异常场景")
        void testFlowsExecutorConditionNodeWithError() {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            flowDefinition.setMetaId(conditionError1);
            FlowData flowData = getFlowData(flowsExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            String metaId = "state1";
            FlowNode flowNode = flowDefinition.getFlowNode(metaId);
            FlowJober flowJoberMocker = mock(FlowJober.class);
            flowNode.setJober(flowJoberMocker);
            WaterflowParamException expectException = new WaterflowParamException(INPUT_PARAM_IS_INVALID, "xxx");
            when(flowJoberMocker.execute(any())).thenThrow(expectException);
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                    contextSupplier(memRepo, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ERROR), 1,
                    MAX_WAIT_TIME_MS);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(memRepo, traceId);
            assertFlowsExecutorStateNodeWithException(flowDefinition, metaId, contexts, all, 3, expectException);
        }
    }

    @Nested
    @Disabled("目前发现测试用的H2内存数据库出现insert成功但是查不到的情况，后面某个用例给查出来了")
    @DisplayName("流程实例手动流转1TO1在数据库持久化的场景测试集合")
    class FlowManualExecutorInPersistTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例1到1包含condition节点人工任务分支1通过的场景测试")
        void testFlowsManualExecutorWithConditionNodeFirstBranchTrue() {
            String jsonData = getJsonData(getFilePath("flows_manual_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsManualExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            String metaId = "event1"; // 来自json文件中的配置

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, FlowNodeStatus.PENDING));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock(metaId, contexts, all);

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext = contexts.get(0);
            Blocks.Block<FlowData> block = from.getBlock(resumeContext.getPosition());

            resumeContext.getData().getBusinessData().put("status", "true");
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowDataAndToBatch(Collections.singletonList(resumeContext));
            block.process(Collections.singletonList(resumeContext));

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            List<FlowContext<FlowData>> resumeContexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED));
            List<FlowContext<FlowData>> resumeAll = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueAfterBlock(resumeContexts, resumeAll, 5,
                    "echo: success");
        }

        @Test
        @DisplayName("流程实例1到1包含condition节点人工任务分支1驳回的场景测试")
        void testFlowsManualExecutorWithConditionNodeFirstBranchFalse() {
            String jsonData = getJsonData(getFilePath("flows_manual_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsManualExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            String metaId = "event1"; // 来自json文件中的配置

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                getSentContextSupplier(REPO, streamId, traceId, metaId, FlowNodeStatus.PENDING), 1, MAX_WAIT_TIME_MS);
            Assertions.assertNotNull(contexts);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock(metaId, contexts, all);

            FlowContext<FlowData> resumeContext = contexts.get(0);
            Blocks.Block<FlowData> block = from.getBlock(resumeContext.getPosition());
            resumeContext.getData().getBusinessData().put("status", false);
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowDataAndToBatch(Collections.singletonList(resumeContext));
            block.process(Collections.singletonList(resumeContext));

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            List<FlowContext<FlowData>> resumeContexts = FlowsTestUtil.waitSize(
                contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED), 1,
                MAX_WAIT_TIME_MS);
            Assertions.assertNotNull(resumeContexts);
            List<FlowContext<FlowData>> resumeAll = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueAfterBlock(resumeContexts, resumeAll, 4,
                "success");
        }

        @Test
        @DisplayName("流程实例1到1包含condition节点人工任务转审的场景测试")
        void testFlowsManualExecutorWithConditionNodeCircle() {
            String jsonData = getJsonData(getFilePath("创建联调分支扩展转审场景.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsManualExecutorWithConditionNodeCircle(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, PERSIST_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            String metaId = "event1"; // 来自json文件中的配置
            when(PLUGIN.runtime()).thenReturn(FIT_RUNTIME);
            when(FIT_RUNTIME.publisherOfEvents()).thenReturn(EVENT_PUBLISHER);
            doAnswer(invocation -> null).when(EVENT_PUBLISHER).publishEvent(any());

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, FlowNodeStatus.PENDING));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeCircle(metaId, contexts, all, 2, FlowNodeStatus.PENDING);
            assertNull(contexts.get(0).getToBatch());

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext = contexts.get(0);
            Blocks.Block<FlowData> block = from.getBlock(resumeContext.getPosition());
            resumeContext.getData().getBusinessData().put("status", "transferred");
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowDataAndToBatch(Collections.singletonList(resumeContext));
            block.process(Collections.singletonList(resumeContext));

            metaId = "event5";
            contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, FlowNodeStatus.PENDING));
            all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeCircle(metaId, contexts, all, 4, FlowNodeStatus.PENDING);
            assertNull(contexts.get(0).getToBatch());

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            resumeContext = contexts.get(0);
            block = from.getBlock(resumeContext.getPosition());
            resumeContext.getData().getBusinessData().put("status", "approved");
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowDataAndToBatch(Collections.singletonList(resumeContext));
            block.process(Collections.singletonList(resumeContext));

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED));
            all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeCircle(metaId, contexts, all, 7, FlowNodeStatus.ARCHIVED);
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @DisplayName("流程实例自动流转1TO1JoberWithFitable节点任务在数据库持久化的场景测试集合")
    class FlowAutoExecutorInPersistWithJoberIncludeFitableTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例1到1只有state节点的执行GeneralJober任务场景测试")
        void testFlowsExecuteGeneralJober() {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            executeJober(flowData);
            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                contextSupplier(memRepo, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED), 1,
                MAX_WAIT_TIME_MS);
            Assertions.assertNotNull(contexts);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(memRepo, traceId);

            assertFlowsExecuteGeneralJober(contexts, all);
        }

        @Test
        @DisplayName("测试实例自动流转state节点执行GeneralJober执行失败抛出异常")
        void testFlowsExecuteGeneralJoberError() throws Throwable {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            Router mockRouter = Mockito.mock(Router.class);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(mockRouter);
            Invoker mockInvoker = Mockito.mock(Invoker.class);
            when(mockRouter.route(any())).thenReturn(mockInvoker);
            when(mockInvoker.communicationType(any())).thenReturn(mockInvoker);
            when(mockInvoker.invoke(anyList())).thenThrow(new WaterflowException(FLOW_EXECUTE_FITABLE_TASK_FAILED));
            when(mockInvoker.invoke(anyString(), anyList(), isA(FlowErrorInfo.class))).thenReturn(null);

            String traceId = from.offer(flowData).getTraceId();

            String metaId = "state1";
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                contextSupplier(memRepo, streamId, traceId, metaId, FlowNodeStatus.ERROR), 1, MAX_WAIT_TIME_MS);
            Assertions.assertNotNull(contexts);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(memRepo, traceId);
            assertFlowsExecuteGeneralJoberError(mockInvoker, flowDefinition, metaId, contexts, all);
        }

        @Test
        @Disabled("目前发现测试用的H2内存数据库出现insert成功但是查不到的情况，后面某个用例给查出来了")
        @DisplayName("测试实例自动流转state节点执行GeneralJober执行失败抛出可重试异常")
        void testFlowsExecuteGeneralJoberWithRetryableException() throws Throwable {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            Router mockRouter = Mockito.mock(Router.class);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(mockRouter);
            Invoker mockInvoker = Mockito.mock(Invoker.class);
            when(mockRouter.route(any())).thenReturn(mockInvoker);
            when(mockInvoker.communicationType(any())).thenReturn(mockInvoker);
            TargetNotFoundException exception = new TargetNotFoundException("test");
            when(mockInvoker.invoke(anyList())).thenThrow(exception);

            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "lzf");
            String traceId = from.offer(flowData).getTraceId();

            String metaId = "state1";
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                contextSupplier(REPO, streamId, traceId, metaId, FlowNodeStatus.RETRYABLE), 1, MAX_WAIT_TIME_MS);

            AtomicReference<FlowRetryPO> retryPO = new AtomicReference<>();
            FlowsTestUtil.waitUntil(() -> {
                retryPO.set(FLOW_RETRY_MAPPER.find(contexts.get(0).getToBatch()));
                return retryPO.get() != null;
            }, MAX_WAIT_TIME_MS);
            Assertions.assertNotNull(retryPO);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecuteGeneralJoberWithRetryableException(from, metaId, contexts, all, retryPO.get());
        }

        @Test
        @DisplayName("流程实例1到1只有state节点执行GeneralJober时动态修改fitable调用")
        void testFlowsExecuteGeneralJoberAndModifyFitables() throws Throwable {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);

            Map<String, Object> businessData = flowsExecutorWithOnlyStateNode1To1();
            businessData.put("state1", JSONObject.parseObject(
                    "{\"name\":\"通知\",\"jober\":{\"type\":\"general_jober\",\"fitables\""
                            + ":[\"423629ecfbe24b3987dd3fa66ca74e8e\"]}}"));
            FlowData flowData = getFlowData(businessData, "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            executeJober(flowData);
            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> allContexts = FlowsTestUtil.waitSize(
                () -> this.getContextsByTraceWrapper(memRepo, traceId), 3, MAX_WAIT_TIME_MS);
            Assertions.assertNotNull(allContexts);

            JSONObject jsonObject = ObjectUtils.cast(allContexts.get(0).getData().getBusinessData().get("state1"));
            JSONObject jober = ObjectUtils.cast(jsonObject.get("jober"));
            JSONArray newFitables = ObjectUtils.cast(jober.get("fitables"));
            Set<String> oldFitables = flowDefinition.getFlowNode("state1").getJober().getFitables();

            assertEquals("创建分支实现", oldFitables.stream().findFirst().get());
            assertEquals("423629ecfbe24b3987dd3fa66ca74e8e", newFitables.getString(0));
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }

        private void executeJober(FlowData flowData) {
            List<Map<String, Object>> outputs = new ArrayList<>();
            Map<String, Object> data = new HashMap<>();
            data.put("businessData", flowData.getBusinessData());
            outputs.add(data);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(ROUTER);
            when(ROUTER.route(any())).thenReturn(INVOKER);
            when(INVOKER.communicationType(any())).thenReturn(INVOKER);
            when(INVOKER.invoke(any())).thenReturn(outputs);
        }
    }

    @Nested
    @DisplayName("流程实例流转M_TO_N在数据库持久化的场景测试集合")
    class FlowAutoExecutorInPersistMToNTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例m到n包含condition节点一次只offer一个数据的场景测试")
        void testFlowsExecuteProduceFromMToNForOfferOneData() {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_condition_node_m_to_n.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            List<FlowData> list = new ArrayList<>();
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "gsy"));
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "yyk"));
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(false), "yxy"));
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, PERSIST_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            List<Map<String, Object>> outputs = new ArrayList<>();
            list.forEach(flowData -> {
                Map<String, Object> data = new HashMap<>();
                data.put("businessData", flowData.getBusinessData());
                data.put("passData", new HashMap<>());
                outputs.add(data);
            });
            Router mockRouter = Mockito.mock(Router.class);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(mockRouter);
            Invoker mockInvoker = Mockito.mock(Invoker.class);
            when(mockRouter.route(any())).thenReturn(mockInvoker);
            when(mockInvoker.communicationType(any())).thenReturn(mockInvoker);
            when(mockInvoker.invoke(any())).thenReturn(outputs);

            String traceId = from.offer(list.get(0)).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                    contextSupplier(memRepo, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED), 3);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(memRepo, traceId);

            assertFlowsExecuteProduceFromMToNForOfferOneData(contexts, all);
        }

        @Test
        @DisplayName("流程实例m到n包含condition节点一次只offer三个数据的场景测试")
        void testFlowsExecuteProduceFromMToNForOfferMultiData() {
            FlowContextRepo<FlowData> memRepo = new FlowContextMemoRepo<>();

            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_condition_node_m_to_n.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            ArrayList<FlowData> flowData = new ArrayList<>();
            flowData.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "gsy"));
            flowData.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "yyk"));
            flowData.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(false), "yxy"));
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(memRepo, PERSIST_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            List<Map<String, Object>> outputs = new ArrayList<>();
            flowData.forEach(flowDatum -> {
                Map<String, Object> data = new HashMap<>();
                data.put("businessData", flowDatum.getBusinessData());
                data.put("passData", new HashMap<>());
                outputs.add(data);
            });
            Router mockRouter = Mockito.mock(Router.class);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(mockRouter);
            Invoker mockInvoker = Mockito.mock(Invoker.class);
            when(mockRouter.route(any())).thenReturn(mockInvoker);
            when(mockInvoker.communicationType(any())).thenReturn(mockInvoker);
            when(mockInvoker.invoke(any())).thenReturn(outputs);

            String traceId = from.offer(flowData.toArray(new FlowData[0])).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                    contextSupplier(memRepo, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED), 3,
                    MAX_WAIT_TIME_MS);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(memRepo, traceId);

            assertFlowsExecuteProduceFromMToNForOfferMultiData(contexts, all);
        }

        @Disabled("使用了batchSize合并了多个traceId的context, 目前流程运行已经无法捞取多traceId的context")
        @Test
        @DisplayName("流程实例m到n包含condition节点包含前后过滤器的场景测试")
        void testFlowsExecuteFilterFromMToN() {
            String jsonData = getJsonData(getFilePath("flows_manual_echo_with_condition_node_m_to_n.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData1 = getFlowData(flowsExecuteFilterFromMToN(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, PERSIST_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            String eventMetaId = "event1"; // 来自json文件中的配置
            String stateMetaId = "state1"; // 来自json文件中的配置
            when(PLUGIN.runtime()).thenReturn(FIT_RUNTIME);
            when(FIT_RUNTIME.publisherOfEvents()).thenReturn(EVENT_PUBLISHER);
            doAnswer(invocation -> null).when(EVENT_PUBLISHER).publishEvent(any());

            String traceId1 = from.offer(flowData1).getTraceId();
            List<FlowContext<FlowData>> contexts1 = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId1, eventMetaId, FlowNodeStatus.PENDING));
            assertEquals(1, contexts1.size());

            checkContext(contexts1, true, from);
            contexts1 = FlowsTestUtil.waitFortyMillis(
                    contextSupplier(REPO, streamId, traceId1, eventMetaId, FlowNodeStatus.PENDING));
            assertEquals(1, contexts1.size());
            verify(EVENT_PUBLISHER, times(0)).publishEvent(any());

            FlowData flowData2 = getFlowData(flowsExecuteFilterFromMToN(), "yyk");
            String traceId2 = from.offer(flowData2).getTraceId();
            List<FlowContext<FlowData>> contexts2 = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId2, eventMetaId, FlowNodeStatus.PENDING));
            assertEquals(1, contexts2.size());

            checkContext(contexts2, true, from);
            contexts2 = FlowsTestUtil.waitFortyMillis(
                    contextSupplier(REPO, streamId, traceId2, eventMetaId, FlowNodeStatus.PENDING));
            assertEquals(1, contexts2.size());
            verify(EVENT_PUBLISHER, times(1)).publishEvent(any());

            FlowData flowData3 = getFlowData(flowsExecuteFilterFromMToN(), "yxy");
            String traceId3 = from.offer(flowData3).getTraceId();
            List<FlowContext<FlowData>> contexts3 = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId3, eventMetaId, FlowNodeStatus.PENDING));
            assertEquals(1, contexts3.size());

            checkContext(contexts3, false, from);
            contexts3 = FlowsTestUtil.waitEmpty(
                    contextSupplier(REPO, streamId, traceId3, eventMetaId, FlowNodeStatus.PENDING));
            assertEquals(0, contexts3.size());
            verify(EVENT_PUBLISHER, times(1)).publishEvent(any());

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            List<FlowContext<FlowData>> endContexts = FlowsTestUtil.waitSize(
                    contextSupplier(REPO, streamId, traceId1, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED), 3);
            assertFlowsExecuteFilterFromMToN(REPO, traceId1, endContexts);
            assertFlowsExecuteFilterFromMToN(REPO, traceId2, endContexts);
            assertFlowsExecuteFilterFromMToN(REPO, traceId3, endContexts);
        }

        private void checkContext(List<FlowContext<FlowData>> contexts1, boolean isApproved, From<FlowData> from) {
            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext1 = contexts1.get(0);
            Blocks.Block<FlowData> block1 = from.getBlock(resumeContext1.getPosition());
            String toBatch1 = UUIDUtil.uuid();
            contexts1.forEach(c -> {
                c.getData().getBusinessData().put("approve", new HashMap<String, Boolean>() {
                    {
                        put("status", isApproved);
                    }
                });
                c.toBatch(toBatch1);
            });
            REPO.update(contexts1);
            block1.process(contexts1);
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @Disabled("目前发现测试用的H2内存数据库出现insert成功但是查不到的情况，后面某个用例给查出来了")
    @DisplayName("流程实例流转M_TO_N添加过滤器后按批次流转在数据库持久化的场景测试集合")
    class FlowAutoExecutorInPersistMToNWithFilterBatchSizeOne extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例m到n最小SIZE过滤器为1单线程offer的场景测试")
        void testFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread() {
            String jsonData = getJsonData(
                    getFilePath("flows_auto_general_jober_state_node_m_to_n_with_filter_batch_size_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            List<FlowData> list = new ArrayList<>();
            int m = 5;
            for (int j = 0; j < m; j++) {
                list.add(getFlowData(flowsExecuteFilterFromMToN(), "gsy"));
            }
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, PERSIST_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();

            List<Map<String, Object>> outputs = list.stream().map(f -> {
                Map<String, Object> result = new HashMap<>();
                Map<String, Object> businessData = f.getBusinessData();
                result.put("businessData", businessData);
                return result;
            }).collect(Collectors.toList());
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(ROUTER);
            when(ROUTER.route(any())).thenReturn(INVOKER);
            when(INVOKER.communicationType(any())).thenReturn(INVOKER);
            when(INVOKER.invoke(any())).thenReturn(outputs);

            String traceId = from.offer(list.get(0)).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(FlowNodeType.END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), FlowNodeStatus.ARCHIVED), m,
                MAX_WAIT_TIME_MS);
            Assertions.assertNotNull(contexts);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread(contexts, all);
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Test
    @DisplayName("测试判断没有达到最大重试次数")
    public void testIsNotMaxRetryCount() {
        when(FLOW_RETRY_REPO.getById(anyString())).thenReturn(null);

        boolean isMaxRetryCount = REPO.isMaxRetryCount("123");

        Assertions.assertFalse(isMaxRetryCount);
    }

    @Test
    @DisplayName("测试判断达到最大重试次数")
    public void testIsMaxRetryCount() {
        when(FLOW_RETRY_REPO.getById(anyString())).thenReturn(new FlowRetry(null, null, null, null, 10, 1));

        boolean isMaxRetryCount = REPO.isMaxRetryCount("123");

        Assertions.assertTrue(isMaxRetryCount);
    }
}
