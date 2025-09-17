/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.mapper;

import modelengine.jade.app.engine.base.dto.UserInfoDto;

/**
 * 用户信息相关映射。
 *
 * @author 陈潇文
 * @since 2024-05-30
 */
public interface UserInfoMapper {
    /**
     * 插入用户信息。
     *
     * @param userInfoDto 表示用户信息消息体的 {@link UserInfoDto}。
     */
    void insert(UserInfoDto userInfoDto);

    /**
     * 更新用户信息。
     *
     * @param userInfoDto 表示用户信息消息体的 {@link UserInfoDto}。
     */
    void update(UserInfoDto userInfoDto);

    /**
     * 查询用户信息。
     *
     * @param userName 表示用户名的 {@link String}。
     * @return 表示用户信息消息体的 {@link UserInfoDto}。
     */
    UserInfoDto get(String userName);

    /**
     * 将所有当前默认应用为 appId 的用户重置为 defaultApp。
     *
     * @param appId 表示应用的唯一标识的 {@link String}。
     * @param defaultApp 表示要重置的默认应用的 {@link String}。
     */
    void resetDefaultAppByAppId(String appId, String defaultApp);
}
