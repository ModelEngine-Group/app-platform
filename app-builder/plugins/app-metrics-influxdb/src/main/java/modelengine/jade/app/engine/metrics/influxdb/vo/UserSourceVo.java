/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.vo;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.app.engine.metrics.influxdb.bo.UserSourceBo;

import lombok.Builder;
import lombok.Data;

/**
 * 用户来源分布视图。
 *
 * @author 高嘉乐
 * @since 2024-12-10
 */
@Data
@Builder
public class UserSourceVo {
    private String departmentName;
    private int userCount;

    /**
     * 从用户来源信息业务对象的转换方法。
     *
     * @param userSourceBo 表示用户来源信息业务对象的 {@link UserSourceBo}。
     * @return 表示用户来源信息视图的 {@link UserSourceVo}。
     */
    public static UserSourceVo from(UserSourceBo userSourceBo) {
        notNull(userSourceBo, "UserSourceBo cannot be null.");
        return UserSourceVo.builder()
                .departmentName(userSourceBo.getDepartmentName())
                .userCount(Math.toIntExact(userSourceBo.getCount()))
                .build();
    }
}