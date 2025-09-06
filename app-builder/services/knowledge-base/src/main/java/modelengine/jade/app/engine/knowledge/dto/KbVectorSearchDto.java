/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * KbVectorSearchDto 文本型知识表多源检索参数
 *
 * @author WangZifan
 * @since 2024-05-29
 */
@Getter
@Setter
public class KbVectorSearchDto {
    /**
     * 知识表id
     */
    private List<Long> tableId;

    /**
     * 查询关键字
     */
    private String content;

    /**
     * topK
     */
    private Integer topK;

    /**
     * 阈值
     */
    private Float threshold;
}
