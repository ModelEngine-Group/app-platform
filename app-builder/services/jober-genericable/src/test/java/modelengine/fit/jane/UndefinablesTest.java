/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.jane.Undefinable;
import modelengine.fit.jane.Undefinables;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * UndefinablesTest
 *
 * @author 梁济时
 * @since 2023/11/28
 */
@DisplayName("测试 Undefinables 工具类")
class UndefinablesTest {
    @Test
    @DisplayName("当值为 null 时，检测结果为未定义")
    void should_return_false_when_value_is_null() {
        boolean result = Undefinables.isDefined(null);
        assertFalse(result);
    }

    @Test
    @DisplayName("当值的 defined 为 false 时，检测结果为未定义")
    void should_return_false_when_value_is_not_defined() {
        Undefinable<String> value = new Undefinable<>(false, null);
        boolean result = Undefinables.isDefined(value);
        assertFalse(result);
    }

    @Test
    @DisplayName("当值的 defined 为 true 时，检测结果为已定义")
    void should_return_true_when_value_is_defined() {
        Undefinable<String> value = new Undefinable<>(true, null);
        boolean result = Undefinables.isDefined(value);
        assertTrue(result);
    }

    @Test
    @DisplayName("当值非 defined 时，require 将抛出异常")
    void should_throw_when_require_undefined() {
        Undefinable<String> value = new Undefinable<>(false, null);
        IllegalStateException ex = new IllegalStateException();
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> Undefinables.require(value, () -> ex));
        assertSame(ex, thrown);
    }

    @Test
    @DisplayName("当值为 defined 时，且定义的值为 null，则返回 null")
    void should_return_null_when_require_defined_null() {
        Undefinable<String> value = new Undefinable<>(true, null);
        String actual = Undefinables.require(value, IllegalStateException::new);
        assertNull(actual);
    }

    @Test
    @DisplayName("当值被 defined 时，且为非 null 时，返回定义的值")
    void should_return_value_when_defined() {
        String expected = "hello";
        Undefinable<String> value = new Undefinable<>(true, expected);
        String actual = Undefinables.withDefault(value, "world");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("当值非 defined 时，则返回默认值")
    void should_return_default_value_when_undefined() {
        String expected = "hello";
        Undefinable<String> value = new Undefinable<>(false, null);
        String actual = Undefinables.withDefault(value, expected);
        assertEquals(expected, actual);
    }
}
