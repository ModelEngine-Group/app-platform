/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import lombok.Data;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * qianfan 知识库检索结果。
 *
 * @author 陈潇文
 * @since 2025-04-25
 */
@Data
public class QianfanRetrievalResult {
    /**
     * chunk数量。
     */
    @Property(description = "total_count", name = "total_count")
    private Integer totalCount;
    /**
     * 切片信息。
     */
    private List<QianfanRetrievalChunksEntity> chunks;
}
