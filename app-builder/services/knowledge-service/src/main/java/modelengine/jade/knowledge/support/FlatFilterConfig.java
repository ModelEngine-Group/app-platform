/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.support;

import modelengine.jade.knowledge.FilterConfig;
import modelengine.jade.knowledge.enums.FilterType;

import lombok.NoArgsConstructor;
import modelengine.fitframework.inspection.Validation;

/**
 * 知识检索的过滤参数信息默认实现。
 *
 * @author 刘信宏
 * @since 2024-09-23
 */
@NoArgsConstructor
public class FlatFilterConfig implements FilterConfig {
    private String name;
    private String type;
    private String description;
    private Number defaultValue;
    private Number minimum;
    private Number maximum;

    /**
     * 使用 {@link FilterConfig} 初始化 {@link FlatFilterConfig} 对象。
     *
     * @param config 表示知识检索的过滤参数信息的 {@link FilterConfig}。
     */
    public FlatFilterConfig(FilterConfig config) {
        this.name = config.name();
        this.type = config.type();
        this.description = config.description();
        this.defaultValue = config.defaultValue();
        this.minimum = config.minimum();
        this.maximum = config.maximum();
    }

    FlatFilterConfig(FlatFilterConfig.Builder builder) {
        this.name = builder.name;
        this.type = builder.type.value();
        this.description = builder.description;
        this.defaultValue = builder.defaultValue;
        this.minimum = builder.minimum;
        this.maximum = builder.maximum;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String type() {
        return this.type;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public Number defaultValue() {
        return this.defaultValue;
    }

    @Override
    public Number minimum() {
        return this.minimum;
    }

    @Override
    public Number maximum() {
        return this.maximum;
    }

    /**
     * {@link FlatFilterConfig} 的构建器。
     */
    public static class Builder implements FilterConfig.Builder {
        private String name;
        private FilterType type;
        private String description;
        private Number defaultValue;
        private Number minimum;
        private Number maximum;

        @Override
        public FilterConfig.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public FilterConfig.Builder type(FilterType type) {
            this.type = type;
            return this;
        }

        @Override
        public FilterConfig.Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public FilterConfig.Builder defaultValue(Number defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Override
        public FilterConfig.Builder minimum(Number minimum) {
            this.minimum = minimum;
            return this;
        }

        @Override
        public FilterConfig.Builder maximum(Number maximum) {
            this.maximum = maximum;
            return this;
        }

        @Override
        public FilterConfig build() {
            Validation.notNull(this.name, "The name cannot be null.");
            Validation.notNull(this.minimum, "The minimum cannot be null.");
            Validation.notNull(this.maximum, "The maximum cannot be null.");
            Validation.notNull(this.type, "The type cannot be null.");
            return new FlatFilterConfig(this);
        }
    }
}
