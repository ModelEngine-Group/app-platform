/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_ENGINE_PARSER_NOT_SUPPORT;

import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;


/**
 * 平行节点解析类
 *
 * @author 杨祥宇
 * @since 2023/8/16
 */
public class ParallelNodeParser implements NodeParser {
    @Override
    public FlowNode parseNode(FlowGraphData flowGraphData, int index) {
        throw new WaterflowException(FLOW_ENGINE_PARSER_NOT_SUPPORT, "ParallelNodeParser::parseNode");
    }
}
