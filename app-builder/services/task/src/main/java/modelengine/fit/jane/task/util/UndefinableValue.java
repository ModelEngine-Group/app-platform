/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 支持未定义的值。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
public interface UndefinableValue<T> {
    /**
     * 返回一个表示未定义的值。
     *
     * @param <T> 表示值的类型。
     * @return 表示未定义的值的 {@link UndefinableValue}。
     */
    static <T> UndefinableValue<T> undefined() {
        return UndefinedValue.instance();
    }

    /**
     * 返回一个表示已定义的值。
     *
     * @param value 表示实际值的 {@link T}。
     * @param <T> 表示值的类型。
     * @return 表示已定义的值的 {@link UndefinableValue}。
     */
    static <T> UndefinableValue<T> defined(T value) {
        return new DefinedValue<>(value);
    }

    /**
     * require
     *
     * @param value value
     * @param exceptionSupplier exceptionSupplier
     * @return T
     */
    static <T> T require(UndefinableValue<T> value, Supplier<RuntimeException> exceptionSupplier) {
        if (value == null || !value.defined()) {
            throw exceptionSupplier.get();
        } else {
            return value.get();
        }
    }

    /**
     * 如果值已被定义，使用指定的方法消费已定义的值。
     *
     * @param value 表示待检查的值的 {@link UndefinableValue}。
     * @param consumer 表示用以消费值的方法的 {@link Consumer}。
     * @param <T> 表示值的类型。
     */
    static <T> void ifDefined(UndefinableValue<T> value, Consumer<T> consumer) {
        if (value != null && value.defined()) {
            consumer.accept(value.get());
        }
    }

    /**
     * withDefault
     *
     * @param value value
     * @param defaultValue defaultValue
     * @return T
     */
    static <T> T withDefault(UndefinableValue<T> value, T defaultValue) {
        if (value == null || !value.defined() || value.get() == null) {
            return defaultValue;
        } else {
            return value.get();
        }
    }

    /**
     * 获取一个值，该值指示是否已经定义了值。
     *
     * @return 若已经定义了值，则为 {@code true}，否则为 {@code false}。
     */
    boolean defined();

    /**
     * 获取已定义的值。
     * <p>仅当 {@link #defined()} 为 {@code true} 时有意义。</p>
     *
     * @return 表示已定义的值的 {@link T}。
     */
    T get();

    /**
     * 需要值被定义，否则抛出异常。
     *
     * @param exceptionSupplier 表示当值未被定义时，创建用以抛出的异常的方法的 {@link Supplier}。
     * @return 表示已定义的值的 {@link T}。
     */
    default T required(Supplier<RuntimeException> exceptionSupplier) {
        if (this.defined()) {
            return this.get();
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 使用默认值。
     * <p>当值未被定义，或已定义的值为 {@code null} 时，将使用默认值。</p>
     *
     * @param defaultValue 表示默认值的 {@link T}。
     * @return 表示最终使用的值的 {@link T}。
     */
    default T withDefault(T defaultValue) {
        if (this.defined() && this.get() != null) {
            return this.get();
        } else {
            return defaultValue;
        }
    }

    /**
     * 如果存在值，则使用该值调用指定的消费者，否则不执行任何操作。
     *
     * @param consumer 指定消费者的 {@link Consumer}。
     */
    default void ifDefined(Consumer<T> consumer) {
        ifDefined(this, consumer);
    }

    /**
     * map
     *
     * @param mapper mapper
     * @return UndefinableValue<R>
     */
    <R> UndefinableValue<R> map(Function<T, R> mapper);
}

