/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.jade.app.engine.knowledge.dto.enums.IndexType;

import java.util.Objects;

/**
 * 表格场景 知识表列信息
 *
 * @since 2024-05-22
 */
@Getter
@Setter
@NoArgsConstructor
public class TableKnowledgeColDto {
    /** 列名 */
    private String name;

    /** 数据类型 */
    private String dataType;

    /** 索引类型 */
    private IndexType indexType;

    /** 向量化服务Id */
    private Long embedServiceId;

    /** 描述 */
    private String desc;

    /** 是否隐藏 */
    private boolean hidden;

    /**
     * 构造方法
     *
     * @param name 列名
     */
    public TableKnowledgeColDto(String name) {
        this.name = name;
        if (Objects.equals(name, "inner_id")) {
            this.hidden = true;
        }
    }

    /**
     * 构造方法
     *
     * @param name 列名
     * @param indexType 索引类型
     */
    public TableKnowledgeColDto(String name, IndexType indexType) {
        this(name);
        this.indexType = indexType;
    }
}
