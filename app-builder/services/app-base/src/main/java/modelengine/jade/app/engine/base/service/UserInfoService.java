/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service;

import modelengine.jade.app.engine.base.dto.UserInfoDto;

/**
 * 用户信息接口。
 *
 * @author 陈潇文
 * @since 2024-05-30
 */
public interface UserInfoService {
    /**
     * 插入用户信息。
     *
     * @param userInfoDto 表示用户信息消息体的 {@link UserInfoDto}。
     * @return 表示用户信息唯一标识的 {@link Long}。
     */
    Long createUserInfo(UserInfoDto userInfoDto);

    /**
     * 更新用户信息。
     *
     * @param userInfoDto 表示用户信息的 {@link UserInfoDto}。
     */
    void updateUserInfo(UserInfoDto userInfoDto);

    /**
     * 查询用户信息。
     *
     * @param userName 表示用户名的 {@link String}。
     * @return 表示用户信息的 {@link String}。
     */
    UserInfoDto getUserInfo(String userName);
}
