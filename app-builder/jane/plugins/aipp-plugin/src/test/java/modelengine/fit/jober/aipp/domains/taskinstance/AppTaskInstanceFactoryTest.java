/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jober.aipp.constants.AippConst;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link AppTaskInstanceFactory} 的测试类。
 *
 * @author 张越
 * @since 2025-01-12
 */
public class AppTaskInstanceFactoryTest {
    private AppTaskInstanceFactory factory;

    @BeforeEach
    public void setUp() {
        this.factory = new AppTaskInstanceFactory(null, null, null, null, null);
    }

    @Test
    @DisplayName("测试AppTaskInstance转换为declarationInfo")
    public void testToDeclarationInfo() {
        LocalDateTime creatTime = LocalDateTime.now();
        AppTaskInstance appTaskInstance = AppTaskInstance.asEntity()
                .setName("instance1")
                .setCreateTime(creatTime)
                .putTags(List.of("tag1"))
                .build();
        InstanceDeclarationInfo info = this.factory.toDeclarationInfo(appTaskInstance);
        assertEquals(creatTime, info.getInfo().getValue().get(AippConst.INST_CREATE_TIME_KEY));
        assertEquals("instance1", info.getInfo().getValue().get(AippConst.INST_NAME_KEY));
        assertEquals(1, info.getTags().getValue().size());
        assertEquals("tag1", info.getTags().getValue().get(0));
    }

    @Test
    @DisplayName("测试创建AppTaskInstance")
    public void testCreate() {
        Instance instance = new Instance();
        instance.setId("instance1");
        AppTaskInstance appTaskInstance = this.factory.create(instance, "taskId1", null);
        assertEquals("instance1", appTaskInstance.getId());
        assertEquals("instance1", appTaskInstance.getEntity().getInstanceId());
        assertEquals("taskId1", appTaskInstance.getTaskId());
        assertEquals("taskId1", appTaskInstance.getEntity().getTaskId());
    }
}
