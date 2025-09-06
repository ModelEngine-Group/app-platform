/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.business;

import static modelengine.fit.jober.aipp.util.UsefulUtils.doIfNotNull;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * 历史获取类.
 *
 * @author 张越
 * @since 2025-01-09
 */
@Getter
public class MemoryGetter {
    private static final String DEFAULT_VALUE = "5";

    private final MemoryConfig memoryConfig;
    private final Map<MemoryTypeEnum, Function<Object, List<Map<String, Object>>>> memoryGenerators;
    private final MemoryTypeEnum defaultType;

    public MemoryGetter(MemoryConfig memoryConfig) {
        this.memoryConfig = memoryConfig;
        this.memoryGenerators = new HashMap<>();
        this.defaultType = MemoryTypeEnum.BY_CONVERSATION_TURN;
    }

    /**
     * 注册不同类型下的数据获取器.
     *
     * @param memoryTypeEnum memory类型.
     * @param generator 数据生成器.
     */
    public void register(MemoryTypeEnum memoryTypeEnum, Function<Object, List<Map<String, Object>>> generator) {
        doIfNotNull(memoryTypeEnum, t -> doIfNotNull(generator, g -> this.memoryGenerators.put(t, g)));
    }

    /**
     * 获取memory数据.
     *
     * @return {@link List}{@code <}{@link String}{@code ,}{@link Object}{@code >}{@code >} 数据对象.
     */
    public List<Map<String, Object>> get() {
        Optional<MemoryTypeEnum> typeOp = MemoryTypeEnum.getType(this.memoryConfig.getMemoryType());
        if (this.memoryConfig.isEmpty() || typeOp.isEmpty()) {
            return this.memoryGenerators.get(this.defaultType).apply(DEFAULT_VALUE);
        }
        MemoryTypeEnum type = typeOp.get();
        return this.memoryGenerators.get(type).apply(this.memoryConfig.getValue());
    }
}
