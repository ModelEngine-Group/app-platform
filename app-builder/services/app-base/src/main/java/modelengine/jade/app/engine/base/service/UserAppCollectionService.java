/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.app.engine.base.dto.CollectionAppInfoDto;
import modelengine.jade.app.engine.base.po.UserAppCollectionPo;
import modelengine.jade.app.engine.base.dto.UserAppCollectionDto;

import java.util.List;

/**
 * 用户应用收藏功能接口。
 *
 * @author 陈潇文
 * @since 2024-05-25
 */
public interface UserAppCollectionService {
    /**
     * 插入用户收藏应用信息。
     *
     * @param userCollectionDto 表示用户应用收藏信息的 {@link UserAppCollectionDto}。
     * @return 表示收藏记录的 {@link Long}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserCollectionService.create")
    Long create(UserAppCollectionDto userCollectionDto);

    /**
     * 删除用户收藏应用记录。
     *
     * @param userInfo 表示用户信息的 {@link String}。
     * @param appId 表示应用唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserCollectionService.deleteByUserInfoAndAppId")
    void deleteByUserInfoAndAppId(String userInfo, String appId);

    /**
     * 删除收藏应用记录。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserCollectionService.deleteByAppId")
    void deleteByAppId(String appId);

    /**
     * 获取用户收藏应用列表。
     *
     * @param userInfo 表示用户信息的 {@link String}。
     * @return 表示用户收藏列表的 {@link List}{@code <}{@link UserAppCollectionPo}{@code >}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserCollectionService.getCollectionsByUserInfo")
    List<UserAppCollectionPo> getCollectionsByUserInfo(String userInfo);

    /**
     * 获取用户收藏应用列表。
     *
     * @param userInfo 表示用户信息的 {@link String}。
     * @return 表示获取所有应用信息的 {@link CollectionAppInfoDto}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserCollectionService.getAppInfoByUserInfo")
    CollectionAppInfoDto getAppInfoByUserInfo(String userInfo);

    /**
     * 通过应用唯一标识更新收藏用户数量。
     *
     * @param appId 表示应用的唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserCollectionService.updateCollectionUserCntByAppId")
    void updateCollectionUserCntByAppId(String appId);

    /**
     * 通过应用唯一标识更新收藏用户数量。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @return 表示应用收藏用户数量的 {@link Integer}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserCollectionService.getCollectionUserCntByAppId")
    Integer getCollectionUserCntByAppId(String appId);
}
