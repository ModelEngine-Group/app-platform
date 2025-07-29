/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.entity.AippInstLog;

import java.util.List;

/**
 * 应用实例历史记录的存储仓库。
 *
 * @author 杨祥宇
 * @since 2025-04-09
 */
public interface AippInstanceLogRepository {
    /**
     * 获取调试类型的应用过期历史记录。
     *
     * @param expiredDays 表示超期天数的 {@code int}。
     * @param limit 表示查询条数的 {@code int}。
     * @return 表示超期历史记录id的 {@link List}{@code <}{@link Long}{@code >}。
     */
    List<Long> getExpireInstanceLogIds(String aippType, int expiredDays, int limit);

    /**
     * 根据日志唯一标识列表强制删除历史记录。
     *
     * @param logIds 表示历史记录的唯一标识列表的 {@link List}{@code <}{@link Long}{@code >}。
     */
    void forceDeleteInstanceLogs(List<Long> logIds);

    /**
     * 根据日志唯一标识列表查询会话历史记录
     *
     * @param logIds 标识日志唯一标识列表的 {@link List}{@code <}{@link Long}{@code >}。
     * @return 表示实例历史记录列表的 {@link List}{@code <}{@link AippInstLog}{@code >}。
     */
    List<AippInstLog> selectByLogIds(List<Long> logIds);
}
