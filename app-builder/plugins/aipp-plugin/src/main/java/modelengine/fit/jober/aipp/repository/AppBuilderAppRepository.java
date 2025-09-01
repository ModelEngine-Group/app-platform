/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;

import java.util.List;

/**
 * app 相关数据库操作对象
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public interface AppBuilderAppRepository {
    /**
     * 根据 app 唯一标识获取 app 对象。
     *
     * @param id 表示 app 的唯一标识的 {@link String}。
     * @return 表示获取到的 app 对象的 {@link AppBuilderApp}。
     */
    AppBuilderApp selectWithId(String id);

    /**
     * 根据查询条件获取最新的 app 对象列表。
     *
     * @param cond 表示 app 的唯一标识的 {@link String}。
     * @param tenantId 表示租户 id 的唯一标识的 {@link String}。
     * @param offset 表示偏移量的 {@code offset}。
     * @param limit 表示获取到的个数的 {@code int}。
     * @return 表示获取到的最新的 app 对象列表的 {@link List}{@code <}{@link AppBuilderApp}{@code >}。
     */
    List<AppBuilderApp> selectWithLatestApp(AppQueryCondition cond, String tenantId,
            long offset, int limit);

    /**
     * 根据查询条件获取的 app 对象列表。
     *
     * @param cond 表示 app 的唯一标识的 {@link String}。
     * @return 表示获取到的 app 对象列表的 {@link List}{@code <}{@link AppBuilderApp}{@code >}。
     */
    List<AppBuilderApp> selectWithCondition(AppQueryCondition cond);

    /**
     * 根据查询条件获取最新的 app 对象数量。
     *
     * @param tenantId 表示租户 id 的唯一标识的 {@link String}。
     * @param cond 表示过滤条件的 {@link AppQueryCondition}。
     * @return 表示获取到的最新的 app 对象数量的 {@code long}。
     */
    long countWithLatestApp(String tenantId, AppQueryCondition cond);

    /**
     * 根据 app path 获取 app 对象。
     *
     * @param path 表示 app path 的唯一标识的 {@link String}。
     * @return 表示获取到的 app 对象的 {@link AppBuilderApp}。
     */
    AppBuilderApp selectWithPath(String path);

    /**
     * 数据插入。
     *
     * @param appBuilderApp 表示需要插入的 app 对象的 {@link AppBuilderApp}。
     */
    void insertOne(AppBuilderApp appBuilderApp);

    /**
     * 数据更新。
     *
     * @param appBuilderApp 表示需要更新的 app 对象的 {@link AppBuilderApp}。
     */
    void updateOne(AppBuilderApp appBuilderApp);

    /**
     * 批量删除数据
     *
     * @param appIds 表示需要删除的 app 对象唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     */
    void delete(List<String> appIds);

    /**
     * 根据 target 寻找和 target 类似名字的应用。
     *
     * @param target 表示待查找的目标的应用名称的 {@link String}。
     * @return 表示所有相似的应用名称的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> selectWithSimilarName(String target);

    /**
     * 根据 path 查看是否重复。
     *
     * @param path 表示 app 的短链唯一标识的 {@link String}。
     * @return 表示短链是否重复 {@link Boolean}。
     */
    boolean checkPathExists(String path);

    /**
     * 更新访客模式状态。
     *
     * @param path 表示应用的短标识的 {@link String}。
     * @param isGuest 表示访客状态的 {@link Boolean}。
     */
    void updateGuestMode(String path, Boolean isGuest);
}
