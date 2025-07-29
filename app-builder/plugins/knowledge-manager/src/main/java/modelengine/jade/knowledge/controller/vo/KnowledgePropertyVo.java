/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.controller.vo;

import modelengine.jade.knowledge.KnowledgeProperty;
import modelengine.jade.knowledge.support.FlatFilterConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 检索参数配置信息展示对象。
 *
 * @author 刘信宏
 * @since 2024-11-28
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KnowledgePropertyVo {
    private List<KnowledgeProperty.IndexInfo> disableIndexType;
    private List<KnowledgeProperty.IndexInfo> enableIndexType;
    private List<FlatFilterConfig> filterConfig;
    private List<KnowledgeProperty.RerankConfig> rerankConfig;
}
