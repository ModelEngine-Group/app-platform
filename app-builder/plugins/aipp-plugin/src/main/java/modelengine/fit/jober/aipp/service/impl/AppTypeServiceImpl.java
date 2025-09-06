/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.dto.AppTypeDto;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppTypeMapper;
import modelengine.fit.jober.aipp.po.AppBuilderAppTypePo;
import modelengine.fit.jober.aipp.service.AppTypeService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用业务分类服务的实现。
 *
 * @author songyongtan
 * @since 2025-01-05
 */
@Component
public class AppTypeServiceImpl implements AppTypeService {
    private final AppBuilderAppTypeMapper appBuilderAppTypeMapper;

    public AppTypeServiceImpl(AppBuilderAppTypeMapper appBuilderAppTypeMapper) {
        this.appBuilderAppTypeMapper = appBuilderAppTypeMapper;
    }

    @Override
    public List<AppTypeDto> queryAll(String tenantId) {
        return this.appBuilderAppTypeMapper.queryAll(tenantId)
            .stream()
            .map(this::deserialize)
            .collect(Collectors.toList());
    }

    @Override
    public AppTypeDto query(String id, String tenantId) {
        AppBuilderAppTypePo po = this.appBuilderAppTypeMapper.query(id, tenantId);
        Validation.notNull(po, () -> new AippParamException(AippErrCode.NOT_FOUND, "appType"));
        return this.deserialize(po);
    }

    @Override
    public AppTypeDto add(AppTypeDto dto, String tenantId) {
        if (StringUtils.isEmpty(dto.getId())) {
            dto.setId(Entities.generateId());
        }
        this.appBuilderAppTypeMapper.insert(this.serialize(dto, tenantId));
        return dto;
    }

    @Override
    public void delete(String id, String tenantId) {
        this.appBuilderAppTypeMapper.delete(id, tenantId);
    }

    @Override
    public void update(AppTypeDto dto, String tenantId) {
        this.appBuilderAppTypeMapper.update(this.serialize(dto, tenantId));
    }

    private AppBuilderAppTypePo serialize(AppTypeDto dto, String tenantId) {
        LocalDateTime now = LocalDateTime.now();
        return AppBuilderAppTypePo.builder()
            .id(dto.getId())
            .name(dto.getName())
            .tenantId(tenantId)
            .createAt(now)
            .updateAt(now)
            .build();
    }

    private AppTypeDto deserialize(AppBuilderAppTypePo po) {
        return AppTypeDto.builder().id(po.getId()).name(po.getName()).build();
    }
}
