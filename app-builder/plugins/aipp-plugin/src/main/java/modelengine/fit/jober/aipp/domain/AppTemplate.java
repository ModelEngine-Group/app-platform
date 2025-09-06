/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import modelengine.fitframework.inspection.Validation;

import java.util.List;
import java.util.Map;

/**
 * 应用模板的领域类。
 *
 * @author 方誉州
 * @since 2024-12-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AppTemplate extends BaseDomain {
    private String id;
    private String name;
    private String builtType;
    private String category;
    private Map<String, Object> attributes;
    private String appType;
    private long like;
    private long collection;
    private long usage;
    private String version;
    private String configId;
    private String flowGraphId;
    private AppBuilderFlowGraph flowGraph;
    private AppBuilderConfig config;
    private List<AppBuilderFormProperty> formProperties;
    private AppBuilderFlowGraphRepository flowGraphRepository;
    private AppBuilderConfigRepository configRepository;
    private AppBuilderConfigPropertyRepository configPropertyRepository;
    private AppBuilderFormRepository formRepository;
    private AppBuilderFormPropertyRepository formPropertyRepository;

    public AppTemplate(AppBuilderFlowGraphRepository flowGraphRepository, AppBuilderConfigRepository configRepository,
            AppBuilderFormRepository formRepository, AppBuilderConfigPropertyRepository configPropertyRepository,
            AppBuilderFormPropertyRepository formPropertyRepository) {
        this.flowGraphRepository = flowGraphRepository;
        this.configRepository = configRepository;
        this.formRepository = formRepository;
        this.configPropertyRepository = configPropertyRepository;
        this.formPropertyRepository = formPropertyRepository;
    }

    /**
     * 获取流程图。
     *
     * @return 表示流程图的 {@link AppBuilderFlowGraph}。
     */
    public AppBuilderFlowGraph getFlowGraph() {
        return lazyGet(this.flowGraph, this::loadFlowGraph, this::setFlowGraph);
    }

    private AppBuilderFlowGraph loadFlowGraph() {
        Validation.notNull(this.flowGraphId, "App builder config can not be null.");
        return this.flowGraphRepository.selectWithId(this.flowGraphId);
    }

    /**
     * 获取应用模板的配置。
     *
     * @return 表示应用配置的 {@link AppBuilderConfig}。
     */
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
     * 获取表单配置项集合。
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
     * 获取 icon.
     *
     * @return {@link String} icon路径.
     */
    public String getIcon() {
        return cast(this.attributes.get("icon"));
    }

    /**
     * 设置icon.
     *
     * @param icon 图标.
     */
    public void setIcon(String icon) {
        this.attributes.put("icon", icon);
    }

    /**
     * 获取描述.
     *
     * @return {@link String} 描述信息.
     */
    public String getDescription() {
        return cast(this.attributes.get("description"));
    }

    /**
     * 设置描述信息.
     *
     * @param description 描述信息.
     */
    public void setDescription(String description) {
        this.attributes.put("description", description);
    }
}
