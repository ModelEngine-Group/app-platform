/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.vo;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.app.engine.metrics.influxdb.bo.UserAccessTrendBo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 用户访问趋势视图。
 *
 * @author 高嘉乐
 * @since 2024-12-10
 */
@Data
@Builder
public class UserAccessTrendVo {
    private LocalDateTime time;
    private int count;

    /**
     * 从用户访问趋势业务对象的转换方法。
     *
     * @param userAccessTrendBo 表示用户访问趋势业务对象的 {@link UserAccessTrendBo}。
     * @return 表示用户访问趋势视图的 {@link DepartmentAccessVo}。
     */
    public static UserAccessTrendVo from(UserAccessTrendBo userAccessTrendBo) {
        notNull(userAccessTrendBo, "UserAccessTrendBo cannot be null.");
        return UserAccessTrendVo.builder()
                .count(Math.toIntExact(userAccessTrendBo.getCount()))
                .time(userAccessTrendBo.getTime().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }
}