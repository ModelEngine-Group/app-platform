/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */
package modelengine.fit.jade.aipp.domain.division.code;

import modelengine.jade.common.code.RetCode;

/**
 * 分域错误码
 *
 * @author 邬涨财
 * @since 2025-08-14
 */
public enum DomainDivisionRetCode implements RetCode {
    /**
     * 用户组不存在
     */
    USER_GROUP_NOT_EXIST(131200001,
            "The current user does not belong to any resource group and has no permission to access the resource."),
    /**
     * 用户名不存在
     */
    USER_NAME_NOT_EXIST(131200002,"The current user not exist and has no permission to access the resource."),

    /**
     * 用户组服务调用失败
     */
    USER_GROUP_EXCHANGE_ERROR(131200003,"Unable to exchange with user group service.");

    private final int code;

    private final String msg;

    DomainDivisionRetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
