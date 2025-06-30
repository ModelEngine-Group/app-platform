/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowDefinitionStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;

import java.util.regex.Pattern;

import static modelengine.fit.waterflow.ErrorCodes.INVALID_FLOW_NODE_SIZE;

/**
 * 流程定义校验规则
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Component
public class DefinitionRule implements FlowRule {
    private static final int EXPECT_NODE_NUMBER = 1;

    private static final int MINIMUM_NODE_SIZE = 3;

    private static final int MAX_NAME_SIZE = 256;

    private static final int META_ID_SIZE = 32;

    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*<?{}|]");

    private static final Pattern STANDARD_VERSION_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+(?:-.*)?$");

    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    @Override
    public void apply(FlowDefinition flowDefinition) {
        validateName(flowDefinition.getName());
        validateMetaId(flowDefinition.getMetaId());
        validateVersion(flowDefinition.getVersion());
        validateStatus(flowDefinition.getStatus());
        validateNodeSet(flowDefinition);
        Validation.notBlank(flowDefinition.getTenant(), exception("flow definition tenant, tenant can not be blank"));
    }

    private int getNodeCount(FlowDefinition flowDefinition, FlowNodeType nodeType) {
        return (int) flowDefinition.getNodeIdSet()
                .stream()
                .map(flowDefinition::getFlowNode)
                .peek(flowNode -> Validation.notNull(flowNode.getType(),
                        exception("flow node type, node type can not be null")))
                .filter(flowNode -> flowNode.belongTo(nodeType))
                .count();
    }

    private void validateName(String name) {
        Validation.notBlank(name, exception("flow definition name, name can not be blank"));
        Validation.isFalse(SPECIAL_CHAR_PATTERN.matcher(name).find(),
                exception("flow definition name, name contains special characters"));
        Validation.lessThan(name.length(), MAX_NAME_SIZE, exception("flow definition name, name length over 256"));
    }

    private void validateMetaId(String metaId) {
        Validation.notBlank(metaId, exception("flow definition metaId, metaId can not be blank"));
        Validation.same(metaId.length(), META_ID_SIZE, exception("flow definition metaId, metaId length must be 32"));
        Validation.isFalse(SPECIAL_CHAR_PATTERN.matcher(metaId).find(),
                exception("flow definition metaId, metaId contains special characters"));
    }

    private void validateVersion(String version) {
        Validation.notBlank(version, exception("flow definition version, version can not be blank"));
        Validation.isTrue(STANDARD_VERSION_PATTERN.matcher(version).matches(),
                exception("flow definition version, version format must be X.Y.Z"));
    }

    private void validateStatus(FlowDefinitionStatus status) {
        Validation.notNull(status, exception("flow definition status, status can not be blank"));
        Validation.isTrue(FlowDefinitionStatus.ACTIVE.equals(status) || FlowDefinitionStatus.INACTIVE.equals(status),
                exception("flow definition status, status must be active or inactive"));
    }

    private void validateNodeSet(FlowDefinition flowDefinition) {
        Validation.notEmpty(flowDefinition.getNodeIdSet(), exception("flow definition nodes, nodes can not be empty"));
        Validation.greaterThanOrEquals(flowDefinition.getNodeIdSet().size(), MINIMUM_NODE_SIZE,
                () -> new WaterflowParamException(INVALID_FLOW_NODE_SIZE));
        Validation.same(getNodeCount(flowDefinition, FlowNodeType.START), EXPECT_NODE_NUMBER,
                exception("start node number"));
        Validation.greaterThanOrEquals(getNodeCount(flowDefinition, FlowNodeType.END), EXPECT_NODE_NUMBER,
                exception("end node number"));
    }
}
