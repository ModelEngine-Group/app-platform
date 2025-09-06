/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.jade.app.engine.knowledge.dto.TableKnowledgeColDto;
import modelengine.jade.app.engine.knowledge.service.param.PageQueryParam;
import modelengine.jade.app.engine.knowledge.utils.DecodeUtil;

import java.util.List;

/**
 * 表格场景 知识表真实数据表格创建参数
 *
 * @since 2024-05-22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableKnowledgeParam extends PageQueryParam {
    /** 知识库Id */
    private Long repositoryId;

    /** 知识表Id */
    private Long knowledgeTableId;

    /** 表头行 */
    private Integer headerLine = 0;

    /** 数据起始行 */
    private Integer startRow = 1;

    /** 工作表ID */
    private Integer sheetId = 0;

    /** 列 */
    private List<TableKnowledgeColDto> columns;

    /** 文件名 */
    private String fileName;

    /**
     * 获取文件名称
     *
     * @return 解码后的文件名称
     */
    public String getFileName() {
        return DecodeUtil.decodeStr(fileName);
    }
}
