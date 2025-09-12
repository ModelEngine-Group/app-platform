/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.controller;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.app.engine.base.common.resp.Response;
import modelengine.jade.app.engine.base.common.resp.ResponseCode;
import modelengine.jade.app.engine.base.dto.CollectionAppInfoDto;
import modelengine.jade.app.engine.base.dto.UserAppCollectionDto;
import modelengine.jade.app.engine.base.po.UserAppCollectionPo;
import modelengine.jade.app.engine.base.service.UserAppCollectionService;

import java.util.List;

/**
 * 处理用户收藏应用请求。
 *
 * @author 陈潇文
 * @since 2024-05-29
 */
@Component
@RequestMapping("/aipp/user")
public class UserCollectionController {
    private final UserAppCollectionService userAppCollectionService;

    public UserCollectionController(UserAppCollectionService userAppCollectionService) {
        this.userAppCollectionService = userAppCollectionService;
    }

    /**
     * 创建用户应用收藏记录。
     *
     * @param userAppCollectionDto 表示用户应用收藏消息体的 {@link UserAppCollectionDto}。
     * @return 表示返回收藏记录标识的 {@link Response}{@code <}{@link Long}{@code >}。
     */
    @PostMapping("/collection")
    public Response<Long> createUserAppCollection(@RequestBody UserAppCollectionDto userAppCollectionDto) {
        Long collectionId = this.userAppCollectionService.create(userAppCollectionDto);
        return Response.success(collectionId, ResponseCode.OK);
    }

    /**
     * 删除应用收藏记录。
     *
     * @param userAppCollectionDto 表示应用收藏记录信息。
     * @return 表示响应的 {@link Response}{@code <}{@link Void}{@code >}。
     */
    @DeleteMapping("/collection")
    public Response<Void> deleteUserCollectionApp(@RequestBody UserAppCollectionDto userAppCollectionDto) {
        this.userAppCollectionService.deleteByUserInfoAndAppId(userAppCollectionDto.getUserInfo(),
                userAppCollectionDto.getAppId());
        return Response.success(ResponseCode.OK);
    }

    /**
     * 通过用户信息获取应用收藏列表。
     *
     * @param userInfo 表示用户信息的 {@link String}。
     * @return 表示收藏应用列表的 {@link Response}{@code <}{@link List}{@code <}{@link UserAppCollectionPo}{@code >>}。
     */
    @GetMapping("/collection/{userInfo}")
    public Response<List<UserAppCollectionPo>> getCollectionsByUserInfo(@PathVariable("userInfo") String userInfo) {
        return Response.success(this.userAppCollectionService.getCollectionsByUserInfo(userInfo), ResponseCode.OK);
    }

    /**
     * 通过用户信息获取应用收藏列表详细信息。
     *
     * @param userInfo 表示用户信息的 {@link String}。
     * @return 表示收藏应用列表详细信息的 {@link Response}{@code <}{@link CollectionAppInfoDto}{@code >}。
     */
    @GetMapping("/collection/app/{userInfo}")
    public Response<CollectionAppInfoDto> getAppCollectionInfoByUserInfo(@PathVariable("userInfo") String userInfo) {
        return Response.success(this.userAppCollectionService.getAppInfoByUserInfo(userInfo), ResponseCode.OK);
    }

    /**
     * 获取应用收藏用户数量。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @return 表示用户收藏数量的 {@link Response}{@code <}{@link Integer}{@code >}。
     */
    @GetMapping("/collection/count/{appId}")
    public Response<Integer> getCollectionUserCntByAppId(@PathVariable("appId") String appId) {
        return Response.success(this.userAppCollectionService.getCollectionUserCntByAppId(appId), ResponseCode.OK);
    }
}
