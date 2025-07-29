/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.business;

import modelengine.fit.jober.aipp.constants.AippConst;

import lombok.Getter;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 历史配置类.
 *
 * @author 张越
 * @since 2025-01-09
 */
@Getter
public class MemoryConfig {
    private static final String TYPE_KEY = "type";
    private static final String NAME_KEY = "name";
    private static final String VALUE_KEY = "value";

    private List<Map<String, Object>> memoryConfigs;
    private String memoryType;
    private Boolean enableMemory;
    private Object value;

    /**
     * MemoryConfig 的构造方法
     *
     * @param memoryConfigs 用于初始化历史配置类的配置集合的 {@link List}{@code <}{@link Map}{@code <}{@link String}{@code ,
     * }{@link Object}{@code >>}。
     */
    public MemoryConfig(List<Map<String, Object>> memoryConfigs) {
        if (CollectionUtils.isEmpty(memoryConfigs)) {
            return;
        }
        this.memoryConfigs = memoryConfigs;
        this.memoryConfigs.forEach(this::setMemoryConfig);
    }

    private void setMemoryConfig(Map<String, Object> config) {
        if (this.isValue(config)) {
            this.value = config.get(VALUE_KEY);
            return;
        }
        if (this.isMemorySwitch(config)) {
            this.enableMemory = ObjectUtils.cast(config.get(VALUE_KEY));
            return;
        }
        if (this.isType(config)) {
            this.memoryType = ObjectUtils.cast(config.get(VALUE_KEY));
        }
    }

    /**
     * 是否启用memory.
     *
     * @return true/false.
     */
    public boolean getEnableMemory() {
        return Optional.ofNullable(this.enableMemory).orElse(false);
    }

    private boolean isValue(Map<String, Object> config) {
        return StringUtils.equals(ObjectUtils.cast(config.get(NAME_KEY)), VALUE_KEY);
    }

    private boolean isMemorySwitch(Map<String, Object> config) {
        return StringUtils.equals(ObjectUtils.cast(config.get(NAME_KEY)), AippConst.MEMORY_SWITCH_KEY);
    }

    private boolean isType(Map<String, Object> config) {
        return StringUtils.equals(ObjectUtils.cast(config.get(NAME_KEY)), TYPE_KEY);
    }

    /**
     * 配置是否为空.
     *
     * @return true/false.
     */
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(this.memoryConfigs);
    }

    /**
     * 获取memory类型.
     *
     * @return {@link String} memory类型.
     */
    public String getMemoryType() {
        return Optional.ofNullable(this.memoryType).orElse(StringUtils.EMPTY);
    }
}
