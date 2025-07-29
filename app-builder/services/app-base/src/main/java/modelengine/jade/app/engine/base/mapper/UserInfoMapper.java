/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.mapper;

import modelengine.jade.app.engine.base.dto.UserInfoDto;

/**
 * 用户信息相关映射
 *
 * @since 2024-5-30
 *
 */
public interface UserInfoMapper {
    /**
     * 插入用户信息
     *
     * @param userInfoDto 用户信息消息体
     */
    void insert(UserInfoDto userInfoDto);

    /**
     * 更新用户信息
     *
     * @param userInfoDto 用户信息消息体
     */
    void update(UserInfoDto userInfoDto);

    /**
     * 查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息消息体
     */
    UserInfoDto get(String userName);

    /**
     * 将所有当前默认应用为 appId 的用户重置为 defaultApp
     *
     * @param appId 应用 id
     * @param defaultApp 要重置为的应用 id
     */
    void resetDefaultAppByAppId(String appId, String defaultApp);
}
