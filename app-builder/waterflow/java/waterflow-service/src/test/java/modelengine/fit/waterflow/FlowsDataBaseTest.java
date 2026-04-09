/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_ENGINE_EXECUTOR_ERROR;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.ARCHIVED;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.ERROR;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.PENDING;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.RETRYABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowCacheService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMemoRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream.Publisher;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowRetryPO;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 从json文件中获取流程定义的基础类
 *
 * @author 高诗意
 * @since 2023/08/22
 */
public abstract class FlowsDataBaseTest {
    private static final Logger log = Logger.get(FlowsDataBaseTest.class);

    /**
     * 失败节点1
     */
    protected final String stateError1 = "state-error1";

    /**
     * 失败节点2
     */
    protected final String stateError2 = "state-error2";

    /**
     * 失败条件1
     */
    protected final String conditionError1 = "condition-error1";

    /**
     * 线程数
     */
    protected final int threadNum = 2;

    @AfterAll
    static void cleanPublisher() {
        FlowCacheService.clear();
    }

    @AfterEach
    void tearDown() {
        FlowCacheService.clear();
    }

    /**
     * contextSupplier
     *
     * @param repo repo
     * @param streamId streamId
     * @param traceId traceId
     * @param metaId metaId
     * @param status status
     * @return Supplier<List < FlowContext < FlowData>>>
     */
    protected Supplier<List<FlowContext<FlowData>>> contextSupplier(FlowContextRepo<FlowData> repo, String streamId,
            String traceId, String metaId, FlowNodeStatus status) {
        return () -> {
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(repo, traceId);
            return all.stream()
                    .filter(c -> c.getStreamId().equals(streamId))
                    .filter(c -> c.getPosition().equals(metaId))
                    .filter(c -> c.getStatus() == status)
                    .collect(Collectors.toList());
        };
    }

    /**
     * getSentContextSupplier for manual task
     *
     * @param repo repo
     * @param streamId streamId
     * @param traceId traceId
     * @param metaId metaId
     * @param status status
     * @return Supplier<List < FlowContext < FlowData>>>
     */
    protected Supplier<List<FlowContext<FlowData>>> getSentContextSupplier(FlowContextRepo<FlowData> repo,
            String streamId, String traceId, String metaId, FlowNodeStatus status) {
        return () -> {
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(repo, traceId);
            return all.stream()
                .filter(c -> c.getStreamId().equals(streamId))
                .filter(c -> c.getPosition().equals(metaId))
                .filter(c -> c.getStatus() == status)
                .filter(c -> c.isSent())
                .collect(Collectors.toList());
        };
    }

    /**
     * contextSupplier
     *
     * @param repo repo
     * @param traceId traceId
     * @param metaId metaId
     * @param status status
     * @return Supplier<List < FlowContext < FlowData>>>
     */
    protected Supplier<List<FlowContext<FlowData>>> contextSupplier(FlowContextRepo<FlowData> repo, String traceId,
            String metaId, FlowNodeStatus status) {
        return () -> {
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(repo, traceId);
            return all.stream()
                    .filter(c -> c.getPosition().equals(metaId))
                    .filter(c -> c.getStatus() == status)
                    .collect(Collectors.toList());
        };
    }

    /**
     * getContextsByTraceWrapper
     *
     * @param repo repo
     * @param traceId traceId
     * @return List<FlowContext < FlowData>>
     */
    protected List<FlowContext<FlowData>> getContextsByTraceWrapper(FlowContextRepo<FlowData> repo, String traceId) {
        if (repo instanceof FlowContextMemoRepo) {
            return repo.getContextsByTrace(traceId);
        }
        return repo.getContextsByTrace(traceId)
            .stream()
            .map(c -> {
                FlowContext<FlowData> result = c.convertData(FlowData.parseFromJson(ObjectUtils.cast(c.getData())),
                    c.getId());
                result.setSent(c.isSent());
                return result;
            }).collect(Collectors.toList());
    }

    /**
     * 获取流程定义解析的json数据
     *
     * @param fileName 流程定义的json文件
     * @return 流程定义的json字符串
     */
    protected String getJsonData(String fileName) {
        try (InputStream in = IoUtils.resource(FlowsDataBaseTest.class.getClassLoader(), fileName)) {
            return new String(IoUtils.read(in), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    /**
     * getThreads
     *
     * @param latch latch
     * @param failTimes failTimes
     * @param runnable runnable
     * @return Thread集合
     */
    protected List<Thread> getThreads(CountDownLatch latch, AtomicInteger failTimes, Runnable runnable) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            Thread thread = new Thread(() -> {
                try {
                    runnable.run();
                } catch (Throwable e) {
                    failTimes.getAndIncrement();
                    fail("Fail executor in thread! fail message: " + e);
                } finally {
                    latch.countDown();
                }
            });
            thread.setUncaughtExceptionHandler((tr, ex) -> log.error(tr.getName() + " : " + ex.getMessage()));
            threads.add(thread);
        }
        return threads;
    }

    /**
     * 获取文件路径
     *
     * @param fileName 流程定义的json文件名
     * @return 流程定义的json文件路径
     */
    protected String getFilePath(String fileName) {
        return getFilePathPrefix() + fileName;
    }

    /**
     * 获取文件路径前缀
     *
     * @return 文件路径前缀
     */
    protected abstract String getFilePathPrefix();

    /**
     * getFlowData
     *
     * @param businessData businessData
     * @param operator operator
     * @return FlowData
     */
    protected FlowData getFlowData(Map<String, Object> businessData, String operator) {
        return FlowData.builder()
                .operator(operator)
                .startTime(LocalDateTime.now())
                .businessData(businessData)
                .contextData(new HashMap<>())
                .build();
    }

    /**
     * flowsExecutorWithOnlyStateNode1To1
     *
     * @return businessData
     */
    protected Map<String, Object> flowsExecutorWithOnlyStateNode1To1() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("cudehubUser", "user");
        businessData.put("cudehubBranch", "branch");
        businessData.put("libingStatus", "status");
        businessData.put("cudehubTag", "tag");
        businessData.put("libingId", "id");
        businessData.put("application", "framework");
        return businessData;
    }

    /**
     * flowsExecutorWithConditionNodeFirstBranchTrue
     *
     * @return businessData
     */
    protected Map<String, Object> flowsExecutorWithConditionNodeFirstBranchTrue() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("cmc", new HashMap<String, Boolean>() {
            {
                put("approved", true);
            }
        });
        businessData.put("committer", new HashMap<String, Boolean>() {
            {
                put("approved", true);
            }
        });
        businessData.put("approvedResult", "success");
        businessData.put("application", "framework");
        return businessData;
    }

    /**
     * flowsManualExecutorWithConditionNodeFirstBranchTrue
     *
     * @return businessData
     */
    protected Map<String, Object> flowsManualExecutorWithConditionNodeFirstBranchTrue() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approvedResult", "success");
        businessData.put("application", "framework");
        return businessData;
    }

    /**
     * flowsManualExecutorWithConditionNodeCircle
     *
     * @return businessData
     */
    protected Map<String, Object> flowsManualExecutorWithConditionNodeCircle() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approved.result", "success");
        businessData.put("application", "framework");
        return businessData;
    }

    /**
     * flowsExecuteProduceFromMToNForOfferOneData
     *
     * @param approved approved
     * @return businessData
     */
    protected Map<String, Object> flowsExecuteProduceFromMToNForOfferOneData(boolean approved) {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approvedResult", "success");
        businessData.put("approved", approved);
        businessData.put("application", "framework");
        return businessData;
    }

    /**
     * flowsExecuteFilterFromMToN
     *
     * @return businessData
     */
    protected Map<String, Object> flowsExecuteFilterFromMToN() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approvedResult", "success");
        businessData.put("application", "framework");
        return businessData;
    }

    /**
     * errorMessage
     *
     * @param streamId streamId
     * @param metaId metaId
     * @param name name
     * @return 错误信息
     */
    protected String errorMessage(String streamId, String metaId, String name) {
        return MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), streamId, metaId, name,
                WaterflowException.class.getSimpleName(), String.format(
                        "execute jober failed, jober name: %s, jober type: ECHO_JOBER, fitables: [], errors: null",
                        name));
    }

    protected String errorMessage(String streamId, String metaId, String name, Exception exception) {
        return MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), streamId, metaId, name,
                exception.getClass().getSimpleName(),
                Optional.ofNullable(exception.getMessage()).orElse("internal error"));
    }

    /**
     * assertSingleInstance
     *
     * @param existInstance existInstance
     * @param newInstance newInstance
     */
    protected void assertSingleInstance(Publisher<FlowData> existInstance, Publisher<FlowData> newInstance) {
        assertEquals(System.identityHashCode(existInstance), System.identityHashCode(newInstance));
    }

    /**
     * assertFlowsExecutorWithOnlyStateNode1To1
     *
     * @param flowData flowData
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecutorWithOnlyStateNode1To1(FlowData flowData, List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(4, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));

        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals("hello: echo: user", resultBusinessData.get("cudehubUser"));
        assertEquals("hello: echo: branch", resultBusinessData.get("cudehubBranch"));
        assertEquals("echo: status", resultBusinessData.get("libingStatus"));
        assertEquals("hello: echo: tag", resultBusinessData.get("cudehubTag"));
        assertEquals("echo: id", resultBusinessData.get("libingId"));
    }

    /**
     * assertFlowsExecutorStateNodeWithError
     *
     * @param flowDefinition flowDefinition
     * @param metaId metaId
     * @param contexts contexts
     * @param allContexts allContexts
     * @param expectedNodeNumber expectedNodeNumber
     */
    protected void assertFlowsExecutorStateNodeWithError(FlowDefinition flowDefinition, String metaId,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts, int expectedNodeNumber) {
        FlowNode flowNode = flowDefinition.getFlowNode(metaId);
        assertEquals(1, contexts.size());
        assertEquals(ERROR, contexts.get(0).getStatus());
        assertEquals(errorMessage(flowDefinition.getStreamId(), metaId, flowNode.getName()).substring(0, 20),
                contexts.get(0).getData().getErrorMessage().substring(0, 20));
        assertEquals(expectedNodeNumber, allContexts.size());
    }

    protected void assertFlowsExecutorStateNodeWithException(FlowDefinition flowDefinition, String metaId,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts, int expectedNodeNumber,
            Exception exception) {
        FlowNode flowNode = flowDefinition.getFlowNode(metaId);
        assertEquals(1, contexts.size());
        assertEquals(ERROR, contexts.get(0).getStatus());
        assertEquals(errorMessage(flowDefinition.getStreamId(), metaId, flowNode.getName(), exception),
                contexts.get(0).getData().getErrorMessage());
        assertEquals(expectedNodeNumber, allContexts.size());
    }

    /**
     * assertFlowsExecutorWithConditionNodeFirstBranchTrue
     *
     * @param flowData flowData
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecutorWithConditionNodeFirstBranchTrue(FlowData flowData,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(6, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertTrue((boolean) getBusinessDataFromChainedKey(resultBusinessData, "cmc.approved"));
        assertTrue((boolean) getBusinessDataFromChainedKey(resultBusinessData, "committer.approved"));
        assertEquals("state2: state1: success", resultBusinessData.get("approvedResult"));
    }

    /**
     * assertFlowsExecutorWithConditionNodeFirstFalseBranch
     *
     * @param flowData flowData
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecutorWithConditionNodeFirstFalseBranch(FlowData flowData,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(3, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertFalse((boolean) getBusinessDataFromChainedKey(resultBusinessData, "cmc.approved"));
        assertTrue((boolean) getBusinessDataFromChainedKey(resultBusinessData, "committer.approved"));
        assertEquals("success", resultBusinessData.get("approvedResult"));
    }

    /**
     * assertFlowsExecutorWithConditionNodeSecondFalseBranch
     *
     * @param flowData flowData
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecutorWithConditionNodeSecondFalseBranch(FlowData flowData,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(5, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));

        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertTrue((boolean) getBusinessDataFromChainedKey(resultBusinessData, "cmc.approved"));
        assertFalse((boolean) getBusinessDataFromChainedKey(resultBusinessData, "committer.approved"));
        assertEquals("state1: success", resultBusinessData.get("approvedResult"));
    }

    /**
     * assertFlowsExecutorConditionNodeWithError
     *
     * @param flowDefinition flowDefinition
     * @param metaId metaId
     * @param flowNode flowNode
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecutorConditionNodeWithError(FlowDefinition flowDefinition, String metaId,
            FlowNode flowNode, List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ERROR, contexts.get(0).getStatus());
        assertEquals(errorMessage(flowDefinition.getStreamId(), metaId, flowNode.getName()).substring(0, 20),
                contexts.get(0).getData().getErrorMessage().substring(0, 20));
        assertEquals(3, allContexts.size());
    }

    /**
     * assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock
     *
     * @param metaId metaId
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock(String metaId,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(PENDING, contexts.get(0).getStatus());
        assertEquals(2, allContexts.size());
        allContexts.stream()
                .filter(c -> !c.getPosition().equals(metaId))
                .forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals("success", resultBusinessData.get("approvedResult"));
    }

    /**
     * assertFlowsManualExecutorWithConditionNodeCircle
     *
     * @param metaId metaId
     * @param contexts contexts
     * @param allContexts allContexts
     * @param allSize allSize
     * @param status status
     */
    protected void assertFlowsManualExecutorWithConditionNodeCircle(String metaId, List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts, int allSize, FlowNodeStatus status) {
        assertEquals(1, contexts.size());
        assertEquals(status, contexts.get(0).getStatus());
        assertEquals(allSize, allContexts.size());
        allContexts.stream()
                .filter(c -> !c.getPosition().equals(metaId))
                .forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    /**
     * assertFlowsManualExecutorWithConditionNodeFirstBranchTrueAfterBlock
     *
     * @param resumeContexts resumeContexts
     * @param resumeAllContexts resumeAllContexts
     * @param contextSizeExpected contextSizeExpected
     * @param approvedExpected approvedExpected
     */
    protected void assertFlowsManualExecutorWithConditionNodeFirstBranchTrueAfterBlock(
            List<FlowContext<FlowData>> resumeContexts, List<FlowContext<FlowData>> resumeAllContexts,
            int contextSizeExpected, String approvedExpected) {
        assertEquals(1, resumeContexts.size());
        assertEquals(contextSizeExpected, resumeAllContexts.size());
        resumeAllContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        Map<String, Object> resumeResultBusinessData = resumeContexts.get(0).getData().getBusinessData();
        assertEquals(approvedExpected, resumeResultBusinessData.get("approvedResult"));
    }

    /**
     * assertFlowsExecuteGeneralJober
     *
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecuteGeneralJober(List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(3, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));

        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals(6, resultBusinessData.size());
        assertEquals("branch", resultBusinessData.get("cudehubBranch"));
    }

    /**
     * assertFlowsExecuteGeneralJoberError
     *
     * @param invoker invoker
     * @param flowDefinition flowDefinition
     * @param metaId metaId
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecuteGeneralJoberError(Invoker invoker, FlowDefinition flowDefinition, String metaId,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        FlowNode flowNode = flowDefinition.getFlowNode(metaId);
        assertEquals(1, contexts.size());
        assertEquals(ERROR, contexts.get(0).getStatus());
        String errorMsg = "Flow general jober invoke error.";
        String expected = MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), flowDefinition.getStreamId(),
                metaId, flowNode.getName(), WaterflowException.class.getSimpleName(), errorMsg);
        String actual = contexts.get(0).getData().getErrorMessage();
        assertEquals(expected, actual);
        assertEquals(2, allContexts.size());
        verify(invoker, times(1)).invoke(anyString(), anyList(), isA(FlowErrorInfo.class));
    }

    /**
     * assertFlowsExecuteGeneralJoberWithRetryableException
     *
     * @param from from
     * @param cxtPosition cxtPosition
     * @param contexts contexts
     * @param allContexts allContexts
     * @param retryPO retryPO
     */
    protected void assertFlowsExecuteGeneralJoberWithRetryableException(From<FlowData> from, String cxtPosition,
        List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts, FlowRetryPO retryPO) {
        assertEquals(1, contexts.size());
        assertEquals(RETRYABLE, contexts.get(0).getStatus());
        assertEquals(cxtPosition, contexts.get(0).getPosition());
        assertEquals(2, allContexts.size());
        assertNotNull(retryPO.getNextRetryTime());
        assertNull(retryPO.getLastRetryTime());
        assertEquals(retryPO.getRetryCount(), 0);
        FitStream.Subscriber<FlowData, Object> subscriberWithException = from.getSubscriber(
                contexts.get(0).getPosition());
        assertTrue(subscriberWithException.isAuto());
        assertEquals(cxtPosition, subscriberWithException.getId());
    }

    /**
     * assertFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread
     *
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread(
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(5, contexts.size());
        contexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        contexts.forEach(c -> assertEquals("hello: success", c.getData().getBusinessData().get("approvedResult")));
        assertEquals(12, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    /**
     * assertFlowsExecuteProduceFromMToNForOfferMultiData
     *
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecuteProduceFromMToNForOfferMultiData(List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(3, contexts.size());
        contexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        assertEquals(14, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    /**
     * assertFlowsExecuteFilterFromMToN
     *
     * @param repo repo
     * @param traceId traceId
     * @param endContexts endContexts
     */
    protected void assertFlowsExecuteFilterFromMToN(FlowContextRepo<FlowData> repo, String traceId,
            List<FlowContext<FlowData>> endContexts) {
        List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(repo, traceId);
        assertEquals(3, endContexts.size());
        assertEquals(10, all.size());
        all.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    /**
     * assertFlowsExecuteProduceFromMToNForOfferOneData
     *
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecuteProduceFromMToNForOfferOneData(List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(3, contexts.size());
        contexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        assertEquals(10, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    private Object getBusinessDataFromChainedKey(Map<String, Object> businessData, String keyChain) {
        Map<String, Object> businessDataCopy = businessData;
        String[] keys = keyChain.split("\\.");
        Object result = "";
        for (int i = 0; i < keys.length; i++) {
            if (i == keys.length - 1) {
                result = businessDataCopy.get(keys[i]);
                break;
            }
            if (!(businessDataCopy.get(keys[i]) instanceof Map)) {
                throw new IllegalStateException(
                        String.format("Failed to get the value of \"%s\" from the businessData %s", keyChain,
                                businessData));
            }
            businessDataCopy = ObjectUtils.cast(businessDataCopy.get(keys[i]));
        }
        if (result instanceof String) {
            if ("TRUE".equalsIgnoreCase(ObjectUtils.cast(result))) {
                return true;
            }
            if ("FALSE".equalsIgnoreCase(ObjectUtils.cast(result))) {
                return false;
            }
        }
        return result;
    }
}
