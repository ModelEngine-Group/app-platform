/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.vo;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.app.engine.metrics.influxdb.bo.DepartmentBo;

import lombok.Builder;
import lombok.Data;

/**
 * 部门访问统计信息视图。
 *
 * @author 高嘉乐
 * @since 2024-12-10
 */
@Data
@Builder
public class DepartmentAccessVo {
    private String departmentName;
    private int accessCount;

    /**
     * 从部门访问信息业务对象的转换方法。
     *
     * @param departmentBo 表示部门访问信息业务对象的 {@link DepartmentBo}。
     * @return 表示部门访问信息视图的 {@link DepartmentAccessVo}。
     */
    public static DepartmentAccessVo from(DepartmentBo departmentBo) {
        notNull(departmentBo, "Department bo cannot be null.");
        return DepartmentAccessVo.builder()
                .departmentName(departmentBo.getDepartmentName())
                .accessCount(Math.toIntExact(departmentBo.getCount()))
                .build();
    }
}