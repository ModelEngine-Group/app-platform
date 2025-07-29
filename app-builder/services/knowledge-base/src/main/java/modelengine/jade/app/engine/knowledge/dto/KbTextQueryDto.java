/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * KbTextQueryDto 文本类型知识表查询参数
 *
 * @author YangPeng
 * @since 2024-05-23
 */
@Setter
@Getter
public class KbTextQueryDto {
    /**
     * 知识库id
     */
    private Long knowledgeId;

    /**
     * 知识表id
     */
    private Long tableId;

    /**
     * 页数
     */
    private Integer pageNo;

    /**
     * 页面大小
     */
    private Integer pageSize;
}
