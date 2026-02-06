/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 表示知识库列表分页数据对象。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Data
@Builder
public class PageVoKnowledgeList {
    /**
     * 知识库列表查询数据。
     */
    private List<DataMateKnowledgeEntity> knowledgeEntityList;

    /**
     * 知识库总数。
     */
    private int total;
}

