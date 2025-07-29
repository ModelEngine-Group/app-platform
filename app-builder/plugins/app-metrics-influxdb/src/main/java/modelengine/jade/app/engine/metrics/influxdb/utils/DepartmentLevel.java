/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.utils;

import lombok.Getter;

/**
 * 部门级别枚举类。
 *
 * @author 高嘉乐
 * @since 2025-01-09
 */
public enum DepartmentLevel {
    /** 表示一级部门。 */
    DEP_LEVEL_1("l1_name"),

    /** 表示二级部门。 */
    DEP_LEVEL_2("l2_name"),

    /** 表示三级部门。 */
    DEP_LEVEL_3("l3_name"),

    /** 表示四级部门。 */
    DEP_LEVEL_4("l4_name"),

    /** 表示五级部门。 */
    DEP_LEVEL_5("l5_name"),

    /** 表示六级部门。 */
    DEP_LEVEL_6("l6_name"),

    /** 表示非法部门。 */
    DEP_INVALID("invalid");

    @Getter
    private final String levelName;

    DepartmentLevel(String levelName) {
        this.levelName = levelName;
    }

    /**
     * 根据字符串获取当前部门级别枚举
     *
     * @param departmentLevelName 表示部门级别的 {@link String}
     * @return 表示部门级别的枚举 {@link DepartmentLevel}
     */
    public static DepartmentLevel getLevel(String departmentLevelName) {
        for (DepartmentLevel level : values()) {
            if (level.levelName.equals(departmentLevelName)) {
                return level;
            }
        }
        return DEP_INVALID;
    }
}
