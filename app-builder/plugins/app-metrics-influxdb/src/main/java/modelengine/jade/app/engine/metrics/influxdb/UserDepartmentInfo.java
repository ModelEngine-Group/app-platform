/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb;

/**
 * 用户部门信息。
 *
 * @author 高嘉乐
 * @since 2025-01-02
 */
public class UserDepartmentInfo {
    /**
     * 用户名
     */
    private String name;

    /**
     * 用户一级部门
     */
    private String depName1 = "NA";

    /**
     * 用户二级部门
     */
    private String depName2 = "NA";

    /**
     * 用户三级部门
     */
    private String depName3 = "NA";

    /**
     * 用户四级部门
     */
    private String depName4 = "NA";

    /**
     * 用户五级部门
     */
    private String depName5 = "NA";

    /**
     * 用户六级部门
     */
    private String depName6 = "NA";

    public UserDepartmentInfo() {

    }

    public UserDepartmentInfo(String name, String depName1, String depName2, String depName3, String depName4,
            String depName5, String depName6) {
        this.name = name;
        this.depName1 = depName1;
        this.depName2 = depName2;
        this.depName3 = depName3;
        this.depName4 = depName4;
        this.depName5 = depName5;
        this.depName6 = depName6;
    }

    public String getName() {
        return name;
    }

    public String getDepName1() {
        return depName1;
    }

    public String getDepName2() {
        return depName2;
    }

    public String getDepName3() {
        return depName3;
    }

    public String getDepName4() {
        return depName4;
    }

    public String getDepName5() {
        return depName5;
    }

    public String getDepName6() {
        return depName6;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepName1(String depName1) {
        this.depName1 = depName1;
    }

    public void setDepName2(String depName2) {
        this.depName2 = depName2;
    }

    public void setDepName3(String depName3) {
        this.depName3 = depName3;
    }

    public void setDepName4(String depName4) {
        this.depName4 = depName4;
    }

    public void setDepName5(String depName5) {
        this.depName5 = depName5;
    }

    public void setDepName6(String depName6) {
        this.depName6 = depName6;
    }
}
