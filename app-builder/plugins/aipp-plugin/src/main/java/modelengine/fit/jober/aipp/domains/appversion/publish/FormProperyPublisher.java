/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
package modelengine.fit.jober.aipp.domains.appversion.publish;

import lombok.AllArgsConstructor;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.PublishContext;
import modelengine.fit.jober.aipp.dto.AppBuilderSaveConfigDto;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.util.JsonUtils;

import java.util.List;

/**
 * 配置发布器.
 *
 * @author 张越
 * @since 2025-01-16
 */
@AllArgsConstructor
public class FormProperyPublisher implements Publisher {
    private final AppBuilderFormPropertyRepository formPropertyRepository;

    @Override
    public void publish(PublishContext context, AppVersion appVersion) {
        AppBuilderSaveConfigDto saveConfigDto = AppBuilderSaveConfigDto.builder()
                .graph(JsonUtils.toJsonString(context.getAppearance()))
                .input(context.getPublishData().getConfigFormProperties())
                .build();
        List<AppBuilderFormProperty> formProperties = saveConfigDto.getInput().stream()
                .map(formPropertyDto -> AppBuilderFormProperty.builder()
                        .id(formPropertyDto.getId())
                        .formId("null")
                        .name(formPropertyDto.getName())
                        .dataType(formPropertyDto.getDataType())
                        .defaultValue(formPropertyDto.getDefaultValue())
                        .from(formPropertyDto.getFrom())
                        .group(formPropertyDto.getGroup())
                        .description(formPropertyDto.getDescription())
                        .build())
                .toList();
        this.formPropertyRepository.updateMany(formProperties);
    }
}
