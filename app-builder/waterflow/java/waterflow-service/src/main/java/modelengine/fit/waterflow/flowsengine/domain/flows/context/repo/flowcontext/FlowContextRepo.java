/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext;

import modelengine.fit.jade.waterflow.ErrorCodes;
import modelengine.fit.jade.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.flowsengine.biz.service.TraceOwnerService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors.Filter;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程上下文持久化Repo核心类型
 * 包含{@link FlowContextMemoRepo}和{@link FlowContextPersistRepo}两种实现
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public interface FlowContextRepo<T> {
    /**
     * 人工任务节点拉取边上的上下文，在节点的preprocess中处理
     *
     * @param streamId 版本ID
     * @param posIds posId
     * @param status status
     * @return List<FlowContext < T>>
     */
    List<FlowContext<T>> getContextsByPosition(String streamId, List<String> posIds, String status);

    /**
     * 获取节点处理完后产生的新的context，发送给下个节点处理，后续可以判断是否删除该方法
     *
     * @param streamId 版本ID
     * @param posId posId
     * @param batchId 批次ID
     * @param status status
     * @return List<FlowContext < T>>
     */
    List<FlowContext<T>> getContextsByPosition(String streamId, String posId, String batchId, String status);

    /**
     * getContextsByTrace
     *
     * @param traceId transId
     * @return List<FlowContext < T1>>
     */
    <T1> List<FlowContext<T1>> getContextsByTrace(String traceId);

    /**
     * 根据traceId查询所有的context对象
     *
     * @param traceId traceId
     * @return List<FlowContext < T1>>
     */
    <T1> List<FlowContext<T1>> findWithoutFlowDataByTraceId(String traceId);

    /**
     * getContextsByTrace
     *
     * @param traceId transId
     * @param status 状态
     * @return List<FlowContext < T>>
     */
    List<FlowContext<T>> getContextsByTrace(String traceId, String status);

    /**
     * 批量保存context
     *
     * @param contexts contexts
     */
    void save(List<FlowContext<T>> contexts);

    /**
     * 批量更新context的内容，不更新status和position
     *
     * @param contexts contexts
     */
    default void update(List<FlowContext<T>> contexts) {
        save(contexts);
    }

    /**
     * updateToSent
     *
     * @param contexts contexts
     */
    void updateToSent(List<FlowContext<T>> contexts);

    /**
     * updateToReady
     *
     * @param contexts contexts
     */
    void updateToReady(List<FlowContext<T>> contexts);

    /**
     * saveWithoutPassData
     *
     * @param contexts contexts
     */
    default void saveWithoutPassData(List<FlowContext<T>> contexts) {

    }

    /**
     * save
     *
     * @param context context
     */
    void save(FlowContext<T> context);

    /**
     * getContextsByParallel
     *
     * @param parallelId parallelId
     * @return List<FlowContext < T1>>
     */
    <T1> List<FlowContext<T1>> getContextsByParallel(String parallelId);

    /**
     * getById
     *
     * @param id id
     * @return FlowContext<T>
     */
    FlowContext<T> getById(String id);

    /**
     * 根据ids查找FlowContext<T>
     *
     * @param ids ids
     * @return List<FlowContext < T>>
     */
    List<FlowContext<T>> getByIds(List<String> ids);

    /**
     * 根据toBatch查找FlowContext<T>
     *
     * @param toBatchIds 上下文toBatch
     * @return List<FlowContext < T>>
     */
    List<FlowContext<T>> getByToBatch(List<String> toBatchIds);

    /**
     * 查找和指定一批ID对应的状态为PENDING且SENT了的流程上下文
     *
     * @param ids ids
     * @return List<FlowContext < T>>
     */
    List<FlowContext<T>> getPendingAndSentByIds(List<String> ids);

    /**
     * 查找map节点所有from事件上待处理的上下文
     *
     * @param streamId 流程版本ID
     * @param subscriptions from事件的事件ID
     * @param filter 默认过滤器，map的场景永远使用默认过滤器过滤批次数据
     * @param validator block校验器
     * @return 待处理的上下文
     */
    List<FlowContext<T>> requestMappingContext(String streamId, List<String> subscriptions, Filter<T> filter,
            Validator<T> validator);

    /**
     * 查找produce节点所有from事件上待处理的上下文
     *
     * @param streamId 流程版本ID
     * @param subscriptions from事件的事件ID
     * @param filter filter校验器
     * @return 待处理的上下文
     */
    List<FlowContext<T>> requestProducingContext(String streamId, List<String> subscriptions, Filter<T> filter);

    /**
     * 查找流程对应版本所有上下文
     *
     * @param metaId 流程metaId标识
     * @param version 流程对应版本
     * @return 对应所有上下文
     */
    default List<FlowContext<T>> findByStreamId(String metaId, String version) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findByStreamId");
    }

    /**
     * 查找流程对应版本正在运行的上下文
     *
     * @param metaId metaId 流程metaId标识
     * @param version 流程对应版本
     * @return 对应所有上下文
     */
    default Integer findRunningContextCountByMetaId(String metaId, String version) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findRunningContextByMetaId");
    }

    /**
     * 删除流程对应版本所有上下文
     *
     * @param metaId metaId 流程metaId标识
     * @param version 流程对应版本
     */
    default void delete(String metaId, String version) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "delete");
    }

    /**
     * 批量更新trace的contextPool
     *
     * @param after context
     * @param traces 需要更新的tranceId列表
     */
    default void updateContextPool(List<FlowContext<T>> after, Set<String> traces) {
        save(after);
    }

    /**
     * save
     *
     * @param trace trace
     * @param flowContext flowContext
     */
    void save(FlowTrace trace, FlowContext<T> flowContext);

    /**
     * 批量更新context的上下文数据flowData字段
     *
     * @param contexts contexts
     */
    void updateFlowDataAndToBatch(List<FlowContext<T>> contexts);

    /**
     * 批量更新上下文数据
     *
     * @param flowDataList 数据列表（contextId, T）
     */
    default void updateFlowData(Map<String, T> flowDataList) {
    }

    /**
     * 批量更新context的status和position
     *
     * @param contexts contexts
     * @param status status
     * @param position position
     */
    default void updateStatus(List<FlowContext<T>> contexts, String status, String position) {
        save(contexts);
    }

    /**
     * 更新context和trace的状态
     *
     * @param traceIds traceIds
     */
    default void updateToTerminated(List<String> traceIds) {
    }

    /**
     * 判断trace终止
     *
     * @param traceIds traceIds
     * @return boolean
     */
    default boolean isTracesTerminate(List<String> traceIds) {
        return false;
    }

    /**
     * 根据traceId查询所有上下文
     *
     * @param traceId traceId
     * @return 上下文集合
     */
    default List<FlowContext<T>> findByTraceId(String traceId) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findByTraceId");
    }

    /**
     * 根据traceId查询所有错误上下文
     *
     * @param traceId traceId
     * @return 错误上下文集合
     */
    default List<FlowContext<T>> findErrorContextsByTraceId(String traceId) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findByTraceId");
    }

    /**
     * 根据transId查询所有错误上下文
     *
     * @param transId transId
     * @return 错误上下文集合
     */
    default List<FlowContext<T>> findErrorContextsByTransId(String transId) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findByTransId");
    }

    /**
     * 根据transId返回运行状态的contextId
     *
     * @param flowTransId transId
     * @return context ID列表
     */
    default List<String> getRunningContextsIdByTransaction(String flowTransId) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "getRunningContextsIdByTransaction");
    }

    /**
     * 根据transId返回运行状态的contextId
     *
     * @param traceId traceId
     * @return context ID列表
     */
    default List<String> getRunningContextsIdByTraceId(String traceId) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "getRunningContextsIdByTraceId");
    }

    /**
     * 根据transId获取已完成上下文
     * 包括end节点和error状态的上下文
     *
     * @param flowTransId trans id
     * @param endNode 结束节点id
     * @param pageNum 第几页
     * @param limit 每页数量
     * @return 已完成上下文集合
     */
    default List<FlowContext<FlowData>> findFinishedContextsPagedByTransId(String flowTransId, String endNode,
            Integer pageNum, Integer limit) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findPageByStatusAndTransId");
    }

    /**
     * 根据transId获取stream id
     *
     * @param flowTransId trans id
     * @return stream id
     */
    default String getStreamIdByTransId(String flowTransId) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findPageByStatusAndTransId");
    }

    /**
     * 根据transId获取已完成上下文呢数量
     * 包括end节点和错误状态的上下文
     *
     * @param flowTransId trans id
     * @param endNode 结束节点Id
     * @return 已完成上下文集合
     */
    default int findFinishedPageNumByTransId(String flowTransId, String endNode) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findFinishedPageNumByTransId");
    }

    /**
     * 获取结束节点上下文信息
     *
     * @param flowTransId trans id
     * @param endNode 结束节点Id
     * @param pageNum 第几页
     * @param limit 每个个数
     * @return 结束节点上下文集合
     */
    default List<FlowContext<FlowData>> getEndContextsPagedByTransId(String flowTransId, String endNode,
            Integer pageNum, Integer limit) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "getEndContextsPagedByTransId");
    }

    /**
     * 获取结束节点上下文数量
     *
     * @param flowTransId trans id
     * @param endNode 结束节点id
     * @return 结束节点上下文个数
     */
    default int findEndContextsPageNumByTransId(String flowTransId, String endNode) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findEndContextsPageNumByTransId");
    }

    /**
     * 获取错误状态上下文信息
     *
     * @param flowTransId trans id
     * @param pageNum 第几页
     * @param limit 每页个数
     * @return 上下文集合
     */
    default List<FlowContext<FlowData>> getErrorContextsPagedByTransId(String flowTransId, Integer pageNum,
            Integer limit) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "getErrorContextsPagedByTransId");
    }

    /**
     * 获取错误状态上下文数量
     *
     * @param flowTransId trans id
     * @return 上下文集合
     */
    default int findErrorContextsPageNumByTransId(String flowTransId) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findErrorContextsPageNumByTransId");
    }

    /**
     * 根据transId获取traceId
     *
     * @param transId transId
     * @return traceId
     */
    default List<String> getTraceByTransId(String transId) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "getTraceByTransId");
    }

    /**
     * 根据transId删除上下文
     *
     * @param transId trans id
     */
    default void deleteByTransId(String transId) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "deleteByTransId");
    }

    /**
     * 查询可重试状态上下文在重试排表中的记录
     *
     * @param entityId 上下文实体Id
     * @return FlowRetry
     */
    default FlowRetry getRetrySchedule(String entityId) {
        return null;
    }

    /**
     * 查询重试是否达到上限
     *
     * @param entityId 上下文实体Id
     * @return true/false
     */
    default boolean isMaxRetryCount(String entityId) {
        return true;
    }

    /**
     * 批量创建可重试状态上下文的重试排表
     *
     * @param flowRetryList 重试记录列表
     */
    default void createRetrySchedule(List<FlowRetry> flowRetryList) {
    }

    /**
     * 批量更新可重试状态上下文的重试排表
     *
     * @param entityIdList 上下文实体Id列表
     * @param nextRetryTime 下次重试时间
     */
    default void updateRetrySchedule(List<String> entityIdList, LocalDateTime nextRetryTime) {
    }

    /**
     * 批量删除可重试状态上下文在重试排表中的记录
     *
     * @param entityIdList 上下文实体Id列表
     */
    default void deleteRetryRecord(List<String> entityIdList) {
    }

    /**
     * 批量保存可重试状态上下文的重试排表
     *
     * @param contexts context列表
     */
    default void saveRetrySchedule(List<FlowContext<T>> contexts) {
    }

    /**
     * findFinishedContextsPagedByTraceId
     *
     * @param traceId traceId
     * @param endNode endNode
     * @param pageNum pageNum
     * @param limit limit
     * @return List<FlowContext < FlowData>>
     */
    default List<FlowContext<FlowData>> findFinishedContextsPagedByTraceId(String traceId, String endNode,
            Integer pageNum, Integer limit) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findFinishedContextsPagedByTraceId");
    }

    /**
     * findFinishedPageNumByTraceId
     *
     * @param traceId traceId
     * @param endNode endNode
     * @return int
     */
    default int findFinishedPageNumByTraceId(String traceId, String endNode) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findFinishedPageNumByTraceId");
    }

    /**
     * getEndContextsPagedByTraceId
     *
     * @param traceId traceId
     * @param endNode endNode
     * @param pageNum pageNum
     * @param limit limit
     * @return List<FlowContext < FlowData>>
     */
    default List<FlowContext<FlowData>> getEndContextsPagedByTraceId(String traceId, String endNode, Integer pageNum,
            Integer limit) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "getEndContextsPagedByTraceId");
    }

    /**
     * findEndContextsPageNumByTraceId
     *
     * @param traceId traceId
     * @param endNode endNode
     * @return int
     */
    default int findEndContextsPageNumByTraceId(String traceId, String endNode) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findEndContextsPageNumByTraceId");
    }

    /**
     * getErrorContextsPagedByTraceId
     *
     * @param traceId traceId
     * @param pageNum pageNum
     * @param limit limit
     * @return List<FlowContext < FlowData>>
     */
    default List<FlowContext<FlowData>> getErrorContextsPagedByTraceId(String traceId, Integer pageNum, Integer limit) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "getErrorContextsPagedByTraceId");
    }

    /**
     * findErrorContextsPageNumByTraceId
     *
     * @param traceId traceId
     * @return int
     */
    default int findErrorContextsPageNumByTraceId(String traceId) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findErrorContextsPageNumByTransId");
    }

    /**
     * getRunningContextsByTraceId
     *
     * @param traceId traceId
     * @return List<FlowContext < T>>
     */
    default List<FlowContext<T>> getRunningContextsByTraceId(String traceId) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "getRunningContextsByTraceId");
    }

    /**
     * getTraceOwnerService
     *
     * @return TraceOwnerService
     */
    default TraceOwnerService getTraceOwnerService() {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "getTraceOwnerService");
    }

    /**
     * deleteByContextIds
     *
     * @param contextIds contextIds
     */
    default void deleteByContextIds(List<String> contextIds) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "deleteByContextIds");
    }

    /**
     * 根据contextIds获取traceIds
     *
     * @param contextIds contextIds
     * @return traceId列表
     */
    default List<String> findTraceIdsByContextIds(List<String> contextIds) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findTraceIdsByContextIds");
    }

    /**
     * 获取所有已完成上下文呢集合
     * 包括end节点和错误状态的上下文
     *
     * @param flowTransId transId
     * @param endNode 结束节点id
     * @return 上下文集合
     */
    default List<FlowContext<FlowData>> findFinishedContextsByTransId(String flowTransId, String endNode) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findFinishedContextsByTransId");
    }

    /**
     * findFinishedContextsByTraceId
     *
     * @param flowTraceId flowTraceId
     * @param endNode endNode
     * @return List<FlowContext < FlowData>>
     */
    default List<FlowContext<FlowData>> findFinishedContextsByTraceId(String flowTraceId, String endNode) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findFinishedContextsByTraceId");
    }

    /**
     * 在节点处理完成后，之前这批context的状态，toBatch, 节点位置信息
     *
     * @param contexts 同一批context
     */
    default void updateProcessStatus(List<FlowContext<T>> contexts) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "updateProcessStatus");
    }

    /**
     * 根据to batch id查询不带有flow data的上下文数据
     *
     * @param toBatchIds toBatchId列表
     * @return 上下文列表
     */
    default List<FlowContext<String>> getWithoutFlowDataByToBatch(List<String> toBatchIds) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "getWithoutFlowDataByToBatch");
    }

    /**
     * 至少含有一个符合状态的context
     *
     * @param statusList 状态列表
     * @param traceId trace id
     * @return true or false
     */
    boolean hasContextWithStatus(List<String> statusList, String traceId);

    /**
     * 所有context状态都符合要求
     *
     * @param statusList 状态列表
     * @param traceId trace id
     * @return true or false
     */
    boolean isAllContextStatus(List<String> statusList, String traceId);

    /**
     * 在某个节点至少含有一个符合状态的context
     *
     * @param statusList 状态列表
     * @param traceId trace id
     * @param position 位置
     * @return true or false
     */
    boolean hasContextWithStatusAtPosition(List<String> statusList, String traceId, String position);

    /**
     * 根据trace id获取trans id
     *
     * @param traceId trace id
     * @return trans id
     */
    String getTransIdByTrace(String traceId);

    /**
     * 根据链路唯一标识列表删除对应的上下文数据。
     *
     * @param traceIdList 表示链路唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    default void deleteByTraceIdList(List<String> traceIdList) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "deleteByTraceIdList");
    }
}

