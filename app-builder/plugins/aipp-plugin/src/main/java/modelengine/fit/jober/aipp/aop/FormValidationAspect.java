/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jober.aipp.aop;

import modelengine.fit.jade.aipp.domain.division.service.DomainDivisionService;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.service.AppBuilderFormService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.aop.annotation.Before;

/**
 * 表单校验切面
 *
 * @author 邬涨财
 * @since 2025-08-26
 */
@Aspect
@Component
public class FormValidationAspect extends ValidationAspect {
    private final AppBuilderFormService appBuilderFormService;

    public FormValidationAspect(DomainDivisionService domainDivisionService,
            AppBuilderFormService appBuilderFormService,
            @Value("${domain-division.isEnable}") boolean isEnableDomainDivision) {
        super(domainDivisionService, isEnableDomainDivision);
        this.appBuilderFormService = appBuilderFormService;
    }

    @Before("@annotation(modelengine.fit.jober.aipp.aop.FormValidation)")
    public void appValidation(JoinPoint joinPoint) {
        this.validate(joinPoint, "formId");
    }

    @Override
    protected void throwNoPermissionException() {
        throw new AippException(AippErrCode.NO_PERMISSION_OPERATE_FORM);
    }

    @Override
    protected String getUserGroupId(String id) {
        AppBuilderForm appBuilderForm = this.appBuilderFormService.selectWithId(id);
        if (appBuilderForm == null) {
            throw new AippException(AippErrCode.FORM_NOT_EXIST);
        }
        return appBuilderForm.getUserGroupId();
    }
}
