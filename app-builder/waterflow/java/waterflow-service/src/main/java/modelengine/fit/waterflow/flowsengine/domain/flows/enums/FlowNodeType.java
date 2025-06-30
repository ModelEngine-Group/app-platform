/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.enums;

import static java.util.Locale.ROOT;
import static modelengine.fit.waterflow.ErrorCodes.ENUM_CONVERT_FAILED;

import lombok.Getter;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.ConditionNodeParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.EndNodeParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.NodeParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.ParallelNodeParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.StartNodeParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.StateNodeParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes.ConditionNodeRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes.EndNodeRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes.ForkNodeRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes.NodeRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes.ParallelNodeRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes.StartNodeRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes.StateNodeRule;

import java.util.Arrays;

/**
 * 流程定义节点类型
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
public enum FlowNodeType {
    START("START", false, new StartNodeParser(), new StartNodeRule()),
    STATE("STATE", false, new StateNodeParser(), new StateNodeRule()),
    CONDITION("CONDITION", false, new ConditionNodeParser(), new ConditionNodeRule()),
    PARALLEL("PARALLEL", false, new ParallelNodeParser(), new ParallelNodeRule()),
    FORK("FORK", true, null, new ForkNodeRule()),
    JOIN("JOIN", true, null, null),
    EVENT("EVENT", true, null, null),
    END("END", false, new EndNodeParser(), new EndNodeRule());

    private final String code;

    private final boolean subNode;

    private final NodeParser nodeParser;

    private final NodeRule nodeRule;

    FlowNodeType(String code, boolean subNode, NodeParser nodeParser, NodeRule nodeRule) {
        this.code = code;
        this.subNode = subNode;
        this.nodeParser = nodeParser;
        this.nodeRule = nodeRule;
    }

    /**
     * getNodeType
     *
     * @param code code
     * @return FlowNodeType
     */
    public static FlowNodeType getNodeType(String code) {
        return Arrays.stream(values())
                .filter(value -> code.toUpperCase(ROOT).endsWith(value.getCode()))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowNodeType", code));
    }
}
