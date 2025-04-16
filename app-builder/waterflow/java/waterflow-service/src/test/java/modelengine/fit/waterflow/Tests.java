/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.waterflow.utils.Dates;
import modelengine.fit.waterflow.utils.Entities;
import org.mockito.ArgumentMatcher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 为单元测试提供工具方法。
 *
 * @author 梁济时
 * @since 2023-12-21
 */
public final class Tests {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Tests() {
    }

    /**
     * 匹配参数
     *
     * @param expected 期望参数
     * @param <T> 泛型类型
     * @return 参数列表
     */
    @SafeVarargs
    public static <T> ArgumentMatcher<List<T>> matchArguments(T... expected) {
        return args -> equals(expected, args);
    }

    private static <T> boolean equals(T[] expected, List<T> actual) {
        if (actual.size() != expected.length) {
            return false;
        }
        for (int i = 0; i < actual.size(); i++) {
            if (!equals(expected[i], actual.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean equals(Object expected, Object actual) {
        if (expected instanceof Predicate) {
            Predicate<Object> predicate = cast(expected);
            return predicate.test(actual);
        } else {
            return Objects.equals(expected, actual);
        }
    }

    /**
     * 是不是id
     *
     * @return bool结果
     */
    public static Predicate<String> isId() {
        return Entities::isId;
    }

    /**
     * earlierUtc
     *
     * @return 更早的时间
     */
    public static Predicate<LocalDateTime> earlierUtc() {
        return value -> {
            LocalDateTime now = LocalDateTime.now();
            now = Dates.toUtc(now);
            return !value.isAfter(now);
        };
    }
}
