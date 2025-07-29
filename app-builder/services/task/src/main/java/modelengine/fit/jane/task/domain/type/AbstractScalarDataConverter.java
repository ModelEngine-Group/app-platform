/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain.type;

import static modelengine.fitframework.util.ObjectUtils.mapIfNotNull;

import modelengine.fit.jane.task.domain.DataConverter;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.ParsingResult;
import modelengine.fitframework.util.support.DefaultParsingResult;

/**
 * 为单值类型的数据转换器提供基类。
 *
 * @author 梁济时
 * @since 2024-01-23
 */
public abstract class AbstractScalarDataConverter implements DataConverter {
    @Override
    public Object fromExternal(Object value) {
        return mapIfNotNull(value, this::fromExternal0);
    }

    @Override
    public Object toExternal(Object value) {
        return mapIfNotNull(value, this::toExternal0);
    }

    @Override
    public Object fromPersistence(Object value) {
        return mapIfNotNull(value, this::fromPersistence0);
    }

    @Override
    public Object toPersistence(Object value) {
        return mapIfNotNull(value, this::toPersistence0);
    }

    @Override
    public ParsingResult<Object> parse(String text) {
        if (text == null) {
            return new DefaultParsingResult<>(true, null);
        } else {
            return this.parse0(text);
        }
    }

    @Override
    public String toString(Object value) {
        return mapIfNotNull(value, this::toString0);
    }

    /**
     * 将外部传入的值转为当前数据类型的值。
     * <p>待转换的值（{@code value}）不会为 {@code null}。</p>
     *
     * @param value 表示外部传入的值的 {@link Object}。
     * @return 表示当前类型的值的 {@link Object}。
     */
    protected abstract Object fromExternal0(@Nonnull Object value);

    /**
     * 将当前数据类型的值转为外部系统展示的值。
     * <p>待转换的值（{@code value}）不会为 {@code null}。</p>
     *
     * @param value 表示当前类型的值的 {@link Object}。
     * @return 表示用以外部系统展示的值的 {@link Object}。
     */
    protected abstract Object toExternal0(@Nonnull Object value);

    /**
     * 将持久化数据转为当前数据类型的值。
     * <p>待转换的值（{@code value}）不会为 {@code null}。</p>
     *
     * @param value 表示持久化的值的 {@link Object}。
     * @return 表示当前类型的值的 {@link Object}。
     */
    protected abstract Object fromPersistence0(@Nonnull Object value);

    /**
     * 将当前类型的数据转为用以持久化的数据。
     * <p>待转换的值（{@code value}）不会为 {@code null}。</p>
     *
     * @param value 表示当前类型的值的 {@link Object}。
     * @return 表示用以持久化的值的 {@link Object}。
     */
    protected abstract Object toPersistence0(@Nonnull Object value);

    /**
     * 从文本中解析当前类型的数据。
     * <p>待转换的值（{@code value}）不会为 {@code null}。</p>
     *
     * @param text 表示包含当前类型数据的文本的 {@link String}。
     * @return 表示解析结果的 {@link ParsingResult}。
     * @throws IllegalArgumentException {@code text} 中不包含当前类型的数据。
     */
    protected abstract ParsingResult<Object> parse0(@Nonnull String text);

    /**
     * 将当前类型的数据转为字符串表现形式。
     * <p>待转换的值（{@code value}）不会为 {@code null}。</p>
     *
     * @param value 表示待转为字符串表现形式的当前类型的数据的 {@link Object}。
     * @return 表示包含当前类型数据的字符串的 {@link String}。
     */
    protected abstract String toString0(@Nonnull Object value);
}
