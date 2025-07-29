/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

/**
 * 节点类型枚举
 *
 * @author 孙怡菲
 * @since 2024-11-26
 */
public enum NodeType {
    LLM_NODE("llmNodeState"),
    TOOL_INVOKE_NODE("toolInvokeNodeState"),
    MANUAL_CHECK_NODE("manualCheckNodeState"),
    RETRIEVAL_NODE("knowledgeRetrievalNodeState"),
    OLD_RETRIEVAL_NODE("retrievalNodeState"),
    END_NODE("endNodeEnd");

    private final String type;

    NodeType(String type) {
        this.type = type;
    }

    /**
     * 节点类型名称。
     *
     * @return 节点类型名称。
     */
    public String type() {
        return this.type;
    }
}
