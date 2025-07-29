/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.mapper;

import modelengine.jade.app.engine.base.dto.UsrAppCollectionDto;
import modelengine.jade.app.engine.base.po.UsrAppCollectionPo;
import modelengine.jade.app.engine.base.po.UsrAppInfoAndCollectionPo;

import java.util.List;

/**
 * 应用收藏映射
 *
 * @since 2024-5-25
 *
 */
public interface UsrAppCollectionMapper {
    /**
     * 插入应用收藏记录
     *
     * @param usrAppCollectionDto 应用收藏消息体
     */
    void insert(UsrAppCollectionDto usrAppCollectionDto);

    /**
     * 通过id删除应用收藏记录
     *
     * @param usrInfo 用户信息
     * @param appId 应用id
     */
    void deleteByUsrInfoAndAppId(String usrInfo, String appId);

    /**
     * 通过id删除应用收藏记录
     *
     * @param appId 应用id
     */
    void deleteByAppId(String appId);

    /**
     * 通过用户信息获取应用收藏列表
     *
     * @param usrInfo 用户信息
     * @return 收藏列表
     */
    List<UsrAppCollectionPo> getCollectionsByUsrInfo(String usrInfo);

    /**
     * 通过用户信息获取应用收藏列表详细信息
     *
     * @param usrInfo 用户信息
     * @return 收藏列表详细信息
     */
    List<UsrAppInfoAndCollectionPo> getAppInfoByUsrInfo(String usrInfo);

    /**
     * 通过应用id更新收藏用户数量
     *
     * @param collectionNum 收藏用户计数
     * @param appId 应用id
     */
    void updateCollectionUsrCntByAppId(String appId, Integer collectionNum);

    /**
     * 查询应用收藏用户数量
     *
     * @param appId 应用id
     * @return 应用收藏用户数
     */
    Integer getCollectionUsrCntByAppId(String appId);

    /**
     * 获取默认应用
     *
     * @param usrInfo 用户信息
     * @return 应用信息
     */
    UsrAppInfoAndCollectionPo getDefaultAppInfo(String usrInfo);
}
