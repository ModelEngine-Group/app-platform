/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.common.entity;

/**
 * 操作人相关上下文。
 *
 * @author 陈镕希
 * @since 2023-12-15
 */
public class OperationContext {
    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 操作人.
     */
    private String operator;

    /**
     * 全局UserId.
     */
    private String globalUserId;

    /**
     * 账号.
     */
    private String account;

    /**
     * 工号.
     */
    private String employeeNumber;

    /**
     * 姓名.
     */
    private String name;

    /**
     * 操作人ip信息
     */
    private String operatorIp;

    /**
     * 调用来源平台信息
     */
    private String sourcePlatform;

    private String language;

    /**
     * 无参构造函数
     */
    public OperationContext() {
    }

    public OperationContext(String tenantId, String operator, String globalUserId, String account,
            String employeeNumber, String name, String operatorIp, String sourcePlatform, String language) {
        this.tenantId = tenantId;
        this.operator = operator;
        this.globalUserId = globalUserId;
        this.account = account;
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.operatorIp = operatorIp;
        this.sourcePlatform = sourcePlatform;
        this.language = language;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getGlobalUserId() {
        return globalUserId;
    }

    public void setGlobalUserId(String globalUserId) {
        this.globalUserId = globalUserId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperatorIp() {
        return operatorIp;
    }

    public void setOperatorIp(String operatorIp) {
        this.operatorIp = operatorIp;
    }

    public String getSourcePlatform() {
        return sourcePlatform;
    }

    public void setSourcePlatform(String sourcePlatform) {
        this.sourcePlatform = sourcePlatform;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}