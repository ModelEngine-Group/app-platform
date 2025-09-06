/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service.impl;

import modelengine.fitframework.annotation.Component;
import modelengine.jade.app.engine.base.dto.UserInfoDto;
import modelengine.jade.app.engine.base.service.UserInfoService;
import modelengine.jade.app.engine.base.mapper.UserInfoMapper;

/**
 * 用户信息服务接口实现
 *
 * @since 2024-5-30
 *
 */
@Component
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoMapper userInfoMapper;

    public UserInfoServiceImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    /**
     * 插入用户信息
     *
     * @param userInfoDto 用户信息消息体
     * @return 用户信息 id
     */
    @Override
    public Long createUserInfo(UserInfoDto userInfoDto) {
        userInfoMapper.insert(userInfoDto);
        return userInfoDto.getId();
    }

    /**
     * 更新用户信息
     *
     * @param userInfoDto 用户信息
     */
    @Override
    public void updateUserInfo(UserInfoDto userInfoDto) {
        userInfoMapper.update(userInfoDto);
    }

    /**
     * 查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    @Override
    public UserInfoDto getUserInfo(String userName) {
        return userInfoMapper.get(userName);
    }
}
