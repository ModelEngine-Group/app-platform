/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jober.aipp.aop;

import modelengine.fit.jade.aipp.domain.division.service.DomainDivisionService;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.aop.annotation.Before;

import java.util.Optional;

/**
 * 应用校验切面
 *
 * @author 邬涨财
 * @since 2025-08-25
 */
@Aspect
@Component
public class AppValidationAspect extends ValidationAspect {
    private final AppVersionService appVersionService;

    public AppValidationAspect(AppVersionService appVersionService,
            DomainDivisionService domainDivisionService,
            @Value("${domain-division.isEnable}") boolean isEnableDomainDivision) {
        super(domainDivisionService, isEnableDomainDivision);
        this.appVersionService = appVersionService;
    }

    @Before("@annotation(modelengine.fit.jober.aipp.aop.AppValidation)")
    public void appValidation(JoinPoint joinPoint) {
        this.validate(joinPoint, "appId");
    }

    @Override
    protected String getUserGroupId(String id) {
        Optional<AppVersion> appVersionOptional = this.appVersionService.getByAppId(String.valueOf(id));
        if (appVersionOptional.isEmpty()) {
            throw new AippException(AippErrCode.APP_NOT_FOUND);
        }
        AppVersion appVersion = appVersionOptional.get();
        return appVersion.getData().getUserGroupId();
    }

    @Override
    protected void throwNoPermissionException() {
        throw new AippException(AippErrCode.NO_PERMISSION_OPERATE_APP);
    }
}
