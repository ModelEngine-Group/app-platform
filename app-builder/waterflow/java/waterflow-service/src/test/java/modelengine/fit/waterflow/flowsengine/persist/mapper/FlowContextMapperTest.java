/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.persist.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.waterflow.DatabaseBaseTest;
import modelengine.fit.waterflow.MethodNameLoggerExtension;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.persist.entity.FlowContextUpdateInfo;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowContextPO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * {@link FlowContextMapper}测试类
 *
 * @author 杨祥宇
 * @since 2023/8/24
 */
@ExtendWith(MethodNameLoggerExtension.class)
public class FlowContextMapperTest extends DatabaseBaseTest {
    private static final String NEW_STATUS = FlowNodeStatus.NEW.toString();

    private static final String PROCESSING_STATUS = FlowNodeStatus.PROCESSING.toString();

    private static final String READY_STATUS = FlowNodeStatus.READY.toString();

    private static final String TERMINATE_STATUS = FlowNodeStatus.TERMINATE.toString();

    private static final String RETRYABLE_STATUS = FlowNodeStatus.RETRYABLE.toString();

    private static final String ARCHIVED_STATUS = FlowNodeStatus.ARCHIVED.toString();

    private static final String ERROR_STATUS = FlowNodeStatus.ERROR.toString();

    private static final String PENDING_STATUS = FlowNodeStatus.PENDING.toString();

    private final String sqlFile = "handler/flowContext/saveData.sql";

    private FlowContextMapper flowContextMapper;

    @BeforeEach
    void before() {
        flowContextMapper = sqlSessionManager.openSession(true).getMapper(FlowContextMapper.class);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowContext/cleanData.sql");
    }

    @Test
    @DisplayName("测试flow context更新sent标志成功")
    public void testUpdateFlowContextToSentSuccess() {
        executeSqlInFile(sqlFile);

        List<String> ids = Arrays.asList("1", "2", "3", "4");
        flowContextMapper.updateToSent(ids);

        ids.forEach(id -> {
            FlowContextPO po = flowContextMapper.find(id);
            assertTrue(po.isSent());
        });
    }

    @Test
    @DisplayName("测试flow context通过positionId查询成功")
    public void testFindFlowContextByPositionSuccess() {
        executeSqlInFile(sqlFile);

        List<FlowContextPO> result = flowContextMapper.findByPositions("1", Arrays.asList("1"), "PENDING",
            Arrays.asList("1", "2", "3", "4"));

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getContextId());
        assertEquals("1", result.get(0).getTransId());
        assertEquals("2", result.get(0).getBatchId());
        assertEquals("2", result.get(0).getToBatch());
    }

    @Test
    @DisplayName("测试flow context通过positionId和transId查询成功")
    public void testFindFlowContextByPositionWithTransIdSuccess() {
        executeSqlInFile(sqlFile);

        List<FlowContextPO> result = flowContextMapper.findByPositionWithBatchId("1", "1", "2", "PENDING");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getContextId());
        assertEquals("1", result.get(0).getTransId());
        assertEquals("2", result.get(0).getBatchId());
        assertEquals("2", result.get(0).getToBatch());
    }

    @Test
    @DisplayName("测试flow context通过SubscriptionId查询成功")
    public void testFindFlowContextBySubscriptionSuccess() {
        executeSqlInFile(sqlFile);
        ArrayList<String> subscription = new ArrayList<>(Arrays.asList("1", "2"));
        List<FlowContextPO> result = flowContextMapper.findBySubscriptions("1", subscription, "PENDING",
                Arrays.asList("1", "2", "3", "4", "5"));

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getContextId());
        assertEquals("2", result.get(0).getBatchId());
        assertEquals("2", result.get(0).getToBatch());
    }

    @Test
    @DisplayName("测试flow context通过flowContextId查询成功")
    public void testFindFlowContextByIdSuccess() {
        executeSqlInFile(sqlFile);
        FlowContextPO result = flowContextMapper.find("1");

        assertEquals("1", result.getContextId());
        assertEquals("2", result.getBatchId());
        assertEquals("2", result.getToBatch());
    }

    @Test
    @DisplayName("测试根据streamId列表查询context成功")
    public void testFindFlowContextByStreamIdListSuccess() {
        executeSqlInFile(sqlFile);
        List<FlowContextPO> flowContextPOS = flowContextMapper.findByStreamIdList(
                Arrays.asList("12ddd65a3ed54e69936a739ca7767c2f-1.0.0", "12ddd65a3ed54e69936a739ca7767c2f-1.0.2"));
        assertEquals(3, flowContextPOS.size());
    }

    @Test
    @DisplayName("测试根据streamId分页查询contexts成功")
    public void testPageQueryFlowContextByStreamIdSuccess() {
        executeSqlInFile(sqlFile);
        List<FlowContextPO> flowContextPOs = flowContextMapper.pageQueryByStreamId(
                "12ddd65a3ed54e69936a739ca7767c2f-1.0.0", 1, 1);
        assertEquals(1, flowContextPOs.size());
        assertEquals("4", flowContextPOs.get(0).getContextId());
    }

    @Test
    @DisplayName("测试根据streamId查询contexts总数")
    public void testGetTotalByStreamIdSuccess() {
        executeSqlInFile(sqlFile);

        int actual = flowContextMapper.getTotalByStreamId("12ddd65a3ed54e69936a739ca7767c2f-1.0.0");

        assertEquals(3, actual);
    }

    @Test
    @DisplayName("测试根据contextId列表查询context成功")
    public void testFindFlowContextByContextIdListSuccess() {
        executeSqlInFile(sqlFile);

        List<FlowContextPO> flowContextPOS = flowContextMapper.findByContextIdList(Arrays.asList("3", "4"));

        Assertions.assertEquals(2, flowContextPOS.size());
    }

    @Test
    @DisplayName("测试批量创建context成功")
    public void testBatchSaveFlowContextSuccess() {
        FlowContextPO context1 = getFlowContextPO();
        FlowContextPO context2 = getOtherFlowContextPO();
        List<FlowContextPO> contextList = new ArrayList<>();
        contextList.add(context1);
        contextList.add(context2);

        flowContextMapper.batchCreate(contextList);

        FlowContextPO result1 = flowContextMapper.find(context1.getContextId());
        FlowContextPO result2 = flowContextMapper.find(context2.getContextId());
        Assertions.assertEquals(context1.getContextId(), result1.getContextId());
        Assertions.assertEquals(context2.getContextId(), result2.getContextId());
    }

    @Test
    @DisplayName("测试批量更新context成功")
    public void testBatchUpdateFlowContextSuccess() {
        executeSqlInFile(sqlFile);
        FlowContextPO context1 = getFlowContextPO();
        FlowContextPO context2 = getOtherFlowContextPO();
        List<FlowContextPO> contextList = new ArrayList<>();
        contextList.add(context1);
        contextList.add(context2);

        flowContextMapper.batchUpdate(contextList);

        FlowContextPO result1 = flowContextMapper.find(context1.getContextId());
        FlowContextPO result2 = flowContextMapper.find(context2.getContextId());
        Assertions.assertEquals(context1.getFlowData(), result1.getFlowData());
        Assertions.assertEquals(context1.getParallel(), result1.getParallel());
        Assertions.assertEquals(context1.getParallelMode(), result1.getParallelMode());
        Assertions.assertEquals(context1.getBatchId(), result1.getBatchId());
        Assertions.assertEquals(context1.getToBatch(), result1.getToBatch());
        assertFalse(result1.isSent());
        Assertions.assertEquals(context2.getFlowData(), result2.getFlowData());
        Assertions.assertEquals(context2.getParallel(), result2.getParallel());
        Assertions.assertEquals(context2.getParallelMode(), result2.getParallelMode());
        Assertions.assertEquals(context2.getBatchId(), result2.getBatchId());
        Assertions.assertEquals(context2.getToBatch(), result2.getToBatch());
        assertFalse(result2.isSent());
    }

    @Test
    @DisplayName("测试批量更新flowData和toBatch字段")
    public void testBatchUpdateFlowDataSuccess() {
        executeSqlInFile(sqlFile);
        FlowContextPO context1 = getFlowContextPO();
        FlowContextPO context2 = getOtherFlowContextPO();
        List<FlowContextPO> contextList = new ArrayList<>();
        contextList.add(context1);
        contextList.add(context2);

        flowContextMapper.updateFlowDataAndToBatch(contextList);

        FlowContextPO result1 = flowContextMapper.find(context1.getContextId());
        FlowContextPO result2 = flowContextMapper.find(context2.getContextId());
        Assertions.assertEquals("2", result1.getBatchId());
        Assertions.assertEquals(context1.getFlowData(), result1.getFlowData());
        Assertions.assertEquals(context1.getToBatch(), result1.getToBatch());
        Assertions.assertEquals(PENDING_STATUS, result1.getStatus());
        assertFalse(result1.isSent());
        Assertions.assertEquals("3", result2.getBatchId());
        Assertions.assertEquals(context2.getFlowData(), result2.getFlowData());
        Assertions.assertEquals(context2.getToBatch(), result2.getToBatch());
        Assertions.assertEquals(ERROR_STATUS, result2.getStatus());
        assertFalse(result2.isSent());
    }

    @Test
    @DisplayName("测试查询context id列表成功")
    public void testGetRunningContextsIdByTransaction() {
        executeSqlInFile(sqlFile);
        String transId = "1";

        List<String> contexts = flowContextMapper.getRunningContextsIdByTransaction(transId);

        Assertions.assertEquals(1, contexts.size());
        Assertions.assertEquals("1", contexts.get(0));
    }

    @Test
    @DisplayName("测试批量更新status和position字段成功")
    public void testUpdateStatusAndPositionSuccess() {
        executeSqlInFile(sqlFile);
        List<String> contextIds = Arrays.asList("1", "2");

        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(READY_STATUS, "12", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(READY_STATUS));

        FlowContextPO result1 = flowContextMapper.find("1");
        FlowContextPO result2 = flowContextMapper.find("2");
        Assertions.assertEquals(READY_STATUS, result1.getStatus());
        Assertions.assertEquals("12", result1.getPositionId());
        Assertions.assertEquals(READY_STATUS, result2.getStatus());
        Assertions.assertEquals("12", result2.getPositionId());
    }

    @Test
    @DisplayName("如果context的状态是terminate，不能更新为pending")
    public void testUpdateStatusAndPositionFailedWhenContextStatusIsTerminate() {
        executeSqlInFile(sqlFile);
        List<String> contextIds = Collections.singletonList("1");
        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(TERMINATE_STATUS, "21", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(TERMINATE_STATUS));

        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(PENDING_STATUS, "12", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(PENDING_STATUS));

        FlowContextPO result1 = flowContextMapper.find("1");
        Assertions.assertEquals(TERMINATE_STATUS, result1.getStatus());
        Assertions.assertEquals("21", result1.getPositionId());
    }

    @Test
    @DisplayName("如果context的状态是error，可以更新为pending")
    public void testUpdateStatusPendingSuccessWhenContextStatusIsError() {
        executeSqlInFile(sqlFile);
        List<String> contextIds = Collections.singletonList("1");
        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(ERROR_STATUS, "21", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(ERROR_STATUS));

        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(PENDING_STATUS, "12", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(PENDING_STATUS));

        FlowContextPO result1 = flowContextMapper.find("1");
        Assertions.assertEquals(PENDING_STATUS, result1.getStatus());
        Assertions.assertEquals("12", result1.getPositionId());
    }

    @Test
    @DisplayName("如果context的状态是archived，可以更新为pending")
    public void testUpdateStatusPendingSuccessWhenContextStatusIsArchived() {
        executeSqlInFile(sqlFile);
        List<String> contextIds = Collections.singletonList("1");
        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(ARCHIVED_STATUS, "21", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(ARCHIVED_STATUS));

        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(PENDING_STATUS, "12", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(PENDING_STATUS));

        FlowContextPO result1 = flowContextMapper.find("1");
        Assertions.assertEquals(PENDING_STATUS, result1.getStatus());
        Assertions.assertEquals("12", result1.getPositionId());
    }

    @Test
    @DisplayName("如果context的状态是error，则不能更新为terminate")
    public void testUpdateStatusTerminateFailedWhenContextStatusIsError() {
        executeSqlInFile(sqlFile);
        List<String> contextIds = Collections.singletonList("1");
        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(READY_STATUS, "21", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(READY_STATUS));

        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(ERROR_STATUS, "21", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(ERROR_STATUS));

        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(TERMINATE_STATUS, "12", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(TERMINATE_STATUS));

        FlowContextPO result1 = flowContextMapper.find("1");
        Assertions.assertEquals(ERROR_STATUS, result1.getStatus());
        Assertions.assertEquals("21", result1.getPositionId());
    }

    @Test
    @DisplayName("如果context的状态是archived，则不能更新为terminate")
    public void testUpdateStatusAndPositionFailedWhenContextStatusIsArchived() {
        executeSqlInFile(sqlFile);
        List<String> contextIds = Collections.singletonList("1");
        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(READY_STATUS, "21", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(READY_STATUS));

        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(ARCHIVED_STATUS, "21", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(ARCHIVED_STATUS));

        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(TERMINATE_STATUS, "12", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(TERMINATE_STATUS));

        FlowContextPO result1 = flowContextMapper.find("1");
        Assertions.assertEquals(ARCHIVED_STATUS, result1.getStatus());
        Assertions.assertEquals("21", result1.getPositionId());
    }

    @Test
    @DisplayName("测试查询stream id成功")
    public void testGetStreamIdByTransIdSuccess() {
        executeSqlInFile(sqlFile);
        String transId = "1";

        String streamId = flowContextMapper.getStreamIdByTransId(transId);

        Assertions.assertEquals("1", streamId);
    }

    @Test
    @DisplayName("测试查询已完成的上下文")
    public void testFindFinishedContextsPagedByTransIdSuccess() {
        executeSqlInFile(sqlFile);
        String transId = "2";

        List<FlowContextPO> contextPOS = flowContextMapper.findFinishedContextsPagedByTransId(transId, "1", 1, 1);

        Assertions.assertEquals(1, contextPOS.size());
    }

    @Test
    @DisplayName("测试查询已完成的上下文数量")
    public void testFindFinishedPageNumByTransIdSuccess() {
        executeSqlInFile(sqlFile);
        String transId = "1";

        int totalNum = flowContextMapper.findFinishedPageNumByTransId(transId, "1");

        Assertions.assertEquals(1, totalNum);
    }

    @Test
    @DisplayName("如果context处于任意非ready状态，则不能更新为retryable")
    public void testUpdateStatusToRetryableFailedWhenContextStatusIsNonReady() {
        executeSqlInFile(sqlFile);
        List<FlowNodeStatus> nonReadyList = Arrays.asList(FlowNodeStatus.PROCESSING, FlowNodeStatus.ARCHIVED,
                FlowNodeStatus.ERROR,
                FlowNodeStatus.TERMINATE);
        Random randomizer = new Random();
        FlowNodeStatus randomExclusive = nonReadyList.get(randomizer.nextInt(nonReadyList.size()));
        List<String> contextIds = Collections.singletonList("1");

        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(RETRYABLE_STATUS, "12", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(RETRYABLE_STATUS));
        FlowContextPO result1 = flowContextMapper.find("1");
        Assertions.assertEquals(PENDING_STATUS, result1.getStatus());
        Assertions.assertEquals("1", result1.getPositionId());

        flowContextMapper.updateStatus(contextIds, randomExclusive);

        flowContextMapper.updateStatusAndPosition(contextIds,
                new FlowContextUpdateInfo(RETRYABLE_STATUS, "12", LocalDateTime.now(), LocalDateTime.now()),
                Constant.CONTEXT_EXCLUSIVE_STATUS_MAP.get(RETRYABLE_STATUS));

        result1 = flowContextMapper.find("1");
        Assertions.assertEquals(randomExclusive.name(), result1.getStatus());
        Assertions.assertEquals("1", result1.getPositionId());
    }

    @Test
    public void testFindByToBatch() {
        executeSqlInFile(sqlFile);
        List<FlowContextPO> contextPO = flowContextMapper.findByToBatch(Collections.singletonList("5"));
        Assertions.assertEquals(2, contextPO.size());
        Assertions.assertEquals("4", contextPO.get(0).getContextId());
        Assertions.assertEquals("5", contextPO.get(1).getContextId());
    }

    @Test
    @DisplayName("测试根据trace id列表删除成功")
    void testDeleteByTraceIds() {
        executeSqlInFile(sqlFile);
        flowContextMapper.deleteByTraceIdList(Collections.singletonList("4"));

        FlowContextPO flowContextPO = flowContextMapper.find("4");
        Assertions.assertNull(flowContextPO);
    }

    private FlowContextPO getFlowContextPO() {
        LocalDateTime time = LocalDateTime.now();
        return FlowContextPO.builder()
                .contextId("1")
                .traceId("1")
                .transId("1")
                .rootId("1")
                .streamId("1")
                .flowData("input1")
                .positionId("12")
                .status("NEW")
                .parallel("parallel")
                .parallelMode("either")
                .batchId("batchId")
                .toBatch("toBatch1")
                .sent(true)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .archivedAt(time)
                .build();
    }

    private FlowContextPO getOtherFlowContextPO() {
        LocalDateTime time = LocalDateTime.now();
        return FlowContextPO.builder()
                .contextId("2")
                .traceId("2")
                .transId("2")
                .rootId("2")
                .streamId("2")
                .flowData("input2")
                .positionId("21")
                .status("NEW")
                .parallel("parallel")
                .parallelMode("all")
                .batchId("batchId")
                .toBatch("toBatch2")
                .sent(true)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .archivedAt(time)
                .build();
    }
}
