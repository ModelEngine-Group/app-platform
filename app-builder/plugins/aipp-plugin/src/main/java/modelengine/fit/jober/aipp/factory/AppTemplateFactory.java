/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.factory;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.repository.AppTemplateRepository;

import modelengine.fitframework.annotation.Component;

import java.util.Collections;
import java.util.List;

/**
 * 应用模板的构造工厂。
 *
 * @author 方誉州
 * @since 2024-12-31
 */
@Component
public class AppTemplateFactory {
    private final AppBuilderFlowGraphRepository flowGraphRepository;
    private final AppBuilderConfigRepository configRepository;
    private final AppBuilderFormRepository formRepository;
    private final AppBuilderConfigPropertyRepository configPropertyRepository;
    private final AppBuilderFormPropertyRepository formPropertyRepository;
    private final AppTemplateRepository appTemplateRepository;

    public AppTemplateFactory(AppBuilderFlowGraphRepository flowGraphRepository,
            AppBuilderConfigRepository configRepository, AppBuilderFormRepository formRepository,
            AppBuilderConfigPropertyRepository configPropertyRepository,
            AppBuilderFormPropertyRepository formPropertyRepository, AppTemplateRepository appTemplateRepository) {
        this.flowGraphRepository = flowGraphRepository;
        this.configRepository = configRepository;
        this.configPropertyRepository = configPropertyRepository;
        this.formRepository = formRepository;
        this.formPropertyRepository = formPropertyRepository;
        this.appTemplateRepository = appTemplateRepository;
    }

    /**
     * 根据应用模板 id 创建一个模板类。
     *
     * @param templateId 表示应用模板 id 的 {@link String}。
     * @return 表示应用模板数据的 {@link AppTemplate}。
     */
    public AppTemplate create(String templateId) {
        AppTemplate template = this.appTemplateRepository.selectWithId(templateId);
        notNull(template, () -> new AippException(AippErrCode.TEMPLATE_NOT_FOUND));
        this.setRepositories(template);
        return template;
    }

    /**
     * 保存应用模板。
     *
     * @param template 表示应用模板的 {@link AppTemplate}。
     */
    public void save(AppTemplate template) {
        this.appTemplateRepository.insertOne(template);
        this.configRepository.insertOne(template.getConfig());
        this.flowGraphRepository.insertOne(template.getFlowGraph());
        this.configPropertyRepository.insertMore(template.getConfig().getConfigProperties());
        List<AppBuilderFormProperty> formProperties = template.getFormProperties();
        formProperties.forEach(property -> {
            property.setAppId(template.getId());
        });
        this.formPropertyRepository.insertMore(formProperties);
    }

    /**
     * 初始化应用模板的数据仓库。
     *
     * @param template 表示应用模板的 {@link AppTemplate}。
     */
    public void setRepositories(AppTemplate template) {
        template.setFormRepository(this.formRepository);
        template.setFormPropertyRepository(this.formPropertyRepository);
        template.setConfigRepository(this.configRepository);
        template.setConfigPropertyRepository(this.configPropertyRepository);
        template.setFlowGraphRepository(this.flowGraphRepository);
    }

    /**
     * 删除应用模板
     *
     * @param templateId 表示待删除的应用模板的 {@link String}。
     */
    public void delete(String templateId) {
        AppTemplate template = this.appTemplateRepository.selectWithId(templateId);
        this.configRepository.delete(Collections.singletonList(template.getConfigId()));
        this.flowGraphRepository.delete(Collections.singletonList(template.getFlowGraphId()));
        this.appTemplateRepository.deleteOne(templateId);
        this.formPropertyRepository.deleteByAppIds(Collections.singletonList(templateId));
    }
}
