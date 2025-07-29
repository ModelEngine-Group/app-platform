/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.domains.taskinstance.service.impl.AppTaskInstanceServiceImpl;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.model.support.DefaultRange;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * {@link AppTaskInstanceService} 的测试类。
 *
 * @author 张越
 * @since 2025-01-13
 */
public class AppTaskInstanceServiceTest {
    private AppTaskInstanceService appTaskInstanceService;

    private MetaInstanceService metaInstanceService;

    @BeforeEach
    public void setUp() {
        this.metaInstanceService = mock(MetaInstanceService.class);
        AppTaskInstanceFactory factory = new AppTaskInstanceFactory(null, null, null, null, null);
        this.appTaskInstanceService = new AppTaskInstanceServiceImpl(this.metaInstanceService, factory);
    }

    @Test
    @DisplayName("测试getInstance方法，当不存在时返回Optional.empty()")
    public void testGetInstanceShouldReturnOptionalEmptyIfNotExists() {
        // given.
        when(this.metaInstanceService.list(any(), any(), eq(0L), eq(1), any())).thenReturn(
                RangedResultSet.create(new ArrayList<>(), new DefaultRange(0, 0), 0));

        // when.
        Optional<AppTaskInstance> optionalAppTaskInstance = this.appTaskInstanceService.getInstance("task_id",
                "task_instance_id", new OperationContext());

        // then.
        assertTrue(optionalAppTaskInstance.isEmpty());
    }

    @Test
    @DisplayName("测试getInstance方法，当存在时返回Optional.present")
    public void testGetInstanceShouldReturnOptionalPresentIfExists() {
        // given.
        Instance instance = new Instance();
        String taskInstanceId = "task_instance_id";
        instance.setId(taskInstanceId);
        when(this.metaInstanceService.retrieveById(eq(taskInstanceId), any())).thenReturn(instance);

        // when.
        Optional<AppTaskInstance> optionalAppTaskInstance = this.appTaskInstanceService.getInstance("task_id",
                taskInstanceId, new OperationContext());

        // then.
        assertTrue(optionalAppTaskInstance.isPresent());
        assertEquals(taskInstanceId, optionalAppTaskInstance.get().getId());
        assertEquals("task_id", optionalAppTaskInstance.get().getTaskId());
    }

    @Test
    @DisplayName("测试getInstancesByTaskId方法")
    public void testGetInstancesByTaskIdShouldReturnList() {
        // given.
        Instance instance = new Instance();
        instance.setId("task_instance_id");

        Instance instance1 = new Instance();
        instance1.setId("task_instance_id_1");

        Instance instance2 = new Instance();
        instance2.setId("task_instance_id_2");
        when(this.metaInstanceService.list(eq("task_id"), anyLong(), anyInt(), any())).thenReturn(
                        RangedResultSet.create(List.of(instance, instance1), new DefaultRange(0, 2), 3))
                .thenReturn(RangedResultSet.create(List.of(instance2), new DefaultRange(0, 2), 3));

        // when.
        List<AppTaskInstance> instances = this.appTaskInstanceService.getInstancesByTaskId("task_id", 2,
                new OperationContext());

        // then.
        assertEquals(3, instances.size());
        assertEquals("task_instance_id", instances.get(0).getId());
        assertEquals("task_instance_id_1", instances.get(1).getId());
        assertEquals("task_instance_id_2", instances.get(2).getId());
        verify(this.metaInstanceService, times(2)).list(eq("task_id"), anyLong(), anyInt(), any());
    }

    @Test
    @DisplayName("测试getInstanceStreamByTaskId方法")
    public void testGetInstanceStreamByTaskIdShouldReturnList() {
        // given.
        Instance instance = new Instance();
        instance.setId("task_instance_id");

        Instance instance1 = new Instance();
        instance1.setId("task_instance_id_1");

        when(this.metaInstanceService.list(eq("task_id"), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(List.of(instance, instance1), new DefaultRange(0, 2), 2));

        // when.
        Stream<AppTaskInstance> instanceStream = this.appTaskInstanceService.getInstanceStreamByTaskId("task_id", 2,
                new OperationContext());

        List<AppTaskInstance> instances = instanceStream.toList();

        // then.
        assertEquals(2, instances.size());
        assertEquals("task_instance_id", instances.get(0).getId());
        assertEquals("task_instance_id_1", instances.get(1).getId());
        verify(this.metaInstanceService, times(1)).list(eq("task_id"), anyLong(), anyInt(), any());
    }

    @Test
    @DisplayName("测试update方法")
    public void testUpdateShouldOk() {
        // given.
        AppTaskInstance instance = AppTaskInstance.asUpdate("task_id", "task_instance_id")
                .setStatus("active")
                .setName("task_name")
                .build();

        // when.
        this.appTaskInstanceService.update(instance, new OperationContext());

        // then.
        ArgumentCaptor<InstanceDeclarationInfo> captor = ArgumentCaptor.forClass(InstanceDeclarationInfo.class);
        verify(this.metaInstanceService, times(1)).patchMetaInstance(eq("task_id"), eq("task_instance_id"),
                captor.capture(), any());

        InstanceDeclarationInfo info = captor.getValue();
        assertEquals("task_name", info.getInfo().getValue().get(AippConst.INST_NAME_KEY));
        assertEquals("active", info.getInfo().getValue().get(AippConst.INST_STATUS_KEY));
    }

    @Test
    @DisplayName("测试createInstance方法")
    public void testCreateInstanceShouldOk() {
        // given.
        AppTaskInstance appTaskInstance = AppTaskInstance.asCreate("task_id", "zy", "task_name").build();
        Instance instance = new Instance();
        instance.setId("task_instance_id");
        instance.setInfo(MapBuilder.<String, String>get()
                .put(AippConst.INST_NAME_KEY, "task_name")
                .put(AippConst.INST_CREATOR_KEY, "zy")
                .build());
        when(this.metaInstanceService.createMetaInstance(any(), any(), any())).thenReturn(instance);

        // when.
        AppTaskInstance result = this.appTaskInstanceService.createInstance(appTaskInstance, new OperationContext());

        // then.
        assertEquals("task_instance_id", result.getId());
        assertEquals("task_id", result.getTaskId());
        assertEquals("task_name", result.getEntity().getName());
        assertEquals("zy", result.getEntity().getCreator());
    }

    @Test
    @DisplayName("测试delete方法")
    public void testDeleteShouldOk() {
        // given.
        // when.
        this.appTaskInstanceService.delete("task_id", "task_instance_id", new OperationContext());

        // then.
        verify(this.metaInstanceService, times(1)).deleteMetaInstance(eq("task_id"), eq("task_instance_id"), any());
    }

    @Test
    @DisplayName("测试getTaskId方法")
    public void testGetTaskIdShouldOk() {
        // given.
        when(this.metaInstanceService.getMetaVersionId(anyString())).thenReturn("task_id");

        // when.
        String taskId = this.appTaskInstanceService.getTaskId("task_instance_id");

        // then.
        assertEquals("task_id", taskId);
    }
}
