/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.util;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.FlowDataConstant;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 流程数据操作的工具类
 *
 * @author songyongtan
 * @since 2024/10/14
 */
public final class FlowDataUtils {
    /**
     * 从flowData中获取flow的运行实例ID
     *
     * @param flowData 流程执行上下文数据
     * @return 返回flow的运行实例ID
     * @throws JobberException 查询不到数据时
     */
    public static String getFlowInstanceId(Map<String, Object> flowData) {
        List<String> traces = cast(getContextData(flowData).get(FlowDataConstant.FLOW_TRACE_IDS));
        Validation.isFalse(CollectionUtils.isEmpty(traces),
                () -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "Flow instance id can not be empty."));
        return traces.get(0);
    }

    /**
     * 获取当前数据的唯一id
     *
     * @param flowData 流程执行上下文数据
     * @return 返回当前数据的标识
     * @throws JobberException 查询不到数据时
     */
    public static String getFlowDataId(Map<String, Object> flowData) {
        String flowDataId = cast(getContextData(flowData).get(FlowDataConstant.FLOW_DATA_ID));
        Validation.isFalse(StringUtils.isBlank(flowDataId),
                () -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "Flow data id can not be empty."));
        return flowDataId;
    }

    /**
     * 获取 flowData中的context data
     *
     * @param flowData 流程执行上下文数据
     * @return 返回context data
     * @throws JobberException 当不存在context data时
     */
    public static Map<String, Object> getContextData(Map<String, Object> flowData) {
        if (!flowData.containsKey(FlowDataConstant.CONTEXT_DATA)) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "Flow context data is not exist.");
        }
        return cast(flowData.get(FlowDataConstant.CONTEXT_DATA));
    }

    /**
     * 获取 flowData中的business data
     *
     * @param flowData 流程执行上下文数据
     * @return 返回 business data
     * @throws JobberException 当不存在 business data 时
     */
    public static Map<String, Object> getBusinessData(Map<String, Object> flowData) {
        if (!flowData.containsKey(FlowDataConstant.BUSINESS_DATA)) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "Flow business data data is not exist.");
        }
        return cast(flowData.get(FlowDataConstant.BUSINESS_DATA));
    }

    /**
     * 获取数据归属的definitionId
     *
     * @param flowData 流程执行上下文数据
     * @return 流程的definitionId
     * @throws JobberException 查询不到数据时
     */
    public static String getFlowDefinitionId(Map<String, Object> flowData) {
        String flowDefinitionId = cast(getContextData(flowData).get(FlowDataConstant.FLOW_DEFINITION_ID));
        Validation.isFalse(StringUtils.isBlank(flowDefinitionId),
                () -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "Flow definition id can not be empty."));
        return flowDefinitionId;
    }

    /**
     * 获取数据对应的节点id
     *
     * @param contextData 流程执行上下文数据中的contextData
     * @return 流程节点id
     * @throws JobberException 查询不到数据时
     */
    public static String getNodeId(Map<String, Object> contextData) {
        String flowNodeId = cast(contextData.get(FlowDataConstant.FLOW_NODE_ID));
        Validation.isFalse(StringUtils.isBlank(flowNodeId),
                () -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "Flow node id can not be empty."));
        return flowNodeId;
    }

    /**
     * 获取指定节点的执行信息
     *
     * @param businessData FlowData中的businessData
     * @param nodeId 节点id
     * @return 节点执行信息。查询不到是返回空列表
     */
    public static List<Map<String, Object>> getExecuteInfo(Map<String, Object> businessData, String nodeId) {
        return cast(getValueByKeyPath(businessData,
                Arrays.asList(FlowDataConstant.BUSINESS_DATA_INTERNAL_KEY, FlowDataConstant.INTERNAL_EXECUTE_INFO_KEY,
                        nodeId), List.class).orElseGet(Collections::emptyList));
    }

    /**
     * 根据指定path查询对象
     *
     * @param map 目标map
     * @param paths paths
     * @param clz 期望的类型
     * @return 结果
     */
    public static <T> Optional<T> getValueByKeyPath(Map<String, Object> map, List<String> paths, Class<T> clz) {
        Map<String, Object> tmp = map;
        for (int i = 0; i < paths.size() - 1; i++) {
            if (tmp.get(paths.get(i)) instanceof Map) {
                tmp = cast(tmp.get(paths.get(i)));
            } else {
                tmp = null;
            }
            if (Objects.isNull(tmp)) {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(ObjectUtils.as(tmp.get(paths.get(paths.size() - 1)), clz));
    }
}
