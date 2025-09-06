/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.app.engine.base.dto.CollectionAppInfoDto;
import modelengine.jade.app.engine.base.po.UsrAppCollectionPo;
import modelengine.jade.app.engine.base.dto.UsrAppCollectionDto;

import java.util.List;

/**
 * 用户应用收藏功能接口
 *
 * @since 2024-5-25
 *
 */
public interface UsrAppCollectionService {
    /**
     * 插入用户收藏应用信息
     *
     * @param usrCollectionDto 用户应用收藏信息
     * @return 收藏记录id
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrCollectionService.create")
    Long create(UsrAppCollectionDto usrCollectionDto);

    /**
     * 删除用户收藏应用记录
     *
     * @param usrInfo 用户信息
     * @param appId 应用Id
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrCollectionService.deleteByUsrInfoAndAppId")
    void deleteByUsrInfoAndAppId(String usrInfo, String appId);

    /**
     * 删除收藏应用记录
     *
     * @param appId 应用Id
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrCollectionService.deleteByAppId")
    void deleteByAppId(String appId);

    /**
     * 获取用户收藏应用列表
     *
     * @param usrInfo 用户信息
     * @return 用户收藏列表
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrCollectionService.getCollectionsByUsrInfo")
    List<UsrAppCollectionPo> getCollectionsByUsrInfo(String usrInfo);

    /**
     * 获取用户收藏应用列表
     *
     * @param usrInfo 用户信息
     * @return 获取所有应用信息
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrCollectionService.getAppInfoByUsrInfo")
    CollectionAppInfoDto getAppInfoByUsrInfo(String usrInfo);

    /**
     * 通过应用id更新收藏用户数量
     *
     * @param appId 应用id
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrCollectionService.updateCollectionUsrCntByAppId")
    void updateCollectionUsrCntByAppId(String appId);

    /**
     * 通过应用id更新收藏用户数量
     *
     * @param appId 应用id
     * @return 应用收藏用户数量
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrCollectionService.getCollectionUsrCntByAppId")
    Integer getCollectionUsrCntByAppId(String appId);
}
