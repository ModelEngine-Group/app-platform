/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 为 {@link Undefinable} 提供工具方法。
 *
 * @author 梁济时
 * @since 2023-11-14
 */
public final class Undefinables {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Undefinables() {
    }

    /**
     * 检查指定值是否被定义。
     *
     * @param value 表示待检查的值的 {@link Undefinable}。
     * @return 若值被定义，则为 {@code true}，否则为 {@code false}。
     */
    public static boolean isDefined(Undefinable<?> value) {
        return Optional.ofNullable(value).map(Undefinable::getDefined).orElse(false);
    }

    /**
     * 获取被定义的值。
     *
     * @param value 表示待检查的值的 {@link Undefinable}。
     * @param exceptionSupplier 表示当值未被定义时抛出异常的提供程序的 {@link Supplier}。
     * @param <T> 表示值的类型。
     * @return 表示已定义的值的 {@link T}，在值已被定义时，其也可能为 {@code null}。
     */
    public static <T> T require(Undefinable<T> value, Supplier<? extends RuntimeException> exceptionSupplier) {
        return Optional.ofNullable(value).filter(Undefinables::isDefined).orElseThrow(exceptionSupplier).getValue();
    }

    /**
     * 当指定的值未被定义，或者已定义的值为 {@code null} 时，使用默认值。
     *
     * @param value 表示待检查的值的 {@link Undefinable}。
     * @param defaultValue 表示值未定义或为 {@code null} 时使用的默认值的 {@link T}。
     * @param <T> 表示值的类型。
     * @return 表示应用了默认值后的值的 {@link T}。
     */
    public static <T> T withDefault(Undefinable<T> value, T defaultValue) {
        return Optional.ofNullable(value)
                .filter(Undefinables::isDefined)
                .map(Undefinable::getValue)
                .orElse(defaultValue);
    }

    /**
     * 当值被定义时，使用指定方法消费该值。
     *
     * @param value 表示待检查的值的 {@link Undefinable}。
     * @param consumer 表示用以消费已定义的值的方法的 {@link Consumer}。
     * @param <T> 表示被定义的值的类型。
     */
    public static <T> void whenDefined(Undefinable<T> value, Consumer<T> consumer) {
        if (isDefined(value)) {
            consumer.accept(value.getValue());
        }
    }
}
