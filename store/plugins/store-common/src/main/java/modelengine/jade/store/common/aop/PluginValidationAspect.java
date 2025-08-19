/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.store.common.aop;

import modelengine.fit.jade.aipp.domain.division.service.DomainDivisionService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.aop.annotation.Before;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.code.PluginRetCode;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.service.PluginService;

import java.util.Collections;

/**
 * 插件校验切面
 *
 * @author 邬涨财
 * @since 2025-08-26
 */
@Aspect(scope = Scope.GLOBAL)
@Component
public class PluginValidationAspect extends ValidationAspect {
    private final PluginService pluginService;
    private final DomainDivisionService domainDivisionService;

    public PluginValidationAspect(DomainDivisionService domainDivisionService,
            @Value("${domain-division.isEnable}") boolean isEnableDomainDivision,
            PluginService pluginService) {
        super(isEnableDomainDivision);
        this.domainDivisionService = domainDivisionService;
        this.pluginService= pluginService;
    }

    @Before("@annotation(modelengine.jade.store.entity.aop.PluginValidation)")
    public void pluginValidation(JoinPoint joinPoint) {
        this.validate(joinPoint, "pluginId");
    }

    @Override
    protected void validate(Object value) {
        String appUserGroupId = this.getUserGroupId(value);
        if (StringUtils.isEmpty(appUserGroupId)) {
            return;
        }
        if (!this.domainDivisionService.validate(Collections.singletonList(appUserGroupId))) {
            throw new ModelEngineException(PluginRetCode.NO_PERMISSION_OPERATE_PLUGIN);
        }
    }

    private String getUserGroupId(Object value) {
        PluginData plugin = this.pluginService.getPlugin(String.valueOf(value));
        if (plugin == null) {
            return null;
        }
        return plugin.getUserGroupId();
    }
}
