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
 * 用户信息服务接口实现。
 *
 * @author 陈潇文
 * @since 2024-05-30
 */
@Component
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoMapper userInfoMapper;

    public UserInfoServiceImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    public Long createUserInfo(UserInfoDto userInfoDto) {
        userInfoMapper.insert(userInfoDto);
        return userInfoDto.getId();
    }

    @Override
    public void updateUserInfo(UserInfoDto userInfoDto) {
        userInfoMapper.update(userInfoDto);
    }

    @Override
    public UserInfoDto getUserInfo(String userName) {
        return userInfoMapper.get(userName);
    }
}
