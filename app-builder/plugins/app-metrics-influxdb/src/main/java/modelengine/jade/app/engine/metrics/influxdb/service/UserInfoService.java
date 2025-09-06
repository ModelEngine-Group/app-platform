/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.service;

import modelengine.jade.app.engine.metrics.influxdb.UserDepartmentInfo;

/**
 * 获取用户信息的服务。
 *
 * @author 高嘉乐
 * @since 2025-01-02
 */
public interface UserInfoService {
    /**
     * 根据用户名获取用户部门信息。
     *
     * @param username 表示用户名的 {@link String}。
     * @return 表示用户部门信息的 {@link UserDepartmentInfo}。
     */
    UserDepartmentInfo getUserDepartmentInfoByName(String username);
}
