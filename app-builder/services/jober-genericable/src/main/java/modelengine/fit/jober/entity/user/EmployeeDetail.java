/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity.user;

import java.util.Objects;

/**
 * 用户详情结构体。
 *
 * @author 陈镕希
 * @since 2023-11-13
 */
public class EmployeeDetail {
    private String employeeNumber;

    private String uid;

    private String uuid;

    /**
     * e.g. 张三
     */
    private String fullName;

    /**
     * e.g. 张三
     */
    private String lastName;

    /**
     * e.g. Zhang San
     */
    private String englishName;

    private String aduCn;

    private String globalUserId;

    /**
     * EmployeeDetail
     */
    public EmployeeDetail() {
    }

    public EmployeeDetail(String employeeNumber, String uid, String uuid, String fullName, String lastName,
            String englishName, String aduCn, String globalUserId) {
        this.employeeNumber = employeeNumber;
        this.uid = uid;
        this.uuid = uuid;
        this.fullName = fullName;
        this.lastName = lastName;
        this.englishName = englishName;
        this.aduCn = aduCn;
        this.globalUserId = globalUserId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmployeeDetail that = (EmployeeDetail) o;
        return Objects.equals(employeeNumber, that.employeeNumber) && Objects.equals(uid, that.uid) && Objects.equals(
                uuid, that.uuid) && Objects.equals(fullName, that.fullName) && Objects.equals(lastName, that.lastName)
                && Objects.equals(englishName, that.englishName) && Objects.equals(aduCn, that.aduCn) && Objects.equals(
                globalUserId, that.globalUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeNumber, uid, uuid, fullName, lastName, englishName, aduCn, globalUserId);
    }
}
