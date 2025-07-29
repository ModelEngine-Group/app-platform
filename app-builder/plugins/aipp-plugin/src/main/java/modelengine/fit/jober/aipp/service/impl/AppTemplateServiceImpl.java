/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.condition.TemplateQueryCondition;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.template.TemplateAppCreateDto;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fit.jober.aipp.repository.AppTemplateRepository;
import modelengine.fit.jober.aipp.service.AppBuilderAppService;
import modelengine.fit.jober.aipp.service.AppTemplateService;
import modelengine.fit.jober.aipp.util.TemplateUtils;
import modelengine.fit.jober.common.RangedResultSet;

import lombok.AllArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link AppTemplateService} 接口实现。
 *
 * @author 方誉州
 * @since 2025-01-02
 */
@Component
@AllArgsConstructor
public class AppTemplateServiceImpl implements AppTemplateService {
    private final AppBuilderAppService appService;
    private final AppTemplateRepository templateRepository;

    @Override
    public RangedResultSet<TemplateInfoDto> query(TemplateQueryCondition cond, OperationContext context) {
        List<TemplateInfoDto> rawResult = this.templateRepository.selectWithCondition(cond)
                .stream()
                .map(TemplateUtils::convertToTemplateDto)
                .collect(Collectors.toList());
        int total = this.templateRepository.countWithCondition(cond);
        return RangedResultSet.create(rawResult, cond.getOffset(), cond.getLimit(), total);
    }

    @Override
    public TemplateInfoDto publish(TemplateAppCreateDto createDto, OperationContext context) {
        return this.appService.publishTemplateFromApp(createDto, context);
    }

    @Override
    @Transactional
    public AppBuilderAppDto createAppByTemplate(TemplateAppCreateDto createDto, OperationContext context) {
        this.templateRepository.increaseUsage(createDto.getId());
        return this.appService.createAppByTemplate(createDto, context);
    }

    @Override
    public void delete(String templateId, OperationContext context) {
        this.appService.deleteTemplate(templateId, context);
    }
}
