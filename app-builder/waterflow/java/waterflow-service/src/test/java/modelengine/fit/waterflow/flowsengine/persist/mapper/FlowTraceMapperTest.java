/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.persist.mapper;

import modelengine.fit.waterflow.DatabaseBaseTest;
import modelengine.fit.waterflow.MethodNameLoggerExtension;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowTracePO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link FlowTraceMapper} 测试类
 *
 * @author 晏钰坤
 * @since 2023/12/13
 */
@ExtendWith(MethodNameLoggerExtension.class)
public class FlowTraceMapperTest extends DatabaseBaseTest {
    private static final String TERMINATE_STATUS = FlowTraceStatus.TERMINATE.toString();

    private static final String RUNNING_STATUS = FlowTraceStatus.RUNNING.toString();

    private static final String ARCHIVED_STATUS = FlowTraceStatus.ARCHIVED.toString();

    private static final String ERROR_STATUS = FlowTraceStatus.ERROR.toString();

    private final String sqlFile = "handler/flowTrace/saveData.sql";

    private FlowTraceMapper flowTraceMapper;

    @BeforeEach
    void before() {
        flowTraceMapper = sqlSessionManager.openSession(true).getMapper(FlowTraceMapper.class);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowTrace/cleanData.sql");
    }

    @Test
    @DisplayName("如果trace的状态是RUNNING，可以更新为TERMINATE")
    public void testUpdateTraceTerminateSuccessWhenStatusIsRunning() {
        executeSqlInFile(sqlFile);

        flowTraceMapper.updateStatus(Collections.singletonList("1"), TERMINATE_STATUS, LocalDateTime.now(),
                Constant.TRACE_EXCLUSIVE_STATUS_MAP.get(TERMINATE_STATUS));

        FlowTracePO tracePO = flowTraceMapper.find("1");
        Assertions.assertEquals(TERMINATE_STATUS, tracePO.getStatus());
    }

    @Test
    @DisplayName("如果trace的状态是ARCHIVED，不能更新为TERMINATE")
    public void testUpdateTraceTerminateFailedWhenStatusIsArchived() {
        executeSqlInFile(sqlFile);
        flowTraceMapper.updateStatus(Collections.singletonList("1"), ARCHIVED_STATUS, LocalDateTime.now(),
                Constant.TRACE_EXCLUSIVE_STATUS_MAP.get(ARCHIVED_STATUS));

        flowTraceMapper.updateStatus(Collections.singletonList("1"), TERMINATE_STATUS, LocalDateTime.now(),
                Constant.TRACE_EXCLUSIVE_STATUS_MAP.get(TERMINATE_STATUS));

        FlowTracePO tracePO = flowTraceMapper.find("1");
        Assertions.assertEquals(ARCHIVED_STATUS, tracePO.getStatus());
    }

    @Test
    @DisplayName("如果trace的状态是ERROR，不能更新为TERMINATE")
    public void testUpdateTraceTerminateFailedWhenStatusIsError() {
        executeSqlInFile(sqlFile);
        flowTraceMapper.updateStatus(Collections.singletonList("1"), ERROR_STATUS, LocalDateTime.now(),
                Constant.TRACE_EXCLUSIVE_STATUS_MAP.get(ERROR_STATUS));

        flowTraceMapper.updateStatus(Collections.singletonList("1"), TERMINATE_STATUS, LocalDateTime.now(),
                Constant.TRACE_EXCLUSIVE_STATUS_MAP.get(TERMINATE_STATUS));

        FlowTracePO tracePO = flowTraceMapper.find("1");
        Assertions.assertEquals(ERROR_STATUS, tracePO.getStatus());
    }

    @Test
    @DisplayName("如果trace的状态是TERMINATE，不能更新为RUNNING")
    public void testUpdateTraceRunningFailedWhenStatusIsTerminate() {
        executeSqlInFile(sqlFile);
        flowTraceMapper.updateStatus(Collections.singletonList("1"), TERMINATE_STATUS, LocalDateTime.now(),
                Constant.TRACE_EXCLUSIVE_STATUS_MAP.get(TERMINATE_STATUS));

        flowTraceMapper.updateStatus(Collections.singletonList("1"), RUNNING_STATUS, LocalDateTime.now(),
                Constant.TRACE_EXCLUSIVE_STATUS_MAP.get(RUNNING_STATUS));

        FlowTracePO tracePO = flowTraceMapper.find("1");
        Assertions.assertEquals(TERMINATE_STATUS, tracePO.getStatus());
    }

    @Test
    @DisplayName("如果trace的状态是ERROR，可以更新为RUNNING")
    public void testUpdateTraceRunningSuccessWhenStatusIsError() {
        executeSqlInFile(sqlFile);
        flowTraceMapper.updateStatus(Collections.singletonList("1"), ERROR_STATUS, LocalDateTime.now(),
                Constant.TRACE_EXCLUSIVE_STATUS_MAP.get(ERROR_STATUS));

        flowTraceMapper.updateStatus(Collections.singletonList("1"), RUNNING_STATUS, LocalDateTime.now(),
                Constant.TRACE_EXCLUSIVE_STATUS_MAP.get(RUNNING_STATUS));

        FlowTracePO tracePO = flowTraceMapper.find("1");
        Assertions.assertEquals(RUNNING_STATUS, tracePO.getStatus());
    }

    @Test
    @DisplayName("根据traceId列表查询trace成功")
    public void testFindByTraceIdListSuccess() {
        executeSqlInFile(sqlFile);

        List<FlowTracePO> flowTracePOS = flowTraceMapper.findByTraceIdList(Arrays.asList("1", "2"));

        Assertions.assertEquals("1", flowTracePOS.get(0).getTraceId());
        Assertions.assertEquals("RUNNING", flowTracePOS.get(0).getStatus());
        Assertions.assertEquals("2", flowTracePOS.get(1).getTraceId());
        Assertions.assertEquals("ERROR", flowTracePOS.get(1).getStatus());
    }

    @Test
    @DisplayName("查询过期trace成功")
    public void testGetExpiredTraceSuccess() {
        FlowTracePO tracePO = FlowTracePO
                .builder()
                .traceId("123")
                .streamId("123")
                .operator("yxy")
                .application("flow")
                .startNode("start")
                .startTime(LocalDateTime.now())
                .status("ERROR")
                .endTime(LocalDateTime.of(2023, 1, 1, 1, 1))
                .build();
        flowTraceMapper.create(tracePO);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(30);

        List<String> expiredTrace = flowTraceMapper.getExpiredTrace(expired, 2);

        Assertions.assertEquals(1, expiredTrace.size());
    }
}
