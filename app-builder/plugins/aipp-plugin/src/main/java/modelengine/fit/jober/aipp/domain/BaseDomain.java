/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 基础领域对象，所有领域对象都应该继承此类
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseDomain {
    private String createBy;
    private String updateBy;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean isDeleted;

    /**
     * 懒加载，获取一个对象
     *
     * @param target 待获取的目标
     * @param supplier 如果目标为null, 则执行supplier获取
     * @param consumer 获取后回调该方法, 用于设置属性
     * @param <X> 延迟加载的类型
     * @return 延迟加载的结果
     */
    protected static <X> X lazyGet(X target, Supplier<X> supplier, Consumer<X> consumer) {
        if (target != null) {
            return target;
        }
        X newTarget = supplier.get();
        consumer.accept(newTarget);
        return newTarget;
    }
}
