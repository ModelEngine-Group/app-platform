/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.service.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.jade.app.engine.metrics.influxdb.UserDepartmentInfo;
import modelengine.jade.app.engine.metrics.influxdb.mapper.UserInfoMapper;
import modelengine.jade.app.engine.metrics.influxdb.service.UserInfoService;

import modelengine.fitframework.annotation.Component;

/**
 * 获取用户信息的服务。
 *
 * @author 高嘉乐
 * @since 2025-01-02
 */
@Component
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoMapper userInfoMapper;

    public UserInfoServiceImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    public UserDepartmentInfo getUserDepartmentInfoByName(String username) {
        notBlank(username, "User name cannot be null or blank. [username={0}]", username);
        return this.userInfoMapper.getUserDepartmentInfo(username);
    }
}
