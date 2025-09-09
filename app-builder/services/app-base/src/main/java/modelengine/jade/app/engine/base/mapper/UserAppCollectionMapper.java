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
 * 应用收藏映射
 *
 * @since 2024-5-25
 *
 */
public interface UserAppCollectionMapper {
    /**
     * 插入应用收藏记录
     *
     * @param userAppCollectionDto 应用收藏消息体
     */
    void insert(UserAppCollectionDto userAppCollectionDto);

    /**
     * 通过id删除应用收藏记录
     *
     * @param userInfo 用户信息
     * @param appId 应用id
     */
    void deleteByUserInfoAndAppId(String userInfo, String appId);

    /**
     * 通过id删除应用收藏记录
     *
     * @param appId 应用id
     */
    void deleteByAppId(String appId);

    /**
     * 通过用户信息获取应用收藏列表
     *
     * @param userInfo 用户信息
     * @return 收藏列表
     */
    List<UserAppCollectionPo> getCollectionsByUserInfo(String userInfo);

    /**
     * 通过用户信息获取应用收藏列表详细信息
     *
     * @param userInfo 用户信息
     * @return 收藏列表详细信息
     */
    List<UserAppInfoAndCollectionPo> getAppInfoByUserInfo(String userInfo);

    /**
     * 通过应用id更新收藏用户数量
     *
     * @param collectionNum 收藏用户计数
     * @param appId 应用id
     */
    void updateCollectionUserCntByAppId(String appId, Integer collectionNum);

    /**
     * 查询应用收藏用户数量
     *
     * @param appId 应用id
     * @return 应用收藏用户数
     */
    Integer getCollectionUserCntByAppId(String appId);

    /**
     * 获取默认应用
     *
     * @param userInfo 用户信息
     * @return 应用信息
     */
    UserAppInfoAndCollectionPo getDefaultAppInfo(String userInfo);
}
