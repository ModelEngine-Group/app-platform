/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.entity.AippLogData;

import java.util.List;
import java.util.Map;

/**
 * aipp实例历史记录服务接口
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
public interface AippLogService {
    /**
     * 查询指定app最近5个的历史记录
     *
     * @param appId 指定app的id
     * @param type app的类型
     * @param context 登录信息
     * @return log数据
     */
    List<AippInstLogDataDto> queryAippRecentInstLog(String appId, String type, OperationContext context);

    /**
     * 查询指定appId的最近一次会话的历史记录
     *
     * @param appId 指定app的id
     * @param aippType 指定aipp的类型
     * @param context 登录信息
     * @return log数据
     */
    List<AippInstLogDataDto> queryAppRecentChatLog(String appId, String aippType, OperationContext context);

    /**
     * 查询指定chatId的最近5次实例记录
     *
     * @param chatId 指定会话Id
     * @param context 登录信息
     * @param appId 指定app的id
     * @return log数据
     */
    List<AippInstLogDataDto> queryChatRecentChatLog(String chatId, String appId, OperationContext context);

    /**
     * 查询指定aipp instance的历史记录, 可选开始时间
     *
     * @param instanceId 指定aipp instance的id
     * @param timeString 开始的时间范围, 可能为空
     * @return log数据
     */
    List<AippInstLog> queryInstanceLogSince(String instanceId, String timeString);

    /**
     * 查询指定aipp instance的form类型的最新一条历史记录
     *
     * @param instanceId 指定aipp instance的id
     * @return log数据
     */
    AippInstLog queryLastInstanceFormLog(String instanceId);

    /**
     * 删除指定app的历史记录
     *
     * @param appId 指定app的id
     * @param type 指定app的类型
     * @param context 登录信息
     */
    void deleteAippInstLog(String appId, String type, OperationContext context);

    /**
     * 删除指定aipp预览的历史记录
     *
     * @param previewAippId 指定aipp的id
     * @param context 登录信息
     */
    void deleteAippPreviewLog(String previewAippId, OperationContext context);

    /**
     * 插入aipp的历史记录
     *
     * @param logType 日志类型
     * @param logData 日志数据
     * @param businessData 业务数据
     * @return 返回插入的日志id
     */
    String insertLogWithInterception(String logType, AippLogData logData, Map<String, Object> businessData);

    /**
     * 插入ERROR类型的历史记录
     *
     * @param msg ERROR日志内容
     * @param flowData 流程执行上下文数据。
     */
    void insertErrorLog(String msg, List<Map<String, Object>> flowData);

    /**
     * 更新指定log id的记录
     *
     * @param logId 指定log的id
     * @param newLogData 新的log_data
     * @throws IllegalArgumentException 如果logId不存在或者newLogData为空，则抛出此异常
     */
    void updateLog(Long logId, String newLogData) throws IllegalArgumentException;

    /**
     * 更新指定log id的记录
     *
     * @param logId 指定log的id
     * @param newLogType 新的log类型
     * @throws IllegalArgumentException 如果logId不存在或者newLogData为空，则抛出此异常
     */
    void updateLogType(Long logId, String newLogType) throws IllegalArgumentException;

    /**
     * 更新指定log id的记录
     *
     * @param logId 指定log的id
     * @param newLogType 新的日志类型
     * @param newLogData 新的log_data
     * @throws IllegalArgumentException 如果logId不存在或者newLogData为空，则抛出此异常
     */
    void updateLog(Long logId, String newLogType, String newLogData) throws IllegalArgumentException;

    /**
     * 查询指定aipp从暂停后的最近历史记录
     *
     * @param aippId 指定aipp的id
     * @param aippType 指定aipp的类型
     * @param context 登录信息
     * @return log数据
     */
    List<AippInstLogDataDto> queryRecentLogsSinceResume(String aippId, String aippType, OperationContext context);

    /**
     * 根据父Instance的id获取其路径。
     *
     * @param parentInstId 表示父instance的id的 {@link String}。
     * @return 表示父instId的路径的 {@link String}。
     */
    String getParentPath(String parentInstId);

    /**
     * 根据父Instance的路径构建当前Instance的路径。
     *
     * @param instId 表示当前instance的id的 {@link String}。
     * @param parentInstId 表示父instance的id的 {@link String}。
     * @return 表示当前instId的路径的 {@link String}。
     */
    String buildPath(String instId, String parentInstId);

    /**
     * 查询提示词拼接后的历史记录
     *
     * @param aippId 指定aipp的id
     * @param aippType aipp的类型
     * @param count 轮次数目
     * @param context 登录信息
     * @return log数据
     */
    List<AippInstLogDataDto> queryAippRecentInstLogAfterSplice(String aippId, String aippType, Integer count,
            OperationContext context);

    /**
     * 批量查询全量日志（包含父子流程），并过滤掉指定类型的日志。
     *
     * @param instanceIds 表示指定实例 id 列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param filterLogTypes 表示指定日志类型列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示查询到的日志列表的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> queryBatchAndFilterFullLogsByLogType(List<String> instanceIds, List<String> filterLogTypes);

    /**
     * 删除指定的对话历史记录。
     *
     * @param logIds 表示指定的日志 id 列表的 {@link List}{@code <}{@link Long}{@code >}。
     */
    void deleteLogs(List<Long> logIds);

    /**
     * 插入一条日志记录，但不触发发送逻辑。
     *
     * @param logType 表示日志类型的 {@link String}。
     * @param logData 表示日志主体数据的 {@link AippLogData} 实例。
     * @param businessData 表示业务数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    void insertLog(String logType, AippLogData logData, Map<String, Object> businessData);
}
