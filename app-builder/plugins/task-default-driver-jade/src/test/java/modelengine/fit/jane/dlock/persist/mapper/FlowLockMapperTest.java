/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.persist.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.jane.dlock.DatabaseBaseTest;
import modelengine.fit.jane.dlock.jdbc.persist.mapper.FlowLockMapper;
import modelengine.fit.jane.dlock.jdbc.persist.po.FlowLockPO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * {@link FlowLockMapper}测试类
 *
 * @author 李哲峰
 * @since 2023/11/29
 */
public class FlowLockMapperTest extends DatabaseBaseTest {
    private final String sqlFile = "handler/flowlock/saveData.sql";

    private FlowLockMapper flowLockMapper;

    @BeforeEach
    void before() {
        flowLockMapper = sqlSessionManager.openSession(true).getMapper(FlowLockMapper.class);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowlock/cleanData.sql");
    }

    @Test
    @DisplayName("测试flow lock保存并查询成功")
    public void testCreateFlowLockSuccess() {
        FlowLockPO flowLockPO = getFlowLockPO();

        assertEquals(1, flowLockMapper.create(flowLockPO));
        FlowLockPO result = flowLockMapper.find(flowLockPO.getLockKey());
        assertEquals(flowLockPO.getExpiredAt(), result.getExpiredAt());
        assertEquals(flowLockPO.getLockedClient(), result.getLockedClient());
    }

    @Test
    @DisplayName("测试flow lock存在")
    public void testCreateFlowLockExists() {
        executeSqlInFile(sqlFile);
        FlowLockPO flowLockPO = getFlowLockPO();
        assertTrue(flowLockMapper.isExists(flowLockPO));
    }

    @Test
    @DisplayName("测试继续保留flow lock成功")
    public void testUpdateFlowLockSuccess() {
        executeSqlInFile(sqlFile);
        FlowLockPO flowLockPO = getFlowLockPO();
        assertEquals(1, flowLockMapper.update(flowLockPO, LocalDateTime.now()));
        FlowLockPO result = flowLockMapper.find(flowLockPO.getLockKey());
        assertEquals(flowLockPO.getExpiredAt(), result.getExpiredAt());
    }

    @Test
    @DisplayName("测试继续保留flow lock失败")
    public void testUpdateFlowLockFailure() {
        executeSqlInFile(sqlFile);
        FlowLockPO flowLockPO = getFlowLockPO();
        flowLockPO.setLockedClient("192.168.1.2");
        assertEquals(0, flowLockMapper.update(flowLockPO, flowLockPO.getExpiredAt()));
        FlowLockPO result = flowLockMapper.find(flowLockPO.getLockKey());
        assertNotEquals(flowLockPO.getExpiredAt(), result.getExpiredAt());
    }

    @Test
    @DisplayName("测试续期flow lock成功")
    public void testUpdateFlowLockExpiredAtSuccess() {
        executeSqlInFile(sqlFile);
        FlowLockPO flowLockPO = getFlowLockPO();
        assertEquals(1, flowLockMapper.updateExpiredAt(flowLockPO));
        FlowLockPO result = flowLockMapper.find(flowLockPO.getLockKey());
        assertEquals(flowLockPO.getExpiredAt(), result.getExpiredAt());
    }

    @Test
    @DisplayName("测试删除flow lock成功")
    public void deleteFlowLockSuccess() {
        executeSqlInFile(sqlFile);
        FlowLockPO flowLockPO = getFlowLockPO();
        assertEquals(1, flowLockMapper.delete(flowLockPO.getLockKey(), flowLockPO.getLockedClient()));
        assertNull(flowLockMapper.find(flowLockPO.getLockKey()));
    }

    @Test
    @DisplayName("测试删除过期flow lock成功")
    public void deleteExpiredFlowLockSuccess() {
        executeSqlInFile(sqlFile);
        FlowLockPO flowLockPO = getFlowLockPO();
        flowLockPO.setExpiredAt(LocalDateTime.now());
        assertEquals(1, flowLockMapper.deleteExpired(flowLockPO));
        assertNull(flowLockMapper.find(flowLockPO.getLockKey()));
    }

    private FlowLockPO getFlowLockPO() {
        LocalDateTime time = LocalDateTime.parse("2023-11-28T19:12:30.011");
        return FlowLockPO.builder().lockKey("flow-event-1-1").expiredAt(time).lockedClient("192.168.1.1").build();
    }
}
