/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import lombok.Data;
import modelengine.fit.jober.aipp.common.exception.AippJsonDecodeException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

/**
 * {@link JsonUtils} 的单元测试类
 *
 * @author 姚江
 * @since 2024-07-17
 */
@ExtendWith(MockitoExtension.class)
public class JsonUtilsTest {
    @Test
    @DisplayName("测试String转Map")
    void testParseObject() {
        String str = "{\"hello\": \"world\"}";
        Map<String, Object> map = Assertions.assertDoesNotThrow(() -> JsonUtils.parseObject(str));
        Assertions.assertTrue(map.containsKey("hello"));
        Assertions.assertEquals("world", map.get("hello"));
    }

    @Test
    @DisplayName("测试String转Map失败")
    void testParseObjectFailed() {
        String str = "{\"hello\": \"world\"";
        AippJsonDecodeException aippJsonDecodeException =
                Assertions.assertThrows(AippJsonDecodeException.class, () -> JsonUtils.parseObject(str));
        Assertions.assertEquals(90002900, aippJsonDecodeException.getCode());
    }

    @Test
    @DisplayName("测试String转自定义类型")
    void testParseObjectClazz() {
        String str = "{\"hello\": \"world\", \"start\": 2}";
        TestEntity map = Assertions.assertDoesNotThrow(() -> JsonUtils.parseObject(str, TestEntity.class));
        Assertions.assertEquals("world", map.getHello());
        Assertions.assertEquals(2, map.getStart());
    }

    @Test
    @DisplayName("测试String转自定义类型失败")
    void testParseObjectClazzFailed() {
        String str = "{\"hello\": \"world\"";
        AippJsonDecodeException aippJsonDecodeException = Assertions.assertThrows(AippJsonDecodeException.class,
                () -> JsonUtils.parseObject(str, TestEntity.class));
        Assertions.assertEquals(90002900, aippJsonDecodeException.getCode());
    }

    @Test
    @DisplayName("测试String转自定义列表")
    void testParseArrayClazz() {
        String str = "[{\"hello\": \"world\", \"start\": 2}]";
        List<TestEntity> list = Assertions.assertDoesNotThrow(() -> JsonUtils.parseArray(str, TestEntity[].class));
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("world", list.get(0).getHello());
        Assertions.assertEquals(2, list.get(0).getStart());
    }

    @Test
    @DisplayName("测试String转自定义列表失败")
    void testParseArrayClazzFailed() {
        String str = "{\"hello\": \"world\"";
        AippJsonDecodeException aippJsonDecodeException = Assertions.assertThrows(AippJsonDecodeException.class,
                () -> JsonUtils.parseArray(str, TestEntity[].class));
        Assertions.assertEquals(90002900, aippJsonDecodeException.getCode());
    }

    @Data
    private static class TestEntity {
        private String hello;
        private Integer start;
    }
}
