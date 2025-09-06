/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * 为 {@link UndefinableValue} 提供表示已定义的值的实现。
 *
 * @param <T> 表示值的实际类型。
 * @author 陈镕希
 * @since 2023-08-07
 */
class DefinedValue<T> implements UndefinableValue<T> {
    private final T value;

    DefinedValue(T value) {
        this.value = value;
    }

    @Override
    public boolean defined() {
        return true;
    }

    @Override
    public T get() {
        return this.value;
    }

    @Override
    public <R> UndefinableValue<R> map(Function<T, R> mapper) {
        notNull(mapper, "The mapper to convert undefinable value cannot be null.");
        R result = mapper.apply(this.value);
        return UndefinableValue.defined(result);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DefinedValue) {
            DefinedValue<?> another = (DefinedValue<?>) obj;
            return Objects.equals(this.value, another.value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.get()});
    }

    @Override
    public String toString() {
        return StringUtils.format("[defined] {0}", this.value);
    }
}
