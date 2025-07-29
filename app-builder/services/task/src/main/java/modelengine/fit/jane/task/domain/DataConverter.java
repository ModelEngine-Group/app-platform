/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fitframework.util.ParsingResult;

/**
 * 表示数据类型。
 *
 * @author 梁济时
 * @since 2024-01-23
 */
public interface DataConverter {
    /**
     * 将外部传入的值转为当前数据类型的值。
     *
     * @param value 表示外部传入的值的 {@link Object}。
     * @return 表示当前类型的值的 {@link Object}。
     */
    Object fromExternal(Object value);

    /**
     * 将当前数据类型的值转为外部系统展示的值。
     *
     * @param value 表示当前类型的值的 {@link Object}。
     * @return 表示用以外部系统展示的值的 {@link Object}。
     */
    Object toExternal(Object value);

    /**
     * 将持久化数据转为当前数据类型的值。
     *
     * @param value 表示持久化的值的 {@link Object}。
     * @return 表示当前类型的值的 {@link Object}。
     */
    Object fromPersistence(Object value);

    /**
     * 将当前类型的数据转为用以持久化的数据。
     *
     * @param value 表示当前类型的值的 {@link Object}。
     * @return 表示用以持久化的值的 {@link Object}。
     */
    Object toPersistence(Object value);

    /**
     * 从文本中解析当前类型的数据。
     *
     * @param text 表示包含当前类型数据的文本的 {@link String}。
     * @return 表示解析结果的 {@link ParsingResult}。
     * @throws IllegalArgumentException {@code text} 中不包含当前类型的数据。
     */
    ParsingResult<Object> parse(String text);

    /**
     * 将当前类型的数据转为字符串表现形式。
     *
     * @param value 表示待转为字符串表现形式的当前类型的数据的 {@link Object}。
     * @return 表示包含当前类型数据的字符串的 {@link String}。
     */
    String toString(Object value);
}
