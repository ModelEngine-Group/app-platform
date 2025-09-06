/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

/**
 * 操作人相关上下文。
 *
 * @author 陈镕希
 * @since 2023-08-28
 */
public class OperationContext {
    private String tenantId;

    private String operator;

    private String operatorIp;

    private String sourcePlatform;

    private String language;

    /**
     * OperationContext
     */
    public OperationContext() {
    }

    public OperationContext(String tenantId, String operator, String operatorIp, String sourcePlatform,
            String language) {
        this.tenantId = tenantId;
        this.operator = operator;
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