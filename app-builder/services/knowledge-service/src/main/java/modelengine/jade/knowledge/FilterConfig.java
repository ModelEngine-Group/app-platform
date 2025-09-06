/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import modelengine.jade.knowledge.enums.FilterType;
import modelengine.jade.knowledge.support.FlatFilterConfig;

import modelengine.fitframework.inspection.Nonnull;

/**
 * 知识检索的过滤参数信息。
 *
 * @author 刘信宏
 * @since 2024-09-23
 */
public interface FilterConfig {
    /**
     * 获取过滤参数的名称。
     *
     * @return 表示过滤参数名称的 {@link String}。
     */
    @Nonnull
    String name();

    /**
     * 获取过滤参数类型。
     *
     * @return 表示过滤参数类型的 {@link String}。
     */
    @Nonnull
    String type();

    /**
     * 获取过滤参数的描述。
     *
     * @return 表示过滤参数描述的 {@link String}。
     */
    String description();

    /**
     * 获取过滤参数的默认值。
     *
     * @return 表示过滤参数默认值的 {@link Number}。
     */
    Number defaultValue();

    /**
     * 获取过滤参数的最小值。
     *
     * @return 表示过滤参数最小值的 {@link Number}。
     */
    @Nonnull
    Number minimum();

    /**
     * 获取过滤参数的最大值。
     *
     * @return 表示过滤参数最大值的 {@link Number}。
     */
    @Nonnull
    Number maximum();

    /**
     * {@link FilterConfig} 的构建器。
     */
    interface Builder {
        /**
         * 设置过滤参数名称。
         *
         * @param name 表示过滤参数名称的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 设置过滤参数类型。
         *
         * @param name 表示过滤参数类型的 {@link FilterType}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder type(FilterType name);

        /**
         * 设置过滤参数描述。
         *
         * @param description 表示过滤参数描述的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder description(String description);

        /**
         * 设置过滤参数默认值。
         *
         * @param defaultValue 表示过滤参数默认值的 {@link Number}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder defaultValue(Number defaultValue);

        /**
         * 设置过滤参数的最小值。
         *
         * @param minimum 表示过滤参数的最小值的 {@link Number}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder minimum(Number minimum);

        /**
         * 设置过滤参数的最大值。
         *
         * @param maximum 表示过滤参数的最大值的 {@link Number}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder maximum(Number maximum);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link FilterConfig}。
         */
        FilterConfig build();
    }

    /**
     * 返回一个构建器，用以构建过滤参数的新实例。
     *
     * @return 表示用以构建过滤参数的构建器的 {@link FilterConfig.Builder}。
     */
    static FilterConfig.Builder custom() {
        return new FlatFilterConfig.Builder();
    }
}
