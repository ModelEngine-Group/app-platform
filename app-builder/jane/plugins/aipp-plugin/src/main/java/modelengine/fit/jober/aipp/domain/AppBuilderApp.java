/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import modelengine.fitframework.inspection.Validation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * app 领域对象
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Slf4j
public class AppBuilderApp extends BaseDomain {
    private String id;
    private String name;
    private String appId;
    private String appSuiteId;
    private String tenantId;
    private String configId;
    private String flowGraphId;
    private String type;
    private String version;
    private Map<String, Object> attributes;
    private String path;
    private String state;
    private String appType;
    private String appBuiltType;
    private String appCategory;
    private Boolean isActive;
    private String status;
    private String uniqueName;
    private LocalDateTime publishAt;
    private AppBuilderFlowGraph flowGraph;
    private AppBuilderConfig config;
    private List<AppBuilderFormProperty> formProperties;
    private AppBuilderFlowGraphRepository flowGraphRepository;
    private AppBuilderConfigRepository configRepository;
    private AppBuilderConfigPropertyRepository configPropertyRepository;
    private AppBuilderFormRepository formRepository;
    private AppBuilderFormPropertyRepository formPropertyRepository;

    public AppBuilderApp(AppBuilderFlowGraphRepository flowGraphRepository, AppBuilderConfigRepository configRepository,
            AppBuilderFormRepository formRepository, AppBuilderConfigPropertyRepository configPropertyRepository,
            AppBuilderFormPropertyRepository formPropertyRepository) {
        this.flowGraphRepository = flowGraphRepository;
        this.configRepository = configRepository;
        this.formRepository = formRepository;
        this.configPropertyRepository = configPropertyRepository;
        this.formPropertyRepository = formPropertyRepository;
    }

    public AppBuilderFlowGraph getFlowGraph() {
        return lazyGet(this.flowGraph, this::loadFlowGraph, this::setFlowGraph);
    }

    private AppBuilderFlowGraph loadFlowGraph() {
        Validation.notNull(this.flowGraphId, "App builder config can not be null.");
        return this.flowGraphRepository.selectWithId(this.flowGraphId);
    }

    public AppBuilderConfig getConfig() {
        return lazyGet(this.config, this::loadConfig, this::setConfig);
    }

    private AppBuilderConfig loadConfig() {
        Validation.notNull(this.configId, "App builder config can not be null.");
        AppBuilderConfig appBuilderConfig = this.configRepository.selectWithId(this.configId);
        Validation.notNull(appBuilderConfig, "App builder config can not be null.");
        appBuilderConfig.setFormRepository(this.formRepository);
        appBuilderConfig.setFormPropertyRepository(this.formPropertyRepository);
        appBuilderConfig.setConfigPropertyRepository(this.configPropertyRepository);
        return appBuilderConfig;
    }

    /**
     * 获取表单配置项集合
     *
     * @return 表示表单配置项集合的 {@link List}{@code <}{@link AppBuilderFormProperty}{@code >}。
     */
    public List<AppBuilderFormProperty> getFormProperties() {
        return lazyGet(this.formProperties, this::loadFormProperties, this::setFormProperties);
    }

    private List<AppBuilderFormProperty> loadFormProperties() {
        return this.formPropertyRepository.selectWithAppId(this.id);
    }

    /**
     * app是否已经发布.
     *
     * @return true/false, true表示已发布; 否则, 未发布.
     */
    public boolean isPublished() {
        return AppState.PUBLISHED.getName().equals(this.state);
    }
}
