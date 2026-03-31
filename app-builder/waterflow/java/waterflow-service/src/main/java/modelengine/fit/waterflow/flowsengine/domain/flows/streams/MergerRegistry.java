/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Merger 注册中心，用于管理数据类型到 Merger 的映射关系
 * 支持插件式扩展，无需修改核心代码即可添加新的 Merger 实现
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public class MergerRegistry {
    private static final MergerRegistry INSTANCE = new MergerRegistry();

    private final Map<Class<?>, Supplier<Processors.Merger<?>>> registry = new ConcurrentHashMap<>();

    private MergerRegistry() {
        registerDefaults();
    }

    public static MergerRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * 注册指定类型对应的 Merger 工厂
     *
     * @param type 数据类型
     * @param mergerFactory Merger 工厂方法
     * @param <T> 输入数据类型
     * @param <M> Merger 实现类型
     */
    @SuppressWarnings("unchecked")
    public <T, M extends Processors.Merger<T>> void register(Class<T> type, Supplier<M> mergerFactory) {
        registry.put(type, (Supplier<Processors.Merger<?>>) mergerFactory);
    }

    /**
     * 根据类型获取对应的 Merger
     *
     * @param type 数据类型
     * @param <T> 泛型类型
     * @return Merger 实例，如果未注册则返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> Processors.Merger<T> getMerger(Class<?> type) {
        Supplier<Processors.Merger<?>> factory = registry.get(type);
        if (factory != null) {
            return (Processors.Merger<T>) factory.get();
        }
        // 尝试查找父类型或接口
        for (Map.Entry<Class<?>, Supplier<Processors.Merger<?>>> entry : registry.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return (Processors.Merger<T>) entry.getValue().get();
            }
        }
        return null;
    }

    private void registerDefaults() {
        register(modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData.class,
                FlowDataMerger::new);
    }
}
