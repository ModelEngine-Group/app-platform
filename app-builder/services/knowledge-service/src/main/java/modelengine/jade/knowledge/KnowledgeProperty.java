/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.support.FlatFilterConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fitframework.inspection.Validation;

import java.util.Collections;
import java.util.List;

/**
 * 检索参数配置信息。
 *
 * @author 刘信宏
 * @since 2024-09-18
 */
@Getter
@Setter
@NoArgsConstructor
public class KnowledgeProperty {
    private List<IndexInfo> indexType;
    private List<FlatFilterConfig> filterConfig;
    private List<RerankConfig> rerankConfig;

    public KnowledgeProperty(List<IndexInfo> indexType, List<FlatFilterConfig> filterConfig,
            List<RerankConfig> rerankConfig) {
        this.indexType = Validation.notNull(indexType, "The index type cannot be null.");
        this.filterConfig = Validation.notNull(filterConfig, "The filter config cannot be null.");
        this.rerankConfig = Validation.notNull(rerankConfig, "The rerank config cannot be null.");
    }

    /**
     * 获取支持的检索方式配置。
     *
     * @return 表示检索方式列表的 {@link List}{@code <}{@link IndexInfo}{@code >}。
     */
    public List<IndexInfo> indexType() {
        return Collections.unmodifiableList(this.indexType);
    }

    /**
     * 获取支持检索应用上限的配置列表。
     *
     * @return 表示检索应用上限配置信息的 {@link List}{@code <}{@link FlatFilterConfig}{@code >}。
     */
    public List<FlatFilterConfig> filterConfig() {
        return Collections.unmodifiableList(this.filterConfig);
    }

    /**
     * 获取结果重排的配置列表。
     *
     * @return 表示结果重排配置信息的 {@link List}{@code <}{@link RerankConfig}{@code >}。
     */
    public List<RerankConfig> rerankConfig() {
        return Collections.unmodifiableList(this.rerankConfig);
    }

    /**
     * 检索方式属性信息。
     */
    @NoArgsConstructor
    public static class IndexInfo extends SchemaItem {
        /**
         * 初始化 {@link IndexInfo} 对象。
         *
         * @param type 表示检索方式的 {@link IndexType}。
         * @param name 表示检索方式名字的 {@link String}。
         * @param description 表示检索方式描述的 {@link String}。
         */
        public IndexInfo(IndexType type, String name, String description) {
            super(Validation.notNull(type, "The index type cannot be null.").value(), name, description);
        }
    }

    /**
     * 结果重排属性信息。
     */
    @NoArgsConstructor
    public static class RerankConfig extends SchemaItem {
        private Object defaultValue;

        /**
         * 初始化 {@link RerankConfig} 对象。
         *
         * @param type 表示结果重排配置值类型的 {@link String}。
         * @param name 表示结果重排名字的 {@link String}。
         * @param description 表示结果重排描述的 {@link String}。
         * @param defaultValue 表示结果重排默认值的 {@link Object}。
         */
        public RerankConfig(String type, String name, String description, Object defaultValue) {
            super(type, name, description);
            this.defaultValue = defaultValue;
        }

        /**
         * 获取结果重排默认值。
         *
         * @return 表示结果重排默认值的 {@code boolean}。
         */
        public Object defaultValue() {
            return this.defaultValue;
        }
    }
}