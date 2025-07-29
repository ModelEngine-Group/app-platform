/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.StringUtils;

/**
 * 表示统一资源名称。
 *
 * @author 梁济时
 * @since 2023-08-09
 */
public interface Urn {
    /**
     * 获取父资源的 URN。
     *
     * @return 表示父资源的 URN 的 {@link Urn}。
     */
    Urn parent();

    /**
     * 获取资源的类型。
     *
     * @return 表示资源类型的 {@link String}。
     */
    String type();

    /**
     * 获取资源的唯一标识。
     *
     * @return 表示资源唯一标识的 {@link String}。
     */
    String id();

    /**
     * 创建一个 URN。
     *
     * @param type 表示对象的类型的 {@link String}。忽略前后的空白字符。
     * @param id 表示对象的唯一标识的 {@link String}。忽略前后的空白字符。
     * @return 表示新创建的 URN 的 {@link Urn}。
     * @throws IllegalArgumentException {@code type} 或 {@code id} 为空白字符串。
     */
    static Urn create(String type, String id) {
        return new DefaultUrn(null, type, id);
    }

    /**
     * 创建一个 URN。
     *
     * @param parent 表示父 URN 的 {@link Urn}。
     * @param type 表示对象的类型的 {@link String}。忽略前后的空白字符。
     * @param id 表示对象的唯一标识的 {@link String}。忽略前后的空白字符。
     * @return 表示新创建的 URN 的 {@link Urn}。
     * @throws IllegalArgumentException {@code type} 或 {@code id} 为空白字符串。
     */
    static Urn create(Urn parent, String type, String id) {
        return new DefaultUrn(parent, type, id);
    }

    /**
     * 从字符串中解析 URN 信息。
     *
     * @param value 表示包含 URN 信息的字符串的 {@link String}。
     * @return 表示从字符串中解析到的 URN 信息的 {@link Urn}。
     * @throws IllegalArgumentException {@code value} 为 {@code null} 或不是有效的 URN 格式。
     */
    static Urn parse(String value) {
        notNull(value, "The string value to parse URN cannot be null.");
        if (!StringUtils.startsWithIgnoreCase(value, "urn:")) {
            throw new IllegalArgumentException(
                    StringUtils.format("A URN must starts with 'urn:' prefix. [value={0}]", value));
        }
        String[] parts = StringUtils.split(value, ':');
        if ((parts.length % 2) < 1) {
            throw new IllegalArgumentException(
                    StringUtils.format("A URN must be divided into odd parts by a ':'. [value={0}]", value));
        }
        int index = 1;
        Urn urn = null;
        while (index < parts.length) {
            String type = parts[index++];
            String id = parts[index++];
            urn = create(urn, type, id);
        }
        return urn;
    }
}

