/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.controller;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PatchMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.app.engine.base.common.resp.Response;
import modelengine.jade.app.engine.base.common.resp.ResponseCode;
import modelengine.jade.app.engine.base.dto.UserInfoDto;
import modelengine.jade.app.engine.base.service.UserInfoService;

/**
 * 用户信息相关控制器
 *
 * @since 2024-5-30
 *
 */
@Component
@RequestMapping("/aipp/user")
public class UserInfoController {
    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    /**
     * 创建用户信息记录
     *
     * @param userInfoDto 用户信息消息体
     * @return 用户信息 id
     */
    @PostMapping("/info")
    public Response<Long> createUserAppCollection(@RequestBody UserInfoDto userInfoDto) {
        Long userInfoId = this.userInfoService.createUserInfo(userInfoDto);
        return Response.success(userInfoId, ResponseCode.OK);
    }

    /**
     * 修改用户信息
     *
     * @param userInfoDto 用户信息消息体
     * @return 响应信息
     */
    @PatchMapping("/info")
    public Response<Void> updateUserAppCollection(@RequestBody UserInfoDto userInfoDto) {
        this.userInfoService.updateUserInfo(userInfoDto);
        return Response.success(ResponseCode.OK);
    }

    /**
     * 获取用户信息记录
     *
     * @param userName 用户名
     * @return 用户信息
     */
    @GetMapping("/info/{userName}")
    public Response<UserInfoDto> createUserAppCollection(@PathVariable("userName") String userName) {
        return Response.success(this.userInfoService.getUserInfo(userName), ResponseCode.OK);
    }
}
