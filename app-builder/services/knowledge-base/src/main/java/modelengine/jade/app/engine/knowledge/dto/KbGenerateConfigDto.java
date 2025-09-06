/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.dto;

import lombok.Getter;
import lombok.Setter;
import modelengine.jade.app.engine.knowledge.dto.enums.SplitType;

import java.util.List;

/**
 * KbGenerateConfigDto 导入文件的配置信息参数
 *
 * @author YangPeng
 * @since 2024-05-17 16:16
 */
@Getter
@Setter
public class KbGenerateConfigDto {
    /**
     * 知识库id
     */
    private Long knowledgeId;

    /**
     * 知识表id
     */
    private Long tableId;

    /**
     * 导入文件名列表
     */
    private List<String> fileNames;

    /**
     * 切片模式
     */
    private SplitType splitType;

    /**
     * 切片大小
     */
    private Integer chunkSize;

    /**
     * 切片重叠长度
     */
    private Integer chunkOverlap;

    /**
     * 文本清洗算子列表
     */
    private List<String> operatorIds;

    /**
     * embedding服务
     */
    private String embeddingUrl;

    /**
     * 向量数据库服务
     */
    private String vectorUrl;
}
