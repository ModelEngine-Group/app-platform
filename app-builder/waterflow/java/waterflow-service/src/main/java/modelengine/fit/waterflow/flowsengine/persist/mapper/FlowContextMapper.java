/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.persist.mapper;

import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.persist.entity.FlowContextUpdateInfo;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowContextPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * flow context对应MybatisMapper类
 *
 * @author 杨祥宇
 * @since 2023/8/22
 */
@Mapper
public interface FlowContextMapper {
    /**
     * 根据id查询flow contextPO对象
     *
     * @param flowContextId {@link String} flowContext ID标识
     * @return flowContext ID对应对象 {@link FlowContextPO}
     */
    FlowContextPO find(String flowContextId);

    /**
     * 人工任务节点拉取边上的上下文，在节点的preprocess中处理，SQL中有SENT=FALSE的过滤条件
     *
     * @param streamId 版本ID
     * @param posIds {@link String} context停留在边上的ID
     * @param status {@link String} 查询的context状态
     * @param traceIds {@link List<String>} 查询的traceIds
     * @return flowContextPO对应集合 {@link List}{@code <}{@link FlowContextPO}{@code >}
     */
    List<FlowContextPO> findByPositions(String streamId, List<String> posIds, String status, List<String> traceIds);

    /**
     * 获取节点处理完后产生的新的context，发送给下个节点处理，后续可以判断是否删除该方法
     *
     * @param streamId 版本ID
     * @param posId {@link String} 对应节点Id
     * @param batchId 批次ID
     * @param status {@link String} 查询的context状态
     * @return flowContextPO对应集合 {@link List}{@code <}{@link FlowContextPO}{@code >}
     */
    List<FlowContextPO> findByPositionWithBatchId(String streamId, String posId, String batchId, String status);

    /**
     * 查找map节点或者produce节点所有event边上待处理的上下文
     *
     * @param streamId 版本ID
     * @param subscriptions 对应连接线id {@link List}{@code <}{@link String}{@code >}
     * @param status 查询的context状态 {@link String}
     * @param traceIds 查询的traceIds {@link List}{@code <}{@link String}{@code >}
     * @return List<FlowContextPO>
     */
    List<FlowContextPO> findBySubscriptions(String streamId, List<String> subscriptions, String status,
            List<String> traceIds);

    /**
     * 查找map节点或者produce节点所有event边上指定数量的待处理上下文
     *
     * @param streamId 版本ID
     * @param subscriptions 对应连接线id {@link List}{@code <}{@link String}{@code >}
     * @param status 查询的context状态 {@link String}
     * @param traceIds 查询的traceIds {@link List}{@code <}{@link String}{@code >}
     * @param limit 查询的条数 {@link Integer}
     * @return List<FlowContextPO>
     */
    List<FlowContextPO> findSomeBySubscriptions(String streamId, List<String> subscriptions, String status,
            List<String> traceIds, Integer limit);

    /**
     * 根据streamId查询flow context对象
     *
     * @param streamId 流程定义标识 {@link String}
     * @return flowContextPO对应集合 {@link List}{@code <}{@link FlowContextPO}{@code >}
     */
    List<FlowContextPO> findByStreamId(String streamId);

    /**
     * 根据metaId查询正在运行的context数目
     *
     * @param streamId 流程定义标识 {@link String}
     * @return flowContextPO对应集合 {@link List}{@code <}{@link FlowContextPO}{@code >}
     */
    Integer findRunningContextCountByMetaId(String streamId);

    /**
     * 删除一个stream中所有context对象
     *
     * @param streamId streamId 流程定义标识 {@link String}
     */
    void delete(String streamId);

    /**
     * 批量获取context列表
     *
     * @param streamIds 流程streamId列表
     * @return context列表
     */
    List<FlowContextPO> findByStreamIdList(List<String> streamIds);

    /**
     * 分页获取contexts列表
     *
     * @param streamId 流程streamId
     * @param limit limit
     * @param offset offset
     * @return context列表
     */
    List<FlowContextPO> pageQueryByStreamId(String streamId, int limit, long offset);

    /**
     * 通过streamId获取flowContexts的数量
     *
     * @param streamId 流程streamId
     * @return total
     */
    int getTotalByStreamId(String streamId);

    /**
     * 根据context的id将其SENT标记更新为TRUE
     *
     * @param ids contexts的更新列表
     */
    void updateToSent(List<String> ids);

    /**
     * 通过contextIds获取对应的context列表
     *
     * @param contextIds contextIds
     * @return context列表
     */
    List<FlowContextPO> findByContextIdList(List<String> contextIds);

    /**
     * 通过contextIds获取对应的context列表,去除flowData
     *
     * @param contextIds contextIds
     * @return context列表
     */
    List<FlowContextPO> findWithoutFlowDataByContextIdList(List<String> contextIds);

    /**
     * 批量创建contexts
     *
     * @param flowContextPOS flowContextPOS
     */
    void batchCreate(List<FlowContextPO> flowContextPOS);

    /**
     * 批量更新flowData和其batchId
     *
     * @param flowContextPOS flowContextPOS
     */
    void updateFlowDataAndToBatch(List<FlowContextPO> flowContextPOS);

    /**
     * 批量更新上下文数据
     *
     * @param flowDataList 数据列表, 对应k:v如下：<contextId:flowData序列化后的数据>
     */
    void updateFlowData(@Param("flowDataList") Map<String, String> flowDataList);

    /**
     * 通过contextId列表批量更新状态和位置
     *
     * @param ids ids
     * @param updateInfo 更新信息
     * @param exclusiveStatus 更新的status对应的互斥状态列表，如果数据库的status在exclusiveStatus中，则不能更新
     */
    void updateStatusAndPosition(List<String> ids, FlowContextUpdateInfo updateInfo,
            List<String> exclusiveStatus);

    /**
     * 批量更新contexts
     *
     * @param flowContextPOS flowContextPOS
     */
    void batchUpdate(List<FlowContextPO> flowContextPOS);

    /**
     * 根据trans批量查询context
     *
     * @param flowTransIds 流程实例唯一标识
     * @return context列表
     */
    List<FlowContextPO> findByTransIdList(@Param("flowTransIds") List<String> flowTransIds);

    /**
     * 根据trans批量查询context,不带flowData
     *
     * @param flowTransIds 流程实例唯一标识
     * @return context列表
     */
    List<FlowContextPO> findWithoutFlowDataByTransIdList(@Param("flowTransIds") List<String> flowTransIds);

    /**
     * 批量更新context状态
     *
     * @param contextId context标识
     * @param status context状态
     */
    void updateStatus(@Param("contextIds") List<String> contextId, @Param("status") FlowNodeStatus status);

    /**
     * 查询非archived状态的context数目
     *
     * @param streamId 流程streanid
     * @return context列表
     */
    Integer findUnarchivedContextCountByMetaId(String streamId);

    /**
     * 根据traceId查询context
     *
     * @param traceId traceId
     * @return context列表
     */
    List<FlowContextPO> findByTraceId(@Param("traceId") String traceId);

    /**
     * 根据traceId查询所有的context对象
     *
     * @param traceId traceId
     * @return List<FlowContextPO>
     */
    List<FlowContextPO> findWithoutFlowDataByTraceId(String traceId);

    /**
     * 根据traceId查询context
     *
     * @param traceId traceId
     * @return context列表
     */
    List<FlowContextPO> findErrorContextByTraceId(@Param("traceId") String traceId);

    /**
     * 根据transId获取所有错误上下文信息
     *
     * @param transId transId
     * @return 错误上下文集合
     */
    List<FlowContextPO> findErrorContextByTransId(String transId);

    /**
     * 根据stream id批量查询context
     *
     * @param streamIds stream id列表
     * @return context列表
     */
    List<FlowContextPO> findRunningContextByStreamIds(@Param("streamIds") List<String> streamIds);

    /**
     * 根据transId获取未完成的context id列表
     *
     * @param flowTransId trans id
     * @return context列表
     */
    List<String> getRunningContextsIdByTransaction(@Param("transId") String flowTransId);

    /**
     * 根据transId获取已完成的上下文
     * 包括end节点以及运行失败的上下文
     *
     * @param transId trans id
     * @param endNode endNode
     * @param pageNum 页数
     * @param limit 每页个数
     * @return context列表
     */
    List<FlowContextPO> findFinishedContextsPagedByTransId(String transId, String endNode, Integer pageNum,
            Integer limit);

    /**
     * 根据transId获取streamId
     *
     * @param transId trans id
     * @return stream id
     */
    String getStreamIdByTransId(String transId);

    /**
     * 根据transId获取已完成上下文数量
     * 包括end节点以及运行失败的上下文
     *
     * @param transId trans id
     * @param endNode end节点id
     * @return 总数量
     */
    int findFinishedPageNumByTransId(String transId, String endNode);

    /**
     * 获取end节点已完成上下文
     *
     * @param transId trans id
     * @param endNode end节点id
     * @param pageNum 页数
     * @param limit 每页数量
     * @return 上下文信息列表
     */
    List<FlowContextPO> findEndContextsPagedByTransId(String transId, String endNode, Integer pageNum, Integer limit);

    /**
     * 获取end节点上下文数量
     *
     * @param transId trans id
     * @param endNode end节点id
     * @return end节点上下文数量
     */
    int findEndContextsNumByTransId(String transId, String endNode);

    /**
     * 根据transId获取失败的上下文
     *
     * @param transId trans id
     * @param pageNum 页数
     * @param limit 每页数量
     * @return 失败上下文集合
     */
    List<FlowContextPO> findErrorContextsPagedByTransId(String transId, Integer pageNum, Integer limit);

    /**
     * 根据transId获取失败上下文数量
     *
     * @param transId trans id
     * @return 失败上下文数量
     */
    int findErrorContextsNumByTransId(String transId);

    /**
     * 根据transId获取tranceId
     *
     * @param flowTransId transId
     * @return traceId
     */
    List<String> getTraceByTransId(String flowTransId);

    /**
     * 根据transId删除上下文
     *
     * @param flowTransId transId
     */
    void deleteByTransId(String flowTransId);

    /**
     * 根据traceId获取未完成的context id列表
     *
     * @param traceId trace id
     * @return context id列表
     */
    List<String> getRunningContextsIdByTraceId(String traceId);

    /**
     * 根据toBatchId查询上下文
     *
     * @param toBatchIds toBatchIds
     * @return 上下文列表
     */
    List<FlowContextPO> findByToBatch(List<String> toBatchIds);

    /**
     * 根据traceId获取上下文
     *
     * @param traceId trace id
     * @return 上下文列表
     */
    List<FlowContextPO> getRunningContextsByTraceId(String traceId);

    /**
     * 根据traceId获取已完成的上下文
     * 包括end节点以及运行失败的上下文
     *
     * @param traceId trans id
     * @param endNode endNode
     * @param pageNum 页数
     * @param limit 每页个数
     * @return context列表
     */
    List<FlowContextPO> findFinishedContextsPagedByTraceId(String traceId, String endNode, Integer pageNum,
            Integer limit);

    /**
     * 根据traceId获取已完成上下文数量
     * 包括end节点以及运行失败的上下文
     *
     * @param traceId trans id
     * @param endNode end节点id
     * @return 总数量
     */
    int findFinishedPageNumByTraceId(String traceId, String endNode);

    /**
     * 获取end节点已完成上下文
     *
     * @param traceId trans id
     * @param endNode end节点id
     * @param pageNum 页数
     * @param limit 每页数量
     * @return 上下文信息列表
     */
    List<FlowContextPO> findEndContextsPagedByTraceId(String traceId, String endNode, Integer pageNum, Integer limit);

    /**
     * 获取end节点上下文数量
     *
     * @param traceId trans id
     * @param endNode end节点id
     * @return end节点上下文数量
     */
    int findEndContextsNumByTraceId(String traceId, String endNode);

    /**
     * 根据traceId获取失败的上下文
     *
     * @param traceId trans id
     * @param pageNum 页数
     * @param limit 每页数量
     * @return 失败上下文集合
     */
    List<FlowContextPO> findErrorContextsPagedByTraceId(String traceId, Integer pageNum, Integer limit);

    /**
     * 根据traceId获取失败上下文数量
     *
     * @param traceId trans id
     * @return 失败上下文数量
     */
    int findErrorContextsNumByTraceId(String traceId);

    /**
     * 根据contextIds删除context
     *
     * @param contextIds contextIds
     */
    void deleteByContextIds(List<String> contextIds);

    /**
     * findTraceIdsByContextIds
     *
     * @param contextIds contextIds
     * @return trace id列表
     */
    List<String> findTraceIdsByContextIds(List<String> contextIds);

    /**
     * 获取所有已完成上下文信息
     *
     * @param transId trans id
     * @param endNode 结束节点id
     * @return 已完成上下文信息集合
     */
    List<FlowContextPO> findFinishedContextsByTransId(String transId, String endNode);

    /**
     * 根据traceId获取所有已完成上下文信息
     *
     * @param traceId trace id
     * @param endNode 结束节点id
     * @return 已完成上下文信息集合
     */
    List<FlowContextPO> findFinishedContextsByTraceId(String traceId, String endNode);

    /**
     * 更新节点处理后的状态
     *
     * @param ids id列表
     * @param updateInfo 更新信息
     * @param exclusiveStatus 独占状态
     */
    void updateProcessStatus(List<String> ids, FlowContextUpdateInfo updateInfo, List<String> exclusiveStatus);

    /**
     * 根据to batch id列表获取不包含flow data的上下文信息
     *
     * @param toBatchIds to batch id 列表
     * @return 上下文集合
     */
    List<FlowContextPO> findWithoutFlowDataByToBatch(List<String> toBatchIds);

    /**
     * 统计某些状态的context数量
     *
     * @param statusList status列表
     * @param traceId trace id
     * @return context数量
     */
    int findCountByStatus(List<String> statusList, String traceId);

    /**
     * 统计在某个位置上某些状态的context数量
     *
     * @param statusList status 列表
     * @param traceId trace id
     * @param position 位置
     * @return context数量
     */
    int findCountByStatusAtPosition(List<String> statusList, String traceId, String position);

    /**
     * 查找不属于某些状态的context数量
     *
     * @param statusList status列表
     * @param traceId trace id
     * @return context数量
     */
    int findCountNotInStatus(List<String> statusList, String traceId);

    /**
     * 根据trace id获取对应trans id
     *
     * @param traceId trace id
     * @return trans id
     */
    String getTransIdByTrace(String traceId);

    /**
     * 根据trace列表删除对应的context
     *
     * @param traceIds trace id列表
     */
    void deleteByTraceIdList(List<String> traceIds);
}
