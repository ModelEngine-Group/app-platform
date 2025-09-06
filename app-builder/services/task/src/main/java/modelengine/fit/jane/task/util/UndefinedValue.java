/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.util;

import java.util.function.Function;

/**
 * 为 {@link UndefinableValue} 提供表示未定义的值的实现。
 *
 * @param <T> 表示值的实际类型。
 * @author 陈镕希
 * @since 2023-08-07
 */
class UndefinedValue<T> implements UndefinableValue<T> {
    private static final UndefinedValue<?> INSTANCE = new UndefinedValue<>();

    static <T> UndefinedValue<T> instance() {
        return (UndefinedValue<T>) INSTANCE;
    }

    @Override
    public boolean defined() {
        return false;
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public <R> UndefinableValue<R> map(Function<T, R> mapper) {
        return UndefinableValue.undefined();
    }

    @Override
    public String toString() {
        return "[undefined]";
    }
}
