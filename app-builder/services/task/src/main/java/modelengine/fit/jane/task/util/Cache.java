/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.util;

import java.util.function.Function;

/**
 * 为应用程序提供缓存。
 *
 * @param <K> 表示键的类型。
 * @param <V> 表示值的类型。
 * @author 梁济时
 * @since 2023-11-20
 */
public interface Cache<K, V> {
    /**
     * 获取指定键的值。
     *
     * @param key 表示键的 {@link K}。
     * @return 表示值的 {@link V}。
     */
    V get(K key);

    /**
     * 为缓存提供构建器。
     *
     * @param <K> 表示键的类型。
     * @param <V> 表示值的类型。
     * @author 梁济时
     * @since 2023-11-20
     */
    interface Builder<K, V> {
        /**
         * 设置初始化器。
         * <p>初始化器可通过指定键获取到对应的值。</p>
         *
         * @param initiator 表示初始化器的 {@link Function}{@code <}{@link K}{@code , }{@link V}{@code >}。
         * @return 表示当前构建器的 {@link Builder}{@code <}{@link K}{@code , }{@link V}{@code >}。
         */
        Builder<K, V> initiator(Function<K, V> initiator);

        /**
         * 设置缓存有效期的毫秒数。
         *
         * @param milliseconds 表示缓存有效期的毫秒数的 64 位整数。
         * @return 表示当前构建器的 {@link Builder}{@code <}{@link K}{@code , }{@link V}{@code >}。
         */
        Builder<K, V> expire(long milliseconds);

        /**
         * 构建缓存实例。
         *
         * @return 表示新构建的缓存实例的 {@link Cache}{@code <}{@link K}{@code , }{@link V}{@code >}。
         */
        Cache<K, V> build();
    }

    /**
     * 返回一个构建器，用以构建缓存的新实例。
     * <pre>
     *     Cache.&lt;String, String&gt;custom()
     *             .initiator(initiator)
     *             .expire(5 * 60 * 1000)
     *             .build();
     * </pre>
     *
     * @param <K> 表示缓存的键的类型。
     * @param <V> 表示缓存的值的类型。
     * @return 表示用以构建缓存实例的构建器的 {@link Builder}{@code <}{@link K}{@code , }{@link V}{@code >}。
     */
    static <K, V> Builder<K, V> custom() {
        return new DefaultCache.Builder<>();
    }
}
