/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fitframework.inspection.Validation;

import java.time.LocalDateTime;

/**
 * 知识库元数据。
 *
 * @author 刘信宏
 * @since 2024-09-09
 */
@Getter
@Setter
@NoArgsConstructor
public class KnowledgeRepo {
    private String id;
    private String name;
    private String description;
    private String type;
    private LocalDateTime createdAt;

    /**
     * 初始化 {@link KnowledgeRepo} 对象。
     *
     * @param id 表示知识库标识的 {@link String}。
     * @param name 表示知识库名称的 {@link String}。
     * @param description 表示知识库描述的 {@link String}。
     * @param type 表示知识库类型的 {@link String}。
     * @param createdAt 表示知识库创建时间的 {@link LocalDateTime}。
     */
    public KnowledgeRepo(String id, String name, String description, String type, LocalDateTime createdAt) {
        this.id = Validation.notBlank(id, "The repository id cannot be null.");
        this.name = name;
        this.description = description;
        this.type = type;
        this.createdAt = createdAt;
    }

    /**
     * 获取知识库标识。
     *
     * @return 表示知识库标识的 {@link String}。
     */
    public String id() {
        return id;
    }

    /**
     * 获取知识库名称。
     *
     * @return 表示知识库名称的 {@link String}。
     */
    public String name() {
        return name;
    }

    /**
     * 获取知识库描述。
     *
     * @return 表示知识库描述的 {@link String}。
     */
    public String description() {
        return description;
    }

    /**
     * 获取知识库类型。
     *
     * @return 表示知识库类型的 {@link String}。
     */
    public String type() {
        return type;
    }

    /**
     * 获取知识库创建时间。
     *
     * @return 表示知识库创建时间的 {@link LocalDateTime}。
     */
    public LocalDateTime createdAt() {
        return createdAt;
    }
}