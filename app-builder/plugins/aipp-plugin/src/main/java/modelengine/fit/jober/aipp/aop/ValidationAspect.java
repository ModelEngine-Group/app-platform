/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jober.aipp.aop;

import modelengine.fit.jade.aipp.domain.division.service.DomainDivisionService;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.aop.MethodSignature;

import java.util.Collections;

/**
 * 校验切面
 *
 * @author 邬涨财
 * @since 2025-08-26
 */
public abstract class ValidationAspect {
    private final DomainDivisionService domainDivisionService;
    private final boolean isEnableDomainDivision;

    public ValidationAspect(DomainDivisionService domainDivisionService,
            @Value("${domain-division.isEnable}") boolean isEnableDomainDivision) {
        this.domainDivisionService = domainDivisionService;
        this.isEnableDomainDivision = isEnableDomainDivision;
    }

    /**
     * 资源校验
     *
     * @param joinPoint 表示连接点的 {@link JoinPoint}。
     * @param idKey 表示变量键的 {@link String}。
     */
    protected void validate(JoinPoint joinPoint, String idKey) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        Object id = null;
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(idKey)) {
                id = paramValues[i];
                break;
            }
        }
        if (id == null) {
            return;
        }
        if (!this.isEnableDomainDivision) {
            return;
        }
        String appUserGroupId = this.getUserGroupId(String.valueOf(id));
        if (!this.domainDivisionService.validate(Collections.singletonList(appUserGroupId))) {
            throwNoPermissionException();
        }
    }

    /**
     * 抛出没有权限的异常。
     */
    protected abstract void throwNoPermissionException();

    /**
     * 获取资源的用户组唯一标识。
     *
     * @param id 表示资源唯一标识的 {@link String}。
     * @return 表示用户组唯一标识的 {@link String}
     */
    protected abstract String getUserGroupId(String id);
}
