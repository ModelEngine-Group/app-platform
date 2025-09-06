/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.log.repository;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.domains.log.AppLog;

import java.util.List;

/**
 * 日志仓库接口.
 *
 * @author 张越
 * @since 2025-02-07
 */
public interface AippLogRepository {
    /**
     * 查询任务实例的所有日志，包含子实例的日志.
     *
     * @param instanceId 实例id.
     * @return 日志列表.
     */
    List<AppLog> selectAllLogsByInstanceId(String instanceId);

    /**
     * 根据父Instance的id获取其路径。
     *
     * @param parentInstId 表示父instance的id的 {@link String}。
     * @return 表示父instId的路径的 {@link String}。
     */
    String getParentPath(String parentInstId);

    /**
     * 查询指定实例且指定类型的的日志。
     *
     * @param instanceId 表示指定实例 id 的 {@link String}。
     * @param logTypes 表示指定日志类型列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示查询到的日志列表的 {@link List}{@code <}{@link AppLog}{@code >}。
     */
    List<AppLog> selectByInstanceIdAndLogTypes(String instanceId, List<String> logTypes);

    /**
     * 删除指定实例的历史记录。
     *
     * @param instanceId 指定实例的 id。
     */
    void deleteByInstanceId(String instanceId);

    /**
     * 删除指定aipp预览的历史记录
     *
     * @param previewAippId 指定aipp的id
     * @param context 登录信息
     */
    void deleteAippPreviewLog(String previewAippId, OperationContext context);

    /**
     * 修改数据和类型.
     *
     * @param logId 日志id.
     * @param newLogType 新的日志类型.
     * @param newLogData 新的日志数据.
     */
    void updateDataAndType(Long logId, String newLogType, String newLogData);
}
