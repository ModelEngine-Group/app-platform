/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.aop.Locale;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;

import java.util.List;

/**
 * App 相关的数据库操作
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
public interface AppBuilderAppMapper {
    /**
     * 根据 id 获取 App 数据对象。
     *
     * @param id 表示 app 的唯一标识的 {@link String}。
     * @return 表示 App 数据对象的 {@link AppBuilderAppPo}。
     */
    @Locale
    AppBuilderAppPo selectWithId(String id);

    /**
     * 根据 path 获取 App 数据对象。
     *
     * @param path 表示 app 的短链唯一标识的 {@link String}。
     * @return 表示 App 数据对象的 {@link AppBuilderAppPo}。
     */
    @Locale
    AppBuilderAppPo selectWithPath(String path);

    /**
     * 通过应用的id来查询版本列表.
     *
     * @param appSuiteId 应用id.
     * @return {@link AppBuilderAppPo} 列表.
     */
    @Locale
    List<AppBuilderAppPo> selectByAppSuiteId(String appSuiteId);

    /**
     * 根据租户 id 获取 App 数据对象。
     *
     * @param tenantId 表示租户 id 的唯一标识的 {@link String}。
     * @return 表示 App 数据对象列表的 {@link List}{@code <}{@link AppBuilderAppPo}{@code >}。
     */
    @Locale
    List<AppBuilderAppPo> selectWithTenantId(String tenantId);

    /**
     * 根据指定条件获取 App 数据对象列表。
     *
     * @param cond 表示 App 查询条件的 {@link AppQueryCondition}。
     * @param tenantId 表示租户 id 的唯一标识的 {@link String}。
     * @param offset 表示偏移量的 {@code long}。
     * @param limit 表示获取个数的 {@code int}。
     * @return 表示 App 数据对象列表的 {@link List}{@code <}{@link AppBuilderAppPo}{@code >}。
     */
    List<AppBuilderAppPo> selectByTenantIdWithPage(AppQueryCondition cond, String tenantId, long offset, int limit);

    /**
     * 根据指定条件获取 App 数据对象列表。
     *
     * @param cond 表示 App 查询条件的 {@link AppQueryCondition}。
     * @return 表示 App 数据对象列表的 {@link List}{@code <}{@link AppBuilderAppPo}{@code >}。
     */
    List<AppBuilderAppPo> selectWithCondition(AppQueryCondition cond);

    /**
     * 根据指定条件计算 App 个数。
     *
     * @param tenantId 表示租户 id 的唯一标识的 {@link String}。
     * @param cond 表示 App 查询条件的 {@link AppQueryCondition}。
     * @return 表示 App 个数的 {@code long}。
     */
    long countByTenantId(String tenantId, AppQueryCondition cond);

    /**
     * 插入一条 App 数据对象。
     *
     * @param insert 表示需要插入的 App 数据对象的 {@link AppBuilderAppPo}。
     */
    void insertOne(AppBuilderAppPo insert);

    /**
     * 更新一条 App 数据对象。
     *
     * @param update 表示需要更新的 App 数据对象的 {@link AppBuilderAppPo}。
     */
    void updateOne(AppBuilderAppPo update);

    /**
     * 根据 Store 标识列表获取 App 数据对象列表。
     *
     * @param storeIds 表示 Store 标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示 App 数据对象列表的 {@link List}{@code <}{@link AppBuilderAppPo}{@code >}。
     */
    List<AppBuilderAppPo> selectWithStoreId(List<String> storeIds);

    /**
     * 根据 Store 标识更新 App 数据对象列表。
     *
     * @param uniqueName 表示 Store 标识的 {@link String}。
     * @param id 表示 app 标识的 {@link String}。
     * @param version 表示 app 版本的 {@link String}。
     */
    void updateAppWithStoreId(String uniqueName, String id, String version);

    /**
     * 根据 app 唯一标识删除 app 对象数据。
     *
     * @param appIds 表示 app 唯一标识的 {@link List}{@code <}{@link String}{@code >}。
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
