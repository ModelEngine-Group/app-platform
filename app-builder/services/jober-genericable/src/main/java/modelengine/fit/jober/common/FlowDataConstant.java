/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common;

/**
 * 流程数据中的一些常量
 *
 * @author songyongtan
 * @since 2024/12/9
 */
public class FlowDataConstant {
    /**
     * business data
     */
    public static final String BUSINESS_DATA = "businessData";

    /**
     * context data
     */
    public static final String CONTEXT_DATA = "contextData";

    /**
     * 流程运行实例的traceIds
     */
    public static final String FLOW_TRACE_IDS = "flowTraceIds";

    /**
     * 流程数据的唯一标识
     */
    public static final String FLOW_DATA_ID = "contextId";

    /**
     * 流程定义的唯一标识
     */
    public static final String FLOW_DEFINITION_ID = "flowDefinitionId";

    /**
     * 节点id
     */
    public static final String FLOW_NODE_ID = "nodeMetaId";

    /**
     * businessData._internal 存放内部数据的地方
     */
    public static final String BUSINESS_DATA_INTERNAL_KEY = "_internal";

    /**
     * _internal.executeInfo 存放节点执行信息的地方
     */
    public static final String INTERNAL_EXECUTE_INFO_KEY = "executeInfo";

    /**
     * 节点执行信息中的入参信息
     */
    public static final String EXECUTE_INPUT_KEY = "input";
}
