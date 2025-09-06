/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.jade.app.engine.knowledge.dto.TableKnowledgeColDto;

import java.util.List;

/**
 * 表格型知识检索结果
 *
 * @since 2024/5/24
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TableKnowledgeResult {
    /** 总数 */
    long count;

    /** 列属性 */
    private List<TableKnowledgeColDto> columns;

    /** 知识内容 */
    private List<List<Object>> list;
}
