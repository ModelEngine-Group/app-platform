/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.service;

import modelengine.fit.waterflow.entity.OperationContext;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.entity.FlowStartDTO;
import modelengine.fit.waterflow.entity.FlowStartInfo;

import java.util.List;
import java.util.Map;

/**
 * 流程启动运行时相关接口
 *
 * @author yangxiangyu
 * @since 2025/2/22
 */
public interface FlowRuntimeService {
    /**
     * 启动流程实例
     *
     * @param flowId           流程定义UUID标识 {@link String}
     * @param flowData         流程启动数据 {@link String}
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 流程实例相关标识
     */
    FlowStartDTO startFlows(String flowId, String flowData, OperationContext operationContext);

    /**
     * 启动流程实例
     *
     * @param flowId           流程定义UUID标识 {@link String}
     * @param flowData         flowData 流程启动数据 {@link FlowStartInfo}
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 流程实例相关标识
     */
    FlowStartDTO startFlows(String flowId, FlowStartInfo flowData, OperationContext operationContext);

    /**
     * 启动流程实例
     *
     * @param metaId   流程定义ID标识 {@link String}
     * @param version  流程定义版本
     * @param flowData 流程启动数据 {@link String}
     * @return String 流程实例transId标识
     */
    FlowStartDTO startFlows(String metaId, String version, String flowData);

    /**
     * 在某个trans下启动流程实例
     *
     * @param metaId   流程定义ID标识 {@link String}
     * @param version  流程定义版本
     * @param transId  transId
     * @param flowData 流程启动数据 {@link String}
     * @return String 流程实例transId标识
     */
    FlowStartDTO startFlowsWithTrans(String metaId, String version, String transId, String flowData);

//    /**
//     * 删除流程实例所有信息
//     *
//     * @param transId 流程实例trans id标识
//     */
//    void deleteFlow(String transId);
//
//    /**
//     * 对流程的指定节点关联数据源
//     *
//     * @param metaId     流程metaId标识
//     * @param version    流程定义版本
//     * @param nodeMetaId 流程中节点的metaId
//     * @param publisher  数据源
//     */
//    void offerFlowNode(String metaId, String version, String nodeMetaId, InterStream<FlowStartInfo> publisher);
//
    /**
     * 人工任务恢复流程执行
     * 参数外层map的key为contextId，value为有更新的业务数据map
     * 内存map的key为businessData和operator，后续可以补充passData
     *
     * @param flowId   流程定义UUID
     * @param contexts 变更的上下文业务数据结合
     */
    void resumeFlows(String flowId, Map<String, Map<String, Object>> contexts);
//
//    /**
//     * 获取流程错误信息
//     *
//     * @param traceId 流程实例traceId
//     * @return 错误信息列表
//     */
//    List<FlowsErrorInfo> getFlowErrorInfo(String traceId);
//
    /**
     * 根据traceId终止流程
     * filter中可以传入与业务相关的过滤条件，停止满足条件的部分context，目前不支持
     *
     * @param traceId          traceId
     * @param filter           filter
     * @param operationContext operationContext
     */
    void terminateFlows(String traceId, Map<String, Object> filter, OperationContext operationContext);

    /**
     * 恢复异步jober
     *
     * @param preIds            之前的contextId列表
     * @param businessDataList 业务执行完返回的数据
     * @param operationContext 操作上下文
     */
    void resumeAsyncJob(List<String> preIds, List<Map<String, Object>> businessDataList,
                        OperationContext operationContext);

    /**
     * 将异步任务设置失败
     *
     * @param preIds           之前的contextId列表
     * @param exception        异常信息
     * @param operationContext 操作上下文
     */
    void failAsyncJob(List<String> preIds, WaterflowException exception,
                      OperationContext operationContext);
//
//    /**
//     * 根据transId终止流程
//     *
//     * @param transId          流程实例id
//     * @param operationContext operationContext
//     */
//    void terminateFlowsByTransId(String transId, OperationContext operationContext);
//
//    /**
//     * 判断一个trans下是否存在终止的trace
//     *
//     * @param transId trans id
//     * @return true or false
//     */
//    boolean hasTerminatedTrace(String transId);
//
//    /**
//     * 根据contextIds删除context
//     *
//     * @param contextIds       contextIds
//     * @param operationContext 操作上下文
//     */
//    void deleteFlowContexts(List<String> contextIds, OperationContext operationContext);
//
//    /**
//     * 更新traceIds的状态
//     *
//     * @param traceIds traceIds
//     * @param status   status
//     */
//    void updateTraceStatus(List<String> traceIds, FlowTraceStatus status);
//
//    /**
//     * 获取context的traceId
//     *
//     * @param contextIds contextIds
//     * @return traceId列表
//     */
//    List<String> findTraceIdsByContextIds(List<String> contextIds);
}
