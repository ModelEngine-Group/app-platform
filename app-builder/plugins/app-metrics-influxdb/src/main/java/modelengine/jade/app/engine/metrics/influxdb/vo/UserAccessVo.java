/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.vo;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.app.engine.metrics.influxdb.bo.UserBo;

import lombok.Builder;
import lombok.Data;

/**
 * 用户访问统计信息视图。
 *
 * @author 高嘉乐
 * @since 2024-12-10
 */
@Data
@Builder
public class UserAccessVo {
    private String userName;
    private int accessCount;

    /**
     * 从单个用户请求统计业务对象的转换方法。
     *
     * @param userBo 表示单个用户请求统计业务对象的 {@link UserBo}。
     * @return 表示用户访问信息视图的 {@link UserAccessVo}。
     */
    public static UserAccessVo from(UserBo userBo) {
        notNull(userBo, "UserBo cannot be null.");
        return UserAccessVo.builder()
                .userName(userBo.getUserName())
                .accessCount(Math.toIntExact(userBo.getCount()))
                .build();
    }
}