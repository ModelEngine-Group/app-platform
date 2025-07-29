/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import static modelengine.fit.jober.aipp.domain.BaseDomain.lazyGet;

import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用构建器配置属性类
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderConfigProperty {
    private String id;
    private String nodeId;
    private String formPropertyId;
    private String configId;
    private AppBuilderFormProperty formProperty;
    private AppBuilderConfig config;
    private AppBuilderConfigRepository configRepository;
    private AppBuilderFormRepository formRepository;
    private AppBuilderFormPropertyRepository formPropertyRepository;

    public AppBuilderConfigProperty(AppBuilderConfigRepository configRepository,
            AppBuilderFormRepository formRepository, AppBuilderFormPropertyRepository formPropertyRepository) {
        this.configRepository = configRepository;
        this.formRepository = formRepository;
        this.formPropertyRepository = formPropertyRepository;
    }

    public AppBuilderFormProperty getFormProperty() {
        return lazyGet(this.formProperty, this::loadFormProperty, this::setFormProperty);
    }

    private AppBuilderFormProperty loadFormProperty() {
        return this.formPropertyRepository.selectWithId(this.formPropertyId);
    }
}
