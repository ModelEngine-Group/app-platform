/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.task_new.repository;

import modelengine.fit.task_new.condition.MetaInstanceCondition;
import modelengine.fit.task_new.entity.MetaInstance;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Meta 实例数据库 Repo 层接口
 *
 * @author 邬涨财
 * @since 2025-03-31
 */
public interface MetaInstanceRepository {
    /**
     * 数据插入。
     *
     * @param metaInstance 表示需要插入的 {@link MetaInstance}。
     */
    void insertOne(MetaInstance metaInstance);

    /**
     * 数据更新。
     *
     * @param metaInstance 表示需要更新的 {@link MetaInstance}。
     */
    void updateOne(MetaInstance metaInstance);

    /**
     * 数据删除。
     *
     * @param ids 表示需要删除的 {@link List<String>}。
     */
    void delete(List<String> ids);

    /**
     * 数据查询
     *
     * @param condition 表示所要查询的条件的 {@link MetaInstanceCondition}。
     * @return 表示查询后的结果的 {@link List}{@code <}{@link MetaInstance}{@code >}。
     */
    List<MetaInstance> select(MetaInstanceCondition condition);

    /**
     * 数据统计
     *
     * @param condition 表示所要统计的条件的 {@link MetaInstanceCondition}。
     * @return 表示查询后的结果的 {@code long}
     */
    long count(MetaInstanceCondition condition);

    /**
     * 获取超期的元数据实例唯一标识列表。
     *
     * @param expiredDays 表示超期时间的 {@link LocalDateTime}。
     * @param limit 表示查询条数的 {@code int}。
     * @return 表示元数据实例的唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> getExpiredInstanceIds(int expiredDays, int limit);

    /**
     * 根据元数据实例唯一标识列表强制删除会话记录。
     *
     * @param ids 表示元数据实例唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    void forceDelete(List<String> ids);
}
