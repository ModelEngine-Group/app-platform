/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fel.core.document.Document;
import modelengine.fel.core.document.Measurable;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.resource.web.Media;
import modelengine.fitframework.serialization.annotation.SerializeStrategy;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 知识库文档。
 *
 * @author 刘信宏
 * @since 2024-09-23
 */
@SerializeStrategy(include = SerializeStrategy.Include.NON_EMPTY)
@Getter
@Setter
@NoArgsConstructor
public class KnowledgeDocument implements Document, Measurable {
    private String id;

    private String text;

    private double score;

    private Map<String, Object> metadata;

    /**
     * 初始化 {@link KnowledgeDocument} 对象。
     *
     * @param id 表示文档标识的 {@link String}。
     * @param text 表示文档内容的 {@link String}。
     * @param score 表示量化分数的 {@code double}。
     * @param metadata 表示文档元数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public KnowledgeDocument(String id, String text, double score, Map<String, Object> metadata) {
        this.id = id;
        this.text = text;
        this.score = score;
        this.metadata = ObjectUtils.getIfNull(metadata, Collections::emptyMap);
    }

    /**
     * 使用 {@link Document} 初始化 {@link KnowledgeDocument} 对象。
     *
     * @param document 表示原始文档的 {@link Document}。
     * @param score 表示文档评分的 {@code double}。
     */
    public KnowledgeDocument(Document document, double score) {
        this.id = document.id();
        this.text = document.text();
        this.score = score;
        this.metadata = ObjectUtils.getIfNull(document.metadata(), Collections::emptyMap);
    }

    @Override
    public String text() {
        return this.text;
    }

    @Override
    public List<Media> medias() {
        return Collections.emptyList();
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public Map<String, Object> metadata() {
        return Optional.ofNullable(this.metadata).map(Collections::unmodifiableMap).orElse(Collections.emptyMap());
    }

    @Override
    public double score() {
        return this.score;
    }

    @Override
    @Nonnull
    public String group() {
        return StringUtils.EMPTY;
    }
}
