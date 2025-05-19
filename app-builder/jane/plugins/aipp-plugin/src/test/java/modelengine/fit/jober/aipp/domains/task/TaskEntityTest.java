/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

import static modelengine.fit.jober.aipp.enums.AippMetaStatusEnum.ACTIVE;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import modelengine.fit.dynamicform.entity.FormMetaInfo;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AippNodeForms;
import modelengine.fit.jober.aipp.enums.JaneCategory;
import modelengine.fit.jober.entity.consts.NodeTypes;
import modelengine.fit.jober.entity.task.TaskProperty;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * {@link TaskEntity} 的测试类。
 *
 * @author 张越
 * @since 2025-01-13
 */
public class TaskEntityTest {
    @Test
    @DisplayName("基本测试")
    public void test() {
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime lastModified = createTime.plusDays(1);
        LocalDateTime publishTime = createTime.plusDays(2);
        String uniqueName = UUID.randomUUID().toString();

        TaskEntity<?> entity = AppTask.asEntity();
        entity.setName("test_name");
        entity.setVersion("1.0");
        entity.setAppSuiteId("app_suite_id");
        entity.setCategory(JaneCategory.AIPP.name());
        entity.setAippType(NORMAL.name());
        entity.setStatus(ACTIVE.getCode());
        entity.setTaskId("task_id");
        entity.setAppId("app_id");
        entity.setCreationTime(createTime);
        entity.setLastModificationTime(lastModified);
        entity.setCreator("zy");
        entity.setDescription("test_description");
        entity.setIcon("http://xxx.com/a.png");
        entity.setFlowConfigId("flow_config_id");
        entity.setPublishDescription("application published");
        entity.setPublishLog("update by zy");
        entity.setUniqueName(uniqueName);
        entity.setAttributeVersion("2.0");
        entity.setFlowDefinitionId("flow_definition_id");
        entity.setPublishTime(publishTime.toString());
        entity.setBaseLineVersion("3.0");

        assertEquals("test_name", entity.getName());
        assertEquals("1.0", entity.getVersion());
        assertEquals("app_suite_id", entity.getAppSuiteId());
        assertEquals(JaneCategory.AIPP.name(), entity.getCategory());
        assertEquals(NORMAL.name(), entity.getAippType());
        assertEquals(ACTIVE.getCode(), entity.getStatus());
        assertEquals("task_id", entity.getTaskId());
        assertEquals("app_id", entity.getAppId());
        assertSame(createTime, entity.getCreationTime());
        assertSame(lastModified, entity.getLastModificationTime());
        assertEquals("zy", entity.getCreator());
        assertEquals("test_description", entity.getDescription());
        assertEquals("http://xxx.com/a.png", entity.getIcon());
        assertEquals("flow_config_id", entity.getFlowConfigId());
        assertEquals("application published", entity.getPublishDescription());
        assertEquals("update by zy", entity.getPublishLog());
        assertEquals(uniqueName, entity.getUniqueName());
        assertEquals("2.0", entity.getAttributeVersion());
        assertEquals("flow_definition_id", entity.getFlowDefinitionId());
        assertEquals(publishTime.toString(), entity.getPublishTime());
        assertEquals("3.0", entity.getBaseLineVersion());
    }

    @Test
    @DisplayName("测试从AippDto中提取数据")
    public void testFetchFromAippDto() {
        TaskEntity<?> entity = AppTask.asEntity();
        Map<String, Object> flowViewData = new HashMap<>();
        flowViewData.put("version", "1.0");
        entity.fetch(AippDto.builder()
                .appId("app_id")
                .name("task_name")
                .version("1.0")
                .description("test_description")
                .icon("http://xxxx.con/aaa.png")
                .flowViewData(flowViewData)
                .build());

        assertEquals("task_name", entity.getName());
        assertEquals("app_id", entity.getAppId());
        assertEquals("1.0", entity.getVersion());
        assertEquals("test_description", entity.getDescription());
        assertEquals("http://xxxx.con/aaa.png", entity.getIcon());
    }

    @Test
    @DisplayName("测试从AippCreateDto中提取数据")
    public void testFetchFromAippCreateDto() {
        TaskEntity<?> entity = AppTask.asEntity();
        entity.fetch(AippCreateDto.builder().version("2.0").aippId("app_suite_id").build());

        assertEquals("app_suite_id", entity.getAppSuiteId());
        assertEquals("2.0", entity.getBaseLineVersion());
    }

    @Test
    @DisplayName("测试从flowView中提取数据")
    public void testFetchFromFlowView() {
        TaskEntity<?> entity = AppTask.asEntity();
        entity.fetch(MapBuilder.<String, Object>get()
                .put(AippConst.FLOW_CONFIG_ID_KEY, "flow_config_id")
                .put(AippConst.FLOW_CONFIG_VERSION_KEY, "3.0")
                .build());

        assertEquals("flow_config_id", entity.getFlowConfigId());
        assertEquals("3.0", entity.getAttributeVersion());
    }

    @Test
    @DisplayName("测试从nodeForms中提取数据")
    public void testFetchFromNodeForms() {
        List<AippNodeForms> nodeForms = new ArrayList<>();
        nodeForms.add(AippNodeForms.builder()
                .type(NodeTypes.START.getType())
                .metaInfo(List.of(new FormMetaInfo("start_form", "1.0.1")))
                .build());
        nodeForms.add(AippNodeForms.builder()
                .type(NodeTypes.END.getType())
                .metaInfo(List.of(new FormMetaInfo("end_form", "2.0.2")))
                .build());

        TaskEntity<?> entity = AppTask.asEntity();
        entity.fetch(nodeForms);

        assertEquals("start_form", entity.getStartFormId());
        assertEquals("1.0.1", entity.getStartFormVersion());
        assertEquals("end_form", entity.getEndFormId());
        assertEquals("2.0.2", entity.getEndFormVersion());
    }

    @Test
    @DisplayName("测试loadFrom")
    public void testLoadFrom() {
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime lastModificationTime = creationTime.plusDays(2);
        TaskProperty property = new TaskProperty();
        property.setId("property_id");
        Meta meta = new Meta();
        meta.setId("app_suite_id");
        meta.setVersionId("task_id");
        meta.setName("test_name");
        meta.setCategory(JaneCategory.AIPP.name());
        meta.setCreator("zy");
        meta.setLastModifier("wla");
        meta.setTenant("cloud");
        meta.setVersion("1.1.1");
        meta.setCreationTime(creationTime);
        meta.setLastModificationTime(lastModificationTime);
        meta.setAttributes(
                MapBuilder.<String, Object>get().put(AippConst.ATTR_FLOW_CONFIG_ID_KEY, "flow_config_id").build());
        meta.setProperties(List.of(property));

        TaskEntity<?> entity = AppTask.asEntity();
        entity.loadFrom(meta);

        assertEquals("app_suite_id", entity.getAppSuiteId());
        assertEquals("task_id", entity.getTaskId());
        assertEquals("test_name", entity.getName());
        assertEquals(JaneCategory.AIPP.name(), entity.getCategory());
        assertEquals("zy", entity.getCreator());
        assertEquals("wla", entity.getLastModifier());
        assertEquals("cloud", entity.getTenant());
        assertEquals("1.1.1", entity.getVersion());
        assertSame(creationTime, entity.getCreationTime());
        assertSame(lastModificationTime, entity.getLastModificationTime());
        assertEquals("flow_config_id", entity.getFlowConfigId());
        assertEquals("property_id", entity.getProperties().get(0).getId());
    }
}
