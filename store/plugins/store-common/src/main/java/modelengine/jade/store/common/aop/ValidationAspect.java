/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.store.common.aop;

import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.aop.MethodSignature;

/**
 * 校验切面
 *
 * @author 邬涨财
 * @since 2025-08-26
 */
public abstract class ValidationAspect {
    private final boolean isEnableDomainDivision;

    public ValidationAspect(@Value("${domain-division.isEnable}") boolean isEnableDomainDivision) {
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
        Object value = null;
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(idKey)) {
                value = paramValues[i];
                break;
            }
        }
        if (value == null) {
            return;
        }
        if (!this.isEnableDomainDivision) {
            return;
        }
        this.validate(value);
    }

    /**
     * 资源校验
     *
     * @param value 表示需要校验的资源的 {@link Object}。
     */
    protected abstract void validate(Object value);
}
