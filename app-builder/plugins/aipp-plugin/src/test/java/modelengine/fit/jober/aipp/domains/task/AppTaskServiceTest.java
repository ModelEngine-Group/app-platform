/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

import static modelengine.fit.jober.aipp.enums.AippMetaStatusEnum.INACTIVE;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.PREVIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.task.service.impl.AppTaskServiceImpl;
import modelengine.fit.jober.aipp.enums.JaneCategory;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * {@link AppTaskService} 的测试类。
 *
 * @author 张越
 * @since 2025-01-13
 */
public class AppTaskServiceTest {
    private MetaService metaService;
    private AppTaskService appTaskService;

    @BeforeEach
    public void setUp() {
        AppTaskFactory factory = new AppTaskFactory(null, null, null, null, null, null, null, null);
        this.metaService = mock(MetaService.class);
        this.appTaskService = new AppTaskServiceImpl(this.metaService, factory);
    }

    @Test
    @DisplayName("测试创建方法.")
    public void testCreate() {
        // given.
        Meta meta = new Meta();
        meta.setId("app_suite_id_1");
        meta.setVersionId("task_1");
        when(this.metaService.create(any(), any())).thenReturn(meta);

        // when.
        AppTask task = this.appTaskService.createTask(AppTask.asEntity().build(), new OperationContext());

        // then.
        assertEquals("app_suite_id_1", task.getEntity().getAppSuiteId());
        assertEquals("task_1", task.getEntity().getTaskId());
    }

    @Test
    @DisplayName("测试修改方法.")
    public void testUpdate() {
        // given.
        doNothing().when(this.metaService).patch(any(), any(), any());

        // when.
        this.appTaskService.updateTask(
                AppTask.asEntity().setTaskId("task_1").setName("my_task").setCategory(JaneCategory.AIPP.name()).build(),
                new OperationContext());

        // then.
        ArgumentCaptor<MetaDeclarationInfo> captor = ArgumentCaptor.forClass(MetaDeclarationInfo.class);
        verify(this.metaService).patch(eq("task_1"), captor.capture(), any());
        MetaDeclarationInfo declarationInfo = captor.getValue();
        assertEquals("my_task", declarationInfo.getName().getValue());
        assertEquals(JaneCategory.AIPP.name(), declarationInfo.getCategory().getValue());
    }

    @Test
    @DisplayName("测试删除方法.")
    public void testDelete() {
        // given.
        doNothing().when(this.metaService).delete(any(), any());

        // when.
        this.appTaskService.deleteTaskById("task_1", new OperationContext());

        // then.
        verify(this.metaService).delete(eq("task_1"), any());
    }

    @Test
    @DisplayName("测试getLatestCreate方法，通过appSuiteId、aippType、status查询.")
    public void testGetLatestCreate() {
        // given.
        doNothing().when(this.metaService).delete(any(), any());
        Meta meta = new Meta();
        meta.setId("app_suite_id_1");
        meta.setVersionId("task_1");
        meta.setVersion("1.0");
        meta.setCategory(JaneCategory.AIPP.name());
        meta.setAttributes(MapBuilder.<String, Object>get().put(AippConst.ATTR_AIPP_TYPE_KEY, NORMAL.name()).build());
        when(this.metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(List.of(meta), 0, 1, 1));

        // when.
        Optional<AppTask> result = this.appTaskService.getLatestCreate("app_suite_id_1", NORMAL.name(),
                INACTIVE.getCode(), new OperationContext());

        // then.
        assertTrue(result.isPresent());
        assertEquals("app_suite_id_1", result.get().getEntity().getAppSuiteId());
        assertEquals("task_1", result.get().getEntity().getTaskId());
        assertEquals("1.0", result.get().getEntity().getVersion());
        assertEquals(NORMAL.name(), result.get().getEntity().getAippType());

        ArgumentCaptor<MetaFilter> captor = ArgumentCaptor.forClass(MetaFilter.class);
        verify(this.metaService).list(captor.capture(), anyBoolean(), anyLong(), anyInt(), any());

        MetaFilter metaFilter = captor.getValue();
        assertEquals("app_suite_id_1", metaFilter.getMetaIds().get(0));
        assertEquals(NORMAL.name(), metaFilter.getAttributes().get(AippConst.ATTR_AIPP_TYPE_KEY).get(0));
        assertEquals(INACTIVE.getCode(), metaFilter.getAttributes().get(AippConst.ATTR_META_STATUS_KEY).get(0));
        assertEquals("desc(created_at)", metaFilter.getOrderBys().get(0));
        assertEquals(JaneCategory.AIPP.name(), metaFilter.getCategories().get(0));
    }

    @Test
    @DisplayName("测试getLatestCreate方法，通过appSuiteId、aippType查询.")
    public void testGetLatestCreateUseAppSuiteIdAndAippType() {
        // given.
        doNothing().when(this.metaService).delete(any(), any());
        Meta meta = new Meta();
        meta.setId("app_suite_id_1");
        meta.setVersionId("task_1");
        meta.setVersion("1.0");
        meta.setCategory(JaneCategory.AIPP.name());
        meta.setAttributes(MapBuilder.<String, Object>get()
                .put(AippConst.ATTR_AIPP_TYPE_KEY, NORMAL.name())
                .put(AippConst.ATTR_META_STATUS_KEY, INACTIVE.getCode())
                .build());
        when(this.metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(List.of(meta), 0, 1, 1));

        // when.
        Optional<AppTask> result = this.appTaskService.getLatestCreate("app_suite_id_1", NORMAL.name(),
                new OperationContext());

        // then.
        assertTrue(result.isPresent());
        assertEquals("app_suite_id_1", result.get().getEntity().getAppSuiteId());
        assertEquals("task_1", result.get().getEntity().getTaskId());
        assertEquals("1.0", result.get().getEntity().getVersion());
        assertEquals(NORMAL.name(), result.get().getEntity().getAippType());
        assertEquals(INACTIVE.getCode(), result.get().getEntity().getStatus());

        ArgumentCaptor<MetaFilter> captor = ArgumentCaptor.forClass(MetaFilter.class);
        verify(this.metaService).list(captor.capture(), anyBoolean(), anyLong(), anyInt(), any());

        MetaFilter metaFilter = captor.getValue();
        assertEquals("app_suite_id_1", metaFilter.getMetaIds().get(0));
        assertEquals(NORMAL.name(), metaFilter.getAttributes().get(AippConst.ATTR_AIPP_TYPE_KEY).get(0));
        assertEquals("desc(created_at)", metaFilter.getOrderBys().get(0));
        assertEquals(JaneCategory.AIPP.name(), metaFilter.getCategories().get(0));
    }

    @Test
    @DisplayName("测试getLatest方法，通过uniqueName查询.")
    public void testGetLatestUserUniqueName() {
        // given.
        String uniqueName = UUID.randomUUID().toString();
        doNothing().when(this.metaService).delete(any(), any());
        Meta meta = new Meta();
        meta.setAttributes(MapBuilder.<String, Object>get()
                .put(AippConst.ATTR_UNIQUE_NAME, uniqueName)
                .build());
        when(this.metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(List.of(meta), 0, 1, 1));

        // when.
        Optional<AppTask> result = this.appTaskService.getLatest(uniqueName, new OperationContext());

        // then.
        assertTrue(result.isPresent());
        assertEquals(uniqueName, result.get().getEntity().getUniqueName());
        ArgumentCaptor<MetaFilter> captor = ArgumentCaptor.forClass(MetaFilter.class);
        verify(this.metaService).list(captor.capture(), anyBoolean(), anyLong(), anyInt(), any());
        MetaFilter metaFilter = captor.getValue();
        assertEquals(uniqueName, metaFilter.getAttributes().get(AippConst.ATTR_UNIQUE_NAME).get(0));
    }

    @Test
    @DisplayName("测试getLatest方法，通过appSuiteId、version查询.")
    public void testGetLatestUseAppSuiteIdAndVersion() {
        // given.
        doNothing().when(this.metaService).delete(any(), any());
        Meta meta = new Meta();
        meta.setId("app_suite_id_1");
        meta.setVersion("1.0");
        when(this.metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(List.of(meta), 0, 1, 1));

        // when.
        Optional<AppTask> result = this.appTaskService.getLatest("app_suite_id_1", "1.0", new OperationContext());

        // then.
        assertTrue(result.isPresent());
        assertEquals("app_suite_id_1", result.get().getEntity().getAppSuiteId());
        assertEquals("1.0", result.get().getEntity().getVersion());
        ArgumentCaptor<MetaFilter> captor = ArgumentCaptor.forClass(MetaFilter.class);
        verify(this.metaService).list(captor.capture(), anyBoolean(), anyLong(), anyInt(), any());
        MetaFilter metaFilter = captor.getValue();
        assertEquals(JaneCategory.AIPP.name(), metaFilter.getCategories().get(0));
        assertEquals("desc(updated_at)", metaFilter.getOrderBys().get(0));
    }

    @Test
    @DisplayName("测试getTaskList方法，通过appSuiteId、aippType、status查询.")
    public void testGetTaskList() {
        // given.
        doNothing().when(this.metaService).delete(any(), any());
        Meta meta = new Meta();
        when(this.metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(List.of(meta), 0, 1, 1));

        // when.
        List<AppTask> result = this.appTaskService.getTaskList("app_suite_id_1", NORMAL.name(), INACTIVE.getCode(),
                new OperationContext());

        // then.
        assertEquals(1, result.size());
        ArgumentCaptor<MetaFilter> captor = ArgumentCaptor.forClass(MetaFilter.class);
        verify(this.metaService, times(1)).list(captor.capture(), anyBoolean(), anyLong(), anyInt(), any());
        MetaFilter metaFilter = captor.getValue();
        assertEquals(JaneCategory.AIPP.name(), metaFilter.getCategories().get(0));
        assertEquals("desc(created_at)", metaFilter.getOrderBys().get(0));
        assertEquals(NORMAL.name(), metaFilter.getAttributes().get(AippConst.ATTR_AIPP_TYPE_KEY).get(0));
        assertEquals(INACTIVE.getCode(), metaFilter.getAttributes().get(AippConst.ATTR_META_STATUS_KEY).get(0));
    }

    @Test
    @DisplayName("测试getTaskList方法，通过query查询.")
    public void testGetTaskListUseQuery() {
        // given.
        doNothing().when(this.metaService).delete(any(), any());
        Meta meta = new Meta();
        when(this.metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(List.of(meta), 0, 1, 1));

        // when.
        List<AppTask> result = this.appTaskService.getTaskList(AppTask.asQueryEntity(0L, 1).latest().build(),
                new OperationContext());

        // then.
        assertEquals(1, result.size());
        verify(this.metaService, times(1)).list(any(), eq(true), eq(0L), eq(10), any());
    }

    @Test
    @DisplayName("测试getPreview方.")
    public void testGetPreviewTask() {
        // given.
        doNothing().when(this.metaService).delete(any(), any());
        Meta meta = new Meta();
        when(this.metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(List.of(meta), 0, 1, 1));

        // when.
        List<AppTask> result = this.appTaskService.getPreviewTasks("app_suite_id_1", new OperationContext());

        // then.
        assertEquals(1, result.size());
        ArgumentCaptor<MetaFilter> captor = ArgumentCaptor.forClass(MetaFilter.class);
        verify(this.metaService, times(1)).list(captor.capture(), anyBoolean(), anyLong(), anyInt(), any());
        MetaFilter metaFilter = captor.getValue();
        assertEquals(JaneCategory.AIPP.name(), metaFilter.getCategories().get(0));
        assertEquals("desc(updated_at)", metaFilter.getOrderBys().get(0));
        assertEquals(PREVIEW.name(), metaFilter.getAttributes().get(AippConst.ATTR_AIPP_TYPE_KEY).get(0));
    }

    @Test
    @DisplayName("测试getTasksByAppId方.")
    public void testGetTasksByAppId() {
        // given.
        doNothing().when(this.metaService).delete(any(), any());
        Meta meta = new Meta();
        when(this.metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(List.of(meta), 0, 1, 1));

        // when.
        List<AppTask> result = this.appTaskService.getTasksByAppId("app_id_1", new OperationContext());

        // then.
        assertEquals(1, result.size());
        ArgumentCaptor<MetaFilter> captor = ArgumentCaptor.forClass(MetaFilter.class);
        verify(this.metaService, times(1)).list(captor.capture(), anyBoolean(), anyLong(), anyInt(), any());
        MetaFilter metaFilter = captor.getValue();
        assertEquals("desc(created_at)", metaFilter.getOrderBys().get(0));
        assertEquals("app_id_1", metaFilter.getAttributes().get(AippConst.ATTR_APP_ID_KEY).get(0));
    }

    @Test
    @DisplayName("测试getTasksByAppId方，通过appId和aippType.")
    public void testGetTasksByAppIdAndAippType() {
        // given.
        doNothing().when(this.metaService).delete(any(), any());
        Meta meta = new Meta();
        when(this.metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(List.of(meta), 0, 1, 1));

        // when.
        List<AppTask> result = this.appTaskService.getTasksByAppId("app_id_1", NORMAL.name(), new OperationContext());

        // then.
        assertEquals(1, result.size());
        ArgumentCaptor<MetaFilter> captor = ArgumentCaptor.forClass(MetaFilter.class);
        verify(this.metaService, times(1)).list(captor.capture(), anyBoolean(), anyLong(), anyInt(), any());
        MetaFilter metaFilter = captor.getValue();
        assertEquals("app_id_1", metaFilter.getAttributes().get(AippConst.ATTR_APP_ID_KEY).get(0));
        assertEquals(NORMAL.name(), metaFilter.getAttributes().get(AippConst.ATTR_AIPP_TYPE_KEY).get(0));
    }

    @Test
    @DisplayName("测试getTaskById.")
    public void testGetTasksById() {
        // given.
        doNothing().when(this.metaService).delete(any(), any());
        Meta meta = new Meta();
        meta.setId("app_suite_id_1");
        meta.setVersionId("task_1");
        when(this.metaService.retrieve(any(), any())).thenReturn(meta);

        // when.
        Optional<AppTask> result = this.appTaskService.getTaskById("task_1", new OperationContext());

        // then.
        assertTrue(result.isPresent());
        assertEquals("app_suite_id_1", result.get().getEntity().getAppSuiteId());
        assertEquals("task_1", result.get().getEntity().getTaskId());
        verify(this.metaService, times(1)).retrieve(eq("task_1"), any());
    }
}
