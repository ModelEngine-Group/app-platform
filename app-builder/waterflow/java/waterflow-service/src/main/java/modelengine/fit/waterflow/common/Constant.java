/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.common;

import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 常数类
 *
 * @author 晏钰坤
 * @since 2023/6/15
 */
public final class Constant {
    /**
     * 任务第三方id
     */
    public static final String REQUIREMENT_ID = "id";

    /**
     * 责任人
     */
    public static final String OWNER = "owner";

    /**
     * 重试的时间间隔
     */
    public static final Integer RETRY_INTERVAL = 2000;

    /**
     * 流程定义ID分隔符
     */
    public static final char STREAM_ID_SEPARATOR = '-';

    /**
     * 流程节点事件中条件属性的KEY值
     */
    public static final String CONDITION_RULE_PROPERTY_KEY = "conditionRule";

    /**
     * 流程节点事件中条件优先级的KEY值
     */
    public static final String PRIORITY_PROPERTY_KEY = "priority";

    /**
     * 任务定义status字段
     */
    public static final String STATUS = "status";

    /**
     * 任务定义优先级字段
     */
    public static final String PRIORITY = "priority";

    /**
     * 创建时间
     */
    public static final String CREATED_DATE = "created_date";

    /**
     * 任务定义title字段
     */
    public static final String TITLE = "title";

    /**
     * 任务定义created_by字段
     */
    public static final String CREATED_BY = "created_by";

    /**
     * 流程上下文 ID字段
     */
    public static final String FLOW_CONTEXT_ID_KEY = "flowContextId";

    /**
     * 流程trace ID字段
     */
    public static final String TRACE_ID_KEY = "traceId";

    /**
     * 流程节点 ID字段
     */
    public static final String NODE_ID_KEY = "nodeId";

    /**
     * 阶段信息
     */
    public static final String NODE_STAGE_KEY = "stage";

    /**
     * 流程节点属性配置
     */
    public static final String NODE_PROPERTIES_KEY = "nodeProperties";

    /**
     * 节点类型
     */
    public static final String NODE_TYPE_KEY = "nodeType";

    /**
     * 流程节点 ID字段
     */
    public static final String NODE_META_ID_KEY = "nodeMetaId";

    /**
     * 流程业务数据businessData字段
     */
    public static final String BUSINESS_DATA_KEY = "businessData";

    /**
     * 流程业务数据operator字段
     */
    public static final String OPERATOR_KEY = "operator";

    /**
     * 用于处理流程引擎发布事件的线程池
     */
    public static final String FLOWS_EVENT_HANDLER_EXECUTOR = "flowsEventHandlerExecutor";

    /**
     * 流程运行流程元数据信息
     */
    public static final String CONTEXT_DATA = "contextData";

    /**
     * 流程运行数据上下文id
     */
    public static final String CONTEXT_ID = "contextId";

    /**
     * 流程运行非落盘数据
     */
    public static final String PASS_DATA = "passData";

    /**
     * 流程自动任务重试实体toBatch类型
     */
    public static final String TO_BATCH_KEY = "toBatch";

    /**
     * 内置数据key
     */
    public static final String BUSINESS_DATA_INTERNAL_KEY = "_internal";

    /**
     * 不同节点输出数据的缓存区
     */
    public static final String INTERNAL_OUTPUT_SCOPE_KEY = "outputScope";

    /**
     * 不同节点输入输出数据的缓存区
     */
    public static final String INTERNAL_EXECUTE_INFO_KEY = "executeInfo";

        /**
         * 内部跳过标记，用于让未命中的条件分支继续参与后续汇聚。
         */
        public static final String INTERNAL_SKIPPED_SIGNAL_KEY = "skippedSignal";

    /**
     * 存储系统参数的虚拟节点id
     */
    public static final String SYSTEM_PARAMETER_NODE_KEY = "_systemEnv";

    /**
     * 节点元数据信息中的节点id
     */
    public static final String NODE_META_ID = "nodeMetaId";

    /**
     * 节点元数据信息中的节点类型
     */
    public static final String NODE_TYPE = "nodeType";

    /**
     * 提取用户输出数据需要忽略的key
     */
    public static final Set<String> BUSINESS_DATA_IGNORED_KEYS = new HashSet<>(
            Arrays.asList(BUSINESS_DATA_INTERNAL_KEY));

    /**
     * flowContext 终止状态的互斥状态列表，即如果原始状态为terminate\error\archived，则不能更改为terminate
     */
    public static final List<String> CONTEXT_TERMINATE_EXCLUSIVE_STATUS_LIST = Collections.unmodifiableList(
            Arrays.asList(FlowNodeStatus.TERMINATE.toString(), FlowNodeStatus.ARCHIVED.toString(),
                    FlowNodeStatus.ERROR.toString()));

    /**
     * flowContext 重试状态的互斥状态列表
     */
    public static final List<String> CONTEXT_RETRYABLE_EXCLUSIVE_STATUS_LIST = Collections.unmodifiableList(
            Arrays.asList(FlowNodeStatus.NEW.toString(), FlowNodeStatus.PENDING.toString(),
                    FlowNodeStatus.PROCESSING.toString(), FlowNodeStatus.TERMINATE.toString(),
                    FlowNodeStatus.ARCHIVED.toString(), FlowNodeStatus.ERROR.toString(),
                    FlowNodeStatus.RETRYABLE.toString()));

    /**
     * flowContext 其他状态的互斥状态列表
     */
    public static final List<String> CONTEXT_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST = Collections.singletonList(
            FlowNodeStatus.TERMINATE.toString());

    /**
     * error状态互斥表
     */
    public static final List<String> CONTEXT_ERROR_EXCLUSIVE_STATUS_LIST = Collections.unmodifiableList(
            Arrays.asList(FlowNodeStatus.TERMINATE.toString(), FlowNodeStatus.ARCHIVED.toString(),
                    FlowNodeStatus.RETRYABLE.toString(), FlowNodeStatus.PENDING.toString(),
                    FlowNodeStatus.ERROR.toString()));

    /**
     * archived状态互斥表
     */
    public static final List<String> CONTEXT_ARCHIVED_EXCLUSIVE_STATUS_LIST = Collections.unmodifiableList(
            Arrays.asList(FlowNodeStatus.TERMINATE.toString(), FlowNodeStatus.ARCHIVED.toString(),
                    FlowNodeStatus.RETRYABLE.toString(), FlowNodeStatus.PENDING.toString()));

    /**
     * flowContext状态互斥map
     */
    public static final Map<String, List<String>> CONTEXT_EXCLUSIVE_STATUS_MAP = new HashMap<String, List<String>>() {
        {
            put(FlowNodeStatus.RETRYABLE.toString(), CONTEXT_RETRYABLE_EXCLUSIVE_STATUS_LIST);
            put(FlowNodeStatus.TERMINATE.toString(), CONTEXT_TERMINATE_EXCLUSIVE_STATUS_LIST);
            put(FlowNodeStatus.ERROR.toString(), CONTEXT_ERROR_EXCLUSIVE_STATUS_LIST);
            put(FlowNodeStatus.PENDING.toString(), CONTEXT_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
                        put(FlowNodeStatus.SKIPPED.toString(), CONTEXT_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
            put(FlowNodeStatus.READY.toString(), CONTEXT_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
            put(FlowNodeStatus.PROCESSING.toString(), CONTEXT_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
            put(FlowNodeStatus.ARCHIVED.toString(), CONTEXT_ARCHIVED_EXCLUSIVE_STATUS_LIST);
        }
    };

    /**
     * flowTrace 终止状态的互斥状态列表，即如果原始状态为terminate\error\archived，则不能更改为terminate
     */
    public static final List<String> TRACE_TERMINATE_EXCLUSIVE_STATUS_LIST = Collections.unmodifiableList(
            Arrays.asList(FlowTraceStatus.TERMINATE.toString(), FlowTraceStatus.ARCHIVED.toString(),
                    FlowTraceStatus.ERROR.toString()));

    /**
     * flowTrace 其他状态的互斥状态列表
     */
    public static final List<String> TRACE_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST = Collections.singletonList(
            FlowTraceStatus.TERMINATE.toString());

    /**
     * flowTrace状态互斥map
     */
    public static final Map<String, List<String>> TRACE_EXCLUSIVE_STATUS_MAP = new HashMap<String, List<String>>() {
        {
            put(FlowTraceStatus.TERMINATE.toString(), TRACE_TERMINATE_EXCLUSIVE_STATUS_LIST);
            put(FlowTraceStatus.ERROR.toString(), TRACE_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
            put(FlowTraceStatus.RUNNING.toString(), TRACE_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
            put(FlowTraceStatus.READY.toString(), TRACE_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
            put(FlowTraceStatus.ARCHIVED.toString(), TRACE_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
            put(FlowTraceStatus.PARTIAL_ERROR.toString(), TRACE_NONE_TERMINATE_EXCLUSIVE_STATUS_LIST);
        }
    };
}
