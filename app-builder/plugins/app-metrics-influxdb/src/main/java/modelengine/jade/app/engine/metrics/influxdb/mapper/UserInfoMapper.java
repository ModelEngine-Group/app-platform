/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.mapper;

import modelengine.jade.app.engine.metrics.influxdb.UserDepartmentInfo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户信息查询。
 *
 * @author 高嘉乐
 * @since 2024-12-18
 */
@Mapper
public interface UserInfoMapper {
    /**
     * 获取用户部门信息。
     *
     * @param name 表示用户名的 {@link String}。
     * @return 用户部门信息 {@link UserDepartmentInfo}。
     */
    UserDepartmentInfo getUserDepartmentInfo(@Param("name") String name);
}
