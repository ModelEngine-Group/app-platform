/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.waterflow.service;

import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;
import modelengine.fit.waterflow.entity.OperationContext;

import java.util.List;
import java.util.Map;

/**
 * 流程定义服务相关接口
 *
 * @author yangxiangyu
 * @since 2025/2/21
 */
public interface FlowDefinitionService {
    /**
     * 创建流程定义
     *
     * @param graphData 流程定义json数据 {@link String}
     * @param context   表示操作上下文的 {@link OperationContext}
     * @return 流程定义对象 {@link FlowDefinitionResult}
     */
    FlowDefinitionResult createFlows(String graphData, OperationContext context);

    /**
     * 更新流程定义
     *
     * @param flowId           流程定义id标识
     * @param graphData        流程定义json数据
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 流程定义对象
     */
    FlowDefinitionResult updateFlows(String flowId, String graphData, OperationContext operationContext);

    /**
     * 删除流程定义
     *
     * @param flowId  流程定义id标识 {@link String}
     * @param context 表示操作上下文的 {@link OperationContext}
     */
    void deleteFlows(String flowId, OperationContext context);

    /**
     * 根据metaid和version删除流程定义
     *
     * @param metaId  流程metaId {@link String}
     * @param version 版本 {@link String}
     * @param context 表示操作上下文的 {@link OperationContext}
     */
    void deleteFlows(String metaId, String version, OperationContext context);

    /**
     * 根据metaid和version强制流程定义
     *
     * @param metaId  metaId
     * @param version version
     * @param context context
     */
    void forceDeleteFlows(String metaId, String version, OperationContext context);

    /**
     * 根据租户id获取流程定义对象列表
     *
     * @param context 表示操作上下文的 {@link OperationContext}
     * @return 流程定义PO对象列表 {@link List} {@code <} {@link FlowDefinitionResult} {@code >}
     */
    List<FlowDefinitionResult> findFlowsByTenant(OperationContext context);

    /**
     * 根据流程名称和版本获取对应流程定义
     *
     * @param name    流程定义名称 {@link String}
     * @param version 流程定义版本 {@link String}
     * @param context 表示操作上下文的 {@link OperationContext}
     * @return 流程定义PO对象 {@link FlowDefinitionResult}
     */
    FlowDefinitionResult findFlowsByName(String name, String version, OperationContext context);

    /**
     * 根据流程定义Id获取对应流程定义
     *
     * @param flowId  流程定义对应id {@link String}
     * @param context 表示操作上下文的 {@link OperationContext}
     * @return 流程定义对象 {@link FlowDefinitionResult}
     */
    FlowDefinitionResult findFlowsById(String flowId, OperationContext context);

    /**
     * 根据流程的metaId和version查询对应流程定义
     *
     * @param metaId  流程的metaId {@link String}
     * @param version 流程定义版本 {@link String}
     * @param context 表示操作上下文的 {@link OperationContext}
     * @return 流程定义对象 {@link FlowDefinitionResult}
     */
    FlowDefinitionResult findFlowsByMetaIdAndVersion(String metaId, String version, OperationContext context);

    /**
     * 根据流程的metaId和部分version查询对应流程定义
     *
     * @param metaId  流程的metaId {@link String}
     * @param version 流程定义版本 {@link String}
     * @return 流程定义对象 {@link FlowDefinitionResult}
     */
    List<FlowDefinitionResult> findFlowsByMetaIdAndPartVersion(String metaId, String version);

    /**
     * 根据metaId和version查询流程定义
     *
     * @param streamIds 流程streamId列表
     * @return 流程定义
     */
    Map<String, FlowDefinitionResult> findFlowsByStreamIds(List<String> streamIds);

    /**
     * 根据fitableIds获取ohScript脚本
     *
     * @param fitableIds 调用服务ids
     * @return 脚本代码
     */
    String getScript(List<String> fitableIds);

    /**
     * 根据fitableid分页查询使用了这个fitable的流程定义
     *
     * @param fitableId fitable id
     * @param offset    offset
     * @param limit     limit
     * @return 流程定义列表和总数
     */
    Map<String, Object> getFlowDefinitionByFitable(String fitableId, Integer offset, Integer limit);

    /**
     * getCountByFitable
     *
     * @param fitableIds fitableIds
     * @return Map<String, Integer>
     */
    Map<String, Integer> getCountByFitable(List<String> fitableIds);

    /**
     * 通过graph数据获取FlowDefinition信息
     *
     * @param flowGraph graph数据
     * @return FlowDefinition信息
     */
    FlowDefinitionResult getFlowDefinitionByGraphData(String flowGraph);

    /**
     * 验证graph data合法性
     *
     * @param definitionData 流程定义数据
     */
    void validateDefinitionData(String definitionData);
}
