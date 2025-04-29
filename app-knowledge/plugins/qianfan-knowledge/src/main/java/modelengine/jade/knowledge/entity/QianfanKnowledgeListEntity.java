/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * qianfan知识库列表Entity。
 *
 * @author 陈潇文
 * @since 2025-04-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QianfanKnowledgeListEntity {
    /**
     * 知识库查询的起始id。
     */
    private String marker;
    /**
     * 知识库列表查询数据。
     */
    private List<QianfanKnowledgeEntity> data;
    /**
     * 最大查询数量。
     */
    private int maxKeys;
    /**
     * 请求id。
     */
    private String requestId;
    /**
     * 下次查询的起始位置。
     */
    private String nextMarker;
    /**
     * true表示后面还有数据，false表示已经是最后一页。
     */
    @JsonProperty("isTruncated")
    private boolean isTruncated;
    /**
     * 知识库总数。
     */
    private int total;
}
