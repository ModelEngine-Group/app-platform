/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.JobberException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link MetaInstanceUtils} 的单元测试
 *
 * @author 姚江
 * @since 2024-07-17
 */
@ExtendWith(MockitoExtension.class)
public class MetaInstanceUtilsTest {
    @Mock
    private MetaInstanceService metaInstanceService;

    private OperationContext context;

    @BeforeEach
    void before() {
        context = new OperationContext();
        context.setOperator("UT XX1234567");
    }

    @Test
    @DisplayName("测试persistInstance方法")
    void testPersistInstance() {
        InstanceDeclarationInfo info = InstanceDeclarationInfo.custom().putInfo("hello", "world").build();
        Map<String, Object> businessData = new HashMap<>();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> MetaInstanceUtils.persistInstance(metaInstanceService, info, businessData, context));
        Assertions.assertEquals("Get blank meta version id", exception.getMessage());
        businessData.put(AippConst.BS_META_VERSION_ID_KEY, "version_id");
        IllegalArgumentException exception2 = Assertions.assertThrows(IllegalArgumentException.class,
                () -> MetaInstanceUtils.persistInstance(metaInstanceService, info, businessData, context));
        Assertions.assertEquals("Get blank aipp instance id", exception2.getMessage());
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, "instance_id");
        Assertions.assertDoesNotThrow(
                () -> MetaInstanceUtils.persistInstance(metaInstanceService, info, businessData, context));
    }

    @Test
    @DisplayName("测试getInstanceDetail方法失败：未查询到对应的instance")
    void testGetInstanceDetailWithoutResult() {
        Mockito.when(
                        metaInstanceService.list(Mockito.anyList(), Mockito.eq(0L),
                                Mockito.eq(1), Mockito.eq(context)))
                .thenReturn(RangedResultSet.create(new ArrayList<>(), 0L, 1, 0));
        JobberException exception = Assertions.assertThrows(JobberException.class,
                () -> MetaInstanceUtils.getInstanceDetail("versionId", "instanceId", context, metaInstanceService));
        Assertions.assertEquals(10000006, exception.getCode());
    }

    @Test
    @DisplayName("测试getInstanceDetail方法成功")
    void testGetInstanceDetail() {
        Instance instance = new Instance();
        instance.setId("instanceId");
        Mockito.when(
                        metaInstanceService.list(Mockito.anyList(), Mockito.eq(0L),
                                Mockito.eq(1), Mockito.eq(context)))
                .thenReturn(RangedResultSet.create(Collections.singletonList(instance), 0L, 1, 1));
        Instance result = Assertions.assertDoesNotThrow(
                () -> MetaInstanceUtils.getInstanceDetail("versionId", "instanceId", context, metaInstanceService));
        Assertions.assertEquals("instanceId", result.getId());
    }
}
