/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogQueryCondition;
import modelengine.fit.jober.aipp.entity.AippInstLog;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * aipp实例历史记录db接口
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
public interface AippLogMapper {
    /**
     * 根据 aipp id 查询最近若干条历史记录。
     *
     * @param aippId 表示指定 aipp id 的 {@link String}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @return 表示查询到的历史记录的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> selectRecentByAippId(String aippId, String aippType, String createUserAccount);

    /**
     * 根据 aipp id 查询指定数量的 instance id。
     *
     * @param aippId 表示指定 aipp id 的 {@link String}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param number 表示查询数量的 {@link Integer}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @return 表示查询到的实例 id 的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> selectRecentInstanceId(String aippId, String aippType, Integer number, String createUserAccount);

    /**
     * 根据 aipp id 列表查询最近若干条 instance id。
     *
     * @param aippIds 表示指定 aipp id 的 {@link List}{@code <}{@link String}{@code >}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param number 表示查询数量的 {@link Integer}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @return 表示查询到的实例 id 的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> selectRecentInstanceIdByAippIds(List<String> aippIds, String aippType, Integer number,
            String createUserAccount);

    /**
     * 用于查询简历之后的实例 id 列表。
     *
     * @param aippId 表示指定 aipp id 的 {@link String}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @return 表示查询到的实例 id 的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> selectRecentAfterResume(String aippId, String aippType, String createUserAccount);

    /**
     * 根据 path 查询 instanceId 的历史记录（包括当前实例的信息，以及子实例的FORM待确认表单信息）。
     *
     * @param instanceIds 表示指定实例 id 列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示查询到的历史记录的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> getFullLogsByInstanceIds(List<String> instanceIds);

    /**
     * 根据条件查询历史记录。
     *
     * @param cond 表示查询条件的 {@link AippLogQueryCondition}。
     * @return 表示查询到的历史记录的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> selectWithCondition(AippLogQueryCondition cond);

    /**
     * 根据 instance id 查询最新的 FORM 类型历史记录。
     *
     * @param instanceId 表示指定实例 id 的 {@link String}。
     * @return 表示查询到的历史记录的 {@link AippInstLog}。
     */
    AippInstLog selectLastInstanceFormLog(String instanceId);

    /**
     * 根据 aipp id 列表按时间倒序查询 instance id 列表。
     *
     * @param aippIds 表示指定 aipp id 的 {@link List}{@code <}{@link String}{@code >}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @return 表示查询到的实例 id 的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> selectNormalInstanceIdOrderByTimeDesc(List<String> aippIds, String aippType, String createUserAccount);

    /**
     * 根据 aipp 类型删除历史记录。
     *
     * @param aippId 表示指定 aipp id 的 {@link String}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @param instanceIdExclude 表示排除的 instance id 的 {@link String}。
     */
    void deleteByType(String aippId, String aippType, String createUserAccount, String instanceIdExclude);

    /**
     * 根据 aipp id 批量删除历史记录。
     *
     * @param aippIds 表示指定 aipp id 的 {@link List}{@code <}{@link String}{@code >}。
     * @param aippType 表示指定 aipp 类型的 {@link String}。
     * @param createUserAccount 表示创建者账号的 {@link String}。
     * @param instanceIdExclude 表示排除的 instance id 的 {@link String}。
     */
    void delete(List<String> aippIds, String aippType, String createUserAccount, String instanceIdExclude);

    /**
     * 插入一条历史记录。
     *
     * @param data 表示 aipp 实例历史记录的 {@link AippLogCreateDto}。
     */
    void insertOne(AippLogCreateDto data);

    /**
     * 更新指定历史记录。
     *
     * @param logId 表示指定历史记录 id 的 {@link Long}。
     * @param newLogData 表示新的历史记录的 {@link String}。
     */
    void updateOne(Long logId, String newLogData);

    /**
     * 更新指定历史记录。
     *
     * @param logId 表示指定历史记录 id 的 {@link Long}。
     * @param logType 表示新的类型的 {@link String}。
     */
    void updateLogType(Long logId, String logType);

    /**
     * 更新指定历史记录和类型
     *
     * @param logId 表示指定历史记录 id 的 {@link Long}。
     * @param logType 表示日志记录 的 {@link String}。
     * @param newLogData 表示新的历史记录的 {@link String}。
     */
    void updateDataAndType(Long logId, String logType, String newLogData);

    /**
     * 获取历史记录的 path 字段。
     *
     * @param parentId 表示实例 id 的 {@link String}。
     * @return 表示查询到的 path 的 {@link String}。
     */
    String getParentPath(String parentId);

    /**
     * 删除指定实例的历史记录。
     *
     * @param instanceId 表示指定实例 id 的 {@link String}。
     */
    void deleteInstanceLog(String instanceId);

    /**
     * 根据 instanceId 查询指定实例的历史记录。
     *
     * @param instanceId 表示指定实例 id 的 {@link String}。
     * @return 表示 aipp 实例历史记录的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> getLogsByInstanceId(String instanceId);

    /**
     * 根据 instanceId 和 logTypes 查询指定实例的历史记录。
     *
     * @param instanceId 表示指定实例 id 的 {@link String}。
     * @param logTypes 表示指定日志类型列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示 aipp 实例历史记录的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> getLogsByInstanceIdAndLogTypes(String instanceId, @Param("logTypes")List<String> logTypes);

    /**
     * 删除指定实例的历史记录。
     *
     * @param instanceIds 表示指定实例 id 集合。
     */
    void deleteByInstanceIds(List<String> instanceIds);

    /**
     * 根据 logId 删除对话记录。
     *
     * @param logIds 表示指定的历史记录 id 的 {@link List}{@code <}{@link Long}{@code >}。
     */
    void deleteInstanceLogs(@Param("logIds") List<Long> logIds);

    /**
     * 获取超期的调试对话记录唯一标识列表。
     *
     * @param expiredDays 表示超期时间的 {@code int}。
     * @param limit 表示查询条数的 {@code int}。
     * @return 表示历史会话记录的id列表的 {@link List}{@code <}{@link Long}{@code >}。
     */
    List<Long> getExpireInstanceLogIds(String aippType, int expiredDays, int limit);

    /**
     * 根据实例唯一标识列表强制删除会话记录。
     *
     * @param logIds 表示会话实例id列表的 {@link List}{@code <}{@link Long}{@code >}。
     */
    void forceDeleteInstanceLogsByIds(List<Long> logIds);

    /**
     * 根据日志唯一标识列表查询会话历史记录。
     *
     * @param logIds 标识日志唯一标识列表的 {@link List}{@code <}{@link Long}{@code >}。
     * @return 表示实例历史记录列表的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> selectByLogIds(@Param("logIds") List<Long> logIds);
}
