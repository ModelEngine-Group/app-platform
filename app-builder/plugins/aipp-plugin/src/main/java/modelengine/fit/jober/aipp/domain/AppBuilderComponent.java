/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import modelengine.fitframework.inspection.Validation;

/**
 * 应用构建器组件类
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class AppBuilderComponent extends BaseDomain {
    private String id;
    private String name;
    private String type;
    private String description;
    private String formId;
    private String serviceId;
    private String tenantId;
    private AppBuilderForm form;
    private AppBuilderFormRepository formRepository;
    private AppBuilderFormPropertyRepository formPropertyRepository;

    public AppBuilderComponent(AppBuilderFormRepository formRepository,
                               AppBuilderFormPropertyRepository formPropertyRepository) {
        this.formRepository = formRepository;
        this.formPropertyRepository = formPropertyRepository;
    }

    public AppBuilderForm getForm() {
        return lazyGet(this.form, this::loadForm, this::setForm);
    }

    private AppBuilderForm loadForm() {
        AppBuilderForm appBuilderForm = this.formRepository.selectWithId(this.formId);
        Validation.notNull(appBuilderForm, "App builder form can not be null.");
        appBuilderForm.setFormPropertyRepository(this.formPropertyRepository);
        return appBuilderForm;
    }
}
