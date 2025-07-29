/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service;

import modelengine.jade.app.engine.base.dto.UserInfoDto;

/**
 * 用户信息接口
 *
 * @since 2024-5-30
 *
 */
public interface UserInfoService {
    /**
     * 插入用户信息
     *
     * @param userInfoDto 用户信息消息体
     * @return 用户信息 id
     */
    Long createUserInfo(UserInfoDto userInfoDto);

    /**
     * 更新用户信息
     *
     * @param userInfoDto 用户信息
     */
    void updateUserInfo(UserInfoDto userInfoDto);

    /**
     * 查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    UserInfoDto getUserInfo(String userName);
}
