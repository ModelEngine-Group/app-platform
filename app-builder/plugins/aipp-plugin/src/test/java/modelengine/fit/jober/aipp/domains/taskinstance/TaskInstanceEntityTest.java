/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.entity.task.TaskProperty;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link TaskInstanceEntity} 的测试类。
 *
 * @author 张越
 * @since 2025-01-12
 */
public class TaskInstanceEntityTest {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @DisplayName("基本测试")
    public void test() {
        Map<String, String> infos = this.buildInfos();
        TaskInstanceDomainEntity entity = AppTaskInstance.asEntity().putInfos(infos).putTags(new ArrayList<>());
        entity.setInstanceId("instanceId");
        entity.setTaskId("taskId");
        assertEquals("instance-name", entity.getName());
        assertEquals("instanceId", entity.getInstanceId());
        assertEquals("taskId", entity.getTaskId());
        assertEquals("zy", entity.getCreator());
        Assertions.assertTrue(entity.getStatus().isPresent());
        assertEquals("active", entity.getStatus().get());
        assertEquals("10", entity.getProgress());
        assertEquals("trace1", entity.getFlowTranceId());
        assertEquals("form1", entity.getFormId());
        assertEquals("1.0", entity.getFormVersion());
        assertEquals("startNodeId", entity.getCurrentNodeId());
        assertEquals("childInstanceId", entity.getChildInstanceId());
        assertEquals("output", entity.getLlmOutput());
        assertEquals(10L, entity.getResumeDuration());
    }

    @Test
    @DisplayName("set方法测试")
    public void testSetMethods() {
        LocalDateTime createTime = LocalDateTime.now();
        String currentTimeStr = DF.format(createTime);
        LocalDateTime finishTime = createTime.plusDays(1);
        String finishTimeStr = DF.format(finishTime);
        LocalDateTime smartFormTime = createTime.plusDays(2);
        String smartFormTimeStr = DF.format(smartFormTime);

        TaskInstanceDomainEntity entity = AppTaskInstance.asEntity();
        entity.setCurrentNodeId("currentNodeId");
        entity.setFlowTraceId("flowTraceId");
        entity.setTaskId("taskId");
        entity.setInstanceId("instanceId");
        entity.setChildInstanceId("childInstanceId");
        entity.setCreator("createCreator");
        entity.setCreateTime(createTime);
        entity.setFinishTime(finishTime);
        entity.setFormId("formId");
        entity.setName("name");
        entity.setStatus("active");
        entity.setProgress("20");
        entity.setFormVersion("1.0");
        entity.setLlmOutput("llmOutput");
        entity.setSmartFormTime(smartFormTime);
        entity.setResumeDuration("20");

        assertEquals("currentNodeId", entity.getCurrentNodeId());
        assertEquals("flowTraceId", entity.getFlowTranceId());
        assertEquals("taskId", entity.getTaskId());
        assertEquals("instanceId", entity.getInstanceId());
        assertEquals("childInstanceId", entity.getChildInstanceId());
        assertEquals("createCreator", entity.getCreator());
        assertEquals(currentTimeStr, entity.getCreateTime());
        assertEquals(finishTimeStr, entity.getFinishTime(null));
        assertEquals("formId", entity.getFormId());
        assertEquals("name", entity.getName());
        assertTrue(entity.getStatus().isPresent());
        assertEquals("active", entity.getStatus().get());
        assertEquals("20", entity.getProgress());
        assertEquals("1.0", entity.getFormVersion());
        assertEquals("llmOutput", entity.getLlmOutput());
        assertEquals(smartFormTimeStr, entity.getSmartFormTime().orElse(StringUtils.EMPTY));
        assertEquals(20L, entity.getResumeDuration());
    }

    @Test
    @DisplayName("fetch测试")
    public void testFetch() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.INST_NAME_KEY, "zy");
        businessData.put(AippConst.INST_CREATOR_KEY, "wla");

        TaskProperty property = new TaskProperty();
        property.setId("propertyId");
        property.setName(AippConst.INST_NAME_KEY);
        List<TaskProperty> props = new ArrayList<>();
        props.add(property);

        TaskInstanceDomainEntity entity = AppTaskInstance.asEntity();
        entity.fetch(businessData, props);

        assertEquals("zy", entity.getName());
        assertNull(entity.getCreator());
    }

    private Map<String, String> buildInfos() {
        Map<String, String> infos = new HashMap<>();
        infos.put(AippConst.INST_NAME_KEY, "instance-name");
        infos.put(AippConst.INST_CREATOR_KEY, "zy");
        infos.put(AippConst.INST_STATUS_KEY, "active");
        infos.put(AippConst.INST_PROGRESS_KEY, "10");
        infos.put(AippConst.INST_FLOW_INST_ID_KEY, "trace1");
        infos.put(AippConst.INST_CURR_FORM_ID_KEY, "form1");
        infos.put(AippConst.INST_CURR_FORM_VERSION_KEY, "1.0");
        infos.put(AippConst.INST_CURR_NODE_ID_KEY, "startNodeId");
        infos.put(AippConst.INST_RESUME_DURATION_KEY, "10");
        infos.put(AippConst.INST_CHILD_INSTANCE_ID, "childInstanceId");
        infos.put("llmOutput", "output");
        return infos;
    }
}
