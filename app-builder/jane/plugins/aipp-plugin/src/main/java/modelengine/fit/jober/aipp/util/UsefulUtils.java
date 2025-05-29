/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fitframework.util.StringUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 有用的工具.
 *
 * @author 张越
 * @since 2025.01.06
 */
public class UsefulUtils {
    /**
     * 当不为null的时候处理.
     *
     * @param v 待判断数据.
     * @param consumer 消费者.
     * @param <T> 数据类型.
     */
    public static <T> void doIfNotNull(T v, Consumer<T> consumer) {
        if (v != null) {
            consumer.accept(v);
        }
    }

    /**
     * 当字符串不为空的时候处理.
     *
     * @param v 待判断数据.
     * @param consumer 消费者.
     */
    public static void doIfNotBlank(String v, Consumer<String> consumer) {
        if (StringUtils.isNotBlank(v)) {
            consumer.accept(v);
        }
    }

    /**
     * 当为null的时候处理.
     *
     * @param v 待判断数据.
     * @param runnable 执行器.
     * @param <T> 数据类型.
     */
    public static <T> void doIfNull(T v, Runnable runnable) {
        if (v == null) {
            runnable.run();
        }
    }

    /**
     * 当字符串为空的时候处理.
     *
     * @param v 待判断数据.
     * @param runner 执行函数.
     */
    public static void doIfBlank(String v, Runnable runner) {
        if (StringUtils.isBlank(v)) {
            runner.run();
        }
    }

    /**
     * 懒加载，获取一个对象
     *
     * @param target 待获取的目标
     * @param supplier 如果目标为null, 则执行supplier获取
     * @param consumer 获取后回调该方法, 用于设置属性
     * @param <X> 延迟加载的类型
     * @return 延迟加载的结果
     */
    public static <X> X lazyGet(X target, Supplier<X> supplier, Consumer<X> consumer) {
        if (target != null) {
            return target;
        }
        X newTarget = supplier.get();
        consumer.accept(newTarget);
        return newTarget;
    }
}
