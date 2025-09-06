/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

/**
 * 用户详情结构体。
 *
 * @author 陈镕希
 * @since 2023-10-07
 */
public class EmployeeDetailVO {
    private String employeeNumber;

    private String uid;

    private String uuid;

    private String fullName;

    private String lastName;

    private String englishName;

    private String aduCn;

    private String globalUserId;

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getAduCn() {
        return aduCn;
    }

    public void setAduCn(String aduCn) {
        this.aduCn = aduCn;
    }

    public String getGlobalUserId() {
        return globalUserId;
    }

    public void setGlobalUserId(String globalUserId) {
        this.globalUserId = globalUserId;
    }
}
