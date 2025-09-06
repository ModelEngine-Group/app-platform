/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 为 {@link Cache} 提供默认实现。
 *
 * @param <K> 表示缓存的键的类型。
 * @param <V> 表示缓存的值的类型。
 */
class DefaultCache<K, V> implements Cache<K, V> {
    private final Map<K, Entry> cache;

    private final Function<K, V> initiator;

    private final long expirations;

    DefaultCache(Function<K, V> initiator, long expirations) {
        this.cache = new ConcurrentHashMap<>();
        this.initiator = initiator;
        this.expirations = expirations;
    }

    @Override
    public V get(K key) {
        return this.cache.computeIfAbsent(key, Entry::new).get();
    }

    private class Entry {
        private final K key;

        private volatile V value;

        private volatile long timestamp;

        private final Object monitor;

        Entry(K key) {
            this.key = key;
            this.value = null;
            this.timestamp = -1;
            this.monitor = new byte[0];
        }

        V get() {
            V actual = this.value;
            if (actual == null || this.expired()) {
                synchronized (this.monitor) {
                    actual = this.value;
                    if (actual == null || this.expired()) {
                        this.value = null;
                        actual = DefaultCache.this.initiator.apply(this.key);
                        this.timestamp = System.currentTimeMillis();
                        this.value = actual;
                    }
                }
            }
            return actual;
        }

        private long expirations() {
            return DefaultCache.this.expirations;
        }

        private boolean expired() {
            return this.expirations() > 0L && System.currentTimeMillis() - this.timestamp > this.expirations();
        }
    }

    static class Builder<K, V> implements Cache.Builder<K, V> {
        private Function<K, V> initiator;

        private long expirations;

        @Override
        public Cache.Builder<K, V> initiator(Function<K, V> initiator) {
            this.initiator = initiator;
            return this;
        }

        @Override
        public Cache.Builder<K, V> expire(long milliseconds) {
            this.expirations = milliseconds;
            return this;
        }

        @Override
        public Cache<K, V> build() {
            return new DefaultCache<>(this.initiator, this.expirations);
        }
    }
}
