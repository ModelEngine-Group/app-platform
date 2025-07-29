/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 百度千帆 检索pipeline 参数。
 *
 * @author 陈潇文
 * @since 2025-04-25
 */
@Data
@Builder
public class QianfanPipelineQueryParam {
    /**
     * 该节点的自定义名称。如：step1。
     */
    private String name;
    /**
     * 该节点的类型。可选值：elastic_search：数据源来自BES ; vector_db：数据源来自VDB
     */
    private String type;
    /**
     * 得分阈值。取值范围： [0, 1]。
     */
    private float threshold;
    /**
     * 召回数量。取值范围： [0, 800]。默认400 。
     */
    private int top;
    /**
     * 输入的节点名。
     */
    private List<String> inputs;
}
