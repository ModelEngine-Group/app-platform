/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 表示支持未定义的值。
 *
 * @author 梁济时
 * @since 2023-11-14
 */
public class Undefinable<T> {
    private Boolean defined;

    private T value;

    public Undefinable() {
        this(null, null);
    }

    public Undefinable(Boolean defined, T value) {
        this.defined = defined;
        this.value = value;
    }

    /**
     * 返回一个表示未定义的值。
     *
     * @param <T> 表示值的类型。
     * @return 表示未定义的值的 {@link Undefinable}。
     */
    public static <T> Undefinable<T> undefined() {
        return new Undefinable<>(false, null);
    }

    /**
     * 返回一个表示已定义的值。
     *
     * @param value 表示实际值的 {@link T}。
     * @param <T> 表示值的类型。
     * @return 表示已定义的值的 {@link Undefinable}。
     */
    public static <T> Undefinable<T> defined(T value) {
        return new Undefinable<>(true, value);
    }

    /**
     * 获取一个值，该值指示值是否被定义。
     *
     * @return 若为 {@code true}，表示值被定义，否则表示值未被定义。
     */
    public Boolean getDefined() {
        return this.defined;
    }

    /**
     * 设置一个值，该值指示值是否被定义。
     *
     * @param defined 若值被定义，则为 {@code true}，否则为 {@code null} 或 {@code false}。
     */
    public void setDefined(Boolean defined) {
        this.defined = defined;
    }

    /**
     * 获取被定义的值。
     *
     * @return 表示被定义的值的 {@link T}。
     */
    public T getValue() {
        return this.value;
    }

    /**
     * 设置被定义的值。
     *
     * @param value 表示被定义的值的 {@link T}。
     */
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            Undefinable<?> another = (Undefinable<?>) obj;
            return Objects.equals(this.getDefined(), another.getDefined()) && Objects.equals(this.getValue(),
                    another.getValue());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.getDefined(), this.getValue()});
    }

    @Override
    public String toString() {
        return StringUtils.format("[defined={0}, value={1}]", this.getDefined(), this.getValue());
    }
}
