/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.mapper;

import modelengine.jade.app.engine.base.dto.UserAppCollectionDto;
import modelengine.jade.app.engine.base.po.UserAppCollectionPo;
import modelengine.jade.app.engine.base.po.UserAppInfoAndCollectionPo;

import java.util.List;

/**
 * 应用收藏映射。
 *
 * @author 陈潇文
 * @since 2024-05-25
 */
public interface UserAppCollectionMapper {
    /**
     * 插入应用收藏记录。
     *
     * @param userAppCollectionDto 表示应用收藏消息体的 {@link UserAppCollectionDto}。
     */
    void insert(UserAppCollectionDto userAppCollectionDto);

    /**
     * 通过唯一标识删除应用收藏记录。
     *
     * @param userInfo 表示用户信息的 {@link String}。
     * @param appId 表示应用唯一标识的 {@link String}。
     */
    void deleteByUserInfoAndAppId(String userInfo, String appId);

    /**
     * 通过唯一标识删除应用收藏记录。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     */
    void deleteByAppId(String appId);

    /**
     * 通过用户信息获取应用收藏列表。
     *
     * @param userInfo 表示用户信息的 {@link String}。
     * @return 表示收藏列表的 {@link List}{@code <}{@link UserAppCollectionPo}{@code >}。
     */
    List<UserAppCollectionPo> getCollectionsByUserInfo(String userInfo);

    /**
     * 通过用户信息获取应用收藏列表详细信息。
     *
     * @param userInfo 表示用户信息的 {@link String}。
     * @return 表示收藏列表详细信息的 {@link List}{@code <}{@link UserAppInfoAndCollectionPo}{@code >}。
     */
    List<UserAppInfoAndCollectionPo> getAppInfoByUserInfo(String userInfo);

    /**
     * 通过应用唯一标识更新收藏用户数量。
     *
     * @param collectionNum 表示收藏用户计数的 {@link String}。
     * @param appId 表示应用唯一标识的 {@link Integer}。
     */
    void updateCollectionUserCntByAppId(String appId, Integer collectionNum);

    /**
     * 查询应用收藏用户数量。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @return 表示应用收藏用户数的 {@link Integer}。
     */
    Integer getCollectionUserCntByAppId(String appId);

    /**
     * 获取默认应用。
     *
     * @param userInfo 表示用户信息的 {@link String}。
     * @return 表示应用信息的 {@link UserAppInfoAndCollectionPo}。
     */
    UserAppInfoAndCollectionPo getDefaultAppInfo(String userInfo);
}
