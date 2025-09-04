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
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.code.PluginRetCode;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.support.DeployStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 部署插件校验切面
 *
 * @author 邬涨财
 * @since 2025-08-26
 */
@Aspect(scope = Scope.GLOBAL)
@Component
public class DeployPluginValidationAspect extends ValidationAspect {
    private final PluginService pluginService;
    private final DomainDivisionService domainDivisionService;

    public DeployPluginValidationAspect(DomainDivisionService domainDivisionService,
            @Value("${domain-division.isEnable}") boolean isEnableDomainDivision, PluginService pluginService) {
        super(isEnableDomainDivision);
        this.pluginService = pluginService;
        this.domainDivisionService = domainDivisionService;
    }

    @Before("@annotation(modelengine.jade.store.entity.aop.DeployPluginValidation)")
    public void pluginValidation(JoinPoint joinPoint) {
        this.validate(joinPoint, "toDeployPluginIds");
    }

    /**
     * 部署校验规则：
     * （1）卸载的已部署的插件，必须是符合用户组规则
     * （2）新增的部署插件，必须是符合用户组规则
     *
     * @param value 表示需要校验的属性的 {@link Object}。
     */
    @Override
    protected void validate(Object value) {
        List<String> toDeployPluginIds = ObjectUtils.cast(value);
        List<PluginData> deployedPlugins = this.pluginService.getPlugins(DeployStatus.DEPLOYED);
        Map<String, String> deployedPluginIdMapping = deployedPlugins.stream()
                .filter(pluginData -> !pluginData.getBuiltin())
                .collect(Collectors.toMap(PluginData::getPluginId, PluginData::getUserGroupId));
        List<String> toUnDeployedPluginIds =
                new ArrayList<>(CollectionUtils.difference(deployedPluginIdMapping.keySet(), toDeployPluginIds));
        this.validate(toUnDeployedPluginIds, deployedPluginIdMapping);
        List<PluginData> deployablePlugins = this.getDeployablePlugins();
        Map<String, String> deployablePluginIdMapping = deployablePlugins.stream()
                .filter(pluginData -> !pluginData.getBuiltin())
                .collect(Collectors.toMap(PluginData::getPluginId, PluginData::getUserGroupId));
        List<String> newDeployedPluginIds =
                new ArrayList<>(CollectionUtils.difference(toDeployPluginIds, deployedPluginIdMapping.keySet()));
        this.validate(newDeployedPluginIds, deployablePluginIdMapping);
    }

    private List<PluginData> getDeployablePlugins() {
        List<PluginData> unDeployedPlugins = this.pluginService.getPlugins(DeployStatus.UNDEPLOYED);
        List<PluginData> failedPlugins = this.pluginService.getPlugins(DeployStatus.DEPLOYMENT_FAILED);
        return CollectionUtils.connect(unDeployedPlugins, failedPlugins);
    }

    private void validate(List<String> pluginIds, Map<String, String> pluginIdUserGroupMapping) {
        List<String> userGroupIds = pluginIds.stream()
                .map(key -> pluginIdUserGroupMapping.getOrDefault(key, null))
                .filter(Objects::nonNull)
                .toList();
        if (!this.domainDivisionService.validate(userGroupIds)) {
            throw new ModelEngineException(PluginRetCode.NO_PERMISSION_DEPLOY_PLUGIN);
        }
    }
}
