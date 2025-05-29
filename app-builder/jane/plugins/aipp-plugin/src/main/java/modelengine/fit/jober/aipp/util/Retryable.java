/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import lombok.Setter;
import modelengine.fitframework.util.ObjectUtils;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 重试类.
 *
 * @author 张越
 * @since 2025-02-14
 */
@Setter
public class Retryable<T, E extends Exception> {
    private Supplier<T> supplier;
    private int retryTimes;
    private Class<E> observeException;
    private Predicate<E> breakCondition;
    private BiConsumer<E, Integer> exceptionConsumer;

    public Retryable(Supplier<T> supplier, int retryTimes) {
        this.supplier = supplier;
        this.retryTimes = retryTimes;
    }

    /**
     * 重试方法，返回重试结果.
     * {@code T} 表示实际的返回结果，{@code E} 代表用户关注的异常对象.
     *
     * @return {@link RetryResult} 重试结果
     */
    public RetryResult<T, E> retry() {
        int left = this.retryTimes;
        E lastException;
        do {
            try {
                return new RetryResult<>(this.supplier.get(), null);
            } catch (Exception e) {
                if (this.observeException == null || !this.observeException.isInstance(e)) {
                    throw e;
                }
                lastException = ObjectUtils.cast(e);
                if (this.breakCondition != null && this.breakCondition.test(ObjectUtils.cast(e))) {
                    break;
                }
                if (this.exceptionConsumer != null) {
                    this.exceptionConsumer.accept(ObjectUtils.cast(e), this.retryTimes - left);
                }
            }
        } while (left-- > 0);
        return new RetryResult<>(null, lastException);
    }

    /**
     * 重试结果类.
     *
     * @param <T> 表示实际的返回结果
     * @param <E> 代表用户关注的异常对象
     */
    public static class RetryResult<T, E extends Exception> {
        private final T t;

        private final E exception;

        public RetryResult(T t, E e) {
            this.t = t;
            this.exception = e;
        }

        /**
         * 当 T 为 null 时抛出异常.
         *
         * @param exceptionSupplier 异常提供器.
         * @return 实际结果.
         * @throws X 异常对象.
         */
        public <X extends Throwable> T orElseThrow(Function<E, ? extends X> exceptionSupplier) throws X {
            if (this.t != null) {
                return this.t;
            }
            throw exceptionSupplier.apply(this.exception);
        }
    }
}
