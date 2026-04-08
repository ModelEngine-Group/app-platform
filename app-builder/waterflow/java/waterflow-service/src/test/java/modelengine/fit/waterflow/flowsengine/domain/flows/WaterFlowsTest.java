/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows;

import static modelengine.fit.waterflow.flowsengine.domain.flows.FlowsTestUtil.MAX_WAIT_TIME_MS;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.PENDING;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.READY;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.ParallelMode.EITHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.waterflow.MethodNameLoggerExtension;
import modelengine.fit.ohscript.util.UUIDUtil;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMemoMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMemoRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocksMemo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Blocks.FilterBlock;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Blocks.ValidatorBlock;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Node;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * FlowsTest
 * 流程流转框架测试类
 *
 * @author 高诗意
 * @since 2023/07/18
 */
@ExtendWith(MethodNameLoggerExtension.class)
@DisplayName("流程实例核心引擎原始测试用例集合")
class WaterFlowsTest {
    static class TestData {
        private int f = 0;

        private int s = 0;

        private int t = 0;

        /**
         * TestData
         */
        public TestData() {
        }

        /**
         * TestData
         *
         * @param f first
         * @param s second
         * @param t third
         */
        public TestData(int f, int s, int t) {
            this.f = f;
            this.s = s;
            this.t = t;
        }

        TestData first(int n) {
            this.f = n;
            return this;
        }

        TestData second(int n) {
            this.s = n;
            return this;
        }

        TestData third(int n) {
            this.t = n;
            return this;
        }

        /**
         * 总数
         *
         * @return 总数
         */
        public int total() {
            return f + s + t;
        }
    }

    @Nested
    @DisplayName("流程流转引擎基础测试用例集合")
    class WaterFlowBaseLineTest {
        private FlowContextRepo repo;

        private FlowContextMessenger messenger;

        private FlowLocks locks;

        @BeforeEach
        void setUp() {
            messenger = new FlowContextMemoMessenger();
            locks = new FlowLocksMemo();
            repo = new FlowContextMemoRepo();
        }

        @Test
        @DisplayName("流程实例map节点流转逻辑")
        void testFitStreamMapComputation() {
            long[] data = {1, 0};
            Flows.ProcessFlow<Integer> flowTest = Flows.<Integer>create(repo, messenger, locks)
                    .id("flow test")
                    .map(i -> i + data[0]++)
                    .map(i -> i + data[0]++)
                    .close(r -> data[1] = r.get().getData());
            flowTest.offer(1);

            FlowsTestUtil.waitUntil(() -> data[1] != 0, MAX_WAIT_TIME_MS);
            assertEquals("flow test", flowTest.getId());
            assertEquals(3, data[0]);
            assertEquals(4, data[1]);

            List<String> result = new ArrayList<>();
            Flows.<Integer>create(repo, messenger, locks)
                    .map(i -> i + 1)
                    .map(i -> i * 2)
                    .map(i -> i.toString() + "-okay")
                    .close(r -> {
                        result.clear();
                        result.addAll(r.getAll().stream().map(FlowContext::getData).collect(Collectors.toList()));
                    })
                    .offer(new Integer[] {1, 2, 3, 4, 5});
            FlowsTestUtil.waitSize(() -> result, 5);
            assertTrue(result.contains("12-okay"));
        }

        @Test
        @Disabled("目前block会释放trace锁，有机制冲突, 导致运行中出现中断，用例无法稳定运行")
        @DisplayName("流程实例map节点结合block节点流转逻辑")
        void testFitStreamMapComputationWithBlock() {
            List<String> result = new ArrayList<>();
            AtomicInteger gate = new AtomicInteger(4);
            ValidatorBlock<Integer> block = new ValidatorBlock<>((i, all) -> i.getData() > gate.get());
            Activities.State<Integer, Integer, Integer, Flows.ProcessFlow<Integer>>
                    mapper = Flows.<Integer>create(repo, messenger,
                    locks).map(i -> i + 1);
            String traceId = mapper.map(i -> i * 2).block(block).map(i -> i.toString() + "-okay").close(r -> {
                result.clear();
                result.addAll(r.getAll().stream().map(FlowContext::getData).collect(Collectors.toList()));
            }).offer(new Integer[] {1, 2, 3, 4, 5}).getTraceId();
            // block住了，流程停在map(i->i*2)处
            List<FlowContext<Integer>> contexts = FlowsTestUtil.waitSize(
                    contextSupplier(repo, traceId, mapper.getSubscriptionsId().get(0), PENDING), 5);
            Assertions.assertEquals(0, result.size());

            String toBatch = UUIDUtil.uuid();
            contexts.forEach(c -> c.toBatch(toBatch));
            repo.updateFlowDataAndToBatch(contexts);

            // 由于block有validator，需要value>4，只有2个元素通过验证
            // block没有允许忽略transaction，所以不会触发close事件
            block.process(contexts);
            FlowsTestUtil.waitSize(() -> result, 2);
            // 没有全部执行完
            assertEquals(2, result.size());
            // 设置忽略transaction，输入是5个数，满足条件的是3个,但有2个已经处理掉
            gate.compareAndSet(4, 3);
            block.process(contexts);
            FlowsTestUtil.waitSingle(() -> result);
            assertEquals(1, result.size());
            // 条件重新设置，最后两个数字满足条件
            gate.compareAndSet(3, 0);
            block.process(contexts);
            FlowsTestUtil.waitSize(() -> result, 2);
            assertEquals(2, result.size());
            assertTrue(result.contains("4-okay"));
        }

        @Test
        @DisplayName("流程实例produce节点流转逻辑")
        void testFitStreamProduceComputation() {
            List<String> result = new ArrayList<>();
            Flows.<Integer>create(repo, messenger, locks)
                    .map((i -> i - 1))
                    .produce(l -> l.stream()
                            .sorted()
                            .limit(2)
                            .map(i -> i.toString() + "-okay")
                            .collect(Collectors.toList()))
                    .close(i -> result.addAll(
                            i.getAll().stream().map(FlowContext::getData).collect(Collectors.toList())))
                    .offer(new Integer[] {1, 2, 3, 4, 5});

            FlowsTestUtil.waitSize(() -> result, 2);
            assertEquals(2, result.size());
        }

        @Test
        @Disabled("目前block会释放trace锁，有机制冲突, 导致运行中出现中断，用例无法稳定运行")
        @DisplayName("流程实例produce节点结合block节点流转逻辑")
        void testFitStreamProduceComputationWithBlock() {
            List<String> result = new ArrayList<>();
            FilterBlock<Integer> block = new FilterBlock<>();
            Activities.Start<Integer, Integer, Flows.ProcessFlow<Integer>> start = Flows.create(repo, messenger, locks);
            String traceId = start.produce(
                            l -> l.stream().limit(2).map(i -> i.toString() + "-okay").collect(Collectors.toList()))
                    .block(block)
                    .map((i -> i + "-map"))
                    .close(r -> {
                        result.addAll(r.getAll().stream().map(FlowContext::getData).collect(Collectors.toList()));
                    })
                    .offer(new Integer[] {1, 2, 3, 4, 5})
                    .getTraceId();
            List<FlowContext<Integer>> contexts = FlowsTestUtil.waitSize(
                    contextSupplier(repo, traceId, start.getSubscriptionsId().get(0), PENDING), 5);
            Assertions.assertEquals(0, result.size());

            String toBatch = UUIDUtil.uuid();
            contexts.forEach(c -> c.toBatch(toBatch));
            repo.updateFlowDataAndToBatch(contexts);
            block.process(contexts);
            FlowsTestUtil.waitSize(() -> result, 2);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("流程实例reduce节点流转逻辑")
        void testFitStreamReduceComputation() {
            Integer[] data = {0};
            Flows.<Integer>create(repo, messenger, locks)
                    .map(i -> i * 2)
                    .reduce(list -> list.stream().reduce(Integer::sum).orElse(0))
                    .close(r -> data[0] = r.get().getData())
                    .offer(new Integer[] {1, 2, 3, 4, 5});
            FlowsTestUtil.waitUntil(() -> data[0] != 0, MAX_WAIT_TIME_MS);
            assertEquals(30, (long) data[0]);
        }

        @Test
        @DisplayName("流程实例parallel节点以及fork节点以及join节点all模式流转逻辑")
        void testFitStreamWithForkJoinAll() {
            TestData input = new TestData();
            List<TestData> output1 = new ArrayList<>();
            Flows.<TestData>create(repo, messenger, locks)
                    // f=1;s=50;t=100
                    .just(i -> i.first(1).second(50).third(100))
                    // f=0;s=50;t=100
                    .parallel(i -> i.f--)
                    // f=4;s=50;t=100
                    .fork(i -> i.f += 4)
                    // f=4;s=49;t=100
                    .fork(i -> i.s--)
                    // f=4;s=54;t=100
                    .just(i -> i.s += 5)
                    // f=4;s=54;t=102
                    .fork(i -> i.t += 2)
                    .join(data -> data.get(0))
                    .close(r -> output1.add(r.get().getData()))
                    .offer(input);
            FlowsTestUtil.waitSingle(() -> output1);
            assertEquals(160, output1.get(0).total());

            List<Integer> output2 = new ArrayList<>();
            Flows.<TestData>create(repo, messenger, locks)
                    // f=0;s=50;t=100
                    .parallel()
                    // f=1;s=50;t=100
                    .fork(i -> i.first(1))
                    // f=1;s=2;t=100
                    .fork(i -> i.second(2))
                    // f=1;s=2;t=3
                    .fork(i -> i.third(3))
                    .join(data -> data.get(0))
                    .close(r -> output2.add((r.get().getData()).total()))
                    .offer(input);
            FlowsTestUtil.waitSingle(() -> output2);
            assertEquals(6, output2.get(0));
        }

        @Test
        @Disabled("目前block会释放trace锁，有机制冲突, 导致运行中出现中断，用例无法稳定运行")
        @DisplayName("流程实例parallel节点以及fork节点以及join节点either模式流转逻辑")
        void testFitStreamWithForkJoinEither() {
            TestData input = new TestData();
            List<TestData> output = new ArrayList<>();
            ValidatorBlock<TestData> block = new ValidatorBlock<>();
            Flows.<TestData>create(repo, messenger, locks)
                    .parallel(EITHER)
                    .fork(i -> i.first(100))
                    .fork(i -> i.second(200))
                    .block(block)
                    .join()
                    .close(r -> output.add(r.get().getData()))
                    .offer(input);
            FlowsTestUtil.waitSingle(() -> output);
            assertEquals(100, output.get(0).f);
            assertEquals(0, output.get(0).s);
            block.process(new ArrayList<>());
            // either 已经失效，所以这个block没用
            assertEquals(0, output.get(0).s);
        }

        @Test
        @DisplayName("流程实例condition节点以及match节点以及others节点流转逻辑")
        void testFitStreamWithCondition() {
            List<TestData> output = new ArrayList<>();
            // test conditions and others for just
            Flows.ProcessFlow<TestData> flow = Flows.<TestData>create(repo, messenger, locks)
                    .conditions()
                    .match(i -> i.getData().f > 10)
                    .just(i -> i.f++)
                    .match(i -> i.getData().s > 10)
                    .map(i -> {
                        i.s++;
                        return i;
                    })
                    .others(i -> {
                        i.t++;
                        return i;
                    })
                    .close(r -> output.add(r.get().getData()));
            TestData input = new TestData();
            flow.offer(input.first(11).second(0).third(0));
            FlowsTestUtil.waitSingle(() -> output);
            assertTestData(new TestData(12, 0, 0), output);
            output.clear();

            flow.offer(input.first(0).second(11).third(0));
            FlowsTestUtil.waitSingle(() -> output);
            assertTestData(new TestData(0, 12, 0), output);
            output.clear();

            flow.offer(input.first(0).second(0).third(11));
            FlowsTestUtil.waitSingle(() -> output);
            assertTestData(new TestData(0, 0, 12), output);

            // test when and others for map
            output.clear();
            flow = Flows.<TestData>create(repo, messenger, locks).when(i -> {
                i.f++;
                return i;
            }).map(i -> {
                i.f--;
                return i;
            }).conditions(i -> i.t++).match(i -> i.getData().f > 10).just(i -> i.f++).others(i -> {
                i.t++;
                output.add(i);
                return i;
            }).close();
            flow.offer(input.first(0).second(0).third(11));
            FlowsTestUtil.waitSingle(() -> output);
            assertTestData(new TestData(0, 0, 13), output);
        }

        private void assertTestData(TestData expected, List<TestData> output) {
            assertEquals(expected.f, output.get(0).f);
            assertEquals(expected.s, output.get(0).s);
            assertEquals(expected.t, output.get(0).t);
        }

        @Test
        @DisplayName("流程实例when节点flux模式流转逻辑")
        void testFitStreamAllWhen() {
            AtomicReference<TestData> result = new AtomicReference<>();
            Flows.flux(() -> new Integer[] {10, 20, 30})
                    .when(Object::toString)
                    .produce(list -> {
                        List<Integer> product = new ArrayList<>();
                        list.forEach(i -> product.add(Integer.valueOf(i)));
                        product.add(40);
                        return product;
                    })
                    .when(Object::toString)
                    .map(i -> i + ":okay")
                    .when(i -> Integer.valueOf(i.split(":")[0]))
                    .reduce(list -> list.stream().mapToInt(i -> i).sum())
                    .when(i -> {
                        TestData data = new TestData();
                        data.first(i);
                        return data;
                    })
                    .just(d -> d.second(200))
                    .close(r -> result.set(r.get().getData()))
                    .offer();
            FlowsTestUtil.waitUntil(() -> result.get() != null, MAX_WAIT_TIME_MS);
            assertEquals(100, result.get().f);
            assertEquals(200, result.get().s);
        }

        @Test
        @DisplayName("流程实例just节点mono模式流转逻辑")
        void testFitStreamCreateFromSingleDataProducer() {
            List<TestData> output = new ArrayList<>();
            Flows.mono(() -> new TestData().first(10).second(10).third(10))
                    .just(i -> i.f++)
                    .just(output::add)
                    .close()
                    .offer();
            FlowsTestUtil.waitSingle(() -> output);
            assertEquals(11, output.get(0).f);
        }

        @Test
        @DisplayName("流程实例reduce节点flux模式流转逻辑")
        void testFitStreamCreateFromListDataProducer() {
            Integer[] input = new Integer[] {1, 2, 7};
            Integer[] output = {0};
            Flows.flux(() -> input)
                    .reduce(list -> list.stream().reduce(Integer::sum).get())
                    .close(r -> output[0] = r.get().getData())
                    .offer();
            FlowsTestUtil.waitUntil(() -> output[0] == 10, MAX_WAIT_TIME_MS);
            assertEquals(10, (long) output[0]);
        }

        @Test
        @Disabled(
                "流程之间跳转存在两个问题：1、版本streamId不一样，导致跳转到目标流程根据版本查不到数据；2、跳转的节点存在多个to，并发无法控制")
        @DisplayName("流程实例just节点以及alias节点流转逻辑")
        void testFitStreamCrossStream() {
            AtomicBoolean f1Result = new AtomicBoolean(false);
            AtomicInteger f3Result = new AtomicInteger(0);
            // Flow 1
            Activities.AtomicStateAlias<TestData, TestData> act1 = new Activities.AtomicStateAlias<>();
            Activities.AtomicStateAlias<TestData, TestData> act2 = new Activities.AtomicStateAlias<>();
            // 設置別名，為其他flow引用
            Flows.<TestData>create(repo, messenger, locks)
                    .just(d -> d.t++)
                    .alias(act1::set)
                    .just(d -> d.f++)
                    .alias(act2::set)
                    .just(d -> d.s++)
                    .close(r -> {
                        f1Result.set(true);
                        f3Result.set(r.get().getData().total());
                    });

            // Flow 2
            AtomicInteger f2Result = new AtomicInteger(0);
            Activities.AtomicStateAlias<Integer, Integer> act3 = new Activities.AtomicStateAlias<>();
            Flows.ProcessFlow<Integer> flow2 = Flows.<Integer>create(repo, messenger, locks)
                    .when(i -> new TestData().first(i))
                    .to(act1.get())
                    .when(i -> i, i -> false)
                    .map(i -> i + 100)
                    .alias(w -> act3.set(w))
                    .close(r -> f2Result.set(r.get().getData()));

            // begin to offer data without act2 come back
            flow2.offer(100);
            FlowsTestUtil.waitMillis(Collections::emptyList, 50);
            assertTrue(f1Result.get());
            assertEquals(0, f2Result.get());
            assertEquals(103, f3Result.get());

            // offer data with act2 come back
            f1Result.set(false);
            // send the data back
            act2.get().to(act3.get(), d -> d.f + d.t);
            flow2.offer(100);
            FlowsTestUtil.waitFortyMillis(Collections::emptyList);
            assertFalse(f1Result.get());
            assertEquals(202, f2Result.get());
        }

        @Test
        @Disabled(
                "流程之间跳转存在两个问题：1、版本streamId不一样，导致跳转到目标流程根据版本查不到数据；2、跳转的节点存在多个to，并发无法控制")
        @DisplayName("流程实例垮流程节点流转逻辑")
        void testFitStreamNotifyCrossStream() {
            AtomicInteger f1Result = new AtomicInteger(0);
            AtomicInteger f2Result = new AtomicInteger(0);
            // Flow 1
            Activities.AtomicStateAlias<TestData, TestData> act1 = new Activities.AtomicStateAlias<>();
            // 設置別名，為其他flow引用
            Flows.<TestData>create(repo, messenger, locks)
                    .just(d -> d.t++)
                    .alias(act1::set)
                    .just(d -> d.f++)
                    .just(d -> d.s++)
                    .close(r -> f1Result.set(r.get().getData().total()));

            // Flow 2
            // act1.get()的节点在其自身流程中有一个to节点(just(d -> d.f++))
            // 在跳转流程中有一个to节点(close(r -> f2Result.set(r.get())))
            // 并发会导致一个context同时发送到两个节点处理，中间状态错乱
            // when.to的方式同时只允许一条边是满足条件的，以下用例不正确
            Flows.<Integer>create(repo, messenger, locks)
                    .when(i -> new TestData().first(i))
                    .to(act1.get())
                    .when(i -> i, i -> true)
                    .map(i -> i + 100)
                    .close(r -> f2Result.set(r.get().getData()))
                    .offer(100);

            FlowsTestUtil.waitFortyMillis(Collections::emptyList);
            assertEquals(103, f1Result.get());
            assertEquals(200, f2Result.get());
        }

        @Test
        @DisplayName("流程实例异常处理流转逻辑")
        void testExceptionHandleForFitStream() {
            AtomicReference<TestData> output = new AtomicReference<>();
            // 单节点错误处理
            Flows.<TestData>create(repo, messenger, locks).just(t -> t.first(100)).just(t -> {
                if (t.f < 120) {
                    throw new RuntimeException();
                } else {
                    t.second(100);
                }
            }).error((e, b, d) -> {
                d.get(0).getData().first(120);
                d.forEach(c -> c.setStatus(READY));
                b.retry(e, d);
            }).close(r -> output.set(r.get().getData())).offer(new TestData());
            FlowsTestUtil.waitUntil(() -> output.get() != null, MAX_WAIT_TIME_MS);
            assertEquals(120, output.get().f);
            assertEquals(100, output.get().s);

            output.set(null);
            // 整体错误处理
            Flows.<TestData>create(repo, messenger, locks).just(t -> t.first(100)).just(t -> {
                if (t.f < 120) {
                    throw new RuntimeException();
                } else {
                    t.second(100);
                }
            }).close(r -> output.set(r.get().getData()), (e, b, d) -> {
                FlowContext context = d.get(0);
                ObjectUtils.<TestData>cast(context.getData()).first(120);
                d.forEach(c -> c.setStatus(READY));
                b.retry(e, d);
            }).offer(new TestData());
            FlowsTestUtil.waitUntil(() -> output.get() != null, MAX_WAIT_TIME_MS);
            assertEquals(120, output.get().f);
            assertEquals(100, output.get().s);
        }

        @Test
        void test_flow_subscribe_flow() {
            AtomicInteger out1 = new AtomicInteger();
            // 第一条流:start->node1->end
            Flows.ProcessFlow<Integer> flow1 = Flows.<Integer>create(repo, messenger, locks)
                    .map(Object::toString)
                    .close();
            // 第二条流:start->node->handle_node->end
            Flows.ProcessFlow<String> flow2 = Flows.<String>create(repo, messenger, locks)
                    .map(s -> s + "00")
                    .id("sender")
                    .map(s -> Integer.parseInt(s) + 100)
                    .close(r -> out1.set(r.get().getData()));
            // 从中间节点offer数据flow1.end节点的数据
            flow2.offer("sender", flow1);
            flow1.offer(100);
            FlowsTestUtil.waitUntil(() -> out1.get() != 0, MAX_WAIT_TIME_MS);
            assertEquals(200, out1.get());

            AtomicReference<String> out2 = new AtomicReference<>();
            AtomicReference<String> out3 = new AtomicReference<>();
            Activities.State<Integer, Integer, Integer, Flows.ProcessFlow<Integer>>
                    node1 = Flows.<Integer>create().map(i -> i + 100);
            flow1 = node1.just(i -> out2.set(i.toString() + "flow1")).close();
            Flows.<Integer>create(repo, messenger, locks)
                    .just(i -> out3.set(i.toString() + "flow2"))
                    .close()
                    .offer(node1);
            flow1.offer(100);
            FlowsTestUtil.waitUntil(() -> out2.get() != null && out3.get() != null, MAX_WAIT_TIME_MS);
            assertEquals("200flow1", out2.get());
            assertEquals("200flow2", out3.get());
        }

        @Test
        void test_flow_flat_map() {
            List<String> result = new ArrayList<>();
            Flows.<Integer>create(repo, messenger, locks).flatMap(num -> {
                List<String> maps = new ArrayList<>();
                for (int i = 0; i < num; i++) {
                    maps.add("flat map " + i);
                }
                return maps;
            }).just(c -> {
                synchronized (result) {
                    result.add(c);
                }
            }).close().offer(4);
            FlowsTestUtil.waitUntil(() -> result.size() == 4, MAX_WAIT_TIME_MS);
            assertEquals(4, result.size());
        }

        @Test
        @DisplayName("流程实例节点 Merger 合并多输入数据逻辑")
        void testNodeMergerComputation() {
            AtomicReference<List<Integer>> result = new AtomicReference<>();

            // 创建 Reduce 类型节点，使用自定义 Merger 合并多输入
            Node<Integer, Integer> mergeNode = new Node<>(
                    "test-merger-flow",
                    (List<FlowContext<Integer>> inputs) -> {
                        // processor 接收的是 Merger 合并后的单条数据
                        result.set(inputs.stream()
                                .map(FlowContext::getData)
                                .collect(Collectors.toList()));
                        return inputs.get(0).getData();
                    },
                    repo, messenger, locks,
                    (List<FlowContext<Integer>> contexts) -> {
                        if (contexts == null || contexts.isEmpty()) {
                            return null;
                        }
                        int mergedValue = contexts.stream()
                                .mapToInt(c -> c.getData())
                                .sum();
                        return contexts.get(0).convertData(mergedValue, contexts.get(0).getId());
                    }
            );
            mergeNode.setFanInMode(modelengine.fit.waterflow.flowsengine.domain.flows.streams.To.FanInMode.ALL);

            // 创建结束节点
            Node<Integer, Integer> endNode = new Node<>(
                    "test-merger-flow",
                    (List<FlowContext<Integer>> inputs) -> inputs.get(0).getData(),
                    repo, messenger, locks
            );

            // 构建拓扑: merge -> end
            mergeNode.subscribe(endNode);

            // 模拟两个分支同时发送数据到 mergeNode
            mergeNode.offer(10);
            mergeNode.offer(20);

            // 等待合并结果 (10 + 20 = 30)
            FlowsTestUtil.waitUntil(() -> !result.get().isEmpty(), MAX_WAIT_TIME_MS);
            assertEquals(1, result.get().size());
            assertEquals(30, result.get().get(0));
        }

        @Test
        @DisplayName("流程实例自定义 Merger Lambda 实现数据合并")
        void testCustomMergerLambda() {
            AtomicReference<TestData> result = new AtomicReference<>();

            Processors.Merger<TestData> customMerger = (List<FlowContext<TestData>> contexts) -> {
                if (contexts == null || contexts.isEmpty()) {
                    return null;
                }
                TestData merged = new TestData();
                for (FlowContext<TestData> ctx : contexts) {
                    TestData data = ctx.getData();
                    merged.first(merged.f + data.f);
                    merged.second(merged.s + data.s);
                    merged.third(merged.t + data.t);
                }
                return contexts.get(0).convertData(merged, contexts.get(0).getId());
            };

            // 创建 Reduce 类型节点用于合并
            Node<TestData, TestData> mergeNode = new Node<>(
                    "custom-merger-test",
                    (List<FlowContext<TestData>> inputs) -> {
                        result.set(inputs.get(0).getData());
                        return inputs.get(0).getData();
                    },
                    repo, messenger, locks,
                    customMerger
            );
            mergeNode.setFanInMode(modelengine.fit.waterflow.flowsengine.domain.flows.streams.To.FanInMode.ALL);

            Node<TestData, TestData> endNode = new Node<>(
                    "custom-merger-test",
                    (List<FlowContext<TestData>> inputs) -> inputs.get(0).getData(),
                    repo, messenger, locks
            );

            mergeNode.subscribe(endNode);

            // 发送两条数据到合并节点
            mergeNode.offer(new TestData(1, 2, 3));
            mergeNode.offer(new TestData(4, 5, 6));

            FlowsTestUtil.waitUntil(() -> result.get() != null, MAX_WAIT_TIME_MS);
            assertEquals(5, result.get().f);  // 1 + 4
            assertEquals(7, result.get().s);  // 2 + 5
            assertEquals(9, result.get().t);  // 3 + 6
        }

        @Test
        @DisplayName("流程实例无 Merger 时节点正常流转")
        void testNodeWithoutMerger() {
            List<Integer> result = new ArrayList<>();

            Node<Integer, Integer> node = new Node<>(
                    "no-merger-test",
                    (List<FlowContext<Integer>> inputs) -> inputs.get(0).getData() * 2,
                    repo, messenger, locks
            );

            Node<Integer, Integer> endNode = new Node<>(
                    "no-merger-test",
                    (List<FlowContext<Integer>> inputs) -> {
                        result.add(inputs.get(0).getData());
                        return inputs.get(0).getData();
                    },
                    repo, messenger, locks
            );

            node.subscribe(endNode);

            node.offer(1);
            node.offer(2);

            FlowsTestUtil.waitUntil(() -> result.size() == 2, MAX_WAIT_TIME_MS);
            assertEquals(2, result.size());
            assertTrue(result.contains(2));
            assertTrue(result.contains(4));
        }

        private <T> Supplier<List<FlowContext<T>>> contextSupplier(FlowContextRepo<T> repo, String traceId,
                String metaId, FlowNodeStatus status) {
            return () -> {
                List<FlowContext<T>> all = repo.getContextsByTrace(traceId);
                return all.stream()
                        .filter(c -> c.getPosition().equals(metaId))
                        .filter(c -> c.getStatus() == status)
                        .collect(Collectors.toList());
            };
        }
    }
}