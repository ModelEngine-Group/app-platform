/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion.repository;

import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;

import java.util.List;
import java.util.Optional;

/**
 * app version 相关数据库操作对象
 *
 * @author 张越
 * @since 2025-01-14
 */
public interface AppVersionRepository {
    /**
     * 根据 app 唯一标识获取 app 对象。
     *
     * @param id 表示 app 的唯一标识的 {@link String}。
     * @return {@link Optional}{@code <}{@link AppVersion}{@code >} 对象。
     */
    Optional<AppVersion> selectById(String id);

    /**
     * 修改一个应用版本.
     *
     * @param appVersion {@link AppVersion} 版本对象.
     */
    void update(AppVersion appVersion);

    /**
     * 根据 path 查看是否重复。
     *
     * @param path 表示 app 的短链唯一标识的 {@link String}。
     * @return 表示短链是否重复 {@link Boolean}。
     */
    boolean checkPathExists(String path);

    /**
     * 通过条件查询.
     *
     * @param cond 条件对象.
     * @return {@link List}{@code <}{@link AppVersion}{@code >} 列表.
     */
    List<AppVersion> selectByCondition(AppQueryCondition cond);

    /**
     * 根据名称查询相似应用名称。
     *
     * @param appName 应用名称。
     * @return {@link List}{@code <}{@link String}{@code >} 相似名称列表。
     */
    List<String> selectWithSimilarName(String appName);

    /**
     * 通过path路径查询.
     *
     * @param path 路径.
     * @return {@link Optional}{@code <}{@link AppVersion}{@code >} 对象.
     */
    Optional<AppVersion> selectByPath(String path);

    /**
     * 通过应用的id来查询版本列表.
     *
     * @param appSuiteId 应用id.
     * @return {@link AppVersion} 列表.
     */
    List<AppVersion> selectByAppSuiteId(String appSuiteId);

    /**
     * 通过tenantId以及查询条件分页查询.
     *
     * @param cond 查询条件.
     * @param tenantId 租户id.
     * @param offset 偏移量.
     * @param limit 条数限制.
     * @return {@link AppVersion} 列表.
     */
    List<AppVersion> pageListByTenantId(AppQueryCondition cond, String tenantId, long offset, int limit);

    /**
     * 保存应用版本。
     *
     * @param appVersion 应用版本对象。
     */
    void save(AppVersion appVersion);

    /**
     * 通过查询条件和tenantId计算应用数量.
     *
     * @param cond 查询条件.
     * @param tenantId 租户id.
     * @return 数量.
     */
    long countByTenantId(AppQueryCondition cond, String tenantId);

    /**
     * 通过id批量删除.
     *
     * @param appIds 版本id集合.
     */
    void deleteByIds(List<String> appIds);

    /**
     * 通过appId获取appSuiteId.
     *
     * @param appId app版本id.
     * @return appSuiteId，应用唯一id.
     */
    String getAppSuiteIdByAppId(String appId);
}
