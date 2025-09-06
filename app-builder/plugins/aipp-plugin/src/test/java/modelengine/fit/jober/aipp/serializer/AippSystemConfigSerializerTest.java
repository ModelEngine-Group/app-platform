/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer;

import modelengine.fit.jober.aipp.domain.AippSystemConfig;
import modelengine.fit.jober.aipp.po.AippSystemConfigPo;
import modelengine.fit.jober.aipp.serializer.impl.AippSystemConfigSerializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * AippSystemConfigSerializerTest
 *
 * @author 张越
 * @since 2024-12-04
 */
@DisplayName("系统配置序列化器测试类")
public class AippSystemConfigSerializerTest {
    private static AippSystemConfigSerializer aippSystemConfigSerializer;

    @BeforeAll
    public static void beforeAll() {
        aippSystemConfigSerializer = new AippSystemConfigSerializer();
    }

    @Test
    @DisplayName("测试serialize正常返回")
    void shouldOkWhenSerialize() {
        AippSystemConfigPo po = aippSystemConfigSerializer.serialize(AippSystemConfig.builder().id(1L).build());
        Assertions.assertEquals(1L, (long) po.getId());
    }

    @Test
    @DisplayName("测试serialize返回null")
    void shouldReturnNullWhenSerialize() {
        AippSystemConfigPo po = aippSystemConfigSerializer.serialize(null);
        Assertions.assertNull(po);
    }

    @Test
    @DisplayName("测试deSerialize正常返回")
    void shouldOkWhenDeSerialize() {
        AippSystemConfig config = aippSystemConfigSerializer.deserialize(AippSystemConfigPo.builder().id(1L).build());
        Assertions.assertEquals(1L, (long) config.getId());
    }

    @Test
    @DisplayName("测试deSerialize返回null")
    void shouldReturnNullWhenDeSerialize() {
        AippSystemConfig config = aippSystemConfigSerializer.deserialize(null);
        Assertions.assertNull(config);
    }
}
