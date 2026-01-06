/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.knowledge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.util.List;

/**
 * DataMate 知识库检索结果。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataMateRetrievalResult {
    /**
     * 检索结果集合。
     */
    private List<DataMateRetrievalChunksEntity> data;
}

